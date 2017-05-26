package com.livelo.livelo;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcV;
import android.os.Handler;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class collect_data extends AppCompatActivity {

    private NfcAdapter myNfcAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    private TextView tv;
    private TextView data;
    private TextView tv_progress;
    private ProgressBar progressBar;
    byte[] id;
    private int k = 0;

    int i = 0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Collect data");
        setContentView(R.layout.activity_collect_data);

        tv = (TextView) findViewById(R.id.tv);
        data = (TextView) findViewById(R.id.data);
        tv_progress = (TextView) findViewById(R.id.tv_progress);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(32);
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

    public void openNFCSettings(View view) {

        String textmsg = "alsdkfhéalkjshfdlasdf";

        try {
            FileOutputStream fileout=openFileOutput("mytextfile.txt", MODE_PRIVATE);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
            outputWriter.write(textmsg.toString());
            outputWriter.close();

            //display file saved message
            Toast.makeText(getBaseContext(), "File saved successfully!",
                    Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            FileInputStream fileIn = openFileInput("mytextfile.txt");
            InputStreamReader InputRead = new InputStreamReader(fileIn);

            char[] inputBuffer= new char[100];
            String s = "";
            int charRead;

            while ((charRead=InputRead.read(inputBuffer))>0) {
                // char to string conversion
                String readstring=String.copyValueOf(inputBuffer,0,charRead);
                s +=readstring;
            }
            InputRead.close();
            Toast.makeText(getBaseContext(), s,Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
            startActivity(intent);
        } else {
            Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
            startActivity(intent);
        }
    }

    private void refresh(){
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int timeToBlink = 100;    //in milissegunds
                try{Thread.sleep(timeToBlink);}catch (Exception e) {}
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setProgress(i);
                        tv_progress.setText("loading : " + String.valueOf(k/32*100) + "%");
                        if(k<=32) refresh();
                    }
                });
            }
        }).start();
    }


    @Override
    public void onNewIntent(Intent intent) {

        //if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(getIntent().getAction())) {
        Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        NfcV nfcv = NfcV.get(detectedTag);
        try {
            nfcv.connect();
            if (nfcv.isConnected()) {
                // get device id
                id = nfcv.transceive(new byte[]{0x00, 0x2B});

                StringBuilder id_string = new StringBuilder();
                for (byte b : id) {
                    id_string.append(String.format("%02X", b));
                }

                // TODO find sensor by id

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
//
//                tv.setText("Sensor detected:"
//                        + "\nName : " + Sensor.first_name
//                        + "\nLast name : " + Sensor.last_name
//                        + "\nCompany : " + Sensor.company
//                        + "\nLocation : " + Sensor.location
//                        + "\nType : " + Sensor.type
//                        + "\nWorking since : " + fDateStartTime
//                        + "\nLase collect : " + fDateLastCollect
//                        + "\nOpen source : " + (Sensor.open_source ? "yes" : "no")
//                        + "\nid : " + id_string.toString());


                // commencer le refresh pour la progressbar
                refresh();
                // get now for file name
                Calendar now = Calendar.getInstance();
                //String now_string = new SimpleDateFormat("yyyy-MM-dd").format(now);
                //String now_string = new SimpleDateFormat("dd-MM-yyyy").format(now);
                //String now_string = dateFormat(.format(now.getTime()));
                //
                //
                //
                // now_string + "_" +

                String fileName = id_string.toString() + ".txt";

                // TODO for ou while
                byte index[];
                index = nfcv.transceive(new byte[]{0x00, (byte) -64, 0x07, 0x41, 0x06});
                int samplingNb = ((index[4] & 0xff) << 8) | (index[5] & 0xff);

                FileOutputStream fileout=openFileOutput(fileName, MODE_PRIVATE);
                OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);

                int blockCount = 1;

                data.setText("dslhjaslkdjféalfd");

                for ( k = 0; k < 3; k++) { //32 corresponds to 32*2048

                    /////////////////Start transferring from FRAM to RAM////////////////
                    byte command[] = new byte[]{ 0x00, 0x21, (byte) 0, 0x01, 0x00, 0x20, 0x03, 0x01, 0x01, 0x00, 0x00};
                    nfcv.transceive(command);
                    ////////////////////wait 100 ms///////////////////////
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {@Override public void run() {}}, 100);
                    //////////////////Initialize transfer from RAM to phone////////////
                    byte[] buffer;// buffer containing the data
                    StringBuilder sb = new StringBuilder();
                    byte i = 0x44; //Start block of data
                    int j = 0;
                    //////////////////Start transfer/////////////////////
                    //First blocks from 0x644 to 0x6FF
                    while (j < 188) {
                        buffer = nfcv.transceive(new byte[]{0x00, (byte) -64, 0x07, i, 0x06});//Read single block
                        for(int l = 0 ; l < buffer.length; l++){
                            if(l%2 == 1){
                                Log.i(String.format("%1$d",blockCount),String.format("%8s", Integer.toBinaryString(buffer[l] & 0xFF)).replace(' ', '0'));
                                Log.i(String.format("%1$d",blockCount),String.format("%8s", Integer.toBinaryString(buffer[l+1] & 0xFF)).replace(' ', '0'));
                                int  currentData = ((buffer[l] & 0xff) << 8) | (buffer[l+1] & 0xff);
//-------------------------------------------------------------------------------------------------- lecture ici
                                try {
                                    outputWriter.write(String.format("%1$d",currentData) + "\n");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        j++;
                        i++;
                        blockCount++;
                    }

                    //Second blocks
                    j = 0;
                    while (j < 68) {
                        buffer = nfcv.transceive(new byte[]{0x00, (byte) -64, 0x07, i, 0x07});
                        for(int l = 0 ; l < buffer.length; l++) {
                            if (l % 2 == 1) {
                                Log.i(String.format("%1$d", blockCount), String.format("%8s", Integer.toBinaryString(buffer[l] & 0xFF)).replace(' ', '0'));
                                Log.i(String.format("%1$d", blockCount), String.format("%8s", Integer.toBinaryString(buffer[l + 1] & 0xFF)).replace(' ', '0'));
                                int currentData = ((buffer[l] & 0xff) << 8) | (buffer[l + 1] & 0xff);
//-------------------------------------------------------------------------------------------------- lecture ici
                                try {
                                    outputWriter.write(String.format("%1$d",currentData) + "\n");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        j++;
                        i++;
                        blockCount++;
                    }
                }

                outputWriter.close();

                //////////////////////Reset the device /////////////////////
                byte resetCommand[] = new byte[]{ 0x00, 0x21, (byte) 0, -128, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00};
                nfcv.transceive(resetCommand);

                /////////////////////read the file///////////////////////////
                try {
                    FileInputStream fileIn = openFileInput(fileName);
                    InputStreamReader InputRead = new InputStreamReader(fileIn);

                    char[] inputBuffer= new char[100];
                    String s = "";
                    int charRead;

                    while ((charRead = InputRead.read(inputBuffer))>0) {
                        // char to string conversion
                        String readstring=String.copyValueOf(inputBuffer,0,charRead);
                        s +=readstring;
                    }
//                    InputRead.close();
//                    Toast.makeText(getBaseContext(), s,Toast.LENGTH_SHORT).show();
                    data.setText(s);

                } catch (Exception e) {
                    e.printStackTrace();
                }
//__________________________________________________________________________________________________ fonction de lecture
//                try {
//                    FileOutputStream fileout=openFileOutput(fileName, MODE_PRIVATE);
//                    OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
//                    outputWriter.write("sdéflkéslkdfhég".toString());
//                    outputWriter.close();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//__________________________________________________________________________________________________
                nfcv.close();

            } else
                Toast.makeText(getBaseContext(), "Not connected to the tag",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Error",Toast.LENGTH_SHORT).show();
        }

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

/*
    @Override
    public void onNewIntent(Intent intent) {
        //if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(getIntent().getAction())) {
        Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        NfcV nfcv = NfcV.get(detectedTag);
        try {
            nfcv.connect();
            if (nfcv.isConnected()) {
                myText.append("Connected to the tag");
                myText.append("\nTag DSF: " + Byte.toString(nfcv.getDsfId())+ "\n");

                /////////////////////Read the number of samples since the last time/////////////
                byte index[];
                index = nfcv.transceive(new byte[]{0x00, (byte) -64, 0x07, 0x41, 0x06});
                samplingNb = ((index[4] & 0xff) << 8) | (index[5] & 0xff);
                myText.append("Number of sampling since last time: " + String.format("%1$d", currentData));
                myText.append("\n");




                int blockCount = 1;
                for ( int k = 0; k < 1; k++) { //32 corresponds to 32*2048

                    /////////////////Start transferring from FRAM to RAM////////////////
                    byte command[] = new byte[]{
                            0x00,
                            0x21,
                            (byte) 0,
                            0x01, //General control register
                            0x00, //Firmware Status register
                            0x20, //Sensor control register: ExtFram
                            0x03, //Frequency control register
                            0x01, //Number of passes register
                            0x01, //Averaging register
                            0x00, //Interrupt control register
                            //0x20 //Error control register: log into ram
                            0x00
                    };

                    nfcv.transceive(command);

                    ////////////////////wait 100 ms///////////////////////
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                        }
                    }, 100);

                    //////////////////Initialize transfer from RAM to phone////////////
                    byte[] buffer;// buffer containing the data
                    StringBuilder sb = new StringBuilder();

                    byte i = 0x44; //Start block of data
                    int j = 0;

                    //////////////////Start transfer/////////////////////
                    //First blocks from 0x644 to 0x6FF
                    while (j < 188) {
                        buffer = nfcv.transceive(new byte[]{0x00, (byte) -64, 0x07, i, 0x06});//Read single block

                        sb.append("Number " + blockCount + "\n");
                        /*for (byte b : buffer) {
                            sb.append(String.format("%02X ", b));
                        }
                        for(int l = 0 ; l < buffer.length; l++){
                            if(l%2 == 1){
                                Log.i(String.format("%1$d",blockCount),String.format("%8s", Integer.toBinaryString(buffer[l] & 0xFF)).replace(' ', '0'));
                                Log.i(String.format("%1$d",blockCount),String.format("%8s", Integer.toBinaryString(buffer[l+1] & 0xFF)).replace(' ', '0'));
                                currentData = ((buffer[l] & 0xff) << 8) | (buffer[l+1] & 0xff);

                                sb.append(String.format("%1$d", currentData));
                                sb.append(" ");
                            }

                        }
                        sb.append("\n");
                        j++;
                        i++;
                        blockCount++;
                    }

                    //Second blocks
                    j = 0;
                    while (j < 68) {
                        buffer = nfcv.transceive(new byte[]{0x00, (byte) -64, 0x07, i, 0x07});

                        sb.append("Number " + blockCount + "\n");
                        for (byte b : buffer) {
                            sb.append(String.format("%02X ", b));
                        }
                        sb.append("\n");
                        j++;
                        i++;
                        blockCount++;
                    }

                    myText.append("\n" + sb.toString());
                }

                //////////////////////Reset the device /////////////////////
                byte resetCommand[] = new byte[]{//Send reset
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
                };

                nfcv.transceive(resetCommand);
                myText.append("\n Reset Done");



                nfcv.close();

            }else{
                myText.append("Not connected to the tag");
            }


        } catch(IOException e){
            myText.append("Error");
        }

    }*/

