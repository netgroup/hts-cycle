package it.uniroma2.musicsharep2p;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MusicSettingActivity extends Activity {


	private static SQLiteDatabase database;
	private DataBaseHelper myDbHelper;
	
	private void showdialog(){

		
    	LayoutInflater factory = LayoutInflater.from(this);
    	final View textEntryView = factory.inflate(R.layout.edit, null);
    	//text_entry is an Layout XML file containing two text field to display in alert dialog
    	final EditText input1 = (EditText) textEntryView.findViewById(R.id.path);
 
    	final String path;
    	

		try
		
	    {database=myDbHelper.getWritableDatabase();}
		catch(Exception e){
			
			database=myDbHelper.getWritableDatabase();
			
		}
	 
		


	    Cursor cursore = database.rawQuery(("SELECT * FROM dest"), null);
	    cursore.moveToFirst();
	    path = cursore.getString(cursore.getColumnIndex("dest"));
	    cursore.close();
	    
		database.close();
    	
    	
    	input1.setText(path);
    	
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Insert New Music Folder Path");

		// Set an EditText view to get user input 
		alert.setView(textEntryView);
		alert.setCancelable(false);
	
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
		  String dest = input1.getText().toString();

		  
	
			try
			
		    {database=myDbHelper.getWritableDatabase();}
			catch(Exception e){
				
				database=myDbHelper.getWritableDatabase();
				
			}
		  
			
			database.delete("dest", "dest='"+path+"'", null);
		   ContentValues values = new ContentValues();
		   values.put("dest",dest );
		   database.insert("dest", null, values);
		    
			database.close();
		
		 }
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    // Canceled.
				
		  }
		});

		alert.show();
	
    	
		
		
	}

	
	private Button changepath;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music_setting);
		
		changepath=(Button) findViewById(R.id.button1);
		
		OnClickListener ls= new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showdialog();
				
			}
			
			
			
			
		};
		changepath.setOnClickListener(ls);
	
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.music_setting, menu);
		menu.clear();
		return true;
	}

}
