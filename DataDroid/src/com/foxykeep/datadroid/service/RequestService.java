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

import com.foxykeep.datadroid.config.LogConfig;
import com.foxykeep.datadroid.exception.ConnectionException;
import com.foxykeep.datadroid.exception.DataException;
import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.requestmanager.RequestManager;

/**
 * This class is the superclass of all the worker services you'll create.
 *
 * @author Foxykeep
 */
public abstract class RequestService extends MultiThreadedIntentService {

    /**
     * Interface to implement by your operations
     *
     * @author Foxykeep
     */
    public interface Operation {
        /**
         * Execute the request and returns a {@link Bundle} containing the data to return.
         *
         * @param request The request to execute.
         * @return A {@link Bundle} containing the data to return. If no data to return, null.
         * @throws ConnectionException Thrown when a connection error occurs. It will be propagated
         *             to the {@link RequestManager} as a
         *             {@link RequestManager#ERROR_TYPE_CONNEXION}.
         * @throws DataException Thrown when a problem occurs while managing the data of the
         *             webservice. It will be propagated to the {@link RequestManager} as a
         *             {@link RequestManager#ERROR_TYPE_DATA}.
         * @throws Exception Any other exception you may have to throw. A call to
         *             {@link RequestService#onCustomError(Exception)} will be made with the
         *             Exception thrown.
         */
        public Bundle execute(Request request) throws ConnectionException, DataException, Exception;
    }

    public static final String LOG_TAG = RequestService.class.getSimpleName();

    public static final String INTENT_EXTRA_RECEIVER = "com.foxykeep.datadroid.extras.receiver";
    public static final String INTENT_EXTRA_REQUEST = "com.foxykeep.datadroid.extras.request";

    public static final int SUCCESS_CODE = 0;
    public static final int ERROR_CODE = -1;

    public RequestService(final int maxThreads) {
        super(maxThreads);
    }

    /**
     * Proxy method for {@link #sendResult(Intent, Bundle, int)} when the work is a success.
     *
     * @param intent The value passed to {@link onHandleIntent(Intent)}.
     * @param data A {@link Bundle} with the data to send back.
     */
    protected final void sendSuccess(ResultReceiver receiver, Bundle data) {
        sendResult(receiver, data, SUCCESS_CODE);
    }

    /**
     * Proxy method for {@link #sendResult(Intent, Bundle, int)} when the work is a failure due to
     * the network.
     *
     * @param intent The value passed to {@link onHandleIntent(Intent)}.
     * @param data A {@link Bundle} the data to send back.
     */
    protected final void sendConnexionFailure(ResultReceiver receiver, Bundle data) {
        if (data == null) {
            data = new Bundle();
        }
        data.putInt(RequestManager.RECEIVER_EXTRA_ERROR_TYPE, RequestManager.ERROR_TYPE_CONNEXION);
        sendResult(receiver, data, ERROR_CODE);
    }

    /**
     * Proxy method for {@link #sendResult(Intent, Bundle, int)} when the work is a failure due to
     * the data (parsing for example).
     *
     * @param intent The value passed to {@link onHandleIntent(Intent)}.
     * @param data A {@link Bundle} the data to send back.
     */
    protected final void sendDataFailure(ResultReceiver receiver, Bundle data) {
        if (data == null) {
            data = new Bundle();
        }
        data.putInt(RequestManager.RECEIVER_EXTRA_ERROR_TYPE, RequestManager.ERROR_TYPE_DATA);
        sendResult(receiver, data, ERROR_CODE);
    }

    /**
     * Method used to send back the result to the {@link RequestManager}.
     *
     * @param intent The value passed to {@link onHandleIntent(Intent)}. It must contain the
     *            {@link ResultReceiver} and the requestId.
     * @param data A {@link Bundle} the data to send back.
     * @param code The success/error code to send back.
     */
    private void sendResult(ResultReceiver receiver, Bundle data, int code) {
        if (LogConfig.DD_DEBUG_LOGS_ENABLED) {
            Log.d(LOG_TAG, "sendResult : " + ((code == SUCCESS_CODE) ? "Success" : "Failure"));
        }

        if (receiver != null) {
            if (data == null) {
                data = new Bundle();
            }

            receiver.send(code, data);
        }
    }

    @Override
    protected final void onHandleIntent(Intent intent) {
        Request request = intent.getParcelableExtra(INTENT_EXTRA_REQUEST);
        ResultReceiver receiver = intent.getParcelableExtra(INTENT_EXTRA_RECEIVER);

        Operation operation = getOperationForType(request.getRequestType());
        try {
            sendSuccess(receiver, operation.execute(request));
        } catch (ConnectionException e) {
            if (LogConfig.DD_ERROR_LOGS_ENABLED) {
                Log.e(LOG_TAG, "ConnectionException", e);
            }
            sendConnexionFailure(receiver, null);
        } catch (DataException e) {
            if (LogConfig.DD_ERROR_LOGS_ENABLED) {
                Log.e(LOG_TAG, "DataException", e);
            }
            sendDataFailure(receiver, null);
        } catch (RuntimeException e) {
            if (LogConfig.DD_ERROR_LOGS_ENABLED) {
                Log.e(LOG_TAG, "RuntimeException", e);
            }
            sendDataFailure(receiver, null);
        } catch (Exception e) {
            if (LogConfig.DD_ERROR_LOGS_ENABLED) {
                Log.e(LOG_TAG, "Custom Exception", e);
            }
            sendDataFailure(receiver, onCustomError(e));
        }

    }

    /**
     * Get the {@link Operation} corresponding to the given request type.
     *
     * @param requestType The request type (extracted from {@link Request}).
     * @return The corresponding {@link Operation}.
     */
    public abstract Operation getOperationForType(int requestType);

    /**
     * Call if a custom {@link Exception} is thrown by an {@link Operation}. You may return a Bundle
     * containing data to return to the {@link RequestManager}.
     * <p>
     * Default implementation return null. You may want to override this method in your
     * implementation of {@link RequestService} to execute specific action and/or return specific
     * data.
     *
     * @param exception The custom {@link Exception} thrown.
     * @return A {@link Bundle} containing data to return to the {@link RequestManager}. Default
     *         implementation return null.
     */
    protected Bundle onCustomError(Exception exception) {
        return null;
    }

}
