package com.example.bmt;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
@SuppressLint("ResourceAsColor")
public class Sales extends AppCompatActivity {
    SharedPreferences pref;
    Connection con;
    String fdate,tdate,btype="All";
    sgadapter sgada;
    syadapter syada;
    sbadapter sbada;
    GridView gvdata;
    DateFormat adate,sdate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);
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
        Date date = new Date();
        tdate=sdate.format(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, -1);
        date = cal.getTime();
        fdate=sdate.format(date);
        pref = getSharedPreferences("user_details",MODE_PRIVATE);
        String fno = pref.getString("fno", null);
        Spinner tfname = (Spinner) findViewById(R.id.tcname);
        Dictionary data;
        data= new Hashtable();
        List<String> list = new ArrayList<String>();
        try {
            ConnectionHelper conhelper = new ConnectionHelper();
            con = conhelper.connectionclass();
            if (con != null) {
                String softype = pref.getString("soft_type", null);
                String fc = pref.getString("fcode", null);
                String q = "select FirmName,FirmNo,update_dttm from Firm_mst where Cust_Code='" + fc + "' and IsActive=1";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(q);
                while (rs.next()) {
                    String udate=rs.getString("update_dttm");
                    Date date1 = sdate.parse(udate.split(" ")[0]);
                    String uutdate = adate.format(date1);
                    data.put(rs.getString("FirmName")+" ("+uutdate+" "+udate.split(" ")[1]+")",rs.getString("FirmNo"));
                    list.add(rs.getString("FirmName")+" ("+uutdate+" "+udate.split(" ")[1]+")");
                }
                ArrayAdapter aa = new ArrayAdapter(Sales.this, R.layout.spinner_list, list);
                aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                tfname.setAdapter(aa);
                tfname.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("fname",tfname.getItemAtPosition(position).toString());
                        editor.putString("fno",data.get(tfname.getItemAtPosition(position)).toString());
                        editor.apply();
                        getdata();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        tfname.setSelection(tfname.getFirstVisiblePosition());
                    }
                });
            }
        }
        catch (Exception e){
            Log.e("error",e.getMessage());
        }
        getdata();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.filter, null);
                try {
                    ConnectionHelper conhelper = new ConnectionHelper();
                    con = conhelper.connectionclass();
                    List<String> list = new ArrayList<String>();
                    list.add("All");
                    if (con != null) {
                        String q = "select distinct billtype from salbillmst_view where FirmNo='"+fno+"'";
                        Statement st = con.createStatement();
                        ResultSet rs = st.executeQuery(q);
                        while (rs.next()) {
                            list.add(rs.getString("billtype"));
                        }
                    }
                    Spinner sp = popupView.findViewById(R.id.tbtyped);
                    ArrayAdapter aa = new ArrayAdapter(popupView.getContext(), android.R.layout.simple_spinner_item,list);
                    aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp.setAdapter(aa);
                    sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            btype=sp.getItemAtPosition(position).toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
                catch (Exception e){
                    Log.e("error",e.getMessage());
                }
                // create the popup window
                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true; // lets taps outside the popup also dismiss it
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
                popupWindow.showAtLocation(v,Gravity.CENTER, 0, 50);
                EditText edfdate= (EditText) popupView.findViewById(R.id.fdated);
                EditText edtdate= (EditText) popupView.findViewById(R.id.tdated);
                Date date = null;
                try {
                    date = sdate.parse(fdate);
                    String ffdate = adate.format(date);
                    edfdate.setText(ffdate);
                    date = sdate.parse(tdate);
                    String ttdate = adate.format(date);
                    edtdate.setText(ttdate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                edfdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Calendar c = Calendar.getInstance();
                        int mYear, mMonth, mDay;
                        mYear = c.get(Calendar.YEAR);
                        mMonth = c.get(Calendar.MONTH);
                        mDay = c.get(Calendar.DAY_OF_MONTH);
                        DatePickerDialog datePickerDialog = new DatePickerDialog(Sales.this,R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {
                                    edfdate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                }
                            }, mYear, mMonth, mDay);
                        datePickerDialog.show();
                        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(R.color.blue);
                        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(R.color.blue);
                    }
                });
                edtdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Calendar c = Calendar.getInstance();
                        int mYear, mMonth, mDay;
                        mYear = c.get(Calendar.YEAR);
                        mMonth = c.get(Calendar.MONTH);
                        mDay = c.get(Calendar.DAY_OF_MONTH);
                        DatePickerDialog datePickerDialog = new DatePickerDialog(Sales.this,R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                edtdate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            }
                        }, mYear, mMonth, mDay);
                        datePickerDialog.show();
                        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(R.color.blue);
                        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(R.color.blue);
                    }
                });
                Button bc=(Button) popupView.findViewById(R.id.bclose);
                bc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });
                Button ba=(Button) popupView.findViewById(R.id.bapply);
                ba.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            EditText edfdate = (EditText)popupView.findViewById(R.id.fdated);
                            Date date = adate.parse(edfdate.getText().toString());
                            fdate = sdate.format(date);
                            EditText edtdate = (EditText)popupView.findViewById(R.id.tdated);
                            date = adate.parse(edtdate.getText().toString());
                            tdate = sdate.format(date);
                            getdata();
                            popupWindow.dismiss();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
    public void getdata(){
        gvdata= (GridView) findViewById(R.id.gvdata);
        String fno = pref.getString("fno", null);
        try{
            ConnectionHelper conhelper= new ConnectionHelper();
            con=conhelper.connectionclass();
            String softtype=pref.getString("soft_type",null);
            if (softtype.equals("G")) {
                ArrayList<SalesCardG> ArrayList = new ArrayList<SalesCardG>();
                if (con != null) {
                    String q="";
                    if(btype.equals("All")) {
                        q = "select  * from salbillmst_view where BillDate between '" + fdate + "' and '" + tdate + "' and FirmNo='" + fno + "' order by BillDate desc,BillNO desc;";
                    }
                    else{
                        q = "select  * from salbillmst_view where billtype='"+btype+"' and BillDate between '" + fdate + "' and '" + tdate + "' and FirmNo='" + fno + "' order by BillDate desc,BillNO desc;";
                    }
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery(q);
                    while (rs.next()) {
                        String bdate=rs.getString("billdate").split(" ")[0];
                        Date date = sdate.parse(bdate);
                        bdate = adate.format(date);
                        ArrayList.add(new SalesCardG(rs.getString("acname"), rs.getString("billno"),
                                rs.getString("billtype"), rs.getString("chno"),
                                bdate, rs.getString("qty"),
                                rs.getString("pcs"), rs.getString("chese"),
                                rs.getString("taxable_amt"), rs.getString("gstrs"),
                                rs.getString("billamount"),rs.getString("AcId")));
                    }
                }
                sgada= new sgadapter(this, ArrayList);
                gvdata.setAdapter(sgada);
                gvdata.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        SalesCardG ele = (SalesCardG) gvdata.getItemAtPosition(position);
                        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                        View popupView = inflater.inflate(R.layout.sales_popupcard_g, null);
                        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                        boolean focusable = true; // lets taps outside the popup also dismiss it
                        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
                        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 50);
                        TextView tcname = popupView.findViewById(R.id.tcname);
                        tcname.setText(ele.tcname);
                        TextView tbnod = popupView.findViewById(R.id.tbnod);
                        tbnod.setText(ele.bno);
                        TextView tbdated = popupView.findViewById(R.id.tbdated);
                        tbdated.setText(ele.bdate);
                        TextView ttotqd = popupView.findViewById(R.id.ttotqd);
                        ttotqd.setText(ele.pcs);
                        TextView ttomtrsd = popupView.findViewById(R.id.ttomtrsd);
                        ttomtrsd.setText(ele.totmrts);
                        TextView ttaxad = popupView.findViewById(R.id.ttaxad);
                        ttaxad.setText(ele.taxa);
                        TextView tgstad = popupView.findViewById(R.id.tgstad);
                        tgstad.setText(ele.gsta);
                        TextView tbillad = popupView.findViewById(R.id.tbillad);
                        tbillad.setText(ele.bamt);
                        Button bc = popupView.findViewById(R.id.bclose);
                        GridView gvdata = popupView.findViewById(R.id.gvdata);
                        bc.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                popupWindow.dismiss();
                            }
                        });
                        ArrayList<SalesPopUpCardBin> ArrayList = new ArrayList<SalesPopUpCardBin>();
                        String q = "select * from salbilldtl_view where billno='"+ele.bno+"' and AcId='"+ele.AcId+"' and  FirmNo='"+fno+"' order by Srno asc;";
                        try{
                            Statement st = con.createStatement();
                            ResultSet rs = st.executeQuery(q);
                            while (rs.next()) {
                                ArrayList.add(new SalesPopUpCardBin(
                                        rs.getString("Qty1"),rs.getString("Pcs"),rs.getString("Mtrs"), rs.getString("Rate_Disc"),rs.getString("Amt")));
                            }
                            sbinadapter ada = new sbinadapter(getApplicationContext(), ArrayList);
                            gvdata.setAdapter(ada);
                        }
                        catch (Exception e){
                            Log.e("error",e.getMessage());
                        }
                    }
                });
            }
            else if (softtype.equals("Y")) {
                ArrayList<SalesCardY> ArrayList = new ArrayList<SalesCardY>();
                if (con != null) {
                    String q="";
                    if(btype.equals("All")) {
                        q = "select  * from salbillmst_view where BillDate between '" + fdate + "' and '" + tdate + "' and FirmNo='" + fno + "' order by BillDate desc,BillNO desc;";
                    }
                    else{
                        q = "select  * from salbillmst_view where billtype='"+btype+"' and BillDate between '" + fdate + "' and '" + tdate + "' and FirmNo='" + fno + "' order by BillDate desc,BillNO desc;";
                    }
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery(q);
                    while (rs.next()) {
                        String bdate=rs.getString("billdate").split(" ")[0];
                        Date date = sdate.parse(bdate);
                        bdate = adate.format(date);
                        ArrayList.add(new SalesCardY(rs.getString("acname"), rs.getString("billno"),
                                rs.getString("billtype"), rs.getString("chno"),
                                bdate, rs.getString("qty"),
                                rs.getString("pcs"), rs.getString("chese"),
                                rs.getString("taxable_amt"), rs.getString("gstrs"),
                                rs.getString("billamount"),rs.getString("AcId")));
                    }
                }
                syada= new syadapter(this, ArrayList);
                gvdata.setAdapter(syada);
                gvdata.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        SalesCardY ele = (SalesCardY) gvdata.getItemAtPosition(position);
                        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                        View popupView = inflater.inflate(R.layout.sales_popupcard_y, null);
                        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                        boolean focusable = true; // lets taps outside the popup also dismiss it
                        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
                        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 50);
                        TextView tcname = popupView.findViewById(R.id.tcname);
                        tcname.setText(ele.tcname);
                        TextView tbnod = popupView.findViewById(R.id.tbnod);
                        tbnod.setText(ele.bno);
                        TextView tbdated = popupView.findViewById(R.id.tbdated);
                        tbdated.setText(ele.bdate);
                        TextView ttotqd = popupView.findViewById(R.id.ttotqd);
                        ttotqd.setText(ele.box);
                        TextView ttotwtd = popupView.findViewById(R.id.ttotwtd);
                        ttotwtd.setText(ele.netwt);
                        TextView ttaxad = popupView.findViewById(R.id.ttaxad);
                        ttaxad.setText(ele.taxa);
                        TextView tgstad = popupView.findViewById(R.id.tgstad);
                        tgstad.setText(ele.gsta);
                        TextView tbillad = popupView.findViewById(R.id.tbillad);
                        tbillad.setText(ele.bamt);
                        Button bc = popupView.findViewById(R.id.bclose);
                        GridView gvdata = popupView.findViewById(R.id.gvdata);
                        bc.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                popupWindow.dismiss();
                            }
                        });
                        ArrayList<SalesPopUpCardBin> ArrayList = new ArrayList<SalesPopUpCardBin>();
                        String q = "select * from salbilldtl_view where billno='"+ele.bno+"' and AcId='"+ele.AcId+"' and  FirmNo='"+fno+"' order by Srno asc;";
                        try{
                            Statement st = con.createStatement();
                            ResultSet rs = st.executeQuery(q);
                            while (rs.next()) {
                                ArrayList.add(new SalesPopUpCardBin(
                                        rs.getString("Pcs"),rs.getString("Qty1"),rs.getString("Mtrs"),rs.getString("Rate_Disc"),rs.getString("Amt")));
                            }
                            sbinadapter ada = new sbinadapter(getApplicationContext(), ArrayList);
                            gvdata.setAdapter(ada);
                        }
                        catch (Exception e){
                            Log.e("error",e.getMessage());
                        }
                    }
                });
            }
            else {
                ArrayList<SalesCardB> ArrayList = new ArrayList<SalesCardB>();
                if (con != null) {
                    String q="";
                    if(btype.equals("All")) {
                        q = "select  * from salbillmst_view where BillDate between '" + fdate + "' and '" + tdate + "' and FirmNo='" + fno + "' order by BillDate desc,BillNO desc;";
                    }
                    else{
                        q = "select  * from salbillmst_view where billtype='"+btype+"' and BillDate between '" + fdate + "' and '" + tdate + "' and FirmNo='" + fno + "' order by BillDate desc,BillNO desc;";
                    }
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery(q);
                    while (rs.next()) {
                        String bdate=rs.getString("billdate").split(" ")[0];
                        Date date = sdate.parse(bdate);
                        bdate = adate.format(date);
                        ArrayList.add(new SalesCardB(rs.getString("acname"), rs.getString("billno"),
                                rs.getString("billtype"), rs.getString("chno"),
                                bdate, rs.getString("qty"),
                                rs.getString("pcs"),
                                rs.getString("taxable_amt"), rs.getString("gstrs"),
                                rs.getString("billamount"),rs.getString("SB_Id")));
                    }
                }
                sbada = new sbadapter(this, ArrayList);
                gvdata.setAdapter(sbada);
                gvdata.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        SalesCardB ele = (SalesCardB) gvdata.getItemAtPosition(position);
                        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                        View popupView = inflater.inflate(R.layout.sales_popupcard_b, null);
                        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                        boolean focusable = true; // lets taps outside the popup also dismiss it
                        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
                        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 50);
                        TextView tcname = popupView.findViewById(R.id.tcname);
                        tcname.setText(ele.tcname);
                        TextView tbnod = popupView.findViewById(R.id.tbnod);
                        tbnod.setText(ele.bno);
                        TextView tbdated = popupView.findViewById(R.id.tbdated);
                        tbdated.setText(ele.bdate);
                        TextView ttotqd = popupView.findViewById(R.id.ttotqd);
                        ttotqd.setText(ele.qty);
                        TextView ttaxad = popupView.findViewById(R.id.ttaxad);
                        ttaxad.setText(ele.taxa);
                        TextView tgstad = popupView.findViewById(R.id.tgstad);
                        tgstad.setText(ele.gsta);
                        TextView tbillad = popupView.findViewById(R.id.tbillad);
                        tbillad.setText(ele.bamt);
                        Button bc = popupView.findViewById(R.id.bclose);
                        GridView gvdata = popupView.findViewById(R.id.gvdata);
                        bc.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                popupWindow.dismiss();
                            }
                        });
                        ArrayList<SalesPopUpCardBin> ArrayList = new ArrayList<SalesPopUpCardBin>();
                        String q = "exec salbilldt_prc @SB_Id='"+ele.SB_Id+"',@FirmNO='"+fno+ "';";
                        try{
                            Statement st = con.createStatement();
                            ResultSet rs = st.executeQuery(q);
                            while (rs.next()) {
                                ArrayList.add(new SalesPopUpCardBin(
                                        rs.getString("Qnty")," X ",rs.getString("Rate_Disc")+" "+rs.getString("CDP")," = ",rs.getString("Amt")));
                            }
                            sbinadapter ada = new sbinadapter(getApplicationContext(), ArrayList);
                            gvdata.setAdapter(ada);
                        }
                        catch (Exception e){
                            Log.e("error",e.getMessage());
                        }
                    }
                });
            }
        }
        catch (Exception e) {
            Log.e("error",e.getMessage());
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sample_menu, menu);
        MenuItem se = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) se.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String softtype=pref.getString("soft_type",null);
                if(softtype.equals("G")){
                    sgada.getFilter().filter(newText);
                }
                else if(softtype.equals("Y")){
                    syada.getFilter().filter(newText);
                }
                else{
                    sbada.getFilter().filter(newText);
                }
                return true;
            }
        });
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
        Intent intent8 = new Intent(this, mainact.class);
        item8.setIntent(intent8);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.Logout) {
            pref = getSharedPreferences("user_details",MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.clear();
            editor.apply();
            startActivity(new Intent(this,mainactivity1.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}