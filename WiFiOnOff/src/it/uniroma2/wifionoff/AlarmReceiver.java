package it.uniroma2.wifionoff;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.w("Alarm","Start");
		long now=System.currentTimeMillis();
		
		Intent myService = new Intent(Setting.START);
	    context.sendBroadcast(myService);
        String time="";
        Integer t=30;
        
	    if(intent.hasExtra("alarm_time")){
	    	
	    	time= (intent.getCharSequenceExtra("alarm_time")).toString();
	    	t=Integer.parseInt(time);
	    	
	    	
	    }
	  
	    // add 5 minutes to the calendar object

	
	    
//	    
//        Intent next = new Intent(context, AlarmReceiver.class);
//        next.putExtra("alarm_time", time);
//        // In reality, you would want to have a static variable for the request code instead of 192837
//        PendingIntent sender = PendingIntent.getBroadcast( context, 192837, next, PendingIntent.FLAG_UPDATE_CURRENT);
//        // Get the AlarmManager service
//        
//        long yourmilliseconds = now + (t*1000) ;
//        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss");
//
//        Date resultdate = new Date(yourmilliseconds);
//        System.out.println(sdf.format(resultdate));
//        
//        AlarmManager am = (AlarmManager) context.getSystemService(Service.ALARM_SERVICE);
//        am.set(AlarmManager.RTC_WAKEUP, now + (t*1000) , sender);
//        


        
	    // add 5 minutes to the timeoutendar object
     

        
	}

}
