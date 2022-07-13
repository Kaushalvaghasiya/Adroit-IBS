package com.example.bmt;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

import java.util.ArrayList;

public class paging {
    private ArrayList data;
    int TOTAL_LIST_ITEMS;
    int NUM_ITEMS_PAGE;
    int noOfBtns;
    Button[] btns;
    LinearLayout ll;
    Context ctx;
    GridView gvdata;
    Adapter ad;
    String type;

    public paging(Context ctx, LinearLayout ll, int no_i_p, Adapter ad, GridView gvdata,String type) {
        this.ctx=ctx;
        this.ll=ll;
        NUM_ITEMS_PAGE=no_i_p;
        TOTAL_LIST_ITEMS=ad.getCount();
        this.gvdata=gvdata;
        this.ad=ad;
        this.type=type;
    }

    @SuppressLint("ResourceAsColor")
    void Btnfooter()
    {
        ll.removeAllViews();
        TOTAL_LIST_ITEMS=ad.getCount();
        int val = TOTAL_LIST_ITEMS%NUM_ITEMS_PAGE;
        val = val==0?0:1;
        noOfBtns=TOTAL_LIST_ITEMS/NUM_ITEMS_PAGE+val;
        btns=new Button[noOfBtns];

        for(int i=0;i<noOfBtns;i++)
        {
            btns[i] =   new Button(ctx);
            btns[i].setBackgroundColor(android.R.color.transparent);
            btns[i].setText(""+(i+1));

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            ll.addView(btns[i], lp);

            final int j = i;
            btns[j].setOnClickListener(new View.OnClickListener() {

                public void onClick(View v)
                {
                    loadList(j);
                    CheckBtnBackGroud(j);
                }
            });
        }
    }
    @SuppressLint("ResourceAsColor")
    void CheckBtnBackGroud(int index)
    {
        for(int i=0;i<noOfBtns;i++)
        {
            btns[i].setPadding(-5,-5,-5,-5);
            btns[i].setTextSize(10);
            if(i==index)
            {
                btns[i].setBackgroundColor(Color.parseColor("#23709C"));
                btns[i].setTextColor(Color.parseColor("#FFFFFFFF"));
            }
            else
            {
                btns[i].setBackgroundColor(android.R.color.transparent);
                btns[i].setTextColor(Color.parseColor("#FF000000"));
            }
        }
    }
    void loadList(int number)
    {
        ArrayList sort = new ArrayList();

        int start = number * NUM_ITEMS_PAGE;
        for(int i=start;i<(start)+NUM_ITEMS_PAGE;i++)
        {
            if(i<data.size())
            {
                sort.add(data.get(i));
            }
            else
            {
                break;
            }
        }
        Adapter sd=null;
        if(type.equals("sgada"))sd= new sgadapter(ctx, sort);
        else if(type.equals("syada"))sd= new syadapter(ctx, sort);
        else if(type.equals("sbada"))sd= new sbadapter(ctx, sort);
        else if(type.equals("cgada"))sd= new cgadapter(ctx, sort);
        else if(type.equals("cyada"))sd= new cyadapter(ctx, sort);
        else if(type.equals("cbada"))sd= new cbadapter(ctx, sort);
        else if(type.equals("cada"))sd= new cadapter(ctx, sort);
        else if(type.equals("lada"))sd= new ladapter(ctx, sort);
        else if(type.equals("purada"))sd= new padapter(ctx, sort);
        else sd= new radapter(ctx, sort);
        gvdata.setAdapter((ListAdapter) sd);
    }
    void addata(){
        data = new ArrayList();

        for(int i=0;i<TOTAL_LIST_ITEMS;i++)
        {
            data.add(ad.getItem(i));
        }
        loadList(0);

        CheckBtnBackGroud(0);
    }
}
