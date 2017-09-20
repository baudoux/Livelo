package com.livelo.livelo;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;



/**
 * Created by Remi on 14/05/2017.
 */
public class Global {
    public static long last_upload = 0;
    public static int[] myarray1=new int[10];
    public static Sensor tmp_sensor;

    public boolean fileExists(String name){
        try {
            FileInputStream fileIn = openFileInput(name);
            InputStreamReader InputRead = new InputStreamReader(fileIn);
            int c = InputRead.read();

            if(c == -1) {
                InputRead.close();
                return false;
            }
            else{
                InputRead.close();
                return true;
            }

        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

