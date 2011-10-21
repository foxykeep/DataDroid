/*
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
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
    private int mErrorStatus = -1;

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
    public RestClientException(final String detailMessage, final Throwable throwable) {
        super(detailMessage, throwable);
    }

    /**
     * Constructs a new {@link RestClientException} that includes the current
     * stack trace and the specified detail message.
     * 
     * @param detailMessage the detail message for this exception.
     */
    public RestClientException(final String detailMessage) {
        super(detailMessage);
    }

    /**
     * Constructs a new {@link RestClientException} that includes the current
     * stack trace and the specified detail message.
     * 
     * @param detailMessage the detail message for this exception.
     * @param redirection url.
     */
    public RestClientException(final String detailMessage, final String redirectionUrl) {
        super(detailMessage);
        mNewUrl = redirectionUrl;
    }

    /**
     * Constructs a new {@link RestClientException} that includes the current
     * stack trace and the specified detail message and the error status code
     * 
     * @param detailMessage the detail message for this exception.
     * @param errorStatus
     */
    public RestClientException(final String detailMessage, final int errorStatus) {
        super(detailMessage);
        mErrorStatus = errorStatus;
    }

    /**
     * Constructs a new {@link RestClientException} that includes the current
     * stack trace and the specified cause.
     * 
     * @param throwable the cause of this exception.
     */
    public RestClientException(final Throwable throwable) {
        super(throwable);
    }

    public String getRedirectionUrl() {
        return mNewUrl;
    }

    public int getErrorStatus() {
        return mErrorStatus;
    }

}
