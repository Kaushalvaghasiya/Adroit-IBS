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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Locale;

public class SalesCardY {
    String bno,btype,chno,bdate,netwt,box,ch,taxa,gsta,bamt,tcname,AcId;

    public SalesCardY(String tcname,String bno, String btype,String chno,String bdate,String netwt,String box,String ch,String taxa,String gsta,String bamt,String AcId) {
        this.tcname=tcname;
        this.bno = bno;
        this.btype = btype;
        this.chno = chno;
        this.bdate = bdate;
        this.netwt = netwt;
        this.box = box;
        this.ch = ch;
        this.taxa = taxa;
        this.gsta = gsta;
        this.bamt = bamt;
        this.AcId = AcId;
    }
}
class syadapter extends BaseAdapter implements Filterable {
    ArrayList<SalesCardY> ArrayList;
    Context context;
    ArrayList<SalesCardY> filterdArrayList;
    public syadapter(@NonNull Context context, ArrayList<SalesCardY> ArrayList) {
        this.ArrayList=ArrayList;
        this.filterdArrayList=ArrayList;
        this.context=context;
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
            listitemView = LayoutInflater.from(context).inflate(R.layout.sales_card_y, parent, false);
        }
        SalesCardY courseModel = ArrayList.get(position);
        TextView tcname = listitemView.findViewById(R.id.tcname);
        TextView tbno = listitemView.findViewById(R.id.tbillnod);
        TextView tbtype = listitemView.findViewById(R.id.tbilltd);
        TextView chno = listitemView.findViewById(R.id.tchnod);
        TextView bdate = listitemView.findViewById(R.id.tbilldd);
        TextView netwt = listitemView.findViewById(R.id.tnetwtd);
        TextView box = listitemView.findViewById(R.id.tboxd);
        TextView ch = listitemView.findViewById(R.id.tchd);
        TextView taxa = listitemView.findViewById(R.id.ttaxad);
        TextView gsta = listitemView.findViewById(R.id.tgstad);
        TextView bamt = listitemView.findViewById(R.id.tbillad);
        tcname.setText(courseModel.tcname);
        tbno.setText(courseModel.bno);
        tbtype.setText(courseModel.btype);
        chno.setText(courseModel.chno);
        bdate.setText(courseModel.bdate);
        netwt.setText(courseModel.netwt);
        box.setText(courseModel.box);
        ch.setText(courseModel.ch);
        taxa.setText(courseModel.taxa);
        gsta.setText(courseModel.gsta);
        bamt.setText(courseModel.bamt);
        return listitemView;
    }
    public Filter getFilter(){
        Filter filter =new Filter() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults =new FilterResults();
                if(constraint.length()==0){
                    filterResults.values=filterdArrayList;
                    filterResults.count=filterdArrayList.size();
                }
                else{
                    String search = constraint.toString().toUpperCase();
                    ArrayList<SalesCardY> searchreasult= new ArrayList<>();
                    for(SalesCardY s: filterdArrayList){
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
                ArrayList = (ArrayList<SalesCardY>) results.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }
}
