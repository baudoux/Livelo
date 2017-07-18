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

import org.json.JSONArray;
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
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static java.lang.StrictMath.round;

public class collect_data extends AppCompatActivity {

    private NfcAdapter myNfcAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    private TextView tv;
    private TextView textView4;
    private TextView data;
    private TextView tv_progress;
    private ProgressBar progressBar;
    private ProgressBar progressBar4;
    private FileOutputStream fileout;
    private OutputStreamWriter outputWriter;
    private StringBuilder id_string;
    public static String dataForMail;

    byte[] c; // count, number of samples
    byte[] p; // sampling period
    int count = 0; //number of samples
    float periodInMin = 0; //period of sampling in minutes

    public JSONArray data_array;

    byte[] id;
    private int k = 0;
    byte readCommand[] = new byte[]{0x00, 0x21, (byte) 0, 0x01, 0x00, 0x20, 0x03, 0x01, 0x01, 0x00, 0x00};
    byte[] buffer;// buffer containing the data
    Tag detectedTag;
    NfcV nfcv;
    int blockCount = 1;
    String fileName;

    private Intent intentNFC;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Collect data");
        setContentView(R.layout.activity_collect_data);

        tv = (TextView) findViewById(R.id.tv);
        textView4 = (TextView) findViewById(R.id.textView4);
        data = (TextView) findViewById(R.id.data);
        tv_progress = (TextView) findViewById(R.id.tv_progress);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar4 = (ProgressBar) findViewById(R.id.progressBar4);
        progressBar.setMax(31);
        myNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        data.setText("");


        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter nfcv = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        mFilters = new IntentFilter[]{
                nfcv,
        };
        mTechLists = new String[][]{new String[]{NfcV.class.getName()},
                new String[]{NdefFormatable.class.getName()}};

        progressBar.setVisibility(View.INVISIBLE);
        tv_progress.setVisibility(View.INVISIBLE);
        tv.setVisibility(View.INVISIBLE);
        textView4.setVisibility(View.VISIBLE);
        progressBar4.setVisibility(View.VISIBLE);
        data.setVisibility(View.VISIBLE);

        data_array = new JSONArray();

    }

    public void openNFCSettings(View view) { // ça me sert de bouton pour les tests aussi. c'est normal si li y a un peu de la merde dedans
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
            startActivity(intent);
        } else {
            Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
            startActivity(intent);
        }
    }

    private void refresh() {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int timeRead = 100;    //in milissegunds
                try {
                    Thread.sleep(timeRead);
                } catch (Exception e) {
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        readOneBlock();

                        k++;

                        try {
                            nfcv.connect();
                            nfcv.transceive(readCommand);
                            nfcv.close();
                        } catch (IOException e) {
                            Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT).show();
                        }


                        if (k < 1) {// number of bolcks to read - 1
                            refresh();
                        } else {
                            readOneBlock();


                            long now = System.currentTimeMillis() / 1000;
                            String now_string = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(now * 1000));

                            // create the log object////////////////////////////////////////////////
                            JSONObject log_json = new JSONObject();
                            try {
                                log_json.put("id", id_string);
                                // TODO ajouter la date du début si on peut la stocker dans le sensor sinon on la met en ligne quand on start le sensor
                                log_json.put("start", now);
                                log_json.put("stop", now);
                                // TODO ajouter le nomber de sampling ici
                                log_json.put("num", count);
                                log_json.put("period", periodInMin);
                                // TODO lecture de calibration stockée dans le sensor
                                log_json.put("cal_start", 1234);
                                // TODO lecture de calibration au moment de la collecte
                                log_json.put("cal_stop", 2345);
                                log_json.put("data", data_array);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            // store it in a file //////////////////////////////////////////////////
                            fileName = now_string + "_" + id_string.toString() + ".json";
                            try {
                                fileout = openFileOutput(fileName, MODE_PRIVATE);
                                outputWriter = new OutputStreamWriter(fileout);
                                outputWriter.write(log_json.toString());
                                outputWriter.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            // get the string of the json
                            // try {
                            //     FileInputStream fileIn = openFileInput(now_string + "_" +id_string.toString() + ".json");
                            //     InputStreamReader InputRead = new InputStreamReader(fileIn);
//
                            //     char[] inputBuffer = new char[100];
                            //     String s = "";
                            //     int charRead;
//
                            //     while ((charRead = InputRead.read(inputBuffer)) > 0) {
                            //         // char to string conversion
                            //         String readstring = String.copyValueOf(inputBuffer, 0, charRead);
                            //         s += readstring;
                            //         Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
                            //     }
                            //     InputRead.close();
                            // } catch (Exception e) {
                            //     e.printStackTrace();
                            // }

                            // FIXME faire qqch pour si le file existe pas, ou le créer de toutes façons dans l'app

                            // parse the json string
                            String s = "";
                            JSONArray log_files = new JSONArray();

                            File file = new File("log_files.json");
                            if (file.exists()) {
                                try {
                                    FileInputStream fileIn = openFileInput("log_files.json");
                                    InputStreamReader InputRead = new InputStreamReader(fileIn);
                                    char[] inputBuffer = new char[100];
                                    int charRead;
                                    while ((charRead = InputRead.read(inputBuffer)) > 0) {
                                        // char to string conversion
                                        String readstring = String.copyValueOf(inputBuffer, 0, charRead);
                                        s += readstring;
                                        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
                                        log_files = new JSONArray(s);
                                    }
                                    InputRead.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            /////////////////////keep the name of the file in files_names.txt ///////////////////////////
                            try {
                                log_files.put(fileName);
                                fileout = openFileOutput("log_files.json", MODE_PRIVATE);
                                outputWriter = new OutputStreamWriter(fileout);
                                outputWriter.write(log_files.toString());
                                outputWriter.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            //__________________________________________________________________________________________________
                            try {
                                nfcv.close();
                            } catch (IOException e) {
                                Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT).show();
                            }
                            Toast.makeText(getApplicationContext(), "done", Toast.LENGTH_LONG).show();

                        }
                        progressBar.setProgress(k);
                        tv_progress.setText("loading : " + String.valueOf(round((float) k / 31 * 100)) + "%");

                    }
                });
            }
        }).start();
    }


    @Override
    public void onNewIntent(Intent intent) {
        progressBar4.setVisibility(View.INVISIBLE);
        textView4.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        tv_progress.setVisibility(View.VISIBLE);
        data.setVisibility(View.VISIBLE);
        tv.setVisibility(View.VISIBLE);

        //if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(getIntent().getAction())) {
        intentNFC = intent;
        detectedTag = intentNFC.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        nfcv = NfcV.get(detectedTag);
        try {
            nfcv.connect();
            if (nfcv.isConnected()) {
                // get device id
                id = nfcv.transceive(new byte[]{0x00, 0x2B});

                id_string = new StringBuilder();
                for (byte b : id) {
                    id_string.append(String.format("%02X", b));
                }

                ///////////////////////////how many samples since last time ?
                readNbofSamples();


                ///////////////////////What is the sampling frequency ?
                readSamplingFreq();



                // TODO find sensor by id
                File file = new File(id_string.toString() + ".json");
                if (!file.exists()) {
                    tv.setText("Sensor detected: unknown sensor");
                } else {
                    // read the json
                    String s = "";
                    try {
                        FileInputStream fileIn = openFileInput(id_string.toString() + ".json");
                        InputStreamReader InputRead = new InputStreamReader(fileIn);
                        char[] inputBuffer = new char[100];
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
                    // create the object
                    // extract data

                    try {
                        JSONObject sensor = new JSONObject(s);// mettre la string en argument
                        tv.setText("Sensor detected:"
                                + "\nid : " + sensor.getString("id")
                                + "\nName : " + sensor.getString("first_name")
                                + "\nLast name : " + sensor.getString("last_name")
                                + "\nCompany : " + sensor.getString("company")
                                + "\nLocation : " + sensor.getString("location")
                                + "\nType : " + sensor.getString("type")
                                + "\nComment : " + sensor.getString("comment")
                                + "\nOpen source : " + sensor.getBoolean("open_source")
                                + "\nWorking since : " + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(sensor.getLong("set_up_time") * 1000))
                                + "\nLast collect time : " + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(sensor.getLong("last_collect_time") * 1000))
                                + "\n");
                        long now = System.currentTimeMillis() / 1000;
                        sensor.put("last_collect_time", now);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


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
                }
                // get now for file name
                Calendar now = Calendar.getInstance();
                String now_string = new SimpleDateFormat("yyyy-MM-dd").format(now.getTimeInMillis());

                fileName = now_string + "_" + id_string.toString() + ".txt";

                byte index[];
                index = nfcv.transceive(new byte[]{0x00, (byte) -64, 0x07, 0x41, 0x06});

                fileout = openFileOutput(fileName, MODE_PRIVATE);
                outputWriter = new OutputStreamWriter(fileout);

                // ask for the first block
                nfcv.transceive(readCommand);


                //////////////////// start the reading function//////////////////////
                refresh();
                nfcv.close();
            } else
                Toast.makeText(getBaseContext(), "Not connected to the tag", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT).show();
        }
        tv_progress.setText("connection started");

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
        Intent intent = new Intent(this, menu.class);
        startActivity(intent);
    }

    public void readOneBlock() {
        byte i = 0x44; //Start block of data
        int j = 0;
        //////////////////Start transfer/////////////////////
        //First blocks from 0x644 to 0x6FF
        try {
            nfcv.connect();
            while (j < 188) {
                try {
                    buffer = nfcv.transceive(new byte[]{0x00, (byte) -64, 0x07, i, 0x06});//Read single block
                } catch (IOException e) {
                    Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT).show();
                }
                for (int l = 0; l < buffer.length; l++) {
                    if (l % 2 == 1) {
                        Log.i(String.format("%1$d", blockCount), String.format("%8s", Integer.toBinaryString(buffer[l] & 0xFF)).replace(' ', '0'));
                        Log.i(String.format("%1$d", blockCount), String.format("%8s", Integer.toBinaryString(buffer[l + 1] & 0xFF)).replace(' ', '0'));
                        int currentData = ((buffer[l] & 0xff) << 8) | (buffer[l + 1] & 0xff);
                        data_array.put(currentData);
                        //-------------------------------------------------------------------------------------------------- lecture ici
                        try {
                            outputWriter.write(String.format("%1$d", currentData) + "\n");
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
                try {
                    buffer = nfcv.transceive(new byte[]{0x00, (byte) -64, 0x07, i, 0x07});
                } catch (IOException e) {
                    Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT).show();
                }
                for (int l = 0; l < buffer.length; l++) {
                    if (l % 2 == 1) {
                        Log.i(String.format("%1$d", blockCount), String.format("%8s", Integer.toBinaryString(buffer[l] & 0xFF)).replace(' ', '0'));
                        Log.i(String.format("%1$d", blockCount), String.format("%8s", Integer.toBinaryString(buffer[l + 1] & 0xFF)).replace(' ', '0'));
                        int currentData = ((buffer[l] & 0xff) << 8) | (buffer[l + 1] & 0xff);
                        data_array.put(currentData);
                        //-------------------------------------------------------------------------------------------------- lecture ici
                        try {
                            outputWriter.write(String.format("%1$d", currentData) + "\n");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                j++;
                i++;
                blockCount++;
            }
            nfcv.close();
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT).show();
        }

    }


    public void readSamplingFreq() {
        try {
            p = nfcv.transceive(new byte[]{0x00, 0x20, 0x03}); //read block 3 from FRAM
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT).show();
        }
        int period = ((p[4] & 0xff) << 24) | ((p[3] & 0xff) << 16) | ((p[2] & 0xff) << 8) | (p[1] & 0xff);//Warning: order of bytes inversed!
        periodInMin = (float) period / 60000; //period in minutes
        return;
    }

    public void readNbofSamples(){
        try {
            //nfcv.connect();
            c = nfcv.transceive(new byte[]{0x00, (byte) -64, 0x07, 0x41, 0x06}); //read block 641h from RAM
            //nfcv.close();
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT).show();
        }
        count = ((c[6] & 0xff) << 8) | (c[5] & 0xff);//Warning: order of bytes inversed!
        count = (count >> 1) - 1;
        if (count < 0)
            count = 0;
        return;
    }

}

