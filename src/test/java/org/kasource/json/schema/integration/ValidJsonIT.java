package org.kasource.json.schema.integration;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.springframework.core.io.DefaultResourceLoader;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.junit.Assert.assertThat;

import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import org.kasource.json.schema.JsonSchema;
import org.kasource.json.schema.validation.ValidJson;


public class ValidJsonIT {
    private static final String SCHEMA_NAME = "person";
    private static final String SCHEMA_VERSION = "1.0";
    private static Validator validator;

    @BeforeClass
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void validJson() {
        String personJson = loadJson("classpath:data/person.json");

        Set<ConstraintViolation<PersonRequest>> violations = validator.validate(new PersonRequest(personJson));

        assertThat(violations.isEmpty(), is(true));
    }


    @Test
    public void missingField() {
        String personJson = loadJson("classpath:data/person-missing_name.json");

        Set<ConstraintViolation<PersonRequest>> violations = validator.validate(new PersonRequest(personJson));

        Set<String> violationMessages = violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toSet());

        assertThat(violations.isEmpty(), is(false));

        assertThat(violationMessages, contains(
                stringContainsInOrder(Arrays.asList("does not comply", "schema 'person'", "version '1.0'")),
                stringContainsInOrder(Arrays.asList( "required properties", "lastName"))));
    }

    @Test
    public void NonJsonField() {
        String personJson = loadJson("classpath:data/non.json");

        Set<ConstraintViolation<PersonRequest>> violations = validator.validate(new PersonRequest(personJson));


        assertThat(violations.isEmpty(), is(false));
        assertThat(violations.iterator().next().getMessage(), stringContainsInOrder(Arrays.asList("firstName=firstName", "is not valid JSON")));

        System.out.println(violations.iterator().next().getMessage());
    }

    private String loadJson(String location) {
        try {
            return IOUtils.toString(new DefaultResourceLoader().getResource(location).getInputStream(), "UTF-8");
        } catch (IOException e) {
            throw new IllegalArgumentException("Can't read JSON from " + location, e);
        }
    }

    private static class PersonRequest {
        @ValidJson(jsonSchemaClass = Person.class)
        private String body;

        public PersonRequest(String json) {
            this.body = json;
        }

    }

    @JsonSchema(name = SCHEMA_NAME, version = SCHEMA_VERSION, location = "/person-schema-v1.json")
    private static class Person {
        private String firstName;

        private String lastName;
    }
}
