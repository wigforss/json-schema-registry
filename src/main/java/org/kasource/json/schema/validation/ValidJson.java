package org.kasource.json.schema.validation;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import org.kasource.json.schema.validation.impl.JsonSchemaNodeValidatorImpl;
import org.kasource.json.schema.validation.impl.JsonSchemaStringValidatorImpl;


@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({METHOD, FIELD, CONSTRUCTOR, PARAMETER, ANNOTATION_TYPE})
@Constraint(validatedBy = {JsonSchemaStringValidatorImpl.class, JsonSchemaNodeValidatorImpl.class})
public @interface ValidJson {

    String message() default "does not comply to the JSON Schema";

    /**
     * Class annotated with om.qgcommunications.tisha.jsonschema.annotation.JsonSchema.
     *
     * @return Class annotated with @JsonSchema
     **/
    Class<?> jsonSchemaClass();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
