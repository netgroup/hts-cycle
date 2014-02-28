package it.uniroma2.wifionoff;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;

public class ServiceCall {
	
	
	private static final String LOG_TAG = "ServiceCall";;

	private MessageMaker myMessageMaker= MessageMaker.getInstance();
	private static ServiceCall instance;

	private Thread gdt;
	private MyDatagramReceiver rct;

	
	
	public static synchronized ServiceCall getInstance(Context ctx)
	{
	if (instance == null){
	instance = new ServiceCall(ctx);
		Log.w(LOG_TAG,"New Istance Created");
	}
	Log.w(LOG_TAG,"Istance Returned");	
	return instance;
	}
    
	public Context getCTX(){
		
		return this.ctx;
		
	}
	
	private void runUdpClient(String c)  {

	    String udpMsg = c;

	    DatagramSocket ds = null;

	    

	    
	    
	    try {

	        ds = new DatagramSocket();
	        ds.setBroadcast(true);
	       // InetAddress serverAddr = InetAddress.getByName(UDPADD);

	        DatagramPacket dp;

	        dp = new DatagramPacket(udpMsg.getBytes(), udpMsg.length(), getBroadcastAddress(), 23000);

	        ds.send(dp);

	    } catch (Exception e) {

	        e.printStackTrace();

	    } finally {

	        if (ds != null) {

	            ds.close();

	        }

	    }

	}
	
	
	public BroadcastReceiver MyReceiver = new BroadcastReceiver() {


		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			
			//aggiungere variabile globale per sapere se stiamo parlando o no
			Intent bro= new Intent(Setting.DONE);
			bro.putExtra("end", "ok");
			
				Log.w("ServiceCall", "Intent Timeout Received");
				ctx.sendBroadcast(bro);
				Log.w("ServiceCall", "Broadcast sendend");
				
				
				
				


			}
		
    };
    
    
    
	private class MyDatagramReceiver extends Thread {
	    private boolean bKeepRunning = true;
	

	    public DatagramSocket socket = null;
	      
	    public void run() {
	    	WifiReceiver wi= WifiReceiver.getInstance(ctx, null, null);
	        String message;
	        String IP;
	        byte[] lmessage = new byte[1024];
	        DatagramPacket packet = new DatagramPacket(lmessage, lmessage.length);
	        Log.i("Server", "Running");
	       
            try {
            	if (socket == null) {
            	    socket = new DatagramSocket(null);
            	    socket.setReuseAddress(true);

            	    socket.bind(new InetSocketAddress(23000));
            	}
			} catch (SocketException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            bKeepRunning = true;
	        while(bKeepRunning) {
	            try {
	                socket.receive(packet);
	                message = new String(lmessage, 0, packet.getLength());
	                IP = (packet.getAddress()).getHostAddress();
	                Log.i("Get", message+" from "+ IP);//per le prove
	                if(message!=null && (!(IP.equals(wi.Ip)))){
	                    Log.i("Get", message+" from "+ IP);
	                    
	                    Intent i = new Intent(Setting.GETMESSAGE);
	                    i.putExtra("Text", message);
	                    i.putExtra("IP", IP);
	                    ctx.sendBroadcast(i);
	                	
	                }
	            
	               //pippo.setText(message);
	            } catch (Throwable e) {
	                e.printStackTrace();
	                bKeepRunning=false;
	            }
	        }

	        if (socket != null) {
	            socket.close();
	        }
	    }
	    

	    public void kill() { 
	        bKeepRunning = false;
	    }

	  
	};
    

	
	public BroadcastReceiver StopReceiver = new BroadcastReceiver() {


		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			
			//aggiungere variabile globale per sapere se stiamo parlando o no
			rct.kill();
			   if (rct.socket != null) {
		            rct.socket.close();
		        }
			
			}
		
    };
	
	public BroadcastReceiver netReceiver = new BroadcastReceiver() {


		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			
			NetworkInfo mWifiNetworkInfo =
	                    (NetworkInfo) arg1.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
			
			
			if(mWifiNetworkInfo.isConnected()){
			
				   Calendar timeout = Calendar.getInstance();
			    	
				    timeout.add(Calendar.SECOND,6);     
				    timeout.add(Calendar.MILLISECOND,(-1*( timeout.get(Calendar.MILLISECOND))));
			        Intent tim = new Intent(arg0, WifiOffReceiver.class);
			        // In reality, you would want to have a static variable for the request code instead of 192837
			        PendingIntent sen = PendingIntent.getBroadcast( arg0, 192837, tim, PendingIntent.FLAG_UPDATE_CURRENT);
			        Log.w("Next Alarm", "H "+ timeout.get(Calendar.HOUR)+":"+timeout.get(Calendar.MINUTE)+":"+timeout.get(Calendar.SECOND)+":"+timeout.get(Calendar.MILLISECOND));
			        // Get the AlarmManager service
			        AlarmManager am2 = (AlarmManager) arg0.getSystemService(Service.ALARM_SERVICE);
			        am2.set(AlarmManager.RTC_WAKEUP, timeout.getTimeInMillis(), sen);
				
				
				
				Log.w("ServiceCall", "Intent Wifi Received");
				
			 	
			 	final IntentFilter FilterT = new IntentFilter();
		       FilterT.addAction(Setting.TIMEOUT_OCCURRED);
			 	arg0.registerReceiver(StopReceiver, FilterT);
				Log.w(LOG_TAG,"StopReceiver Registered");
			 	
				
			     
				gdt = new Thread(new Runnable() {
					    public void run() {
					    
					    	
					    for(int i=0;i<5;i++)
					    {	Log.w("ServiceCall", "PrePacket "+i);
					    
					    	
						    runUdpClient( myMessageMaker.BuildMessage() );	
						    Log.w("ServiceCall", "Packet Sended "+i);
						
						    try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
					    
					    }
							

				    }
					  });


				rct=new MyDatagramReceiver();
				rct.start();
		
					gdt.start();
	
						
				}


					


			}
		
    };

    private Context ctx;


    
    
    
	InetAddress getBroadcastAddress() throws IOException {
	    WifiManager wifi = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
	    DhcpInfo dhcp = wifi.getDhcpInfo();
	    // handle null somehow

	    int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
	    byte[] quads = new byte[4];
	    for (int k = 0; k < 4; k++)
	      quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
	    return InetAddress.getByAddress(quads);
	}  


	public ServiceCall(Context ctx){
		this.ctx=ctx;
		
	}
	


}
