package com.livelo.livelo;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

public class new_sensor extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    // private com.google.android.gms.common.api.GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("New sensor");
        setContentView(R.layout.activity_new_sensor);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        // client = new com.google.android.gms.common.api.GoogleApiClient.Builder(this).addApi(com.google.android.gms.appindexing.AppIndex.API).build();
    }


    public void goto_add_new_sensor(View view) {

        // create sensor structure and load infos in it
/*
        new_sensor.name = editName.getText().toString();
        new_sensor.last_name = editLastName.getText().toString();
        new_sensor.company = editCompany.getText().toString();
        new_sensor.location = editLocation.getText().toString();
        new_sensor.type = editType.getText().toString();
        new_sensor.open = checkBoxOpenSource.isChecked();
        */

        EditText editName = (EditText) findViewById(R.id.editName);
        EditText editLastName = (EditText) findViewById(R.id.editLastName);
        EditText editCompany = (EditText) findViewById(R.id.editCompany);
        EditText editLocation = (EditText) findViewById(R.id.editLocation);
        EditText editType = (EditText) findViewById(R.id.editType);
        EditText editPeriod = (EditText) findViewById(R.id.editPeriod);
        CheckBox checkBoxOpenSource = (CheckBox) findViewById(R.id.checkBoxOpenSource);


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

        Sensor.id = id;


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
/*
    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        com.google.android.gms.appindexing.Action viewAction = com.google.android.gms.appindexing.Action.newAction(
                com.google.android.gms.appindexing.Action.TYPE_VIEW, // TODO: choose an action type.
                "new_sensor Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.livelo.livelo/http/host/path")
        );
        com.google.android.gms.appindexing.AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        com.google.android.gms.appindexing.Action viewAction = com.google.android.gms.appindexing.Action.newAction(
                com.google.android.gms.appindexing.Action.TYPE_VIEW, // TODO: choose an action type.
                "new_sensor Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.livelo.livelo/http/host/path")
        );
        com.google.android.gms.appindexing.AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
*/
    /*private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {

        //------- To get city name from coordinates --------
            String cityName = null;
            Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(loc.getLatitude(),
                        loc.getLongitude(), 1);
                if (addresses.size() > 0) {
                    System.out.println(addresses.get(0).getLocality());
                    cityName = addresses.get(0).getLocality();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(
                        getBaseContext(),
                        "Location changed: Lat: " + loc.getLatitude() + " Lng: "
                                + loc.getLongitude()+"   "+ cityName, Toast.LENGTH_SHORT).show();

            }
        }
    }*/
}
