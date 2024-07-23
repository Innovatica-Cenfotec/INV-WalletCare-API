package com.inv.walletCare.logic.entity;

/**
 * Represents a generic response structure for API operations.
 * This class encapsulates a simple message, typically used for conveying information about the result of an operation.
 */
public class Response {
    // The message to be conveyed in the response.
    private String message;

    public Response() {
    }

    /**
     * Constructs a new Response object with a specified message.
     *
     * @param message The message that this response should carry.
     */
    public Response(String message) {
        this.message = message;
    }

    /**
     * Retrieves the message contained in this response.
     *
     * @return The message of this response.
     */
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}