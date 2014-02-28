package it.uniroma2.wifionoff;

import java.io.IOException;
import java.net.InetAddress;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SettingActivity extends Activity {

	
	private Button time;
	private Button app;
	private Button delay;
	private static long start;
	private static long stop;
	private SQLiteDatabase database;
	private DataBaseHelper myDbHelper;
	

	  private WifiManager mWifiManager;
	private WifiManagerNew mWifiManagerNew;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		time=(Button) findViewById(R.id.button1);
		app=(Button) findViewById(R.id.button2);
		delay = (Button) findViewById(R.id.button3);
		
		OnClickListener l = new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(v.getId()==R.id.button1){
					
					
					Intent List=new Intent (SettingActivity.this,MyListActivity.class);
					startActivity(List);
					
					
				}
				
				
			}
			
			
			
		};
		
		
		
		mWifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		
		 mWifiManagerNew = new WifiManagerNew(mWifiManager);


		
		time.setOnClickListener(l);
		
		delay.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			 	final IntentFilter FilterT = new IntentFilter();
		       FilterT.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
				SettingActivity.this.registerReceiver(TestReceiver,FilterT);
				
				   
				 	final IntentFilter FilterS = new IntentFilter();
				       FilterS.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
					 	SettingActivity.this.registerReceiver(netReceiver, FilterS);
						Log.w("LOL","NetReceiver Registered");
				
				start=System.currentTimeMillis();
				
				try{
					mWifiManager.setWifiEnabled(true);
					Log.w("Setting","WifiOn");
				}catch(SecurityException s){


				}
	
				
			}
			
			
		});
		
		app.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stu
				
			}
						
		});
				
	}

	

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
			wifiConfig.setIpAddress(InetAddress.getByName("10.0.0.2"), 24);
			//wifiConfig.setGateway(InetAddress.getByName("10.0.0.1"));
			wifiConfig.setDNS(InetAddress.getByName("8.8.8.8"));

			/* Add, enable and save network as normal */
			int id = mWifiManager.addNetwork(wifiConfig);

			if (id < 0) {

			} else {
				if(check==1)
				{mWifiManager.enableNetwork(id, true);
				Log.w("Setting","AdHocNetwork Enabled");}
				else
				{ mWifiManager.disableNetwork(id);
				mWifiManager.removeNetwork(id);
				Log.w("Setting","Disabled");}

				mWifiManager.saveConfiguration();


			}


		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	 public BroadcastReceiver TestReceiver = new BroadcastReceiver() {


			@Override
			public void onReceive(Context context, Intent arg1) {
				// TODO Auto-generated method stub
				Log.w("Service", "Intent Wifi Received");
				
				//if(arg1.hasExtra(WifiManager.EXTRA_WIFI_STATE)){

				//	int state =  arg1.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -3);

				//	if(state==WifiManager.WIFI_STATE_ENABLED){
						Log.w("Setting","Ad-hoc mode is supported");


						if (mWifiManagerNew.isIbssSupported()) {
							Log.w("Setting","Ad-hoc mode is supported");

							configureAdhocNetwork(1);
							Log.w("Setting","AdHocNetwork On");
												
						} else {

							Log.w("WifiReceiver","AdHocNetwork Not Supported");

						}



					//}




				//}
				context.unregisterReceiver(this);
			}
	    };

	

	    public BroadcastReceiver netReceiver = new BroadcastReceiver() {


			@Override
			public void onReceive(Context arg0, Intent arg1) {
				// TODO Auto-generated method stub
				
				NetworkInfo mWifiNetworkInfo =
		                    (NetworkInfo) arg1.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
				
				
				if(mWifiNetworkInfo.isConnected()){
					
					stop=System.currentTimeMillis();
					
					
					
					Log.w("Delay", ""+(stop-start));
					
					
					
					myDbHelper = new DataBaseHelper(SettingActivity.this);
				    
				     
				    try {myDbHelper.createDataBase();} 
				    	catch (IOException ioe) 
				    	{throw new Error("Unable to create database");}
				     
				    try {myDbHelper.openDataBase();}
				    	catch(SQLException sqle){throw sqle;}
					
					configureAdhocNetwork(0);
					
					try{
						mWifiManager.setWifiEnabled(false);
						Log.w("Setting","WifiOn");
					}catch(SecurityException s){


					}
					
					database=myDbHelper.getReadableDatabase();
					
					
					Cursor cursor;
					cursor = database.rawQuery(("select * from delay"), null);
					cursor.moveToFirst();
					String Ric = new String();
					Ric=cursor.getString(cursor.getColumnIndex("delay"));
					Log.w("Setting","Erasing "+Ric);
					database.delete("delay", "delay = '"+Ric+"'", null);
					ContentValues values = new ContentValues();
					values.put("delay",(stop-start)+"" );

					Log.w("Setting","Inserted "+(stop-start)+"");
					database.insert("delay", null, values);
					cursor.close();
					
					
					database.close();
					arg0.unregisterReceiver(this);
					
			
				
				}
				
				
			}
			
			
			
	    };
	       
	    
	    
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.setting, menu);
		menu.clear();
		return true;
	}

}
