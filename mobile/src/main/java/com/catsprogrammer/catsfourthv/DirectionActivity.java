package com.catsprogrammer.catsfourthv;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;



import com.catsprogrammer.shared.TagName;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.DecimalFormat;

import static android.R.attr.left;
import static com.catsprogrammer.catsfourthv.Data.checkSend;
import static com.catsprogrammer.catsfourthv.Data.downStack;
import static com.catsprogrammer.catsfourthv.Data.fuseTimer;
import static com.catsprogrammer.catsfourthv.Data.isAlreadySend;
import static com.catsprogrammer.catsfourthv.Data.leftStack;
import static com.catsprogrammer.catsfourthv.Data.rightStack;
import static com.catsprogrammer.catsfourthv.Data.upStack;


public class DirectionActivity extends AppCompatActivity implements TagName {
    MyReceiver myReceiver;


    int sendTime = 0;

    Data data;
    DecimalFormat d = new DecimalFormat("#.##");

    boolean isCorrect = true;



    FileOutputStream[] fileOutputStreams = new FileOutputStream[4];
    PrintWriter[] printWriters = new PrintWriter[4];
    String[] txtName = {"fuseMobile.txt", "differMobile.txt", "fuseWearable.txt", "differWearable.txt"};

    GoogleApiClient googleApiClient;

    ImageView dir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        data = new Data();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        Log.i("안녕","연결");
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.i("안녕","실패");
                    }
                })
                .addApi(AppIndex.API).build();
        googleApiClient.connect();

        IntentFilter intentFilter = new IntentFilter(MOBILE_BROADCAST);
        myReceiver = new MyReceiver();
        registerReceiver(myReceiver,intentFilter);

        //
        startService(new Intent(getApplicationContext(), SensorServiceM.class));
        new SendWearable(START, TAG, googleApiClient).start();
        checkSend = true;

        dir = (ImageView)findViewById(R.id.dir_img);

        Animation startAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation);
        dir.startAnimation(startAnimation);

    }





    @Override
    protected void onStop() {

        //googleApiClient.disconnect();
        super.onStop();
        if(fuseTimer != null) {
            fuseTimer.cancel();
        }
        isAlreadySend = false;
        checkSend = false;
        stopService(new Intent(getApplicationContext(), SensorServiceM.class));
        unregisterReceiver(myReceiver);
        new SendWearable(STOP, TAG, googleApiClient).start();
    }
    @Override
    public void onStart() {
        super.onStart();
        //googleApiClient.connect();

    }

    @Override
    protected void onDestroy() {
       // googleApiClient.disconnect();
        super.onDestroy();
    }

    public class MyReceiver extends BroadcastReceiver { //여기서 일단 보여주는 예정..

        public MyReceiver() {

        }
        @Override
        public void onReceive(Context context, Intent intent) {
            String tag = intent.getStringExtra("DataSend");

            if(tag.startsWith("data")) {
                //Log.i("들어왔나요 최종 모발", differ[0] +", " +differ[1]  + ", " + differ[2]);
                //Log.i("들어왔나요 최종 웨어", differWearable[0] +", " +differWearable[1]  + ", " + differWeble[2]);;

                if(leftStack[3]) {
                    dir.setImageResource(R.drawable.left_dir);
                }
                else if(rightStack[3]) {
                    dir.setImageResource(R.drawable.right_dir);
                }
                else if(upStack[3]) {
                    dir.setImageResource(R.drawable.up);
                }else if(downStack[3]) {
                    dir.setImageResource(R.drawable.down);
                }

                else {
                    dir.setImageResource(R.drawable.dot);
                }
                /*
                if(isCorrect) {

                    for(int i = 0 ; i < 3; i++) {
                        differWearable[i] = data.wearableDiffer[(que+10 - timeInter) % 10][i];
                    }
                    for (int i = 0; i < 3; i++) {
                        try {
                            mobileSensor[i].setText(d.format(fusedOrientation[i] * 180 / Math.PI) + '°');
                            mobileSensor[i + 3].setText(d.format(differ[i]));
                            wearableSensor[i].setText(d.format(savedFused[(sendTime * 3) + i] * 180 / Math.PI) + '°');
                            wearableSensor[i + 3].setText(d.format(savedDiffer[(sendTime * 3) + i]));
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }

                    }
                    String[] str = new String[4];

                    str[0] = "1 " + fusedOrientation[0] * 180 / Math.PI + " " + fusedOrientation[1] * 180 / Math.PI + " " + fusedOrientation[2] * 180 / Math.PI + "\r\n";
                    str[1] = "2 " + differ[0] + " " + differ[1] + " " + differ[2] + "\r\n";
                    str[2] = "3 " + savedFused[(sendTime * 3)] * 180 / Math.PI + " " + savedFused[(sendTime * 3) + 1] * 180 / Math.PI + " " + savedFused[(sendTime * 3) + 2] * 180 / Math.PI + "\r\n";
                    str[3] = "4 " +savedDiffer[(sendTime * 3)] + " " +savedDiffer[(sendTime * 3) + 1] + " " +savedDiffer[(sendTime * 3) + 2] + "\r\n";

                    if (printWriters[0] != null) {
                        for (int i = 0; i < 4; i++) {
                            printWriters[i].println(str[i]);
                        }
                    }
                    sendTime++;
                    if(sendTime >= 5)  {
                        if(checkSend[0]) {
                            sendTime = 0;
                            checkSend[0] = false;
                        }
                        else {
                            sendTime = 4;
                        }
                    }

                }
                */
            }
        }
    }
}
