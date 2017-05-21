package com.example.antoine.walkrun;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayList<Tracking> trackingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        listView = (ListView) findViewById(R.id.list_item);
        UpdateListView();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //ArrayList<Location> locations = trackingList.get(position).getLocations();
                Intent intent = new Intent(ListActivity.this, SummaryAcitvity.class);

                intent.putExtra("tracking",trackingList.get(position));
                intent.putExtra("SOURCE",ListActivity.class.getName());
                //intent.putExtra("locList",locations);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Dialog(trackingList.get(position));
                return true;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        UpdateListView();
    }

    public void UpdateListView(){
        TrackingAdapater trackingAdapater;

        String table_name = "activity";
        String[] columns = {"ID", "NAME", "PATH", "TIME", "DIST", "DATE"};
        String where = null;
        String where_args[] = null;
        String group_by = null;
        String having = null;
        String order_by = "DATE DESC";

        Cursor c = MainActivity.DB.sqLiteDatabase.query(table_name, columns, where, where_args, group_by, having, order_by);
        c.moveToFirst();
        trackingList = new ArrayList<>();
        Log.i("DATABASE","size = "+c.getCount());
        for (int i = 0; i<c.getCount(); i++){
            Tracking tmp = new Tracking(c.getString(1),c.getString(2),c.getLong(3),c.getFloat(4),c.getLong(5));
            trackingList.add(tmp);
            c.moveToNext();
        }
        c.close();
        trackingAdapater = new TrackingAdapater(this,trackingList);
        listView.setAdapter(trackingAdapater);
    }

    public void Dialog(final Tracking tracking){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete activity ?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.DB.deleteActivity(tracking.getPath());
                UpdateListView();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });

        builder.create().show();
    }



}
