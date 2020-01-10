package org.kasource.json.schema;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kasource.commons.collection.builder.SetBuilder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.kasource.json.schema.registry.JsonSchemaRegistry;
import org.kasource.json.schema.registry.JsonSchemaRegistryFactory;


@RunWith(MockitoJUnitRunner.class)
public class JsonSchemaDiscovererTest {
    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ClassPathScanningCandidateComponentProvider scanner;

    @Mock
    private JsonSchemaRegistryFactory repositoryFactory;

    @Mock
    private JsonSchemaRegistry repository;

    @Mock
    private BeanDefinition beanDefinition;

    private String basePackage = "basePackage";
    private String additionalBasePackage = "additionalPackage";

    @InjectMocks
    private JsonSchemaDiscoverer discoverer = new JsonSchemaDiscoverer(objectMapper);

    @Test
    public void discoverSchemas() {

        Class<?> serDeClass = String.class;

        when(repositoryFactory.create()).thenReturn(repository);
        when(scanner.findCandidateComponents(basePackage)).thenReturn(new SetBuilder<BeanDefinition>().add(beanDefinition).build());
        when(beanDefinition.getBeanClassName()).thenReturn(serDeClass.getName());

        discoverer.discoverSchemas(basePackage, additionalBasePackage);

        verify(scanner, times(1)).findCandidateComponents(basePackage);
        verify(scanner, times(1)).findCandidateComponents(additionalBasePackage);
        verify(repository).registerSchemaFor(serDeClass);
    }

    @Test
    public void discoverInvalidClasses() {
        String badClass = "a.bad.Class";

        when(repositoryFactory.create()).thenReturn(repository);
        when(scanner.findCandidateComponents(basePackage)).thenReturn(new SetBuilder<BeanDefinition>().add(beanDefinition).build());


        when(beanDefinition.getBeanClassName()).thenReturn(badClass);

        discoverer.discoverSchemas(basePackage);

        verify(scanner, times(1)).findCandidateComponents(basePackage);
        verifyZeroInteractions(repository);

    }
}
