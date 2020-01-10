package org.kasource.json.schema.validation.impl;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import org.kasource.json.schema.validation.InvalidJsonException;
import org.kasource.json.schema.validation.ValidJson;




/**
 * JSON Schema Validator that validates a JSON Node.
 * <p>
 * New instances needs to initialized by invoking the initialize method.
 */
public class JsonSchemaNodeValidatorImpl extends AbstractJsonValidator
        implements JsonSchemaValidator<JsonNode>, ConstraintValidator<ValidJson, JsonNode> {


    @Override
    public void validate(JsonNode node) {
        ProcessingReport processingReport = this.validationReport(node);
        if (!processingReport.isSuccess()) {
            throw new InvalidJsonException(processingReport, node.asText() + " " + toErrorMessage(processingReport));
        }
    }

    @Override
    public final boolean isValid(final JsonNode value,
                                 final ConstraintValidatorContext context) {
        ProcessingReport processingReport = this.validationReport(value);
        boolean success = processingReport.isSuccess();
        if (!success) {
            setErrorConstraint(processingReport, context);
        }
        return success;
    }
}
