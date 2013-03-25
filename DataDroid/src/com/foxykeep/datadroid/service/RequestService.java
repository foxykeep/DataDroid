/**
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package com.foxykeep.datadroid.service;

import com.foxykeep.datadroid.exception.ConnectionException;
import com.foxykeep.datadroid.exception.CustomRequestException;
import com.foxykeep.datadroid.exception.DataException;
import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.requestmanager.RequestManager;
import com.foxykeep.datadroid.util.DataDroidLog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

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
         * @param context The context to use for your operation.
         * @param request The request to execute.
         * @return A {@link Bundle} containing the data to return. If no data to return, null.
         * @throws ConnectionException Thrown when a connection error occurs. It will be propagated
         *             to the {@link RequestManager} as a
         *             {@link RequestManager#ERROR_TYPE_CONNEXION}.
         * @throws DataException Thrown when a problem occurs while managing the data of the
         *             webservice. It will be propagated to the {@link RequestManager} as a
         *             {@link RequestManager#ERROR_TYPE_DATA}.
         * @throws CustomRequestException Any other exception you may have to throw. A call to
         *             {@link RequestService#onCustomRequestException(Request,
         *             CustomRequestException)} will be made with the Exception thrown.
         */
        public Bundle execute(Context context, Request request) throws ConnectionException,
                DataException, CustomRequestException;
    }

    private static final String LOG_TAG = RequestService.class.getSimpleName();

    public static final String INTENT_EXTRA_RECEIVER = "com.foxykeep.datadroid.extra.receiver";
    public static final String INTENT_EXTRA_REQUEST = "com.foxykeep.datadroid.extra.request";

    private static final int SUCCESS_CODE = 0;
    public static final int ERROR_CODE = -1;

    /**
     * Proxy method for {@link #sendResult(ResultReceiver, Bundle, int)} when the work is a
     * success.
     *
     * @param receiver The result receiver received inside the {@link Intent}.
     * @param data A {@link Bundle} with the data to send back.
     */
    private void sendSuccess(ResultReceiver receiver, Bundle data) {
        sendResult(receiver, data, SUCCESS_CODE);
    }

    /**
     * Proxy method for {@link #sendResult(ResultReceiver, Bundle, int)} when the work is a failure
     * due to the network.
     *
     * @param receiver The result receiver received inside the {@link Intent}.
     * @param exception The {@link ConnectionException} triggered.
     */
    private void sendConnexionFailure(ResultReceiver receiver, ConnectionException exception) {
        Bundle data = new Bundle();
        data.putInt(RequestManager.RECEIVER_EXTRA_ERROR_TYPE, RequestManager.ERROR_TYPE_CONNEXION);
        data.putInt(RequestManager.RECEIVER_EXTRA_CONNECTION_ERROR_STATUS_CODE,
                exception.getStatusCode());
        sendResult(receiver, data, ERROR_CODE);
    }

    /**
     * Proxy method for {@link #sendResult(ResultReceiver, Bundle, int)} when the work is a failure
     * due to the data (parsing for example).
     *
     * @param receiver The result receiver received inside the {@link Intent}.
     */
    private void sendDataFailure(ResultReceiver receiver) {
        Bundle data = new Bundle();
        data.putInt(RequestManager.RECEIVER_EXTRA_ERROR_TYPE, RequestManager.ERROR_TYPE_DATA);
        sendResult(receiver, data, ERROR_CODE);
    }

    /**
     * Proxy method for {@link #sendResult(ResultReceiver, Bundle, int)} when the work is a failure
     * due to {@link CustomRequestException} being thrown.
     *
     * @param receiver The result receiver received inside the {@link Intent}.
     * @param data A {@link Bundle} the data to send back.
     */
    private void sendCustomFailure(ResultReceiver receiver, Bundle data) {
        if (data == null) {
            data = new Bundle();
        }
        data.putInt(RequestManager.RECEIVER_EXTRA_ERROR_TYPE, RequestManager.ERROR_TYPE_CUSTOM);
        sendResult(receiver, data, ERROR_CODE);
    }

    /**
     * Method used to send back the result to the {@link RequestManager}.
     *
     * @param receiver The result receiver received inside the {@link Intent}.
     * @param data A {@link Bundle} the data to send back.
     * @param code The success/error code to send back.
     */
    private void sendResult(ResultReceiver receiver, Bundle data, int code) {
        DataDroidLog.d(LOG_TAG, "sendResult : " + ((code == SUCCESS_CODE) ? "Success" : "Failure"));

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
        request.setClassLoader(getClassLoader());

        ResultReceiver receiver = intent.getParcelableExtra(INTENT_EXTRA_RECEIVER);

        Operation operation = getOperationForType(request.getRequestType());
        try {
            sendSuccess(receiver, operation.execute(this, request));
        } catch (ConnectionException e) {
            DataDroidLog.e(LOG_TAG, "ConnectionException", e);
            sendConnexionFailure(receiver, e);
        } catch (DataException e) {
            DataDroidLog.e(LOG_TAG, "DataException", e);
            sendDataFailure(receiver);
        } catch (CustomRequestException e) {
            DataDroidLog.e(LOG_TAG, "Custom Exception", e);
            sendCustomFailure(receiver, onCustomRequestException(request, e));
        } catch (RuntimeException e) {
            DataDroidLog.e(LOG_TAG, "RuntimeException", e);
            sendDataFailure(receiver);
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
     * Call if a {@link CustomRequestException} is thrown by an {@link Operation}. You may return a
     * Bundle containing data to return to the {@link RequestManager}.
     * <p>
     * Default implementation return null. You may want to override this method in your
     * implementation of {@link RequestService} to execute specific action and/or return specific
     * data.
     *
     * @param request The {@link Request} which execution threw the exception.
     * @param exception The {@link CustomRequestException} thrown.
     * @return A {@link Bundle} containing data to return to the {@link RequestManager}. Default
     *         implementation return null.
     */
    protected Bundle onCustomRequestException(Request request, CustomRequestException exception) {
        return null;
    }

}
