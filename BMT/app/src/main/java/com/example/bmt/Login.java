package com.example.bmt;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
public class Login extends AppCompatActivity {

    Connection con;
    SharedPreferences pref;
    public static ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        pref = getSharedPreferences("user_details",MODE_PRIVATE);
        if(pref.contains("username")){
            startActivity(new Intent(Login.this,mainact.class));
        }
    }
    public void login(View v){
        Login.dialog=ProgressDialog.show(this, "", "Please wait...", true);
        TextView te1=(TextView) findViewById(R.id.textView);
        EditText un=(EditText)findViewById(R.id.uname);
        EditText p=(EditText)findViewById(R.id.pass);
        String err="",r_code="",imei="",soft="";
        try{
            ConnectionHelper conhelper= new ConnectionHelper();
            con=conhelper.connectionclass();
            if(con!=null) {
                imei=pref.getString("imei",null);
                r_code=pref.getString("fcode",null);
                String q = "exec logincheck @imei='"+imei+"', @r_code='"+r_code+"',@user='"+un.getText().toString()+"',@pass='"+p.getText().toString()+"';";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(q);
                while(rs.next()){
                    err=rs.getString("err_msg");
                    soft=rs.getString("soft");
                }
            }
        }
        catch (Exception e) {
            Log.e("error",e.getMessage());
        }
        if(err.equals("Success")){
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("username",un.getText().toString());
            editor.putString("soft",soft);
//            Toast.makeText(Login.this, "Login Successfully",Toast.LENGTH_LONG).show();
            try{
                ConnectionHelper conhelper= new ConnectionHelper();
                con=conhelper.connectionclass();
                if(con!=null) {
                    r_code=pref.getString("fcode",null);
                    String q = "select top 1 FirmNo,Firm_Phone from Firm_mst where Cust_Code='"+r_code+"' and IsActive=1";
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery(q);
                    while(rs.next()){
                        editor.putString("soft_type",rs.getString("Firm_Phone"));
                        editor.apply();
                    }
                }
            }
            catch (Exception e){
                Log.e("error",e.getMessage());
            }
            startActivity(new Intent(Login.this,mainact.class));
        }
        else {
            te1.setVisibility(View.VISIBLE);
            Login.dialog.dismiss();
        }
    }
}