package org.kasource.json.schema.validation.impl;


import java.io.IOException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import org.kasource.json.schema.validation.InvalidJsonException;
import org.kasource.json.schema.validation.ValidJson;


/**
 * JSON Schema Validator that validates a JSON String.
 * <p>
 * New instances needs to initialized by invoking the initialize method.
 */
public class JsonSchemaStringValidatorImpl extends AbstractJsonValidator
        implements JsonSchemaValidator<String>, ConstraintValidator<ValidJson, String> {

    private ObjectMapper objectMapper;


    public JsonSchemaStringValidatorImpl() {
        this.objectMapper = new ObjectMapper();
    }

    public JsonSchemaStringValidatorImpl(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void validate(String json) {
        try {
            ProcessingReport processingReport = isJsonValid(objectMapper.readTree(json));
            if (!processingReport.isSuccess()) {
                throw new InvalidJsonException(processingReport, json + " " + toErrorMessage(processingReport));
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("String is not valid JSON");
        }
    }

    @Override
    public final boolean isValid(final String value,
                                 final ConstraintValidatorContext context) {
        try {
            ProcessingReport processingReport = isJsonValid(objectMapper.readTree(value));
            boolean success = processingReport.isSuccess();
            if (!success) {
                setErrorConstraint(processingReport, context);
            }
            return success;
        } catch (IOException e) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(value + "\n is not valid JSON").addConstraintViolation();
            return false;
        }
    }
}
