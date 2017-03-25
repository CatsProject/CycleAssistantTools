package com.catsprogrammer.shared;

import android.hardware.Sensor;

/**
 * Created by C on 2016-11-20.
 */

public interface TagName {

    int SENS_SIZE = 6;
    int BUTTON_SIZE = 4;

    float oneMinusCoeff = 1.0f - 0.95f;
    int SENS_ACCELEROMETER = Sensor.TYPE_ACCELEROMETER;
    int SENS_GYROSCOPE = Sensor.TYPE_GYROSCOPE;
    int SENS_MAGNETIC = Sensor.TYPE_MAGNETIC_FIELD;

    int MATRIX_SIZE = 9;


    int TIME_START_MOBILE = 1000;
    int TIME_START_WEARABLE = 100;

    String TAG = "늑대와 함께 춤을";
    String START = "/startToSensorToGet";
    String STOP = "/stopToSensorToGet";


    String MOBILE_BROADCAST = "ItIsGoodDayToDieFrom";
    String WEARABLE_BROADCAST = "DoYouWannerBuildSnowManFr";

    String DIFFER = "differ";
    String FUSE = "fuse";


    String ACCURACY = "accuracy";
    String TIMESTAMP = "timestamp";
    String VALUES = "values";
    String TYPE = "type";

    String ACCELEROMETER = "accelerometer";
    String GYROSCOPE ="gyroscope";
    String MAGNETIC = "magnetic";
    String GYROSCOPE_TIME ="gyroscopetime";

    String MESSAGE = "message";


    String SEND_MOBILE = "/SendToMobileCats/";
    String SEND_WEARABLE = "/SendToWearableCats";

    float EPSILON = 0.000000001f;
    int TIME_CONSTANT = 100;
    float FILTER_COEFFICIENT = 0.95f;
    float NS2S = 1.0f / 1000000000.0f;
}
