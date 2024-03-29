package com.example.antoine.walkrun;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.util.ArrayList;

public class SummaryActivity extends AppCompatActivity {
    ArrayList<Location> locArrayList;
    String name;
    File file;
    long time;
    float dist;
    long date;
    Tracking tracking;
    private static final char[] ILLEGAL_CHARACTERS = {' ', '/', '\n', '\r', '\t', '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':' };
    private String SOURCE_ACTIVITY;
    TextView tv_name;
    Button btn_save;
    Button btn_share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_summary_view);

        Intent intent = getIntent();
        ArrayList<Long> time_list;
        TextView tv_av;
        TextView tv_dist;
        TextView tv_time;

        SOURCE_ACTIVITY = intent.getStringExtra("SOURCE");
        tracking =(Tracking) intent.getParcelableExtra("tracking");
        if(SOURCE_ACTIVITY.equals(ListActivity.class.getName())) {
            tracking.computeLocations(getBaseContext());
        }
        locArrayList = tracking.getLocations();
        //locArrayList = (ArrayList<Location>) intent.getSerializableExtra("locList");
        if(locArrayList.size()==0){
            Toast.makeText(this,"Activity is empty",Toast.LENGTH_LONG).show();
            onBackPressed();
            return;
        }
        /*tracking = new Tracking();
        tracking.setLocations(locArrayList);*/
        time_list = tracking.getTimeList(); //(ArrayList<Long>) intent.getSerializableExtra("list");

        Log.i("size loc",locArrayList.size()+"");
        SummaryView sv = (SummaryView) findViewById(R.id.sum_view);
        sv.setList(time_list);

        tv_av = (TextView) findViewById(R.id.sum_tv_av);
        tv_dist = (TextView) findViewById(R.id.sum_tv_dist);
        tv_time = (TextView) findViewById(R.id.sum_tv_time);
        tv_name = (TextView) findViewById(R.id.tv_name);
        btn_save = (Button) findViewById(R.id.btn_save);
        btn_share = (Button) findViewById(R.id.btn_share);

        if(SOURCE_ACTIVITY.equals(ListActivity.class.getName())) {
            tv_name.setText(tracking.getName());
            btn_save.setEnabled(false);
            btn_share.setEnabled(true);
        }

        dist = tracking.getDistance(); //intent.getFloatExtra("all_dist",0);
        time = tracking.getTime(); //intent.getLongExtra("time",0);
        Log.i("summary","dist="+dist);
        Log.i("summary","time="+time);
        long mins = (time/1000)/60;
        long secs = (time/1000)%60;
        float av_speed = (dist/time)*3600.f;
        tv_dist.setText("Distance: "+ String.format("%.2f",dist/1000)+" km");
        tv_av.setText("Average speed: "+ String.format("%.2f", av_speed)+" km/h");
        tv_time.setText("Time: "+GPX.convertMsToHMmSs(time));
        date = locArrayList.get(0).getTime();
        Button btn_map = (Button) findViewById(R.id.btn_map);
        if(locArrayList.size()!=0)
            btn_map.setEnabled(true);

        //openFolder();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.summary,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_delete) {
            if (SOURCE_ACTIVITY.equals(ListActivity.class.getName())) {
                MainActivity.DB.deleteActivity(tracking.getPath());
            }
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void clickMap(View view){
        Intent intent = new Intent(SummaryActivity.this, MapsActivity.class);
        intent.putExtra("locList",locArrayList);
        startActivity(intent);
    }

    public void clickSave(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose an activity name:");

        final EditText et = new EditText(this);
        et.setInputType(InputType.TYPE_CLASS_TEXT);
        et.setHint("Name of activity...");
        builder.setView(et);

        builder.setPositiveButton("OK", (dialog, which) -> {
            name = et.getText().toString();
            Save();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
        });

        builder.create().show();
    }

    private void Save(){
        StringBuilder filename = new StringBuilder(name);
        for (char c:ILLEGAL_CHARACTERS){
            filename = new StringBuilder(filename.toString().replace(c, '_'));
        }
        file = new File(getExternalFilesDir(null),filename+".gpx");
        int i =1;
        while(file.exists()){
            filename.append("(").append(i).append(")");
            file = new File(getExternalFilesDir(null),filename+".gpx");
            i++;
        }

        GPX.writePath(file,name,locArrayList,this);
        ((Button)findViewById(R.id.btn_share)).setEnabled(true);
        MainActivity.DB.addActivity(filename.toString(),name,time,dist,date);
        tv_name.setText(name);
        btn_save.setEnabled(false);
        btn_share.setEnabled(true);
    }

    public void clickShare(View view){
        File filetoShare = new File("");
        if(SOURCE_ACTIVITY.equals(ListActivity.class.getName())){
            filetoShare = new File(getExternalFilesDir(null),tracking.getPath()+".gpx");
        } else {
            filetoShare = file;
        }
        Uri uri = FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".fileprovider", filetoShare);
        Log.i("FILE",filetoShare.toString());
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("*/*");
        startActivity(Intent.createChooser(shareIntent,"Choose an app to share GPX file"));

        //GPX.parse(file);
    }
}
