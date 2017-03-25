package com.catsprogrammer.catsfourthv;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Handler;
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
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Random;

import static com.catsprogrammer.catsfourthv.Data.checkSend;
import static com.catsprogrammer.catsfourthv.Data.differAll;
import static com.catsprogrammer.catsfourthv.Data.differM;
import static com.catsprogrammer.catsfourthv.Data.differW;
import static com.catsprogrammer.catsfourthv.Data.downCount;
import static com.catsprogrammer.catsfourthv.Data.fuseTimer;
import static com.catsprogrammer.catsfourthv.Data.isAlreadySend;
import static com.catsprogrammer.catsfourthv.Data.leftCount;
import static com.catsprogrammer.catsfourthv.Data.rightCount;
import static com.catsprogrammer.catsfourthv.Data.upCount;


public class TestingActivity extends AppCompatActivity implements TagName {

    private final Handler mHandler = new Handler();
    private Runnable mTimer1;
    private Runnable mTimer2;
    private Runnable mTimer3;
    private LineGraphSeries<DataPoint> mSeries1x;
    private LineGraphSeries<DataPoint> mSeries1y;
    private LineGraphSeries<DataPoint> mSeries1z;

    private LineGraphSeries<DataPoint> mSeries2x;
    private LineGraphSeries<DataPoint> mSeries2y;
    private LineGraphSeries<DataPoint> mSeries2z;

    private LineGraphSeries<DataPoint> mSeries3x;
    private LineGraphSeries<DataPoint> mSeries3y;
    private LineGraphSeries<DataPoint> mSeries3z;

    private double graph1LastXValue = 0d;
    private double graph2LastXValue = 0d;
    private double graph3LastXValue = 0d;


    TextView[] mobileSensor = new TextView[3];
    TextView[] watchSensor = new TextView[3];
    TextView[] subSensor = new TextView[3];

    int[] mobileId = {R.id.mobile_x, R.id.mobile_y, R.id.mobile_z};
    int[] watchId = {R.id.watch_x, R.id.watch_y, R.id.watch_z};
    int[] subId = {R.id.sub_x, R.id.sub_y, R.id.sub_z};
    private Button[] buttons = new Button[4];
    private int[] buttonId = {R.id.start, R.id.stop, R.id.record, R.id.save};

    TextView count_up, count_down, count_left, count_right;
    GoogleApiClient googleApiClient;
    MyReceiver myReceiver;
    FileOutputStream[] fileOutputStreams = new FileOutputStream[4];
    PrintWriter[] printWriters = new PrintWriter[4];
    DecimalFormat d = new DecimalFormat("#.##");

    int time  = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);

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
        //startService(new Intent(getApplicationContext(), SensorServiceM.class));
        //new SendWearable(START, TAG, googleApiClient).start();
        //checkSend = true;


        for(int i  = 0; i < 3; i++) {
            mobileSensor[i] = (TextView)findViewById(mobileId[i]);
            watchSensor[i] = (TextView)findViewById(watchId[i]);
            subSensor[i] = (TextView)findViewById(subId[i]);
        }
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (v.getId()) {
                    case R.id.start :
                        startService(new Intent(getApplicationContext(), SensorServiceM.class));
                        new SendWearable(START, TAG, googleApiClient).start();

                        checkSend = true;
                        break;
                    case R.id.stop :
                        if(fuseTimer != null) {
                            fuseTimer.cancel();
                        }
                        isAlreadySend = false;
                        checkSend = false;
                        stopService(new Intent(getApplicationContext(), SensorServiceM.class));
                        new SendWearable(STOP, TAG, googleApiClient).start();
                        break;
                    case R.id.record :
                        try {
                            /*
                            for (int i = 0; i < 4; i++) {
                                String s = "/mnt/sdcard/" + txtName[i] + ".txt";
                                //String s = Environment.getExternalStorageDirectory() +"/" +txtName[i] + txt;
                                //s = s +txtName[i] + txt;

                                fileOutputStreams[i] =  new FileOutputStream(s,true);
                                printWriters[i] = new PrintWriter(fileOutputStreams[i]);

                                pushTime = 30;
                            }
                            */
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

        for(int i = 0; i < 4; i++) {
            buttons[i] = (Button)findViewById(buttonId[i]);
            buttons[i].setOnClickListener(onClickListener);
        }

        count_up = (TextView)findViewById(R.id.count_up);
        count_down = (TextView)findViewById(R.id.count_down);
        count_left = (TextView)findViewById(R.id.count_left);
        count_right = (TextView)findViewById(R.id.count_right);

        GraphView graph = (GraphView)findViewById(R.id.graph_watch);
        mSeries1x = new LineGraphSeries<>();
        mSeries1y = new LineGraphSeries<>();
        mSeries1z = new LineGraphSeries<>();
        mSeries1x.setColor(Color.RED);
        mSeries1y.setColor(Color.BLUE);
        mSeries1z.setColor(Color.GREEN);
        graph.addSeries(mSeries1x);
        graph.addSeries(mSeries1y);
        graph.addSeries(mSeries1z);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(30);

        GraphView graph2 = (GraphView) findViewById(R.id.graph_mobile);
        mSeries2x = new LineGraphSeries<>();
        mSeries2y = new LineGraphSeries<>();
        mSeries2z = new LineGraphSeries<>();
        mSeries2x.setColor(Color.RED);
        mSeries2y.setColor(Color.BLUE);
        mSeries2z.setColor(Color.GREEN);
        graph2.addSeries(mSeries2x);
        graph2.addSeries(mSeries2y);
        graph2.addSeries(mSeries2z);
        graph2.getViewport().setXAxisBoundsManual(true);
        graph2.getViewport().setMinX(0);
        graph2.getViewport().setMaxX(30);

        GraphView graph3 = (GraphView) findViewById(R.id.graph_sub);
        mSeries3x = new LineGraphSeries<>();
        mSeries3y = new LineGraphSeries<>();
        mSeries3z = new LineGraphSeries<>();
        mSeries3x.setColor(Color.RED);
        mSeries3y.setColor(Color.BLUE);
        mSeries3z.setColor(Color.GREEN);
        graph3.addSeries(mSeries3x);
        graph3.addSeries(mSeries3y);
        graph3.addSeries(mSeries3z);
        graph3.getViewport().setXAxisBoundsManual(true);
        graph3.getViewport().setMinX(0);
        graph3.getViewport().setMaxX(30);
    }

    @Override
    public void onResume() {
        super.onResume();
        /*
        mTimer1 = new Runnable() {
            @Override
            public void run() {
                graph1LastXValue += 1d;
                mSeries1.appendData(new DataPoint(graph1LastXValue, getRandom()), true, 30);
                mHandler.postDelayed(this, 100);
            }
        };
        mHandler.postDelayed(mTimer1, 100);

        mTimer2 = new Runnable() {
            @Override
            public void run() {
                graph2LastXValue += 1d;
                mSeries2.appendData(new DataPoint(graph2LastXValue, getRandom()), true, 30);
                mHandler.postDelayed(this, 100);
            }
        };
        mHandler.postDelayed(mTimer2, 100);

        mTimer3 = new Runnable() {
            @Override
            public void run() {
                graph3LastXValue += 1d;
                mSeries3.appendData(new DataPoint(graph2LastXValue, getRandom()), true, 30);
                mHandler.postDelayed(this, 100);
            }
        };
        mHandler.postDelayed(mTimer3, 100);
        */
    }

    @Override
    public void onPause() {
        //mHandler.removeCallbacks(mTimer1);
        //mHandler.removeCallbacks(mTimer2);
        super.onPause();
    }

    @Override
    protected void onStop() {
        if(fuseTimer != null) {
            fuseTimer.cancel();
        }
        isAlreadySend = false;
        checkSend = false;
        stopService(new Intent(getApplicationContext(), SensorServiceM.class));
        new SendWearable(STOP, TAG, googleApiClient).start();
        unregisterReceiver(myReceiver);
        googleApiClient.disconnect();
        upCount = 0;
        downCount = 0;
        leftCount = 0;
        rightCount = 0;
        super.onStop();

    }

    public class MyReceiver extends BroadcastReceiver { //여기서 일단 보여주는 예정..

        public MyReceiver() {

        }
        @Override
        public void onReceive(Context context, Intent intent) {
            String tag = intent.getStringExtra("DataSend");

            if(checkSend) {
                for (int i = 0; i < 3; i++) {
                    mobileSensor[i].setText(d.format(differM[time][i]) + '°');
                    watchSensor[i].setText(d.format(differW[time][i]) + '°');
                    subSensor[i].setText(d.format(differAll[time][i]) + '°');
                }

                graph1LastXValue += 1d;
                graph2LastXValue += 1d;
                graph3LastXValue += 1d;
                mSeries1x.appendData(new DataPoint(graph1LastXValue, differW[time][0]), true, 30);
                mSeries1y.appendData(new DataPoint(graph1LastXValue, differW[time][1]), true, 30);
                mSeries1z.appendData(new DataPoint(graph1LastXValue, differW[time][2]), true, 30);

                mSeries2x.appendData(new DataPoint(graph2LastXValue, differM[time][0]), true, 30);
                mSeries2y.appendData(new DataPoint(graph2LastXValue, differM[time][1]), true, 30);
                mSeries2z.appendData(new DataPoint(graph2LastXValue, differM[time][2]), true, 30);

                mSeries3x.appendData(new DataPoint(graph3LastXValue, differAll[time][0]), true, 30);
                mSeries3y.appendData(new DataPoint(graph3LastXValue, differAll[time][1]), true, 30);
                mSeries3z.appendData(new DataPoint(graph3LastXValue, differAll[time][2]), true, 30);

                count_up.setText(""+upCount);
                count_down.setText(""+downCount);
                count_left.setText(""+leftCount);
                count_right.setText(""+rightCount);

                time ++;
                if(time >= 30)
                    time = 0;
            }
                //Log.i("들어왔나요 최종 모발", differ[0] +", " +differ[1]  + ", " + differ[2]);
                //Log.i("들어왔나요 최종 웨어", differWearable[0] +", " +differWearable[1]  + ", " + differWeble[2]);;
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
