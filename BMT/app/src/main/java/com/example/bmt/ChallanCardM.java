package com.example.bmt;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;

public class ChallanCardM {
    String dp,tra,chno,chd,notes,pcs,qty,tcname,chamt,C_Id;

    public ChallanCardM(String tcname, String dp, String tra, String chno, String chd, String notes, String pcs, String qty, String chamt, String C_Id) {
        this.tcname=tcname;
        this.dp = dp;
        this.tra = tra;
        this.chno = chno;
        this.chd = chd;
        this.notes = notes;
        this.pcs = pcs;
        this.qty = qty;
        this.chamt = chamt;
        this.C_Id = C_Id;
    }
}
class cmadapter extends BaseAdapter implements Filterable {
    ArrayList<ChallanCardM> ArrayList;
    ArrayList<ChallanCardM> filteredArrayList;
    Context context;
    public cmadapter(@NonNull Context context, ArrayList<ChallanCardM> ArrayList) {
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
            listitemView = LayoutInflater.from(context).inflate(R.layout.challan_card_m, parent, false);
        }
        ChallanCardM courseModel = ArrayList.get(position);
        TextView tcname = listitemView.findViewById(R.id.tcname);
        TextView dp = listitemView.findViewById(R.id.tdpd);
        TextView tra = listitemView.findViewById(R.id.ttransd);
        TextView chno = listitemView.findViewById(R.id.tchnod);
        TextView chd = listitemView.findViewById(R.id.tchdd);
        TextView notes = listitemView.findViewById(R.id.tnoted);
        TextView pcs = listitemView.findViewById(R.id.tpcsd);
        TextView chamt = listitemView.findViewById(R.id.tchamtd);
        TextView qty = listitemView.findViewById(R.id.tqtyd);
        tcname.setText(courseModel.tcname);
        dp.setText(courseModel.dp);
        tra.setText(courseModel.tra);
        chno.setText(courseModel.chno);
        chd.setText(courseModel.chd);
        notes.setText(courseModel.notes);
        pcs.setText(courseModel.pcs);
        qty.setText(courseModel.qty);
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
                    ArrayList<ChallanCardM> searchreasult= new ArrayList<>();
                    for(ChallanCardM s: filteredArrayList){
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
                ArrayList = (ArrayList<ChallanCardM>) results.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }
}
