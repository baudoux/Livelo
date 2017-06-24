package com.livelo.livelo;

import android.app.Activity;
        import android.app.AlertDialog;
        import android.app.PendingIntent;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.IntentFilter;
        import android.nfc.NfcAdapter;
        import android.nfc.Tag;
        import android.nfc.tech.Ndef;
        import android.nfc.tech.NdefFormatable;
        import android.nfc.tech.NfcV;
        import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by Nico on 12/05/2017.
 */
public class reset extends AppCompatActivity {

    private NfcAdapter myNfcAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    private TextView tv_reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);
        getSupportActionBar().setTitle("Reset");


        tv_reset = (TextView) findViewById(R.id.tv_reset);
        myNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter nfcv = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        mFilters = new IntentFilter[] {
                nfcv,
        };
        mTechLists = new String[][] { new String[] { NfcV.class.getName() },
                new String[] { NdefFormatable.class.getName() }};

    }

    @Override
    public void onNewIntent(Intent intent) {
        //if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(getIntent().getAction())) {
        Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        NfcV nfcv = NfcV.get(detectedTag);
        try {
            nfcv.connect();
            if (nfcv.isConnected()) {
                tv_reset.append("Connected to the tag");
                tv_reset.append("\nTag DSF: " + Byte.toString(nfcv.getDsfId()));


                /*byte command[] = new byte[]{//Send reset
                        0x00,
                        0x21,
                        (byte) 0,
                        -128, //General control register : reset
                        0x00, //Firmware Status register
                        0x00, //Sensor control register
                        0x00, //Frequency control register
                        0x00, //Number of passes register
                        0x01, //Averaging register
                        0x00, //Interrupt control register: infinite sampling
                        0x00 //Error control register
                };*/

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

                ///////Check if reset is done///////
                byte[] resetIsDone;
                resetIsDone = nfcv.transceive(new byte[]{0x00, 0x20, (byte) 0});
                if((resetIsDone[3] & (byte)64) == (1 << 6)){//Check if the correct function was called
                    Toast.makeText(getBaseContext(), "Reset Done",Toast.LENGTH_SHORT).show();
                }



            }
            nfcv.close();

        } catch (IOException e) {
            tv_reset.append("Error");
        }

//TODO: Add a toast when the reset is done ?


        // go back to the menu activity
        Intent intent2 = new Intent(this, menu.class);
        startActivity(intent2);


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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, menu.class);
        startActivity(intent);
    }

}