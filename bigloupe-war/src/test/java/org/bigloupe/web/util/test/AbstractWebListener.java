/**
 * Created on May 4, 2010
 */
package org.bigloupe.web.util.test;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.annotation.InjectionMetadata.InjectedElement;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextLoader;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.WebApplicationContext;

/**
 * An abstract web test execution listener, with following features:
 * <ul>
 * <li>It must be used with a loader that loads Web application context, eg. {@link GenericWebXmlContextLoader}</li>
 * <li>The mock servlet context (from concrete class) is injected into the spring context</li>
 * <li>Test fields annotated with {@link WebResource} must be of a supported web type and are injected into the test</li>
 * <li>Stores the application context as a servletContext attribute, under well-known key
 * "org.springframework.web.context.WebApplicationContext.ROOT"</li>
 * </ul>
 * It must be extended to provide a specific web mock technology implementation.
 * 
 * @author Gaëtan Pitteloud
 */
public abstract class AbstractWebListener extends AbstractTestExecutionListener {

    protected ServletContext servletContext;

    // all created mocks must be put in this map
    protected final Map<Class<?>, Object> webObjects = new ConcurrentHashMap<Class<?>, Object>();
    private InjectionMetadata injectionMetadata;
    // the test class fields that must be cleaned after test execution (also for subclasses)
    final Set<Field> fieldsToClean = new HashSet<Field>();

    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
        servletContext = createServletContext(testContext);

        if (TestContextUtils.hasApplicationContext(testContext)) {
            final ContextLoader contextLoader = TestContextUtils.getContextLoader(testContext);
            if (contextLoader instanceof ServletContextAware) {
                ((ServletContextAware) contextLoader).setServletContext(servletContext);
            } else {
                throw new IllegalStateException("The contextLoader configured in @ContextConfiguration.loader"
                        + " must implement ServletContextAware in order to receive the ServletContext.");
            }
            // force application context to be reloaded as a Web application context
            testContext.markApplicationContextDirty();
        }

        injectionMetadata = introspectResourceMetadata(testContext);
    }

    /**
     * Create the mock servlet context. The method is invoked once for a test class (not for every test method).
     * 
     * @param testContext the test context
     * @return a servlet context mock implementation
     * @throws Exception configuration exception
     */
    protected abstract ServletContext createServletContext(TestContext testContext) throws Exception;

    @Override
    public final void beforeTestMethod(TestContext testContext) throws Exception {
        putWebObject(ServletContext.class, servletContext);
        // store the servletContext under its mock implementation class
        // putWebObject(servletContext.getClass(), servletContext); <-- does not compile
        webObjects.put(servletContext.getClass(), servletContext);

        createAndStoreMocks(testContext);

        if (TestContextUtils.hasApplicationContext(testContext)) {
            final ApplicationContext ac = testContext.getApplicationContext();
            servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, ac);

            postProcessApplicationContext(ac);
        }

        injectFields(testContext);
    }

    /**
     * Hook method to further process the application context (invoked after mocks have been created).
     * <p>
     * Does nothing by default.
     * 
     * @param ac
     */
    protected void postProcessApplicationContext(ApplicationContext ac) {
        // nothing by default
    }

    /**
     * Create the mocks and store them in internal map using the method {@link #putWebObject(Class, Object)}. The same
     * mock might be stored many times, under all available interface/class that it implements. This internal map is
     * used to retrieved (by field type) and inject a WebResource into the test class instance.
     * <p>
     * The servlet context is already created and stored.
     * 
     * @param testContext the test context
     */
    protected abstract void createAndStoreMocks(TestContext testContext);

    /**
     * Inject instance fields
     * 
     * @param testContext the test context
     */
    private void injectFields(final TestContext testContext) {
        try {
            injectionMetadata.inject(testContext.getTestInstance(), testContext.getTestMethod().getName(), null);
        } catch (final Throwable ex) {
            throw new RuntimeException("Failed to inject instance fields", ex);
        }
    }

    /**
     * Introspect the fields of the given class to find candidate fields for injection.
     * 
     * @param testContext the test context
     * @return class metadata info
     */
    private InjectionMetadata introspectResourceMetadata(final TestContext testContext) {
        final Class<?> testClass = testContext.getTestClass();

        final Collection<InjectedElement> elements = new LinkedList<InjectedElement>();
        ReflectionUtils.doWithFields(testClass, new ReflectionUtils.FieldCallback() {

            public void doWith(final Field field) throws IllegalArgumentException, IllegalAccessException {
                ReflectionUtils.makeAccessible(field);


                InjectedElement element = createElementForTestClassField(field, testContext);
                if (element != null) {
                    elements.add(element);
                    fieldsToClean.add(field);
                }
            }
        });
        return new InjectionMetadata(testClass, elements);
    }

    /**
     * Possibly create an additional element for handling a test class field. WebResource annotation is already handled.
     * Autowired, Resource and such other annotations are handled by another listener and should not be managed by this
     * method.
     * 
     * @param field a test class field
     * @param testContext the test context
     * @return an element or null if the field is not processed by this listener (default to null).
     */
    protected InjectedElement createElementForTestClassField(Field field, TestContext testContext) {
        return null;
    }

    /**
     * Stores a mock in the internal mocks that will be used to inject web resources. The same mock instance can be put
     * many times under different type (typically the servlet API interface and the mock class implementing this
     * interface). This method ensures type safety in the map.
     * 
     * @param <T> the object type
     * @param klass the type
     * @param object the object
     */
    protected final <T> void putWebObject(final Class<? super T> klass, final T object) {
        webObjects.put(klass, object);
    }

    /**
     * Get the web object of the specified type.
     * 
     * @param <T> type
     * @param klass type
     * @return web object
     */
    @SuppressWarnings("unchecked")
    <T> T getWebObject(final Class<T> klass) {
        return (T) webObjects.get(klass);
    }

    @Override
    public final void afterTestMethod(TestContext testContext) throws Exception {
        resetMocks(testContext);

        // clear injected fields
        for (final Field field : fieldsToClean) {
            if (field != null) {
                field.set(testContext.getTestInstance(), null);
            }
        }
    }

    @Override
    public void afterTestClass(TestContext testContext) throws Exception {
        fieldsToClean.clear();
        webObjects.clear();
        injectionMetadata = null;
        servletContext = null;
        testContext.markApplicationContextDirty();
    }

    /**
     * Reset the mock fields created in {@link #createAndStoreMocks(TestContext)}.
     * 
     * @param testContext
     */
    protected abstract void resetMocks(TestContext testContext);

    /**
     * Inject the web object of the type declared by a field annotated with &#064;WebResource.
     * 
     * @author Gaëtan Pitteloud
     */
    protected class WebElement extends InjectionMetadata.InjectedElement {

        /**
         * @param field
         */
        protected WebElement(final Field field) {
            super(field, null);
        }

        @Override
        protected Object getResourceToInject(final Object target, final String requestingBeanName) {
            final Field field = (Field) getMember();
            final Class<?> type = field.getType();
            if (!webObjects.containsKey(type)) {
                throw new IllegalArgumentException("Invalid field type '" + type.getName() + "' for field '"
                        + field.getName() + "' annotated with @WebResource");
            }
            return getWebObject(type);
        }

    }

}
