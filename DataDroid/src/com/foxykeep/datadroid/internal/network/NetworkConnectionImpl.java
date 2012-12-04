/**
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package com.foxykeep.datadroid.internal.network;

import android.content.Context;
import android.support.util.Base64Compat;
import android.util.Base64;
import android.util.Log;

import com.foxykeep.datadroid.config.LogConfig;
import com.foxykeep.datadroid.exception.ConnectionException;
import com.foxykeep.datadroid.network.NetworkConnection;
import com.foxykeep.datadroid.network.NetworkConnection.ConnectionResult;
import com.foxykeep.datadroid.network.NetworkConnection.Method;
import com.foxykeep.datadroid.network.UserAgentUtils;

import org.apache.http.HttpStatus;
import org.apache.http.auth.UsernamePasswordCredentials;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String CONTENT_ENCODING_HEADER = "Content-Encoding";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String LOCATION_HEADER = "Location";
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
     * @param isSslValidationDisabled Whether the request will validate the SSL certificates.
     * @return The result of the webservice call.
     */
    public static ConnectionResult execute(Context context, String urlValue, Method method,
            HashMap<String, String> parameterMap, HashMap<String, String> headerMap,
            boolean isGzipEnabled, String userAgent, String postText,
            UsernamePasswordCredentials credentials, boolean isSslValidationDisabled) throws
            ConnectionException {
        HttpURLConnection connection = null;
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
            if (credentials != null) {
                headerMap.put(AUTHORIZATION_HEADER, createAuthenticationHeader(credentials));
            }

            StringBuilder paramBuilder = new StringBuilder();
            if (parameterMap != null && !parameterMap.isEmpty()) {
                for (Entry<String, String> parameter : parameterMap.entrySet()) {
                    paramBuilder.append(URLEncoder.encode(parameter.getKey(), UTF8_CHARSET));
                    paramBuilder.append("=");
                    paramBuilder.append(URLEncoder.encode(parameter.getValue(), UTF8_CHARSET));
                    paramBuilder.append("&");
                }
            }

            // Log the request
            if (LogConfig.DD_DEBUG_LOGS_ENABLED) {
                Log.d(TAG, "Request url: " + urlValue);
                Log.d(TAG, "Method: " + method.toString());

                if (parameterMap != null && !parameterMap.isEmpty()) {
                    Log.d(TAG, "Parameters:");
                    for (Entry<String, String> parameter : parameterMap.entrySet()) {
                        Log.d(TAG, "- " + parameter.getKey() + " = " + parameter.getValue());
                    }
                }

                if (postText != null) {
                    Log.d(TAG, "Post body: " + postText);
                }

                if (headerMap != null && !headerMap.isEmpty()) {
                    Log.d(TAG, "Headers:");
                    for (Entry<String, String> header : headerMap.entrySet()) {
                        Log.d(TAG, "- " + header.getKey() + " = " + header.getValue());
                    }
                }
            }

            // Create the connection object
            URL url = null;
            String outputText = null;
            switch (method) {
                case GET:
                case DELETE:
                case PUT:
                    url = new URL(urlValue + "?" + paramBuilder.toString());
                    connection = (HttpURLConnection) url.openConnection();
                    break;
                case POST:
                    url = new URL(urlValue);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setDoOutput(true);

                    if (paramBuilder.length() > 0) {
                        outputText = paramBuilder.toString();
                        headerMap.put(CONTENT_TYPE_HEADER, "application/x-www-form-urlencoded");
                    } else if (postText != null) {
                        outputText = postText;
                    }
                    break;
            }

            // Set the request method
            connection.setRequestMethod(method.toString());

            // If it's an HTTPS request and the SSL Validation is disabled
            if (url.getProtocol().equals("https")
                    && isSslValidationDisabled) {
                HttpsURLConnection httpsConnection = (HttpsURLConnection) connection;
                httpsConnection.setSSLSocketFactory(getAllHostsValidSocketFactory());
                httpsConnection.setHostnameVerifier(getAllHostsValidVerifier());
            }

            // Add the headers
            if (!headerMap.isEmpty()) {
                for (Entry<String, String> header : headerMap.entrySet()) {
                    connection.addRequestProperty(header.getKey(), header.getValue());
                }
            }

            // Set the connection and read timeout
            connection.setConnectTimeout(OPERATION_TIMEOUT);
            connection.setReadTimeout(OPERATION_TIMEOUT);

            // Set the outputStream content for POST requests
            if (method == Method.POST && outputText != null) {
                OutputStream output = null;
                try {
                    output = connection.getOutputStream();
                    output.write(outputText.getBytes());
                } finally {
                    if (output != null) {
                        try {
                            output.close();
                        } catch (IOException e) {
                            // Already catching the first IOException so nothing to do here.
                        }
                    }
                }
            }

            int responseCode = connection.getResponseCode();
            if (LogConfig.DD_DEBUG_LOGS_ENABLED) {
                Log.d(TAG, "Response code: " + responseCode);
            }
            if (responseCode != HttpStatus.SC_OK) {
                if (responseCode == HttpStatus.SC_MOVED_PERMANENTLY) {
                    String redirectionUrl = connection.getHeaderField(LOCATION_HEADER);
                    throw new ConnectionException("New location : " + redirectionUrl,
                            redirectionUrl);
                } else {
                    throw new ConnectionException("Invalid response from server.", responseCode);
                }
            }

            String contentEncoding = connection.getHeaderField(CONTENT_ENCODING_HEADER);
            String body = convertStreamToString(connection.getInputStream(),
                    contentEncoding != null
                    && contentEncoding.equalsIgnoreCase("gzip"));

            if (LogConfig.DD_VERBOSE_LOGS_ENABLED) {
                Log.v(TAG, "Response body: ");

                int pos = 0;
                int bodyLength = body.length();
                while (pos < bodyLength) {
                    Log.v(TAG, body.substring(pos, Math.min(bodyLength - 1, pos + 200)));
                    pos = pos + 200;
                }
            }

            return new ConnectionResult(connection.getHeaderFields(), body);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ConnectionException(e);
        } catch (KeyManagementException e) {
            e.printStackTrace();
            throw new ConnectionException(e);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new ConnectionException(e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static String createAuthenticationHeader(UsernamePasswordCredentials credentials) {
        StringBuilder sb = new StringBuilder();
        sb.append(credentials.getUserName()).append(":").append(credentials.getPassword());
        return "Basic" + Base64Compat.encodeToString(sb.toString().getBytes(), Base64.NO_WRAP);
    }

    private static SSLSocketFactory sAllHostsValidSocketFactory;

    private static SSLSocketFactory getAllHostsValidSocketFactory()
            throws NoSuchAlgorithmException, KeyManagementException {
        if (sAllHostsValidSocketFactory == null) {
            TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
            };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            sAllHostsValidSocketFactory = sc.getSocketFactory();
        }

        return sAllHostsValidSocketFactory;
    }

    private static HostnameVerifier sAllHostsValidVerifier;

    private static HostnameVerifier getAllHostsValidVerifier() {
        if (sAllHostsValidVerifier == null) {
            sAllHostsValidVerifier = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
        }

        return sAllHostsValidVerifier;
    }

    private static String convertStreamToString(InputStream is, boolean isGzipEnabled)
            throws IOException {
        InputStream cleanedIs = is;
        if (isGzipEnabled) {
            cleanedIs = new GZIPInputStream(is);
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(cleanedIs, UTF8_CHARSET));
            StringBuilder sb = new StringBuilder();
            for (String line; (line = reader.readLine()) != null;) {
                sb.append(line);
                sb.append("\n");
            }

            return sb.toString();
        } finally {
            if (reader != null) {
                reader.close();
            }

            cleanedIs.close();

            if (isGzipEnabled) {
                is.close();
            }
        }
    }
}
