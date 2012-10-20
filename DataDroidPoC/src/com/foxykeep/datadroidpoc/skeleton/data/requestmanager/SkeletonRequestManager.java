/**
 * 2 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package com.foxykeep.datadroidpoc.skeleton.data.requestmanager;

import android.content.Context;
import android.os.Bundle;

import com.foxykeep.datadroid.requestmanager.RequestManager;

import java.lang.ref.WeakReference;

/**
 * This class is used as a proxy to call the Service. It provides easy-to-use methods to call the
 * service and manages the Intent creation. It also assures that a request will not be sent again if
 * an exactly identical one is already in progress.
 *
 * @author Foxykeep
 */
public final class SkeletonRequestManager extends RequestManager {

    // Singleton management
    private static SkeletonRequestManager sInstance;

    public static SkeletonRequestManager from(final Context context) {
        if (sInstance == null) {
            sInstance = new SkeletonRequestManager(context);
        }

        return sInstance;
    }

    private SkeletonRequestManager(final Context context) {
        super(context);
    }

    /**
     * This method is call whenever a request is finished. Call all the available listeners to let
     * them know about the finished request.
     *
     * @param resultCode The result code of the request.
     * @param resultData The bundle sent back by the service.
     */
    @Override
    protected void handleResult(final int resultCode, final Bundle resultData) {

        // Get the request Id.
        final int requestId = resultData.getInt(RECEIVER_EXTRA_REQUEST_ID);

        // Remove the request Id from the "in progress" request list.
        mRequestSparseArray.remove(requestId);

        // Call the available listeners.
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
     * Here begin the special methods.
     */

    // TODO : This is where you will add your methods which will call the service
}
