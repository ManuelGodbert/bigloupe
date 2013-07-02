/**
 * Created on May 26, 2010
 */
package org.bigloupe.web.util.test;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.test.context.support.AbstractContextLoader;
import org.springframework.test.context.support.AbstractGenericContextLoader;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;

/**
 * Mirror of Spring's {@link AbstractGenericContextLoader} for {@link WebApplicationContext}.
 * 
 * @author Gaëtan Pitteloud
 */
public abstract class AbstractGenericWebContextLoader extends AbstractContextLoader implements ServletContextAware {

    private ServletContext servletContext;

    public ApplicationContext loadContext(String... locations) throws Exception {
        GenericWebApplicationContext wac = new GenericWebApplicationContext();
        wac.setServletContext(servletContext);
        prepareContext(wac);
        customizeBeanFactory(wac.getDefaultListableBeanFactory());
        createBeanDefinitionReader(wac).loadBeanDefinitions(locations);
        AnnotationConfigUtils.registerAnnotationConfigProcessors(wac);
        customizeContext(wac);
        wac.refresh();
        wac.registerShutdownHook();
        return wac;

    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    /**
     * Prepare the {@link GenericApplicationContext} created by this ContextLoader. Called <i>before</> bean definitions
     * are read.
     * <p>
     * The default implementation is empty. Can be overridden in subclasses to customize GenericApplicationContext's
     * standard settings.
     * 
     * @param context the context for which the BeanDefinitionReader should be created
     * @see #loadContext
     * @see org.springframework.context.support.GenericApplicationContext#setResourceLoader
     * @see org.springframework.context.support.GenericApplicationContext#setId
     */
    protected void prepareContext(GenericWebApplicationContext context) {
        // empty by default
    }

    /**
     * Customize the internal bean factory of the ApplicationContext created by this ContextLoader.
     * <p>
     * The default implementation is empty but can be overridden in subclasses to customize DefaultListableBeanFactory's
     * standard settings.
     * 
     * @param beanFactory the bean factory created by this ContextLoader
     * @see #loadContext
     * @see org.springframework.beans.factory.support.DefaultListableBeanFactory#setAllowBeanDefinitionOverriding(boolean)
     * @see org.springframework.beans.factory.support.DefaultListableBeanFactory#setAllowEagerClassLoading(boolean)
     * @see org.springframework.beans.factory.support.DefaultListableBeanFactory#setAllowCircularReferences(boolean)
     * @see org.springframework.beans.factory.support.DefaultListableBeanFactory#setAllowRawInjectionDespiteWrapping(boolean)
     */
    protected void customizeBeanFactory(DefaultListableBeanFactory beanFactory) {
        // empty by default
    }

    /**
     * Factory method for creating new {@link BeanDefinitionReader}s for loading bean definitions into the supplied
     * {@link GenericWebApplicationContext context}.
     * 
     * @param context the context for which the BeanDefinitionReader should be created
     * @return a BeanDefinitionReader for the supplied context
     * @see #loadContext
     * @see BeanDefinitionReader
     */
    protected abstract BeanDefinitionReader createBeanDefinitionReader(GenericWebApplicationContext context);

    /**
     * Customize the {@link GenericWebApplicationContext} created by this ContextLoader <i>after</i> bean definitions
     * have been loaded into the context but before the context is refreshed.
     * <p>
     * The default implementation is empty but can be overridden in subclasses to customize the application context.
     * 
     * @param context the newly created application context
     * @see #loadContext(String...)
     */
    protected void customizeContext(GenericWebApplicationContext context) {
        // empty by default
    }
    
	@Override
	public ApplicationContext loadContext(MergedContextConfiguration mergedConfig) throws Exception {
		GenericWebApplicationContext context = new GenericWebApplicationContext();
		context.getEnvironment().setActiveProfiles(mergedConfig.getActiveProfiles());
		prepareContext(context);
		customizeBeanFactory(context.getDefaultListableBeanFactory());
		createBeanDefinitionReader(context).loadBeanDefinitions(mergedConfig.getLocations());
		AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
		customizeContext(context);
		context.refresh();
		context.registerShutdownHook();
		return context;
	}

}
