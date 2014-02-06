package it.uniroma2.musicsharep2p;


import it.uniroma2.wifionoff.aidl.OnOffService;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends Activity implements OnCompletionListener, SeekBar.OnSeekBarChangeListener {


	private ImageButton btnPlay;
	private ImageButton btnNext;
	private ImageButton btnPrevious;
	private ImageButton btnPlaylist;
	private ImageButton btnLink;
	private TextView Connection;
	private SeekBar songProgressBar;
	private TextView songTitleLabel;
	private TextView songCurrentDurationLabel;
	private TextView songTotalDurationLabel;
	// Media Player
	private  MediaPlayer mp;
	// Handler to update UI timer, progress bar etc,.
	private Handler mHandler = new Handler();;
	private SongsManager songManager;
	private Utilities utils;
	private int seekForwardTime = 5000; // 5000 milliseconds
	private int seekBackwardTime = 5000; // 5000 milliseconds
	private int currentSongIndex = 0; 
	private boolean isShuffle = false;
	private boolean isRepeat = false;
	private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
	private SQLiteDatabase database;
	private DataBaseHelper myDbHelper;
	 private RemoteServiceConnection conn = null; 
	
	
	  Messenger mService = null;
	    boolean mIsBound;
	    final Messenger mMessenger = new Messenger(new IncomingHandler());


	    class IncomingHandler extends Handler {
	        @Override
	        public void handleMessage(Message msg) {
	            switch (msg.what) {
	            case 1:
	                ;
	                break;
	            case 2:
	                ;
	                break;
	            default:
	                super.handleMessage(msg);
	            }
	        }
	    }

	    
	    OnOffService remoteServiceExample;
   
	    RemoteServiceConnection remote;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		
		myDbHelper = new DataBaseHelper(this);

		try {myDbHelper.createDataBase();} 
		catch (IOException ioe) 
		{throw new Error("Unable to create database");}

		try {myDbHelper.openDataBase();}
		catch(SQLException sqle){throw sqle;}

		// All player buttons
		
		btnLink = (ImageButton) findViewById(R.id.buttonlink);
		btnPlay = (ImageButton) findViewById(R.id.buttonPlay);
		btnNext = (ImageButton) findViewById(R.id.buttonNext);
		btnPrevious = (ImageButton) findViewById(R.id.buttonPrevious);
		btnPlaylist = (ImageButton) findViewById(R.id.buttonPause);;
		songProgressBar = (SeekBar) findViewById(R.id.seekBar);
		songTitleLabel = (TextView) findViewById(R.id.title);
		songCurrentDurationLabel = (TextView) findViewById(R.id.current);
		songTotalDurationLabel = (TextView) findViewById(R.id.total);
		Connection = (TextView) findViewById(R.id.connection);
		
		// Mediaplayer
		mp = new MediaPlayer();
		songManager = new SongsManager();
		songManager.setContext(this);
		utils = new Utilities();

		// Listeners
		songProgressBar.setOnSeekBarChangeListener(this); // Important
		mp.setOnCompletionListener(this); // Important

		// Getting all songs list
		songsList = songManager.getPlayList();

		
		// Check already connected to service or not
		database = myDbHelper.getReadableDatabase();
		Cursor c=database.rawQuery("select * from service",null);
		c.moveToFirst();
		String control = c.getString(c.getColumnIndex("isconnected"));
		if(control.equals("yes")){
			
		
			Connection.setText("Connected");
			mIsBound=true;
			c.close();
			database.close();
		
		}
		
		

		/**
		 * Play button click event
		 * plays a song and changes button to pause image
		 * pauses a song and changes button to play image
		 * */
		btnPlay.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// check for already playing
				if(mp.isPlaying()){
					if(mp!=null){
						mp.pause();
						// Changing button image to play button
						btnPlay.setImageResource(android.R.drawable.ic_media_play);
					}
				}else{
					// Resume song
					if(mp!=null){
						mp.start();
						// Changing button image to pause button
						btnPlay.setImageResource(android.R.drawable.ic_media_pause);
					}
				}

			}
		});

	

		/**
		 * Next button click event
		 * Plays next song by taking currentSongIndex + 1
		 * */
		btnNext.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// check if next song is there or not
				if(currentSongIndex < (songsList.size() - 1)){
					playSong(currentSongIndex + 1);
					currentSongIndex = currentSongIndex + 1;
				}else{
					// play first song
					playSong(0);
					currentSongIndex = 0;
				}

			}
		});

		/**
		 * Back button click event
		 * Plays previous song by currentSongIndex - 1
		 * */
		btnPrevious.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if(currentSongIndex > 0){
					playSong(currentSongIndex - 1);
					currentSongIndex = currentSongIndex - 1;
				}else{
					// play last song
					playSong(songsList.size() - 1);
					currentSongIndex = songsList.size() - 1;
				}

			}
		});

		/**
		 * Button Click event for Repeat button
		 * Enables repeat flag to true
		 * */


		/**
		 * Button Click event for Play list click event
		 * Launches list activity which displays list of songs
		 * */
		btnPlaylist.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getApplicationContext(), PlayListActivity.class);
				startActivityForResult(i, 100);			
			}
		});

		
		/**
		 * Play button click event
		 * plays a song and changes button to pause image
		 * pauses a song and changes button to play image
		 * */
		btnLink.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				if(!mIsBound){
				Log.i("lol", "binding");
				
				database = myDbHelper.getReadableDatabase();
				Cursor c=database.rawQuery("select * from service",null);
				c.moveToFirst();
				String control = c.getString(c.getColumnIndex("isconnected"));
				if(control.equals("no")){
					
					remote = RemoteServiceConnection.getInstance(getApplicationContext());
					remote.safelyConnectTheService();
					Connection.setText("Connected");
					
					Log.w(getClass().getSimpleName(),"Erasing "+control);
					database.delete("service", "isconnected = '"+control+"'", null);
					ContentValues values = new ContentValues();
					values.put("isconnected", "yes");

					Log.w(getClass().getSimpleName(),"Inserted yes");
					database.insert("service", null, values);
					c.close();
					database.close();
					mIsBound=true;
					
					
				}
				
				}
				else{
					
					remote = RemoteServiceConnection.getInstance(getApplicationContext());
					
					database = myDbHelper.getReadableDatabase();
					Cursor c=database.rawQuery("select * from service",null);
					c.moveToFirst();
					String control = c.getString(c.getColumnIndex("isconnected"));
					if(control.equals("yes")){
						
						remote.safelyDisconnectTheService();	
						mIsBound=false;
						Connection.setText("Not Connected");
						Log.w(getClass().getSimpleName(),"Erasing "+control);
						database.delete("service", "isconnected = '"+control+"'", null);
						ContentValues values = new ContentValues();
						values.put("isconnected", "no");

						Log.w(getClass().getSimpleName(),"Inserted no");
						database.insert("service", null, values);
						c.close();
						database.close();
						
						
						
					}
					
				
				}
			

			}
		});
		
	}
	


	/**
	 * Receiving song index from playlist view
	 * and play the song
	 * */
	@Override
	protected void onActivityResult(int requestCode,
			int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == 100){
			currentSongIndex = data.getExtras().getInt("songIndex");
			// play selected song
			playSong(currentSongIndex);
		}

	}

	/**
	 * Function to play a song
	 * @param songIndex - index of song
	 * */
	public void  playSong(int songIndex){
		// Play song
		try {
			mp.reset();
			mp.setDataSource(songsList.get(songIndex).get("songPath"));
			mp.prepare();
			mp.start();
			// Displaying Song title
			String songTitle = songsList.get(songIndex).get("songTitle");
			songTitleLabel.setText(songTitle);

			// Changing Button Image to pause image
			btnPlay.setImageResource(android.R.drawable.ic_media_pause);

			// set Progress bar values
			songProgressBar.setProgress(0);
			songProgressBar.setMax(100);

			// Updating progress bar
			updateProgressBar();			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Update timer on seekbar
	 * */
	public void updateProgressBar() {
		mHandler.postDelayed(mUpdateTimeTask, 100);        
	}	

	/**
	 * Background Runnable thread
	 * */
	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {
			long totalDuration = mp.getDuration();
			long currentDuration = mp.getCurrentPosition();

			// Displaying Total Duration time
			songTotalDurationLabel.setText(""+utils.milliSecondsToTimer(totalDuration));
			// Displaying time completed playing
			songCurrentDurationLabel.setText(""+utils.milliSecondsToTimer(currentDuration));

			// Updating progress bar
			int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));
			//Log.d("Progress", ""+progress);
			songProgressBar.setProgress(progress);

			// Running this thread after 100 milliseconds
			mHandler.postDelayed(this, 100);
		}
	};

	/**
	 * 
	 * */
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

	}

	/**
	 * When user starts moving the progress handler
	 * */
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// remove message Handler from updating progress bar
		mHandler.removeCallbacks(mUpdateTimeTask);
	}

	/**
	 * When user stops moving the progress hanlder
	 * */
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		mHandler.removeCallbacks(mUpdateTimeTask);
		int totalDuration = mp.getDuration();
		int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

		// forward or backward to certain seconds
		mp.seekTo(currentPosition);

		// update timer progress again
		updateProgressBar();
	}

	/**
	 * On Song Playing completed
	 * if repeat is ON play same song again
	 * if shuffle is ON play random song
	 * */
	@Override
	public void onCompletion(MediaPlayer arg0) {

		// check for repeat is ON or OFF
		if(isRepeat){
			// repeat is on play same song again
			playSong(currentSongIndex);
		} else if(isShuffle){
			// shuffle is on - play a random song
			Random rand = new Random();
			currentSongIndex = rand.nextInt((songsList.size() - 1) - 0 + 1) + 0;
			playSong(currentSongIndex);
		} else{
			// no repeat or shuffle ON - play next song
			if(currentSongIndex < (songsList.size() - 1)){
				playSong(currentSongIndex + 1);
				currentSongIndex = currentSongIndex + 1;
			}else{
				// play first song
				playSong(0);
				currentSongIndex = 0;
			}
		}
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		mHandler.removeCallbacks(mUpdateTimeTask);
		mp.release();

	}


	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		menu.clear();
		menu.add("Download Music").setOnMenuItemClickListener(new OnMenuItemClickListener(){
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// TODO Auto-generated method stub
				Intent setting= new Intent(MainActivity.this,DownloadActivity.class);
				startActivity(setting);
				return true;
			}


		});


		menu.add("Settings").setOnMenuItemClickListener(new OnMenuItemClickListener(){

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// TODO Auto-generated method stub
				Intent setting= new Intent(MainActivity.this,MusicSettingActivity.class);
				startActivity(setting);
				return true;
			}


		});

		return true;
	}
	
	private static void runTcpClient(String c, String TCPADD)  {
		
		
        try {
        
        	  Socket clientSocket = new Socket(TCPADD, DefaultValue.PORT);
        	  DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
    
        	  outToServer.writeBytes(c + '\n');
        	  clientSocket.close();
       
        }catch(Exception e){
        	
        }


}
	private void runTcpServer(int TCP_SERVER_PORT) throws IOException {

	    ServerSocket ss = null;

	    try {

	        
			ss = new ServerSocket(TCP_SERVER_PORT);

	        ss.setSoTimeout(10000);

	        //accept connections

	        Socket s = ss.accept();

	        BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

	        String incomingMsg = in.readLine() + System.getProperty("line.separator");

	        Log.i("TcpServer", "received: " + incomingMsg);

	

	        s.close();

	    } catch (InterruptedIOException e) {

	        //if timeout occurs
	    	 ss.close();

	        e.printStackTrace();

	    } catch (IOException e) {

	        e.printStackTrace();

	    } finally {

	        if (ss != null) {

	            try {

	                ss.close();

	            } catch (IOException e) {

	                e.printStackTrace();

	            }

	        }

	    }

	}
	

	
	  public BroadcastReceiver DownloadReceiver = new BroadcastReceiver() {
/////////////////////////////DA FINIRE////////////////////////////////////////
		  
			@Override
			public void onReceive(Context arg1, Intent intent) {
				// TODO Auto-generated method stub
				
				final String Ip=(intent.getCharSequenceExtra("IP")).toString();
				Thread recevingThread = new Thread(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						
						for(;;){
							
							try {
								runTcpServer(DefaultValue.PORT);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						}
						
						
					}
					
					
					
				});
				
				
				Thread SendingThread = new Thread(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
													 
						database=myDbHelper.getReadableDatabase();
						Cursor cursor= database.rawQuery("select * from activequery", null);
						cursor.moveToFirst();
						
						String tosend="MusicShareP2P\nLookingFor\n";
						
						while(!cursor.isAfterLast()){
							
							tosend=tosend+cursor.getString(cursor.getColumnIndex("keyword"))+"\n";
							
						}
						
						tosend=tosend+"ThankYou\n";
						
						runTcpClient(tosend,Ip);
						
					} 
					});
				
			
			}
		   
		   
		   
		   
		   };
		
		
	
}
