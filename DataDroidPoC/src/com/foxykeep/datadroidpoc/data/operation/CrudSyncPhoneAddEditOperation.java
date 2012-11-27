/**
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package com.foxykeep.datadroidpoc.data.operation;

import android.content.Context;
import android.os.Bundle;

import com.foxykeep.datadroid.exception.ConnectionException;
import com.foxykeep.datadroid.exception.DataException;
import com.foxykeep.datadroid.network.NetworkConnection.Builder;
import com.foxykeep.datadroid.network.NetworkConnection.ConnectionResult;
import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.service.RequestService.Operation;
import com.foxykeep.datadroidpoc.config.WSConfig;
import com.foxykeep.datadroidpoc.data.factory.PhoneAddEditFactory;
import com.foxykeep.datadroidpoc.data.model.Phone;
import com.foxykeep.datadroidpoc.data.requestmanager.PoCRequestFactory;

import java.util.HashMap;

public final class CrudSyncPhoneAddEditOperation implements Operation {

    public static final String PARAM_USER_ID = "com.foxykeep.datadroidpoc.extras.userId";
    public static final String PARAM_PHONE_ID = "com.foxykeep.datadroidpoc.extras.phoneId";
    public static final String PARAM_NAME = "com.foxykeep.datadroidpoc.extras.name";
    public static final String PARAM_MANUFACTURER =
            "com.foxykeep.datadroidpoc.extras.manufacturer";
    public static final String PARAM_ANDROID_VERSION =
            "com.foxykeep.datadroidpoc.extras.androidVersion";
    public static final String PARAM_SCREEN_SIZE = "com.foxykeep.datadroidpoc.extras.screenSize";
    public static final String PARAM_PRICE = "com.foxykeep.datadroidpoc.extras.price";

    @Override
    public Bundle execute(Context context, Request request) throws ConnectionException,
            DataException {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(WSConfig.WS_CRUD_PHONE_ADD_EDIT_PROPERTY_USER_UDID,
                request.getString(PARAM_USER_ID));
        params.put(WSConfig.WS_CRUD_PHONE_ADD_EDIT_PROPERTY_ID,
                String.valueOf(request.getLong(PARAM_PHONE_ID)));
        params.put(WSConfig.WS_CRUD_PHONE_ADD_EDIT_PROPERTY_NAME, request.getString(PARAM_NAME));
        params.put(WSConfig.WS_CRUD_PHONE_ADD_EDIT_PROPERTY_MANUFACTURER,
                request.getString(PARAM_MANUFACTURER));
        params.put(WSConfig.WS_CRUD_PHONE_ADD_EDIT_PROPERTY_ANDROID_VERSION,
                request.getString(PARAM_ANDROID_VERSION));
        params.put(WSConfig.WS_CRUD_PHONE_ADD_EDIT_PROPERTY_SCREEN_SIZE,
                String.valueOf(request.getDouble(PARAM_SCREEN_SIZE)));
        params.put(WSConfig.WS_CRUD_PHONE_ADD_EDIT_PROPERTY_PRICE,
                String.valueOf(request.getInt(PARAM_PRICE)));

        Builder builder = new Builder(context, WSConfig.WS_CRUD_PHONE_ADD_EDIT_URL);
        builder.setParameters(params);

        ConnectionResult result = builder.execute();

        Phone phone = PhoneAddEditFactory.parseResult(result.body);

        Bundle bundle = new Bundle();
        bundle.putParcelable(PoCRequestFactory.BUNDLE_EXTRA_PHONE_ADD_EDIT_DATA, phone);
        return bundle;
    }
}
