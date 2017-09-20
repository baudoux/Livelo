package com.livelo.livelo;

import android.content.Intent;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static com.livelo.livelo.R.layout.activity_sensors;


public class sensors extends AppCompatActivity {

    // TODO faire une liste scroll pour afficher les capteurs

    private NfcAdapter myNfcAdapter;
    private TextView tvSensrosList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_sensors);
        getSupportActionBar().setTitle("Sensors");

        final ListView sensorsList = (ListView) findViewById(R.id.sensorsListView);

        String[] prenoms = new String[]{
                "Antoine", "Benoit", "Cyril", "David", "Eloise", "Florent"
        };

        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < prenoms.length; ++i) {
            list.add(prenoms[i]);
        }

        final ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, list);
        sensorsList.setAdapter(adapter);

//      sensorsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

//         @Override
//         public void onItemClick(AdapterView<?> parent, final View view,
//                                 int position, long id) {
//             final String item = (String) parent.getItemAtPosition(position);
//             view.animate().setDuration(2000).alpha(0)
//                     .withEndAction(new Runnable() {
//                         @Override
//                         public void run() {
//                             list.remove(item);
//                             adapter.notifyDataSetChanged();
//                             view.setAlpha(1);
//                         }
//                     });
//         }
//     });

        //tvSensrosList = (TextView) findViewById(R.id.tvSensrosList);

 //      try {
 //          FileInputStream fileIn = openFileInput(Sensor.sensorsId);
 //          InputStreamReader InputRead = new InputStreamReader(fileIn);

 //          char[] inputBuffer= new char[100];
 //          String s = "";
 //          int charRead;

 //          while ((charRead=InputRead.read(inputBuffer))>0) {
 //              // char to string conversion
 //              String readstring=String.copyValueOf(inputBuffer,0,charRead);
 //              s +=readstring;
 //          }
 //          InputRead.close();
 //          tvSensrosList.setText(s);
 //      } catch (Exception e) {
 //          e.printStackTrace();
 //      }



        // list = (ListView) findViewById(R.id.list);

//        // my_child_toolbar is defined in the layout file
//        Toolbar myChildToolbar =
//                (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(myChildToolbar);
//
//        // Get a support ActionBar corresponding to this toolbar
//        ActionBar ab = getSupportActionBar();
//
//        // Enable the Up button
//        ab.setDisplayHomeAsUpEnabled(true);

       // List<Tweet> tweets = genererTweets();

       // TweetAdapter adapter = new TweetAdapter(MainActivity.this, tweets);
       // mListView.setAdapter(adapter);
        //final ArrayAdapter<String> adapter = new ArrayAdapter<String>(.this,
        //        android.R.layout.simple_list_item_1, prenoms);
        //list.setAdapter(adapter);
    }
/*
    private List<Tweet> genererTweets(){
        List<Tweet> tweets = new ArrayList<Tweet>();
        tweets.add(new Tweet(Color.BLACK, "Florent", "Mon premier tweet !"));
        tweets.add(new Tweet(Color.BLUE, "Kevin", "C'est ici que ça se passe !"));
        tweets.add(new Tweet(Color.GREEN, "Logan", "Que c'est beau..."));
        tweets.add(new Tweet(Color.RED, "Mathieu", "Il est quelle heure ??"));
        tweets.add(new Tweet(Color.GRAY, "Willy", "On y est presque"));
        return tweets;
    }
*/
    public void goto_new_sensor(View view) {
        myNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (myNfcAdapter == null){
            Toast.makeText(getBaseContext(), "NFC is not available for the device", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, new_sensor.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void goto_map(View view){
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}

