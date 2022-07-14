package com.example.bmt;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;

import java.util.ArrayList;
import java.util.ArrayList;
class SalesPopUpCardYin {
    String data1,data2,data3,data4,data5,data6,data7,data8;

    public SalesPopUpCardYin(String data1,String data2,String data3, String data4,String data5) {
        this.data1 = data1;
        this.data2 = data2;
        this.data3 = data3;
        this.data4 = data4;
        this.data5 = data5;
    }
}
class syinadapter extends ArrayAdapter<SalesPopUpCardYin>{
    ArrayList<SalesPopUpCardYin> ArrayList;
    public syinadapter(@NonNull Context context, ArrayList<SalesPopUpCardYin> ArrayList) {
        super(context, 0, ArrayList);
        this.ArrayList=ArrayList;
    }
    @SuppressLint("ResourceAsColor")
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listitemView = convertView;
        if (listitemView == null) {
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.sales_ppcard_y_in, parent, false);
        }
        SalesPopUpCardYin courseModel = getItem(position);
        TextView data1 = listitemView.findViewById(R.id.tdata1);
        TextView data2 = listitemView.findViewById(R.id.tdata2);
        TextView data3 = listitemView.findViewById(R.id.tdata3);
        TextView data4 = listitemView.findViewById(R.id.tdata4);
        TextView data5 = listitemView.findViewById(R.id.tdata5);
        TextView data6 = listitemView.findViewById(R.id.tdata6);
        TextView data7 = listitemView.findViewById(R.id.tdata7);
        TextView data8 = listitemView.findViewById(R.id.tdata8);
        data1.setText(courseModel.data1);
        data2.setText(courseModel.data2);
        data3.setText(courseModel.data3);
        data4.setText(courseModel.data4);
        data5.setText(courseModel.data5);
        data6.setText(courseModel.data6);
        data7.setText(courseModel.data7);
        data8.setText(courseModel.data8);
        CardView cdv= listitemView.findViewById(R.id.cdv);
        if(position%2!=0)cdv.setCardBackgroundColor(Color.parseColor("#EBEECD"));
        if(position==ArrayList.size()-1){
            data1.setTextColor(Color.parseColor("#F44336"));
            data2.setTextColor(Color.parseColor("#F44336"));
            data3.setTextColor(Color.parseColor("#F44336"));
            data4.setTextColor(Color.parseColor("#F44336"));
            data5.setTextColor(Color.parseColor("#F44336"));
            data6.setTextColor(Color.parseColor("#F44336"));
            data7.setTextColor(Color.parseColor("#F44336"));
            data8.setTextColor(Color.parseColor("#F44336"));
        }
        return listitemView;
    }
}
