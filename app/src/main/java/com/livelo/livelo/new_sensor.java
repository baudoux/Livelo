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
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.livelo.livelo.R.id.progressBarWaitNewSensor;
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

    StringBuilder idString = new StringBuilder();

    private EditText editName;
    private EditText editLastName;
    private EditText editCompany;
    private EditText editLocation;
    private EditText editType;
    private EditText editComment;
    private CheckBox checkBoxOpenSource;
    private ProgressBar progressBarWaitNewSensor;
    private ScrollView ScrollNewSensor;
    private TextView tvWaitNewSensor;

    private FileOutputStream fileout;
    private OutputStreamWriter outputWriter;

    public static String first_name = "";
    public static String last_name = "";
    public static String company = "";
    public static String type = "";
    public static String location = "";
    public static String comment = "";
    public static int sampling_period = 0;
    public static byte id[];
    public static boolean open_source = true;
    public static long start_time = 0;
    public static long last_collect_time = 0;

    public static String filesNames = "files_names.txt";
    public static String sensorsId = "id.txt";
    public static String logFile = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("New sensor");
        setContentView(R.layout.activity_new_sensor);
        myNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        editName = (EditText) findViewById(R.id.editName);
        editLastName = (EditText) findViewById(R.id.editLastName);

        editCompany = (EditText) findViewById(R.id.editCompany);
        editLocation = (EditText) findViewById(R.id.editLocation);
        editType = (EditText) findViewById(R.id.editType);
        editComment = (EditText) findViewById(R.id.editComment);
        checkBoxOpenSource = (CheckBox) findViewById(R.id.checkBoxOpenSource);
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

        // TODO turn on the GPS here to be quicker to get the location at the end



    }
    /*
    public void get_id(View view) {
        if (!myNfcAdapter.isEnabled()) {
            Toast toast = Toast.makeText(getApplicationContext(), "You should turn NFC on before", Toast.LENGTH_SHORT);
            toast.show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
                startActivity(intent);
            } else {
                Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                startActivity(intent);
            }
        } else{
            Toast toast = Toast.makeText(getApplicationContext(), "scan the sensor", Toast.LENGTH_SHORT);
            toast.show();
        }

    }
*/
    public void onNewIntent(Intent intent) {
        Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        NfcV nfcv = NfcV.get(detectedTag);
        try {
            nfcv.connect();
            if (nfcv.isConnected()) {

                id = nfcv.transceive(new byte[]{0x00, 0x2B});
                nfcv.close();
            }
        } catch (IOException e) {
        }


        /////////////////////keep the sensor's id in sensors/id.txt ///////////////////////////

        for (int i = 0; i < id.length; i++) {
            String hex = Integer.toHexString(0xFF & id[i]);
            if (hex.length() == 1) {
                idString.append('0');
            }
            idString.append(hex);
        }

        //TODO regarder si le sensor existe déjà
        File file = new File(idString.toString() + ".json");
        if(file.exists()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("sensor already exits");
            builder.setMessage("Do you want to overwrite the previous one?");
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    add_sensor();
                    dialog.dismiss();
                }
            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(), "cancelled", Toast.LENGTH_SHORT).show();
                    ScrollNewSensor.setVisibility(View.VISIBLE);
                    progressBarWaitNewSensor.setVisibility(View.INVISIBLE);
                    tvWaitNewSensor.setVisibility(View.INVISIBLE);
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
            if (false) {
            }
        }



    }


    public void add_sensor() {


        //try {
        //    fileout = openFileOutput(Sensor.sensorsId, MODE_APPEND | MODE_PRIVATE);
        //    outputWriter = new OutputStreamWriter(fileout);
        //    outputWriter.write(idString.toString() + "\n");
        //    outputWriter.close();
        //} catch (Exception e) {
        //    e.printStackTrace();
        //}

        /////////////////////keep data in json object ///////////////////////////

        first_name = editName.getText().toString();
        last_name = editLastName.getText().toString();
        company = editCompany.getText().toString();
        location = editLocation.getText().toString();
        type = editType.getText().toString();
        comment = editComment.getText().toString();
        open_source = checkBoxOpenSource.isChecked();

        // TODO check for invalid inputs
        if (check_inputs()) {
            Toast toast = Toast.makeText(getApplicationContext(), "invalid input", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }


        /////////////////////keep data in json object ///////////////////////////

        JSONObject new_sensor_json = new JSONObject();
        try {
            new_sensor_json.put("id", id);
            new_sensor_json.put("first_name", first_name);
            new_sensor_json.put("last_name", last_name);
            new_sensor_json.put("company", company);
            new_sensor_json.put("location", location);
            new_sensor_json.put("type", type);
            new_sensor_json.put("comment", comment);
            new_sensor_json.put("open_source", open_source);
            long now = System.currentTimeMillis()/1000;
            new_sensor_json.put("set_up_time", now);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /////////////////////keep sensor in a file ///////////////////
        try {
            fileout = openFileOutput(idString + ".json", MODE_PRIVATE);
            outputWriter = new OutputStreamWriter(fileout);
            outputWriter.write(new_sensor_json.toString());
            outputWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            FileInputStream fileIn = openFileInput("1234567890" + ".json");
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



        //try {
        //    fileout = openFileOutput(idString.toString() + ".txt", MODE_PRIVATE);
        //    outputWriter = new OutputStreamWriter(fileout);
        //    outputWriter.write(Sensor.first_name + "\n");
        //    outputWriter.write(Sensor.last_name + "\n");
        //    outputWriter.write(Sensor.company + "\n");
        //    outputWriter.write(Sensor.location + "\n");
        //    outputWriter.write(Sensor.type + "\n");
        //    outputWriter.write(Sensor.comment + "\n");
        //    outputWriter.write(Sensor.open_source + "\n");
        //    outputWriter.close();
        //} catch (Exception e) {
        //    e.printStackTrace();
        //}



        Calendar now = Calendar.getInstance();
        start_time = now.getTimeInMillis();
        last_collect_time = now.getTimeInMillis();

        Toast toast = Toast.makeText(getApplicationContext(), "new sensor created", Toast.LENGTH_SHORT);
        toast.show();

        Intent intent2 = new Intent(this, sensors.class);
        startActivity(intent2);
    }


    public void goto_add_new_sensor(View view) {
        // pour debugger
        add_sensor();
        return;
        /////////////////////


        //InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
//
//
        //if (myNfcAdapter == null){
        //    Toast.makeText(getBaseContext(), "NFC is not available on this device",Toast.LENGTH_SHORT).show();
        //    return;
        //}
//
        //if (!myNfcAdapter.isEnabled()) {
        //    Toast.makeText(getBaseContext(), "You should turn NFC on before",Toast.LENGTH_SHORT).show();
        //    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        //        Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
        //        startActivity(intent);
        //    } else {
        //        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        //        startActivity(intent);
        //    }
        //    return;
        //}
//
//
//
        //if (myNfcAdapter != null) myNfcAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters,
        //        mTechLists);
        //ScrollNewSensor.setVisibility(View.INVISIBLE);
        //progressBarWaitNewSensor.setVisibility(View.VISIBLE);
        //tvWaitNewSensor.setVisibility(View.VISIBLE);
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
        return false;
    }

    public void click_on_background(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        myNfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, sensors.class);
        startActivity(intent);
    }
}
