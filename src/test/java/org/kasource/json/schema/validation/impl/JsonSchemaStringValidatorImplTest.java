package org.kasource.json.schema.validation.impl;

import java.io.IOException;
import java.util.Arrays;

import javax.validation.ConstraintValidatorContext;

import org.springframework.core.io.DefaultResourceLoader;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kasource.commons.reflection.annotation.AnnotationBuilder;
import org.kasource.json.schema.validation.impl.JsonSchemaStringValidatorImpl;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.kasource.json.schema.JsonSchema;
import org.kasource.json.schema.validation.InvalidJsonException;
import org.kasource.json.schema.validation.ValidJson;


@RunWith(MockitoJUnitRunner.class)
public class JsonSchemaStringValidatorImplTest {
    private static final String SCHEMA_NAME = "person";
    private static final String SCHEMA_VERSION = "1.0";

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;

    @Captor
    private ArgumentCaptor<String> errorMessageCaptor;

    private JsonSchemaStringValidatorImpl personValidator = new JsonSchemaStringValidatorImpl();

    @Before
    public void setupPersonValidator() {
        personValidator.initialize(new AnnotationBuilder<ValidJson>(ValidJson.class).attr("jsonSchemaClass", Person.class).build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void initializeNonJsonSchemaClass() {
        JsonSchemaStringValidatorImpl validator = new JsonSchemaStringValidatorImpl();
        validator.initialize(new AnnotationBuilder<ValidJson>(ValidJson.class).attr("jsonSchemaClass", String.class).build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void initializeInvalidLocationJsonSchemaClass() {
        JsonSchemaStringValidatorImpl validator = new JsonSchemaStringValidatorImpl();
        validator.initialize(new AnnotationBuilder<ValidJson>(ValidJson.class).attr("jsonSchemaClass", InvalidSchemaLocationClass.class).build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void initializeMissingJsonSchemaClass() {
        JsonSchemaStringValidatorImpl validator = new JsonSchemaStringValidatorImpl();
        validator.initialize(new AnnotationBuilder<ValidJson>(ValidJson.class).attr("jsonSchemaClass", InvalidSchemaLocationClass.class).build());
    }

    @Test
    public void initializeSchemaClassFromClasspath() {
        JsonSchemaStringValidatorImpl validator = new JsonSchemaStringValidatorImpl();
        validator.initialize(new AnnotationBuilder<ValidJson>(ValidJson.class).attr("jsonSchemaClass", Person.class).build());
    }

    @Test
    public void initializeSchemaClassFromURL() {
        JsonSchemaStringValidatorImpl validator = new JsonSchemaStringValidatorImpl();
        validator.initialize(new AnnotationBuilder<ValidJson>(ValidJson.class).attr("jsonSchemaClass", Person2.class).build());
    }

    @Test
    public void validateString() {
        String json = loadJson("classpath:data/person.json");
        personValidator.validate(json);
    }

    @Test(expected = InvalidJsonException.class)
    public void validateStringMissingField() {
        String json = loadJson("classpath:data/person-missing_name.json");
        personValidator.validate(json);
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateNonJsonString() {
        String json = loadJson("classpath:data/non.json");
        personValidator.validate(json);
    }

    @Test
    public void isValidString() {
        String json = loadJson("classpath:data/person.json");
        boolean valid = personValidator.isValid(json, context);

        assertThat(valid, is(true));

    }

    @Test
    public void isValidStringMissingField() {
        String json = loadJson("classpath:data/person-missing_name.json");

        when(context.buildConstraintViolationWithTemplate(errorMessageCaptor.capture())).thenReturn(violationBuilder);

        boolean valid = personValidator.isValid(json, context);

        verify(context).disableDefaultConstraintViolation();
        verify(violationBuilder, times(2)).addConstraintViolation();

        assertThat(valid, is(false));
        assertThat(errorMessageCaptor.getAllValues(), containsInAnyOrder(
                stringContainsInOrder(Arrays.asList("schema", SCHEMA_NAME, "version", SCHEMA_VERSION)),
                stringContainsInOrder(Arrays.asList("missing required properties", "lastName"))
        ));
    }

    @Test
    public void isValidNonJsonString() {
        String json = loadJson("classpath:data/non.json");

        when(context.buildConstraintViolationWithTemplate(json + "\n is not valid JSON")).thenReturn(violationBuilder);

        boolean valid = personValidator.isValid(json, context);

        verify(context).disableDefaultConstraintViolation();
        verify(violationBuilder).addConstraintViolation();

        assertThat(valid, is(false));
    }


    private String loadJson(String location) {
        try {
            return IOUtils.toString(new DefaultResourceLoader().getResource(location).getInputStream(), "UTF-8");
        } catch (IOException e) {
            throw new IllegalArgumentException("Can't read JSON from " + location, e);
        }
    }

    @JsonSchema(name = "example", version = "1.0", location = "example-v1.json")
    private static class InvalidSchemaLocationClass {
    }

    @JsonSchema(name = "example", version = "1.0", location = "/example-v1.json")
    private static class MissingSchemaClass {
    }

    @JsonSchema(name = SCHEMA_NAME, version = SCHEMA_VERSION, location = "/person-schema-v1.json")
    private static class Person {
    }

    @JsonSchema(name = SCHEMA_NAME, version = SCHEMA_VERSION, location = "file:src/test/resources/person-schema-v1.json")
    private static class Person2 {
    }
}
