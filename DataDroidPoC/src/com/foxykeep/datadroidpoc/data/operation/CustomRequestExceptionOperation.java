/**
 * 2012 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */
package com.foxykeep.datadroidpoc.data.operation;

import com.foxykeep.datadroid.exception.ConnectionException;
import com.foxykeep.datadroid.exception.CustomRequestException;
import com.foxykeep.datadroid.exception.DataException;
import com.foxykeep.datadroid.network.NetworkConnection;
import com.foxykeep.datadroid.network.NetworkConnection.ConnectionResult;
import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.service.RequestService.Operation;
import com.foxykeep.datadroidpoc.config.WSConfig;
import com.foxykeep.datadroidpoc.data.exception.MyCustomRequestException;
import com.foxykeep.datadroidpoc.data.factory.CityListJsonFactory;

import android.content.Context;
import android.os.Bundle;

/**
 * Sample WS call throwing a {@link CustomRequestException}.
 * <p>
 * Check {@link com.foxykeep.datadroidpoc.data.service.PoCRequestService#onCustomRequestException(Request, CustomRequestException)} to see how to
 * manage the thrown exception.
 */
public class CustomRequestExceptionOperation implements Operation {

    public static final String PARAM_WITH_EXCEPTION =
            "com.foxykeep.datadroidpoc.extra.withException";

    @Override
    public Bundle execute(Context context, Request request) throws ConnectionException,
            DataException, CustomRequestException {

        boolean withException = request.getBoolean(PARAM_WITH_EXCEPTION);
        if (withException) {
            throw new MyCustomRequestException();
        }

        NetworkConnection networkConnection = new NetworkConnection(context,
                WSConfig.WS_CITY_LIST_2_URL);
        ConnectionResult result = networkConnection.execute();

        return CityListJsonFactory.parseResult(result.body);
    }

}
