package org.kasource.json.schema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.kasource.json.schema.registry.JsonSchemaRegistry;
import org.kasource.json.schema.registry.JsonSchemaRegistryFactory;
import org.kasource.json.schema.registry.JsonSchemaRegistryFactoryImpl;


public class JsonSchemaScanner {

    private ClassPathScanningCandidateComponentProvider scanner =
            new ClassPathScanningCandidateComponentProvider(false);
    private JsonSchemaRegistryFactory repositoryFactory;

    public JsonSchemaScanner(final ObjectMapper objectMapper) {
        this(objectMapper, false);
    }

    public JsonSchemaScanner(final ObjectMapper objectMapper,
                             final boolean considerMetaAnnotations) {
        this.repositoryFactory = new JsonSchemaRegistryFactoryImpl(objectMapper);
        scanner.addIncludeFilter(new AnnotationTypeFilter(JsonSchema.class, considerMetaAnnotations));
    }

    public JsonSchemaRegistry scan(String basePackage,
                                   String... additionalBasePackage) {
        JsonSchemaRegistry repo = repositoryFactory.create();
        List<String> packageList = new ArrayList<>(Arrays.asList(additionalBasePackage));
        packageList.add(basePackage);

        Set<Class<?>> schemas = scanForSchemas(packageList);

        schemas.stream()
                .filter(c -> !c.equals(Object.class))
                .forEach(schemaClass -> repo.registerSchemaFor(schemaClass));

        return repo;
    }


    private Set<Class<?>> scanForSchemas(List<String> packageList) {
        return packageList.stream().map(packageName -> scanner.findCandidateComponents(packageName))
                .flatMap(Set::stream)
                .map(beanDefinition -> beanDefinition.getBeanClassName())
                .map(className -> {
                    try {
                        return Class.forName(className);
                    } catch (ClassNotFoundException e) {
                        return Object.class;
                    }
                })
                .collect(Collectors.toSet());
    }
}
