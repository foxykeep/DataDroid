/**
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package com.foxykeep.datadroidpoc.data.worker;

import android.os.Bundle;

import com.foxykeep.datadroid.exception.RestClientException;
import com.foxykeep.datadroid.network.NetworkConnection;
import com.foxykeep.datadroid.network.NetworkConnection.NetworkConnectionResult;
import com.foxykeep.datadroidpoc.config.WSConfig;
import com.foxykeep.datadroidpoc.data.factory.PhoneAddEditFactory;
import com.foxykeep.datadroidpoc.data.model.Phone;
import com.foxykeep.datadroidpoc.data.requestmanager.PoCRequestManager;

import org.json.JSONException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

public final class CrudSyncPhoneAddEditOperation {

    private CrudSyncPhoneAddEditOperation() {
        // No public constructor
    }

    public static Bundle start(final String userId, final long serverId, final String name,
            final String manufacturer, final String androidVersion,
            final double screenSize, final int price) throws IllegalStateException, IOException,
            URISyntaxException, RestClientException,
            JSONException {

        HashMap<String, String> params = new HashMap<String, String>();
        params.put(WSConfig.WS_CRUD_PHONE_ADD_EDIT_PROPERTY_USER_UDID, userId);
        if (serverId > 0) {
            params.put(WSConfig.WS_CRUD_PHONE_ADD_EDIT_PROPERTY_ID, String.valueOf(serverId));
        } else if (serverId != -1) {
            throw new IllegalArgumentException(
                    "serverId must be equal either to a serverId (edit) or to -1 (add)");
        }
        params.put(WSConfig.WS_CRUD_PHONE_ADD_EDIT_PROPERTY_NAME, name);
        params.put(WSConfig.WS_CRUD_PHONE_ADD_EDIT_PROPERTY_MANUFACTURER, manufacturer);
        params.put(WSConfig.WS_CRUD_PHONE_ADD_EDIT_PROPERTY_ANDROID_VERSION, androidVersion);
        params.put(WSConfig.WS_CRUD_PHONE_ADD_EDIT_PROPERTY_SCREEN_SIZE, String.valueOf(screenSize));
        params.put(WSConfig.WS_CRUD_PHONE_ADD_EDIT_PROPERTY_PRICE, String.valueOf(price));

        NetworkConnectionResult wsResult = NetworkConnection.retrieveResponseFromService(
                WSConfig.WS_CRUD_PHONE_ADD_EDIT_URL,
                NetworkConnection.METHOD_GET, params);

        Phone phone = PhoneAddEditFactory.parseResult(wsResult.body);

        Bundle bundle = new Bundle();
        bundle.putParcelable(PoCRequestManager.RECEIVER_EXTRA_PHONE_ADD_EDIT_DATA, phone);
        return bundle;
    }
}
