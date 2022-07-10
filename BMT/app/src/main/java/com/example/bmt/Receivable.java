package com.example.bmt;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.FileProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;

import java.io.File;
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

public class Receivable extends AppCompatActivity {
    SharedPreferences pref;
    Connection con;
    String tdate, btype="All";
    GridView gvdata;
    radapter recada;
    DateFormat sdate,adate;
    Dictionary data;
    public static ProgressDialog dialog;

    List<String> list;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receivable);
        Receivable.dialog = ProgressDialog.show(this, "", "Please wait...", true);
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
                try {
                    ConnectionHelper conhelper = new ConnectionHelper();
                    con = conhelper.connectionclass();
                    List<String> list = new ArrayList<String>();
                    list.add("All");
                    if (con != null) {
                        String fno = pref.getString("fno", null);
                        String q = "select acname from SalBillMst_Outstand_summary where FirmNo='"+fno+"';";
                        Statement st = con.createStatement();
                        ResultSet rs = st.executeQuery(q);
                        while (rs.next()) {
                            list.add(rs.getString("acname"));
                        }
                        con.close();
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
                edfdate.setVisibility(View.INVISIBLE);
                TextView fdatei=popupView.findViewById(R.id.fdate);
                fdatei.setVisibility(View.INVISIBLE);
                EditText edtdate= (EditText) popupView.findViewById(R.id.tdated);
                Date date = null;
                try {
                    date = sdate.parse(tdate);
                    String ttdate = adate.format(date);
                    edtdate.setText(ttdate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                edtdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Calendar c = Calendar.getInstance();
                        int mYear, mMonth, mDay;
                        mYear = c.get(Calendar.YEAR);
                        mMonth = c.get(Calendar.MONTH);
                        mDay = c.get(Calendar.DAY_OF_MONTH);
                        DatePickerDialog datePickerDialog = new DatePickerDialog(Receivable.this,R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                edtdate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
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
                            EditText edtdate = (EditText) popupView.findViewById(R.id.tdated);
                            Date date = adate.parse(edtdate.getText().toString());
                            tdate = sdate.format(date);
                            popupWindow.dismiss();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        Receivable.dialog.dismiss();
    }
    public void getdata() {
        ArrayList<RecCard> ArrayList = new ArrayList<RecCard>();
        gvdata = (GridView) findViewById(R.id.gvdata);
        TextView tfname = (TextView) findViewById(R.id.tcname);
        tfname.setText(pref.getString("fname",null));
        String fno = pref.getString("fno", null);
        try {
            ConnectionHelper conhelper = new ConnectionHelper();
            con = conhelper.connectionclass();
            if (con != null) {
                String q="";
                if(btype.equals("All")) {
                    q = "select * from SalBillMst_Outstand_Summary where FirmNo='"+fno+"' order by AcName;";
                }
                else{
                    q = "select * from SalBillMst_Outstand_Summary where acname='"+btype+"' and FirmNo='"+fno+"' order by AcName;";
                }
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(q);
                while (rs.next()) {
                    ArrayList.add(new RecCard(rs.getString("acname"), rs.getString("phone"),
                            rs.getString("totalbill"),rs.getString("paid_amt"),
                            rs.getString("unpaid_amt"),rs.getString("billamount"),
                            rs.getString("AcId")));
                }
                con.close();
            }
        } catch (Exception e) {
            Log.e("error", e.getMessage());
        }
        recada = new radapter(this, ArrayList);
        LinearLayout ll = findViewById(R.id.btnLay);
        paging p = new paging(this,ll,10,recada,gvdata,"recada");
        p.Btnfooter();
        p.addata();
        gvdata.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RecCard ele = (RecCard)gvdata.getItemAtPosition(position);
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.pay_rec_popupcard, null);
                int width = LinearLayout.LayoutParams.MATCH_PARENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true; // lets taps outside the popup also dismiss it
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
                popupWindow.showAtLocation(view,Gravity.CENTER, 0, 0);
                TextView tpnamed = popupView.findViewById(R.id.tpnamed);
                tpnamed.setText(ele.tcname);
                TextView tphone = popupView.findViewById(R.id.tphoned);
                tphone.setText(ele.phone);
                GridView gvdata=(GridView) popupView.findViewById(R.id.gvdata);
                ArrayList<RecPopupCardin> ArrayList = new ArrayList<RecPopupCardin>();
                String q = "select * from SalBillMst_Outstand where FirmNo='"+fno+"' and AcId='"+ele.AcId+"' order by BillNO asc;";
                try{
                    ConnectionHelper conhelper = new ConnectionHelper();
                    con = conhelper.connectionclass();
                    if (con != null) {
                        Statement st = con.createStatement();
                        ResultSet rs = st.executeQuery(q);
                        double count=0,sumbamt=0,sumpamt=0,sumuamt=0;
                        NumberFormat df2 = new DecimalFormat("#0.00");
                        while (rs.next()) {
                            String bdate=rs.getString("BillDate").split(" ")[0];
                            Date date = sdate.parse(bdate);
                            bdate = adate.format(date);
                            String billamt=rs.getString("BillAmount"),pamt=rs.getString("PaidAmt"),uamt=rs.getString("UnPaidAmt");
                            ArrayList.add(new RecPopupCardin(bdate,rs.getString("BillNO"),df2.format(Double.parseDouble(billamt))
                                    ,rs.getString("days"), df2.format(Double.parseDouble(pamt)), df2.format(Double.parseDouble(uamt))
                                    , ""));
                            sumbamt+=Double.parseDouble(billamt);
                            sumpamt+=Double.parseDouble(pamt);
                            sumuamt+=Double.parseDouble(uamt);
                            count++;
                        }
                        con.close();
                        ArrayList.add(new RecPopupCardin("Total -->>",String.valueOf((int)count) ,df2.format(sumbamt)
                                ,"", df2.format(sumpamt), df2.format(sumuamt)
                                , ""));
                    }
                    recinadapter ada = new recinadapter(getApplicationContext(), ArrayList);
                    gvdata.setAdapter(ada);
                }
                catch (Exception e){
                    Log.e("error",e.getMessage());
                }
                ImageButton igdpdf = (ImageButton) popupView.findViewById(R.id.tdpdf);
                igdpdf.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
                        dir.mkdirs();
                        String fileName = "outstanding_sales_aibs.pdf";
                        File myFile = new File(dir, fileName);
                        try {
                            PdfDocument pdfDoc = new PdfDocument(new PdfWriter(myFile));
                            Document doc = new Document(pdfDoc, new PageSize(595, 842));
                            doc.setMargins(0, 0, 0, 0);

                            Table table = new Table(new float[7]).useAllAvailableWidth();
                            table.setMargin(30);
                            table.setTextAlignment(TextAlignment.CENTER);
                            table.setWidth(UnitValue.createPercentValue(100));
                            table.setBorder(new SolidBorder(0));
                            // first row
                            String fname = pref.getString("fname", null);
                            Cell cell1 = new Cell(1, 7);
                            cell1.add(new Paragraph(fname.split("\\(")[0]).setFontSize(17).setBold().setFontColor(ColorConstants.BLUE));
                            String fno = pref.getString("fno", null);
                            try {
                                ConnectionHelper conhelper = new ConnectionHelper();
                                con = conhelper.connectionclass();
                                if (con != null) {
                                    String q = "select firmadd,firmcity from firm_mst where FirmNo='" + fno + "';";
                                    Statement st = con.createStatement();
                                    ResultSet rs = st.executeQuery(q);
                                    while (rs.next()) {
                                        String add= rs.getString("firmadd");
                                        String city=rs.getString("firmcity");
                                        if(add.equals(city)){
                                            cell1.add(new Paragraph(add+".").setFontSize(12));
                                        }
                                        else{
                                            cell1.add(new Paragraph(add+", "+city+".").setFontSize(12));
                                        }
                                    }
                                    cell1.add(new Paragraph("OUTSTANDING (RECEIVABLE)").setFontSize(12));
                                    String atdate = tdate;
                                    Date date = sdate.parse(atdate);
                                    atdate = adate.format(date);
                                    cell1.add(new Paragraph("Till : " + atdate).setFontSize(12).setBold());
                                    cell1.setTextAlignment(TextAlignment.CENTER);
                                    table.addCell(cell1);
                                    Cell cell2 = new Cell(1, 4);
                                    cell2.add(new Paragraph().add(new Paragraph("PARTY : ").setFontSize(10).setBold()).add(new Paragraph(ele.tcname).setBold().setFontColor(ColorConstants.BLUE)));
                                    cell2.add(new Paragraph("PHONE : " + ele.phone).setFontSize(10).setBold());
                                    cell2.setTextAlignment(TextAlignment.LEFT);
                                    table.addCell(cell2);

                                    Cell cell3 = new Cell(1, 3);
                                    cell3.add(new Paragraph("DATA ").setFontSize(10));
                                    cell3.add(new Paragraph("BROKER : ").setFontSize(10));
                                    cell3.setTextAlignment(TextAlignment.CENTER);
                                    cell3.setPadding(4);
                                    table.addCell(cell3);
//                                    PdfFont body = PdfFontFactory.createFont();
                                    table.addCell(new Cell().add(new Paragraph("DATE").setBold()).setPaddings(0,10,0,10).setFontColor(ColorConstants.BLUE));
                                    table.addCell(new Cell().add(new Paragraph("BILL NO").setBold()).setPaddings(0,10,0,10).setFontColor(ColorConstants.BLUE));
                                    table.addCell(new Cell().add(new Paragraph("BILL AMOUNT").setBold()).setPaddings(0,10,0,10).setFontColor(ColorConstants.BLUE));
                                    table.addCell(new Cell().add(new Paragraph("DAYS").setBold()).setPaddings(0,10,0,10).setFontColor(ColorConstants.BLUE));
                                    table.addCell(new Cell().add(new Paragraph("PAID\nAMOUNT").setBold()).setPaddings(0,10,0,10).setFontColor(ColorConstants.BLUE));
                                    table.addCell(new Cell().add(new Paragraph("UNPAID\nAMOUNT").setBold()).setPaddings(0,10,0,10).setFontColor(ColorConstants.BLUE));
                                    table.addCell(new Cell().add(new Paragraph("INTEREST\nAMOUNT").setBold()).setPaddings(0,10,0,10).setFontColor(ColorConstants.BLUE));

                                    q = "select * from SalBillMst_Outstand where FirmNo='" + fno + "' and acname='" + ele.tcname + "' and BillDate <='"+tdate+"' order by AcName;";
                                    st = con.createStatement();
                                    rs = st.executeQuery(q);
                                    boolean b = false;
                                    while (rs.next()) {
                                        String bdt = rs.getString("billdate").split(" ")[0];
                                        String billamt=rs.getString("billamount"),bno=rs.getString("billno");
                                        Date bdate = sdate.parse(bdt);
                                        bdt = adate.format(bdate);
                                        table.addCell(new Cell().add(new Paragraph(bdt)).setBorder(Border.NO_BORDER).setBackgroundColor(b ? new DeviceRgb(232,232,232) : ColorConstants.WHITE));
                                        table.addCell(new Cell().add(new Paragraph(bno)).setBorder(Border.NO_BORDER).setBackgroundColor(b ? new DeviceRgb(232,232,232) : ColorConstants.WHITE));
                                        table.addCell(new Cell().add(new Paragraph(billamt)).setBorder(Border.NO_BORDER).setBackgroundColor(b ? new DeviceRgb(232,232,232) : ColorConstants.WHITE));
                                        table.addCell(new Cell().add(new Paragraph(rs.getString("days"))).setBorder(Border.NO_BORDER).setBackgroundColor(b ? new DeviceRgb(232,232,232) : ColorConstants.WHITE));
                                        table.addCell(new Cell().add(new Paragraph(rs.getString("paidamt"))).setBorder(Border.NO_BORDER).setBackgroundColor(b ? new DeviceRgb(232,232,232) : ColorConstants.WHITE));
                                        table.addCell(new Cell().add(new Paragraph(rs.getString("unpaidamt"))).setBorder(Border.NO_BORDER).setBackgroundColor(b ? new DeviceRgb(232,232,232) : ColorConstants.WHITE));
                                        String q1 = "select * from salBillmst where FirmNo='"+fno+"' and billno='"+bno+"';";
                                        Statement st1 = con.createStatement();
                                        ResultSet rs1 = st1.executeQuery(q1);
                                        Date pduedate = bdate;
                                        while (rs1.next()) {
                                            pduedate=sdate.parse(rs1.getString("pduedate"));
                                        }
                                        Date tilldate = sdate.parse(tdate);
                                        long difference_In_Time = tilldate.getTime() - pduedate.getTime();
                                        long difference_In_Days = (difference_In_Time / (1000 * 60 * 60 * 24)) % 365;
                                        Double intamt = (difference_In_Days*18*Double.parseDouble(billamt))/36500;
                                        DecimalFormat df = new DecimalFormat();
                                        df.setMaximumFractionDigits(2);
                                        table.addCell(new Cell().add(new Paragraph(String.valueOf(df.format(intamt)))).setBorder(Border.NO_BORDER).setBackgroundColor(b ? new DeviceRgb(232, 232, 232) : ColorConstants.WHITE));
                                        b = !b;
                                    }
                                    q = "select count(*) as totalbill, sum(billamount) as billamount,sum(paidamt) as paid_amt, sum(unpaidamt) as unpaid_amt from SalBillMst_Outstand where billdate<='"+tdate+"' and FirmNo='"+fno+"' and acname='"+ele.tcname+"';";
                                    st = con.createStatement();
                                    rs = st.executeQuery(q);
                                    while (rs.next()) {
                                        String totb=rs.getString("totalbill");
                                        table.addCell(new Cell().add(new Paragraph("Total")).setBold().setFontColor(ColorConstants.RED).setBorder(Border.NO_BORDER));
                                        table.addCell(new Cell().add(new Paragraph(totb)).setBold().setFontColor(ColorConstants.RED).setBorder(Border.NO_BORDER));
                                        if (!totb.equals("0")) {
                                            table.addCell(new Cell().add(new Paragraph(rs.getString("billamount"))).setBold().setFontColor(ColorConstants.RED).setBorder(Border.NO_BORDER));
                                            table.addCell(new Cell().add(new Paragraph("")).setBold().setFontColor(ColorConstants.RED).setBorder(Border.NO_BORDER));
                                            table.addCell(new Cell().add(new Paragraph(rs.getString("paid_amt"))).setBold().setFontColor(ColorConstants.RED).setBorder(Border.NO_BORDER));
                                            table.addCell(new Cell().add(new Paragraph(rs.getString("unpaid_amt"))).setBold().setFontColor(ColorConstants.RED).setBorder(Border.NO_BORDER));
                                            table.addCell(new Cell().add(new Paragraph("")).setBold().setFontColor(ColorConstants.RED).setBorder(Border.NO_BORDER));
                                        }
                                    }
                                }
                                con.close();
                            } catch (Exception e) {
                                Log.e("error", e.getMessage());
                            }
                            doc.add(table);
                            SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss dd-MM-yyyy");
                            Date date = new Date();
                            String tdate = formatter.format(date);
                            doc.add(new Paragraph("Copyright © Designed & Developed by Adro'iT iBS [www.adroit-ibs.com] 97126 77357 \t PDF On : " + tdate).setFontSize(8).setMarginLeft(30).setMarginTop(-20));
                            doc.close();
                            Toast.makeText(Receivable.this, "Pdf file Created", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Log.e("error", e.getMessage());
                        }
                        Uri f = FileProvider.getUriForFile(Receivable.this, getApplicationContext().getPackageName() + ".provider", myFile);
                        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
                        if (myFile.exists()) {
                            intentShareFile.setType("application/pdf");
                            intentShareFile.putExtra(Intent.EXTRA_STREAM, f);
                            startActivity(Intent.createChooser(intentShareFile, "Share File PDF"));
                        }
                    }
                });
                Button bc=(Button) popupView.findViewById(R.id.bclose);
                bc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });
            }
        });
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
                recada.getFilter().filter(newText);
                LinearLayout ll = findViewById(R.id.btnLay);
                paging p = new paging(Receivable.this,ll,10,recada,gvdata,"recada");
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
            Receivable.dialog = ProgressDialog.show(this, "", "Please wait...", true);
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
    @Override
    protected void onResume() {
        super.onResume();
        Receivable.dialog.dismiss();
    }
}