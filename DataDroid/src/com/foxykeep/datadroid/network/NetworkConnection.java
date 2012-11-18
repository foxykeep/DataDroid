/**
 * 2012 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package com.foxykeep.datadroid.network;

import android.content.Context;
import android.util.Log;

import com.foxykeep.datadroid.config.LogConfig;
import com.foxykeep.datadroid.exception.ConnectionException;
import com.foxykeep.datadroid.internal.network.NetworkConnectionImpl;

import org.apache.http.auth.UsernamePasswordCredentials;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class gives the user an API to easily call a webservice and return the received response.
 * <p>
 * Use the {@link Builder} to prepare your webservice call.
 *
 * @author Foxykeep
 */
public final class NetworkConnection {

    private static final String LOG_TAG = NetworkConnection.class.getSimpleName();

    private NetworkConnection() {
        // No public constructor
    }

    public static enum Method {
        GET, POST, PUT, DELETE
    }

    /**
     * The result of a webservice call.
     * <p>
     * Contains the headers and the body of the response as an unparsed <code>String</code>.
     *
     * @author Foxykeep
     */
    public static final class ConnectionResult {

        public Map<String, List<String>> headerMap;
        public String body;

        public ConnectionResult(Map<String, List<String>> headerMap, String body) {
            this.headerMap = headerMap;
            this.body = body;
        }
    }

    /**
     * Builder used to create a {@link NetworkConnection}.
     *
     * @author Foxykeep
     */
    public static final class Builder {
        private final Context mContext;
        private final String mUrl;
        private Method mMethod = Method.GET;
        private HashMap<String, String> mParameterMap = null;
        private HashMap<String, String> mHeaderMap = null;
        private boolean mIsGzipEnabled = true;
        private String mUserAgent = null;
        private String mPostText = null;
        private UsernamePasswordCredentials mCredentials = null;
        private boolean mIsSslValidationDisabled = false;

        /**
         * Get a {@link Builder} to create a {@link NetworkConnection}
         *
         * @param context The context used by the {@link NetworkConnection}. Used to create the
         *            User-Agent.
         * @param url The URL to call.
         */
        public Builder(Context context, String url) {
            if (url == null) {
                if (LogConfig.DD_ERROR_LOGS_ENABLED) {
                    Log.e(LOG_TAG, "NetworkConnectionBuilder - request URL cannot be null.");
                }
                throw new NullPointerException("Request URL has not been set.");
            }
            mContext = context;
            mUrl = url;
        }

        /**
         * Set the method to use. Default is {@link Method#GET}.
         * <p>
         * If set to another value than {@link Method#POST}, the POSTDATA text will be reset as it
         * can only be used with a POST request.
         *
         * @param method The method to use.
         * @return The builder.
         */
        public Builder setMethod(Method method) {
            mMethod = method;
            if (method != Method.POST) {
                mPostText = null;
            }
            return this;
        }

        /**
         * Set the parameters to add to the request. This is meant to be a "key" => "value" Map.
         * <p>
         * The POSTDATA text will be reset as they cannot be used at the same time.
         *
         * @see #setPostText(String)
         * @param parameterMap The parameters to add to the request.
         * @return The builder.
         */
        public Builder setParameters(HashMap<String, String> parameterMap) {
            mParameterMap = parameterMap;
            mPostText = null;
            return this;
        }

        /**
         * Set the headers to add to the request.
         *
         * @param headerList The headers to add to the request.
         * @return The builder.
         */
        public Builder setHeaderList(HashMap<String, String> headerMap) {
            mHeaderMap = headerMap;
            return this;
        }

        /**
         * Set whether the request will use gzip compression if available on the server. Default is
         * true.
         *
         * @param isGzipEnabled Whether the request will user gzip compression if available on the
         *            server.
         * @return The builder.
         */
        public Builder setGzipEnabled(boolean isGzipEnabled) {
            mIsGzipEnabled = isGzipEnabled;
            return this;
        }

        /**
         * Set the user agent to set in the request. Otherwise a default Android one will be used.
         *
         * @param userAgent The user agent.
         * @return The builder.
         */
        public Builder setUserAgent(String userAgent) {
            mUserAgent = userAgent;
            return this;
        }

        /**
         * Set the POSTDATA text that will be added in the request. Also automatically set the
         * {@link Method} to {@link Method#POST} to be able to use it.
         * <p>
         * The parameters will be reset as they cannot be used at the same time.
         *
         * @see #setParameters(HashMap)
         * @param postText The POSTDATA text that will be added in the request.
         * @return The builder.
         */
        public Builder setPostText(String postText) {
            mPostText = postText;
            mMethod = Method.POST;
            mParameterMap = null;
            return this;
        }

        // TODO check http://bit.ly/T7lZEm for implementation code.
        /**
         * Set the credentials to use for authentication.
         *
         * @param credentials The credentials to use for authentication.
         * @return The builder.
         */
        public Builder setCredentials(UsernamePasswordCredentials credentials) {
            mCredentials = credentials;
            return this;
        }

        // TODO check http://bit.ly/XgZpYg for implementation code.
        /**
         * Set whether the SSL certificates validation are disabled. Default is false.
         *
         * @param enabled Whether the SSL certificates validation are disabled.
         * @return The Builder.
         */
        public Builder setSslValidationDisabled(boolean disabled) {
            mIsSslValidationDisabled = disabled;
            return this;
        }

        // TODO add the exceptions
        /**
         * Execute the webservice call and return the {@link ConnectionResult}.
         *
         * @return The result of the webservice call.
         */
        public ConnectionResult execute() throws ConnectionException {
            return NetworkConnectionImpl.execute(mContext, mUrl, mMethod, mParameterMap,
                    mHeaderMap, mIsGzipEnabled, mUserAgent, mPostText, mCredentials,
                    mIsSslValidationDisabled);
        }
    }
}
