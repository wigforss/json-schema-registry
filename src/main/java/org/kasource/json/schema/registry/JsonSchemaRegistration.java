package org.kasource.json.schema.registry;


import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.main.JsonSchema;
import org.kasource.json.schema.validation.impl.JsonSchemaValidator;


public interface JsonSchemaRegistration {

    Class getSerDeClass();

    JsonSchema getJsonSchema();

    JsonSchemaValidator<String> getStringValidator();

    JsonSchemaValidator<JsonNode> getNodeValidator();
}
