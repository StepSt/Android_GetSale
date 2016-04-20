package com.example.admin.android_getsale;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ProgressDialog;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class Main extends Activity implements OnClickListener {

    private Button scanBtn;
    private TextView formatTxt, contentTxt;
    private ProgressDialog pDialog;
    String url = "http://194.87.144.52:1869/Service1.svc/GetSum?id=123456&token=e10adc3949ba59abbe56e057f20f883e";
    String Sale_User, Fond_User, Publicity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scanBtn = (Button)findViewById(R.id.scan_button);
        formatTxt = (TextView)findViewById(R.id.scan_format);
        contentTxt = (TextView)findViewById(R.id.scan_content);
        scanBtn.setOnClickListener(this);

        Button getData = (Button) findViewById(R.id.button_getData);
        getData.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                new GetContacts().execute();

            }
        });
    }
    //region void QT
    public void onClick(View v){
        if(v.getId()==R.id.scan_button){
            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            scanIntegrator.initiateScan();
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();

            formatTxt.setText("FORMAT: " + scanFormat);
            contentTxt.setText("CONTENT: " + scanContent);
            url = "http://194.87.144.52:1869/Service1.svc/GetSum?id=" + scanContent +"&token=e10adc3949ba59abbe56e057f20f883e";
        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }

    }
    //endregion

    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Main.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    String response = jsonObj.getString("GetSumResult");
                    JSONObject jsonObject = new JSONObject(response);

                    // Getting JSON Array node
                    Sale_User = jsonObject.getString("Sale_User");
                    Fond_User = jsonObject.getString("Fond_User");
                    Publicity = jsonObject.getString("Publicity");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            TextView Sale_User_textview = (TextView) findViewById(R.id.txt_Sale_User);
            TextView Fond_User_textview = (TextView) findViewById(R.id.txt_Fond_User);
            TextView Publicity_textview = (TextView) findViewById(R.id.text_Publicity);

            Sale_User_textview.setText(Sale_User);
            Fond_User_textview.setText(Fond_User);
            Publicity_textview.setText(Publicity);


        }

    }

}


