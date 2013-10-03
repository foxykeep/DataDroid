/**
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */
package com.foxykeep.datadroid.internal.network;

import com.foxykeep.datadroid.exception.ConnectionException;
import com.foxykeep.datadroid.network.HttpUrlConnectionHelper;
import com.foxykeep.datadroid.network.NetworkConnection.ConnectionResult;
import com.foxykeep.datadroid.network.NetworkConnection.Method;
import com.foxykeep.datadroid.network.UserAgentUtils;
import com.foxykeep.datadroid.util.DataDroidLog;

import android.content.Context;
import android.support.util.Base64;
import android.text.TextUtils;
import android.util.Log;

import org.apache.http.HttpStatus;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

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
import java.util.ArrayList;
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
 * Implementation of the network connection.
 *
 * @author Foxykeep
 */
public final class NetworkConnectionImpl {

    private static final String TAG = NetworkConnectionImpl.class.getSimpleName();

    private static final String ACCEPT_CHARSET_HEADER = "Accept-Charset";
    private static final String ACCEPT_ENCODING_HEADER = "Accept-Encoding";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String LOCATION_HEADER = "Location";

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
     * @param urlValue The webservice URL.
     * @param method The request method to use.
     * @param parameterList The parameters to add to the request.
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
            ArrayList<BasicNameValuePair> parameterList, HashMap<String, String> headerMap,
            boolean isGzipEnabled, String userAgent, String postText,
            UsernamePasswordCredentials credentials, boolean isSslValidationEnabled) throws
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
            headerMap.put(HTTP.USER_AGENT, userAgent);
            if (isGzipEnabled) {
                headerMap.put(ACCEPT_ENCODING_HEADER, "gzip");
            }
            headerMap.put(ACCEPT_CHARSET_HEADER, UTF8_CHARSET);
            if (credentials != null) {
                headerMap.put(AUTHORIZATION_HEADER, createAuthenticationHeader(credentials));
            }

            StringBuilder paramBuilder = new StringBuilder();
            if (parameterList != null && !parameterList.isEmpty()) {
                for (int i = 0, size = parameterList.size(); i < size; i++) {
                    BasicNameValuePair parameter = parameterList.get(i);
                    String name = parameter.getName();
                    String value = parameter.getValue();
                    if (TextUtils.isEmpty(name)) {
                        // Empty parameter name. Check the next one.
                        continue;
                    }
                    if (value == null) {
                        value = "";
                    }
                    paramBuilder.append(URLEncoder.encode(name, UTF8_CHARSET));
                    paramBuilder.append("=");
                    paramBuilder.append(URLEncoder.encode(value, UTF8_CHARSET));
                    paramBuilder.append("&");
                }
            }

            // Log the request
            if (DataDroidLog.canLog(Log.DEBUG)) {
                DataDroidLog.d(TAG, "Request url: " + urlValue);
                DataDroidLog.d(TAG, "Method: " + method.toString());

                if (parameterList != null && !parameterList.isEmpty()) {
                    DataDroidLog.d(TAG, "Parameters:");
                    for (int i = 0, size = parameterList.size(); i < size; i++) {
                        BasicNameValuePair parameter = parameterList.get(i);
                        String message = "- \"" + parameter.getName() + "\" = \""
                                + parameter.getValue() + "\"";
                        DataDroidLog.d(TAG, message);
                    }

                    DataDroidLog.d(TAG, "Parameters String: \"" + paramBuilder.toString() + "\"");
                }

                if (postText != null) {
                    DataDroidLog.d(TAG, "Post data: " + postText);
                }

                if (headerMap != null && !headerMap.isEmpty()) {
                    DataDroidLog.d(TAG, "Headers:");
                    for (Entry<String, String> header : headerMap.entrySet()) {
                        DataDroidLog.d(TAG, "- " + header.getKey() + " = " + header.getValue());
                    }
                }
            }

            // Create the connection object
            URL url = null;
            String outputText = null;
            switch (method) {
                case GET:
                case DELETE:
                    String fullUrlValue = urlValue;
                    if (paramBuilder.length() > 0) {
                        fullUrlValue += "?" + paramBuilder.toString();
                    }
                    url = new URL(fullUrlValue);
                    connection = HttpUrlConnectionHelper.openUrlConnection(url);
                    break;
                case PUT:
                case POST:
                    url = new URL(urlValue);
                    connection = HttpUrlConnectionHelper.openUrlConnection(url);
                    connection.setDoOutput(true);

                    if (paramBuilder.length() > 0) {
                        outputText = paramBuilder.toString();
                        headerMap.put(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded");
                        headerMap.put(HTTP.CONTENT_LEN,
                                String.valueOf(outputText.getBytes().length));
                    } else if (postText != null) {
                        outputText = postText;
                    }
                    break;
            }

            // Set the request method
            connection.setRequestMethod(method.toString());

            // If it's an HTTPS request and the SSL Validation is disabled
            if (url.getProtocol().equals("https")
                    && !isSslValidationEnabled) {
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

            // Set the outputStream content for POST and PUT requests
            if ((method == Method.POST || method == Method.PUT) && outputText != null) {
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

            String contentEncoding = connection.getHeaderField(HTTP.CONTENT_ENCODING);

            int responseCode = connection.getResponseCode();
            boolean isGzip = contentEncoding != null
                    && contentEncoding.equalsIgnoreCase("gzip");
            DataDroidLog.d(TAG, "Response code: " + responseCode);

            if (responseCode == HttpStatus.SC_MOVED_PERMANENTLY) {
                String redirectionUrl = connection.getHeaderField(LOCATION_HEADER);
                throw new ConnectionException("New location : " + redirectionUrl,
                        redirectionUrl);
            }

            InputStream errorStream = connection.getErrorStream();
            if (errorStream != null) {
                String error = convertStreamToString(errorStream,  isGzip);
                throw new ConnectionException(error, responseCode);
            }

            String body = convertStreamToString(connection.getInputStream(),
                    isGzip);

            if (DataDroidLog.canLog(Log.VERBOSE)) {
                DataDroidLog.v(TAG, "Response body: ");

                int pos = 0;
                int bodyLength = body.length();
                while (pos < bodyLength) {
                    DataDroidLog.v(TAG, body.substring(pos, Math.min(bodyLength - 1, pos + 200)));
                    pos = pos + 200;
                }
            }

            return new ConnectionResult(connection.getHeaderFields(), body);
        } catch (IOException e) {
            DataDroidLog.e(TAG, "IOException", e);
            throw new ConnectionException(e);
        } catch (KeyManagementException e) {
            DataDroidLog.e(TAG, "KeyManagementException", e);
            throw new ConnectionException(e);
        } catch (NoSuchAlgorithmException e) {
            DataDroidLog.e(TAG, "NoSuchAlgorithmException", e);
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
        return "Basic " + Base64.encodeToString(sb.toString().getBytes(), Base64.NO_WRAP);
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
