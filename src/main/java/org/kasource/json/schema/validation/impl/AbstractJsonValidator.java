package org.kasource.json.schema.validation.impl;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.validation.ConstraintValidatorContext;

import org.springframework.core.annotation.AnnotationUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import org.kasource.json.schema.validation.ValidJson;


public class AbstractJsonValidator {

    public static final char CLASSPATH_PREFIX  = '/';
    private String name;
    private String version;
    private JsonSchema jsonSchema;

    public void initialize(final Class<?> serDeClass) {
        org.kasource.json.schema.JsonSchema jsonSchemaAnnotation =
                AnnotationUtils.findAnnotation(serDeClass,
                        org.kasource.json.schema.JsonSchema.class);
        if (jsonSchemaAnnotation != null) {
            jsonSchema = loadSchema(jsonSchemaAnnotation);
            name = jsonSchemaAnnotation.name();
            version = jsonSchemaAnnotation.version();
        } else {
            throw new IllegalArgumentException("Class " + serDeClass.getName() + " must be annotated with @"
                    + org.kasource.json.schema.JsonSchema.class.getName());
        }
    }

    public void initialize(final ValidJson annotation) {
        initialize(annotation.jsonSchemaClass());
    }

    private JsonSchema loadSchema(org.kasource.json.schema.JsonSchema annotation) {
        try {
            JsonNode schema = loadSchemaFrom(annotation.location());
            return JsonSchemaFactory.byDefault().getJsonSchema(schema);
        } catch (IOException | ProcessingException | URISyntaxException e) {
            throw new IllegalStateException("Could not load JSON schema for "
                    + annotation.name() + " version " + annotation.version() + " from " + annotation.location(), e);
        }
    }

    private JsonNode loadSchemaFrom(String location) throws IOException, URISyntaxException {
        if (location.charAt(0) == CLASSPATH_PREFIX) {
            return JsonLoader.fromResource(location);
        } else {
            return JsonLoader.fromURL(new URI(location).toURL());
        }
    }

    protected ProcessingReport validationReport(JsonNode jsonNode) {
        try {
            return jsonSchema.validate(jsonNode);
        } catch (ProcessingException e) {
            throw new IllegalStateException("Could not process JSON schema '" + name + "' version '" + version + "'", e);
        }
    }

    private void addConstraints(ProcessingReport processingReport, ConstraintValidatorContext context) {
        context.buildConstraintViolationWithTemplate("JSON does not comply to schema '" + name
                + "' version '" + version + "'")
                .addConstraintViolation();
        processingReport.forEach(m -> context.buildConstraintViolationWithTemplate(m.asJson().toString())
                .addConstraintViolation());

    }

    protected String toErrorMessage(ProcessingReport processingReport) {
        StringBuilder stringBuilder = new StringBuilder();
        processingReport.forEach(m -> stringBuilder.append(m.getMessage() + ": " + m.asJson()).append('\n'));
        return "JSON does not comply to schema '" + name + "' version '" + version + "': " + stringBuilder.toString();
    }

    protected void setErrorConstraint(ProcessingReport processingReport, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        addConstraints(processingReport, context);
    }

}
