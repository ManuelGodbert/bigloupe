/**
 * Created on Mar 16, 2009
 */
package org.bigloupe.web.util.test;

import java.lang.reflect.Field;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextLoader;
import org.springframework.test.context.TestContext;
import org.springframework.util.ReflectionUtils;

/**
 * Misc utility methods to be used to work with the Spring {@link TestContext} framework.
 * 
 * @author Gaëtan Pitteloud
 */
public class TestContextUtils {

    /**
     * Answer whether the TestContext is configured with an application context (both loader and locations), by looking
     * at the ContextConfiguration annotation in the test class hierarchy.
     * 
     * @param ctx the test context
     * @return true if an application context is configured in the test, false otherwise
     */
    public static boolean hasApplicationContext(TestContext ctx) {
        return AnnotationUtils.findAnnotationDeclaringClass(ContextConfiguration.class, ctx.getTestClass()) != null;
    }

    /**
     * Get the context loader of this test context (by reflection, as the contextLoader field is private without getter.
     * 
     * @param ctx the test context
     * @return the context loader
     */
    public static ContextLoader getContextLoader(TestContext ctx) {
        return getTestContextFieldValue(ctx, "contextLoader", ContextLoader.class);
    }

    /**
     * Read the value of a private field of TestContext.
     * 
     * @param <T> field value type
     * @param ctx test context
     * @param fieldName field name
     * @param expectedType expected field value type
     * @return field value
     */
    private static <T> T getTestContextFieldValue(TestContext ctx, String fieldName, Class<T> expectedType) {
        try {
            Field field = ReflectionUtils.findField(ctx.getClass(), fieldName);
            ReflectionUtils.makeAccessible(field);
            Object ref = field.get(ctx);
            if (ref == null) {
                return null;
            }
            if (expectedType.isInstance(ref)) {
                return expectedType.cast(ref);
            }
            throw new ClassCastException("Field '" + fieldName + "' value is not of expected type '" + expectedType
                    + "'");
        } catch (Exception e) {
            ReflectionUtils.handleReflectionException(e);
            return null;
        }
    }

}
