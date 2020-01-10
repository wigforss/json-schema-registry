package org.kasource.json.schema.registry;


import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonSchemaRegistryFactoryImpl implements JsonSchemaRegistryFactory {

    private ObjectMapper objectMapper;

    public JsonSchemaRegistryFactoryImpl(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public JsonSchemaRegistry create() {
        return new JsonSchemaRegistryImpl(objectMapper);
    }
}
