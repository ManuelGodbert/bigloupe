package org.bigloupe.web.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

import org.hibernate.MappingException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor;
import org.springframework.util.ClassUtils;

/**
 * This class allow to scan the hibernate entities at startup, without having to declare them in the persistence.xml file.
 */
public class SpringEntityScanner implements ApplicationContextAware, PersistenceUnitPostProcessor {
    
    /** Pattern used to scan our entities. */
    private static final String RESOURCE_PATTERN = "**/*.class";
    
    /** Current application context. */
    private ApplicationContext applicationContext = null;
    
    /** List of the packages to scan. */
    private String[] packagesToScan = null;
    
    /** Resources pattern resolver. */
    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    
    /** List of the entityType filters. */
    private TypeFilter[] entityTypeFilters = null;
    
    /**
     * Default constructor.
     */
    public SpringEntityScanner() {
        final List<TypeFilter> listFilter = new ArrayList<TypeFilter>();
        listFilter.add(new AnnotationTypeFilter(Entity.class, false));
        listFilter.add(new AnnotationTypeFilter(Embeddable.class, false));
        listFilter.add(new AnnotationTypeFilter(MappedSuperclass.class, false));
        entityTypeFilters = listFilter.toArray(new TypeFilter[0]);
    }
    
    
    /**
     * Post processing of the entities.
     * 
     * @param pPui
     *            persistence info unit
     */
    public void postProcessPersistenceUnitInfo(MutablePersistenceUnitInfo pPui) {
        String[] entities = scanPackages();
        for (String entity : entities) {
            pPui.addManagedClassName(entity);
        }
    }
    
    /**
     * Set whether to use Spring-based scanning for entity classes in the classpath instead of listing annotated classes explicitly.
     * 
     * <p>
     * Default is none. Specify packages to search for autodetection of your entity classes in the classpath. This is analogous to Spring's
     * component-scan feature (org.springframework.context.annotation.ClassPathBeanDefinitionScanner}).
     * </p>
     * 
     * @param pPackagesToScan
     *            packages to scan
     */
    public void setPackagesToScan(String[] pPackagesToScan) {
        this.packagesToScan = pPackagesToScan;
    }
    
    /**
     * Perform Spring-based scanning for entity classes.
     * 
     * @see #setPackagesToScan
     * 
     * @return list of founded entities
     */
    protected String[] scanPackages() {
        
        final Set<String> entities = new HashSet<String>();
        if (this.packagesToScan != null) {
            try {
                for (String pkg : this.packagesToScan) {
                    
                    final String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + ClassUtils.convertClassNameToResourcePath(pkg)
                            + RESOURCE_PATTERN;
                    
                    final Resource[] resources = this.resourcePatternResolver.getResources(pattern);
                    final MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(this.resourcePatternResolver);
                    
                    for (Resource resource : resources) {
                        if (resource.isReadable()) {
                            final MetadataReader reader = readerFactory.getMetadataReader(resource);
                            final String className = reader.getClassMetadata().getClassName();
                            if (matchesFilter(reader, readerFactory)) {
                                entities.add(className);
                            }
                        }
                    }
                }
            } catch (IOException ex) {
                throw new MappingException("Failed to scan classpath for unlisted classes", ex);
            }
        }
        return entities.toArray(new String[entities.size()]);
    }
    
    /**
     * Check whether any of the configured entity type filters matches the current class descriptor contained in the metadata reader.
     * 
     * @param pReader
     *            metadata reader
     * @param pReaderFactory
     *            metata reader factory
     * @throws IOException
     *             I/O error
     * @return true if matches filter
     */
    private boolean matchesFilter(MetadataReader pReader, MetadataReaderFactory pReaderFactory) throws IOException {
        
        if (this.entityTypeFilters != null) {
            for (TypeFilter filter : this.entityTypeFilters) {
                if (filter.match(pReader, pReaderFactory)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Get the application context.
     * 
     * @return ApplicationContext
     */
    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }
    
    /**
     * Set the application context.
     * 
     * @param pApplicationContext
     *            application context
     */
    public void setApplicationContext(ApplicationContext pApplicationContext) {
        this.applicationContext = pApplicationContext;
    }
}
