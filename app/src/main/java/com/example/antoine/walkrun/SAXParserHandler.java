package com.example.antoine.walkrun;

import android.location.Location;

import java.security.Provider;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Created by Antoine on 13/05/2017.
 */

public class SAXParserHandler extends DefaultHandler {
    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    private ArrayList<Location> list;
    private Location cur;

    private boolean bTime = false;



    public ArrayList<Location> getList(){
        return list;
    }
    /**
     * This method is called whenever start tag of an element is encountered
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
        if (qName.equalsIgnoreCase("trkpt")){
            cur = new Location("null");
            cur.setLatitude(Double.parseDouble(attributes.getValue("lat")));
            cur.setLongitude(Double.parseDouble(attributes.getValue("lon")));

        } else if (qName.equalsIgnoreCase("time")){
            bTime = true;
        } else if(qName.equalsIgnoreCase("trkseg")){
            list = new ArrayList<>();
        }
    }

    /**
     * This method is called whenever ends tag of an element is encountered
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("trkpt")) {
            // add the created Employee object to list
            list.add(cur);
        }
    }

    /**
     * This method is called whenever character data is encountered
     */
    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        String data = new String(ch, start, length);
        if(bTime){
            Long time = (long)0;
            try{
                time = TIME_FORMAT.parse(data).getTime();
            } catch (ParseException e){
                e.printStackTrace();
                //throw new SAXException("error time parse :"+data.toString()+"end");
            }
            cur.setTime(time);
            bTime = false;
        }
    }
}
