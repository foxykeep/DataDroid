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
import com.foxykeep.datadroid.network.NetworkConnection.Method;
import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.service.RequestService.Operation;
import com.foxykeep.datadroidpoc.config.WSConfig;
import com.foxykeep.datadroidpoc.data.factory.CityListJsonFactory;

import android.content.Context;
import android.os.Bundle;

import java.util.HashMap;

public final class CityListOperation implements Operation {

    @Override
    public Bundle execute(Context context, Request request) throws ConnectionException,
            DataException {
        // Add the version parameter to get the new output from the CityList WS
        HashMap<String, String> parameterMap = new HashMap<String, String>();
        parameterMap.put(WSConfig.WS_CITY_PROPERTY_VERSION, "2");

        NetworkConnection networkConnection = new NetworkConnection(context,
                WSConfig.WS_CITY_LIST_URL);
        networkConnection.setMethod(Method.POST);
        networkConnection.setParameters(parameterMap);
        ConnectionResult result = networkConnection.execute();

        return CityListJsonFactory.parseResult(result.body);
    }
}
