/**
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package com.foxykeep.datadroidpoc.skeleton.data.service;

import com.foxykeep.datadroid.service.RequestService;
import com.foxykeep.datadroidpoc.skeleton.data.requestmanager.SkeletonRequestManager;

import android.content.Intent;

/**
 * This class is called by the {@link SkeletonRequestManager} through the {@link Intent} system.
 *
 * @author Foxykeep
 */
public final class SkeletonService extends RequestService {

    // TODO by default only one concurrent worker thread will be used. If you want to change that,
    // override the getMaximumNumberOfThreads() method

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
