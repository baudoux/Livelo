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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Calendar;

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

    byte[] id = {};
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

        Sensor.id = copyOf(id, id.length);
        Sensor.first_name = editName.getText().toString();
        Sensor.last_name = editLastName.getText().toString();
        Sensor.company = editCompany.getText().toString();
        Sensor.location = editLocation.getText().toString();
        Sensor.type = editType.getText().toString();
        Sensor.comment = editComment.getText().toString();
        Sensor.open_source = checkBoxOpenSource.isChecked();

        // TODO check for invalid inputs
        if (check_inputs()) {
            Toast toast = Toast.makeText(getApplicationContext(), "invalid input", Toast.LENGTH_SHORT);
            toast.show();
            return;
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
        if(true) {
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


        //TODO en json???
        try {
            fileout = openFileOutput(Sensor.sensorsId, MODE_APPEND | MODE_PRIVATE);
            outputWriter = new OutputStreamWriter(fileout);
            outputWriter.write(idString.toString() + "\n");
            outputWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        /////////////////////create "id".txt in sensors and keep sensor's info in it ///////////////////////////

        //TODO stocker les infos du sensor dans un fichies (json???) overwrite
        //???????
        JSONObject object = new JSONObject();
        try {
            object.put("id", idString.toString());
            object.put("first_name", Sensor.first_name);
            object.put("last_name", Sensor.last_name);
            object.put("company", Sensor.company);
            object.put("location", Sensor.location);
            object.put("type", Sensor.type);
            object.put("comment", Sensor.comment);
            object.put("open_source", Sensor.open_source);
            Calendar now = Calendar.getInstance();
            object.put("set_up_time", now.getTimeInMillis());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        try {
            fileout = openFileOutput(idString.toString() + ".txt", MODE_PRIVATE);
            outputWriter = new OutputStreamWriter(fileout);
            outputWriter.write(Sensor.first_name + "\n");
            outputWriter.write(Sensor.last_name + "\n");
            outputWriter.write(Sensor.company + "\n");
            outputWriter.write(Sensor.location + "\n");
            outputWriter.write(Sensor.type + "\n");
            outputWriter.write(Sensor.comment + "\n");
            outputWriter.write(Sensor.open_source + "\n");
            outputWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }



        Calendar now = Calendar.getInstance();
        Sensor.start_time = now.getTimeInMillis();
        Sensor.last_collect_time = now.getTimeInMillis();

        Toast toast = Toast.makeText(getApplicationContext(), "new sensor created", Toast.LENGTH_SHORT);
        toast.show();

        Intent intent2 = new Intent(this, sensors.class);
        startActivity(intent2);
    }


    public void goto_add_new_sensor(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

        if (myNfcAdapter != null) myNfcAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters,
                mTechLists);
        ScrollNewSensor.setVisibility(View.INVISIBLE);
        progressBarWaitNewSensor.setVisibility(View.VISIBLE);
        tvWaitNewSensor.setVisibility(View.VISIBLE);
    }



    /*
        public void get_location(View view) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            // TODO activate the gps, or request to activate it
            // TODO get the location
            // TODO set lat and lng in the text view + city??
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            LocationListener locationListener = new MyLocationListener();
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        }
    */
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
