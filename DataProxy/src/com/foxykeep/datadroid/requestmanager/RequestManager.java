/*
 * 2011 Foxykeep (http://www.foxykeep.com)
 *
 * Licensed under the Beerware License :
 * 
 *   As long as you retain this notice you can do whatever you want with this stuff. If we meet some day, and you think
 *   this stuff is worth it, you can buy me a beer in return
 */
package com.foxykeep.datadroid.requestmanager;

/**
 * {@link RequestManager} is the superclass of the classes that will be
 * implemented in your project. It contains constants used in the library.
 * 
 * @author Foxykeep
 */
public abstract class RequestManager {

    public static final String RECEIVER_EXTRA_REQUEST_ID = "com.foxykeep.datadroid.extras.requestId";
    public static final String RECEIVER_EXTRA_RESULT_CODE = "com.foxykeep.datadroid.extras.code";
    public static final String RECEIVER_EXTRA_PAYLOAD = "com.foxykeep.datadroid.extras.payload";
    public static final String RECEIVER_EXTRA_ERROR_TYPE = "com.foxykeep.datadroid.extras.error";
    public static final int RECEIVER_EXTRA_VALUE_ERROR_TYPE_CONNEXION = 1;
    public static final int RECEIVER_EXTRA_VALUE_ERROR_TYPE_DATA = 2;
}
