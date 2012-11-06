/**
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package com.foxykeep.datadroid.requestmanager;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.util.Log;

import com.foxykeep.datadroid.service.RequestService;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * This class allows to send requests through a {@link RequestService}.
 * <p>
 * This class needs to be subclassed in your project.
 *
 * @author Foxykeep
 */
public abstract class RequestManager {

    public static final String TAG = RequestManager.class.getSimpleName();

    /**
     * Clients may implements this interface to be notified when a request is finished.
     *
     * @author Foxykeep
     */
    public static interface OnRequestFinishedListener extends EventListener {

        /**
         * Event fired when a request is finished.
         *
         * @param request The {@link Request} defining the request.
         * @param resultCode The result code. Possible values :
         *            <ul>
         *            <li>{@link RequestService#SUCCESS_CODE} if succeeded.</li>
         *            <li>{@link RequestService#ERROR_CODE} if there was an error.</li>
         *            </ul>
         * @param resultData The result of the service execution.
         */
        public void onRequestFinished(Request request, int resultCode, Bundle resultData);
    }

    public static final String RECEIVER_EXTRA_REQUEST_DATA =
            "com.foxykeep.datadroid.extras.request";
    public static final String RECEIVER_EXTRA_RESULT_CODE = "com.foxykeep.datadroid.extras.code";
    public static final String RECEIVER_EXTRA_PAYLOAD = "com.foxykeep.datadroid.extras.payload";
    public static final String RECEIVER_EXTRA_ERROR_TYPE = "com.foxykeep.datadroid.extras.error";
    public static final int ERROR_TYPE_CONNEXION = 1;
    public static final int ERROR_TYPE_DATA = 2;

    private final Context mContext;

    private final Class<? extends RequestService> mRequestService;
    private final HashMap<Request, RequestReceiver> mRequestReceiverMap;

    protected RequestManager(Context context, Class<? extends RequestService> requestService) {
        mContext = context.getApplicationContext();

        mRequestService = requestService;
        mRequestReceiverMap = new HashMap<Request, RequestReceiver>();
    }

    /**
     * Add a {@link OnRequestFinishedListener} to this {@link RequestManager} to a specific
     * {@link Request}. Clients may use it in order to be notified when the corresponding request is
     * completed.
     * <p>
     * The listener are automatically removed when the request is completed and they are notified.
     * <p>
     * <b>Warning !! </b> If it's an {@link Activity} or a {@link Fragment} that is used as a
     * listener, it must be detached when {@link Activity#onPause} is called in an {@link Activity}.
     *
     * @param listener The listener called when the Request is completed.
     * @param request The {@link Request} to listen to.
     */
    public void addOnRequestFinishedListener(OnRequestFinishedListener listener,
            Request request) {
        if (listener == null) {
            return;
        }
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null.");
        }
        RequestReceiver requestReceiver = mRequestReceiverMap.get(request);
        if (requestReceiver == null) {
            Log.w(TAG, "You tried to add a listener to a non-existing request.");
            return;
        }

        requestReceiver.addListenerHolder(new ListenerHolder(listener));
    }

    /**
     * Remove a {@link OnRequestFinishedListener} to this {@link RequestManager} from every
     * {@link Request}s which it is listening to.
     *
     * @param listener The listener to remove.
     */
    public void removeOnRequestFinishedListener(OnRequestFinishedListener listener) {
        removeOnRequestFinishedListener(listener, null);
    }

    /**
     * Remove a {@link OnRequestFinishedListener} to this {@link RequestManager} from a specific
     * {@link Request}.
     *
     * @param listener The listener to remove.
     * @param request The {@link Request} associated with this listener. If null, the listener will
     *            be removed from every request it is currently associated with.
     */
    public void removeOnRequestFinishedListener(OnRequestFinishedListener listener,
            Request request) {
        if (listener == null) {
            return;
        }
        ListenerHolder holder = new ListenerHolder(listener);
        if (request != null) {
            RequestReceiver requestReceiver = mRequestReceiverMap.get(request);
            if (requestReceiver != null) {
                requestReceiver.removeListenerHolder(holder);
            }
        } else {
            for (RequestReceiver requestReceiver : mRequestReceiverMap.values()) {
                requestReceiver.removeListenerHolder(holder);
            }
        }
    }

    /**
     * Return whether a {@link Request} is still in progress or not.
     *
     * @param request The request.
     * @return Whether the request is still in progress or not.
     */
    public boolean isRequestInProgress(Request request) {
        return mRequestReceiverMap.containsKey(request);
    }

    /**
     * Execute the {@link Request}.
     *
     * @param request The request to execute.
     * @param listener The listener called when the Request is completed.
     */
    public void execute(Request request, OnRequestFinishedListener listener) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null.");
        }
        if (mRequestReceiverMap.containsKey(request)) {
            // This exact request is already in progress. So nothing to do.
            return;
        }

        RequestReceiver requestReceiver = new RequestReceiver(request);
        mRequestReceiverMap.put(request, requestReceiver);

        addOnRequestFinishedListener(listener, request);

        clearDataInMemory(request);

        Intent intent = new Intent(mContext, mRequestService);
        intent.putExtra(RequestService.INTENT_EXTRA_RECEIVER, requestReceiver);
        intent.putExtra(RequestService.INTENT_EXTRA_REQUEST, request);
        mContext.startService(intent);
    }

    /**
     * Overrides this method to persist your data in memory.
     * <p>
     * This method is called before calling the request listeners if any.
     *
     * @param request The executed request.
     * @param resultCode Arbitrary result code of the request.
     * @param resultData Any additional data part of the result of the request.
     */
    protected void persistDataToMemory(Request request, int resultCode, Bundle resultData) {}


    /**
     * Overrides this method to clear the data in memory corresponding to the given request.
     * <p>
     * This method is called before sending the request to the {@link RequestService}.
     *
     * @param request The request associated with the data.
     */
    protected void clearDataInMemory(Request request) {}

    private final class RequestReceiver extends ResultReceiver {

        private final Request mRequest;
        private final Set<ListenerHolder> mListenerHolderSet;

        RequestReceiver(Request request) {
            super(new Handler(Looper.getMainLooper()));

            mRequest = request;
            mListenerHolderSet = Collections.synchronizedSet(new HashSet<ListenerHolder>());
        }

        void addListenerHolder(ListenerHolder listenerHolder) {
            synchronized (mListenerHolderSet) {
                mListenerHolderSet.add(listenerHolder);
            }
        }

        void removeListenerHolder(ListenerHolder listenerHolder) {
            synchronized (mListenerHolderSet) {
                mListenerHolderSet.remove(listenerHolder);
            }
        }

        @Override
        public void onReceiveResult(int resultCode, Bundle resultData) {
            persistDataToMemory(mRequest, resultCode, resultData);

            mRequestReceiverMap.remove(mRequest);

            // Call the available listeners
            synchronized (mListenerHolderSet) {
                for (ListenerHolder listenerHolder : mListenerHolderSet) {
                    listenerHolder.onRequestFinished(mRequest, resultCode, resultData);
                }
            }
        }
    }

    private final class ListenerHolder {

        private final WeakReference<OnRequestFinishedListener> mListenerRef;
        private final int mHashCode;

        public ListenerHolder(OnRequestFinishedListener listener) {
            mListenerRef = new WeakReference<OnRequestFinishedListener>(listener);
            mHashCode = 31 + listener.hashCode();
        }

        public void onRequestFinished(Request request, int resultCode, Bundle resultData) {
            mRequestReceiverMap.remove(request);

            OnRequestFinishedListener listener = mListenerRef.get();
            if (listener != null) {
                listener.onRequestFinished(request, resultCode, resultData);
            }
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof ListenerHolder) {
                ListenerHolder oHolder = (ListenerHolder) o;
                return mListenerRef != null && oHolder.mListenerRef != null
                        && mHashCode == oHolder.mHashCode;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return mHashCode;
        }
    }
}
