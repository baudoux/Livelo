package com.livelo.livelo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;


public class menu extends AppCompatActivity {
    TextView txt = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_menu);
        txt = (TextView) findViewById(R.id.last_upload);

        refresh_time();
    }

    public void goto_sensor(View view) {
        Intent intent = new Intent(this, sensors.class);
        startActivity(intent);
    }

    public void goto_collect(View view) {
        Intent intent = new Intent(this, collect_data.class);
        startActivity(intent);
    }

    public void goto_help(View view) {
        Intent intent = new Intent(this, help.class);
        startActivity(intent);
    }

    public void goto_settings(View view) {
        Intent intent = new Intent(this, settings.class);
        startActivity(intent);
    }

    public void send_data(View view) {
        Toast toast = Toast.makeText(getApplicationContext(), "sending data", Toast.LENGTH_SHORT);
        toast.show();

        // TODO if uploading succeed
        Calendar now = Calendar.getInstance();
        Global.last_upload = now.getTimeInMillis();
        refresh_time();
    }

    void refresh_time(){
        if(Global.last_upload == 0) {
            txt.setText("last uploading: never");
            return;
        }

        Calendar now = Calendar.getInstance();
        long diff =  now.getTimeInMillis() - Global.last_upload;
        long days = diff / (1000); //24*60*60;
        txt.setText("last uploading: " + (days == 0?( "today"):(days == 1? "one day ago": days + " days ago")));
    }
}
