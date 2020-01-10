package org.kasource.json.schema.registry;


public class DuplicateJsonSchemaException extends RuntimeException {

    private static final long serialVersionUID = 842087526055488044L;

    public DuplicateJsonSchemaException(final String message) {
        super(message);
    }
}
