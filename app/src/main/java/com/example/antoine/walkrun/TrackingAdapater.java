package com.example.antoine.walkrun;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Antoine on 15/05/2017.
 */

public class TrackingAdapater extends BaseAdapter {
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private Context context;
    private ArrayList<Tracking> al_tracking;

    public TrackingAdapater(Context c, ArrayList<Tracking> list){
        context = c;
        al_tracking = list;
    }

    @Override
    public int getCount() {
        return al_tracking.size();
    }

    @Override
    public Object getItem(int position) {
        return al_tracking.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        Tracking tracking = al_tracking.get(position);

        if(convertView == null){
            viewHolder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.activity_container,parent,false);

            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.ct_name);
            viewHolder.tv_dist = (TextView) convertView.findViewById(R.id.ct_dist);
            viewHolder.tv_time = (TextView) convertView.findViewById(R.id.ct_time);
            viewHolder.tv_date = (TextView) convertView.findViewById(R.id.ct_date);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String date = sdf.format(tracking.getDate());
        String dist = String.format("%.2f",(tracking.getDistance()/1000))+" km";
        String time = GPX.convertMsToHMmSs(tracking.getTime());
        viewHolder.tv_name.setText(tracking.getName());
        viewHolder.tv_dist.setText(dist);
        viewHolder.tv_time.setText(time);
        viewHolder.tv_date.setText(date);
        return convertView;
    }

    static class ViewHolder{
        public TextView tv_name;
        public TextView tv_dist;
        public TextView tv_time;
        public TextView tv_date;
    }
}
