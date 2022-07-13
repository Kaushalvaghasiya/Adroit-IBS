package com.example.bmt;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
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

public class mainactivity1 extends AppCompatActivity {
    Connection con;
    Dictionary data,fcode;
    SharedPreferences pref;
    public static ProgressDialog dialog;

    private String getMyPhoneNO() {
//        b
        return "044de05fb860f261";
//        g
//        return "d1e0998448ccbced";
//        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainactivity1);
        mainactivity1.dialog=ProgressDialog.show(this, "", "Please wait...", true);
        pref = getSharedPreferences("user_details",MODE_PRIVATE);
//        SharedPreferences.Editor editor = pref.edit();
//        editor.clear();
//        editor.commit();
        if(pref.contains("username")){
            startActivity(new Intent(mainactivity1.this,mainact.class));
        }
        else if (pref.contains("imei")){
            startActivity(new Intent(mainactivity1.this,Login.class));
        }
        EditText et=(EditText) findViewById(R.id.sekey);
        et.setText(getMyPhoneNO());
        TextView te1=(TextView) findViewById(R.id.textView2);
        try{
            ConnectionHelper conhelper= new ConnectionHelper();
            con=conhelper.connectionclass();
            data= new Hashtable();
            fcode= new Hashtable();
            if(con!=null) {
                String q = "select * from loginmaster;";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(q);
                while(rs.next()) {
                    data.put(rs.getString("IMEI"), rs.getString("id"));
                    fcode.put(rs.getString("IMEI"), rs.getString("firmcode"));
                }
                con.close();
            }
        }
        catch (Exception e) {
            Log.e("error",e.getMessage());
        }
        EditText sekey=(EditText) findViewById(R.id.sekey);
        if(data.get(sekey.getText().toString())==null){
            te1.setVisibility(View.VISIBLE);
            mainactivity1.dialog.dismiss();
        }
        else {
            SharedPreferences.Editor editor = pref.edit();
            String fc=fcode.get(sekey.getText().toString()).toString();
            editor.putString("imei",sekey.getText().toString());
            editor.putString("fcode",fc);
            if(pref.getString("fno"," ").equals(" ")) {
                try {
                    ConnectionHelper conhelper = new ConnectionHelper();
                    con = conhelper.connectionclass();
                    if (con != null) {
                        String q = "select top 1 firmno from firm_mst where cust_code='" + fc + "';";
                        Statement st = con.createStatement();
                        ResultSet rs = st.executeQuery(q);
                        while (rs.next()) {
                            editor.putString("fno", rs.getString("firmno"));
                        }
                        con.close();
                    }
                } catch (Exception e) {
                    Log.e("error", e.getMessage());
                }
            }
            editor.commit();
//            Toast.makeText(mainactivity1.this, "Device Verified",Toast.LENGTH_LONG).show();
            startActivity(new Intent(mainactivity1.this, Login.class));
        }
    }
    public void usewp(View v){
        String shareBody = "Serial Key : "+getMyPhoneNO();
        try {
            String text = shareBody;// Replace with your message.
            String toNumber = "919067706550";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://api.whatsapp.com/send?phone="+toNumber +"&text="+text));
            startActivity(intent);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public void useoapp(View v)  {

    }
}