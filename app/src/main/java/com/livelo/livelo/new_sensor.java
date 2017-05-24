package com.livelo.livelo;


import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcV;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Calendar;

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

    EditText editName;
    EditText editLastName;
    EditText editCompany;
    EditText editLocation;
    EditText editType;
    EditText editPeriod;
    TextView editId;
    CheckBox checkBoxOpenSource;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("New sensor");
        setContentView(R.layout.activity_new_sensor);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        // client = new com.google.android.gms.common.api.GoogleApiClient.Builder(this).addApi(com.google.android.gms.appindexing.AppIndex.API).build();
        myNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (!myNfcAdapter.isEnabled()) {
            Toast toast = Toast.makeText(getApplicationContext(), "You should turn NFC on before", Toast.LENGTH_SHORT);
            toast.show();
        }

        /*if (myNfcAdapter == null)
            myText.setText("NFC is not available for the device!!!");
        else
            myText.setText("NFC is available for the device");*/
        editName = (EditText) findViewById(R.id.editName);
        editLastName = (EditText) findViewById(R.id.editLastName);
        editCompany = (EditText) findViewById(R.id.editCompany);
        editLocation = (EditText) findViewById(R.id.editLocation);
        editType = (EditText) findViewById(R.id.editType);
        editPeriod = (EditText) findViewById(R.id.editPeriod);
        editId = (TextView) findViewById(R.id.editId);
        checkBoxOpenSource = (CheckBox) findViewById(R.id.checkBoxOpenSource);

        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter nfcv = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        mFilters = new IntentFilter[]{
                nfcv,
        };
        mTechLists = new String[][]{new String[]{NfcV.class.getName()},
                new String[]{NdefFormatable.class.getName()}};


    }

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

    public void onNewIntent(Intent intent) {

        //if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(getIntent().getAction())) {
        Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        NfcV nfcv = NfcV.get(detectedTag);
        try {
            nfcv.connect();
            if (nfcv.isConnected()) {
                //myText.append("Connected to the tag");
                //myText.append("\nTag DSF: " + Byte.toString(nfcv.getDsfId()));
                    /*buffer=nfcv.transceive(new byte[] {0x00, 0x20, (byte) 0});
                    myText.append("\nByte block 10:"+buffer);
                    myText.append("\nByte block 10 as string:"+new String(buffer));*/


                id = nfcv.transceive(new byte[]{0x00, 0x2B});

                StringBuilder id_string = new StringBuilder();
                for (byte b : id) {
                    id_string.append(String.format("%02X", b));
                }
                editId.setText(id_string.toString());
                nfcv.close();
            }

        } catch (IOException e) {
        }
        //}
    }


    public void goto_add_new_sensor(View view) {

        if(id.length == 0){
            Toast toast = Toast.makeText(getApplicationContext(), "Get sensor's id before", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        Sensor.id = copyOf(id, id.length);
        Sensor.first_name = editName.getText().toString();
        Sensor.last_name = editLastName.getText().toString();
        Sensor.company = editCompany.getText().toString();
        Sensor.location = editLocation.getText().toString();
        Sensor.type = editType.getText().toString();
        String tmp = editPeriod.getText().toString();
        if (!tmp.isEmpty()) Sensor.sampling_period = Integer.parseInt(tmp);
        Sensor.open_source = checkBoxOpenSource.isChecked();

        // TODO check for invalid inputs
        if (check_inputs()) {
            Toast toast = Toast.makeText(getApplicationContext(), "invalid input", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        // attendre l'intent du nfc
        // lire l'ID su capteur par NFC
        int id = 123456789;
        // écrire les paramètres dans le capteur et le démarer



        Calendar now = Calendar.getInstance();
        Sensor.start_time = now.getTimeInMillis();
        Sensor.last_collect_time = now.getTimeInMillis();

        Toast toast = Toast.makeText(getApplicationContext(), "new sensor created", Toast.LENGTH_SHORT);
        toast.show();

        Intent intent = new Intent(this, sensors.class);
        startActivity(intent);
    }

    /*
        public void get_location(View view) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            // TODO activate the gps, or request to activate it
            // TODO get the location
            // TODO set lat and lng in the text view
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
        if (myNfcAdapter != null) myNfcAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters,
                mTechLists);
    }

    @Override
    public void onPause() {
        super.onPause();
        myNfcAdapter.disableForegroundDispatch(this);
    }
}
