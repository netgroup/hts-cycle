package it.uniroma2.wifionoff;



import java.io.IOException;

import java.util.*;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import android.widget.ArrayAdapter;
import android.widget.EditText;

import android.widget.ListView;
import android.widget.TextView;
import android.content.ContentValues;

import android.database.Cursor;
import android.database.SQLException;

import android.util.Log;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;





public class MyListActivity extends ListActivity  {

	private SQLiteDatabase database;
	private DataBaseHelper myDbHelper;
    String toreturn=new String();
    ArrayList<String> listDataHeader;
    
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.activity_my_list);
    myDbHelper = new DataBaseHelper(this);
    
     
    try {myDbHelper.createDataBase();} 
    	catch (IOException ioe) 
    	{throw new Error("Unable to create database");}
     
    try {myDbHelper.openDataBase();}
    	catch(SQLException sqle){throw sqle;}
    
    listDataHeader = new ArrayList<String>();
     listDataHeader.add("30");
    listDataHeader.add("60");
    listDataHeader.add("120");
    listDataHeader.add("240");
    listDataHeader.add("480");

    ArrayAdapter<String> adapter=new ArrayAdapter<String>( this,android.R.layout.simple_list_item_1, listDataHeader){

        @Override
        public View getView(int position, View convertView,
                ViewGroup parent) {
            View view =super.getView(position, convertView, parent);

            TextView textView=(TextView) view.findViewById(android.R.id.text1);

            textView.setTextColor(Color.WHITE);

            return view;
        }
    };

       setListAdapter(adapter);
    

	
  }
  
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);	
		database=myDbHelper.getReadableDatabase();
		String clicked = (String) l.getAdapter().getItem(position);		
		Cursor cursor;
		cursor = database.rawQuery(("select * from times"), null);
		cursor.moveToFirst();
		String Ric = new String();
		Ric=cursor.getString(cursor.getColumnIndex("totaltime"));
		String temp = cursor.getString(cursor.getColumnIndex("timeon"));
		Log.w("List","Erasing "+Ric+" and "+temp);
		database.delete("times", "totaltime = '"+Ric+"'", null);
		ContentValues values = new ContentValues();
		values.put("totaltime",clicked );
		values.put("timeon", temp);
		Log.w("List","Inserted "+clicked+" and "+temp);
		database.insert("times", null, values);
		cursor.close();
		database.close();
		finish();
		
		
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.setting, menu);
		menu.clear();
		return true;
	}
  



}
