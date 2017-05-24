package com.livelo.livelo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class settings extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Settings");
        setContentView(R.layout.activity_settings);
    }

    public void wifi(View view) {
        Switch s = (Switch) findViewById(R.id.switch_wifi);
        if(s.isChecked()){
            Toast toast = Toast.makeText(getApplicationContext(), "wifi on", Toast.LENGTH_SHORT);
            toast.show();
        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(), "wifi off", Toast.LENGTH_SHORT);
            toast.show();

        }
    }
    public void notifications(View view) {
        Switch s = (Switch) findViewById(R.id.switch_notification);
        if(s.isChecked()){
            Toast toast = Toast.makeText(getApplicationContext(), "notifications on", Toast.LENGTH_SHORT);
            toast.show();

        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(), "notifications off", Toast.LENGTH_SHORT);
            toast.show();

        }
    }
    public void autoUpload(View view) {
        Switch s = (Switch) findViewById(R.id.switch_auto);
        if(s.isChecked()){
            Toast toast = Toast.makeText(getApplicationContext(), "auto on", Toast.LENGTH_SHORT);
            toast.show();

        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(), "auto off", Toast.LENGTH_SHORT);
            toast.show();

        }
    }
}
