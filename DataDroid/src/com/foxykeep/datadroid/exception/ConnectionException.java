/**
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package com.foxykeep.datadroid.exception;

/**
 * Thrown to indicate that a connection error occurred.
 *
 * @author Foxykeep
 */
public final class ConnectionException extends Exception {

    private static final long serialVersionUID = 4658308128254827562L;

    private String mRedirectionUrl;
    private int mStatusCode = -1;

    /**
     * Constructs a new {@link ConnectionException} that includes the current stack trace.
     */
    public ConnectionException() {
        super();
    }

    /**
     * Constructs a new {@link ConnectionException} that includes the current stack trace, the
     * specified detail message and the specified cause.
     *
     * @param detailMessage The detail message for this exception.
     * @param throwable The cause of this exception.
     */
    public ConnectionException(final String detailMessage, final Throwable throwable) {
        super(detailMessage, throwable);
    }

    /**
     * Constructs a new {@link ConnectionException} that includes the current stack trace and the
     * specified detail message.
     *
     * @param detailMessage The detail message for this exception.
     */
    public ConnectionException(final String detailMessage) {
        super(detailMessage);
    }

    /**
     * Constructs a new {@link ConnectionException} that includes the current stack trace and the
     * specified detail message.
     *
     * @param detailMessage The detail message for this exception.
     * @param redirectionUrl The redirection URL.
     */
    public ConnectionException(final String detailMessage, final String redirectionUrl) {
        super(detailMessage);
        mRedirectionUrl = redirectionUrl;
        mStatusCode = 301;
    }

    /**
     * Constructs a new {@link ConnectionException} that includes the current stack trace and the
     * specified detail message and the error status code
     *
     * @param detailMessage The detail message for this exception.
     * @param statusCode The HTTP status code
     */
    public ConnectionException(final String detailMessage, final int statusCode) {
        super(detailMessage);
        mStatusCode = statusCode;
    }

    /**
     * Constructs a new {@link ConnectionException} that includes the current stack trace and the
     * specified cause.
     *
     * @param throwable The cause of this exception.
     */
    public ConnectionException(final Throwable throwable) {
        super(throwable);
    }

    public String getRedirectionUrl() {
        return mRedirectionUrl;
    }

    public int getStatusCode() {
        return mStatusCode;
    }

}
