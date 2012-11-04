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
import com.foxykeep.datadroidpoc.data.worker.CrudSyncPhoneAddEditOperation;
import com.foxykeep.datadroidpoc.data.worker.CrudSyncPhoneDeleteOperation;
import com.foxykeep.datadroidpoc.data.worker.CrudSyncPhoneListOperation;
import com.foxykeep.datadroidpoc.data.worker.PersonListOperation;
import com.foxykeep.datadroidpoc.data.worker.RssFeedOperation;

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
    public static final int WORKER_TYPE_CRUD_SYNC_PHONE_ADD_EDIT = 12;

    public static final int WORKER_TYPE_RSS_FEED = 20;

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
                return new CrudSyncPhoneListOperation();
            case WORKER_TYPE_CRUD_SYNC_PHONE_DELETE:
                return new CrudSyncPhoneDeleteOperation();
            case WORKER_TYPE_CRUD_SYNC_PHONE_ADD_EDIT:
                return new CrudSyncPhoneAddEditOperation();
            case WORKER_TYPE_RSS_FEED:
                return new RssFeedOperation();
        }
        return null;
    }
}
