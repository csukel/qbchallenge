package com.example.loucas.quantbetchallenge;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

/**
 * Created by loucas stylianou on 11/03/2015.
 * This class is responsible for network operations (http get and post requests)
 */
public class Communication {
    /*initialize the url for the quiz*/
    private static final String getUrl = "http://www.quantbet.com/quiz";
    /*initialize the url which will be used to send the gcd result*/
    private static final String postUrl = "http://www.quantbet.com/submit";
    /*initialize the httpClient object which will be responsible for keeping the connection alive between the mobile app and the
    * website
    */
    private static DefaultHttpClient httpClient = new DefaultHttpClient();
    /*this class should not be instantiated*/
    private Communication(){}

    /*this method implements the http get request which fetches the html doc for the quiz*/
    public static String getRequest(){
        String result = null;
        HttpGet httpGet = new HttpGet(getUrl);
        try {
            // HttpResponse is an interface just like HttpPost.
            //Therefore we can't initialize them
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity entity = httpResponse.getEntity();
            result = EntityUtils.toString(entity);

        } catch (ClientProtocolException cpe) {
            System.out.println("First Exception caz of HttpResponese :" + cpe);
            cpe.printStackTrace();
        } catch (IOException ioe) {
            System.out.println("Second Exception caz of HttpResponse :" + ioe);
            ioe.printStackTrace();
        }

        return result;
    }

    /*this method implements the http post request to the website*/
    public static String postGCD(String gcd){
        String result = null;
        HttpPost httpPost = new HttpPost(postUrl);
        /*initialise a json object*/
        JSONObject jsonObject = new JSONObject();
        try {
            /*store the gcd result to the json object using as key value the divisor (input field unique name in the quiz html page)*/
            jsonObject.put("divisor",gcd);
            /*set the corresponding header for the post request to use json*/
            httpPost.setHeader("Content-type", "application/json");
            StringEntity se = new StringEntity(jsonObject.toString());
            se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httpPost.setEntity(se);
            // HttpResponse is an interface just like HttpPost.
            //Therefore we can't initialize them
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity entity = httpResponse.getEntity();
            result = EntityUtils.toString(entity);

        } catch (ClientProtocolException cpe) {
            System.out.println("First Exception caz of HttpResponese :" + cpe);
            cpe.printStackTrace();
        } catch (IOException ioe) {
            System.out.println("Second Exception caz of HttpResponse :" + ioe);
            ioe.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }
}
