package com.livelo.livelo;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcV;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class collect_data extends AppCompatActivity {

    private NfcAdapter myNfcAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    private TextView myText;
    private TextView tv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Collect data");
        setContentView(R.layout.activity_collect_data);

        tv = (TextView) findViewById(R.id.tv);
        myNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (!myNfcAdapter.isEnabled()) {
            Toast toast = Toast.makeText(getApplicationContext(), "You should turn NFC on before", Toast.LENGTH_SHORT);
            toast.show();
        }

        /*if (myNfcAdapter == null)
            myText.setText("NFC is not available for the device!!!");
        else
            myText.setText("NFC is available for the device");*/

        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter nfcv = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        mFilters = new IntentFilter[]{
                nfcv,
        };
        mTechLists = new String[][]{new String[]{NfcV.class.getName()},
                new String[]{NdefFormatable.class.getName()}};
    }

    /*
//--------------------------------------------------------------------------------
// should be in onNewIntent function
        String fDateLastCollect = "never"; // shouldn't append if an id is an id is assigned
        if (Sensor.last_collect_time != 0) {
            Date last_collect = new Date(Sensor.last_collect_time);
            fDateLastCollect = new SimpleDateFormat("dd-MM-yyyy").format(last_collect);
        }

        String fDateStartTime = "never"; // shouldn't append if an id is an id is assigned
        if(Sensor.start_time != 0) {
            Date start_time = new Date(Sensor.start_time);
            fDateStartTime = new SimpleDateFormat("dd-MM-yyyy").format(start_time);
        }

        tv.setText("Sensor detected:"
                + "\nName : " + Sensor.first_name
                + "\nLast name : " + Sensor.last_name
                + "\nCompany : " + Sensor.company
                + "\nLocation : " + Sensor.location
                + "\nType : " + Sensor.type
                + "\nWorking since : " + fDateStartTime
                + "\nLase collect : " + fDateLastCollect
                + "\nOpen source : " + (Sensor.open_source? "yes":"no"));
        //---------------------------------------------------------------------------------------
    }*/

    public void openNFCSettings(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
            startActivity(intent);
        } else {
            Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
            startActivity(intent);
        }
    }


    @Override
    public void onNewIntent(Intent intent) {
        Toast toast = Toast.makeText(getApplicationContext(), "in intent", Toast.LENGTH_SHORT);
        toast.show();

        //if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(getIntent().getAction())) {
        Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        NfcV nfcv = NfcV.get(detectedTag);
        try {
            nfcv.connect();
            if (nfcv.isConnected()) {
                //myText.append("Connected to the tag");
                //myText.append("\nTag DSF: " + Byte.toString(nfcv.getDsfId()));
                byte[] buffer;
                    /*buffer=nfcv.transceive(new byte[] {0x00, 0x20, (byte) 0});
                    myText.append("\nByte block 10:"+buffer);
                    myText.append("\nByte block 10 as string:"+new String(buffer));*/


                buffer = nfcv.transceive(new byte[]{0x00, 0x2B});

                StringBuilder sb = new StringBuilder();
                for(byte b : buffer){
                    sb.append(String.format("%02X", b));
                }
                //myText.append(sb.toString());

                nfcv.close();
            } else

                myText.append("Not connected to the tag");
        } catch (IOException e) {
            myText.append("Error");
        }
        //}
    }


    public void collect(View view) {
        myText = (TextView) findViewById(R.id.NFCAdapter);
        myNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (!myNfcAdapter.isEnabled()) {
            Toast toast = Toast.makeText(getApplicationContext(), "You should turn NFC on before", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        if (myNfcAdapter == null)
            myText.setText("NFC is not available for the device!!!");
        else
            myText.setText("NFC is available for the device");

        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(getIntent().getAction())) {
            Tag detectedTag = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG); NfcV nfcv = NfcV.get(detectedTag);
            try {
                nfcv.connect();
                if(nfcv.isConnected()){
                    myText.append("Connected to the tag");
                    myText.append("\nTag DSF: "+Byte.toString(nfcv.getDsfId()));
                    byte[] buffer;
                   /*buffer=nfcv.transceive(new byte[] {0x00, 0x20, (byte) 0});
                   myText.append(“\nByte block 10:“+buffer);
                   myText.append(“\nByte block 10 as string:“+new String(buffer));*/


                    //nfcv.transceive(new byte[] {0x00, 0x21, (byte) 0,0x01, 0x00, 0x10, 0x03, 0x02, 0x01, 0x01, 0x00});

                    buffer=nfcv.transceive(new byte[] {0x00, 0x20, (byte) 9});

                    StringBuilder sb = new StringBuilder();
                    for (byte b : buffer) {
                        sb.append(String.format("%02X ", b));
                    }
                    //System.out.println(sb.toString());

                    myText.append("\nByte block 10:"+sb.toString());
                    myText.append("\nByte block 10 as string:"+new String(buffer));
                    nfcv.close();
                } else
                    myText.append("Not connected to the tag");
            } catch (IOException e) { myText.append("Error");
            }
        }

        // TODO change last collect time if succeed
        Calendar now = Calendar.getInstance();
        Sensor.last_collect_time = now.getTimeInMillis();
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
