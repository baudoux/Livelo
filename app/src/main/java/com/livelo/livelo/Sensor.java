package com.livelo.livelo;

/**
 * Created by Remi on 14/05/2017.
 */
public class Sensor {
    //@JsonProperty("Name")
    public static byte id[];
    public static long start_time = 0;
    public static long last_collect_time = 0;

    public static String filesNames = "files_names.txt";
    public static String sensorsId = "id.txt";
    public static String logFile = "";
    public static int NbOfSamplesGlobal = 0;
}

