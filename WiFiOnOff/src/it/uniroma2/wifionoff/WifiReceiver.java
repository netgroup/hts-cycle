package it.uniroma2.wifionoff;

import java.net.InetAddress;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.os.SystemClock;
import android.util.Log;

public class WifiReceiver{
	
	private WifiManagerNew mWifiManagerNew;
	private WifiManager mWifiManager;
	private Context ctx;
	private static WifiReceiver instance;
	private static String LOG_TAG="WifiReceiver";
	public String Ip=null;
	
	
	private WifiReceiver(Context ctx, WifiManager mw, WifiManagerNew mwn){
		
		this.ctx=ctx;
		this.mWifiManager=mw;
		this.mWifiManagerNew=mwn;
		
	}
	
	
	public static synchronized WifiReceiver getInstance(Context ctx, WifiManager mw, WifiManagerNew mwn)
	{
	if (instance == null){
	instance = new WifiReceiver(ctx,mw,mwn);
		Log.w(LOG_TAG,"New Istance Created");
	}
	Log.w(LOG_TAG,"Istance Returned");	
	return instance;
	}
    
   public BroadcastReceiver WifiReceiverB = new BroadcastReceiver() {


		@Override
		public void onReceive(Context context, Intent arg1) {
			// TODO Auto-generated method stub
			Log.w("Service", "Intent Wifi Received");
			
			if(arg1.hasExtra(WifiManager.EXTRA_WIFI_STATE)){

				int state =  arg1.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -3);

				if(state==WifiManager.WIFI_STATE_ENABLED){


					if (mWifiManagerNew.isIbssSupported()) {
						Log.w("WifiReceiver","Ad-hoc mode is supported");
						configureAdhocNetwork(1);
						Log.w("WifiReceiver","AdHocNetwork On");
						
						
					} else {

						Log.w("WifiReceiver","AdHocNetwork Not Supported");

					}



				}




			}
		}
    };

    

	
  public BroadcastReceiver ListenReceiver = new BroadcastReceiver() {


		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
	        Log.w("ListenReceiver", "Intent Listen Received");

			if(arg1.hasExtra("end")){
				
				if(arg1.getStringExtra("end").equals("ok")){
					
					//cambiare questa parte! mettersi in coda dall'altra app e vedere se torna quell'intent!

					
					
					configureAdhocNetwork(0);
					Ip = null;
					try{
						mWifiManager.setWifiEnabled(false);
						Log.w("ListenReceiver", "WifiOff");

					}catch(SecurityException s){


					}
					

				  
			
				
				 	WifiReceiver wi = WifiReceiver.getInstance(ctx,mWifiManager,mWifiManagerNew);
				 	ServiceCall ser = ServiceCall.getInstance(ctx);
					
					 	
	

							
					

					ctx.unregisterReceiver(wi.ListenReceiver);
					ctx.unregisterReceiver(wi.WifiReceiverB);
					ctx.unregisterReceiver(ser.MyReceiver);
					ctx.unregisterReceiver(ser.netReceiver);
					
					
					
					
					
				}
				
			}

			


			}
		
    };




    
    
	private void configureAdhocNetwork(int check) {
		try {
			/* We use WifiConfigurationNew which provides a way to access 
			 * the Ad-hoc mode and static IP configuration options which are 
			 * not part of the standard API yet */
			WifiConfigurationNew wifiConfig = new WifiConfigurationNew();

			/* Set the SSID and security as normal */
			wifiConfig.SSID = "\"AdHocDemo\"";
			wifiConfig.allowedKeyManagement.set(KeyMgmt.NONE);

			/* Use reflection until API is official */
			wifiConfig.setIsIBSS(true);
			wifiConfig.setFrequency(2442);
			
			/* Use reflection to configure static IP addresses */
			wifiConfig.setIpAssignment("STATIC");
			if(Ip==null){
				
				Ip = IpMaker.getRandomIp();
				
			}
			wifiConfig.setIpAddress(InetAddress.getByName(Ip), 16);
			//wifiConfig.setGateway(InetAddress.getByName("10.0.0.1"));
			wifiConfig.setDNS(InetAddress.getByName("8.8.8.8"));

			/* Add, enable and save network as normal */
			int id = mWifiManager.addNetwork(wifiConfig);

			if (id < 0) {

			} else {
				if(check==1)
				{mWifiManager.enableNetwork(id, true);
				Log.w("WifiReceiver","AdHocNetwork Enabled");}
				else
				{ mWifiManager.disableNetwork(id);
				mWifiManager.removeNetwork(id);
				Log.w("WifiReceiver","Disabled");}

				mWifiManager.saveConfiguration();


			}


		} catch (Exception e) {

			e.printStackTrace();
		}
	}



	
	

}
