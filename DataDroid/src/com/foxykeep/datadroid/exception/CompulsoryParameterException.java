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
public class CompulsoryParameterException extends RuntimeException {

    private static final long serialVersionUID = -6031863210486494461L;

    /**
     * Constructs a new {@link CompulsoryParameterException} that includes the
     * current stack trace.
     */
    public CompulsoryParameterException() {
        super();
    }

    /**
     * Constructs a new {@link CompulsoryParameterException} that includes the
     * current stack trace, the specified detail message and the specified
     * cause.
     * 
     * @param detailMessage the detail message for this exception.
     * @param throwable the cause of this exception.
     */
    public CompulsoryParameterException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    /**
     * Constructs a new {@link CompulsoryParameterException} that includes the
     * current stack trace and the specified detail message.
     * 
     * @param detailMessage the detail message for this exception.
     */
    public CompulsoryParameterException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * Constructs a new {@link CompulsoryParameterException} that includes the
     * current stack trace and the specified cause.
     * 
     * @param throwable the cause of this exception.
     */
    public CompulsoryParameterException(Throwable throwable) {
        super(throwable);
    }

}
