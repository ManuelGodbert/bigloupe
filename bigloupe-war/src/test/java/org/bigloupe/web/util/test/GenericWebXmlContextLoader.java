/**
 * Created on Mar 17, 2009
 */
package org.bigloupe.web.util.test;

import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.support.GenericWebApplicationContext;

/**
 * This loader creates a {@link GenericWebApplicationContext} which reads bean definitions from XML resources.
 * <p>
 * A WebApplicationContext requires a servlet context from which further resources are loaded. The servlet context
 * should be created by the listener and passed to the loader using the {@link ServletContextAware} interface.
 * 
 * @author Gaëtan Pitteloud
 */
public class GenericWebXmlContextLoader extends AbstractGenericWebContextLoader {

    /**
     * Same as spring's GenericXmlContextLoader
     * 
     * @see org.springframework.test.context.support.AbstractContextLoader#getResourceSuffix()
     */
    @Override
    protected String getResourceSuffix() {
        return "-context.xml";
    }

    @Override
    protected BeanDefinitionReader createBeanDefinitionReader(GenericWebApplicationContext context) {
        return new XmlBeanDefinitionReader(context);
    }
    
    

}
