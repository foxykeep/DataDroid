/**
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package com.foxykeep.datadroidpoc.skeleton.data.service;

import android.content.Intent;

import com.foxykeep.datadroid.service.RequestService;
import com.foxykeep.datadroidpoc.skeleton.data.requestmanager.SkeletonRequestManager;

/**
 * This class is called by the {@link SkeletonRequestManager} through the {@link Intent} system.
 *
 * @author Foxykeep
 */
public final class SkeletonService extends RequestService {

    // TODO : Set the number of thread
    // Max number of parallel threads used.
    private static final int MAX_THREADS = 3;

    // TODO : Set a numeric constant for each worker (to distinguish them). These constants will be
    // sent in the Intent in order to see which worker to call

    public SkeletonService() {
        super(MAX_THREADS);
    }

    @Override
    public Operation getOperationForType(int requestType) {
        switch (requestType) {
            // TODO : Add a case per worker where you do the following things :
            // - create the corresponding Operation and return it
            // See the PoC if you need more information.
        }
        return null;
    }
}
