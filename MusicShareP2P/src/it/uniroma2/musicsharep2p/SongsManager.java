package it.uniroma2.musicsharep2p;



import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class SongsManager {

	private SQLiteDatabase database;
	private DataBaseHelper myDbHelper;
	private Context ctx;
	//niente
	// SDCard Path
	//final String MEDIA_PATH = new String("/storage/emulated/0/Music");
	final String MEDIA_PATH = new String("/sdcard");
	private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();



	public void setContext(Context con){

		this.ctx=con;


	}


	// Constructor
	public SongsManager(){

	}



	// Constructor
	public SongsManager(Context con){

		this.ctx=con;

	}

	/**
	 * Function to read all mp3 files from sdcard
	 * and store the details in ArrayList
	 * */
	public ArrayList<HashMap<String, String>> getPlayList(){

		myDbHelper = new DataBaseHelper(ctx);

		try {myDbHelper.createDataBase();} 
		catch (IOException ioe) 
		{throw new Error("Unable to create database");}

		try {myDbHelper.openDataBase();}
		catch(SQLException sqle){throw sqle;}

		


		
		database=myDbHelper.getReadableDatabase();
		
		Cursor cursor;
		cursor = database.rawQuery(("select * from dest"), null);
		cursor.moveToFirst();
		String Ric = new String();
		Ric=cursor.getString(cursor.getColumnIndex("dest"));
		

		File home = new File(Ric);
		if(home!=null)
		{
			FileExtensionFilter lf= new FileExtensionFilter();
			File[] f= home.listFiles(lf);
			if(f!=null)
		if (home.listFiles(new FileExtensionFilter()).length > 0) {
			for (File file : home.listFiles(new FileExtensionFilter())) {
				HashMap<String, String> song = new HashMap<String, String>();
				song.put("songTitle", file.getName().substring(0, (file.getName().length() - 4)));
				song.put("songPath", file.getPath());

				// Adding each song to SongList
				songsList.add(song);
			}
		}
		}
		// return songs list array
		return songsList;
	}
	

	/**
	 * Class to filter files which are having .mp3 extension
	 * */
	class FileExtensionFilter implements FilenameFilter {
		public boolean accept(File dir, String name) {
			return (name.endsWith(".mp3") || name.endsWith(".MP3"));
		}
	}
}