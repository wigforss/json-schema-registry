package org.kasource.json.schema.registry;


import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface JsonSchemaRegistry {

    Optional<JsonSchemaRegistration> getSchemaRegistration(String name, String version);

    Optional<JsonSchemaRegistration> getSchemaRegistration(Class<?> serDeClass);

    boolean isSupported(String name, String version);

    Set<String> getSupportedVersions(String name);

    void registerSchemaFor(Class<?> serDeClass);

    /**
     * Returns a map of all versions of all schemas per schema name.
     *
     * The key of the returned map is schema name and the key of each value is version.
     *
     * @return Map of all versions of all schemas per schema name.
     **/
    Map<String, Map<String, JsonSchemaRegistration>> getRegisteredSchemas();
}
