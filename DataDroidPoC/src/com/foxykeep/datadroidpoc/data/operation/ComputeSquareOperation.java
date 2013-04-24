/**
 * 2013 Foxykeep (http://datadroid.foxykeep.com)
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
import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.service.RequestService;
import com.foxykeep.datadroidpoc.config.JSONTag;
import com.foxykeep.datadroidpoc.config.WSConfig;
import com.foxykeep.datadroidpoc.data.requestmanager.PoCRequestFactory;

import android.content.Context;
import android.os.Bundle;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public final class ComputeSquareOperation implements RequestService.Operation {

    public static final String PARAM_METHOD = "com.foxykeep.datadroidpoc.extra.method";
    public static final String PARAM_NUMBER = "com.foxykeep.datadroidpoc.extra.number";

    @Override
    public Bundle execute(Context context, Request request)
            throws ConnectionException, DataException, CustomRequestException {
        String url;
        NetworkConnection.Method method;

        switch (request.getInt(PARAM_METHOD)) {
            case 0: // GET
                url = WSConfig.WS_REQUEST_TYPES_GET;
                method = NetworkConnection.Method.GET;
                break;
            case 1: // POST
                url = WSConfig.WS_REQUEST_TYPES_POST;
                method = NetworkConnection.Method.POST;
                break;
            case 2: // PUT
                // TODO for now hack to keep it as a GET method as my server doesn't accept PUT
                // requests
                url = WSConfig.WS_REQUEST_TYPES_GET;
                method = NetworkConnection.Method.GET;
//                url = WSConfig.WS_REQUEST_TYPES_PUT;
//                method = NetworkConnection.Method.PUT;
                break;
            case 3: // DELETE
                // TODO for now hack to keep it as a GET method as my server doesn't accept
                // DELETE requests
                url = WSConfig.WS_REQUEST_TYPES_GET;
                method = NetworkConnection.Method.GET;
//                url = WSConfig.WS_REQUEST_TYPES_DELETE;
//                method = NetworkConnection.Method.DELETE;
                break;
            default:
                throw new IllegalArgumentException("Unknown method: "
                        + request.getInt(PARAM_METHOD));
        }

        NetworkConnection connection = new NetworkConnection(context, url);
        connection.setMethod(method);
        ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair(WSConfig.WS_REQUEST_TYPE_NUMBER,
                request.getIntAsString(PARAM_NUMBER)));
        connection.setParameters(params);
        NetworkConnection.ConnectionResult result = connection.execute();

        // Parse the result
        Bundle bundle = new Bundle();
        try {
            JSONObject root = new JSONObject(result.body);
            bundle.putInt(PoCRequestFactory.BUNDLE_EXTRA_SQUARE,
                    root.getInt(JSONTag.REQUEST_TYPE_VALUE));
        } catch (JSONException e) {
            throw new DataException(e);
        }

        return bundle;
    }
}
