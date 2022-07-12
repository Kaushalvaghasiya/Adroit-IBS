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
import java.util.ArrayList;
public class SalesCardB {
    String bno,btype,chno,bdate,qty,pcs,taxa,gsta,bamt,tcname,SB_Id,sgst,cgst,igst,tcsa,roff,tdsa,crd,pdd,ebilln;

    public SalesCardB(String tcname,String bno, String btype,String chno,String bdate,String qty,String pcs,String taxa,String gsta,String bamt,String SB_Id,String sgst,String cgst,String igst,
                      String tcsa,String roff,String tdsa,String crd,String pdd,String ebilln) {
        this.tcname=tcname;
        this.bno = bno;
        this.btype = btype;
        this.chno = chno;
        this.bdate = bdate;
        this.qty = qty;
        this.pcs = pcs;
        this.taxa = taxa;
        this.gsta = gsta;
        this.bamt = bamt;
        this.SB_Id = SB_Id;
        this.sgst = sgst;
        this.cgst = cgst;
        this.igst = igst;
        this.tcsa = tcsa;
        this.roff = roff;
        this.tdsa = tdsa;
        this.crd = crd;
        this.pdd = pdd;
        this.ebilln = ebilln;
    }
}
class sbadapter extends BaseAdapter implements Filterable {
    ArrayList<SalesCardB> ArrayList;
    ArrayList<SalesCardB> filteredArrayList;
    Context context;

    public sbadapter(Context context,ArrayList<SalesCardB> ArrayList) {
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
            listitemView = LayoutInflater.from(context).inflate(R.layout.sales_card_b, parent, false);
        }
        SalesCardB courseModel = ArrayList.get(position);
        TextView tcname = listitemView.findViewById(R.id.tcname);
        TextView tbno = listitemView.findViewById(R.id.tbillnod);
        TextView tbtype = listitemView.findViewById(R.id.tbilltd);
        TextView chno = listitemView.findViewById(R.id.tchnod);
        TextView bdate = listitemView.findViewById(R.id.tbilldd);
        TextView qty = listitemView.findViewById(R.id.tqtyd);
        TextView pcs = listitemView.findViewById(R.id.tpcsd);
        TextView taxa = listitemView.findViewById(R.id.ttaxad);
        TextView gsta = listitemView.findViewById(R.id.tgstad);
        TextView bamt = listitemView.findViewById(R.id.tbillad);
        tcname.setText(courseModel.tcname);
        tbno.setText(courseModel.bno);
        tbtype.setText(courseModel.btype);
        chno.setText(courseModel.chno);
        bdate.setText(courseModel.bdate);
        qty.setText(courseModel.qty);
        pcs.setText(courseModel.pcs);
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
                    filterResults.values=filteredArrayList;
                    filterResults.count=filteredArrayList.size();
                }
                else{
                    String search = constraint.toString().toUpperCase();
                    ArrayList<SalesCardB> searchreasult= new ArrayList<>();
                    for(SalesCardB s: filteredArrayList){
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
                ArrayList = (ArrayList<SalesCardB>) results.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }
}
