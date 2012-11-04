/**
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package com.foxykeep.datadroidpoc.data.worker;

import android.os.Bundle;

import com.foxykeep.datadroid.exception.ConnectionException;
import com.foxykeep.datadroid.exception.DataException;
import com.foxykeep.datadroid.network.NetworkConnection.ConnectionResult;
import com.foxykeep.datadroid.network.NetworkConnection.NetworkConnectionBuilder;
import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.service.RequestService.Operation;
import com.foxykeep.datadroidpoc.config.WSConfig;
import com.foxykeep.datadroidpoc.data.factory.CityListJsonFactory;

public final class CityListOperation implements Operation {

    public CityListOperation() {}

    @Override
    public Bundle execute(Request request) throws ConnectionException, DataException {
        ConnectionResult result = new NetworkConnectionBuilder(WSConfig.WS_CITY_LIST_URL).execute();

        return CityListJsonFactory.parseResult(result.body);
    }
}
