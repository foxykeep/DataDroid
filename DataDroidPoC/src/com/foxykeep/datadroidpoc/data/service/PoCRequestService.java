/**
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package com.foxykeep.datadroidpoc.data.service;

import com.foxykeep.datadroid.exception.CustomRequestException;
import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.service.RequestService;
import com.foxykeep.datadroidpoc.data.exception.MyCustomRequestException;
import com.foxykeep.datadroidpoc.data.operation.AuthenticationOperation;
import com.foxykeep.datadroidpoc.data.operation.CityList2Operation;
import com.foxykeep.datadroidpoc.data.operation.CityListOperation;
import com.foxykeep.datadroidpoc.data.operation.ComputeSquareOperation;
import com.foxykeep.datadroidpoc.data.operation.CrudSyncPhoneAddEditOperation;
import com.foxykeep.datadroidpoc.data.operation.CrudSyncPhoneDeleteOperation;
import com.foxykeep.datadroidpoc.data.operation.CrudSyncPhoneListOperation;
import com.foxykeep.datadroidpoc.data.operation.CustomRequestExceptionOperation;
import com.foxykeep.datadroidpoc.data.operation.PersonListOperation;
import com.foxykeep.datadroidpoc.data.operation.RssFeedOperation;
import com.foxykeep.datadroidpoc.data.requestmanager.PoCRequestFactory;
import com.foxykeep.datadroidpoc.data.requestmanager.PoCRequestManager;

import android.content.Intent;
import android.os.Bundle;

/**
 * This class is called by the {@link PoCRequestManager} through the {@link Intent} system.
 *
 * @author Foxykeep
 */
public final class PoCRequestService extends RequestService {

    @Override
    protected int getMaximumNumberOfThreads() {
        return 3;
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
            case PoCRequestFactory.REQUEST_TYPE_COMPUTE_SQUARE:
                return new ComputeSquareOperation();
            case PoCRequestFactory.REQUEST_TYPE_AUTHENTICATION:
                return new AuthenticationOperation();
            case PoCRequestFactory.REQUEST_TYPE_CUSTOM_REQUEST_EXCEPTION:
                return new CustomRequestExceptionOperation();
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

    @Override
    protected Bundle onCustomRequestException(Request request, CustomRequestException exception) {
        if (exception instanceof MyCustomRequestException) {
            Bundle bundle = new Bundle();
            bundle.putString(PoCRequestFactory.BUNDLE_EXTRA_ERROR_MESSAGE,
                    "MyCustomRequestException thrown.");
            return bundle;
        }
        return super.onCustomRequestException(request, exception);
    }
}
