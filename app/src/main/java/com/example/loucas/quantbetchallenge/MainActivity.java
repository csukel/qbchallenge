package com.example.loucas.quantbetchallenge;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;

/**
 * Created by loucas stylianou on 11/03/2015.
 */
public class MainActivity extends ActionBarActivity {
    /*this object will be linked to the corresponding text view in the xml resources*/
    private TextView txtResult;
    /*this object will be linked to the corresponding web view in the xml resources*/
    private WebView webView;
    /*this object will be linked to the corresponding button in the xml resources*/
    private Button btnSendRequest;
    private Toast m_currentToast;
    private static final String TAG = MainActivity.class.getSimpleName();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*set the layout of the ui to hte corresponding xml resource file*/
        setContentView(R.layout.activity_main);
        /*instantiate the objects which corresponds to xml resources which are included in the above layout*/
        txtResult = (TextView)findViewById(R.id.txtResult);
        webView = (WebView)findViewById(R.id.webView);
        btnSendRequest = (Button)findViewById(R.id.btnSendRequest);
        /*assign a click listener to the button*/
        btnSendRequest.setOnClickListener(btnSendOnClickListener);
        /*get the action bar and set the logo*/
        try {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setLogo(getResources().getDrawable(R.drawable.logo));
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }catch (NullPointerException nex){
            Log.e(TAG,nex.toString());
        }
    }

    //when a user clicks on the button then do...
    View.OnClickListener btnSendOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //if the device is connected to the internet ...
            if (isNetworkAvailable())
                /*trigger the http-get request which is responsible for fetching the html file of the quiz*/
                getHtml();
            else showToast("Device is not connected to the internet...",Toast.LENGTH_SHORT);
        }
    };

    /*http get request*/
    private void getHtml(){
        /*this asynchronous task triggers the get request found in the Communication class
        * AsyncTask is used for network operations because in Android network operations cannot be
        * performed anymore in the main thread(ui thread)
        */
        class SendGetReqAsyncTask extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute(){
                super.onPreExecute();
            }
            /*in backround invoke the getRequest method*/
            @Override
            protected String doInBackground(Void... params) {

                return Communication.getRequest();
            }

            /*when the async task has finishde its backgound task get the result*/
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (result!=null) {
                    //webView.loadData(result,"text/html; charset=UTF-8", null);
                    /*extract from the resulting string (htnml) the necessary data which are the two numbers*/
                    /*please note that extraction can be done in a better way using a third party library or even using
                    * the regex but due to nature of this assignment and other assignments I have to do for university I
                    * preferred to follow this naive way for extracting data from the string */
                    String strong = "<strong>";
                    int start = result.indexOf("<strong>");
                    int finish = result.indexOf("</strong>");
                    int num1 = Integer.valueOf(result.substring(start + strong.length(), finish));

                    result = result.replace(strong + num1 + "</strong>","");
                    start = result.indexOf("<strong>");
                    finish = result.indexOf("</strong>");
                    int num2 = Integer.valueOf(result.substring(start+strong.length(),finish));
                    txtResult.setText("Num 1: "+num1 + "\nNum 2: " + num2);
                    //sent the result of the gcd using http post
                    postGCD(gcd(num1, num2));
                }

            }
        }
        //check the network state and proceed if there is internet connection
        SendGetReqAsyncTask sendGetReqAsyncTask = new SendGetReqAsyncTask();
        sendGetReqAsyncTask.execute();
    }

    private void postGCD(int arg){
        class SendPostReqAsyncTask extends AsyncTask<Integer, Void, String> {

            @Override
            protected void onPreExecute(){
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Integer... params) {
                int param = params[0];
                return Communication.postGCD(String.valueOf(param));
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                JSONObject jsonObject=null;
                boolean outcome = false;

                if (result!=null) {
                    webView.loadData(result,"text/html; charset=UTF-8", null);

                }

            }
        }
        //check the network state and proceed if there is internet connection
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        txtResult.setText(txtResult.getText().toString() + "\nGCD: " + arg);
        sendPostReqAsyncTask.execute(arg);
    }

    /*calculate the greatest common divisor*/
    private int gcd(int a, int b) {
        if (b==0) return a;
        return gcd(b, a % b);
    }

    //return true when the device is connected to the internet
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //show toasts
    void showToast(String text,int toast_length)
    {
        if(m_currentToast != null)
        {
            m_currentToast.cancel();
        }
        m_currentToast = Toast.makeText(getApplicationContext(), text,toast_length);
        m_currentToast.show();

    }
}
