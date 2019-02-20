package com.wcalendar.klp.wcalendar;

import android.app.ActivityManager;
import android.content.Context;

public class Service_State {

    Context context;

    public Service_State(Context context){
        this.context = context;
    }

    public boolean isServiceRunning(){
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)){
            if(MyService.class.getName().equals(service.service.getClassName()))
                return true;
        }
        return false;
    }
}
