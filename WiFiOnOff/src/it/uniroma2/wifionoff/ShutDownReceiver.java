package it.uniroma2.wifionoff;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ShutDownReceiver extends BroadcastReceiver {

	private static final String Log_tag = "ShutDownReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		
		OnOffService.isOff();
		ServiceCall ser=ServiceCall.getInstance(context);
		ser.getCTX().unregisterReceiver(ser.netReceiver);
		
		context.sendBroadcast(new Intent(Setting.TIMEOUT_OCCURRED)); 
			Log.w(Log_tag,"B Received");
		
			
			
	}

}
