package com.livelo.livelo;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


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

import org.json.JSONArray;
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
        String s = txtToString("log_files.json");//a corriger
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();

        JSONArray log_files = new JSONArray();

        if(fileExists("log_files.json")) {

            try {
                log_files = new JSONArray(s);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String logFile = "";
        // get the name of the logfile
        try {
            logFile = log_files.get(0).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        txtToString(logFile);


        //String logFile = "aésldkfjéldsf";



        //Toast.makeText(getBaseContext(), log_files[1], Toast.LENGTH_SHORT).show();

        //Intent intent = new Intent(this, help.class);
        //startActivity(intent);
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

    public void goto_new_sensor(View view) {
        myNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (myNfcAdapter == null) {
            Toast.makeText(getBaseContext(), "NFC is not available for the device", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, new_sensor.class);
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
        new LongOperation().execute("");

        //TODO check the internet connection
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

    private class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            // ouvre la liste des logfiles pour les envoyer

            //////////////////////////////////////////////
            String s = txtToString("log_files.json");//a corriger

            JSONArray log_files = new JSONArray();
            try {
                log_files = new JSONArray(s);
            } catch (Exception e) {
                e.printStackTrace();
            }

            /////////////////////////////////////////////
            //String s = "";
            //JSONArray log_files = new JSONArray();

            if(fileExists("log_files.json")) {
                s = txtToString("log_files.json");//a corriger
                try {
                    log_files = new JSONArray(s);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }// TODO else?

            // for each log file, send it online
            int nbFilesToSend = log_files.length();
            int n;
            for ( n = nbFilesToSend-1; n>=0  ; n--) {
                String logFile = "";
                // get the name of the logfile
                try {
                    logFile = log_files.get(n).toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //String logFile = "aésldkfjéldsf";
                URL url = null;
                HttpURLConnection connection;
                OutputStreamWriter request = null;
                String response = null;

                // load the logfile into the parameters string
                String parameters = "";
                parameters = txtToString(logFile);

                try {
                    url = new URL(" http://posttestserver.com/post.php?dir=livelo");
                    //url = new URL("http://beta.thinkee.ch:10102/livelo");
                    http://beta.thinkee.ch:10102/livelo
                    connection = (HttpURLConnection) url.openConnection();
                    //connection.setDoOutput(true); // on sait pas a quoi ça sert , false default
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    connection.setRequestMethod("POST");

                    request = new OutputStreamWriter(connection.getOutputStream());
                    request.write(parameters);
                    request.flush();
                    request.close();
                    String line = "";
                    InputStreamReader isr = new InputStreamReader(connection.getInputStream());
                    BufferedReader reader = new BufferedReader(isr);
                    StringBuilder sb = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    // Response from server after login process will be stored in response variable.
                    response = sb.toString();
                    // You can perform UI operations here

                    isr.close();
                    reader.close();

                    File file = new File(logFile);
                    file.delete();

                } catch (IOException e) {
                    try {
                        FileOutputStream fileout = openFileOutput("log_files.json", MODE_PRIVATE);
                        OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
                        outputWriter.write(log_files.toString());
                        outputWriter.close();
                    } catch (Exception f) {
                        f.printStackTrace();
                    }
                }
                try {
                    FileOutputStream fileout = openFileOutput("log_files.json", MODE_PRIVATE);
                    OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
                    outputWriter.write(log_files.toString());
                    outputWriter.close();
                } catch (Exception f) {
                    f.printStackTrace();
                }

                // remove the logfile from the logfiles list
                log_files.remove(n);
            }
            return String.valueOf(nbFilesToSend - n - 1) + " over " + String.valueOf(nbFilesToSend) + " files sent";
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is up to you
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    String txtToString(String file){
        String output = "";
        try {
            FileInputStream fileIn = openFileInput(file);
            InputStreamReader InputRead = new InputStreamReader(fileIn);
            char[] inputBuffer = new char[100];
            int charRead;
            while ((charRead = InputRead.read(inputBuffer)) > 0) {
                // char to string conversion
                String readstring = String.copyValueOf(inputBuffer, 0, charRead);
                output += readstring;
            }
            InputRead.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }

    public boolean fileExists(String name){
        try {
            FileInputStream fileIn = openFileInput(name);
            InputStreamReader InputRead = new InputStreamReader(fileIn);
            int c = InputRead.read();

            if(c == -1) {
                InputRead.close();
                return false;
            }
            else{
                InputRead.close();
                return true;
            }

        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}
