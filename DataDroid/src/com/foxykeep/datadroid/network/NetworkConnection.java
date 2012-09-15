/*
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 *
 * Licensed under the Beerware License :
 * 
 *   As long as you retain this notice you can do whatever you want with this stuff. If we meet some day, and you think
 *   this stuff is worth it, you can buy me a beer in return
 */
package com.foxykeep.datadroid.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.Looper;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.CharArrayBuffer;

import com.foxykeep.datadroid.config.LogConfig;
import com.foxykeep.datadroid.exception.CompulsoryParameterException;
import com.foxykeep.datadroid.exception.RestClientException;

/**
 * This class gives the user methods to easily call a webservice and return the received string
 * 
 * @author Foxykeep
 */
public class NetworkConnection {

    private static final String LOG_TAG = NetworkConnection.class.getSimpleName();

    public static final int METHOD_GET = 0;
    public static final int METHOD_POST = 1;
    public static final int METHOD_PUT = 2;
    public static final int METHOD_DELETE = 3;

    private static String sDefaultUserAgent = null;

    /**
     * By default the user agent is empty. If you want to use the standard Android user agent, call this method before using the
     * <code>retrieveResponseFromService</code> methods
     * 
     * @param context The context
     */
    public static void generateDefaultUserAgent(final Context context) {
        if (sDefaultUserAgent != null) {
            return;
        }

        try {
            Constructor<WebSettings> constructor = WebSettings.class.getDeclaredConstructor(Context.class, WebView.class);
            constructor.setAccessible(true);
            try {
                WebSettings settings = constructor.newInstance(context, null);
                sDefaultUserAgent = settings.getUserAgentString();
            } finally {
                constructor.setAccessible(false);
            }
        } catch (Exception e) {
            if (Thread.currentThread().getName().equalsIgnoreCase("main")) {
                WebView webview = new WebView(context);
                sDefaultUserAgent = webview.getSettings().getUserAgentString();
            } else {
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        WebView webview = new WebView(context);
                        sDefaultUserAgent = webview.getSettings().getUserAgentString();
                        Looper.loop();
                    }
                };
                thread.start();
            }
        }
    }

    /**
     * The result of a webservice call. Contain the Header of the response and the body of the response as an unparsed String
     * 
     * @author Foxykeep
     */
    public static class NetworkConnectionResult {

        public Header[] headerArray;
        public String wsResponse;

        /**
         * Http response result container.
         * 
         * @param resultHeader
         * @param result
         */
        public NetworkConnectionResult(final Header[] resultHeader, final String result) {
            headerArray = resultHeader;
            wsResponse = result;
        }
    }

    /**
     * Call a webservice and return the response
     * 
     * @param url The url of the webservice
     * @return A NetworkConnectionResult containing the response
     * @throws IllegalStateException
     * @throws IOException
     * @throws URISyntaxException
     * @throws RestClientException
     */
    public static NetworkConnectionResult retrieveResponseFromService(final String url) throws IllegalStateException, IOException,
            URISyntaxException, RestClientException {
        return retrieveResponseFromService(url, METHOD_GET);
    }

    /**
     * Call a webservice and return the response
     * 
     * @param url The url of the webservice
     * @param method The method to use (must be one of the following : {@link #METHOD_GET}, {@link #METHOD_POST}, {@link #METHOD_PUT} ,
     *            {@link #METHOD_DELETE}
     * @return A NetworkConnectionResult containing the response
     * @throws IllegalStateException
     * @throws IOException
     * @throws URISyntaxException
     * @throws RestClientException
     */
    public static NetworkConnectionResult retrieveResponseFromService(final String url, final int method) throws IllegalStateException, IOException,
            URISyntaxException, RestClientException {
        return retrieveResponseFromService(url, method, null);
    }

    /**
     * Call a webservice and return the response
     * 
     * @param url The url of the webservice
     * @param method The method to use (must be one of the following : {@link #METHOD_GET}, {@link #METHOD_POST}, {@link #METHOD_PUT} ,
     *            {@link #METHOD_DELETE}
     * @param parameters The parameters to add to the request. This is a "key => value" Map.
     * @return A NetworkConnectionResult containing the response
     * @throws IllegalStateException
     * @throws IOException
     * @throws URISyntaxException
     * @throws RestClientException
     */
    public static NetworkConnectionResult retrieveResponseFromService(final String url, final int method, final Map<String, String> parameters)
            throws IllegalStateException, IOException, URISyntaxException, RestClientException {
        return retrieveResponseFromService(url, method, parameters, null);
    }

    /**
     * Call a webservice and return the response
     * 
     * @param url The url of the webservice
     * @param method The method to use (must be one of the following : {@link #METHOD_GET}, {@link #METHOD_POST}, {@link #METHOD_PUT} ,
     *            {@link #METHOD_DELETE}
     * @param parameters The parameters to add to the request. This is a "key => value" Map.
     * @param headers The headers to add to the request
     * @return A NetworkConnectionResult containing the response
     * @throws IllegalStateException
     * @throws IOException
     * @throws URISyntaxException
     * @throws RestClientException
     */
    public static NetworkConnectionResult retrieveResponseFromService(final String url, final int method, final Map<String, String> parameters,
            final ArrayList<Header> headers) throws IllegalStateException, IOException, URISyntaxException, RestClientException {
        return retrieveResponseFromService(url, method, parameters, headers, false);
    }

    /**
     * Call a webservice and return the response
     * 
     * @param url The url of the webservice
     * @param method The method to use (must be one of the following : {@link #METHOD_GET}, {@link #METHOD_POST}, {@link #METHOD_PUT} ,
     *            {@link #METHOD_DELETE}
     * @param parameters The parameters to add to the request. This is a "key => value" Map.
     * @param headers The headers to add to the request
     * @param isGzipEnabled Whether we should use gzip compression if available
     * @return A NetworkConnectionResult containing the response
     * @throws IllegalStateException
     * @throws IOException
     * @throws URISyntaxException
     * @throws RestClientException
     */
    public static NetworkConnectionResult retrieveResponseFromService(final String url, final int method, final Map<String, String> parameters,
            final ArrayList<Header> headers, final boolean isGzipEnabled) throws IllegalStateException, IOException, URISyntaxException,
            RestClientException {
        return retrieveResponseFromService(url, method, parameters, headers, isGzipEnabled, null);
    }

    /**
     * Call a webservice and return the response
     * 
     * @param url The url of the webservice
     * @param method The method to use (must be one of the following : {@link #METHOD_GET}, {@link #METHOD_POST}, {@link #METHOD_PUT} ,
     *            {@link #METHOD_DELETE}
     * @param parameters The parameters to add to the request. This is a "key => value" Map.
     * @param headers The headers to add to the request
     * @param isGzipEnabled Whether we should use gzip compression if available
     * @param userAgent The user agent to set in the request. If not given, the default one will be used
     * @return A NetworkConnectionResult containing the response
     * @throws IllegalStateException
     * @throws IOException
     * @throws URISyntaxException
     * @throws RestClientException
     */
    public static NetworkConnectionResult retrieveResponseFromService(final String url, final int method, final Map<String, String> parameters,
            final ArrayList<Header> headers, final boolean isGzipEnabled, final String userAgent) throws IllegalStateException, IOException,
            URISyntaxException, RestClientException {

        return retrieveResponseFromService(url, method, parameters, headers, isGzipEnabled, userAgent, null);
    }

    /**
     * Call a webservice and return the response
     * 
     * @param url The url of the webservice
     * @param method The method to use (must be one of the following : {@link #METHOD_GET}, {@link #METHOD_POST}, {@link #METHOD_PUT} ,
     *            {@link #METHOD_DELETE}
     * @param parameters The parameters to add to the request. This is a "key => value" Map.
     * @param headers The headers to add to the request
     * @param isGzipEnabled Whether we should use gzip compression if available
     * @param userAgent The user agent to set in the request. If not given, the default one will be used
     * @param postText A POSTDATA text that will be added in the request (only if the method is set to {@link #METHOD_POST})
     * @return A NetworkConnectionResult containing the response
     * @throws IllegalStateException
     * @throws IOException
     * @throws URISyntaxException
     * @throws RestClientException
     */
    public static NetworkConnectionResult retrieveResponseFromService(final String url, final int method, final Map<String, String> parameters,
            final ArrayList<Header> headers, final boolean isGzipEnabled, final String userAgent, final String postText)
            throws IllegalStateException, IOException, URISyntaxException, RestClientException {
        return retrieveResponseFromService(url, method, parameters, headers, isGzipEnabled, userAgent, postText, null);
    }

    /**
     * Call a webservice and return the response
     * 
     * @param url The url of the webservice
     * @param method The method to use (must be one of the following : {@link #METHOD_GET}, {@link #METHOD_POST}, {@link #METHOD_PUT} ,
     *            {@link #METHOD_DELETE}
     * @param parameters The parameters to add to the request. This is a "key => value" Map.
     * @param headers The headers to add to the request
     * @param isGzipEnabled Whether we should use gzip compression if available
     * @param userAgent The user agent to set in the request. If not given, the default one will be used
     * @param postText A POSTDATA text that will be added in the request (only if the method is set to {@link #METHOD_POST})
     * @param credentials The credentials to use for authentication
     * @return A NetworkConnectionResult containing the response
     * @throws IllegalStateException
     * @throws IOException
     * @throws URISyntaxException
     * @throws RestClientException
     */
    public static NetworkConnectionResult retrieveResponseFromService(final String url, final int method, final Map<String, String> parameters,
            final ArrayList<Header> headers, final boolean isGzipEnabled, final String userAgent, final String postText,
            final UsernamePasswordCredentials credentials)
            throws IllegalStateException, IOException, URISyntaxException, RestClientException {
        return retrieveResponseFromService(url, method, parameters, headers, isGzipEnabled, userAgent, postText,
        		credentials,  new ArrayList<String>());
    }
    
    private static NetworkConnectionResult retrieveResponseFromService(final String url, final int method, final Map<String, String> parameters,
            final ArrayList<Header> headers, final boolean isGzipEnabled, final String userAgent, final String postText,
            final UsernamePasswordCredentials credentials, final ArrayList<String> previousUrlList)
            throws IllegalStateException, IOException, URISyntaxException, RestClientException {

    	final URI uri;
    	HttpContext httpContext = new BasicHttpContext();

        // Get the request URL
        if (url == null) {
            if (LogConfig.DP_ERROR_LOGS_ENABLED) {
                Log.e(LOG_TAG, "retrieveResponseFromService - Compulsory Parameter : request URL has not been set");
            }
            throw new CompulsoryParameterException("Request URL has not been set");
        }
        if (LogConfig.DP_DEBUG_LOGS_ENABLED) {
            Log.d(LOG_TAG, "retrieveResponseFromService - Request url : " + url);
        }

        // Get the request method
        if (method != METHOD_GET && method != METHOD_POST && method != METHOD_PUT && method != METHOD_DELETE) {
            if (LogConfig.DP_ERROR_LOGS_ENABLED) {
                Log.e(LOG_TAG, "retrieveResponseFromService - Request method must be METHOD_GET, METHOD_POST, METHOD_PUT or METHOD_DELETE");
            }
            throw new IllegalArgumentException(
                    "retrieveResponseFromService - Request method must be METHOD_GET, METHOD_POST, METHOD_PUT or METHOD_DELETE");
        }
        if (LogConfig.DP_DEBUG_LOGS_ENABLED) {
            Log.d(LOG_TAG, "retrieveResponseFromService - Request method : " + method);
        }

        // Get the request parameters
        if (LogConfig.DP_DEBUG_LOGS_ENABLED) {
            Log.d(LOG_TAG, "retrieveResponseFromService - Request parameters (number) : " + ((parameters != null) ? parameters.size() : ""));
        }

        // Get the request headers
        if (LogConfig.DP_DEBUG_LOGS_ENABLED) {
            Log.d(LOG_TAG, "retrieveResponseFromService - Request headers (number) : " + ((headers != null) ? headers.size() : ""));
        }

        // Create the Request
        AndroidHttpClient client = AndroidHttpClient.newInstance(userAgent != null ? userAgent : sDefaultUserAgent);
        if (LogConfig.DP_DEBUG_LOGS_ENABLED) {
            Log.d(LOG_TAG, "retrieveResponseFromService - Request user agent : " + userAgent);
        }
        
        try {
            HttpUriRequest request = null;
            switch (method) {
                case METHOD_GET:
                case METHOD_PUT:
                case METHOD_DELETE: {
                    final StringBuffer sb = new StringBuffer();
                    sb.append(url);

                    // Add the parameters to the GET url if any
                    if (parameters != null && !parameters.isEmpty()) {
                        sb.append("?");

                        final ArrayList<String> keyList = new ArrayList<String>(parameters.keySet());
                        final int keyListLength = keyList.size();

                        for (int i = 0; i < keyListLength; i++) {
                            final String key = keyList.get(i);

                            sb.append(URLEncoder.encode(key, "UTF-8"));
                            sb.append("=");
                            sb.append(URLEncoder.encode(parameters.get(key), "UTF-8"));
                            sb.append("&");
                        }
                    }

                    if (LogConfig.DP_INFO_LOGS_ENABLED) {
                        Log.i(LOG_TAG, "retrieveResponseFromService - GET Request - complete URL with parameters if any : ");
                        final String completeUrl = sb.toString();
                        int pos = 0;
                        int dumpLength = completeUrl.length();
                        while (pos < dumpLength) {
                            Log.i(LOG_TAG, completeUrl.substring(pos, Math.min(dumpLength - 1, pos + 120)));
                            pos = pos + 120;
                        }
                    }

                    uri = new URI(sb.toString());

                    if (method == METHOD_GET) {
                        request = new HttpGet(uri);
                    } else if (method == METHOD_PUT) {
                        request = new HttpPut(uri);
                    } else if (method == METHOD_DELETE) {
                        request = new HttpDelete(uri);
                    }
                    break;
                }
                case METHOD_POST: {
                    uri = new URI(url);
                    request = new HttpPost(uri);

                    // Add the parameters to the POST request if any
                    if (parameters != null && !parameters.isEmpty()) {

                        final List<NameValuePair> postRequestParameters = new ArrayList<NameValuePair>();
                        final ArrayList<String> keyList = new ArrayList<String>(parameters.keySet());
                        final int keyListLength = keyList.size();

                        for (int i = 0; i < keyListLength; i++) {
                            final String key = keyList.get(i);
                            postRequestParameters.add(new BasicNameValuePair(key, parameters.get(key)));
                        }

                        if (LogConfig.DP_INFO_LOGS_ENABLED) {
                            Log.i(LOG_TAG, "retrieveResponseFromService - POST Request - parameters list (key => value) : ");

                            final int postRequestParametersLength = postRequestParameters.size();
                            for (int i = 0; i < postRequestParametersLength; i++) {
                                final NameValuePair nameValuePair = postRequestParameters.get(i);
                                Log.i(LOG_TAG, "- " + nameValuePair.getName() + " => " + nameValuePair.getValue());
                            }
                        }

                        request.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded");
                        ((HttpPost) request).setEntity(new UrlEncodedFormEntity(postRequestParameters, "UTF-8"));
                    } else if (null != postText) { // Add post text (send xml
                                                   // for
                        // example)
                        ((HttpPost) request).setEntity(new StringEntity(postText));
                    }
                    break;
                }
                default: {
                    if (LogConfig.DP_ERROR_LOGS_ENABLED) {
                        Log.e(LOG_TAG, "retrieveResponseFromService - Request method must be METHOD_GET, METHOD_POST, METHOD_PUT or METHOD_DELETE");
                    }
                    throw new IllegalArgumentException(
                            "retrieveResponseFromService - Request method must be METHOD_GET, METHOD_POST, METHOD_PUT or METHOD_DELETE");
                }
            }

            // Activate the gzip compression if asked
            if (isGzipEnabled) {
                AndroidHttpClient.modifyRequestToAcceptGzipResponse(request);
            }

            if (LogConfig.DP_INFO_LOGS_ENABLED) {
                Log.i(LOG_TAG, "retrieveResponseFromService - Request - headers list (name => value) : ");

                final HeaderIterator iterator = request.headerIterator();
                while (iterator.hasNext()) {
                    final Header header = iterator.nextHeader();
                    Log.i(LOG_TAG, "- " + header.getName() + " => " + header.getValue());
                }
            }

            // Add the request headers if any
            if (headers != null && !headers.isEmpty()) {

                final int headersLength = headers.size();

                for (int i = 0; i < headersLength; i++) {
                    request.addHeader(headers.get(i));
                }
            }

            // Set creds if provided
            if (credentials != null) {
            	final URL urlObj = uri.toURL();
            	AuthScope scope = new AuthScope(urlObj.getHost(), urlObj.getPort());
            	CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            	credentialsProvider.setCredentials(scope, credentials);
            	httpContext.setAttribute(ClientContext.CREDS_PROVIDER, credentialsProvider);
            }
            
            // Launch the request
            String result = null;
            if (LogConfig.DP_DEBUG_LOGS_ENABLED) {
                Log.d(LOG_TAG, "retrieveResponseFromService - Executing the request");
            }
            final HttpResponse response = client.execute(request, httpContext);

            // Get the response status
            final StatusLine status = response.getStatusLine();
            if (LogConfig.DP_DEBUG_LOGS_ENABLED) {
                Log.d(LOG_TAG, "retrieveResponseFromService - Response status : " + status.getStatusCode());
            }
            final int statusCode = status.getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                if (LogConfig.DP_ERROR_LOGS_ENABLED) {
                    Log.e(LOG_TAG, "retrieveResponseFromService - Invalid response from server : " + status.toString());
                }
                if (statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
                    final Header newLocation = response.getFirstHeader("Location");
                    if (LogConfig.DP_INFO_LOGS_ENABLED) {
                        Log.i(LOG_TAG, "retrieveResponseFromService - New location : " + newLocation.getValue());
                    }
                    throw new RestClientException("New location : " + newLocation, newLocation.getValue());
                } else if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY) {
                    if (method == METHOD_GET) {
                        final String newUrl = response.getHeaders("Location")[0].getValue();
                        if (!previousUrlList.contains(newUrl)) {
                            Log.d(LOG_TAG, "retrieveResponseFromService - Url moved permanently - Trying the new url : " + newUrl);
                            previousUrlList.add(newUrl);
                            return retrieveResponseFromService(newUrl, method, parameters, headers, isGzipEnabled, userAgent, postText);
                        } else {
                            // It's an url already checked. We are in a loop. So let's throw an Exception
                            throw new RestClientException("Moved permanently - Loop detected", statusCode);
                        }
                    } else {
                        throw new RestClientException("Invalid response from server : ", statusCode);
                    }
                } else {
                    throw new RestClientException("Invalid response from server : ", statusCode);
                }
            }

            // Get the response entity
            final HttpEntity entity = response.getEntity();

            final Header contentEncoding = response.getFirstHeader("Content-Encoding");

            if (entity != null) {
                result = convertStreamToString(entity.getContent(), contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip"),
                        method, (int) entity.getContentLength());
            }

            if (LogConfig.DP_INFO_LOGS_ENABLED) {
                Log.i(LOG_TAG, "retrieveResponseFromService - Result from webservice : " + result);
            }

            return new NetworkConnectionResult(response.getAllHeaders(), result);

        } finally {
            client.close();
        }
    }

    /**
     * Transform an InputStream into a String
     * 
     * @param is InputStream
     * @return String from the InputStream
     * @throws IOException If a problem occurs while reading the InputStream
     */
    private static String convertStreamToString(final InputStream is, final boolean isGzipEnabled, final int method, final int contentLength)
            throws IOException {
        InputStream cleanedIs = is;
        if (isGzipEnabled) {
            cleanedIs = new GZIPInputStream(is);
        }

        try {
            switch (method) {
                case METHOD_GET:
                case METHOD_PUT:
                case METHOD_DELETE: {
                    final BufferedReader reader = new BufferedReader(new InputStreamReader(cleanedIs));
                    final StringBuilder sb = new StringBuilder();

                    String line = null;

                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }

                    return sb.toString();
                }
                case METHOD_POST: {
                    int i = contentLength;
                    if (i < 0) {
                        i = 4096;
                    }

                    final Reader reader = new InputStreamReader(cleanedIs);
                    final CharArrayBuffer buffer = new CharArrayBuffer(i);
                    final char[] tmp = new char[1024];
                    int l;
                    while ((l = reader.read(tmp)) != -1) {
                        buffer.append(tmp, 0, l);
                    }

                    return buffer.toString();
                }
                default:
                    return null;
            }
        } finally {
            cleanedIs.close();

            if (isGzipEnabled) {
                is.close();
            }
        }
    }
}
