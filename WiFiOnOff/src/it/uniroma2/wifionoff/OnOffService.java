package it.uniroma2.wifionoff;

import it.uniroma2.wifionoff.aidl.OnOffService.Stub;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class OnOffService extends Service { 
	
	public static String LOGTAG = "OnOffService";
	static Context context;
	public static List<AppHelper> ConnectedApp;
	private WifiManager mWifiManager;
	private WifiManagerNew mWifiManagerNew;
	public static String Device;
	//public static boolean alarm;
	public static boolean stillon=false;
	private static Integer delay=0;
	public static List<String> IPs;
	private SQLiteDatabase database;
	private DataBaseHelper myDbHelper;

	
	

	
	   public BroadcastReceiver MyReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg1, Intent intent) {
			// TODO Auto-generated method stub
			
			IPs= new ArrayList<String>();
			
			mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			
			 mWifiManagerNew = new WifiManagerNew(mWifiManager);

			 	ServiceCall ser = ServiceCall.getInstance(context);
			 	WifiReceiver wi = WifiReceiver.getInstance(context,mWifiManager,mWifiManagerNew);
				 

		       
			 	
		   if(!mWifiManager.isWifiEnabled()){
					
			   final IntentFilter Filter = new IntentFilter();
		       Filter.addAction(Setting.TIMEOUT_OCCURRED);
			 	context.registerReceiver(ser.MyReceiver, Filter);
				Log.w(LOGTAG,"MyReceiver Registered");
			 	
			 		final IntentFilter FilterW = new IntentFilter();
		        FilterW.addAction(Setting.DONE);
			 	context.registerReceiver(wi.ListenReceiver, FilterW);
				Log.w(LOGTAG,"ListenReceiver Registered");
			 	
			 	
			 	final IntentFilter FilterT = new IntentFilter();
		       FilterT.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
			 	context.registerReceiver(wi.WifiReceiverB, FilterT);
				Log.w(LOGTAG,"WifiReceiver Registered");
			   
			 	final IntentFilter FilterS = new IntentFilter();
			       FilterS.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
				 	context.registerReceiver(ser.netReceiver, FilterS);
					Log.w(LOGTAG,"NetReceiver Registered");
				 	
			   
			   
					try{
						mWifiManager.setWifiEnabled(true);
						Log.w("Service","WifiOn");
					}catch(SecurityException s){


					}
			

			}else{
				
				if(stillon){
					
				 	final IntentFilter FilterS = new IntentFilter();
				       FilterS.addAction(Setting.SEARCH);
					 	context.registerReceiver(ser.netReceiver, FilterS);
						Log.w(LOGTAG,"NetReceiver Registered");
					 	
					
					
					try{
						
						Thread.sleep(delay);
						
						
					}catch(Exception e){
						
						e.printStackTrace();
					}
					
					sendBroadcast(new Intent(Setting.SEARCH)); 
					
					
				}else{
					
					

					unregisterReceiver(wi.ListenReceiver);
					unregisterReceiver(wi.WifiReceiverB);
					unregisterReceiver(ser.MyReceiver);
					unregisterReceiver(ser.netReceiver);
					
					
				}
				
			}
			
		}
	   
	   
	   
	   
	   };
	
	

	
	
	@Override
	public void onCreate() { 
		super.onCreate();
		myDbHelper = new DataBaseHelper(this);
	    
	    try {myDbHelper.createDataBase();} 
    	catch (IOException ioe) 
    	{throw new Error("Unable to create database");}
     
    try {myDbHelper.openDataBase();}
    	catch(SQLException sqle){throw sqle;}
    
		context = getApplicationContext();
		IntentFilter Start=new IntentFilter(Setting.START);
		context.registerReceiver(MyReceiver, Start);
		ConnectedApp=new ArrayList<AppHelper>();
	    database=myDbHelper.getReadableDatabase();
		Cursor cursor;
		cursor = database.rawQuery(("select * from name"), null);
		cursor.moveToFirst();
		Device=cursor.getString(cursor.getColumnIndex("name"));
		cursor.close();
		database.close();
		IPs=new ArrayList<String>();
		Log.i(LOGTAG, "SharingFileService Created");
	
		
	}
	
	@Override
	public void onDestroy(){
		
	
		
	}

	
	protected ServiceConnection mServerConn = new ServiceConnection() {
	   

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			Log.w("lol","Lol");
			
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			
		}
	};
	
	
		  @Override
		  public int onStartCommand(Intent intent, int flags, int startId) {
		    //TODO do something useful
			  
			  
			  
		    return Service.START_NOT_STICKY;
		    
		    
		    
		    
		  }
		  
			
		  @Override
		  public IBinder onBind(Intent arg0) {
		  Log.d(getClass().getSimpleName(), "onBind()");
  
		  
		  
		  return OnOffServiceStub;
		  }  
		  

		  private Stub OnOffServiceStub = new Stub() {
			  public void doServiceTask() throws RemoteException {
			  /// Write here, code to be executed in background
			  Log.i(getClass().getSimpleName(),"Hello World From Remote Service!!");
			  }

			@Override
			public boolean removeMe(String s) throws RemoteException {
				// TODO Auto-generated method stub
				Log.i(getClass().getSimpleName(),"Removing "+s);

				for(int i=0; i< OnOffService.ConnectedApp.size(); i++)
					if( ( (ConnectedApp.get(i)).getName()).equals(s) )
					{
						ConnectedApp.remove(i);
						
						return true;
						
					}
				return false;
			}

			@Override
			public boolean addMe(String name, String Bro)
					throws RemoteException {
				// TODO Auto-generated method stub
				Log.i(getClass().getSimpleName(),"Adding "+name+" with intent "+Bro);

				  AppHelper a= new AppHelper( name, Bro);
				  
				  ConnectedApp.add( a );
				
				return false;
			}   
			  };

		public static void setDelay(int parseInt) {
			// TODO Auto-generated method stub
			delay=parseInt;
		}

		public static void setStilloN() {
			// TODO Auto-generated method stub
			stillon=true;
		}

		public static void isOff() {
			// TODO Auto-generated method stub
			stillon=false;
		}
		  
		} 

