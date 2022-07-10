package com.example.bmt;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;

public class RecCard {
    String phone,ttb,bamt,pamt,upamt,tcname,AcId,bno,days,inte;

    public RecCard(String tcname,String phone, String ttb,String pamt,String upamt,String bamt,String AcId) {
        this.tcname=tcname;
        this.phone = phone;
        this.ttb = ttb;
        this.pamt = pamt;
        this.upamt = upamt;
        this.bamt = bamt;
        this.AcId = AcId;
        this.bno = "bno";
        this.days = "days";
        this.inte = "inte";
    }
}
class radapter extends BaseAdapter implements Filterable {
    ArrayList<RecCard> ArrayList;
    ArrayList<RecCard> filteredArrayList;
    Context context;
    public radapter(@NonNull Context context, ArrayList<RecCard> ArrayList) {
        this.ArrayList = ArrayList;
        this.filteredArrayList = ArrayList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return ArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return ArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listitemView = convertView;
        if (listitemView == null) {
            listitemView = LayoutInflater.from(context).inflate(R.layout.rec_card, parent, false);
        }
        RecCard courseModel = ArrayList.get(position);
        TextView tcname = listitemView.findViewById(R.id.tcname);
        TextView phone = listitemView.findViewById(R.id.tphonedata);
        TextView ttb = listitemView.findViewById(R.id.ttbilld);
        TextView pamt = listitemView.findViewById(R.id.tpamtd);
        TextView upamt = listitemView.findViewById(R.id.tupamtd);
        TextView bamt = listitemView.findViewById(R.id.tbillad);
        tcname.setText(courseModel.tcname);
        phone.setText(courseModel.phone);
        ttb.setText(courseModel.ttb);
        pamt.setText(courseModel.pamt);
        upamt.setText(courseModel.upamt);
        bamt.setText(courseModel.bamt);
        return listitemView;
    }

    @Override
    public Filter getFilter() {
        Filter filter =new Filter() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults =new FilterResults();
                if(constraint.length()==0){
                    filterResults.values=filteredArrayList;
                    filterResults.count=filteredArrayList.size();
                }
                else{
                    String search = constraint.toString().toUpperCase();
                    ArrayList<RecCard> searchreasult= new ArrayList<>();
                    for(RecCard s: filteredArrayList){
                        if(s.tcname.contains(search)){
                            searchreasult.add(s);
                        }
                    }
                    filterResults.values=searchreasult;
                    filterResults.count=searchreasult.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                ArrayList = (ArrayList<RecCard>) results.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }
}
