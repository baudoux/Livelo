package com.livelo.livelo;

/**
 * Created by Remi on 14/05/2017.
 */
public class Sensor {
    //@JsonProperty("Name")
    public static String first_name = "";
    public static String last_name = "";
    public static String company = "";
    public static String type = "";
    public static String location = "";
    public static String comment = "";
    public static int sampling_period = 0;
    public static byte id[];
    public static boolean open_source = true;
    public static long start_time = 0;
    public static long last_collect_time = 0;

    public static String filesNames = "files_names.txt";
    public static String sensorsId = "id.txt";
    public static String logFile = "";
}

