package com.foxykeep.datadroidpoc.data.worker;

import java.io.IOException;
import java.net.URISyntaxException;

import android.os.Bundle;

import org.json.JSONException;

import com.foxykeep.datadroid.exception.RestClientException;
import com.foxykeep.datadroid.network.NetworkConnection;
import com.foxykeep.datadroid.network.NetworkConnection.NetworkConnectionResult;
import com.foxykeep.datadroidpoc.config.WSConfig;
import com.foxykeep.datadroidpoc.data.factory.CityListJsonFactory;

public class CityListWorker {

    public static Bundle start() throws IllegalStateException, IOException, URISyntaxException, RestClientException,
            JSONException {

        NetworkConnectionResult wsResult = NetworkConnection.retrieveResponseFromService(WSConfig.WS_CITY_LIST_URL,
                NetworkConnection.METHOD_GET);

        return CityListJsonFactory.parseResult(wsResult.wsResponse);
    }
}
