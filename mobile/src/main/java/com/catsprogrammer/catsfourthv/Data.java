package com.catsprogrammer.catsfourthv;

import java.util.Timer;

/**
 * Created by C on 2016-11-20.
 */

public class Data {

    public static Timer fuseTimer;

    //Mobile Sensor
    public static float[] gyro = new float[3];
    public static float[] magnet = new float[3];
    public static float[] acc = new float[3];

    public static boolean[] rightStack = {false, false, false, false};
    public static boolean[] leftStack = {false, false, false, false};
    public static boolean[] upStack = {false, false, false, false};
    public static boolean[] downStack = {false, false, false, false};


    public static float[][] gravitySensorM = new float[30][3];



    public static float[] gyroOrientationM = new float[3];
    public static float[] accMagOrientationM = new float[3];

    public static float[][] fusedSensorM =  new float[30][3];
    public static float[][] differM = new float[30][3];

    public static float[] rotationMatrixM = new float[9];
    public static float[] gyroMatrixM = new float[9];


    //Sensor from wearable
    public static float[] gyroFromWear = new float[15];
    public static float[] magnetFromWear = new float[15];
    public static float[] accFromWear = new float[15];

    public static float[] gyroOrientationW = new float[3];
    public static float[] accMagOrientationW = new float[3];

    public static float[][] fusedSensorW =  new float[30][3];
    public static float[][] differW = new float[30][3];

    public static float[] rotationMatrixW = new float[9];
    public static float[] gyroMatrixW = new float[9];


    //Sensor subtract

    public static float[][] differAll = new float[30][3];
    //flag

    public static boolean checkSend = false;
    public static boolean isAlreadySend = false;
    public static boolean isInput = false;

    public static int upCount = 0;
    public static int downCount = 0;
    public static int leftCount = 0;
    public static int rightCount = 0;
}
