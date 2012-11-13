/**
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package com.foxykeep.datadroid.internal.network;

import android.content.Context;
import android.util.Log;

import com.foxykeep.datadroid.config.LogConfig;
import com.foxykeep.datadroid.exception.ConnectionException;
import com.foxykeep.datadroid.network.NetworkConnection;
import com.foxykeep.datadroid.network.NetworkConnection.ConnectionResult;
import com.foxykeep.datadroid.network.NetworkConnection.Method;
import com.foxykeep.datadroid.network.UserAgentUtils;

import org.apache.http.auth.UsernamePasswordCredentials;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * Implementation of {@link NetworkConnection}.
 *
 * @author Foxykeep
 * @see NetworkConnectionImplGB
 */
public final class NetworkConnectionImpl {

    private static final String TAG = NetworkConnectionImpl.class.getSimpleName();

    private static final String ACCEPT_CHARSET_HEADER = "Accept-Charset";
    private static final String ACCEPT_ENCODING_HEADER = "Accept-Encoding";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String USER_AGENT_HEADER = "User-Agent";

    private static final String UTF8_CHARSET = "UTF-8";

    // Default connection and socket timeout of 60 seconds. Tweak to taste.
    private static final int OPERATION_TIMEOUT = 60 * 1000;

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
     * @param headerMap The headers to add to the request.
     * @param isGzipEnabled Whether the request will use gzip compression if available on the
     *            server.
     * @param userAgent The user agent to set in the request. If null, a default Android one will be
     *            created.
     * @param postText The POSTDATA text to add in the request.
     * @param credentials The credentials to use for authentication.
     * @param isSslValidationEnabled Whether the request will validate the SSL certificates.
     * @return The result of the webservice call.
     */
    public static ConnectionResult execute(Context context, String urlValue, Method method,
            HashMap<String, String> parameterMap, HashMap<String, String> headerMap,
            boolean isGzipEnabled, String userAgent, String postText,
            UsernamePasswordCredentials credentials, boolean isSslValidationEnabled) throws
            ConnectionException {
        ConnectionResult result = null;
        // TODO add implementation based on HttpURLConnection.
        // TODO add the http response cache for ICS+
        try {
            // Prepare the request information
            if (userAgent == null) {
                userAgent = UserAgentUtils.get(context);
            }
            if (headerMap == null) {
                headerMap = new HashMap<String, String>();
            }
            headerMap.put(USER_AGENT_HEADER, userAgent);
            if (isGzipEnabled) {
                headerMap.put(ACCEPT_ENCODING_HEADER, "gzip");
            }
            headerMap.put(ACCEPT_CHARSET_HEADER, UTF8_CHARSET);

            StringBuilder paramBuilder = new StringBuilder();
            for (Entry<String, String> parameter : parameterMap.entrySet()) {
                paramBuilder.append(URLEncoder.encode(parameter.getKey(), UTF8_CHARSET));
                paramBuilder.append("=");
                paramBuilder.append(URLEncoder.encode(parameter.getValue(), UTF8_CHARSET));
                paramBuilder.append("&");
            }

            // Log the request
            if (LogConfig.DD_DEBUG_LOGS_ENABLED) {
                Log.d(TAG, "Request url : " + urlValue);
                Log.d(TAG, "Method : " + method.toString());

                if (parameterMap != null && !parameterMap.isEmpty()) {
                    Log.d(TAG, "Parameters :");
                    for (Entry<String, String> parameter : parameterMap.entrySet()) {
                        Log.d(TAG, "- " + parameter.getKey() + " = " + parameter.getValue());
                    }
                }

                if (postText != null) {
                    Log.d(TAG, "Post body : " + postText);
                }

                if (headerMap != null && !headerMap.isEmpty()) {
                    Log.d(TAG, "Headers :");
                    for (Entry<String, String> header : headerMap.entrySet()) {
                        Log.d(TAG, "- " + header.getKey() + " = " + header.getValue());
                    }
                }
            }

            // Create the connection object
            HttpURLConnection connection = null;
            switch (method) {
                case GET:
                case DELETE:
                case PUT:
                    URL url = new URL(urlValue + "?" + paramBuilder.toString());
                    connection = (HttpURLConnection) url.openConnection();
                    break;
                case POST:
                    connection = (HttpURLConnection) new URL(urlValue).openConnection();
                    connection.setDoOutput(true);

                    String outputText = null;
                    if (paramBuilder.length() > 0) {
                        outputText = paramBuilder.toString();
                        headerMap.put(CONTENT_TYPE_HEADER, "application/x-www-form-urlencoded");
                    } else if (postText != null) {
                        outputText = postText;
                    } else {
                        break;
                    }

                    OutputStream output = null;
                    try {
                        output = connection.getOutputStream();
                        output.write(outputText.getBytes());
                    } finally {
                        if (output != null) {
                            try {
                                output.close();
                            } catch (IOException e) {
                                // Already catching the first exception so nothing to do here.
                            }
                        }
                    }
                    break;
            }

            // Set the request method
            connection.setRequestMethod(method.toString());

            // Add the headers
            for (Entry<String, String> header : headerMap.entrySet()) {
                connection.addRequestProperty(header.getKey(), header.getValue());
            }

            // Set the connection and read timeout
            connection.setConnectTimeout(OPERATION_TIMEOUT);
            connection.setReadTimeout(OPERATION_TIMEOUT);

            // TODO manage the response
        } catch (IOException e) {
            throw new ConnectionException(e);
        }

        return result;
    }
}
