package com.example.antoine.walkrun;

import android.content.Context;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Antoine on 12/05/2017.
 */

public class Tracking implements Parcelable {
    private String name;
    private ArrayList<Location> locations;
    private long time;
    private float distance;
    private long date;
    private String path;

    public Tracking(String name, String path, long time, float distance, long date){
        this.path = path;
        this.name = name;
        this.date = date;
        this.distance = distance;
        this.time = time;
    }

    public Tracking(){
        locations = new ArrayList<>();
        distance = 0;
        time = 0;
        date = 0;
    }

    public void computeDistance(){
        Location last=null;
        distance = 0;
        for(Location loc : locations){
            if(last == null)
                last = loc;
            distance += loc.distanceTo(last);
            last = loc;
        }
    }

    public void computeTime(){
        date = locations.get(0).getTime();
        time = locations.get(locations.size()-1).getTime()
                - locations.get(0).getTime();
    }

    public String getPath() {return path;}
    public long getDate() {return date;}
    public long getTime() {return time;}
    public String getName() {return name;}
    public ArrayList<Location> getLocations(){return locations;}
    public float getDistance(){return distance;}

    public void setPath(String path){this.path=path;}
    public void setName(String name){this.name = name;}
    public void setDate(long date) {this.date = date;}

    public void setLocations(ArrayList<Location> locations) {
        this.locations = locations;
        if(locations.size()!=0) {
            computeDistance();
            computeTime();
        }
    }

    public void computeLocations(Context context){
        locations = GPX.parse(new File(context.getExternalFilesDir(null),path+".gpx"));
        computeDistance();
        computeTime();
    }

    public ArrayList<Long> getTimeList(){
        ArrayList<Long> list = new ArrayList<>();
        float lastK = 0;
        Location last=null;
        float dist = 0;
        long last_time=0;
        for(Location loc : locations){
            if(last == null) {
                last = loc;
                last_time = loc.getTime();
            }
            dist += loc.distanceTo(last);
            if(dist - lastK > 1000){
                Long pace_time = loc.getTime() - last_time;
                list.add(pace_time);
                last_time = loc.getTime();
                lastK +=1000;
            }
            last = loc;
        }
        return list;
    }


    protected Tracking(Parcel in) {
        name = in.readString();
        if (in.readByte() == 0x01) {
            locations = new ArrayList<Location>();
            in.readList(locations, Location.class.getClassLoader());
        } else {
            locations = null;
        }
        time = in.readLong();
        distance = in.readFloat();
        date = in.readLong();
        path = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        if (locations == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(locations);
        }
        dest.writeLong(time);
        dest.writeFloat(distance);
        dest.writeLong(date);
        dest.writeString(path);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Tracking> CREATOR = new Parcelable.Creator<Tracking>() {
        @Override
        public Tracking createFromParcel(Parcel in) {
            return new Tracking(in);
        }

        @Override
        public Tracking[] newArray(int size) {
            return new Tracking[size];
        }
    };

}
