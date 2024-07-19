package com.inv.walletCare.logic.entity.response;

/**
 * The {@code Response} class represents a simple response object
 * containing a message. This class provides methods to get and set
 * the message.
 */
public class Response {

    /**
     * The message contained in the response.
     */
    private String message;

    /**
     * Constructs a new {@code Response} with the specified message.
     *
     * @param message the message to be set in the response
     */
    public Response(String message) {
        this.message = message;
    }

    /**
     * Returns the message contained in this response.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message contained in this response.
     *
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
