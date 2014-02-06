package it.uniroma2.wifionoff;



import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	//Pulsante start
	private ImageView Start;
	//Pulsante stop
	private ImageView Stop;
	//ListView per ora non utilizzata
	private ListView list;
	//Notifica servizio attivo
	private Notification n;
	private SQLiteDatabase database;
	private DataBaseHelper myDbHelper;
	
	
	List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
	
//Imposta l'animazione sui tasti effetto "pressed"
protected void touchcolor(){

		
		Start.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				switch (arg1.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    Start.setImageResource(R.drawable.buttonstartpress);
                    break;
                }
                case MotionEvent.ACTION_UP:{
                	Start.setImageResource(R.drawable.buttonstart);
                	Rect check = new Rect();
                	Start.getLocalVisibleRect(check);
                	if (check.contains((int)arg1.getX(), (int)arg1.getY())) {
                    	Start.performClick();
                	} break;
                }
                }
                return true;
			}


            
        });
        
		
        Stop.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				switch (arg1.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    Stop.setImageResource(R.drawable.buttonstoppress);
                    break;
                }
                case MotionEvent.ACTION_UP:{
                	Stop.setImageResource(R.drawable.buttonstop);
                	Rect check = new Rect();
                	Stop.getLocalVisibleRect(check);
                	if (check.contains((int)arg1.getX(), (int)arg1.getY())) {
                    	Stop.performClick();
                	}
                    break;
                }
                }
                return true;
			}
            
        });

		
		
	}
    
    //
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		myDbHelper = new DataBaseHelper(this);
	    
	     
	    try {myDbHelper.createDataBase();} 
	    	catch (IOException ioe) 
	    	{throw new Error("Unable to create database");}
	     
	    try {myDbHelper.openDataBase();}
	    	catch(SQLException sqle){throw sqle;}
		//prendo le variabili e setto animazione
		Start = (ImageView) findViewById(R.id.StartService);
		Stop = (ImageView) findViewById(R.id.StopService);
		touchcolor();
		
		//Intent per la notifica
		Intent intent2 = new Intent(MainActivity.this, MainActivity.class);
		 PendingIntent pIntent = PendingIntent.getActivity(MainActivity.this, 0, intent2, 0);

		
		
		 //crea la notifica
		 n  = new Notification.Builder(MainActivity.this)
		         .setContentTitle("WiFiOnOff")
		         .setContentText("The service is running!")
		         .setSmallIcon(R.drawable.ic_launcher)
		         .setContentIntent(pIntent)
		         .setOngoing(true) 
		         .build();
		     
		
		//Click listener sui pulsanti
		OnClickListener l = new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				//Se premo Start
				if(arg0.getId() == R.id.StartService){
					
					
					
		
					
				
					//creo intent
					Intent myService = new Intent(MainActivity.this, OnOffService.class);
				    MainActivity.this.startService(myService);
					
							
					 //attivo la notifica
					 NotificationManager notificationManager = 
					   (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
					 Calendar cal = Calendar.getInstance();

					 notificationManager.notify(0, n); 

					 		

					 	
					 	Log.w("M",""+cal.get(Calendar.SECOND));
					Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
						
						database=myDbHelper.getReadableDatabase();
						
						Cursor cursor;
						cursor = database.rawQuery(("select * from times"), null);
						cursor.moveToFirst();
						String Ric = new String();
						Ric=cursor.getString(cursor.getColumnIndex("totaltime"));
						Integer t=Integer.parseInt(Ric);
					 	 if(cal.get(Calendar.SECOND)>29){
						        
						    	cal.add(Calendar.SECOND,(t ) );
						    	cal.add(Calendar.SECOND,(-1*( cal.get(Calendar.SECOND))));
						        cal.add(Calendar.MILLISECOND,(-1*( cal.get(Calendar.MILLISECOND))));
						    	
						    	
						    }else{
					        
						    	cal.add(Calendar.SECOND,(-1*( cal.get(Calendar.SECOND))));
						        cal.add(Calendar.MILLISECOND,(-1*( cal.get(Calendar.MILLISECOND))));
						    cal.add(Calendar.SECOND,t);       }

					 	cursor.close();
					 	
					 	String delay;
					 	
					 	Cursor cur= database.rawQuery("select * from delay",null);
					 	
						cur.moveToFirst();
				
						delay=cur.getString(cur.getColumnIndex("delay"));
						
						Log.w("Main","delay "+delay);
					 	cur.close();
						database.close();
						intent.putExtra("alarm_time", Ric);
						intent.putExtra("delay", delay);
						// In reality, you would want to have a static variable for the request code instead of 192837
						PendingIntent sender = PendingIntent.getBroadcast(MainActivity.this, 192837, intent, PendingIntent.FLAG_UPDATE_CURRENT);
						// Get the AlarmManager service
						AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
						String ampm;
						if( cal.get(Calendar.AM_PM)==Calendar.PM){
							ampm=" PM";
							
						}else
						{
							ampm=" AM";
							
						}
				        long yourmilliseconds = (System.currentTimeMillis()) + ((   t-((System.currentTimeMillis()/1000)%(t)) )*1000)	- (Integer.parseInt(delay)) -(System.currentTimeMillis()%1000)  ;
				        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss");

				        Date resultdate = new Date(yourmilliseconds);
				        System.out.println(sdf.format(resultdate));
				     
											
				        am.setRepeating(AlarmManager.RTC_WAKEUP, (System.currentTimeMillis()) + ((   t-((System.currentTimeMillis()/1000)%(t)) )*1000)	- (Integer.parseInt(delay)) - (System.currentTimeMillis()%1000) , 1000*(t), sender); 
						//am.set(AlarmManager.RTC_WAKEUP,(System.currentTimeMillis()) + ((   t-((System.currentTimeMillis()/1000)%(t)) )*1000)	- (Integer.parseInt(delay))   , sender);

					    //mostro un toast di conferma
					    Toast toast=Toast.makeText(MainActivity.this, 
								"service started", 
								Toast.LENGTH_LONG); 
						toast.show();
						
				}else if(arg0.getId() == R.id.StopService){
					
					Intent next = new Intent(MainActivity.this, AlarmReceiver.class);
					next.putExtra("alarm_message", "O'Doyle Rules!");
					// In reality, you would want to have a static variable for the request code instead of 192837
					PendingIntent sender = PendingIntent.getBroadcast( MainActivity.this , 192837, next, PendingIntent.FLAG_UPDATE_CURRENT);

					// Get the AlarmManager service
					AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
					am.cancel(sender);
					Toast toast=Toast.makeText(MainActivity.this, 
							"service stopped", 
							Toast.LENGTH_LONG); 
					toast.show();
					 NotificationManager notificationManager = 
							   (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

							 notificationManager.cancel( 0); 
								Intent myService = new Intent(MainActivity.this, OnOffService.class);
							 MainActivity.this.stopService(myService);
				}
					
				
				
			}
			
			
			
		};
		
		Start.setOnClickListener(l);
		Stop.setOnClickListener(l);
	    
 	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		menu.clear();
		menu.add("Settings").setOnMenuItemClickListener(new OnMenuItemClickListener(){

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// TODO Auto-generated method stub
				Intent setting= new Intent(MainActivity.this,SettingActivity.class);
				startActivity(setting);
				return true;
			}
			
			
		});
		
		return true;
	}


}
