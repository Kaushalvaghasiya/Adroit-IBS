package com.example.bmt;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    public static ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        MainActivity.dialog = ProgressDialog.show(this, "", "Please wait...", true);
        new logolaucher().start();
    }
    class logolaucher extends  Thread
    {
        public void  run()
        {
            try
            {
                sleep(500);
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
            Intent intent  = new Intent(MainActivity.this,mainactivity1.class);
            startActivity(intent);
        }
    }
}