package org.kasource.json.schema.registry;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kasource.commons.collection.builder.MapBuilder;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.unitils.inject.util.InjectionUtils;

import org.kasource.json.schema.JsonSchema;

@RunWith(MockitoJUnitRunner.class)
public class JsonSchemaRegistryImplTest {
    private static final String SCHEMA_NAME = "person";
    private static final String SCHEMA_VERSION = "1.0";

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private Map<String, Map<String, JsonSchemaRegistration>> schemas;

    @Mock
    private Map<Class<?>, JsonSchemaRegistration> schemasPerClass;

    @Mock
    private Map<String, JsonSchemaRegistration> schemasPerName;

    @Mock
    private JsonSchemaRegistration duplicate;

    @Mock
    private JsonSchemaRegistration registration;

    @Mock
    private JsonSchemaRegistration registration2;

    @Mock
    private JsonSchemaRegistration registration3;

    @Captor
    private ArgumentCaptor<JsonSchemaRegistration> registrationCaptor;

    @InjectMocks
    private JsonSchemaRegistryImpl repository = new JsonSchemaRegistryImpl(objectMapper);

    @Test
    public void getSchemaRegistration() {
        when(schemas.get(SCHEMA_NAME)).thenReturn(schemasPerName);
        when(schemasPerName.get(SCHEMA_VERSION)).thenReturn(registration);

        Optional<JsonSchemaRegistration> reg = repository.getSchemaRegistration(SCHEMA_NAME, SCHEMA_VERSION);

        assertThat(reg, equalTo(Optional.of(registration)));
    }

    @Test
    public void getSchemaRegistrationNotFound() {
        when(schemas.get(SCHEMA_NAME)).thenReturn(null);

        Optional<JsonSchemaRegistration> reg = repository.getSchemaRegistration(SCHEMA_NAME, SCHEMA_VERSION);

        assertThat(reg, equalTo(Optional.empty()));
    }

    @Test
    public void getSchemaRegistrationForClass() {
        Class<?> clazz = String.class;

        when(schemasPerClass.get(clazz)).thenReturn(registration);

        Optional<JsonSchemaRegistration> reg = repository.getSchemaRegistration(clazz);

        assertThat(reg.get(), is(equalTo(registration)));
    }


    @Test
    public void isSupported() {
        when(schemas.get(SCHEMA_NAME)).thenReturn(schemasPerName);
        when(schemasPerName.get(SCHEMA_VERSION)).thenReturn(registration);

        boolean supported = repository.isSupported(SCHEMA_NAME, SCHEMA_VERSION);

        assertThat(supported, is(true));
    }

    @Test
    public void isNotSupported() {
        when(schemas.get(SCHEMA_NAME)).thenReturn(null);

        boolean supported = repository.isSupported(SCHEMA_NAME, SCHEMA_VERSION);

        assertThat(supported, is(false));
    }

    @Test
    public void supportedVersions() {
        when(schemas.get(SCHEMA_NAME)).thenReturn(new MapBuilder<String, JsonSchemaRegistration>().put(SCHEMA_VERSION, registration).build());
        Set<String> versions = repository.getSupportedVersions(SCHEMA_NAME);

        assertThat(versions, notNullValue());
        assertThat(versions.size(), is(1));
        assertThat(versions, contains(equalTo(SCHEMA_VERSION)));
    }

    @Test
    public void noSupportedVersions() {
        when(schemas.get(SCHEMA_NAME)).thenReturn(null);
        Set<String> versions = repository.getSupportedVersions(SCHEMA_NAME);

        assertThat(versions, notNullValue());
        assertThat(versions.isEmpty(), is(true));

    }

    @Test
    public void registerNonSchemaClass() {
        repository.registerSchemaFor(String.class);
        verifyZeroInteractions(schemas);
    }

    @Test(expected = IllegalArgumentException.class)
    public void registerClassInvalidSchemaLocation() {
        repository.registerSchemaFor(InvalidSchemaLocationClass.class);
    }

    @Test(expected = IllegalStateException.class)
    public void registerClassMissingSchema() {
        repository.registerSchemaFor(MissingSchemaClass.class);
    }

    @Test(expected = DuplicateJsonSchemaException.class)
    public void registerClassDuplicate() {

        when(schemas.get(SCHEMA_NAME)).thenReturn(schemasPerName);
        when(schemasPerName.containsKey(SCHEMA_VERSION)).thenReturn(true);
        when(schemasPerName.get(SCHEMA_VERSION)).thenReturn(duplicate);
        when(duplicate.getSerDeClass()).thenReturn(this.getClass());

        repository.registerSchemaFor(Person.class);

        verify(schemasPerName).put(eq(SCHEMA_VERSION), registrationCaptor.capture());

        JsonSchemaRegistration schema = registrationCaptor.getValue();

        assertThat(schema, notNullValue());

        assertThat(schema.getJsonSchema(), notNullValue());
        assertThat(schema.getSerDeClass(), equalTo(Person.class));
        assertThat(schema.getNodeValidator(), notNullValue());
        assertThat(schema.getStringValidator(), notNullValue());

    }

    @Test
    public void registerClass() {

        when(schemas.get(SCHEMA_NAME)).thenReturn(schemasPerName);

        repository.registerSchemaFor(Person.class);
    }

    @Test
    public void getRegisteredSchemas() {
        Map<String, Map<String, JsonSchemaRegistration>> registeredSchemas =
        new MapBuilder<String, Map<String, JsonSchemaRegistration>>()
                .put("schema1", new MapBuilder<String, JsonSchemaRegistration>()
                        .put("1.0", registration)
                        .put("2.0", registration2)
                        .build())
                .put("schema2",  new MapBuilder<String, JsonSchemaRegistration>()
                        .put("3.0", registration3)
                        .build())
                .build();

        InjectionUtils.injectInto(registeredSchemas, repository, "schemas");


        Map<String, Map<String, JsonSchemaRegistration>> schemas = repository.getRegisteredSchemas();

        assertThat(schemas, is(notNullValue()));
        assertThat(schemas.size(), is(2));

        Map<String, JsonSchemaRegistration> schema1 = schemas.get("schema1");

        assertThat(schema1, is(notNullValue()));
        assertThat(schema1.containsKey("1.0"), is(true));
        assertThat(schema1.containsKey("2.0"), is(true));
        assertThat(schema1.containsKey("3.0"), is(false));
        assertThat(schema1.get("1.0"), is(equalTo(registration)));
        assertThat(schema1.get("2.0"), is(equalTo(registration2)));

        Map<String, JsonSchemaRegistration> schema2 = schemas.get("schema2");
        assertThat(schema2, is(notNullValue()));
        assertThat(schema2.containsKey("1.0"), is(false));
        assertThat(schema2.containsKey("3.0"), is(true));
        assertThat(schema2.get("3.0"), is(equalTo(registration3)));
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
}
