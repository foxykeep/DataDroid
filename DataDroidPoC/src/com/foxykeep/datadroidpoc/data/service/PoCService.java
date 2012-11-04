/**
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package com.foxykeep.datadroidpoc.data.service;

import android.content.Intent;

import com.foxykeep.datadroid.service.RequestService;
import com.foxykeep.datadroidpoc.data.requestmanager.PoCRequestManager;
import com.foxykeep.datadroidpoc.data.worker.CityListOperation;
import com.foxykeep.datadroidpoc.data.worker.PersonListOperation;

/**
 * This class is called by the {@link PoCRequestManager} through the {@link Intent} system.
 *
 * @author Foxykeep
 */
public final class PoCService extends RequestService {

    // Max number of parallel threads used
    private static final int MAX_THREADS = 3;

    // Worker types
    public static final int WORKER_TYPE_PERSON_LIST = 0;
    public static final int WORKER_TYPE_CITY_LIST = 1;

    public static final int WORKER_TYPE_CRUD_SYNC_PHONE_LIST = 10;
    public static final int WORKER_TYPE_CRUD_SYNC_PHONE_DELETE = 11;
    public static final int WORKER_TYPE_CRUD_SYNC_PHONE_ADD = 12;
    public static final int WORKER_TYPE_CRUD_SYNC_PHONE_EDIT = 13;

    public static final int WORKER_TYPE_RSS_FEED = 20;

    // Worker params.
    // - PersonList WS params.
    // - CrudSyncPhoneList WS params.
    public static final String INTENT_EXTRA_CRUD_SYNC_PHONE_LIST_USER_ID = "com.foxykeep.datadroidpoc.extras.crudPhoneListUserId";
    // - CrudSyncPhoneDelete WS params.
    public static final String INTENT_EXTRA_CRUD_SYNC_PHONE_DELETE_USER_ID = "com.foxykeep.datadroidpoc.extras.crudPhoneDeleteUserId";
    public static final String INTENT_EXTRA_CRUD_SYNC_PHONE_DELETE_PHONE_ID_LIST = "com.foxykeep.datadroidpoc.extras.crudPhoneDeletePhoneIdList";
    // - CrudSyncPhoneAdd WS params.
    public static final String INTENT_EXTRA_CRUD_SYNC_PHONE_ADD_USER_ID = "com.foxykeep.datadroidpoc.extras.crudPhoneAddUserId";
    public static final String INTENT_EXTRA_CRUD_SYNC_PHONE_ADD_NAME = "com.foxykeep.datadroidpoc.extras.crudPhoneAddName";
    public static final String INTENT_EXTRA_CRUD_SYNC_PHONE_ADD_MANUFACTURER = "com.foxykeep.datadroidpoc.extras.crudPhoneAddManufacturer";
    public static final String INTENT_EXTRA_CRUD_SYNC_PHONE_ADD_ANDROID_VERSION = "com.foxykeep.datadroidpoc.extras.crudPhoneAddAndroidVersion";
    public static final String INTENT_EXTRA_CRUD_SYNC_PHONE_ADD_SCREEN_SIZE = "com.foxykeep.datadroidpoc.extras.crudPhoneAddScreenSize";
    public static final String INTENT_EXTRA_CRUD_SYNC_PHONE_ADD_PRICE = "com.foxykeep.datadroidpoc.extras.crudPhoneAddPrice";
    // - CrudSyncPhoneEdit WS params.
    public static final String INTENT_EXTRA_CRUD_SYNC_PHONE_EDIT_USER_ID = "com.foxykeep.datadroidpoc.extras.crudPhoneEditUserId";
    public static final String INTENT_EXTRA_CRUD_SYNC_PHONE_EDIT_ID = "com.foxykeep.datadroidpoc.extras.crudPhoneEditId";
    public static final String INTENT_EXTRA_CRUD_SYNC_PHONE_EDIT_NAME = "com.foxykeep.datadroidpoc.extras.crudPhoneEditName";
    public static final String INTENT_EXTRA_CRUD_SYNC_PHONE_EDIT_MANUFACTURER = "com.foxykeep.datadroidpoc.extras.crudPhoneEditManufacturer";
    public static final String INTENT_EXTRA_CRUD_SYNC_PHONE_EDIT_ANDROID_VERSION = "com.foxykeep.datadroidpoc.extras.crudPhoneEditAndroidVersion";
    public static final String INTENT_EXTRA_CRUD_SYNC_PHONE_EDIT_SCREEN_SIZE = "com.foxykeep.datadroidpoc.extras.crudPhoneEditScreenSize";
    public static final String INTENT_EXTRA_CRUD_SYNC_PHONE_EDIT_PRICE = "com.foxykeep.datadroidpoc.extras.crudPhoneEditPrice";
    // - RssFeed WS params.
    public static final String INTENT_EXTRA_RSS_FEED_URL = "com.foxykeep.datadroidpoc.extras.rssFeedUrl";

    public PoCService() {
        super(MAX_THREADS);
    }

    @Override
    public Operation getOperationForType(int requestType) {
        switch (requestType) {
            case WORKER_TYPE_PERSON_LIST:
                return new PersonListOperation(this);
            case WORKER_TYPE_CITY_LIST:
                return new CityListOperation();
            case WORKER_TYPE_CRUD_SYNC_PHONE_LIST:
            case WORKER_TYPE_CRUD_SYNC_PHONE_DELETE:
            case WORKER_TYPE_CRUD_SYNC_PHONE_ADD:
            case WORKER_TYPE_CRUD_SYNC_PHONE_EDIT:
            case WORKER_TYPE_RSS_FEED:
        }
        return null;
    }
}
