package com.example.bmt;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.graphics.Typeface;
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
class LedgerPopupCardin {
    String data1,data2,data3,data4,data5;

    public LedgerPopupCardin(String data1,String data2,String data3, String data4,String data5) {
        this.data1 = data1;
        this.data2 = data2;
        this.data3 = data3;
        this.data4 = data4;
        this.data5 = data5;
    }
}
class ledinadapter extends ArrayAdapter<LedgerPopupCardin>{
    ArrayList<LedgerPopupCardin> ArrayList;
    public ledinadapter(@NonNull Context context, ArrayList<LedgerPopupCardin> ArrayList) {
        super(context, 0, ArrayList);
        this.ArrayList=ArrayList;
    }
    @SuppressLint("ResourceAsColor")
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listitemView = convertView;
        if (listitemView == null) {
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.ledger_ppcard_in, parent, false);
        }
        LedgerPopupCardin courseModel = getItem(position);
        TextView data1 = listitemView.findViewById(R.id.tdata1);
        TextView data2 = listitemView.findViewById(R.id.tdata2);
        TextView data3 = listitemView.findViewById(R.id.tdata3);
        TextView data4 = listitemView.findViewById(R.id.tdata4);
        TextView data5 = listitemView.findViewById(R.id.tdata5);
        data1.setText(courseModel.data1);
        data2.setText(courseModel.data2);
        data3.setText(courseModel.data3);
        data4.setText(courseModel.data4);
        data5.setText(courseModel.data5);
        CardView cdv= listitemView.findViewById(R.id.cdv);
        if(position%2!=0)cdv.setCardBackgroundColor(Color.parseColor("#EBEECD"));
        if(position==ArrayList.size()-2){
            data2.setTextColor(Color.parseColor("#3F51B5"));
            data2.setTypeface(null, Typeface.BOLD);
            data3.setTypeface(null, Typeface.BOLD);
            data4.setTypeface(null, Typeface.BOLD);
        }
        if(position==ArrayList.size()-1){
            data2.setTextColor(Color.parseColor("#3F51B5"));
            data2.setTypeface(null, Typeface.BOLD);
            data3.setTypeface(null, Typeface.BOLD);
            data5.setTypeface(null, Typeface.BOLD);
            data5.setTextColor(Color.parseColor("#3F51B5"));
        }
        return listitemView;
    }
}
