package com.wcalendar.klp.wcalendar;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.util.List;

public class Area_name {

    double lat;
    double lon;
    Context context;
    List<Address> address_List;

    public Area_name(Context context, double lat, double lon){
        this.lat = lat;
        this.lon = lon;
        this.context = context;
    }
    public String Location_name(){//지역
        Geocoder geocoder = new Geocoder(context);
        String address = null;
        try{
            address_List = geocoder.getFromLocation(lat,lon,10);
            address = address_List.get(0).getAdminArea();
            if(address.equals("경상남도"))
                address = "경남";
            if(address.equals("경상북도"))
                address = "경북";
            if(address.equals("충청북도"))
                address = "충북";
            if(address.equals("충청남도"))
                address = "충남";
            if(address.equals("경상북도"))
                address = "경북";
            if(address.equals("전라남도"))
                address = "전남";
            if(address.equals("전라북도"))
                address = "전북";
            if(address.equals("강원도"))
                address = "강원";
            if(address.equals("서울특별시"))
                address = "서울";
            if(address.equals("부산광역시"))
                address = "부산";
            if(address.equals("대구광역시"))
                address = "대구";
            if(address.equals("인천광역시"))
                address = "인천";
            if(address.equals("광주광역시"))
                address = "광주";
            if(address.equals("대전광역시"))
                address = "대전";
            if(address.equals("울산광역시"))
                address = "울산";
            if(address.equals("경기도"))
                address = "경기";
            if(address.equals("제주특별자치도"))
                address = "제주";
        }catch (Exception e){
            e.printStackTrace();
        }
        return address;
    }
}
