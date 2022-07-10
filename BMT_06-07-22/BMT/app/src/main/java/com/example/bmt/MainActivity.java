package com.example.bmt;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.*;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Dictionary;
import java.util.Hashtable;

public class MainActivity extends AppCompatActivity {
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        dialog = ProgressDialog.show(this, "", "Please wait...", true);
        logolaucher logolaucher = new logolaucher();
        logolaucher.start();
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
            finish();
        }
    }
}