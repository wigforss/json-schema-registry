package org.kasource.json.schema.registry;


import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.main.JsonSchema;
import org.kasource.json.schema.validation.impl.JsonSchemaValidator;


public class JsonSchemaRegistrationImpl implements JsonSchemaRegistration {

    private final Class<?> serDeClass;

    private final JsonSchema jsonSchema;

    private final JsonSchemaValidator<String> stringValidator;

    private final JsonSchemaValidator<JsonNode> nodeValidator;

    public JsonSchemaRegistrationImpl(final Class<?> serDeClass,
                                      final JsonSchema jsonSchema,
                                      final JsonSchemaValidator<String> stringValidator,
                                      final JsonSchemaValidator<JsonNode> nodeValidator) {
        this.serDeClass = serDeClass;
        this.jsonSchema = jsonSchema;
        this.stringValidator = stringValidator;
        this.nodeValidator = nodeValidator;
    }

    @Override
    public Class<?> getSerDeClass() {
        return serDeClass;
    }

    @Override
    public JsonSchema getJsonSchema() {
        return jsonSchema;
    }

    @Override
    public JsonSchemaValidator<String> getStringValidator() {
        return stringValidator;
    }

    @Override
    public JsonSchemaValidator<JsonNode> getNodeValidator() {
        return nodeValidator;
    }
}
