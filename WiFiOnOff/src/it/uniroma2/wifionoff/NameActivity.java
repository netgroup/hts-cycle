package it.uniroma2.wifionoff;

import java.io.IOException;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class NameActivity extends Activity {

	private SQLiteDatabase database;
	private DataBaseHelper myDbHelper;
	private EditText name;
	private Button submitName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_name);
		
		name = (EditText) findViewById(R.id.NAME);
		submitName = (Button) findViewById(R.id.SETNAME);
		

	     
	    try {myDbHelper.createDataBase();} 
	    	catch (IOException ioe) 
	    	{throw new Error("Unable to create database");}
	     
	    try {myDbHelper.openDataBase();}
	    	catch(SQLException sqle){throw sqle;}
	    
	    database=myDbHelper.getReadableDatabase();
		Cursor cursor;
		cursor = database.rawQuery(("select * from name"), null);
		cursor.moveToFirst();
		String Ric = new String();
		Ric=cursor.getString(cursor.getColumnIndex("name"));
		name.setText(Ric);
		database.close();
		
		submitName.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				
				database=myDbHelper.getReadableDatabase();
				String clicked = name.getText().toString();		
				Cursor cursor;
				cursor = database.rawQuery(("select * from name"), null);
				cursor.moveToFirst();
				String Ric = new String();
				Ric=cursor.getString(cursor.getColumnIndex("name"));
				database.delete("name", "name = '"+Ric+"'", null);
				ContentValues values = new ContentValues();
				values.put("name",clicked );
				database.insert("name", null, values);
				cursor.close();
				database.close();
				
			}
			
			
			
			
		});
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.name, menu);
		return true;
	}

}
