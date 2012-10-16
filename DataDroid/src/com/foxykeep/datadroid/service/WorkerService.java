/**
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package com.foxykeep.datadroid.service;

import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.foxykeep.datadroid.BuildConfig;
import com.foxykeep.datadroid.config.LogConfig;
import com.foxykeep.datadroid.requestmanager.RequestManager;

/**
 * This class is the superclass of all the worker services you'll create.
 * 
 * @author Foxykeep
 */
public abstract class WorkerService extends MultiThreadedIntentService {

    public static final String LOG_TAG = WorkerService.class.getSimpleName();

    public static final String INTENT_EXTRA_WORKER_TYPE = "com.foxykeep.datadroid.extras.workerType";
    public static final String INTENT_EXTRA_REQUEST_ID = "com.foxykeep.datadroid.extras.requestId";
    public static final String INTENT_EXTRA_RECEIVER = "com.foxykeep.datadroid.extras.receiver";

    public static final int SUCCESS_CODE = 0;
    public static final int ERROR_CODE = -1;

    public WorkerService(final int maxThreads) {
        super(maxThreads);
    }

    /**
     * Proxy method for {@link #sendResult(Intent, Bundle, int)} when the work is a success.
     * 
     * @param intent The value passed to {@link onHandleIntent(Intent)}.
     * @param data A {@link Bundle} with the data to send back.
     */
    protected void sendSuccess(final Intent intent, final Bundle data) {
        sendResult(intent, data, SUCCESS_CODE);
    }

    /**
     * Proxy method for {@link #sendResult(Intent, Bundle, int)} when the work is a failure.
     * 
     * @param intent The value passed to {@link onHandleIntent(Intent)}.
     * @param data A {@link Bundle} the data to send back.
     */
    protected void sendFailure(final Intent intent, final Bundle data) {
        sendResult(intent, data, ERROR_CODE);
    }

    /**
     * Proxy method for {@link #sendResult(Intent, Bundle, int)} when the work is a failure due to
     * the network.
     * 
     * @param intent The value passed to {@link onHandleIntent(Intent)}.
     * @param data A {@link Bundle} the data to send back.
     */
    protected void sendConnexionFailure(final Intent intent, Bundle data) {
        if (data == null) {
            data = new Bundle();
        }
        data.putInt(RequestManager.RECEIVER_EXTRA_ERROR_TYPE,
                RequestManager.RECEIVER_EXTRA_VALUE_ERROR_TYPE_CONNEXION);
        sendResult(intent, data, ERROR_CODE);
    }

    /**
     * Proxy method for {@link #sendResult(Intent, Bundle, int)} when the work is a failure due to
     * the data (parsing for example).
     * 
     * @param intent The value passed to {@link onHandleIntent(Intent)}.
     * @param data A {@link Bundle} the data to send back.
     */
    protected void sendDataFailure(final Intent intent, Bundle data) {
        if (data == null) {
            data = new Bundle();
        }
        data.putInt(RequestManager.RECEIVER_EXTRA_ERROR_TYPE,
                RequestManager.RECEIVER_EXTRA_VALUE_ERROR_TYPE_DATA);
        sendResult(intent, data, ERROR_CODE);
    }

    /**
     * Method used to send back the result to the {@link RequestManager}.
     * 
     * @param intent The value passed to {@link onHandleIntent(Intent)}. It must contain the
     *            {@link ResultReceiver} and the requestId.
     * @param data A {@link Bundle} the data to send back.
     * @param code The success/error code to send back.
     */
    protected void sendResult(final Intent intent, Bundle data, final int code) {

        if (LogConfig.DD_DEBUG_LOGS_ENABLED && BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "sendResult : " + ((code == SUCCESS_CODE) ? "Success" : "Failure"));
        }

        ResultReceiver receiver = (ResultReceiver) intent.getParcelableExtra(INTENT_EXTRA_RECEIVER);

        if (receiver != null) {
            if (data == null) {
                data = new Bundle();
            }

            data.putInt(RequestManager.RECEIVER_EXTRA_REQUEST_ID,
                    intent.getIntExtra(INTENT_EXTRA_REQUEST_ID, -1));

            receiver.send(code, data);
        }
    }
}
