package it.uniroma2.wifionoff;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PacketHandler extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {


		String m=intent.getStringExtra("Message");
		String ip=intent.getStringExtra("IP");
		
		String[] lines = m.split(System.getProperty("line.separator"));
		
		for(int i=1; i<lines.length;i++){
			
			String l=lines[i];
			
			for(int j=0;j<OnOffService.ConnectedApp.size();j++){
				
				if(OnOffService.ConnectedApp.get(j).getName().equals(l)){
					
					Intent noti=new Intent (OnOffService.ConnectedApp.get(j).getBro());
					noti.putExtra("IP", ip);
					context.sendBroadcast(noti);
					
				}
				
			}
			
		}
			
		
		
		
	}

}
