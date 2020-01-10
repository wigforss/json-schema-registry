package org.kasource.json.schema.integration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.kasource.json.schema.JsonSchema;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@JsonSchema(name = "extended", version = "1.0", location = "/person-schema-v1.json")
public @interface ExtendedSchema {
}
