package com.livelo.livelo;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NfcAdapter myNfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        myNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        getSupportActionBar().setTitle("Livelo");
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

    public void goto_info(View view) {
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
        Intent intent = new Intent(this, info.class);
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

    public void goto_new_sensor(View view) {
        myNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (myNfcAdapter == null) {
            Toast.makeText(getBaseContext(), "NFC is not available for the device", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, new_sensor.class);
        startActivity(intent);
    }

    public void goto_sensors(View view) {

        //Intent intent = new Intent(this, sensors.class);
        //startActivity(intent);
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
            System.exit(0);

            //super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
            new MainActivity.LongOperation().execute("");
            //TODO check the internet connection
            Toast.makeText(getApplicationContext(), "sending data", Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

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
                    //http://beta.thinkee.ch:10102/livelo
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
