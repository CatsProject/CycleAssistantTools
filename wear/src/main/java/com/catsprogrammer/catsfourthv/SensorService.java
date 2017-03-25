package com.catsprogrammer.catsfourthv;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;


import com.catsprogrammer.shared.TagName;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;

import static com.catsprogrammer.catsfourthv.WearableData.accData;
import static com.catsprogrammer.catsfourthv.WearableData.gyroData;
import static com.catsprogrammer.catsfourthv.WearableData.gyroTimeStamp;
import static com.catsprogrammer.catsfourthv.WearableData.magnetData;


/**
 * Created by XNOTE on 2016-08-07.
 */

public class SensorService extends Service implements SensorEventListener, TagName{


    SensorManager mSensorManager;
    //SensorFiltering sensorFiltering;
    //MobileClient client;
    MobileClient  client;
    int halfSecond = 0;
    private Timer fuseTimer;

    long timestamp = 0;

    private float[] acc = new float[3];
    private float[] gyro = new float[3];
    private float[] magnet = new float[3];

    private ScheduledExecutorService mScheduler;

    @Override
    public void onCreate() {

        IntentFilter intentFilter = new IntentFilter(WEARABLE_BROADCAST);

        client = MobileClient.getInstance(getApplicationContext());




        super.onCreate();
       // sensorFiltering = new SensorFiltering(this, false);
                //SensorFiltering.getInstance(this, false);
        startMeasurement();
    }

    @Override
    public void onDestroy() {
        stopMeasurement();

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected void startMeasurement() {
        fuseTimer = new Timer();
        fuseTimer.scheduleAtFixedRate(new digitalSensor(), 0, TIME_CONSTANT);

        mSensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));
            Sensor accelerometerSensor = mSensorManager.getDefaultSensor(SENS_ACCELEROMETER);
            Sensor gyroscopeSensor = mSensorManager.getDefaultSensor(SENS_GYROSCOPE);
            Sensor magneticSensor = mSensorManager.getDefaultSensor(SENS_MAGNETIC);


        // Register the listener
        if (mSensorManager != null) {
            if (accelerometerSensor != null) {
                mSensorManager.registerListener(this, accelerometerSensor , SensorManager.SENSOR_DELAY_GAME);
            }
            if (gyroscopeSensor != null) {
                mSensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_GAME);
            }
            if (magneticSensor != null) {
                mSensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_GAME);
            }
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i("Unbinded","곤니찌아");
        return super.onUnbind(intent);
    }

    private void stopMeasurement() {
        fuseTimer.cancel();
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
        }
        if (mScheduler != null && !mScheduler.isTerminated()) {
            mScheduler.shutdown();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) { //여기에서 DeviceClient 로 보내주는데..

        switch (event.sensor.getType()) {
            case SENS_ACCELEROMETER :
                acc = event.values;
                break;
            case SENS_GYROSCOPE :
                gyro = event.values;
                timestamp = event.timestamp;
                break;
            case SENS_MAGNETIC :
                magnet = event.values;
                break;
        }
        //client.sendSensorData();
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    public class digitalSensor extends TimerTask {

        @Override
        public void run() {

            for(int i = 0; i < 3; i++) {
                accData[halfSecond * 3 + i] = acc[i];
                gyroData[halfSecond * 3 + i] = gyro[i];
                magnetData[halfSecond * 3 + i] = magnet[i];
            }
            gyroTimeStamp[halfSecond] = timestamp;
            halfSecond++;

            if(halfSecond >= 5) {
                halfSecond = 0;
                client.sendSensorData();
            }
        }
    }




}