package it.uniroma2.musicsharep2p;


import it.uniroma2.wifionoff.aidl.OnOffService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

public class RemoteServiceConnection implements ServiceConnection {

	private static final String AIDL_MESSAGE_SERVICE_CLASS = ".OnOffService";
	private static final String AIDL_MESSAGE_SERVICE_PACKAGE = "it.uniroma2.wifionoff";
	private static final String INTENT_ACTION_BIND_MESSAGE_SERVICE = "bind.OnOffService";
	private final static String LOG_TAG = "RemoteServiceConnection";

	public OnOffService service;
	private Context context;
	public static RemoteServiceConnection instance = null;
	
	private RemoteServiceConnection(Context c){
		this.context=c;
		}
	
//	
//	public RemoteServiceConnection() {
//		context = null;
//		}

	@Override
	public void onServiceConnected(ComponentName arg0, IBinder arg1) {		
		this.service = OnOffService.Stub.asInterface(arg1);
	
		try {
			service.addMe(DefaultValue.APPNAME, DefaultValue.LISTEN);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			  Toast toast=Toast.makeText(context, 
						"Error: Service Not Running", 
						Toast.LENGTH_LONG); 
				toast.show();
			e.printStackTrace();
		}
		Log.d(LOG_TAG, "The service is now connected!");
	}

	@Override
	public void onServiceDisconnected(ComponentName arg0) {
		Log.d(LOG_TAG,
				"The connection to the service got disconnected unexpectedly!");
		
		service = null;

	}

	public void safelyDisconnectTheService() {
		if (service != null) {			
			context.unbindService(this);
			try {
				service.removeMe(DefaultValue.APPNAME);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			service = null;
			Log.d(LOG_TAG, "The connection to the service was closed!");
		}
	}


	public void setContext(Context c){
		
		this.context=c;
		
	}
	

	/**
	 * Method to connect the Service.
	 */
	public void safelyConnectTheService() {
		
		if (service == null) {
			Intent bindIntent = new Intent(INTENT_ACTION_BIND_MESSAGE_SERVICE);
			bindIntent.setClassName(AIDL_MESSAGE_SERVICE_PACKAGE,
					AIDL_MESSAGE_SERVICE_PACKAGE + AIDL_MESSAGE_SERVICE_CLASS);
			context.bindService(bindIntent, this, Context.BIND_AUTO_CREATE);
			Log.d(LOG_TAG,
					"The Service will be connected soon (asynchronus call)...");
		}
	}

	public static RemoteServiceConnection getInstance(Context applicationContext) {
		// TODO Auto-generated method stub
		if(instance==null){
			
			instance=new RemoteServiceConnection(applicationContext);
			
		}
		return instance;
	}

	
}
