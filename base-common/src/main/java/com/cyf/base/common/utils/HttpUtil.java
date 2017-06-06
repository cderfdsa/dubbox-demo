package com.cyf.base.common.utils;

import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * Created by chenyf on 2017/3/25.
 */
public class HttpUtil {
    private static PoolingHttpClientConnectionManager cm;
    private static String EMPTY_STR = "";
    private static String UTF_8 = "UTF-8";

    private static LoggerWrapper logger = LoggerWrapper.getLoggerWrapper(HttpUtil.class);

    public static String getUrl(String host, String path){
        String url = StringUtil.trim(host, "/") + "/" + StringUtil.trim(path, "/");
        if(! url.startsWith("http://") && ! url.startsWith("https://")){
            url = "http://" + url;
        }
        return url;
    }

    public static String encode(String url, Map<String, ?> queryParam){
        try {
            URIBuilder uBuilder = new URIBuilder(url);
            if(queryParam != null && !queryParam.isEmpty()){
                for(Map.Entry<String, ?> entry : queryParam.entrySet()){
                    uBuilder.setParameter(entry.getKey(), entry.getValue().toString());
                }
            }

            URI uri = uBuilder.build();
            return uri.toString();
        } catch (Exception e) {
            logger.error(e);
            return url;
        }
    }

    /**
     *
     * @param url
     * @return
     */
    public static String get(String url){
        HttpGet httpGet = new HttpGet(url);
        return getResult(httpGet);
    }

    public static String get(String url, Map<String, Object> params){
        try{
            url = encode(url, params);

            HttpGet httpGet = new HttpGet(url);
            return getResult(httpGet);
        }catch(Exception e){
            logger.error(e);
            return EMPTY_STR;
        }
    }

    public static String get(String url, Map<String, String> headers, Map<String, Object> params) {
        try{
            URIBuilder ub = new URIBuilder(url);

            if(params != null && ! params.isEmpty()){
                ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
                ub.setParameters(pairs);
            }

            HttpGet httpGet = new HttpGet(ub.build());
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpGet.addHeader(entry.getKey(), entry.getValue());
            }
            return getResult(httpGet);
        }catch(Exception e){
            logger.error(e);
            return EMPTY_STR;
        }
    }

    public static String getJson(String url){
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-type", "application/json");
        headers.put("Accept", "application/json");
        return get(url, headers, null);
    }

    public static String getJson(String url, Map<String, Object> params){
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-type", "application/json");
        headers.put("Accept", "application/json");
        return get(url, headers, params);
    }

    public static String post(String url) {
        HttpPost httpPost = new HttpPost(url);
        return getResult(httpPost);
    }

    public static String post(String url, Map<String, Object> params){
        HttpPost httpPost = new HttpPost(url);
        ArrayList<NameValuePair> pairs = covertParams2NVPS(params);

        try{
            httpPost.setEntity(new UrlEncodedFormEntity(pairs, UTF_8));
            return getResult(httpPost);
        }catch(Exception e){
            logger.error(e);
            return EMPTY_STR;
        }
    }

    public static String post(String url, Map<String, Object> headers, Map<String, Object> params){
        HttpPost httpPost = new HttpPost(url);

        for (Map.Entry<String, Object> param : headers.entrySet()) {
            httpPost.addHeader(param.getKey(), String.valueOf(param.getValue()));
        }

        try{
            ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
            httpPost.setEntity(new UrlEncodedFormEntity(pairs, UTF_8));
            return getResult(httpPost);
        }catch(Exception e){
            logger.error(e);
            return EMPTY_STR;
        }

    }

    private static ArrayList<NameValuePair> covertParams2NVPS(Map<String, Object> params) {
        ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            pairs.add(new BasicNameValuePair(param.getKey(), String.valueOf(param.getValue())));
        }
        return pairs;
    }

    /**
     * 处理Http请求
     *
     * @param request
     * @return
     */
    private static String getResult(HttpRequestBase request) {
        CloseableHttpClient httpClient = getHttpClient();
        try {
            CloseableHttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String result = EntityUtils.toString(entity);
                response.close();
                return result;
            }
        } catch (ClientProtocolException e) {
            logger.error(e);
        } catch (IOException e) {
            logger.error(e);
        } finally {
        }
        return EMPTY_STR;
    }

    /**
     * 通过连接池获取HttpClient
     * @return
     */
    private static CloseableHttpClient getHttpClient() {
        init();
        return HttpClients.custom().setConnectionManager(cm).build();
    }

    /**
     * 初始化
     */
    private static void init() {
        if (cm == null) {
            synchronized (HttpUtil.class){
                if(cm == null){//double check
                    cm = new PoolingHttpClientConnectionManager();
                    cm.setMaxTotal(50);// 整个连接池最大连接数
                    cm.setDefaultMaxPerRoute(5);// 每路由最大连接数，默认值是2
                }
            }
        }
    }
}
