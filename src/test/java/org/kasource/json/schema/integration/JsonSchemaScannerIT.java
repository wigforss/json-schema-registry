package org.kasource.json.schema.integration;

import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import static org.junit.Assert.*;
import org.junit.Test;

import org.kasource.json.schema.JsonSchemaScanner;
import org.kasource.json.schema.registry.JsonSchemaRegistration;
import org.kasource.json.schema.registry.JsonSchemaRegistry;

public class JsonSchemaScannerIT {

    @Test
    public void findAnnotationsConsiderMetaAnnotation() {
        JsonSchemaScanner discoverer = new JsonSchemaScanner(new ObjectMapper(), true);
        JsonSchemaRegistry repo = discoverer.scan("org.kasource.json.schema.integration");
        Optional<JsonSchemaRegistration> registration = repo.getSchemaRegistration("some", "1.0");

        assertTrue(registration.isPresent());
        assertEquals(SchemaClass.class, registration.get().getSerDeClass());

        registration = repo.getSchemaRegistration("extended", "1.0");

        assertTrue(registration.isPresent());
        assertEquals(AnotherSchemaClass.class, registration.get().getSerDeClass());

    }

    @Test
    public void findAnnotations() {
        JsonSchemaScanner discoverer = new JsonSchemaScanner(new ObjectMapper());
        JsonSchemaRegistry repo = discoverer.scan("org.kasource.json.schema.integration");
        Optional<JsonSchemaRegistration> registration = repo.getSchemaRegistration("some", "1.0");

        assertTrue(registration.isPresent());
        assertEquals(SchemaClass.class, registration.get().getSerDeClass());

        registration = repo.getSchemaRegistration("extended", "1.0");

        assertFalse(registration.isPresent());

    }
}
