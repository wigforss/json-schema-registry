package org.kasource.json.schema.validation;


import com.github.fge.jsonschema.core.report.ProcessingReport;

public class InvalidJsonException extends RuntimeException {

    private static final long serialVersionUID = -6365139625827703646L;

    private final transient ProcessingReport processingReport;

    public InvalidJsonException(final ProcessingReport processingReport, final String message) {
        super(message);
        this.processingReport = processingReport;
    }

    public ProcessingReport getProcessingReport() {
        return processingReport;
    }
}
