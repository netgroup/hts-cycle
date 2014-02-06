package it.uniroma2.wifionoff;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;

public class WifiHandler {

	static private WifiHandler single_instance=null;
	final static String LOGTAG="WifiHandler";
	private WifiManagerNew mWifiManagerNew;
	int secondstart;
	int minutestart;
	Context ctx;
	
	
	
	  private WifiManager mWifiManager;
	

	
	  public void startall(Context arg0){	// TODO Auto-generated method stub
			
			//aggiungere variabile globale per sapere se stiamo parlando o no
			
				arg0=OnOffService.context;
			mWifiManager = (WifiManager) arg0.getSystemService(Context.WIFI_SERVICE);
			
			 mWifiManagerNew = new WifiManagerNew(mWifiManager);

			

		       final IntentFilter Filter = new IntentFilter();
		       Filter.addAction(Setting.TIMEOUT_OCCURRED);
			 	ServiceCall ser = new ServiceCall(arg0);
			 	arg0.registerReceiver(ser.MyReceiver, Filter);
				Log.w(LOGTAG,"MyReceiver Registered");
			 
			 	final IntentFilter FilterS = new IntentFilter();
		       FilterS.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
			 	arg0.registerReceiver(ser.netReceiver, FilterS);
				Log.w(LOGTAG,"NetReceiver Registered");
			 	
			 	
			 	WifiReceiver wi = WifiReceiver.getInstance(arg0,mWifiManager,mWifiManagerNew);
			 	final IntentFilter FilterW = new IntentFilter();
		        FilterW.addAction(Setting.DONE);
			 	arg0.registerReceiver(wi.ListenReceiver, FilterW);
				Log.w(LOGTAG,"ListenReceiver Registered");
			 	
			 	
			 	final IntentFilter FilterT = new IntentFilter();
		       FilterT.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
			 	arg0.registerReceiver(wi.WifiReceiverB, FilterT);
				Log.w(LOGTAG,"WifiReceiver Registered");
			 	
//			 	final IntentFilter FilterO = new IntentFilter();
//			       FilterT.addAction(Setting.ON);
//				 	arg0.registerReceiver(wi.OnReceiver, FilterO);
//					Log.w(LOGTAG,"WifiReceiver Registered");
				
				Intent on= new Intent(Setting.ON);
				arg0.sendBroadcast(on);
			 
			 


			}
	


	static public WifiHandler getInstance()
	{
	 if ( single_instance == null )
		 {
		 	//single_instance = new WifiHandler();
		 	Log.i(LOGTAG, "WifiHandler instance created.(Constructor called)");
		 }
	 
	 return single_instance;
	}
	
	public WifiHandler(Context ctx, WifiManager mw, WifiManagerNew mwn){
		
		this.ctx=ctx;
		this.mWifiManager=mw;
		this.mWifiManagerNew=mwn;
		
	}

	

	public void run() {
		// TODO Auto-generated method stub
		
	//	final IntentFilter Filter = new IntentFilter();
	//       Filter.addAction(Setting.START);
		 	
	 //      Log.i(LOGTAG, "COD");
	       
	      // ctx.registerReceiver(MyReceiver, Filter);
	}

}
