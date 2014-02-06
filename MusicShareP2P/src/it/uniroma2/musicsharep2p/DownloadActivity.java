package it.uniroma2.musicsharep2p;



import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class DownloadActivity extends Activity {

	private ListView list;
	private Button start;
	private TextView query;
	private SQLiteDatabase database;
	private DataBaseHelper myDbHelper;
	String toreturn=new String();
	ArrayList<String> listDataHeader;




	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_download);


		//inizializzazion
		list = (ListView) findViewById(R.id.listQuery);
		start = (Button) findViewById(R.id.startQuery);
		query = (TextView) findViewById(R.id.queryText);



		myDbHelper = new DataBaseHelper(this);


		try {myDbHelper.createDataBase();} 
		catch (IOException ioe) 
		{throw new Error("Unable to create database");}

		try {myDbHelper.openDataBase();}
		catch(SQLException sqle){throw sqle;}

		listDataHeader = getQuery();


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

		list.setAdapter(adapter);

		OnItemClickListener l = new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				final String clicked = (String) arg0.getAdapter().getItem(arg2);

				AlertDialog.Builder alert = new AlertDialog.Builder(DownloadActivity.this);

				alert.setTitle("Remove "+clicked+" from active query?");
				alert.setMessage("Click Ok to remove.");
				alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						database= myDbHelper.getReadableDatabase();
						database.delete("activequery", "keyword = '"+clicked+"'", null);
						
						
						listDataHeader = getQuery();


						ArrayAdapter<String> adapter=new ArrayAdapter<String>( DownloadActivity.this,android.R.layout.simple_list_item_1, listDataHeader){

							@Override
							public View getView(int position, View convertView,
									ViewGroup parent) {
								View view =super.getView(position, convertView, parent);

								TextView textView=(TextView) view.findViewById(android.R.id.text1);

								textView.setTextColor(Color.WHITE);

								return view;
							}
						};

						list.setAdapter(adapter);

						
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


		};


		list.setOnItemClickListener(l);

		OnClickListener buttonlistener = new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(v.getId()==R.id.startQuery)
				{

					String newQuery= query.getText().toString();
					database=myDbHelper.getWritableDatabase();
					ContentValues values = new ContentValues();
					values.put("keyword",newQuery );
					database.insert("activequery", null, values);
					listDataHeader = getQuery();


					ArrayAdapter<String> adapter=new ArrayAdapter<String>( DownloadActivity.this,android.R.layout.simple_list_item_1, listDataHeader){

						@Override
						public View getView(int position, View convertView,
								ViewGroup parent) {
							View view =super.getView(position, convertView, parent);

							TextView textView=(TextView) view.findViewById(android.R.id.text1);

							textView.setTextColor(Color.WHITE);

							return view;
						}
					};

					list.setAdapter(adapter);

					query.setText("");

					
					database.close();


				}

			}




		};

		start.setOnClickListener(buttonlistener);		

	}


	public ArrayList<String>  getQuery() {
		ArrayList<String> bfs =  new ArrayList<String>();
		database=myDbHelper.getWritableDatabase();
		Cursor cursor = database.rawQuery(("select * from activequery"), null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			String bf = cursorToString(cursor);
			bfs.add(bf);
			cursor.moveToNext();
		}
		cursor.close();
		return bfs;

	}


	public static String cursorToString(Cursor cursor) {
		// TODO Auto-generated method stub
		return cursor.getString(cursor.getColumnIndex("keyword"));
	} 


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.download, menu);
		menu.clear();
		return true;
	}

}
