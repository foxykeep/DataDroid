/**
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package com.foxykeep.datadroid.requestmanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.SparseArray;

import com.foxykeep.datadroid.service.WorkerService;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.Random;

/**
 * {@link RequestManager} is the superclass of the classes that will be implemented in your project.
 * It contains constants used in the library.
 *
 * @author Foxykeep
 */
public abstract class RequestManager {

    /**
     * Clients may implements this interface to be notified when a request is finished.
     *
     * @author Foxykeep
     */
    public static interface OnRequestFinishedListener extends EventListener {

        /**
         * Event fired when a request is finished.
         *
         * @param requestId The request Id (to see if this is the right request).
         * @param resultCode The result code. Possible values : - {@link WorkerService#SUCCESS_CODE}
         *            if succeeded. - {@link WorkerService#ERROR_CODE} if there was an error.
         * @param payload The result of the service execution.
         */
        public void onRequestFinished(int requestId, int resultCode, Bundle payload);
    }

    public static final String RECEIVER_EXTRA_REQUEST_ID = "com.foxykeep.datadroid.extras.requestId";
    public static final String RECEIVER_EXTRA_RESULT_CODE = "com.foxykeep.datadroid.extras.code";
    public static final String RECEIVER_EXTRA_PAYLOAD = "com.foxykeep.datadroid.extras.payload";
    public static final String RECEIVER_EXTRA_ERROR_TYPE = "com.foxykeep.datadroid.extras.error";
    public static final int RECEIVER_EXTRA_VALUE_ERROR_TYPE_CONNEXION = 1;
    public static final int RECEIVER_EXTRA_VALUE_ERROR_TYPE_DATA = 2;

    protected static final int MAX_RANDOM_REQUEST_ID = 1000000;

    protected Random mRandom = new Random();
    protected Context mContext;

    protected SparseArray<Intent> mRequestSparseArray;
    protected ArrayList<WeakReference<OnRequestFinishedListener>> mListenerList;
    protected EvalReceiver mEvalReceiver = new EvalReceiver(new Handler());

    protected RequestManager(final Context context) {
        mContext = context.getApplicationContext();
        mRequestSparseArray = new SparseArray<Intent>();
        mListenerList = new ArrayList<WeakReference<OnRequestFinishedListener>>();
    }

    /**
     * The ResultReceiver that will receive the result from the Service.
     */
    private class EvalReceiver extends ResultReceiver {
        EvalReceiver(final Handler h) {
            super(h);
        }

        @Override
        public void onReceiveResult(final int resultCode, final Bundle resultData) {
            handleResult(resultCode, resultData);
        }
    }

    protected abstract void handleResult(final int resultCode, final Bundle resultData);

    /**
     * Add a {@link OnRequestFinishedListener} to this {@link PoCRequestManager}. Clients may use it
     * in order to listen to events fired when a request is finished..
     * <p>
     * <b>Warning !! </b> If it's an {@link Activity} that is used as a Listener, it must be
     * detached when {@link Activity#onPause} is called in an {@link Activity}.
     * </p>
     *
     * @param listener The listener to add to this {@link PoCRequestManager} .
     */
    public void addOnRequestFinishedListener(final OnRequestFinishedListener listener) {
        if (listener == null) {
            return;
        }
        synchronized (mListenerList) {
            // Check if the listener is not already in the list
            if (!mListenerList.isEmpty()) {
                for (WeakReference<OnRequestFinishedListener> weakRef : mListenerList) {
                    if (listener.equals(weakRef.get())) {
                        return;
                    }
                }
            }
            mListenerList.add(new WeakReference<OnRequestFinishedListener>(listener));
        }
    }

    /**
     * Remove a {@link OnRequestFinishedListener} to this {@link PoCRequestManager}.
     *
     * @param listenerThe listener to remove to this {@link PoCRequestManager}.
     */
    public void removeOnRequestFinishedListener(final OnRequestFinishedListener listener) {
        if (listener == null) {
            return;
        }
        synchronized (mListenerList) {
            final int listenerListSize = mListenerList.size();
            for (int i = 0; i < listenerListSize; i++) {
                if (listener.equals(mListenerList.get(i).get())) {
                    mListenerList.remove(i);
                    return;
                }
            }
        }
    }

    /**
     * Return whether a request (specified by its id) is still in progress or not.
     *
     * @param requestId The request id.
     * @return Whether the request is still in progress or not.
     */
    public boolean isRequestInProgress(final int requestId) {
        return (mRequestSparseArray.indexOfKey(requestId) >= 0);
    }
}
