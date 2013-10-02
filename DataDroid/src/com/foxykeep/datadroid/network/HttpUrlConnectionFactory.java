package com.foxykeep.datadroid.network;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUrlConnectionFactory {
  private HttpUrlConnectionFactory(){}
  
  public static HttpURLConnection openUrlConnection(URL url) throws IOException{
    try {
      Class<?> okHttpClientClass = Class.forName("com.squareup.okhttp.OkHttpClient");
      
      java.lang.reflect.Method openMethod = okHttpClientClass.getDeclaredMethod("open", URL.class);
      
      return (HttpURLConnection) openMethod.invoke(okHttpClientClass.newInstance(), url);
    } catch (InstantiationException e) {
    } catch (IllegalAccessException e) {
    } catch (ClassNotFoundException e) {
    } catch (NoSuchMethodException e) {
    } catch (IllegalArgumentException e) {
    } catch (InvocationTargetException e) {
    }
    
    return (HttpURLConnection) url.openConnection();
  }
}
