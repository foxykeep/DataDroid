/**
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */
package com.foxykeep.datadroid.network;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

public class HttpUrlConnectionFactory {
  private HttpUrlConnectionFactory(){}
  
  public static HttpURLConnection openUrlConnection(URL url) throws IOException{
    try {
      Class<?> okHttpClientClass = Class.forName("com.squareup.okhttp.OkHttpClient");
      
      java.lang.reflect.Method openMethod = okHttpClientClass.getDeclaredMethod("open", URL.class);
      
      setPrivateSSLSocketFactory(okHttpClientClass);
      
      return (HttpURLConnection) openMethod.invoke(okHttpClientClass.newInstance(), url);
    } catch (InstantiationException e) {
    } catch (IllegalAccessException e) {
    } catch (ClassNotFoundException e) {
    } catch (NoSuchMethodException e) {
    } catch (IllegalArgumentException e) {
    } catch (InvocationTargetException e) {
    } catch (GeneralSecurityException e) {
    }
    
    return (HttpURLConnection) url.openConnection();
  }

  /**
   * Fixes OkHttp issue https://github.com/square/okhttp/issues/184#issuecomment-18772733
   * 
   * @param okHttpClientClass
   * @throws NoSuchAlgorithmException
   * @throws KeyManagementException
   * @throws NoSuchMethodException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   */
  private static void setPrivateSSLSocketFactory(Class<?> okHttpClientClass) throws NoSuchAlgorithmException,
      KeyManagementException, NoSuchMethodException, InstantiationException, IllegalAccessException,
      InvocationTargetException {
    SSLContext sslContext = SSLContext.getInstance("TLS");
    sslContext.init(null, null, null);

    java.lang.reflect.Method setSslSocketFactoryMethod = okHttpClientClass.getDeclaredMethod("setSslSocketFactory", SSLSocketFactory.class);
    
    Object okHttpClientInstance = okHttpClientClass.newInstance();
    
    setSslSocketFactoryMethod.invoke(okHttpClientInstance, sslContext.getSocketFactory());
  }
}
