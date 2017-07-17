package com.livelo.livelo;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import static android.content.ContentValues.TAG;


public class menu extends AppCompatActivity {
    TextView txt = null;
    private NfcAdapter myNfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_menu);
        txt = (TextView) findViewById(R.id.last_upload);
        myNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        refresh_time();
    }


    public void goto_sensor(View view) {
        Intent intent = new Intent(this, sensors.class);
        startActivity(intent);
    }

    public void goto_collect(View view) {
        if (myNfcAdapter == null) {
            Toast.makeText(getBaseContext(), "NFC is not available for the device", Toast.LENGTH_SHORT).show();
            return;
        } else if (!myNfcAdapter.isEnabled()) {
            Toast.makeText(getBaseContext(), "You should turn NFC on before", Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
                startActivity(intent);
            } else {
                Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                startActivity(intent);
            }
            return;
        }

        Intent intent = new Intent(this, collect_data.class);
        startActivity(intent);
    }

    public void goto_help(View view) {
        Intent intent = new Intent(this, help.class);
        startActivity(intent);
    }

    public void goto_settings(View view) {
        myNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (myNfcAdapter == null) {
            Toast.makeText(getBaseContext(), "NFC is not available for the device", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, settings.class);
        startActivity(intent);
    }

    public void goto_reset(View view) {
        if (myNfcAdapter == null) {
            Toast.makeText(getBaseContext(), "NFC is not available for the device", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!myNfcAdapter.isEnabled()) {
            Toast.makeText(getBaseContext(), "You should turn NFC on before", Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
                startActivity(intent);
            } else {
                Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                startActivity(intent);
            }
            return;
        }
        Intent intent = new Intent(this, reset.class);
        startActivity(intent);
    }

    public void send_data(View view) {
        Toast.makeText(getApplicationContext(), "sending data", Toast.LENGTH_SHORT).show();

        //new LongOperation().execute("");
        return;

        // TODO if uploading succeed
        /*Calendar now = Calendar.getInstance();
        Global.last_upload = now.getTimeInMillis();
        refresh_time();

        //Send a mail with data
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"nicolas.li@hotmail.fr","denis.gillet@epfl.ch"});
        i.putExtra(Intent.EXTRA_SUBJECT, "Pressure data");
        i.putExtra(Intent.EXTRA_TEXT   , collect_data.dataForMail);
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(menu.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }*/
    }

    void refresh_time() {
        if (Global.last_upload == 0) {
            txt.setText("last uploading: never");
            return;
        }

        Calendar now = Calendar.getInstance();
        long diff = now.getTimeInMillis() - Global.last_upload;
        long days = diff / (1000); //24*60*60;
        txt.setText("last uploading: " + (days == 0 ? ("today") : (days == 1 ? "one day ago" : days + " days ago")));
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    /*private class LongOperation extends AsyncTask<String, Void, String> {
/*
        @Override
        protected String doInBackground(String... params) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://localhost/gcg/insert.php");

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("id", "12345"));
                nameValuePairs.add(new BasicNameValuePair("stringdata", "AndDev is Cool!"));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            // TextView txt = (TextView) findViewById(R.id.output);
            // txt.setText("Executed"); // txt.setText(result);
            // Toast.makeText(getApplicationContext(),
            // et.getText().toString(),Toast.LENGTH_SHORT).show();
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }*/

    private class MyAsyncTask extends AsyncTask<String, Integer, Double>{

        @Override
        protected Double doInBackground(String... params) {
            // TODO Auto-generated method stub
            postData(params[0]);
            return null;
        }

        protected void onPostExecute(Double result){
            // pb.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "command sent", Toast.LENGTH_LONG).show();
        }
        protected void onProgressUpdate(Integer... progress){
            // pb.setProgress(progress[0]);
        }

        public void postData(String valueIWantToSend) {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://somewebsite.com/receiver.php");

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("myHttpData", valueIWantToSend));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }
        }

    }
}
