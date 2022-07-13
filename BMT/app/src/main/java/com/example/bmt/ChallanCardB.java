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

public class ChallanCardB {
    String dp,tra,chno,chd,pcs,mtr,chamt,tcname,C_Id;

    public ChallanCardB(String tcname,String dp, String tra,String chno,String chd,String pcs,String mtr,String chamt,String C_Id) {
        this.tcname=tcname;
        this.dp = dp;
        this.tra = tra;
        this.chno = chno;
        this.chd = chd;
        this.pcs = pcs;
        this.mtr = mtr;
        this.chamt = chamt;
        this.C_Id = C_Id;
    }
}
class cbadapter extends BaseAdapter implements Filterable {
    ArrayList<ChallanCardB> ArrayList;
    ArrayList<ChallanCardB> filteredArrayList;
    Context context;
    public cbadapter(@NonNull Context context, ArrayList<ChallanCardB> ArrayList) {
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
            listitemView = LayoutInflater.from(context).inflate(R.layout.challan_card_b, parent, false);
        }
        ChallanCardB courseModel = ArrayList.get(position);
        TextView tcname = listitemView.findViewById(R.id.tcname);
        TextView dp = listitemView.findViewById(R.id.tdpd);
        TextView tra = listitemView.findViewById(R.id.ttransd);
        TextView chno = listitemView.findViewById(R.id.tchnod);
        TextView chd = listitemView.findViewById(R.id.tchdd);
        TextView pcs = listitemView.findViewById(R.id.tpcsd);
        TextView mtr = listitemView.findViewById(R.id.tmtrd);
        TextView chamt = listitemView.findViewById(R.id.tchamtd);
        tcname.setText(courseModel.tcname);
        dp.setText(courseModel.dp);
        tra.setText(courseModel.tra);
        chno.setText(courseModel.chno);
        chd.setText(courseModel.chd);
        pcs.setText(courseModel.pcs);
        mtr.setText(courseModel.mtr);
        chamt.setText(courseModel.chamt);
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
                    ArrayList<ChallanCardB> searchreasult= new ArrayList<>();
                    for(ChallanCardB s: filteredArrayList){
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
                ArrayList = (ArrayList<ChallanCardB>) results.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }
}
