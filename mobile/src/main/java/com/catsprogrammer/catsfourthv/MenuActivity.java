package com.catsprogrammer.catsfourthv;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import mehdi.sakout.fancybuttons.FancyButton;

public class MenuActivity extends AppCompatActivity {

    FancyButton btn_dir;
    FancyButton btn_test;
    FancyButton btn_ftp;
    FancyButton btn_setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        startActivity(new Intent(this, SplashActivity.class));

        btn_dir = (FancyButton)findViewById(R.id.btn_dir);
        btn_test = (FancyButton)findViewById(R.id.btn_test);
        btn_ftp = (FancyButton)findViewById(R.id.btn_ftp);
        btn_setting = (FancyButton)findViewById(R.id.btn_setting);
    }

    public void c_dir(View v){
        startActivity(new Intent(this, DirectionActivity.class));
    }

    public void c_test(View v){
        startActivity(new Intent(this, TestingActivity.class));
    }

    public void c_ftp(View v){
        startActivity(new Intent(this, FTPActivity.class));
    }

    public void c_setting(View v){
        startActivity(new Intent(this, SettingActivity.class));
    }

}
