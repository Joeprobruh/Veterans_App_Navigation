package com.Senior_Proj_Fall_2015.Veterans_App_Navigation;

import com.google.android.gms.maps.model.Marker;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

public class MyMarker {
    private ArrayList<Marker> mMarkers;
    private ArrayList<String> names;
    private ArrayList<String> descriptions;

    public MyMarker(){
        mMarkers = new ArrayList<>();
        names = new ArrayList<>();
        descriptions = new ArrayList<>();
    }

    public void add(Marker m, String s, String t){
        mMarkers.add(m);
        names.add(s);
        descriptions.add(t);
    }

    public ArrayList<Marker> getMarkers(){
        return mMarkers;
    }

    public Marker getId(Marker marker){
        for (Marker m : mMarkers){
            if(m.equals(marker)){
                return m;
            }
        }
        return null;
    }

    public int getCount(Marker marker){
        int i = 0;
        for (Marker m : mMarkers){
            if(m.equals(marker)){
                return i;
            }
            else{
                i++;
            }
        }
        return 0;
    }

    public String getLocName(int x){
        return names.get(x);
    }

    public String getLocInfo(int x){
        return descriptions.get(x);
    }

    public Marker getSpecificID(int x){
        return mMarkers.get(x);
    }
}
