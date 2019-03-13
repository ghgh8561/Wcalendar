package com.wcalendar.klp.wcalendar;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.util.List;

public class City {

    Context context;
    double lat;
    double lon;

    public City(Context context, double lat ,double lon){
        this.lat = lat;
        this.lon = lon;
        this.context = context;
    }

    public String City_name(){
        Geocoder geocoder = new Geocoder(context);
        List<Address> addressList;
        String addressname;
        String[] cityName = null;
        int i = 0;
        try{
            addressList = geocoder.getFromLocation(lat, lon, 10);
            addressname = addressList.get(0).getAddressLine(0).replace(",","");
            cityName = addressname.split(" ");
            for(i = 0; i<cityName.length; i++) { // 문자열중 "시"가포함된 부분을 고름
                if(cityName[i].contains("시"))
                    break;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return cityName[i];
    }

    public String full_name(){
        Geocoder geocoder = new Geocoder(context);
        List<Address> addressList;
        String addressname;
        while(true) {
            try {
                addressList = geocoder.getFromLocation(lat, lon, 10);
                addressname = addressList.get(0).getAddressLine(0).replace(",", "");
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return addressname;
    }
}
