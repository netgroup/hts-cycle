package it.uniroma2.wifionoff;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

public class WifiOffReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		
				context.sendBroadcast(new Intent(Setting.TIMEOUT_OCCURRED)); 
				 Log.w("Service", "BroadCast Sended");

				
				
		

	}
	
}
