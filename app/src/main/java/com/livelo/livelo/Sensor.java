package com.livelo.livelo;

/**
 * Created by Remi on 14/05/2017.
 */
public class Sensor {
    public static String first_name = "", last_name = "", company = "", type = "", location = "";
    public static int sampling_period = 0;
    public static byte id[];
    public static boolean open_source = true;
    public static long start_time = 0;
    public static long last_collect_time = 0;

    public static String filesNames = "files_names.txt";
    public static String sensorsId = "id.txt";
}
