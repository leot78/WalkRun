package com.example.antoine.walkrun;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.SystemClock;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private TextView tv_speed;
    private TextView tv_av_speed;
    private TextView tv_dist;
    private Button btn_start;
    private Button btn_stop;
    private Button btn_open;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private Location last_loc;
    private long last_time;
    private long last;
    private long all_time_elapse;
    private float all_distance;
    private float last_kDistance;
    private float av_speed;

    private ArrayList<Location> locArrayList;
    private ArrayList<Long> time_list;
    private Chronometer chrono;

    public static DataBase DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_speed = (TextView) findViewById(R.id.tv_speed);
        tv_av_speed = (TextView) findViewById(R.id.tv_av_speed);
        tv_dist = (TextView) findViewById(R.id.tv_dist);
        btn_start = (Button) findViewById(R.id.btn_start);
        btn_stop = (Button) findViewById(R.id.btn_stop);
        btn_open = (Button) findViewById(R.id.btn_open);

        chrono = (Chronometer) findViewById(R.id.chrono);
        chrono.setFormat("%s");

        DB = new DataBase(this);

    }

    public void start_tracking(View view) {
        btn_stop.setEnabled(true);
        btn_start.setEnabled(false);
        btn_open.setEnabled(false);

        last=0;
        last_loc =null;
        all_distance =0;
        last_kDistance =0;
        time_list = new ArrayList<>();
        locArrayList = new ArrayList<>();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        addLocationListener();
    }

    public void stop_tracking(View view){
        btn_stop.setEnabled(false);
        btn_start.setEnabled(true);
        btn_open.setEnabled(true);

        locationManager.removeUpdates(locationListener);
        locationManager = null;

        chrono.stop();

        Log.i("main","dist="+all_distance);
        Log.i("main","time="+all_time_elapse);
        Intent intent = new Intent(MainActivity.this, SummaryActivity.class);
        Tracking tracking = new Tracking();
        tracking.setLocations(locArrayList);
        intent.putExtra("tracking",tracking);
        intent.putExtra("SOURCE",MainActivity.class.getName());

        startActivity(intent);

        chrono.setBase(SystemClock.elapsedRealtime());
        tv_speed.setText(R.string.tv_speed);
        tv_dist.setText(R.string.tv_dist);
        tv_av_speed.setText(R.string.tv_average_speed);
    }

    private void addLocationListener() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Please accept gps location.", Toast.LENGTH_LONG).show();
            return;
        }
        locationListener = createLocListener();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, locationListener);
    }

    private LocationListener createLocListener(){
        return new LocationListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onLocationChanged(Location location) {
                locArrayList.add(location);
                long curTime = SystemClock.elapsedRealtime();

                if(last_loc==null) {
                    chrono.setBase(SystemClock.elapsedRealtime());
                    chrono.start();
                    last_loc = location;
                    last_time = curTime;
                    tv_speed.setText(R.string.default_speed);
                    tv_av_speed.setText(R.string.default_average_speed);
                    tv_dist.setText(R.string.default_distance);
                } else {

                    float distance = location.distanceTo(last_loc);

                    long time_elapse = curTime - last_time;
                    float speed = (distance / time_elapse) * 3600.f;
                    tv_speed.setText("Speed : " + String.format("%.2f",
                            speed) + " km/h");

                    all_distance = all_distance + distance;
                    all_time_elapse = curTime - chrono.getBase();

                    av_speed = (all_distance / all_time_elapse) * 3600.f;
                    tv_av_speed.setText("Average speed : " + String.format("%.2f", av_speed) + " km/h");
                    tv_dist.setText("Distance : " + String.format("%.2f", all_distance / 1000) + " km");

                    last_loc = location;
                    last_time = curTime;
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
                if(provider.equals(LocationManager.GPS_PROVIDER)) {
                    Toast.makeText(getBaseContext(), "GPS is active", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onProviderDisabled(String provider) {
                if(provider.equals(LocationManager.GPS_PROVIDER)){
                    tv_speed.setText(R.string.tv_speed);
                    Toast.makeText(getBaseContext(), "GPS is not active", Toast.LENGTH_SHORT).show();
                    tv_speed.setText(R.string.tv_speed);
                }
            }
        };
    }

    public void clickOpen(View view){
        Intent intent = new Intent(MainActivity.this, ListActivity.class);
        startActivity(intent);
    }
}
