package com.foxykeep.dataproxypoc.data.worker;

import java.io.IOException;
import java.net.URISyntaxException;

import android.os.Bundle;

import org.json.JSONException;

import com.foxykeep.dataproxy.exception.RestClientException;
import com.foxykeep.dataproxy.network.NetworkConnection;
import com.foxykeep.dataproxy.network.NetworkConnection.NetworkConnectionResult;
import com.foxykeep.dataproxypoc.config.WSConfig;
import com.foxykeep.dataproxypoc.data.factory.CityListJsonFactory;

public class CityListWorker {

    public static Bundle start() throws IllegalStateException, IOException, URISyntaxException, RestClientException,
            JSONException {

        NetworkConnectionResult wsResult = NetworkConnection.retrieveResponseFromService(WSConfig.WS_CITY_LIST_URL,
                NetworkConnection.METHOD_GET);

        return CityListJsonFactory.parseResult(wsResult.wsResponse);
    }
}
