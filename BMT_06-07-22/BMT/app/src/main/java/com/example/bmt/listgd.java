package com.example.bmt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class listgd {
        private String t_name,data,logo="₹";

        public listgd(String t_name, String data) {
            this.t_name = t_name;
            this.data = data;
        }

    public String getT_name() {
        return t_name;
    }

    public String get_data() {
        return data;
    }

}
class adapter extends ArrayAdapter<listgd>{
    public adapter(@NonNull Context context, ArrayList<listgd> ArrayList) {
        super(context, 0, ArrayList);
    }
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listitemView = convertView;
        if (listitemView == null) {
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.card_item, parent, false);
        }
        listgd courseModel = getItem(position);
        TextView tname = listitemView.findViewById(R.id.tname);
        TextView tdata = listitemView.findViewById(R.id.tdata);
        tname.setText(courseModel.getT_name());
        tdata.setText(courseModel.get_data());
        return listitemView;
    }
}
