/*
 * 2011 Foxykeep (http://www.foxykeep.com)
 *
 * Licensed under the Beerware License :
 * 
 *   As long as you retain this notice you can do whatever you want with this stuff. If we meet some day, and you think
 *   this stuff is worth it, you can buy me a beer in return
 */
package com.foxykeep.dataproxypoc.data.worker;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

import android.content.Context;
import android.os.Bundle;

import com.foxykeep.dataproxy.exception.RestClientException;
import com.foxykeep.dataproxy.network.NetworkConnection;
import com.foxykeep.dataproxy.network.NetworkConnection.NetworkConnectionResult;
import com.foxykeep.dataproxypoc.config.WSConfig;
import com.foxykeep.dataproxypoc.data.factory.PersonJsonFactory;
import com.foxykeep.dataproxypoc.data.factory.PersonXmlFactory;

public class PersonsWorker {

    public static final int RETURN_FORMAT_XML = 0;
    public static final int RETURN_FORMAT_JSON = 1;

    public static Bundle start(final Context context, final int minAge, final int returnFormat)
            throws IllegalStateException, IOException, URISyntaxException, RestClientException {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(WSConfig.WS_PERSONS_PARAM_MIN_AGE, minAge + "");

        NetworkConnectionResult wsResult = NetworkConnection.retrieveResponseFromService(
                returnFormat == RETURN_FORMAT_XML ? WSConfig.WS_PERSONS_URL_XML : WSConfig.WS_PERSONS_URL_JSON,
                NetworkConnection.METHOD_GET, params);

        if (returnFormat == RETURN_FORMAT_XML) {
            PersonXmlFactory.parseResult(wsResult.mWsResponse);
        } else {
            PersonJsonFactory.parseResult(wsResult.mWsResponse);
        }

        return null;
    }
}
