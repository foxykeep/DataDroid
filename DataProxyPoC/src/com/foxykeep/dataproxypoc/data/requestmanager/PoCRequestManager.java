/*
 * 2011 Foxykeep (http://www.foxykeep.com)
 *
 * Licensed under the Beerware License :
 * 
 *   As long as you retain this notice you can do whatever you want with this stuff. If we meet some day, and you think
 *   this stuff is worth it, you can buy me a beer in return
 */
package com.foxykeep.dataproxypoc.data.requestmanager;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.SparseArray;

import com.foxykeep.dataproxy.requestmanager.RequestManager;
import com.foxykeep.dataproxypoc.data.service.PoCService;

/**
 * This class is used as a proxy to call the Service. It provides easy-to-use
 * methods to call the service and manages the Intent creation. It also assures
 * that a request will not be sent again if an exactly identical one is already
 * in progress
 * 
 * @author Foxykeep
 */
public class PoCRequestManager extends RequestManager {

    private static final int MAX_RANDOM_REQUEST_ID = 1000000;

    // Singleton management
    private static PoCRequestManager sInstance;

    public static PoCRequestManager getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new PoCRequestManager(context);
        }

        return sInstance;
    }

    private static Random sRandom = new Random();

    private SparseArray<Intent> mRequestSparseArray;
    private Context mContext;
    private ArrayList<OnRequestFinishedListener> mListenerList;
    private Handler mHandler = new Handler();
    private EvalReceiver mEvalReceiver = new EvalReceiver(mHandler);

    private PoCRequestManager(final Context context) {
        mContext = context;
        mRequestSparseArray = new SparseArray<Intent>();
        mListenerList = new ArrayList<OnRequestFinishedListener>();
    }

    /**
     * The ResultReceiver that will receive the result from the Service
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

    /**
     * Clients may implements this interface to be notified when a request is
     * finished
     * 
     * @author Foxykeep
     */
    public static interface OnRequestFinishedListener extends EventListener {

        /**
         * Event fired when a request is finished.
         * 
         * @param requestId The request Id (to see if this is the right request)
         * @param resultCode The result code (0 if there was no error)
         * @param payload The result of the service execution.
         */
        public void onRequestFinished(int requestId, int resultCode, Bundle payload);
    }

    /**
     * Add a {@link OnRequestFinishedListener} to this {@link PoCRequestManager}
     * . Clients may use it in order to listen to events fired when a request is
     * finished.
     * <p>
     * <b>Warning !! </b> If it's an {@link Activity} that is used as a
     * Listener, it must be detached when {@link Activity#onPause} is called in
     * an {@link Activity}.
     * </p>
     * 
     * @param listener The listener to add to this {@link PoCRequestManager} .
     */
    public void addOnRequestFinishedListener(final OnRequestFinishedListener listener) {
        synchronized (mListenerList) {
            if (!mListenerList.contains(listener)) {
                mListenerList.add(listener);
            }
        }
    }

    /**
     * Remove a {@link OnRequestFinishedListener} to this
     * {@link PoCRequestManager}.
     * 
     * @param listenerThe listener to remove to this {@link PoCRequestManager}.
     */
    public void removeOnRequestFinishedListener(final OnRequestFinishedListener listener) {
        synchronized (mListenerList) {
            mListenerList.remove(listener);
        }
    }

    /**
     * Return whether a request (specified by its id) is still in progress or
     * not
     * 
     * @param requestId The request id
     * @return whether the request is still in progress or not.
     */
    public boolean isRequestInProgress(final int requestId) {
        return (mRequestSparseArray.indexOfKey(requestId) >= 0);
    }

    /**
     * This method is call whenever a request is finished. Call all the
     * available listeners to let them know about the finished request
     * 
     * @param resultCode The result code of the request
     * @param resultData The bundle sent back by the service
     */
    protected void handleResult(final int resultCode, final Bundle resultData) {

        // Get the request Id
        final int requestId = resultData.getInt(RECEIVER_EXTRA_REQUEST_ID);

        // Remove the request Id from the "in progress" request list
        mRequestSparseArray.remove(requestId);

        // Call the available listeners
        synchronized (mListenerList) {
            for (OnRequestFinishedListener listener : mListenerList) {
                listener.onRequestFinished(requestId, resultCode, resultData);
            }
        }
    }

    /**
     * Gets the list of persons without specifying a minimum age
     * 
     * @param returnFormat 0 for XML, 1 for JSON
     * @return the request Id
     */
    public int getPersons(final int returnFormat) {
        return getPersons(-1, returnFormat);
    }

    /**
     * Gets the list of persons which are at least minAge years old
     * 
     * @param minAge the minimum age
     * @param returnFormat 0 for XML, 1 for JSON
     * @return the request Id
     */
    public int getPersons(final int minAge, final int returnFormat) {

        // Check if a request is already launched
        final int requestSparseArrayLength = mRequestSparseArray.size();
        for (int i = 0; i < requestSparseArrayLength; i++) {
            final Intent savedIntent = mRequestSparseArray.valueAt(i);

            if (savedIntent.getIntExtra(PoCService.INTENT_EXTRA_WORKER_TYPE, -1) != PoCService.WORKER_TYPE_PERSONS) {
                continue;
            }
            if (savedIntent.getIntExtra(PoCService.INTENT_EXTRA_PERSONS_MIN_AGE, -2) != minAge) {
                continue;
            }
            if (savedIntent.getIntExtra(PoCService.INTENT_EXTRA_PERSONS_RETURN_FORMAT, -1) != returnFormat) {
                continue;
            }
            return mRequestSparseArray.keyAt(i);
        }

        final int requestId = sRandom.nextInt(MAX_RANDOM_REQUEST_ID);

        final Intent intent = new Intent(mContext, PoCService.class);
        intent.putExtra(PoCService.INTENT_EXTRA_WORKER_TYPE, PoCService.WORKER_TYPE_PERSONS);
        intent.putExtra(PoCService.INTENT_EXTRA_RECEIVER, mEvalReceiver);
        intent.putExtra(PoCService.INTENT_EXTRA_REQUEST_ID, requestId);
        intent.putExtra(PoCService.INTENT_EXTRA_PERSONS_MIN_AGE, minAge);
        mContext.startService(intent);

        mRequestSparseArray.append(requestId, intent);

        return requestId;
    }
}
