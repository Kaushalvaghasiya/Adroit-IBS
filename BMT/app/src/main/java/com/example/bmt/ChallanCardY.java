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

public class ChallanCardY {
    String dp,tra,chno,chd,rate,totch,grade,lotn,shno,totcr,tcname,netwt,C_Id;

    public ChallanCardY(String tcname,String dp, String tra,String chno,String chd,String grade,String lotn,String shno,String totcr,String totch,String netwt,String rate, String C_Id) {
        this.tcname=tcname;
        this.dp = dp;
        this.tra = tra;
        this.chno = chno;
        this.chd = chd;
        this.rate = rate;
        this.grade = grade;
        this.lotn = lotn;
        this.shno = shno;
        this.totcr = totcr;
        this.totch = totch;
        this.netwt = netwt;
        this.C_Id = C_Id;
    }
}
class cyadapter extends BaseAdapter implements Filterable {
    ArrayList<ChallanCardY> ArrayList;
    ArrayList<ChallanCardY> filteredArrayList;
    Context context;
    public cyadapter(@NonNull Context context, ArrayList<ChallanCardY> ArrayList) {
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
            listitemView = LayoutInflater.from(context).inflate(R.layout.challan_card_y, parent, false);
        }
        ChallanCardY courseModel = ArrayList.get(position);
        TextView tcname = listitemView.findViewById(R.id.tcname);
        TextView dp = listitemView.findViewById(R.id.tdpd);
        TextView tra = listitemView.findViewById(R.id.ttransd);
        TextView chno = listitemView.findViewById(R.id.tchnod);
        TextView chd = listitemView.findViewById(R.id.tchdd);
        TextView rate = listitemView.findViewById(R.id.trated);
        TextView grade = listitemView.findViewById(R.id.tgraded);
        TextView lotn = listitemView.findViewById(R.id.tlotd);
        TextView shno = listitemView.findViewById(R.id.tshd);
        TextView totcr = listitemView.findViewById(R.id.ttotcd);
        TextView totch = listitemView.findViewById(R.id.ttotchd);
        TextView netwt = listitemView.findViewById(R.id.ttotwtd);
        tcname.setText(courseModel.tcname);
        dp.setText(courseModel.dp);
        tra.setText(courseModel.tra);
        chno.setText(courseModel.chno);
        chd.setText(courseModel.chd);
        rate.setText(courseModel.rate);
        grade.setText(courseModel.grade);
        lotn.setText(courseModel.lotn);
        shno.setText(courseModel.shno);
        totcr.setText(courseModel.totcr);
        totch.setText(courseModel.totch);
        netwt.setText(courseModel.netwt);
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
                    ArrayList<ChallanCardY> searchreasult= new ArrayList<>();
                    for(ChallanCardY s: filteredArrayList){
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
                ArrayList = (ArrayList<ChallanCardY>) results.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }
}
