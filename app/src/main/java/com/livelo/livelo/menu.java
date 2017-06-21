package com.livelo.livelo;

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

        send_data();
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

    public void send_data() {
        // get the string of the json
        String s = "";
        try {
            FileInputStream fileIn = openFileInput(Sensor.logFile);
            InputStreamReader InputRead = new InputStreamReader(fileIn);

            char[] inputBuffer = new char[100];
            int charRead;

            while ((charRead = InputRead.read(inputBuffer)) > 0) {
                // char to string conversion
                String readstring = String.copyValueOf(inputBuffer, 0, charRead);
                s += readstring;
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }
            InputRead.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, String> postData = new HashMap<>();
        try {
            postData = new JSONObject(s);
        } catch (Exception e) {
            e.printStackTrace();
        }

        postData.put("anotherParam", anotherParam);
        HttpPostAsyncTask task = new HttpPostAsyncTask(postData);
        task.execute(baseUrl + "/some/path/goes/here");

        try {
            String urlParameters = "Hello";
            byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
            int postDataLength = postData.length;
            //String request        = "http://192.168.43.176:10101/ThinkEE/http/livelo";
            String request = "http://httpbin.org/post";
            URL url = new URL(request);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            conn.setUseCaches(false);
            conn.connect();
            try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                wr.write(postData);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "pas de connexion", Toast.LENGTH_SHORT).show();
        }


    }

    public class HttpPostAsyncTask extends AsyncTask<String, Void, Void> {

        // This is the JSON body of the post
        JSONObject postData;

        // This is a constructor that allows you to pass in the JSON body
        public HttpPostAsyncTask(Map<String, String> postData) {
            if (postData != null) {
                this.postData = new JSONObject(postData);
            }
        }

        // This is a function that we are overriding from AsyncTask. It takes Strings as parameters because that is what we defined for the parameters of our async task
        @Override
        protected Void doInBackground(String... params) {

            try {
                // This is getting the url from the string we passed in
                URL url = new URL(params[0]);

                // Create the urlConnection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();


                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                urlConnection.setRequestProperty("Content-Type", "application/json");

                urlConnection.setRequestMethod("POST");


                // OPTIONAL - Sets an authorization header
                urlConnection.setRequestProperty("Authorization", "someAuthString");

                // Send the post body
                if (this.postData != null) {
                    OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                    writer.write(postData.toString());
                    writer.flush();
                }

                int statusCode = urlConnection.getResponseCode();

                if (statusCode == 200) {

                    InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());

                    String response = convertInputStreamToString(inputStream);

                    // From here you can convert the string to JSON with whatever JSON parser you like to use

                    // After converting the string to JSON, I call my custom callback. You can follow this process too, or you can implement the onPostExecute(Result) method

                } else {
                    // Status code is not 200
                    // Do something to handle the error
                }

            } catch (Exception e) {
                Log.d(TAG, e.getLocalizedMessage());
            }
            return null;
        }
    }
}
