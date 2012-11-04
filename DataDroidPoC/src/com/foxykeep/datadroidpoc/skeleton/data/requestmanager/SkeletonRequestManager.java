/**
 * 2 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package com.foxykeep.datadroidpoc.skeleton.data.requestmanager;

import android.content.Context;

import com.foxykeep.datadroid.requestmanager.RequestManager;
import com.foxykeep.datadroidpoc.skeleton.data.service.SkeletonService;

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
        super(context, SkeletonService.class);
    }

    /**
     * Here begin the special methods.
     */

    // TODO : This is where you will add your methods which will call the service
}
