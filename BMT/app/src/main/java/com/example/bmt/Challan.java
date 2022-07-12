package com.example.bmt;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
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
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

public class Challan extends AppCompatActivity {
    SharedPreferences pref;
    Connection con;
    String fdate,tdate;
    cadapter cada;
    cgadapter cgada;
    GridView gvdata;
    cyadapter cyada;
    DateFormat adate,sdate;
    public static ProgressDialog dialog;
    Dictionary data;
    List<String> list;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onResume() {
        super.onResume();
        Challan.dialog.dismiss();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challan);
        Challan.dialog = ProgressDialog.show(this, "", "Please wait...", true);
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
        fdate="2022-04-01";
        pref = getSharedPreferences("user_details",MODE_PRIVATE);
        data= new Hashtable();
        list = new ArrayList<String>();
        try {
            ConnectionHelper conhelper = new ConnectionHelper();
            con = conhelper.connectionclass();
            if (con != null) {
                String softype = pref.getString("soft_type", null);
                String fc = pref.getString("fcode", null);
                String q = "select FirmName,FirmNo,update_dttm from Firm_mst where Cust_Code='" + fc + "' and IsActive=1";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(q);
                while(rs.next()){
                    String udate=rs.getString("update_dttm");
                    date = sdate.parse(udate.split(" ")[0]);
                    String uutdate = adate.format(date);
                    String fname=rs.getString("FirmName")+" ("+uutdate+" "+udate.split(" ")[1].split("00")[0];
                    fname=fname.substring(0,fname.length()-1);
                    fname+=")";
                    data.put(fname,rs.getString("FirmNo"));
                    list.add(fname);
                }
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
                TextView btype=popupView.findViewById(R.id.tbtype);
                btype.setVisibility(View.INVISIBLE);
                Spinner sp=popupView.findViewById(R.id.tbtyped);
                sp.setVisibility(View.INVISIBLE);
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
                    @SuppressLint("ResourceAsColor")
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onClick(View v) {
                        final Calendar c = Calendar.getInstance();
                        int mYear, mMonth, mDay;
                        mYear = c.get(Calendar.YEAR);
                        mMonth = c.get(Calendar.MONTH);
                        mDay = c.get(Calendar.DAY_OF_MONTH);
                        DatePickerDialog datePickerDialog = new DatePickerDialog(Challan.this,R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
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
                    @SuppressLint("ResourceAsColor")
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onClick(View v) {
                        final Calendar c = Calendar.getInstance();
                        int mYear, mMonth, mDay;
                        mYear = c.get(Calendar.YEAR);
                        mMonth = c.get(Calendar.MONTH);
                        mDay = c.get(Calendar.DAY_OF_MONTH);
                        DatePickerDialog datePickerDialog = new DatePickerDialog(Challan.this,R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
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

    @Override
    protected void onStart() {
        super.onStart();
        Challan.dialog.dismiss();
    }
    public void getdata() {
        gvdata = (GridView) findViewById(R.id.gvdata);
        String fno = pref.getString("fno", null);
        TextView tfname = (TextView) findViewById(R.id.tcname);
        tfname.setText(pref.getString("fname",null));
        try {
            ConnectionHelper conhelper= new ConnectionHelper();
            con=conhelper.connectionclass();
            String softtype=pref.getString("soft_type",null);
            if (softtype.equals("G")) {
                ArrayList<ChallanCardG> ArrayList = new ArrayList<ChallanCardG>();
                if (con != null) {
                    String q = "select  * from ChalanMst_View_G where BillDate between '" + fdate + "' and '" + tdate + "' and FirmNo='" + fno + "' order by BillDate desc,BillNo desc;";
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery(q);
                    while (rs.next()) {
                        String bdate=rs.getString("billdate").split(" ")[0];
                        Date date = sdate.parse(bdate);
                        bdate = adate.format(date);
                        NumberFormat df = new DecimalFormat("#0.000");
                        NumberFormat df2 = new DecimalFormat("#0.00");
                        ArrayList.add(new ChallanCardG(rs.getString("AcName"), rs.getString("Deli_Party"),
                                rs.getString("Transport"), rs.getString("BillNo"),
                                bdate, df2.format(Double.parseDouble(rs.getString("Notes"))),
                                rs.getString("RTime"), df.format(Double.parseDouble(rs.getString("TotTaka"))),
                                df.format(Double.parseDouble(rs.getString("TotWt"))), df.format(Double.parseDouble(rs.getString("TotMtrs"))),
                                rs.getString("BillAmount"),rs.getString("C_Id")));
                    }
                }
                cgada= new cgadapter(this, ArrayList);
                LinearLayout ll = findViewById(R.id.btnLay);
                paging p = new paging(this,ll,10,cgada,gvdata,"cgada");
                p.Btnfooter();
                p.addata();
                gvdata.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ChallanCardG ele = (ChallanCardG) gvdata.getItemAtPosition(position);
                        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                        View popupView = inflater.inflate(R.layout.challan_popupcard_g, null);
                        int width = LinearLayout.LayoutParams.MATCH_PARENT;
                        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                        boolean focusable = true; // lets taps outside the popup also dismiss it
                        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
                        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                        TextView tcname = popupView.findViewById(R.id.tbpnamed);
                        tcname.setText(ele.tcname);
                        TextView tchnod = popupView.findViewById(R.id.tchnod);
                        tchnod.setText(ele.chno);
                        TextView tchdd = popupView.findViewById(R.id.tchdd);
                        tchdd.setText(ele.chd);
                        TextView trated = popupView.findViewById(R.id.trated);
                        trated.setText(ele.rate);
                        TextView tdpnamed = popupView.findViewById(R.id.tdpnamed);
                        tdpnamed.setText(ele.dp);
                        Button bc = popupView.findViewById(R.id.bclose);
                        GridView gvdata = popupView.findViewById(R.id.gvdata);
                        bc.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                popupWindow.dismiss();
                            }
                        });
                        ArrayList<PurchasePopupCardin> ArrayList = new ArrayList<PurchasePopupCardin>();
                        String q = "select * from chalandtlview_G where Ch_id='"+ele.C_Id+"' and  FirmNo='"+fno+"' order by Srno asc";
                        try{
                            Statement st = con.createStatement();
                            ResultSet rs = st.executeQuery(q);
                            NumberFormat df = new DecimalFormat("#0.00");
                            double sumpsc=0,summtr=0;
                            while (rs.next()) {
                                String psc=rs.getString("Pcs"),mtr=rs.getString("Mtrs");
                                ArrayList.add(new PurchasePopupCardin(
                                        rs.getString("TakaNO"),rs.getString("Pcs").split("\\.")[0],
                                        df.format(Double.parseDouble(rs.getString("Mtrs"))),
                                        rs.getString("RC2"),
                                        rs.getString("ProdName")));
                                sumpsc+=Double.parseDouble(psc);
                                summtr+=Double.parseDouble(mtr);
                            }
                            ArrayList.add(new PurchasePopupCardin(
                                    "Total -->>",String.valueOf((int)sumpsc),
                                    df.format(summtr),"",""));
                            pinadapter ada = new pinadapter(getApplicationContext(), ArrayList);
                            gvdata.setAdapter(ada);
                        }
                        catch (Exception e){
                            Log.e("error",e.getMessage());
                        }
                    }
                });
            }
            else if (softtype.equals("B")){
                ArrayList<ChallanCardG> ArrayList = new ArrayList<ChallanCardG>();
                if (con != null) {
                    String q = "select  * from ChalanMst_View where BillDate between '" + fdate + "' and '" + tdate + "' and FirmNo='" + fno + "' order by BillDate desc,BillNo desc;";
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery(q);
                    while (rs.next()) {
                        String bdate=rs.getString("billdate").split(" ")[0];
                        Date date = sdate.parse(bdate);
                        bdate = adate.format(date);
                        NumberFormat df = new DecimalFormat("#0.000");
                        NumberFormat df2 = new DecimalFormat("#0.00");
                        ArrayList.add(new ChallanCardG(rs.getString("AcName"), rs.getString("Deli_Party"),
                                rs.getString("Transport"), rs.getString("BillNo"),
                                bdate, df2.format(Double.parseDouble(rs.getString("Notes"))),
                                rs.getString("RTime"), df.format(Double.parseDouble(rs.getString("TotTaka"))),
                                df.format(Double.parseDouble(rs.getString("TotWt"))), df.format(Double.parseDouble(rs.getString("TotMtrs"))),
                                rs.getString("BillAmount"),rs.getString("C_Id")));
                    }
                }
                cgada= new cgadapter(this, ArrayList);
                LinearLayout ll = findViewById(R.id.btnLay);
                paging p = new paging(this,ll,10,cgada,gvdata,"cgada");
                p.Btnfooter();
                p.addata();
                gvdata.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ChallanCardG ele = (ChallanCardG) gvdata.getItemAtPosition(position);
                        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                        View popupView = inflater.inflate(R.layout.challan_popupcard_b, null);
                        int width = LinearLayout.LayoutParams.MATCH_PARENT;
                        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                        boolean focusable = true; // lets taps outside the popup also dismiss it
                        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
                        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                        TextView tcname = popupView.findViewById(R.id.tbpnamed);
                        tcname.setText(ele.tcname);
                        TextView tchnod = popupView.findViewById(R.id.tchnod);
                        tchnod.setText(ele.chno);
                        TextView tchdd = popupView.findViewById(R.id.tchdd);
                        tchdd.setText(ele.chd);
                        TextView trated = popupView.findViewById(R.id.trated);
                        trated.setText(ele.rate);
                        TextView tdpnamed = popupView.findViewById(R.id.tdpnamed);
                        tdpnamed.setText(ele.dp);
                        Button bc = popupView.findViewById(R.id.bclose);
                        GridView gvdata = popupView.findViewById(R.id.gvdata);
                        bc.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                popupWindow.dismiss();
                            }
                        });
                        ArrayList<SalesPopUpCardGin> ArrayList = new ArrayList<SalesPopUpCardGin>();
                        String q = "select * from chalandtlview where Ch_id='"+ele.C_Id+"' and  FirmNo='"+fno+"' order by Srno asc";
                        try{
                            Statement st = con.createStatement();
                            ResultSet rs = st.executeQuery(q);
                            NumberFormat df = new DecimalFormat("#0.00");
                            double sumpsc=0,summtr=0,suma=0;
                            int i=1;
                            while (rs.next()) {
                                String psc=rs.getString("Pcs"),mtr=rs.getString("Mtrs");
                                String billa=rs.getString("billamount");
                                ArrayList.add(new SalesPopUpCardGin(String.valueOf(i),
                                        rs.getString("prodname"),rs.getString("Pcs").split("\\.")[0],
                                        "0.00", df.format(Double.parseDouble(rs.getString("Mtrs"))),"",
                                        rs.getString("rate"),
                                        rs.getString("billamount")));
                                i++;
                                suma+=Double.parseDouble(billa);
                                sumpsc+=Double.parseDouble(psc);
                                summtr+=Double.parseDouble(mtr);
                            }
                            ArrayList.add(new SalesPopUpCardGin("",
                                    "Total -->>",String.valueOf((int)sumpsc),"",
                                    df.format(summtr),"","",df.format(suma)));
                            sginadapter ada = new sginadapter(getApplicationContext(), ArrayList);
                            gvdata.setAdapter(ada);
                        }
                        catch (Exception e){
                            Log.e("error",e.getMessage());
                        }
                    }
                });
            }
            else if (softtype.equals("Y")) {
                ArrayList<ChallanCardY> ArrayList = new ArrayList<ChallanCardY>();
                if (con != null) {
                    String q = "select  * from ChalanMst_View_G where BillDate between '" + fdate + "' and '" + tdate + "' and FirmNo='" + fno + "' order by BillDate desc,BillNo desc;";
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery(q);
                    while (rs.next()) {
                        String bdate=rs.getString("billdate").split(" ")[0];
                        Date date = sdate.parse(bdate);
                        bdate = adate.format(date);
                        NumberFormat df = new DecimalFormat("#0.000");
                        ArrayList.add(new ChallanCardY(rs.getString("AcName"), rs.getString("Deli_Party"),
                                rs.getString("Transport"), rs.getString("BillNo"),
                                bdate, rs.getString("Grade"),
                                rs.getString("LotNo"), rs.getString("ShadeNO"),
                                df.format(Double.parseDouble(rs.getString("TotTaka"))), rs.getString("TotWt"),
                                df.format(Double.parseDouble(rs.getString("TotMtrs"))),rs.getString("BillAmount"), rs.getString("C_Id")));
                    }
                }
                cyada= new cyadapter(this, ArrayList);
                LinearLayout ll = findViewById(R.id.btnLay);
                paging p = new paging(this,ll,10,cyada,gvdata,"cyada");
                p.Btnfooter();
                p.addata();
                gvdata.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ChallanCardY ele = (ChallanCardY) gvdata.getItemAtPosition(position);
                        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                        View popupView = inflater.inflate(R.layout.challan_popupcard_y, null);
                        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                        boolean focusable = true; // lets taps outside the popup also dismiss it
                        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
                        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 50);
                        TextView tcname = popupView.findViewById(R.id.tcname);
                        tcname.setText(ele.tcname);
                        TextView tchnod = popupView.findViewById(R.id.tchnod);
                        tchnod.setText(ele.chno);
                        TextView tchdd = popupView.findViewById(R.id.tchdd);
                        tchdd.setText(ele.chd);
                        TextView tcrtnd = popupView.findViewById(R.id.tcrtnd);
                        tcrtnd.setText(ele.totcr);
                        TextView ttotwtd = popupView.findViewById(R.id.ttotwtd);
                        ttotwtd.setText(ele.netwt);
                        TextView ttotchd = popupView.findViewById(R.id.ttotchd);
                        ttotchd.setText(ele.totch);
                        TextView tdepd = popupView.findViewById(R.id.tdepd);
                        tdepd.setText(ele.dp);
                        TextView tshd = popupView.findViewById(R.id.tshd);
                        tshd.setText(ele.shno);
                        TextView tlotd = popupView.findViewById(R.id.tlotd);
                        tlotd.setText(ele.lotn);
                        TextView tgraded = popupView.findViewById(R.id.tgraded);
                        tgraded.setText(ele.grade);
                        Button bc = popupView.findViewById(R.id.bclose);
                        GridView gvdata = popupView.findViewById(R.id.gvdata);
                        bc.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                popupWindow.dismiss();
                            }
                        });
                        ArrayList<SalesPopUpCardBin> ArrayList = new ArrayList<SalesPopUpCardBin>();
                        String q = "select * from chalandtlview_G where Ch_id='"+ele.C_Id+"' and  FirmNo='"+fno+"' order by Srno asc";
                        try{
                            Statement st = con.createStatement();
                            ResultSet rs = st.executeQuery(q);
                            NumberFormat df = new DecimalFormat("#0.000");
                            while (rs.next()) {
                                ArrayList.add(new SalesPopUpCardBin(rs.getString("ProdName"),
                                        rs.getString("TakaNO"),df.format(Double.parseDouble(rs.getString("Pcs"))),
                                        df.format(Double.parseDouble(rs.getString("Mtrs"))),rs.getString("Grade_D")));
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
                ArrayList<ChallanCard> ArrayList = new ArrayList<ChallanCard>();
                if (con != null) {
                    String q = "select  * from ChalanMst_View where BillDate between '" + fdate + "' and '" + tdate + "' and FirmNo='" + fno + "' order by BillDate desc,BillNo desc;";
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery(q);
                    while (rs.next()) {
                        String bdate=rs.getString("billdate").split(" ")[0];
                        Date date = sdate.parse(bdate);
                        bdate = adate.format(date);
                        NumberFormat df = new DecimalFormat("#0.000");
                        ArrayList.add(new ChallanCard(rs.getString("AcName"), rs.getString("Deli_Party"),
                                rs.getString("Transport"), rs.getString("BillNo"),
                                bdate, rs.getString("Notes"),
                                df.format(Double.parseDouble(rs.getString("Pcs"))), df.format(Double.parseDouble(rs.getString("Qty"))),
                                rs.getString("BillAmount"), rs.getString("C_Id")));
                    }
                }
                cada = new cadapter(this, ArrayList);
                LinearLayout ll = findViewById(R.id.btnLay);
                paging p = new paging(this,ll,10,cada,gvdata,"cada");
                p.Btnfooter();
                p.addata();
                gvdata.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ChallanCard ele = (ChallanCard) gvdata.getItemAtPosition(position);
                        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                        View popupView = inflater.inflate(R.layout.challan_popupcard_y, null);
                        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                        boolean focusable = true; // lets taps outside the popup also dismiss it
                        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
                        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 50);
                        TextView tcname = popupView.findViewById(R.id.tcname);
                        tcname.setText(ele.tcname);
                        TextView tchnod = popupView.findViewById(R.id.tchnod);
                        tchnod.setText(ele.chno);
                        TextView tchdd = popupView.findViewById(R.id.tchdd);
                        tchdd.setText(ele.chd);
                        TextView ttotchd = popupView.findViewById(R.id.ttotchd);
                        ttotchd.setText(ele.chamt);
                        TextView tdepd = popupView.findViewById(R.id.tdepd);
                        tdepd.setText(ele.dp);
                        Button bc = popupView.findViewById(R.id.bclose);
                        GridView gvdata = popupView.findViewById(R.id.gvdata);
                        bc.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                popupWindow.dismiss();
                            }
                        });
                        ArrayList<SalesPopUpCardBin> ArrayList = new ArrayList<SalesPopUpCardBin>();
                        String q = "select * from chalandtlview where Ch_id='"+ele.C_Id+"' and  FirmNo='"+fno+"' order by Srno asc";
                        try{
                            Statement st = con.createStatement();
                            ResultSet rs = st.executeQuery(q);
                            NumberFormat df = new DecimalFormat("#0.000");
                            while (rs.next()) {
                                ArrayList.add(new SalesPopUpCardBin(
                                        rs.getString("PM"),df.format(Double.parseDouble(rs.getString("Pcs"))),
                                        df.format(Double.parseDouble(rs.getString("Mtrs"))),
                                        rs.getString("Rate"),rs.getString("Amt")));
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
        } catch (Exception e) {
            Log.e("error", e.getMessage());
        }
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
                LinearLayout ll = findViewById(R.id.btnLay);
                paging p;
                if(softtype.equals("G")){
                    cgada.getFilter().filter(newText);
                    p = new paging(Challan.this,ll,10,cgada,gvdata,"cgada");
                }
                else if(softtype.equals("Y")){
                    cyada.getFilter().filter(newText);
                    p = new paging(Challan.this,ll,10,cyada,gvdata,"cyada");
                }
                else{
                    cada.getFilter().filter(newText);
                    p = new paging(Challan.this,ll,10,cada,gvdata,"cada");
                }
                p.Btnfooter();
                p.addata();
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
        else if(id ==R.id.search||id== R.id.home||id==R.id.Purchase || id ==R.id.Sales||id== R.id.Receivable||id==R.id.Payable || id ==R.id.Ledger||id== R.id.Challan||id==R.id.About_us){
            Challan.dialog = ProgressDialog.show(this, "", "Please wait...", true);
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
}