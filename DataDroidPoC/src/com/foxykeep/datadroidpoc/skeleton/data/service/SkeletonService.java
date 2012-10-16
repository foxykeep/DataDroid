/**
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package com.foxykeep.datadroidpoc.skeleton.data.service;

import android.content.Intent;
import android.util.Log;

import com.foxykeep.datadroid.network.NetworkConnection;
import com.foxykeep.datadroid.service.WorkerService;
import com.foxykeep.datadroidpoc.skeleton.data.requestmanager.SkeletonRequestManager;

/**
 * This class is called by the {@link SkeletonRequestManager} through the {@link Intent} system. Get
 * the parameters stored in the {@link Intent} and call the right Worker.
 * 
 * @author Foxykeep
 */
public final class SkeletonService extends WorkerService {

    private static final String LOG_TAG = SkeletonService.class.getSimpleName();

    // TODO : Set the number of thread
    // Max number of parallel threads used.
    private static final int MAX_THREADS = 3;

    // TODO : Set a numeric constant for each worker (to distinguish them). These constants will be
    // sent in the Intent in order to see which worker to call

    // TODO : Set a string constants for each param to send to the worker. You should use these
    // constants in the Intent as extra name

    public SkeletonService() {
        super(MAX_THREADS);
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        // This line will generate the Android User Agent which will be used in your webservice
        // calls if you don't specify a special one.
        NetworkConnection.generateDefaultUserAgent(this);

        final int workerType = intent.getIntExtra(INTENT_EXTRA_WORKER_TYPE, -1);

        try {
            switch (workerType) {
            // TODO : Add a case per worker where you do the following things :
            // - get the parameters for this worker (if any)
            // - either call a private method if it is a short work and create the Bundle to return
            // (if any)
            // - or create the worker and start the worker and get the returned Bundle (if any)
            // - call sendSuccess() with the received Intent and the Bundle (if any)
            // See the PoC if you need more information.
                default:
                    Log.e(LOG_TAG, "This worker type is not implemented");
                    sendFailure(intent, null);
                    break;
            }
            // This block (which should be the last one in your implementation) will catch all the
            // RuntimeException and send you back an error that you can manage. If you remove this
            // catch, the RuntimeException will still crash the Service but you will not be informed
            // (as it is in 'background') so you should never remove this catch.
        } catch (final RuntimeException e) {
            Log.e(LOG_TAG, "RuntimeException", e);
            sendDataFailure(intent, null);
            // TODO normally you should replace this catch block with Exception by specialized
            // blocks which use the methods sendConnexionFailure or sendDataFailure
        } catch (final Exception e) {
            Log.e(LOG_TAG, "Erreur", e);
            sendFailure(intent, null);
        }
    }
}
