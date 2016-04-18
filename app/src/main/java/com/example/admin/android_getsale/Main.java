package com.example.admin.android_getsale;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import android.app.Activity;
import android.content.Intent;
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
                String restURL = "@string/restURL";
                new RestOperation().execute(restURL);
            }
        });
    }

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
        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    public class RestOperation extends AsyncTask<String, Void, Void> {
        final HttpClient httpClient = new DefaultHttpClient();
        String content;
        String error;
        ProgressDialog progressDialog = new ProgressDialog(Main.this);
        String data = "";
        TextView txt_serverDataReceived = (TextView) findViewById(R.id.txt_serverDataReceived);
        TextView txt_showParsedJSON = (TextView) findViewById(R.id.txt_showParsedJSON);
        EditText edit_userIn = (EditText) findViewById(R.id.edit_userIn);

        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog.setTitle ("Please wait...");
            progressDialog.show();
            try {
                data += "&" + URLEncoder.encode("data","UTF-8") + "=" + edit_userIn.getText();
            } catch (UnsupportedEncodingException e){
                e.printStackTrace();
            }

        }
        protected Void doInBackground(String... params){
            BufferedReader br = null;
            URL url;
            try {
                url = new URL(params[0]);
                URLConnection connection = url.openConnection();

                OutputStreamWriter outputStream = new OutputStreamWriter(connection.getOutputStream());
                outputStream.write(data);
                outputStream.flush();

                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = br.readLine())!= null){
                    sb.append(line);
                    sb.append(System.getProperty("line.separator"));
                }
                content = sb.toString();
            } catch (MalformedURLException e){
                error = e.getMessage();
                e.printStackTrace();
            }catch (IOException e){
                error = e.getMessage();
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(Void result){
            super.onPostExecute(result);

            progressDialog.dismiss();

            if (error!=null){
                txt_serverDataReceived.setText("Error" + error);
            } else {
                txt_serverDataReceived.setText(content);

                String output = "";
                JSONObject jsonObject;

                try {
                    jsonObject = new JSONObject(content);
                    JSONArray jsonArray = jsonObject.optJSONArray("Android");
                    for (int i = 0; i<jsonArray.length(); i++){
                        JSONObject child = jsonArray.getJSONObject(i);

                        String str;
                    }
                    txt_showParsedJSON.setText(output);
                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }
    }

}


