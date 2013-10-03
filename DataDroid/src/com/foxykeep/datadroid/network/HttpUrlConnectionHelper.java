/**
 * 2013 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */
package com.foxykeep.datadroid.network;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

/**
 * Helper method to choose the best available method to connect to the given URL.
 * <p/>
 * Current implementation try to use the following methods by descending order of priority:
 * - OkHTTP from Square
 * - HttpURLConnection from Android Framework
 */
public final class HttpUrlConnectionHelper {

    public static final String OKHTTP_HTTP_CLIENT_CLASS_NAME = "com.squareup.okhttp.OkHttpClient";
    public static final String OPEN_METHOD_NAME = "open";
    public static final String SET_SSL_SOCKET_FACTORY_METHOD_NAME = "setSslSocketFactory";

    private HttpUrlConnectionHelper() {}

    /**
     * Open an URL connection using the best available method.
     *
     * @param url The URL to open.
     * @return A HttpURLConnection.
     */
    public static HttpURLConnection openUrlConnection(URL url) throws IOException,
            KeyManagementException, NoSuchAlgorithmException {
        try {
            Class<?> okHttpClientClass = Class.forName(OKHTTP_HTTP_CLIENT_CLASS_NAME);
            Method openMethod = okHttpClientClass.getDeclaredMethod(OPEN_METHOD_NAME, URL.class);

            // Following block of code is needed to fix OkHttp issue
            // See https://github.com/square/okhttp/issues/184#issuecomment-18772733 for more info
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, null, null);
            Method setSslSocketFactoryMethod = okHttpClientClass.getDeclaredMethod(
                    SET_SSL_SOCKET_FACTORY_METHOD_NAME, SSLSocketFactory.class);
            Object okHttpClientInstance = okHttpClientClass.newInstance();
            setSslSocketFactoryMethod.invoke(okHttpClientInstance, sslContext.getSocketFactory());
            // End of workaround fix

            return (HttpURLConnection) openMethod.invoke(okHttpClientInstance, url);
        } catch (InstantiationException e) {
            // Nothing to do here. Fallback to standard url.openConnection()
        } catch (IllegalAccessException e) {
            // Nothing to do here. Fallback to standard url.openConnection()
        } catch (ClassNotFoundException e) {
            // Nothing to do here. Fallback to standard url.openConnection()
        } catch (NoSuchMethodException e) {
            // Nothing to do here. Fallback to standard url.openConnection()
        } catch (IllegalArgumentException e) {
            // Nothing to do here. Fallback to standard url.openConnection()
        } catch (InvocationTargetException e) {
            // Nothing to do here. Fallback to standard url.openConnection()
        } catch (GeneralSecurityException e) {
            // Nothing to do here. Fallback to standard url.openConnection()
        }

        return (HttpURLConnection) url.openConnection();
    }
}
