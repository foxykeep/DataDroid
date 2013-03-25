/**
 * 2012 Foxykeep (http://datadroid.foxykeep.com)
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
import com.foxykeep.datadroidpoc.data.requestmanager.PoCRequestFactory;

import android.content.Context;
import android.os.Bundle;

import org.apache.http.auth.UsernamePasswordCredentials;

public class AuthenticationOperation implements Operation {

    public static final String PARAM_WITH_AUTHENTICATE =
            "com.foxykeep.datadroidpoc.extra.authenticate";

    private static final String LOGIN = "admin";
    private static final String PASSWD = "admin"; // Worst password ever \o/

    @Override
    public Bundle execute(Context context, Request request) throws ConnectionException,
            DataException {
        NetworkConnection networkConnection = new NetworkConnection(context,
                WSConfig.WS_AUTHENTICATION_URL);
        if (request.getBoolean(PARAM_WITH_AUTHENTICATE)) {
            networkConnection.setCredentials(new UsernamePasswordCredentials(LOGIN, PASSWD));
        }
        ConnectionResult result = networkConnection.execute();

        Bundle bundle = new Bundle();
        bundle.putString(PoCRequestFactory.BUNDLE_EXTRA_AUTHENTICATION_RESULT, result.body);
        return bundle;
    }

}
