package it.uniroma2.musicsharep2p;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class MusicShutReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub

		RemoteServiceConnection rem = RemoteServiceConnection.getInstance(context);
		if(!rem.isdownloading()){
			
			Bundle results = getResultExtras(true);
			//do something if needed
			
		}else{
			
			abortBroadcast();
			
		}

	}

}
