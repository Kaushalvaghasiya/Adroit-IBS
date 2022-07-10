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

public class PurchaseCard {
    String bno,btype,bdate,qty,pcs,taxa,gsta,bamt,tcname,SB_Id;

    public PurchaseCard(String tcname,String bno, String btype,String bdate,String qty,String pcs,String taxa,String gsta,String bamt,String SB_Id) {
        this.tcname=tcname;
        this.bno = bno;
        this.btype = btype;
        this.bdate = bdate;
        this.qty = qty;
        this.pcs = pcs;
        this.taxa = taxa;
        this.gsta = gsta;
        this.bamt = bamt;
        this.SB_Id=SB_Id;
    }
}
class padapter extends BaseAdapter implements Filterable {
    ArrayList<PurchaseCard> ArrayList;
    ArrayList<PurchaseCard> filteredArrayList;
    Context context;
    public padapter(@NonNull Context context, ArrayList<PurchaseCard> ArrayList) {
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
            listitemView = LayoutInflater.from(context).inflate(R.layout.purchase_card, parent, false);
        }
        PurchaseCard courseModel = ArrayList.get(position);
        TextView tcname = listitemView.findViewById(R.id.tcname);
        TextView tbno = listitemView.findViewById(R.id.tbillnod);
        TextView tbtype = listitemView.findViewById(R.id.tbilltd);
        TextView bdate = listitemView.findViewById(R.id.tbilldd);
        TextView qty = listitemView.findViewById(R.id.tqtyd);
        TextView pcs = listitemView.findViewById(R.id.tpcsd);
        TextView taxa = listitemView.findViewById(R.id.ttaxad);
        TextView gsta = listitemView.findViewById(R.id.tgstad);
        TextView bamt = listitemView.findViewById(R.id.tbillad);
        tcname.setText(courseModel.tcname);
        tbno.setText(courseModel.bno);
        tbtype.setText(courseModel.btype);
        bdate.setText(courseModel.bdate);
        qty.setText(courseModel.qty);
        pcs.setText(courseModel.pcs);
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
                    ArrayList<PurchaseCard> searchreasult= new ArrayList<>();
                    for(PurchaseCard s: filteredArrayList){
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
                ArrayList = (ArrayList<PurchaseCard>) results.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }
}
