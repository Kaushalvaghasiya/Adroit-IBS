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

public class SalesCardG {
    String bno,btype,chno,bdate,totmrts,pcs,wt,taxa,gsta,bamt,tcname,AcId;

    public SalesCardG(String tcname,String bno, String btype,String chno,String bdate,String totmrts,String pcs,String wt,String taxa,String gsta,String bamt,String AcId) {
        this.tcname=tcname;
        this.bno = bno;
        this.btype = btype;
        this.chno = chno;
        this.bdate = bdate;
        this.totmrts = totmrts;
        this.pcs = pcs;
        this.wt = wt;
        this.taxa = taxa;
        this.gsta = gsta;
        this.bamt = bamt;
        this.AcId = AcId;
    }
}
class sgadapter extends BaseAdapter implements Filterable {
    ArrayList<SalesCardG> ArrayList;
    Context context;
    ArrayList<SalesCardG> filteredArrayList;
    public sgadapter(@NonNull Context context, ArrayList<SalesCardG> ArrayList) {
        this.ArrayList=ArrayList;
        this.context=context;
        this.filteredArrayList=ArrayList;
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
            listitemView = LayoutInflater.from(context).inflate(R.layout.sales_card_g, parent, false);
        }
        SalesCardG courseModel = ArrayList.get(position);
        TextView tcname = listitemView.findViewById(R.id.tcname);
        TextView tbno = listitemView.findViewById(R.id.tbillnod);
        TextView tbtype = listitemView.findViewById(R.id.tbilltd);
        TextView chno = listitemView.findViewById(R.id.tchnod);
        TextView bdate = listitemView.findViewById(R.id.tbilldd);
        TextView totmrts = listitemView.findViewById(R.id.ttomtrsd);
        TextView pcs = listitemView.findViewById(R.id.tpcsd);
        TextView wt = listitemView.findViewById(R.id.twtd);
        TextView taxa = listitemView.findViewById(R.id.ttaxad);
        TextView gsta = listitemView.findViewById(R.id.tgstad);
        TextView bamt = listitemView.findViewById(R.id.tbillad);
        tcname.setText(courseModel.tcname);
        tbno.setText(courseModel.bno);
        tbtype.setText(courseModel.btype);
        chno.setText(courseModel.chno);
        bdate.setText(courseModel.bdate);
        totmrts.setText(courseModel.totmrts);
        pcs.setText(courseModel.pcs);
        wt.setText(courseModel.wt);
        taxa.setText(courseModel.taxa);
        gsta.setText(courseModel.gsta);
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
                    ArrayList<SalesCardG> searchreasult= new ArrayList<>();
                    for(SalesCardG s: filteredArrayList){
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
                ArrayList = (ArrayList<SalesCardG>) results.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }
}
