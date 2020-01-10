package org.kasource.json.schema.validation.impl;


public interface JsonSchemaValidator<T> {
    void validate(T json);
}
