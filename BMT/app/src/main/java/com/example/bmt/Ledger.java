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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

public class Ledger extends AppCompatActivity {
    SharedPreferences pref;
    Connection con;
    ladapter lada;
    String tdate,fdate;
    DateFormat adate,sdate;
    GridView gvdata;
    Dictionary data;
    List<String> list;
    public static ProgressDialog dialog;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ledger);
        Ledger.dialog = ProgressDialog.show(this, "", "Please wait...", true);

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
                    @Override
                    public void onClick(View v) {
                        final Calendar c = Calendar.getInstance();
                        int mYear, mMonth, mDay;
                        mYear = c.get(Calendar.YEAR);
                        mMonth = c.get(Calendar.MONTH);
                        mDay = c.get(Calendar.DAY_OF_MONTH);
                        DatePickerDialog datePickerDialog = new DatePickerDialog(Ledger.this,R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
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
                        DatePickerDialog datePickerDialog = new DatePickerDialog(Ledger.this,R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
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
        Ledger.dialog.dismiss();
    }
    public void getdata() {
        ArrayList<LedgerCard> ArrayList = new ArrayList<LedgerCard>();
        gvdata = (GridView) findViewById(R.id.gvdata);
        TextView tfname = (TextView) findViewById(R.id.tcname);
        tfname.setText(pref.getString("fname",null));
        String fno = pref.getString("fno", null);
        try {
            ConnectionHelper conhelper = new ConnectionHelper();
            con = conhelper.connectionclass();
            if (con != null) {
                String q = "select * from Acc_Ledger where FirmNo='"+fno+"' order by AcName ";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(q);
                while (rs.next()) {
                    String r="";
                    if (rs.getString("ClType").equals("C")){
                        r=" Cr";
                    }
                    else{
                        r=" Dr";
                    }
                    ArrayList.add(new LedgerCard(rs.getString("AcName"), rs.getString("ClBalance")+r,
                            rs.getString("City"),"More Details",rs.getString("AcId"),
                            rs.getString("phone"),rs.getString("LedgerId")));
                }
                con.close();
            }
        } catch (Exception e) {
            Log.e("error", e.getMessage());
        }
        lada = new ladapter(this, ArrayList);
        LinearLayout ll = findViewById(R.id.btnLay);
        paging p = new paging(this,ll,20,lada,gvdata,"lada");
        p.Btnfooter();
        p.addata();
        gvdata.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LedgerCard ele = (LedgerCard)gvdata.getItemAtPosition(position);
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.ledger_popupcard, null);
                int width = LinearLayout.LayoutParams.MATCH_PARENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true; // lets taps outside the popup also dismiss it
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
                popupWindow.showAtLocation(view,Gravity.CENTER, 0, 0);
                TextView tlednod = popupView.findViewById(R.id.tlednod);
                tlednod.setText(ele.LedgerId+"  "+ele.tcname);
                TextView tcityd= popupView.findViewById(R.id.tcityd);
                tcityd.setText(ele.city);
                GridView gvdata = popupView.findViewById(R.id.gvdata);
                ArrayList<LedgerPopupCardin> ArrayList = new ArrayList<LedgerPopupCardin>();
                try {
                    ConnectionHelper conhelper = new ConnectionHelper();
                    con = conhelper.connectionclass();
                    if (con != null) {
                        String q = "select ((select OpBalance From  Acc_Ledger where FirmNO= '"+fno+"' and AcId= '"+ele.AcId+"')+" +
                                "(select abs(sum(DrAmt)-sum(CrAmt)) as Bal From HeadDtl3  where FirmNO= '"+fno+"' and AccId= '"+ele.AcId+"' and  Transdate <'"+tdate+"')) as opbal;";
                        Statement st = con.createStatement();
                        ResultSet rs = st.executeQuery(q);
                        while (rs.next()){
                            String opbal=rs.getString("opbal");
                            if(rs.wasNull())opbal="0";
                            Date date = sdate.parse(fdate);
                            String fdt = adate.format(date);
                            ArrayList.add(new LedgerPopupCardin(fdt,"Op. Balance-","","",opbal));
                        }
                        q = "select  Transdate,Descr, DrAmt,CrAmt ,Abs(DrAmt-CrAmt) as Bal, " +
                                "Case when DrAmt-CrAmt>0 then 'Dr' else 'Cr' end  as Baltype From HeadDtl3  where FirmNO= '"+fno+"' and AccId= '"+ele.AcId+"' and  Transdate Between '"+fdate+"'  and '"+tdate+"';";
                        st = con.createStatement();
                        rs = st.executeQuery(q);
                        double sumc=0,sumd=0,sumb=0;
                        while (rs.next()) {
                            String bdt = rs.getString("Transdate").split(" ")[0];
                            Date date = sdate.parse(bdt);
                            bdt = adate.format(date);
                            String c=rs.getString("cramt"),d=rs.getString("dramt"),b=rs.getString("bal");
                            ArrayList.add(new LedgerPopupCardin(bdt,rs.getString("Descr"),rs.getString("dramt"),rs.getString("cramt"),rs.getString("bal")+" "+rs.getString("baltype")));
                            sumb+=Double.parseDouble(b);
                            sumc+=Double.parseDouble(c);
                            sumd+=Double.parseDouble(d);
                        }
                        Date date = sdate.parse(tdate);
                        String tdt = adate.format(date);
                        ArrayList.add(new LedgerPopupCardin("","Total -->> ",String.valueOf(sumd),String.valueOf(sumc),""));
                        ArrayList.add(new LedgerPopupCardin("","Closing Balance Dt. -->> ",tdt,"", sumb +(((sumd-sumc)>0)?" Dr":" Cr")));
                        ledinadapter ad= new ledinadapter(Ledger.this,ArrayList);
                        gvdata.setAdapter(ad);
                    }
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
                        String fileName = "ledger_aibs.pdf";
                        File myFile = new File(dir, fileName);
                        try {
                            PdfDocument pdfDoc = new PdfDocument(new PdfWriter(myFile));
                            Document doc = new Document(pdfDoc, new PageSize(595, 842));
                            doc.setMargins(0, 0, 0, 0);

                            Table table = new Table(new float[5]).useAllAvailableWidth();
                            table.setMargin(30);
                            table.setTextAlignment(TextAlignment.CENTER);
                            table.setWidth(UnitValue.createPercentValue(100));
                            table.setBorder(new SolidBorder(0));
                            // first row
                            String fname = pref.getString("fname", null);
                            Cell cell1 = new Cell(1, 5);
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
                                    String atdate = tdate,afdate=fdate;
                                    Date date = sdate.parse(atdate);
                                    atdate = adate.format(date);
                                    date = sdate.parse(afdate);
                                    afdate = adate.format(date);
                                    cell1.add(new Paragraph("LEDGER DETAIL From : " + afdate+" To "+atdate).setFontSize(12).setBold().setFontColor(ColorConstants.BLUE));
                                    cell1.setTextAlignment(TextAlignment.CENTER);
                                    table.addCell(cell1);
                                    Cell cell3 = new Cell(1, 4);
                                    cell3.add(new Paragraph().add(new Paragraph("LEDGER NAME : ").setFontSize(10).setBold().setFontColor(ColorConstants.BLUE))
                                            .add(new Paragraph(ele.LedgerId).setBold().setFontColor(ColorConstants.RED)).add(new Paragraph("  "+ele.tcname).setBold().setFontColor(ColorConstants.BLUE)));
                                    cell3.setPadding(4);
                                    cell3.setTextAlignment(TextAlignment.LEFT);
                                    table.addCell(cell3);
                                    table.addCell(new Cell().add(new Paragraph(ele.city).setTextAlignment(TextAlignment.RIGHT).setFontColor(ColorConstants.BLUE)).setBorder(Border.NO_BORDER));
                                    table.addCell(new Cell().add(new Paragraph("Date").setBold()).setPaddings(0,10,0,10).setFontColor(ColorConstants.BLUE));
                                    table.addCell(new Cell().add(new Paragraph("Particulars & Voucher Type & Voucher no.").setBold()).setPaddings(0,10,0,10).setFontColor(ColorConstants.BLUE));
                                    table.addCell(new Cell().add(new Paragraph("Debit").setBold()).setPaddings(0,10,0,10).setFontColor(ColorConstants.BLUE));
                                    table.addCell(new Cell().add(new Paragraph("Credit").setBold()).setPaddings(0,10,0,10).setFontColor(ColorConstants.BLUE));
                                    table.addCell(new Cell().add(new Paragraph("Balance").setBold()).setPaddings(0,10,0,10).setFontColor(ColorConstants.BLUE));

                                    q = "select ((select  OpBalance From  Acc_Ledger where FirmNO= '"+fno+"' and AcId= '"+ele.AcId+"')+" +
                                            "(select abs(sum(DrAmt)-sum(CrAmt)) as Bal From HeadDtl3  where FirmNO= '"+fno+"' and AccId= '"+ele.AcId+"' and  Transdate <'"+tdate+"')) as opbal;";
                                    st = con.createStatement();
                                    rs = st.executeQuery(q);
                                    while (rs.next()){
                                        String opbal=rs.getString("opbal");
                                        if(rs.wasNull())opbal="0";
                                        table.addCell(new Cell().add(new Paragraph(afdate)).setBorder(Border.NO_BORDER).setFontSize(10));
                                        table.addCell(new Cell().add(new Paragraph("Op. Balance-").setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER).setFontSize(10));
                                        table.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER).setFontSize(10));
                                        table.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER).setFontSize(10));
                                        table.addCell(new Cell().add(new Paragraph(opbal)).setBorder(Border.NO_BORDER).setFontSize(10));
                                    }
                                    q = "select  Transdate,Descr, DrAmt,CrAmt ,Abs(DrAmt-CrAmt) as Bal, " +
                                            "Case when DrAmt-CrAmt>0 then 'Dr' else 'Cr' end  as Balype From HeadDtl3  where FirmNO= '"+fno+"' and AccId= '"+ele.AcId+"' and  Transdate Between '"+fdate+"'  and '"+tdate+"';";
                                    st = con.createStatement();
                                    rs = st.executeQuery(q);
                                    boolean b = true;
                                    while (rs.next()) {
                                        String bdt = rs.getString("Transdate").split(" ")[0];
                                        date = sdate.parse(bdt);
                                        bdt = adate.format(date);
                                        table.addCell(new Cell().add(new Paragraph(bdt)).setBorder(Border.NO_BORDER).setBackgroundColor(b ? new DeviceRgb(232,232,232) : ColorConstants.WHITE).setFontSize(10));
                                        table.addCell(new Cell().add(new Paragraph(rs.getString("Descr")).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER).setBackgroundColor(b ? new DeviceRgb(232,232,232) : ColorConstants.WHITE).setFontSize(10));
                                        table.addCell(new Cell().add(new Paragraph(rs.getString("dramt"))).setBorder(Border.NO_BORDER).setBackgroundColor(b ? new DeviceRgb(232,232,232) : ColorConstants.WHITE).setFontSize(10));
                                        table.addCell(new Cell().add(new Paragraph(rs.getString("cramt"))).setBorder(Border.NO_BORDER).setBackgroundColor(b ? new DeviceRgb(232,232,232) : ColorConstants.WHITE).setFontSize(10));
                                        table.addCell(new Cell().add(new Paragraph(rs.getString("bal"))).setBorder(Border.NO_BORDER).setBackgroundColor(b ? new DeviceRgb(232,232,232) : ColorConstants.WHITE).setFontSize(10));
                                        b = !b;
                                    }
                                    q = "select count(*) as tot, sum(DrAmt) as DrTotal,sum(CrAmt) as CrTotal ,sum(Abs(DrAmt-CrAmt)) as BalTotal From HeadDtl3  where FirmNO='"+fno+"'  and AccId= '"+ele.AcId+"' and  Transdate Between '"+fdate+"'  and '"+tdate+"';";
                                    st = con.createStatement();
                                    rs = st.executeQuery(q);
                                    while (rs.next()) {
                                        if(!rs.getString("tot").equals("0")) {
                                            table.addCell(new Cell(1, 2).add(new Paragraph("Total : ")).setBorder(Border.NO_BORDER).setFontColor(ColorConstants.BLUE));
                                            table.addCell(new Cell().add(new Paragraph(rs.getString("DrTotal"))).setBorder(Border.NO_BORDER));
                                            table.addCell(new Cell().add(new Paragraph(rs.getString("CrTotal"))).setBorder(Border.NO_BORDER));
                                            table.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
                                            table.addCell(new Cell(1, 3).add(new Paragraph("Closing Balance Dt. : " + atdate).setFontColor(ColorConstants.BLUE)).setBorder(Border.NO_BORDER));
                                            table.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
                                            table.addCell(new Cell().add(new Paragraph(rs.getString("BalTotal"))).setBorder(Border.NO_BORDER).setFontColor(ColorConstants.BLUE));
                                        }
                                    }
                                }
                                con.close();
                            } catch (Exception e) {
                                Log.e("error",e.getMessage());
                            }
                            doc.add(table);
                            SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss dd-MM-yyyy");
                            Date date = new Date();
                            String tdate = formatter.format(date);
                            doc.add(new Paragraph("Copyright © Designed & Developed by Adro'iT iBS [www.adroit-ibs.com] 97126 77357 \t PDF On : " + tdate).setFontSize(8).setMarginLeft(30).setMarginTop(-20));
                            doc.close();
                            Toast.makeText(Ledger.this, "Pdf file Created", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Log.e("error", e.getMessage());
                        }
                        Uri f = FileProvider.getUriForFile(Ledger.this, getApplicationContext().getPackageName() + ".provider", myFile);
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
                lada.getFilter().filter(newText);
                LinearLayout ll = findViewById(R.id.btnLay);
                paging p = new paging(Ledger.this,ll,20,lada,gvdata,"lada");
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
            Ledger.dialog = ProgressDialog.show(this, "", "Please wait...", true);
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
        Ledger.dialog.dismiss();
    }
}