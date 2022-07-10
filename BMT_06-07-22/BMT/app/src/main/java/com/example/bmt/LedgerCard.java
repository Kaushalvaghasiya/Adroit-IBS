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

public class LedgerCard {
    String bal,city,mdtl,tcname,AcId,Phone,LedgerId;

    public LedgerCard(String tcname,String bal, String city,String mdtl,String AcId,String Phone, String LedgerId) {
        this.tcname=tcname;
        this.bal = bal;
        this.city = city;
        this.mdtl = mdtl;
        this.AcId = AcId;
        this.Phone = Phone;
        this.LedgerId = LedgerId;
    }
}
class ladapter extends BaseAdapter implements Filterable {
    ArrayList<LedgerCard> ArrayList;
    ArrayList<LedgerCard> filteredArrayList;
    Context context;
    public ladapter(@NonNull Context context, ArrayList<LedgerCard> ArrayList) {
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
            listitemView = LayoutInflater.from(context).inflate(R.layout.ledger_card, parent, false);
        }
        LedgerCard courseModel = ArrayList.get(position);
        TextView tcname = listitemView.findViewById(R.id.tcnamed);
        TextView bal = listitemView.findViewById(R.id.tbald);
        TextView city = listitemView.findViewById(R.id.tcityd);
        TextView mdtl = listitemView.findViewById(R.id.tmdtld);
        tcname.setText(courseModel.tcname);
        bal.setText(courseModel.bal);
        city.setText(courseModel.city);
        mdtl.setText(courseModel.mdtl);
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
                    ArrayList<LedgerCard> searchreasult= new ArrayList<>();
                    for(LedgerCard s: filteredArrayList){
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
                ArrayList = (ArrayList<LedgerCard>) results.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }
}
