package com.example.bmt;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

public class mainact extends AppCompatActivity {
    SharedPreferences pref;
    Connection con;
    DateFormat adate,sdate;
    Dictionary data;
    List<String> list;
    public static ProgressDialog dialog;

    @Override
    public void onBackPressed() {
    }
    @Override
    protected void onResume() {
        super.onResume();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("");
        LayoutInflater inflator = (LayoutInflater) this .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.custom_imgview, null);
        actionBar.setCustomView(v);
        TextView tcp = findViewById(R.id.tcp);
        Spannable wordtoSpan = new SpannableString("Copyright 2022 Adro'iT iBS. All rights reserved.");
        wordtoSpan.setSpan(new ForegroundColorSpan(Color.BLUE), 15, 26, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tcp.setText(wordtoSpan);
        sdate = new SimpleDateFormat("yyyy-MM-dd");
        adate = new SimpleDateFormat("dd-MM-yyyy");
        pref = getSharedPreferences("user_details",MODE_PRIVATE);
        GridView gvdata= (GridView) findViewById(R.id.gvdata);
        data= new Hashtable();
        list= new ArrayList<String>();
        try{
            ConnectionHelper conhelper= new ConnectionHelper();
            con=conhelper.connectionclass();
            if(con!=null) {
                String softype=pref.getString("soft_type",null);
                String fc= pref.getString("fcode",null);
                String q="select FirmName,FirmNo,update_dttm from Firm_mst where Cust_Code='"+fc+"' and IsActive=1";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(q);
                while(rs.next()){
                    String udate=rs.getString("update_dttm");
                    Date date = sdate.parse(udate.split(" ")[0]);
                    String uutdate = adate.format(date);
                    String fname=rs.getString("FirmName")+" ("+uutdate+" "+udate.split(" ")[1].split("00")[0];
                    fname=fname.substring(0,fname.length()-1);
                    fname+=")";
                    data.put(fname,rs.getString("FirmNo"));
                    list.add(fname);
                }
                con.close();
            }
            if(pref.getString("fname"," ").equals(" ")) {
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("fname", list.get(0));
                editor.putString("fno", data.get(list.get(0)).toString());
                editor.apply();
            }
        }
        catch (Exception e) {
            Log.e("error",e.getMessage());
        }
        getdata();
        gvdata.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listgd ele=(listgd)gvdata.getItemAtPosition(position);
                mainact.dialog = ProgressDialog.show(mainact.this, "", "Please wait...", true);
                if(ele.getT_name().equals("Sales")) startActivity(new Intent(mainact.this,Sales.class));
                else if(ele.getT_name().equals("Receivable")) startActivity(new Intent(mainact.this,Receivable.class));
                else if(ele.getT_name().equals("Purchase")) startActivity(new Intent(mainact.this,Purchase.class));
                else if(ele.getT_name().equals("Ledger")) startActivity(new Intent(mainact.this,Ledger.class));
                else if(ele.getT_name().equals("Payable")) startActivity(new Intent(mainact.this,Payable.class));
                else if(ele.getT_name().equals("Challan")) startActivity(new Intent(mainact.this,Challan.class));
            }
        });
        mainact.dialog.dismiss();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainact);
        mainact.dialog = ProgressDialog.show(this, "", "Please wait...", true);
    }
    void getdata(){
        ArrayList<listgd> ArrayList = new ArrayList<listgd>();
        GridView gvdata= (GridView) findViewById(R.id.gvdata);
        TextView tfname = (TextView) findViewById(R.id.tcname);
        tfname.setText(pref.getString("fname",null));
        try {
            ConnectionHelper conhelper = new ConnectionHelper();
            con = conhelper.connectionclass();
            if (con != null) {
                String g = "";
                String softype=pref.getString("soft_type",null);
                String fno=pref.getString("fno",null);
                if (softype.equals("G") || softype.equals("Y")) {
                    g = "_G";
                }
                String q = "select(select update_dttm from firm_mst where firmno='" + fno + "') as dtime," +
                        "(select firmname from firm_mst where firmno='" + fno + "') as fname," +
                        "(select top 1 Cl_Balance from [Acc_Payable] where FirmNo='" + fno + "') as pay," +
                        "(select top 1 Cl_Balance from [Acc_Recievable] where FirmNo='" + fno + "') as rec," +
                        "(select top 1 Sales_Amt from [Get_sales_Amt] where FirmNo='" + fno + "' order by YearId) as sale," +
                        "(select top 1 Purchase_Amt from [Get_Purchase_Amt] where FirmNo='" + fno + "' order by YearId) as pur," +
                        "(select top 1 Ch_Amt from [Get_Chalan_Amt" + g + "] where FirmNo='" + fno + "' order by YearId) as ch";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(q);
                while (rs.next()) {
                    ArrayList.add(new listgd("Sales", rs.getString("sale")==null?"00":rs.getString("sale")));
                    ArrayList.add(new listgd("Purchase", rs.getString("pur")==null?"00":rs.getString("pur")));
                    ArrayList.add(new listgd("Receivable", rs.getString("rec")==null?"00":rs.getString("rec")));
                    ArrayList.add(new listgd("Payable", rs.getString("pay")==null?"00":rs.getString("pay")));
                    ArrayList.add(new listgd("Ledger", "-"));
                    ArrayList.add(new listgd("Challan", rs.getString("ch")==null?"00":rs.getString("ch")));
                }
                con.close();
                adapter ada = new adapter(this, ArrayList);
                gvdata.setAdapter(ada);
            }
        }
        catch (Exception e){
            Log.e("error",e.getMessage());
        }
    }
    public void logout(View v){
        pref = getSharedPreferences("user_details",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();
        startActivity(new Intent(mainact.this,mainactivity1.class));
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sample_menu, menu);
        MenuItem pedhi = menu.findItem(R.id.pedhi);
        for (String i:list){
            SpannableString spanString = new SpannableString(i);
            int end = spanString.length();
            spanString.setSpan(new RelativeSizeSpan(0.5f), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            SubMenu sub = pedhi.getSubMenu().addSubMenu(spanString);
        }
        MenuItem se= menu.findItem(R.id.search);
        se.setVisible(false);
        MenuItem item1 = menu.findItem(R.id.Sales);
        Intent intent1 = new Intent(this, Sales.class);
        item1.setIntent(intent1);
        MenuItem item2 = menu.findItem(R.id.Purchase);
        Intent intent2 = new Intent(this, Purchase.class);
        item2.setIntent(intent2);
        MenuItem item3 = menu.findItem(R.id.Receivable);
        Intent intent3 = new Intent(this, Receivable.class);
        item3.setIntent(intent3);
        MenuItem item4 = menu.findItem(R.id.Payable);
        Intent intent4 = new Intent(this, Payable.class);
        item4.setIntent(intent4);
        MenuItem item5 = menu.findItem(R.id.Ledger);
        Intent intent5 = new Intent(this, Ledger.class);
        item5.setIntent(intent5);
        MenuItem item6 = menu.findItem(R.id.Challan);
        Intent intent6 = new Intent(this, Challan.class);
        item6.setIntent(intent6);
        MenuItem item7 = menu.findItem(R.id.About_us);
        Intent intent7 = new Intent(this, About_us.class);
        item7.setIntent(intent7);
        MenuItem item8 = menu.findItem(R.id.home);
        item8.setVisible(false);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.Logout) {
            pref = getSharedPreferences("user_details",MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.clear();
            editor.apply();
            startActivity(new Intent(mainact.this,mainactivity1.class));
        }
        else if(id ==R.id.search||id== R.id.home||id==R.id.Purchase || id ==R.id.Sales||id== R.id.Receivable||id==R.id.Payable || id ==R.id.Ledger||id== R.id.Challan||id==R.id.About_us){
            mainact.dialog = ProgressDialog.show(this, "", "Please wait...", true);
        }
        else if(id==R.id.pedhi){
        }
        else{
            String mname = item.getTitle().toString();
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("fname",mname);
            editor.putString("fno",data.get(mname).toString());
            editor.apply();
            getdata();
        }
        return super.onOptionsItemSelected(item);
    }
    public void aboutus(View v){
        startActivity(new Intent(mainact.this,About_us.class));
    }
}