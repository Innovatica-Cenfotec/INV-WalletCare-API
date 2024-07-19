package com.inv.walletCare.logic.exceptions;

/**
 * Custom exception class for handling field validation errors within requests.
 * This exception is thrown when a specific field does not meet the validation criteria.
 */
public class FieldValidationException extends IllegalArgumentException {
    // The name of the field that caused the validation error.
    private final String field;
    // The validation error message.
    private final String message;

    /**
     * Constructs a new FieldValidationException with the specified detail message and field name.
     *
     * @param field   The name of the field that caused the validation error.
     * @param message The detailed message for the validation error.
     */
    public FieldValidationException(String field, String message) {
        super(message);
        this.field = field;
        this.message = message;
    }

    /**
     * Retrieves the name of the field that caused the validation error.
     *
     * @return The name of the field.
     */
    public String getField() {
        return field;
    }

    /**
     * Retrieves the validation error message.
     *
     * @return The error message.
     */
    public String getMessage() {
        return message;
    }
}