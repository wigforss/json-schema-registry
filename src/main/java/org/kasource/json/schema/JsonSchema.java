package org.kasource.json.schema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks classes as backed by a JSON Schema, these classes will be
 * auto-discovered.
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonSchema {
    String name();

    String version();

    String location();
}
