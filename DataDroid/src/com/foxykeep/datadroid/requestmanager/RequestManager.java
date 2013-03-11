/**
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package com.foxykeep.datadroid.requestmanager;

import com.foxykeep.datadroid.service.RequestService;
import com.foxykeep.datadroid.util.DataDroidLog;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.support.util.LruCache;

import org.apache.http.HttpStatus;

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
 * <p>
 * You can check the following page to see a tutorial on how to implement a webservice call using
 * the {@link RequestManager} : <a
 * href="http://www.datadroidlib.com/installation">http://www.datadroidlib.com/installation</a>.
 *
 * @author Foxykeep
 */
public abstract class RequestManager {

    private static final String TAG = RequestManager.class.getSimpleName();

    /**
     * Clients may implements this interface to be notified when a request is finished.
     *
     * @author Foxykeep
     */
    public static interface RequestListener extends EventListener {

        /**
         * Event fired when a request is finished.
         *
         * @param request The {@link Request} defining the request.
         * @param resultData The result of the service execution.
         */
        public void onRequestFinished(Request request, Bundle resultData);

        /**
         * Event fired when a request encountered a connection error.
         *
         * @param request The {@link Request} defining the request.
         * @param statusCode The HTTP status code returned by the server (if the request succeeded
         *            by the HTTP status code was not {@link HttpStatus#SC_OK}) or -1 if it was a
         *            connection problem
         */
        public void onRequestConnectionError(Request request, int statusCode);

        /**
         * Event fired when a request encountered a data error.
         *
         * @param request The {@link Request} defining the request.
         */
        public void onRequestDataError(Request request);

        /**
         * Event fired when a request encountered a custom error.
         *
         * @param request The {@link Request} defining the request.
         * @param resultData The result of the service execution.
         */
        public void onRequestCustomError(Request request, Bundle resultData);
    }

    public static final String RECEIVER_EXTRA_ERROR_TYPE = "com.foxykeep.datadroid.extra.error";
    public static final String RECEIVER_EXTRA_CONNECTION_ERROR_STATUS_CODE =
            "com.foxykeep.datadroid.extra.connectionErrorStatusCode";
    public static final int ERROR_TYPE_CONNEXION = 1;
    public static final int ERROR_TYPE_DATA = 2;
    public static final int ERROR_TYPE_CUSTOM = 3;

    private final Context mContext;

    private final Class<? extends RequestService> mRequestService;
    private final HashMap<Request, RequestReceiver> mRequestReceiverMap;
    private final LruCache<Request, Bundle> mMemoryCache;

    protected RequestManager(Context context, Class<? extends RequestService> requestService) {
        mContext = context.getApplicationContext();

        mRequestService = requestService;
        mRequestReceiverMap = new HashMap<Request, RequestReceiver>();
        mMemoryCache = new LruCache<Request, Bundle>(30);
    }

    /**
     * Add a {@link RequestListener} to this {@link RequestManager} to a specific {@link Request}.
     * Clients may use it in order to be notified when the corresponding request is completed.
     * <p>
     * The listener is automatically removed when the request is completed and they are notified.
     * <p>
     * <b>Warning !! </b> If it's an {@link Activity} or a {@link Fragment} that is used as a
     * listener, it must be detached when {@link Activity#onPause} is called in an {@link Activity}.
     *
     * @param listener The listener called when the Request is completed.
     * @param request The {@link Request} to listen to.
     */
    public final void addRequestListener(RequestListener listener, Request request) {
        if (listener == null) {
            return;
        }
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null.");
        }
        RequestReceiver requestReceiver = mRequestReceiverMap.get(request);
        if (requestReceiver == null) {
            DataDroidLog.w(TAG, "You tried to add a listener to a non-existing request.");
            return;
        }

        requestReceiver.addListenerHolder(new ListenerHolder(listener));
    }

    /**
     * Remove a {@link RequestListener} to this {@link RequestManager} from every {@link Request}s
     * which it is listening to.
     *
     * @param listener The listener to remove.
     */
    public final void removeRequestListener(RequestListener listener) {
        removeRequestListener(listener, null);
    }

    /**
     * Remove a {@link RequestListener} to this {@link RequestManager} from a specific
     * {@link Request}.
     *
     * @param listener The listener to remove.
     * @param request The {@link Request} associated with this listener. If null, the listener will
     *            be removed from every request it is currently associated with.
     */
    public final void removeRequestListener(RequestListener listener, Request request) {
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
    public final boolean isRequestInProgress(Request request) {
        return mRequestReceiverMap.containsKey(request);
    }

    /**
     * Call the given listener <b>synchronously</b> with the memory cached data corresponding to the
     * request.
     * <p>
     * The method called in the listener will be
     * {@link RequestListener#onRequestFinished(Request, Bundle)}.
     * <p>
     * If no cached data is found, {@link RequestListener#onRequestConnectionError(Request, int)}
     * will be called instead
     *
     * @param listener The listener to call with the data if any.
     * @param request The request associated with the memory cached data.
     */
    public final void callListenerWithCachedData(RequestListener listener, Request request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null.");
        }
        if (listener == null) {
            return;
        }

        if (request.isMemoryCacheEnabled()) {
            Bundle bundle = mMemoryCache.get(request);
            if (bundle != null) {
                listener.onRequestFinished(request, bundle);
            } else {
                listener.onRequestConnectionError(request, -1);
            }
        }
    }

    /**
     * Execute the {@link Request}.
     *
     * @param request The request to execute.
     * @param listener The listener called when the Request is completed.
     */
    public final void execute(Request request, RequestListener listener) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null.");
        }
        if (mRequestReceiverMap.containsKey(request)) {
            DataDroidLog.d(TAG,
                    "This request is already in progress. Adding the new listener to it.");

            // This exact request is already in progress. Adding the new listener.
            addRequestListener(listener, request);
            // Just check if the new request has the memory cache enabled.
            if (request.isMemoryCacheEnabled()) {
                // If true, enable it in the RequestReceiver (if it's not the case already)
                mRequestReceiverMap.get(request).enableMemoryCache();
            }
            return;
        }
        DataDroidLog.d(TAG, "Creating a new request and adding the listener to it.");

        RequestReceiver requestReceiver = new RequestReceiver(request);
        mRequestReceiverMap.put(request, requestReceiver);

        addRequestListener(listener, request);

        Intent intent = new Intent(mContext, mRequestService);
        intent.putExtra(RequestService.INTENT_EXTRA_RECEIVER, requestReceiver);
        intent.putExtra(RequestService.INTENT_EXTRA_REQUEST, request);
        mContext.startService(intent);
    }

    private final class RequestReceiver extends ResultReceiver {

        private final Request mRequest;
        private final Set<ListenerHolder> mListenerHolderSet;
        private boolean mMemoryCacheEnabled;

        /* package */ RequestReceiver(Request request) {
            super(new Handler(Looper.getMainLooper()));

            mRequest = request;
            mListenerHolderSet = Collections.synchronizedSet(new HashSet<ListenerHolder>());
            mMemoryCacheEnabled = request.isMemoryCacheEnabled();

            // Clear the old memory cache if any
            mMemoryCache.remove(request);
        }

        /* package */ void enableMemoryCache() {
            mMemoryCacheEnabled = true;
        }

        /* package */ void addListenerHolder(ListenerHolder listenerHolder) {
            synchronized (mListenerHolderSet) {
                mListenerHolderSet.add(listenerHolder);
            }
        }

        /* package */ void removeListenerHolder(ListenerHolder listenerHolder) {
            synchronized (mListenerHolderSet) {
                mListenerHolderSet.remove(listenerHolder);
            }
        }

        @Override
        public void onReceiveResult(int resultCode, Bundle resultData) {
            if (mMemoryCacheEnabled) {
                mMemoryCache.put(mRequest, resultData);
            }

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

        private final WeakReference<RequestListener> mListenerRef;
        private final int mHashCode;

        /* package */ ListenerHolder(RequestListener listener) {
            mListenerRef = new WeakReference<RequestListener>(listener);
            mHashCode = 31 + listener.hashCode();
        }

        /* package */ void onRequestFinished(Request request, int resultCode, Bundle resultData) {
            mRequestReceiverMap.remove(request);

            RequestListener listener = mListenerRef.get();
            if (listener != null) {
                if (resultCode == RequestService.ERROR_CODE) {
                    switch (resultData.getInt(RECEIVER_EXTRA_ERROR_TYPE)) {
                        case ERROR_TYPE_DATA:
                            listener.onRequestDataError(request);
                            break;
                        case ERROR_TYPE_CONNEXION:
                            int statusCode =
                                    resultData.getInt(RECEIVER_EXTRA_CONNECTION_ERROR_STATUS_CODE);
                            listener.onRequestConnectionError(request, statusCode);
                            break;
                        case ERROR_TYPE_CUSTOM:
                            listener.onRequestCustomError(request, resultData);
                            break;
                    }
                } else {
                    listener.onRequestFinished(request, resultData);
                }
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
