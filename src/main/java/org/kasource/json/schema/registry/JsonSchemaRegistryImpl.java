package org.kasource.json.schema.registry;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.core.annotation.AnnotationUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import org.kasource.json.schema.validation.impl.JsonSchemaNodeValidatorImpl;
import org.kasource.json.schema.validation.impl.JsonSchemaStringValidatorImpl;



public class JsonSchemaRegistryImpl implements JsonSchemaRegistry {
    private Map<String, Map<String, JsonSchemaRegistration>> schemas = new ConcurrentHashMap<>();
    private Map<Class<?>, JsonSchemaRegistration> schemasPerClass = new ConcurrentHashMap<>();

    private ObjectMapper objectMapper;


    public JsonSchemaRegistryImpl(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<JsonSchemaRegistration> getSchemaRegistration(String name, String version) {
        Map<String, JsonSchemaRegistration> schemasPerName = schemas.get(name);
        if (schemasPerName != null) {
            return Optional.ofNullable(schemasPerName.get(version));
        }
        return Optional.empty();
    }

    @Override
    public Optional<JsonSchemaRegistration> getSchemaRegistration(Class<?> serDeClass) {
        return Optional.ofNullable(schemasPerClass.get(serDeClass));
    }

    @Override
    public boolean isSupported(String name, String version) {
        Map<String, JsonSchemaRegistration> schemasPerName = schemas.get(name);
        if (schemasPerName != null) {
            return schemasPerName.get(version) != null;
        }
        return false;
    }

    @Override
    public Set<String> getSupportedVersions(String name) {
        Map<String, JsonSchemaRegistration> schemasPerName = schemas.get(name);
        if (schemasPerName != null) {
            return schemasPerName.keySet();
        }
        return Collections.EMPTY_SET;
    }

    @Override
    public void registerSchemaFor(Class<?> serDeClass) {
        org.kasource.json.schema.JsonSchema annotation =
                AnnotationUtils.findAnnotation(serDeClass,
                        org.kasource.json.schema.JsonSchema.class);
        if (annotation != null) {
            Map<String, JsonSchemaRegistration> schemasPerName = schemas.get(annotation.name());
            if (schemasPerName == null) {
                schemasPerName = new HashMap<>();
                schemas.put(annotation.name(), schemasPerName);
            }
            checkDuplicatedSchema(serDeClass, annotation, schemasPerName);
            JsonSchemaRegistration registration = createRegistration(serDeClass, annotation);
            schemasPerName.put(annotation.version(), registration);
            schemasPerClass.put(serDeClass, registration);
        }
    }

    @Override
    public Map<String, Map<String, JsonSchemaRegistration>> getRegisteredSchemas() {
        return schemas.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                                          e -> e.getValue().entrySet()
                                                  .stream()
                                                  .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))));
    }

    private JsonSchemaRegistration createRegistration(
            Class<?> serDeClass,
            org.kasource.json.schema.JsonSchema annotation) {
        JsonSchema schema = loadSchema(annotation);
        JsonSchemaStringValidatorImpl stringValidator = new JsonSchemaStringValidatorImpl(objectMapper);
        stringValidator.initialize(serDeClass);
        JsonSchemaNodeValidatorImpl nodeValidator = new JsonSchemaNodeValidatorImpl();
        nodeValidator.initialize(serDeClass);
        return new JsonSchemaRegistrationImpl(serDeClass, schema, stringValidator, nodeValidator);
    }

    private JsonSchema loadSchema(org.kasource.json.schema.JsonSchema annotation) {
        try {
            return JsonSchemaFactory.byDefault().getJsonSchema(JsonLoader.fromResource(annotation.location()));
        } catch (IOException | ProcessingException e) {
            throw new IllegalStateException("Could not load JSON schema "
                    + annotation.name() + " version " + annotation.version(), e);
        }
    }

    private void checkDuplicatedSchema(Class<?> serDeClass,
                                       org.kasource.json.schema.JsonSchema annotation,
                                       Map<String, JsonSchemaRegistration> schemasPerName) {
        if (schemasPerName.containsKey(annotation.version())) {
            JsonSchemaRegistration otherSchema = schemasPerName.get(annotation.version());
            throw new DuplicateJsonSchemaException("Duplicate classes are annotated as the same version for schema "
                    + annotation.name() + " and version " + annotation.version() + " both "
                    + otherSchema.getSerDeClass() + " and " + serDeClass);

        }
    }


}
