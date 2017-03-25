package com.catsprogrammer.catsfourthv;

import android.content.Intent;

import com.catsprogrammer.shared.TagName;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by XNOTE on 2016-08-07.
 */
public class ListenerServiceW extends WearableListenerService implements TagName {


    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

       //Log.i(TAG,messageEvent.getPath());

        if (messageEvent.getPath().equals(START)) {
            startService(new Intent(this, SensorService.class));
        }
        if (messageEvent.getPath().equals(STOP)) {
            stopService(new Intent(this, SensorService.class));
        }
    }
}
