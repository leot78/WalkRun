package com.example.antoine.walkrun;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.util.Config;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;


public class GPX {
    private static final String TAG = GPX.class.getName();

    public static void writePath(File file, String n, List<Location> points, Context context) {

        String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?><gpx xmlns=\"http://www.topografix.com/GPX/1/1\" creator=\"WalkRun\" version=\"1.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"  xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\"><trk>\r\n";
        String name = "<name>" + n + "</name><trkseg>\r\n";

        String segments = "";
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        for (Location l : points) {
            segments += "<trkpt lat=\"" + l.getLatitude() + "\" lon=\"" + l.getLongitude() + "\"><time>" + df.format(new Date(l.getTime())) + "</time></trkpt>\r\n";
        }

        String footer = "</trkseg></trk></gpx>";

        String data = header + name + segments + footer;


        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(data.getBytes());
            outputStream.close();
            Toast.makeText(context, "write file in:"+file.toString(),Toast.LENGTH_LONG).show();
            Log.i(TAG,"write file in:"+file.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*try {
            FileWriter writer = new FileWriter(file, false);
            writer.append(header);
            writer.append(name);
            writer.append(segments);
            writer.append(footer);
            writer.flush();
            writer.close();

            Log.i(TAG, "Saved " + points.size() + " points.");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.e(TAG, "Error Writting Path",e);
        }*/
    }

    public static ArrayList<Location> parse(File file){
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        ArrayList<Location> list = new ArrayList<>();
        try{
            SAXParser saxParser = saxParserFactory.newSAXParser();
            SAXParserHandler handler = new SAXParserHandler();
            saxParser.parse(file,handler);
            list = handler.getList();
         } catch(SAXException | IOException | ParserConfigurationException e){
            e.printStackTrace();
        }
        return list;
    }

    public static String convertMsToHMmSs(long ms) {
        ms=ms/1000;
        long s = ms % 60;
        long m = (ms / 60) % 60;
        long h = (ms / (60 * 60)) % 24;
        return String.format("%dh %02dm %02ds", h,m,s);
    }
}

