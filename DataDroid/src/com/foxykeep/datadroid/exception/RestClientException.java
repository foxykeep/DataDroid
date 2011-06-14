/*
 * 2011 Foxykeep (http://www.foxykeep.com)
 *
 * Licensed under the Beerware License :
 * 
 *   As long as you retain this notice you can do whatever you want with this stuff. If we meet some day, and you think
 *   this stuff is worth it, you can buy me a beer in return
 */
package com.foxykeep.datadroid.exception;

/**
 * Thrown to indicate that a compulsory parameter is missing.
 * 
 * @author Foxykeep
 */
public class RestClientException extends Exception {

    private static final long serialVersionUID = 4658308128254827562L;

    private String mNewUrl;

    /**
     * Constructs a new {@link RestClientException} that includes the current
     * stack trace.
     */
    public RestClientException() {
        super();
    }

    /**
     * Constructs a new {@link RestClientException} that includes the current
     * stack trace, the specified detail message and the specified cause.
     * 
     * @param detailMessage the detail message for this exception.
     * @param throwable the cause of this exception.
     */
    public RestClientException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    /**
     * Constructs a new {@link RestClientException} that includes the current
     * stack trace and the specified detail message.
     * 
     * @param detailMessage the detail message for this exception.
     */
    public RestClientException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * Constructs a new {@link RestClientException} that includes the current
     * stack trace and the specified detail message.
     * 
     * @param detailMessage the detail message for this exception.
     * @param redirection url.
     */
    public RestClientException(String detailMessage, String redirectionUrl) {
        super(detailMessage);
        mNewUrl = redirectionUrl;
    }

    /**
     * Constructs a new {@link RestClientException} that includes the current
     * stack trace and the specified cause.
     * 
     * @param throwable the cause of this exception.
     */
    public RestClientException(Throwable throwable) {
        super(throwable);
    }

    public String getRedirectionUrl() {
        return mNewUrl;
    }

}
