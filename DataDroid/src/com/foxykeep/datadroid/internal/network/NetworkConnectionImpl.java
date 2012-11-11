/**
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package com.foxykeep.datadroid.internal.network;

import com.foxykeep.datadroid.network.NetworkConnection;
import com.foxykeep.datadroid.network.NetworkConnection.Method;
import com.foxykeep.datadroid.network.NetworkConnection.ConnectionResult;

import org.apache.http.Header;
import org.apache.http.auth.UsernamePasswordCredentials;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Implementation of {@link NetworkConnection}.
 *
 * @author Foxykeep
 * @see NetworkConnectionImplGB
 */
public final class NetworkConnectionImpl {

    private NetworkConnectionImpl() {
        // No public constructor
    }

    /**
     * Call the webservice using the given parameters to construct the request and return the
     * result.
     *
     * @param context The context to use for this operation. Used to generate the user agent if
     *            needed.
     * @param url The webservice URL.
     * @param method The request method to use.
     * @param parameterMap The parameters to add to the request.
     * @param headerList The headers to add to the request.
     * @param isGzipEnabled Whether the request will use gzip compression if available on the
     *            server.
     * @param userAgent The user agent to set in the request. If null, a default Android one will be
     *            created.
     * @param postText The POSTDATA text to add in the request.
     * @param credentials The credentials to use for authentication.
     * @param isSslValidationEnabled Whether the request will validate the SSL certificates.
     * @return The result of the webservice call.
     */
    public static ConnectionResult execute(Context context, String url, Method method,
            HashMap<String, String> parameterMap, ArrayList<Header> headerList,
            boolean isGzipEnabled, String userAgent, String postText,
            UsernamePasswordCredentials credentials, boolean isSslValidationEnabled) {
        // TODO add implementation based on HttpURLConnection.
        // TODO add the http response cache for ICS+
        return null;
    }
}
