package com.livelo.livelo;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import static java.util.Arrays.copyOf;

public class new_sensor extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    // private com.google.android.gms.common.api.GoogleApiClient client;
    private NfcAdapter myNfcAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;

    String idString = "";

    File file;

    private EditText editSensorName;
    private EditText editName;
    private EditText editLastName;
    private EditText editOrg;
    private EditText editDepth;
    private EditText editSensorDepth;
    private EditText editLocality;
    private EditText editType;
    private EditText editComment;
    private CheckBox checkBoxOpenData;
    private ProgressBar progressBarWaitNewSensor;
    private ScrollView ScrollNewSensor;
    private TextView tvWaitNewSensor;

    private FileOutputStream fileout;
    private OutputStreamWriter outputWriter;

    /////////////////////////Sensors features//////////////////
    public static float lat = 0;
    public static float lng = 0;
    public static float alt = 0;
    public static String locality = "";
    public static float battery = 0;
    public static float depth = 0;
    public static float sensorDepth = 0;
    public static String org = "";
    public static String sensor_name = "";
    public static String first_name = "";
    public static String last_name = "";
    public static String type = "";
    public static String comment = "";
    public static boolean open_source = true;

    NfcV nfcv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("New sensor");
        setContentView(R.layout.activity_new_sensor);

        myNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        editSensorName = (EditText) findViewById(R.id.editSensorName);
        editName = (EditText) findViewById(R.id.editName);
        editLastName = (EditText) findViewById(R.id.editLastName);
        editOrg = (EditText) findViewById(R.id.editOrg);
        editDepth = (EditText) findViewById(R.id.editDepth);
        editSensorDepth = (EditText) findViewById(R.id.editSensorDepth);
        editLocality = (EditText) findViewById(R.id.editLocality);
        editType = (EditText) findViewById(R.id.editType);
        editComment = (EditText) findViewById(R.id.editComment);
        checkBoxOpenData = (CheckBox) findViewById(R.id.checkBoxOpenData);
        progressBarWaitNewSensor = (ProgressBar) findViewById(R.id.progressBarWaitNewSensor);
        ScrollNewSensor = (ScrollView) findViewById(R.id.ScrollNewSensor);
        tvWaitNewSensor = (TextView) findViewById(R.id.tvWaitNewSensor);
        ScrollNewSensor.setVisibility(View.VISIBLE);
        progressBarWaitNewSensor.setVisibility(View.INVISIBLE);
        tvWaitNewSensor.setVisibility(View.INVISIBLE);

        if (!myNfcAdapter.isEnabled()) {
            Toast toast = Toast.makeText(getApplicationContext(), "You should turn NFC on before", Toast.LENGTH_SHORT);
            toast.show();
        }


        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter nfcv = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        mFilters = new IntentFilter[]{
                nfcv,
        };
        mTechLists = new String[][]{new String[]{NfcV.class.getName()},
                new String[]{NdefFormatable.class.getName()}};

        ScrollNewSensor.setVisibility(View.INVISIBLE);
        progressBarWaitNewSensor.setVisibility(View.VISIBLE);
        tvWaitNewSensor.setVisibility(View.VISIBLE);



    }

    public void onNewIntent(Intent intent) {
        Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        nfcv = NfcV.get(detectedTag);

        idString = getId();

        //Checker if the sensor exists

        if(fileExists(idString + ".json")){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("This sensor is already registered");
            builder.setMessage("Do you want to overwrite the previous data?");
            //TODO: maybe display the sensor data here
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    progressBarWaitNewSensor.setVisibility(View.INVISIBLE);
                    tvWaitNewSensor.setVisibility(View.INVISIBLE);
                    ScrollNewSensor.setVisibility(View.VISIBLE);
                    // start the location listener

                }
            });


            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(), "cancelled", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    // back to the sensors
                    Intent intent = new Intent(new_sensor.this, sensors.class);
                    startActivity(intent);
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        }
        else{
            progressBarWaitNewSensor.setVisibility(View.INVISIBLE);
            tvWaitNewSensor.setVisibility(View.INVISIBLE);
            ScrollNewSensor.setVisibility(View.VISIBLE);
        }
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

    public void add_sensor(View view) {

        /////////////////////charge data inputs ///////////////////////////

        sensor_name = editSensorName.getText().toString();
        first_name = editName.getText().toString();
        last_name = editLastName.getText().toString();
        org = editOrg.getText().toString();
        //location = editLocation.getText().toString();
        type = editType.getText().toString();
        comment = editComment.getText().toString();
        open_source = checkBoxOpenData.isChecked();
        String tmp = editDepth.getText().toString();
        if (!tmp.isEmpty())  depth = Float.parseFloat(tmp);
        tmp = editSensorDepth.getText().toString();
        if (!tmp.isEmpty())  sensorDepth = Float.parseFloat(tmp);

        // TODO check for invalid inputs
        /*if (check_inputs()) {
            Toast.makeText(getApplicationContext(), "invalid input", Toast.LENGTH_SHORT).show();
            return;
        }*/

        /////////////////////keep data in json object ///////////////////////////
        // TODO wait for the GPS to get lat and lng

        JSONObject new_sensor_json = new JSONObject();
        try {
            new_sensor_json.put("id", idString);
            new_sensor_json.put("lat", lat);
            new_sensor_json.put("lng", lng);
            new_sensor_json.put("alt", alt);
            new_sensor_json.put("locality", locality);
            new_sensor_json.put("period", 0);
            new_sensor_json.put("last_setup", System.currentTimeMillis()/1000);
            new_sensor_json.put("last_collect", 0);
            //TODO read the battery level
            new_sensor_json.put("battery", battery);
            new_sensor_json.put("depth", depth);
            new_sensor_json.put("sensor_depth", sensorDepth);
            new_sensor_json.put("org", org);
            new_sensor_json.put("sensor_name", sensor_name);
            new_sensor_json.put("first_name", first_name);
            new_sensor_json.put("last_name", last_name);
            new_sensor_json.put("type", type);
            new_sensor_json.put("comment", comment);
            new_sensor_json.put("open_source", open_source);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /////////////////////keep sensor in a file ///////////////////
        try {
            fileout = openFileOutput(idString + ".json", MODE_PRIVATE);
            outputWriter = new OutputStreamWriter(fileout);
            outputWriter.write(new_sensor_json.toString());//Le fichier est-il stocké?

            outputWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

      try {
          FileInputStream fileIn = openFileInput(idString + ".json");
          InputStreamReader InputRead = new InputStreamReader(fileIn);
          char[] inputBuffer = new char[100];
          String s = "";
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
        Toast.makeText(getApplicationContext(), "new sensor created", Toast.LENGTH_SHORT).show();

        Intent intent2 = new Intent(this, sensors.class);
        startActivity(intent2);
    }

    public void get_location(View view) {
        JSONObject log_json = new JSONObject();
        try {
            log_json.put("id", "éasldkjféalkjsldf");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            fileout = openFileOutput("test" + ".json", MODE_PRIVATE);
            outputWriter = new OutputStreamWriter(fileout);
            outputWriter.write(log_json.toString());
            outputWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            FileInputStream fileIn = openFileInput("test" + ".json");
            InputStreamReader InputRead = new InputStreamReader(fileIn);

            char[] inputBuffer = new char[100];
            String s = "";
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



        //InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        //// TODO activate the gps, or request to activate it
        //// TODO get the location
        //// TODO set lat and lng in the text view + city??
        //LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //LocationListener locationListener = new MyLocationListener();
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
    }

    private boolean check_inputs() {
        // TODO complete
        if (sensorDepth > depth) return true;
        if (locality.isEmpty()) return true;
        return false;
    }

    public void click_on_background(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
