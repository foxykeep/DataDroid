/**
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package com.foxykeep.datadroidpoc.data.requestmanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.SparseArray;

import com.foxykeep.datadroid.requestmanager.RequestManager;
import com.foxykeep.datadroidpoc.data.memprovider.MemoryProvider;
import com.foxykeep.datadroidpoc.data.service.PoCService;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.Random;

/**
 * This class is used as a proxy to call the Service. It provides easy-to-use methods to call the
 * service and manages the Intent creation. It also assures that a request will not be sent again if
 * an exactly identical one is already in progress.
 * 
 * @author Foxykeep
 */
public final class PoCRequestManager extends RequestManager {

    private static final int MAX_RANDOM_REQUEST_ID = 1000000;

    // Singleton management
    private static PoCRequestManager sInstance;

    public static PoCRequestManager from(final Context context) {
        if (sInstance == null) {
            sInstance = new PoCRequestManager(context);
        }

        return sInstance;
    }

    public static final String RECEIVER_EXTRA_CITY_LIST = "com.foxykeep.datadroidpoc.extras.cityList";
    public static final String RECEIVER_EXTRA_PHONE_LIST = "com.foxykeep.datadroidpoc.extras.phoneList";
    public static final String RECEIVER_EXTRA_PHONE_DELETE_DATA = "com.foxykeep.datadroidpoc.extras.phoneDeleteData";
    public static final String RECEIVER_EXTRA_PHONE_ADD_EDIT_DATA = "com.foxykeep.datadroidpoc.extras.phoneAddEditData";
    public static final String RECEIVER_EXTRA_RSS_FEED_DATA = "com.foxykeep.datadroidpoc.extras.rssFeed";

    private static Random sRandom = new Random();

    private SparseArray<Intent> mRequestSparseArray;
    private Context mContext;
    private ArrayList<WeakReference<OnRequestFinishedListener>> mListenerList;
    private Handler mHandler = new Handler();
    private EvalReceiver mEvalReceiver = new EvalReceiver(mHandler);
    private MemoryProvider mMemoryProvider = MemoryProvider.getInstance();

    private PoCRequestManager(final Context context) {
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
         * @param resultCode The result code (0 if there was no error).
         * @param payload The result of the service execution.
         */
        public void onRequestFinished(int requestId, int resultCode, Bundle payload);
    }

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

    /**
     * This method is call whenever a request is finished. Call all the available listeners to let
     * them know about the finished request.
     * 
     * @param resultCode The result code of the request.
     * @param resultData The bundle sent back by the service.
     */
    protected void handleResult(final int resultCode, final Bundle resultData) {

        // Get the request Id
        final int requestId = resultData.getInt(RECEIVER_EXTRA_REQUEST_ID);

        if (resultCode == PoCService.SUCCESS_CODE) {
            final Intent intent = mRequestSparseArray.get(requestId);
            switch (intent.getIntExtra(PoCService.INTENT_EXTRA_WORKER_TYPE, -1)) {
                case PoCService.WORKER_TYPE_CITY_LIST:
                    mMemoryProvider.cityList = resultData
                            .getParcelableArrayList(RECEIVER_EXTRA_CITY_LIST);
                    break;
                case PoCService.WORKER_TYPE_CRUD_SYNC_PHONE_LIST:
                    mMemoryProvider.syncPhoneList = resultData
                            .getParcelableArrayList(RECEIVER_EXTRA_PHONE_LIST);
                    break;
                case PoCService.WORKER_TYPE_CRUD_SYNC_PHONE_DELETE:
                    mMemoryProvider.syncPhoneDeleteData = resultData
                            .getLongArray(RECEIVER_EXTRA_PHONE_DELETE_DATA);
                    break;
                case PoCService.WORKER_TYPE_CRUD_SYNC_PHONE_ADD:
                    mMemoryProvider.syncPhoneAddedPhone = resultData
                            .getParcelable(RECEIVER_EXTRA_PHONE_ADD_EDIT_DATA);
                    break;
                case PoCService.WORKER_TYPE_CRUD_SYNC_PHONE_EDIT:
                    mMemoryProvider.syncPhoneEditedPhone = resultData
                            .getParcelable(RECEIVER_EXTRA_PHONE_ADD_EDIT_DATA);
                    break;
                case PoCService.WORKER_TYPE_RSS_FEED:
                    mMemoryProvider.rssFeed = resultData
                            .getParcelable(RECEIVER_EXTRA_RSS_FEED_DATA);
            }
        }

        // Remove the request Id from the "in progress" request list
        mRequestSparseArray.remove(requestId);

        // Call the available listeners
        synchronized (mListenerList) {
            for (int i = 0; i < mListenerList.size(); i++) {
                final WeakReference<OnRequestFinishedListener> weakRef = mListenerList.get(i);
                final OnRequestFinishedListener listener = weakRef.get();
                if (listener != null) {
                    listener.onRequestFinished(requestId, resultCode, resultData);
                } else {
                    mListenerList.remove(i);
                    i--;
                }
            }
        }
    }

    /**
     * Gets the list of persons and save it in the database.
     * 
     * @param returnFormat 0 for XML, 1 for JSON.
     * @return The request Id.
     */
    public int getPersonList(final int returnFormat) {

        // Check if a match to this request is already launched
        final int requestSparseArrayLength = mRequestSparseArray.size();
        for (int i = 0; i < requestSparseArrayLength; i++) {
            final Intent savedIntent = mRequestSparseArray.valueAt(i);

            if (savedIntent.getIntExtra(PoCService.INTENT_EXTRA_WORKER_TYPE, -1) != PoCService.WORKER_TYPE_PERSON_LIST) {
                continue;
            }
            if (savedIntent.getIntExtra(PoCService.INTENT_EXTRA_PERSON_LIST_RETURN_FORMAT, -1) != returnFormat) {
                continue;
            }
            return mRequestSparseArray.keyAt(i);
        }

        final int requestId = sRandom.nextInt(MAX_RANDOM_REQUEST_ID);

        final Intent intent = new Intent(mContext, PoCService.class);
        intent.putExtra(PoCService.INTENT_EXTRA_WORKER_TYPE, PoCService.WORKER_TYPE_PERSON_LIST);
        intent.putExtra(PoCService.INTENT_EXTRA_RECEIVER, mEvalReceiver);
        intent.putExtra(PoCService.INTENT_EXTRA_REQUEST_ID, requestId);
        intent.putExtra(PoCService.INTENT_EXTRA_PERSON_LIST_RETURN_FORMAT, returnFormat);
        mContext.startService(intent);

        mRequestSparseArray.append(requestId, intent);

        return requestId;
    }

    /**
     * Gets the list of cities and save it in the memory provider.
     * 
     * @return The request Id.
     */
    public int getCityList() {

        // Check if a match to this request is already launched
        final int requestSparseArrayLength = mRequestSparseArray.size();
        for (int i = 0; i < requestSparseArrayLength; i++) {
            final Intent savedIntent = mRequestSparseArray.valueAt(i);

            if (savedIntent.getIntExtra(PoCService.INTENT_EXTRA_WORKER_TYPE, -1) != PoCService.WORKER_TYPE_CITY_LIST) {
                continue;
            }
            return mRequestSparseArray.keyAt(i);
        }

        final int requestId = sRandom.nextInt(MAX_RANDOM_REQUEST_ID);

        final Intent intent = new Intent(mContext, PoCService.class);
        intent.putExtra(PoCService.INTENT_EXTRA_WORKER_TYPE, PoCService.WORKER_TYPE_CITY_LIST);
        intent.putExtra(PoCService.INTENT_EXTRA_RECEIVER, mEvalReceiver);
        intent.putExtra(PoCService.INTENT_EXTRA_REQUEST_ID, requestId);
        mContext.startService(intent);

        mRequestSparseArray.append(requestId, intent);

        // Reset the cityList in the provider
        mMemoryProvider.cityList = null;

        return requestId;
    }

    /**
     * Gets the list of phones synchronously and save it in the memory.
     * 
     * @param userId the id of the user (generated by the application).
     * @return The request Id.
     */
    public int getSyncPhoneList(final String userId) {

        // Check if a match to this request is already launched.
        final int requestSparseArrayLength = mRequestSparseArray.size();
        for (int i = 0; i < requestSparseArrayLength; i++) {
            final Intent savedIntent = mRequestSparseArray.valueAt(i);

            if (savedIntent.getIntExtra(PoCService.INTENT_EXTRA_WORKER_TYPE, -1) != PoCService.WORKER_TYPE_CRUD_SYNC_PHONE_LIST) {
                continue;
            }
            if (!savedIntent.getStringExtra(PoCService.INTENT_EXTRA_CRUD_SYNC_PHONE_LIST_USER_ID)
                    .equals(userId)) {
                continue;
            }
            return mRequestSparseArray.keyAt(i);
        }

        final int requestId = sRandom.nextInt(MAX_RANDOM_REQUEST_ID);

        final Intent intent = new Intent(mContext, PoCService.class);
        intent.putExtra(PoCService.INTENT_EXTRA_WORKER_TYPE,
                PoCService.WORKER_TYPE_CRUD_SYNC_PHONE_LIST);
        intent.putExtra(PoCService.INTENT_EXTRA_RECEIVER, mEvalReceiver);
        intent.putExtra(PoCService.INTENT_EXTRA_REQUEST_ID, requestId);
        intent.putExtra(PoCService.INTENT_EXTRA_CRUD_SYNC_PHONE_LIST_USER_ID, userId);
        mContext.startService(intent);

        mRequestSparseArray.append(requestId, intent);

        mMemoryProvider.syncPhoneList = null;

        return requestId;
    }

    /**
     * Delete a phone synchronously.
     * 
     * @param userId the id of the user (generated by the application).
     * @param phoneIdList the list of phone ids to delete (comma separated).
     * @return The request Id.
     */
    public int deleteSyncPhones(final String userId, final String phoneIdList) {

        // Check if a match to this request is already launched.
        final int requestSparseArrayLength = mRequestSparseArray.size();
        for (int i = 0; i < requestSparseArrayLength; i++) {
            final Intent savedIntent = mRequestSparseArray.valueAt(i);

            if (savedIntent.getIntExtra(PoCService.INTENT_EXTRA_WORKER_TYPE, -1) != PoCService.WORKER_TYPE_CRUD_SYNC_PHONE_DELETE) {
                continue;
            }
            if (!savedIntent.getStringExtra(PoCService.INTENT_EXTRA_CRUD_SYNC_PHONE_DELETE_USER_ID)
                    .equals(userId)) {
                continue;
            }
            if (!savedIntent.getStringExtra(
                    PoCService.INTENT_EXTRA_CRUD_SYNC_PHONE_DELETE_PHONE_ID_LIST).equals(
                    phoneIdList)) {
                continue;
            }
            return mRequestSparseArray.keyAt(i);
        }

        final int requestId = sRandom.nextInt(MAX_RANDOM_REQUEST_ID);

        final Intent intent = new Intent(mContext, PoCService.class);
        intent.putExtra(PoCService.INTENT_EXTRA_WORKER_TYPE,
                PoCService.WORKER_TYPE_CRUD_SYNC_PHONE_DELETE);
        intent.putExtra(PoCService.INTENT_EXTRA_RECEIVER, mEvalReceiver);
        intent.putExtra(PoCService.INTENT_EXTRA_REQUEST_ID, requestId);
        intent.putExtra(PoCService.INTENT_EXTRA_CRUD_SYNC_PHONE_DELETE_USER_ID, userId);
        intent.putExtra(PoCService.INTENT_EXTRA_CRUD_SYNC_PHONE_DELETE_PHONE_ID_LIST, phoneIdList);
        mContext.startService(intent);

        mRequestSparseArray.append(requestId, intent);

        mMemoryProvider.syncPhoneDeleteData = null;

        return requestId;
    }

    /**
     * Add a phone synchronously.
     * 
     * @param userId the id of the user (generated by the application).
     * @param name the phone name.
     * @param manufacturer the phone manufacturer.
     * @param androidVersion the phone android version.
     * @param screenSize the phone screen size.
     * @param price the phone price.
     * @return The request Id.
     */
    public int addSyncPhone(final String userId, final String name, final String manufacturer,
            final String androidVersion, final double screenSize,
            final int price) {

        // Check if a match to this request is already launched.
        final int requestSparseArrayLength = mRequestSparseArray.size();
        for (int i = 0; i < requestSparseArrayLength; i++) {
            final Intent savedIntent = mRequestSparseArray.valueAt(i);

            if (savedIntent.getIntExtra(PoCService.INTENT_EXTRA_WORKER_TYPE, -1) != PoCService.WORKER_TYPE_CRUD_SYNC_PHONE_ADD) {
                continue;
            }
            if (!savedIntent.getStringExtra(PoCService.INTENT_EXTRA_CRUD_SYNC_PHONE_ADD_USER_ID)
                    .equals(userId)) {
                continue;
            }
            if (!savedIntent.getStringExtra(PoCService.INTENT_EXTRA_CRUD_SYNC_PHONE_ADD_NAME)
                    .equals(name)) {
                continue;
            }
            if (!savedIntent.getStringExtra(
                    PoCService.INTENT_EXTRA_CRUD_SYNC_PHONE_ADD_MANUFACTURER).equals(manufacturer)) {
                continue;
            }
            if (!savedIntent.getStringExtra(
                    PoCService.INTENT_EXTRA_CRUD_SYNC_PHONE_ADD_ANDROID_VERSION).equals(
                    androidVersion)) {
                continue;
            }
            if (savedIntent.getDoubleExtra(PoCService.INTENT_EXTRA_CRUD_SYNC_PHONE_ADD_SCREEN_SIZE,
                    -1) != screenSize) {
                continue;
            }
            if (savedIntent.getIntExtra(PoCService.INTENT_EXTRA_CRUD_SYNC_PHONE_ADD_PRICE, -1) != price) {
                continue;
            }
            return mRequestSparseArray.keyAt(i);
        }

        final int requestId = sRandom.nextInt(MAX_RANDOM_REQUEST_ID);

        final Intent intent = new Intent(mContext, PoCService.class);
        intent.putExtra(PoCService.INTENT_EXTRA_WORKER_TYPE,
                PoCService.WORKER_TYPE_CRUD_SYNC_PHONE_ADD);
        intent.putExtra(PoCService.INTENT_EXTRA_RECEIVER, mEvalReceiver);
        intent.putExtra(PoCService.INTENT_EXTRA_REQUEST_ID, requestId);
        intent.putExtra(PoCService.INTENT_EXTRA_CRUD_SYNC_PHONE_ADD_USER_ID, userId);
        intent.putExtra(PoCService.INTENT_EXTRA_CRUD_SYNC_PHONE_ADD_NAME, name);
        intent.putExtra(PoCService.INTENT_EXTRA_CRUD_SYNC_PHONE_ADD_MANUFACTURER, manufacturer);
        intent.putExtra(PoCService.INTENT_EXTRA_CRUD_SYNC_PHONE_ADD_ANDROID_VERSION, androidVersion);
        intent.putExtra(PoCService.INTENT_EXTRA_CRUD_SYNC_PHONE_ADD_SCREEN_SIZE, screenSize);
        intent.putExtra(PoCService.INTENT_EXTRA_CRUD_SYNC_PHONE_ADD_PRICE, price);
        mContext.startService(intent);

        mRequestSparseArray.append(requestId, intent);

        mMemoryProvider.syncPhoneAddedPhone = null;

        return requestId;
    }

    /**
     * Edit a phone synchronously.
     * 
     * @param userId the id of the user (generated by the application).
     * @param serverId the phone id.
     * @param name the phone new name.
     * @param manufacturer the phone new manufacturer.
     * @param androidVersion the phone new android version.
     * @param screenSize the phone new screen size.
     * @param price the phone new price.
     * @return The request Id.
     */
    public int editSyncPhone(final String userId, final long serverId, final String name,
            final String manufacturer, final String androidVersion,
            final double screenSize, final int price) {

        // Check if a match to this request is already launched.
        final int requestSparseArrayLength = mRequestSparseArray.size();
        for (int i = 0; i < requestSparseArrayLength; i++) {
            final Intent savedIntent = mRequestSparseArray.valueAt(i);

            if (savedIntent.getIntExtra(PoCService.INTENT_EXTRA_WORKER_TYPE, -1) != PoCService.WORKER_TYPE_CRUD_SYNC_PHONE_EDIT) {
                continue;
            }
            if (!savedIntent.getStringExtra(PoCService.INTENT_EXTRA_CRUD_SYNC_PHONE_EDIT_USER_ID)
                    .equals(userId)) {
                continue;
            }
            if (!savedIntent.getStringExtra(PoCService.INTENT_EXTRA_CRUD_SYNC_PHONE_EDIT_NAME)
                    .equals(name)) {
                continue;
            }
            if (!savedIntent.getStringExtra(
                    PoCService.INTENT_EXTRA_CRUD_SYNC_PHONE_EDIT_MANUFACTURER).equals(manufacturer)) {
                continue;
            }
            if (!savedIntent.getStringExtra(
                    PoCService.INTENT_EXTRA_CRUD_SYNC_PHONE_EDIT_ANDROID_VERSION).equals(
                    androidVersion)) {
                continue;
            }
            if (savedIntent.getDoubleExtra(
                    PoCService.INTENT_EXTRA_CRUD_SYNC_PHONE_EDIT_SCREEN_SIZE, -1) != screenSize) {
                continue;
            }
            if (savedIntent.getIntExtra(PoCService.INTENT_EXTRA_CRUD_SYNC_PHONE_EDIT_PRICE, -1) != price) {
                continue;
            }
            return mRequestSparseArray.keyAt(i);
        }

        final int requestId = sRandom.nextInt(MAX_RANDOM_REQUEST_ID);

        final Intent intent = new Intent(mContext, PoCService.class);
        intent.putExtra(PoCService.INTENT_EXTRA_WORKER_TYPE,
                PoCService.WORKER_TYPE_CRUD_SYNC_PHONE_EDIT);
        intent.putExtra(PoCService.INTENT_EXTRA_RECEIVER, mEvalReceiver);
        intent.putExtra(PoCService.INTENT_EXTRA_REQUEST_ID, requestId);
        intent.putExtra(PoCService.INTENT_EXTRA_CRUD_SYNC_PHONE_EDIT_USER_ID, userId);
        intent.putExtra(PoCService.INTENT_EXTRA_CRUD_SYNC_PHONE_EDIT_ID, serverId);
        intent.putExtra(PoCService.INTENT_EXTRA_CRUD_SYNC_PHONE_EDIT_NAME, name);
        intent.putExtra(PoCService.INTENT_EXTRA_CRUD_SYNC_PHONE_EDIT_MANUFACTURER, manufacturer);
        intent.putExtra(PoCService.INTENT_EXTRA_CRUD_SYNC_PHONE_EDIT_ANDROID_VERSION,
                androidVersion);
        intent.putExtra(PoCService.INTENT_EXTRA_CRUD_SYNC_PHONE_EDIT_SCREEN_SIZE, screenSize);
        intent.putExtra(PoCService.INTENT_EXTRA_CRUD_SYNC_PHONE_EDIT_PRICE, price);
        mContext.startService(intent);

        mRequestSparseArray.append(requestId, intent);

        mMemoryProvider.syncPhoneEditedPhone = null;

        return requestId;
    }

    /**
     * Gets the RSS feed of the given url and save it in the memory.
     * 
     * @param feedUrl the url of the RSS feed.
     * @return The request Id.
     */
    public int getRssFeed(final String feedUrl) {

        // Check if a match to this request is already launched.
        final int requestSparseArrayLength = mRequestSparseArray.size();
        for (int i = 0; i < requestSparseArrayLength; i++) {
            final Intent savedIntent = mRequestSparseArray.valueAt(i);

            if (savedIntent.getIntExtra(PoCService.INTENT_EXTRA_WORKER_TYPE, -1) != PoCService.WORKER_TYPE_RSS_FEED) {
                continue;
            }
            if (!savedIntent.getStringExtra(PoCService.INTENT_EXTRA_RSS_FEED_URL).equals(feedUrl)) {
                continue;
            }
            return mRequestSparseArray.keyAt(i);
        }

        final int requestId = sRandom.nextInt(MAX_RANDOM_REQUEST_ID);

        final Intent intent = new Intent(mContext, PoCService.class);
        intent.putExtra(PoCService.INTENT_EXTRA_WORKER_TYPE, PoCService.WORKER_TYPE_RSS_FEED);
        intent.putExtra(PoCService.INTENT_EXTRA_RECEIVER, mEvalReceiver);
        intent.putExtra(PoCService.INTENT_EXTRA_REQUEST_ID, requestId);
        intent.putExtra(PoCService.INTENT_EXTRA_RSS_FEED_URL, feedUrl);
        mContext.startService(intent);

        mRequestSparseArray.append(requestId, intent);

        mMemoryProvider.rssFeed = null;

        return requestId;
    }
}
