package com.example.antoine.walkrun;

import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<Location> locationArrayList;
    private ArrayList<LatLng> listLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Intent intent = getIntent();
        locationArrayList = (ArrayList<Location>) intent.getSerializableExtra("locList");
        listLatLng = LocToLatLng(locationArrayList);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_map));

            if (!success) {
                Log.e("parse json", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("parse json", "Can't find style. Error: ", e);
        }

        LatLng start = listLatLng.get(0);
        LatLng end = listLatLng.get(listLatLng.size()-1);

        Pair<Integer,Integer> limit = mostDistantPoint(locationArrayList);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        if(!limit.first.equals(limit.second)) {
            builder.include(listLatLng.get(limit.first));
            builder.include(listLatLng.get(limit.second));
        } else {
            builder.include(start);
            builder.include(end);
        }
        LatLngBounds bnds = builder.build();
        Log.i("BOUNDS",bnds.toString());

        mMap.setOnMapLoadedCallback(() -> {
            Pair<Integer,Integer> limit1 = mostDistantPoint(locationArrayList);
            LatLngBounds.Builder builder1 = new LatLngBounds.Builder();
            builder1.include(listLatLng.get(limit1.first));
            builder1.include(listLatLng.get(limit1.second));
            LatLngBounds bnds1 = builder1.build();
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bnds1,100));
            mMap.addMarker(new MarkerOptions()
                    .position(start)
                    .title("Start")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            mMap.addMarker(new MarkerOptions()
                    .position(end)
                    .title("Finish")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

            PolylineOptions options = new PolylineOptions().addAll(listLatLng);
            mMap.addPolyline(options);
        });
    }

    private ArrayList<LatLng> LocToLatLng(ArrayList<Location> list){
        ArrayList<LatLng> res = new ArrayList<>();
        for (Location loc : list) {
            res.add(new LatLng(loc.getLatitude(), loc.getLongitude()));
        }
        return res;
    }

    private Pair<Integer,Integer> mostDistantPoint(ArrayList<Location> list){
        Location curr = list.get(0);
        float max_dist = 0;
        Location max1 = list.get(0);
        for (int i = 1; i<list.size();i++) {
            float cur_dist = list.get(i).distanceTo(curr);
            if (cur_dist > max_dist) {
                max_dist = cur_dist;
                max1 = list.get(i);
            }
        }
        max_dist = 0;
        Location max2 = max1;
        for (int j = 0; j<list.size();j++){
            float cur_dist = list.get(j).distanceTo(max1);
            if(cur_dist>max_dist){
                max_dist = cur_dist;
                max2 = list.get(j);
            }
        }
        return new Pair<>(list.indexOf(max1),list.indexOf(max2));
    }
}
