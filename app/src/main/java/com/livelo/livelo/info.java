package com.livelo.livelo;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcV;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static java.lang.StrictMath.round;

public class info extends AppCompatActivity {

    private NfcAdapter myNfcAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;

    private TextView data;
    private TextView tvConnect;
    private ProgressBar pbConnect;

    private FileOutputStream fileout;
    private OutputStreamWriter outputWriter;
    private String idString = "";
    public static String dataForMail;

    byte[] c; // count, number of samples
    byte[] p; // sampling period
    int count = 0; //number of samples
    float periodInMin = 0; //period of sampling in minutes
    int nbBlocksToRead = Sensor.NbOfSamplesGlobal/1024+1;

    Tag detectedTag;
    NfcV nfcv;


    JSONObject sensor;
    public JSONArray data_array;

    byte[] id;
    private int k = 0;
    byte readCommand[] = new byte[]{0x00, 0x21, (byte) 0, 0x01, 0x00, 0x20, 0x03, 0x01, 0x01, 0x00, 0x00};
    byte[] buffer;// buffer containing the data
    int blockCount = 1;
    String fileName;

    private Intent intentNFC;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Info");
        setContentView(R.layout.activity_info);

        data = (TextView) findViewById(R.id.data);
        tvConnect = (TextView) findViewById(R.id.tvConnect);
        pbConnect = (ProgressBar) findViewById(R.id.pbConnect);
        myNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        data.setText("");

        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter nfcv = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        mFilters = new IntentFilter[]{
                nfcv,
        };
        mTechLists = new String[][]{new String[]{NfcV.class.getName()},
                new String[]{NdefFormatable.class.getName()}};

        data.setVisibility(View.INVISIBLE);
        tvConnect.setVisibility(View.VISIBLE);
        pbConnect.setVisibility(View.VISIBLE);

        data_array = new JSONArray();
        dataForMail = "";

    }


    @Override
    public void onNewIntent(Intent intent) {
        data.setVisibility(View.VISIBLE);
        tvConnect.setVisibility(View.INVISIBLE);
        pbConnect.setVisibility(View.INVISIBLE);

        Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        nfcv = NfcV.get(detectedTag);

        idString = getId();

        //Checker if the sensor exists
        if(fileExists(idString + ".json")) {
            try {
                sensor = new JSONObject(txtToString(idString + ".json"));
                displayInformation();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            data.setText("Unknown sensor");
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        if (myNfcAdapter != null)
            myNfcAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
    }

    @Override
    public void onPause() {
        super.onPause();
        myNfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void readSamplingFreq() {
        try {
            p = nfcv.transceive(new byte[]{0x00, 0x20, 0x03}); //read block 3 from FRAM
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Error7", Toast.LENGTH_SHORT).show();
        }
        int period = ((p[4] & 0xff) << 24) | ((p[3] & 0xff) << 16) | ((p[2] & 0xff) << 8) | (p[1] & 0xff);//Warning: order of bytes inversed!
        periodInMin = (float) period / 60000; //period in minutes
        return;
    }

    public void displayInformation(){
        try {
            data.setText("id : " + sensor.getString("id")
                    + "\nSensor name : " + sensor.getString("sensor_name")
                    + "\nName : " + sensor.getString("first_name")
                    + "\nLast name : " + sensor.getString("last_name")
                    + "\nCompany : " + sensor.getString("org")
                    + "\nLocation : " + sensor.getString("locality")
                    + "\nType : " + sensor.getString("type")
                    + "\nComment : " + sensor.getString("comment")
                    //+ "\nOpen source : " + sensor.getBoolean("open_data")
                    //+ "\nWorking since : " + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(sensor.getLong("set_up_time") * 1000))
                    //+ "\nLast collect time : " + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(sensor.getLong("last_collect_time") * 1000))
                    //+ "\nNumber of Samples since last time: " + Sensor.NbOfSamplesGlobal
                    //+ "\nSampling period (in minutes): " + periodInMin
                    + "\n");

            //Check last collect time
            String fDateLastCollect = "never"; // shouldn't append if an id is an id is assigned
            if (Sensor.last_collect_time != 0) {
                Date last_collect = new Date(Sensor.last_collect_time);
                fDateLastCollect = new SimpleDateFormat("dd-MM-yyyy").format(last_collect);
            }

            String fDateStartTime = "never"; // shouldn't append if an id is an id is assigned
            if (Sensor.start_time != 0) {
                Date start_time = new Date(Sensor.start_time);
                fDateStartTime = new SimpleDateFormat("dd-MM-yyyy").format(start_time);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean fileExists(String s){
        try {
            FileInputStream fileIn = openFileInput(s);
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

    public String getId(){
        StringBuilder idStr = new StringBuilder();
        byte id[] = {0};

        try {
            nfcv.connect();
            if (nfcv.isConnected()) {
                id = nfcv.transceive(new byte[]{0x00, 0x2B});

                //TODO measure the battery level here
                nfcv.close();
            }
        } catch (IOException e) {
        }
        //for (int i = 2; i < id.length-2; i++) {//
        for (int i = id.length-3; i > 1; i--) {//
            String hex = Integer.toHexString(0xFF & id[i]);
            if (hex.length() == 1) {//if string is empty
                idStr.append('0');
            }
            idStr.append(hex);
        }
        return idStr.toString();
    }

}



