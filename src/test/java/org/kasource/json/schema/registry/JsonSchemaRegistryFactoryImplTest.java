package org.kasource.json.schema.registry;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class JsonSchemaRegistryFactoryImplTest {

    @Mock
    private ObjectMapper objectMapper;

    private JsonSchemaRegistryFactoryImpl factory = new JsonSchemaRegistryFactoryImpl(objectMapper);

    @Test
    public void create() {
        JsonSchemaRegistry repository = factory.create();

        assertThat(repository, notNullValue());
    }
}
