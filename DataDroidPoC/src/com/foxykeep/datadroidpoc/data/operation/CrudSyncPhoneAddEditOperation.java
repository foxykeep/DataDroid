/**
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package com.foxykeep.datadroidpoc.data.operation;

import com.foxykeep.datadroid.exception.ConnectionException;
import com.foxykeep.datadroid.exception.DataException;
import com.foxykeep.datadroid.network.NetworkConnection;
import com.foxykeep.datadroid.network.NetworkConnection.ConnectionResult;
import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.service.RequestService.Operation;
import com.foxykeep.datadroidpoc.config.WSConfig;
import com.foxykeep.datadroidpoc.data.factory.PhoneAddEditFactory;
import com.foxykeep.datadroidpoc.data.model.Phone;
import com.foxykeep.datadroidpoc.data.requestmanager.PoCRequestFactory;

import android.content.Context;
import android.os.Bundle;

import java.util.HashMap;

public final class CrudSyncPhoneAddEditOperation implements Operation {

    public static final String PARAM_USER_ID = "com.foxykeep.datadroidpoc.extra.userId";
    public static final String PARAM_PHONE = "com.foxykeep.datadroidpoc.extra.phone";

    @Override
    public Bundle execute(Context context, Request request) throws ConnectionException,
            DataException {
        HashMap<String, String> params = new HashMap<String, String>();
        Phone phone = (Phone) request.getParcelable(PARAM_PHONE);
        params.put(WSConfig.WS_CRUD_PHONE_ADD_EDIT_PROPERTY_USER_UDID,
                request.getString(PARAM_USER_ID));
        params.put(WSConfig.WS_CRUD_PHONE_ADD_EDIT_PROPERTY_ID, String.valueOf(phone.serverId));
        params.put(WSConfig.WS_CRUD_PHONE_ADD_EDIT_PROPERTY_NAME, phone.name);
        params.put(WSConfig.WS_CRUD_PHONE_ADD_EDIT_PROPERTY_MANUFACTURER, phone.manufacturer);
        params.put(WSConfig.WS_CRUD_PHONE_ADD_EDIT_PROPERTY_ANDROID_VERSION, phone.androidVersion);
        params.put(WSConfig.WS_CRUD_PHONE_ADD_EDIT_PROPERTY_SCREEN_SIZE,
                String.valueOf(phone.screenSize));
        params.put(WSConfig.WS_CRUD_PHONE_ADD_EDIT_PROPERTY_PRICE, String.valueOf(phone.price));

        NetworkConnection networkConnection = new NetworkConnection(context,
                WSConfig.WS_CRUD_PHONE_ADD_EDIT_URL);
        networkConnection.setParameters(params);
        ConnectionResult result = networkConnection.execute();

        Phone serverPhone = PhoneAddEditFactory.parseResult(result.body);

        Bundle bundle = new Bundle();
        bundle.putParcelable(PoCRequestFactory.BUNDLE_EXTRA_PHONE_ADD_EDIT_DATA, serverPhone);
        return bundle;
    }
}
