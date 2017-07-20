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
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.valueOf;

public class settings extends AppCompatActivity {

    //private Spinner set_period;
    private ProgressBar progressBarWaitSettings ;
    private TextView tvWaitSettings;
    //private ScrollView scrollSettings;

    private NfcAdapter myNfcAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    private EditText editPeriod;
    private RelativeLayout settingsLayout;
    private float period = 0; //en minutes
    //byte resetCommand[] = new byte[]{ 0x00, 0x21, (byte) 0, -128, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00};
    byte resetCommand[] = new byte[]{//reset seulement du senseur/mémoire
            0x00,
            0x21,
            (byte) 0,
            0x01, //General control register
            0x00, //Firmware Status register
            0x40, //Sensor control register: temp reset
            0x03, //Frequency control register
            0x01, //Number of passes register
            0x01, //Averaging register
            0x00, //Interrupt control register
            //0x20 //Error control register: log into ram
            0x00
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Settings");
        setContentView(R.layout.activity_settings);


        //set_period = (Spinner) findViewById(R.id.set_period);
        editPeriod = (EditText) findViewById(R.id.editPeriod);
        progressBarWaitSettings = (ProgressBar) findViewById(R.id.progressBarWaitSettings);
        tvWaitSettings = (TextView) findViewById(R.id.tvWaitSettings);
        settingsLayout = (RelativeLayout) findViewById(R.id.settingsLayout);
        //scrollSettings = (ScrollView) findViewById(R.id.scrollSettings);
//        List<String> list = new ArrayList<String>();
//        list.add("5 min");
//        list.add("15 min");
//        list.add("30 min");
//        list.add("1h");
//        list.add("2h");
//        list.add("6h");
//        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_spinner_item, list);
//        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        set_period.setAdapter(dataAdapter);

        myNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter nfcv = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        mFilters = new IntentFilter[]{
                nfcv,
        };
        mTechLists = new String[][]{new String[]{NfcV.class.getName()},
                new String[]{NdefFormatable.class.getName()}};

        //scrollSettings.setVisibility(View.VISIBLE);
        progressBarWaitSettings.setVisibility(View.INVISIBLE);
        tvWaitSettings.setVisibility(View.INVISIBLE);
        settingsLayout.setVisibility(View.VISIBLE);
    }

    public void onNewIntent(Intent intent) {
        // TODO connexion nfc ici et mettre les paremetres dans le sensor

        Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        NfcV nfcv = NfcV.get(detectedTag);
        try {
            nfcv.connect();
            if (nfcv.isConnected()) {

//////////////////////Reset the device /////////////////////

                try{
                    //nfcv.connect();
                    byte command[] = new byte[]{//reset seulement du senseur/mémoire
                            0x00,
                            0x21,
                            (byte) 0,
                            0x01, //General control register
                            0x00, //Firmware Status register
                            0x40, //Sensor control register: temp reset
                            0x03, //Frequency control register
                            0x01, //Number of passes register
                            0x01, //Averaging register
                            0x00, //Interrupt control register
                            //0x20 //Error control register: log into ram
                            0x00
                    };

                    nfcv.transceive(command);

                    // nfcv.transceive(resetCommand);
                    ///////Check if reset is done///////
                    byte[] resetIsDone;
                    resetIsDone = nfcv.transceive(new byte[]{0x00, 0x20, (byte) 0});
                    if((resetIsDone[3] & (byte)64) == (1 << 6)){//Check if the correct function was called
                        Toast.makeText(getBaseContext(), "Reset Done",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getBaseContext(), "Error: Reset not done",Toast.LENGTH_SHORT).show();
                    }
                    //nfcv.close();
                } catch (IOException e) {
                    Toast.makeText(getBaseContext(), "Error",Toast.LENGTH_SHORT).show();
                }

                int periodInMs = (int)(period * 60 * 1000); //period in ms

                byte periodInMsB[] = new byte[4];

                for(int i=0; i<4; i++ )
                {
                    periodInMsB[i] = (byte) (periodInMs >>(i*8));
                }

                byte command1[] = new byte[]{
                        0x00,
                        0x21,
                        (byte) 3,
                        periodInMsB[0],
                        periodInMsB[1],
                        periodInMsB[2],
                        periodInMsB[3],
                        0x00,
                        0x00,
                        0x00,
                        0x00
                };

                nfcv.transceive(command1);

                //Start pressure every periodInMs msecs
                byte command[] = new byte[]{
                        0x00,
                        0x21,
                        (byte) 0,
                        0x01, //General control register
                        0x00, //Firmware Status register
                        0x10, //Sensor control register
                        0x10, //Frequency control register: custom time
                        0x02, //Number of passes register
                        0x01, //Averaging register
                        0x01, //Interrupt control register: infinite sampling
                        0x00 //Error control register
                };

                nfcv.transceive(command);

                //Check if sampling is launched
                byte samplingIsLaunched[] = nfcv.transceive(new byte[]{0x00, 0x20, (byte) 0});
                if((samplingIsLaunched[3] & (byte)16)== (1 << 4)){
                    Toast.makeText(getBaseContext(), "Sampling is launched",Toast.LENGTH_SHORT).show();
                }


                nfcv.close();
            } else{

            }

        } catch (IOException e) {
            //myText.append("Error");
        }


        // back to the menu activity
        Intent intent2 = new Intent(this, menu.class);
        startActivity(intent2);
    }



    public void choose_period(View view) {
  //      new AlertDialog.Builder(this)
  //              // .setSingleChoiceItems(items, 0, null)
  //              .setPositiveButton(R.string.editPeriod, new DialogInterface.OnClickListener() {
  //                  public void onClick(DialogInterface dialog, int whichButton) {
  //                      dialog.dismiss();
  //                      int selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
  //                      // Do something useful withe the position of the selected radio button
  //                  }
  //              })
  //              .show();
    }



    public void update(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

        String tmp = editPeriod.getText().toString();
        if (!tmp.isEmpty()) period = Float.parseFloat(tmp);

       // if(period < 0.05){
       //     Toast.makeText(getBaseContext(), "invalid period, must be al least 0.05", Toast.LENGTH_SHORT).show();
       //     return;
       // }

        if (myNfcAdapter == null){
            Toast.makeText(getBaseContext(), "NFC is not available on this device",Toast.LENGTH_SHORT).show();
            return;
        }

        if (!myNfcAdapter.isEnabled()) {
            Toast.makeText(getBaseContext(), "You should turn NFC on before",Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
                startActivity(intent);
            } else {
                Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                startActivity(intent);
            }
            return;
        }
        if (myNfcAdapter != null) myNfcAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters,
                mTechLists);

        //scrollSettings.setVisibility(View.INVISIBLE);
        progressBarWaitSettings.setVisibility(View.VISIBLE);
        tvWaitSettings.setVisibility(View.VISIBLE);
        settingsLayout.setVisibility(View.INVISIBLE);


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
        Intent intent = new Intent(this, menu.class);
        startActivity(intent);
    }
}
