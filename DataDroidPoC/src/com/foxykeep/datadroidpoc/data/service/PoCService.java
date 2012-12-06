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
import com.foxykeep.datadroidpoc.data.operation.AuthenticationOperation;
import com.foxykeep.datadroidpoc.data.operation.CityList2Operation;
import com.foxykeep.datadroidpoc.data.operation.CityListOperation;
import com.foxykeep.datadroidpoc.data.operation.CrudSyncPhoneAddEditOperation;
import com.foxykeep.datadroidpoc.data.operation.CrudSyncPhoneDeleteOperation;
import com.foxykeep.datadroidpoc.data.operation.CrudSyncPhoneListOperation;
import com.foxykeep.datadroidpoc.data.operation.PersonListOperation;
import com.foxykeep.datadroidpoc.data.operation.RssFeedOperation;
import com.foxykeep.datadroidpoc.data.requestmanager.PoCRequestFactory;
import com.foxykeep.datadroidpoc.data.requestmanager.PoCRequestManager;

/**
 * This class is called by the {@link PoCRequestManager} through the {@link Intent} system.
 *
 * @author Foxykeep
 */
public final class PoCService extends RequestService {

    // Max number of parallel threads used
    private static final int MAX_THREADS = 3;

    public PoCService() {
        super(MAX_THREADS);
    }

    @Override
    public Operation getOperationForType(int requestType) {
        switch (requestType) {
            case PoCRequestFactory.REQUEST_TYPE_PERSON_LIST:
                return new PersonListOperation();
            case PoCRequestFactory.REQUEST_TYPE_CITY_LIST:
                return new CityListOperation();
            case PoCRequestFactory.REQUEST_TYPE_CITY_LIST_2:
                return new CityList2Operation();
            case PoCRequestFactory.REQUEST_TYPE_AUTHENTICATION:
                return new AuthenticationOperation();
            case PoCRequestFactory.REQUEST_TYPE_CRUD_SYNC_PHONE_LIST:
                return new CrudSyncPhoneListOperation();
            case PoCRequestFactory.REQUEST_TYPE_CRUD_SYNC_PHONE_DELETE:
                return new CrudSyncPhoneDeleteOperation();
            case PoCRequestFactory.REQUEST_TYPE_CRUD_SYNC_PHONE_ADD:
            case PoCRequestFactory.REQUEST_TYPE_CRUD_SYNC_PHONE_EDIT:
                return new CrudSyncPhoneAddEditOperation();
            case PoCRequestFactory.REQUEST_TYPE_RSS_FEED:
                return new RssFeedOperation();
        }
        return null;
    }
}
