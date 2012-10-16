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
import com.foxykeep.datadroidpoc.data.factory.CityListJsonFactory;

import org.json.JSONException;

import java.io.IOException;
import java.net.URISyntaxException;

public final class CityListWorker {

    private CityListWorker() {
        // No public constructor
    }

    public static Bundle start() throws IllegalStateException, IOException, URISyntaxException,
            RestClientException,
            JSONException {

        NetworkConnectionResult wsResult = NetworkConnection.retrieveResponseFromService(
                WSConfig.WS_CITY_LIST_URL,
                NetworkConnection.METHOD_GET);

        return CityListJsonFactory.parseResult(wsResult.body);
    }
}
