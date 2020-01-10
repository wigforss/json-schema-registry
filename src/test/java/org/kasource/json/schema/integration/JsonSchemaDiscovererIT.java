package org.kasource.json.schema.integration;

import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import static org.junit.Assert.*;
import org.junit.Test;

import org.kasource.json.schema.JsonSchemaDiscoverer;
import org.kasource.json.schema.registry.JsonSchemaRegistration;
import org.kasource.json.schema.registry.JsonSchemaRegistry;

public class JsonSchemaDiscovererIT {

    @Test
    public void findAnnotationsConsiderMetaAnnotation() {
        JsonSchemaDiscoverer discoverer = new JsonSchemaDiscoverer(new ObjectMapper(), true);
        JsonSchemaRegistry repo = discoverer.discoverSchemas("org.kasource.json.schema.integration");
        Optional<JsonSchemaRegistration> registration = repo.getSchemaRegistration("some", "1.0");

        assertTrue(registration.isPresent());
        assertEquals(SchemaClass.class, registration.get().getSerDeClass());

        registration = repo.getSchemaRegistration("extended", "1.0");

        assertTrue(registration.isPresent());
        assertEquals(AnotherSchemaClass.class, registration.get().getSerDeClass());

    }

    @Test
    public void findAnnotations() {
        JsonSchemaDiscoverer discoverer = new JsonSchemaDiscoverer(new ObjectMapper());
        JsonSchemaRegistry repo = discoverer.discoverSchemas("org.kasource.json.schema.integration");
        Optional<JsonSchemaRegistration> registration = repo.getSchemaRegistration("some", "1.0");

        assertTrue(registration.isPresent());
        assertEquals(SchemaClass.class, registration.get().getSerDeClass());

        registration = repo.getSchemaRegistration("extended", "1.0");

        assertFalse(registration.isPresent());

    }
}
