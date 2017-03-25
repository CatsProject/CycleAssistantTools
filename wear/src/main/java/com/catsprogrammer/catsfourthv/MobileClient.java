package com.catsprogrammer.catsfourthv;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseLongArray;

import com.catsprogrammer.shared.TagName;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.catsprogrammer.catsfourthv.WearableData.accData;
import static com.catsprogrammer.catsfourthv.WearableData.gyroData;
import static com.catsprogrammer.catsfourthv.WearableData.gyroTimeStamp;
import static com.catsprogrammer.catsfourthv.WearableData.magnetData;

/**
 * Created by XNOTE on 2016-08-07.
 */
public class MobileClient implements  GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, TagName{

    public static MobileClient instance;

    public long lastTime = 0;

    public static  MobileClient getInstance(Context context) {
        if (instance == null) {
            instance = new  MobileClient(context.getApplicationContext());
        }

        return instance;
    }

    //지역변수 사용.
    private Context context;
    private GoogleApiClient googleApiClient;
    private SparseLongArray lastSensorData;
    private ExecutorService executorService;

    private static final int CLIENT_CONNECTION_TIMEOUT = 15000;


    private MobileClient(Context context) {
        googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        lastSensorData = new SparseLongArray();
        executorService = Executors.newCachedThreadPool();
    }



    public void sendSensorData() {

        new sendSensorDataInBackground().start();


        /*executorService.submit(new Runnable() { // thread를 통해서 핸드폰에다 보내주는것 같음.
            @Override
            public void run() {
                sendSensorDataInBackground();
            }
        });*/
    }


    public class sendSensorDataInBackground extends Thread {

        public sendSensorDataInBackground() {

        }
        @Override
        public void run() {
            PutDataMapRequest dataMap = PutDataMapRequest.create(SEND_MOBILE);

            //Log.i(TAG, fusedOrientation[0] +","+ fusedOrientation[1] +"," + fusedOrientation[2]);

            dataMap.getDataMap().putFloatArray(ACCELEROMETER, accData);
            dataMap.getDataMap().putFloatArray(GYROSCOPE, gyroData);
            dataMap.getDataMap().putFloatArray(MAGNETIC, magnetData);

            dataMap.getDataMap().putLongArray(GYROSCOPE_TIME, gyroTimeStamp);
            //currentTime[0]= System.currentTimeMillis();
            //dataMap.getDataMap().putLongArray(TIMESTAMP, currentTime);

            Log.i("탈주닌자", accData[0] + "," + accData[3] + "," + accData[6] + "," + accData[9] + "," +accData[12]);

            PutDataRequest putDataRequest = dataMap.asPutDataRequest();
            send(putDataRequest);
            super.run();
        }

        private void send(PutDataRequest putDataRequest) {

            if (validateConnection()) {
                Wearable.DataApi.putDataItem(googleApiClient, putDataRequest).setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                    @Override
                    public void onResult(DataApi.DataItemResult dataItemResult) {
                        Log.i(TAG, "Sending sensor data: " + dataItemResult.getStatus().isSuccess());
                    }
                });
            }
        }

        private boolean validateConnection() {
            if (googleApiClient.isConnected()) {
                return true;
            }

            ConnectionResult result = googleApiClient.blockingConnect(CLIENT_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);

            return result.isSuccess();
        }
    }


/*
    private void sendSensorDataInBackground() { //value의 근원을 찾아라~



        currentTime[0] = System.currentTimeMillis();

        if(currentTime[0] != 0) {
            if(lastTime == 0) {
            }
            else {
                if(currentTime[0] - lastTime < 490 )
                    return;
            }
        }
        lastTime = currentTime[0];


        PutDataMapRequest dataMap = PutDataMapRequest.create(SEND_MOBILE);
        Log.i("탈주닌자", differ[0] + "," + differ[1] + "," + differ[2] );

        //Log.i(TAG, fusedOrientation[0] +","+ fusedOrientation[1] +"," + fusedOrientation[2]);

        dataMap.getDataMap().putFloatArray(FUSE, acc);
        dataMap.getDataMap().putFloatArray(DIFFER, gyro);
        dataMap.getDataMap().putLongArray(TIMESTAMP, currentTime);





        PutDataRequest putDataRequest = dataMap.asPutDataRequest();
        send(putDataRequest);
    }


    private boolean validateConnection() {
        if (googleApiClient.isConnected()) {
            return true;
        }

        ConnectionResult result = googleApiClient.blockingConnect(CLIENT_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);

        return result.isSuccess();
    }

    private void send(PutDataRequest putDataRequest) {

        if (validateConnection()) {
            Wearable.DataApi.putDataItem(googleApiClient, putDataRequest).setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                @Override
                public void onResult(DataApi.DataItemResult dataItemResult) {
                    Log.i(TAG, "Sending sensor data: " + dataItemResult.getStatus().isSuccess());
                }
            });
        }
    }

*/
    //연결 지연
    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "지연");
    }

    //연결 실패 ㅋ
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "실패");
    }

    //연결 됬을 때
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "안녕");
        // new SendDataToWatch("/to_wearable", "연결연결").start();
    }
}