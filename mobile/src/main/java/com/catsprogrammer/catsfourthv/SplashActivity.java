package com.catsprogrammer.catsfourthv;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (Build.VERSION.SDK_INT >= 23) {
            checkPermission();
        }
        Handler hd = new Handler();
        hd.postDelayed(new Runnable(){
            @Override
            public void run() {
                finish();
            }
        },3000);
    }


    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(android.Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (shouldShowRequestPermissionRationale(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // Explain to the user why we need to write the permission.
                    //Toast.makeText(this, "11111", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.ACCESS_NETWORK_STATE,android.Manifest.permission.INTERNET}, 1);

                // MY_PERMISSION_REQUEST_STORAGE is an
                // app-defined int constant

            } else {
                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.ACCESS_NETWORK_STATE,android.Manifest.permission.INTERNET},
                        1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                        || grantResults[1] == PackageManager.PERMISSION_GRANTED
                        || grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Permission not granted", Toast.LENGTH_SHORT).show();
                }
        }

    }
}