/**
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package com.foxykeep.datadroidpoc.data.requestmanager;

import android.content.Context;

import com.foxykeep.datadroid.requestmanager.RequestManager;
import com.foxykeep.datadroidpoc.data.service.PoCService;

/**
 * This class is used as a proxy to call the Service. It provides easy-to-use methods to call the
 * service and manages the Intent creation. It also assures that a request will not be sent again if
 * an exactly identical one is already in progress.
 *
 * @author Foxykeep
 */
public final class PoCRequestManager extends RequestManager {

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

    private PoCRequestManager(final Context context) {
        super(context, PoCService.class);
    }
}
