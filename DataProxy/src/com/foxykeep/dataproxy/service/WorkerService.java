/*
 * 2011 Foxykeep (http://www.foxykeep.com)
 *
 * Licensed under the Beerware License :
 * 
 *   As long as you retain this notice you can do whatever you want with this stuff. If we meet some day, and you think
 *   this stuff is worth it, you can buy me a beer in return
 */
package com.foxykeep.dataproxy.service;

import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.foxykeep.dataproxy.config.LogConfig;
import com.foxykeep.dataproxy.requestmanager.RequestManager;

/**
 * This class is the superclass of all the worker service you'll create.
 * 
 * @author Foxykeep
 */
abstract public class WorkerService extends MultiThreadService {

    public static final String LOG_TAG = WorkerService.class.getSimpleName();

    public static final String INTENT_EXTRA_WORKER_TYPE = "workerType";
    public static final String INTENT_EXTRA_REQUEST_ID = "requestId";
    public static final String INTENT_EXTRA_RECEIVER = "receiver";

    public static final int SUCCESS_CODE = 0;
    public static final int ERROR_CODE = -1;

    public WorkerService(int maxThreads) {
        super(maxThreads);
    }

    /**
     * Proxy method for {@link #sendResult(Intent, Bundle, int)} when the work
     * is a success
     * 
     * @param intent The value passed to {@link onHandleIntent(Intent)}.
     * @param data A {@link Bundle} with the data to send back
     */
    protected void sendSuccess(Intent intent, Bundle data) {
        sendResult(intent, data, SUCCESS_CODE);
    }

    /**
     * Proxy method for {@link #sendResult(Intent, Bundle, int)} when the work
     * is a failure
     * 
     * @param intent The value passed to {@link onHandleIntent(Intent)}.
     * @param data A {@link Bundle} the data to send back
     */
    protected void sendFailure(Intent intent, Bundle data) {
        sendResult(intent, data, ERROR_CODE);
    }

    /**
     * Method used to send back the result to the {@link RequestManager}
     * 
     * @param intent The value passed to {@link onHandleIntent(Intent)}. It must
     *            contain the {@link ResultReceiver} and the requestId
     * @param data A {@link Bundle} the data to send back
     * @param code The sucess/error code to send back
     */
    protected void sendResult(Intent intent, Bundle data, int code) {

        if (LogConfig.DEBUG_LOGS_ENABLED) {
            Log.d(LOG_TAG, "sendResult");
        }

        ResultReceiver receiver = (ResultReceiver) intent.getParcelableExtra(INTENT_EXTRA_RECEIVER);

        if (receiver != null) {
            if (data == null) {
                data = new Bundle();
            }

            data.putInt(RequestManager.RECEIVER_EXTRA_REQUEST_ID, intent.getIntExtra(INTENT_EXTRA_REQUEST_ID, -1));

            receiver.send(code, data);
        }
    }
}
