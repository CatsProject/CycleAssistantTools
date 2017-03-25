package com.catsprogrammer.catsfourthv;

import android.*;
import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.catsprogrammer.shared.TagName;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Timer;


import static com.catsprogrammer.catsfourthv.Data.checkSend;
import static com.catsprogrammer.catsfourthv.Data.differM;
import static com.catsprogrammer.catsfourthv.Data.differW;
import static com.catsprogrammer.catsfourthv.Data.fuseTimer;
import static com.catsprogrammer.catsfourthv.Data.fusedSensorM;
import static com.catsprogrammer.catsfourthv.Data.fusedSensorW;
import static com.catsprogrammer.catsfourthv.Data.isAlreadySend;



public class MobileMain extends AppCompatActivity implements TagName {
    MyReceiver myReceiver;

    private TextView[] mobileSensor = new TextView[6];
    private TextView[] wearableSensor = new TextView[6];
    private Button[] buttons = new Button[4];

    private int[] mobileSensorId = {R.id.fuseXm, R.id.fuseYm, R.id.fuseZm, R.id.differXm, R.id.differYm, R.id.differZm};
    private int[] wearableSensorId = {R.id.fuseXw, R.id.fuseYw, R.id.fuseZw, R.id.differXw, R.id.differYw, R.id.differZw};
    private int[] buttonId = {R.id.start, R.id.stop, R.id.record, R.id.save};


    private int que = 0;
    private int timeInter=-1;

    int num;


    private int pushTime = 30;
    public static Data data;


    int sendTime = 0;

    //Data data = 0;
    DecimalFormat d = new DecimalFormat("#.##");

    boolean isCorrect = true;

    FileOutputStream[] fileOutputStreams = new FileOutputStream[4];
    PrintWriter[] printWriters = new PrintWriter[4];

    String[] txtName = {"fuseMobile", "differMobile", "fuseWearable", "differWearable"};
    String txt = ".txt";

    File[] files = new File[4];

    GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //checkSend[0] = false;
        data = new Data();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_main);
        checkPermission();

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        Log.i("안녕", "연결");
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.i("안녕", "실패");
                    }
                })
                .addApi(AppIndex.API).build();
        googleApiClient.connect();


        for(int i = 0; i < SENS_SIZE; i++) {
            mobileSensor[i] = (TextView)findViewById(mobileSensorId[i]);
            wearableSensor[i] = (TextView)findViewById(wearableSensorId[i]);
        }
        for(int i = 0; i < BUTTON_SIZE; i++)  {
            buttons[i] = (Button)findViewById(buttonId[i]);
        }

        IntentFilter intentFilter = new IntentFilter(MOBILE_BROADCAST);
        myReceiver = new MyReceiver();
        registerReceiver(myReceiver,intentFilter);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (v.getId()) {
                    case R.id.start :
                        startService(new Intent(getApplicationContext(), SensorServiceM.class));
                        new SendWearable(START, TAG, googleApiClient).start();
                        for(int i = 0 ; i < 4; i++) {
                           // String s = "/mnt/sdcard/" + txtName[i] + ".txt";
                           // files[i] = new File(s);
                        }
                        checkSend = true;
                        break;
                    case R.id.stop :
                        if(fuseTimer != null) {
                            fuseTimer.cancel();
                        }

                        stopService(new Intent(getApplicationContext(), SensorServiceM.class));
                        new SendWearable(STOP, TAG, googleApiClient).start();
                        break;
                    case R.id.record :
                        try {
                            for (int i = 0; i < 4; i++) {
                                String s = "/mnt/sdcard/" + txtName[i] + ".txt";
                                //String s = Environment.getExternalStorageDirectory() +"/" +txtName[i] + txt;
                                //s = s +txtName[i] + txt;

                                fileOutputStreams[i] =  new FileOutputStream(s,true);
                                printWriters[i] = new PrintWriter(fileOutputStreams[i]);

                                pushTime = 30;
                            }
                            Toast.makeText(getApplicationContext(), "파일생성완료", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {

                            e.printStackTrace();
                        }
                        break;
                    case R.id.save:
                        try {
                            for (int i = 0; i < 4; i++) {
                                printWriters[i].println("\r");
                                printWriters[i].close();
                            }
                            Toast.makeText(getApplicationContext(), "저장 완료", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        break;
                }
            }
        };

        for(int i = 0; i <BUTTON_SIZE; i++) {
            buttons[i].setOnClickListener(onClickListener);
        }
    }

    @Override
    protected void onStop() {
        //stopService(new Intent(this, SensorServiceM.class));
        //new SendWearable(STOP, TAG, googleApiClient).start();
        //unregisterReceiver(myReceiver);
        //googleApiClient.disconnect();
        super.onStop();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(googleApiClient, getIndexApiAction());
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
       // googleApiClient.disconnect();
    }


    @Override
    protected void onDestroy() {
        stopService(new Intent(this, SensorServiceM.class));
        new SendWearable(STOP, TAG, googleApiClient).start();
        unregisterReceiver(myReceiver);
        isAlreadySend = false;
        checkSend = false;
        
        super.onDestroy();

    }

    @Override
    public void onStart() {
        super.onStart();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        googleApiClient.connect();

        //googleApiClient.connect();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.start(googleApiClient, getIndexApiAction());
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("MobileMain Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    public class MyReceiver extends BroadcastReceiver { //여기서 일단 보여주는 예정..

        public MyReceiver() {

        }
        @Override
        public void onReceive(Context context, Intent intent) {
            String tag = intent.getStringExtra("DataSend");

            if(tag.startsWith("data")) {

                if(checkSend) {
                    for (int i = 0; i < 3; i++) {
                        try {
                            mobileSensor[i].setText(d.format(fusedSensorM[sendTime][i] * 180 / Math.PI) + '°');
                            mobileSensor[i + 3].setText(d.format(differM[sendTime][i]));
                            wearableSensor[i].setText(d.format(fusedSensorW[sendTime][i] * 180 / Math.PI) + '°');
                            wearableSensor[i + 3].setText(d.format(differW[sendTime][i]));
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }

                    }
                    String[] str = new String[4];

                    Log.i("Timer3", " : " + sendTime);


                    if(printWriters[0] != null && pushTime > 0) {
                        str[0] =  fusedSensorM[sendTime][0] * 180 / Math.PI + " " + fusedSensorM[sendTime][1] * 180 / Math.PI + " " + fusedSensorM[sendTime][2] * 180 / Math.PI + " ";
                        str[1] =  differM[sendTime][0] + " " + differM[sendTime][1] + " " + differM[sendTime][2] + " ";
                        str[2] =  fusedSensorW[sendTime][0] * 180 / Math.PI + " " + fusedSensorW[sendTime][1] * 180 / Math.PI + " " + fusedSensorW[sendTime][2] * 180 / Math.PI + " ";

                        String[] st = new String[3];

                        for(int i = 0 ; i < 3; i++) {
                            if (differW[sendTime][i] == 0) {
                                st[i] = "0.00";
                            }
                            else {
                                st[i] = d.format(differW[sendTime][i]);
                            }
                        }

                        for (int i = 0; i < 3; i++) {
                            //int strLength = str[i].length();
                            String blankLength = "";
                            for(int j = st[i].length(); j < 7; j++) {
                                blankLength += " ";
                            }
                            st[i] = blankLength + st[i];
                        }
                        Log.i("LogLog", st[0]);

                        str[3] =  st[0] + " " + st[1] + " " + st[2] + " ";

                        for(int i = 0 ; i < 4; i++) {
                            printWriters[i].println(str[i] +"\r");
                        }
                        //pushTime --;

                        if(pushTime == 0) {
                            num++;
                            try {
                                for (int i = 0; i < 4; i++) {
                                    printWriters[i].println("\r");

                                    if(num == 10)  {
                                        printWriters[i].println("\r");
                                    }
                                    printWriters[i].close();
                                }
                                if(num == 10)
                                    num =0;
                                Toast.makeText(getApplicationContext(), "저장 완료", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "저장 안돼", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    Log.i("MobileMain0", d.format(fusedSensorM[sendTime][0]  * 180 / Math.PI)  + " (" + differM[sendTime][0] + ") "
                            + d.format(fusedSensorM[sendTime][1]  * 180 / Math.PI ) + " (" + differM[sendTime][1] + ") " +
                            d.format(fusedSensorM[sendTime][2]  * 180 / Math.PI) + " (" + differM[sendTime][2] + ") " );
                    Log.i("MobileMain1", d.format(fusedSensorW[sendTime][0]  * 180 / Math.PI)  + " (" + differW[sendTime][0] + ") "
                            + d.format(fusedSensorW[sendTime][1]  * 180 / Math.PI) + " (" + differW[sendTime][1] + ") " +
                            d.format(fusedSensorW[sendTime][2]  * 180 / Math.PI) + " (" + differW[sendTime][2] + ") " );



                    sendTime++;
                    if(sendTime >= 30)  {
                        {
                            sendTime = 0;
                        }
                    }

                }

            }
        }
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // Explain to the user why we need to write the permission.
                    //Toast.makeText(this, "11111", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                // MY_PERMISSION_REQUEST_STORAGE is an
                // app-defined int constant

            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
            }
        }
    }
}
