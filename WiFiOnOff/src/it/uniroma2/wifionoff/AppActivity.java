package it.uniroma2.wifionoff;




import java.util.*;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import android.widget.ArrayAdapter;
import android.widget.TextView;

import android.graphics.Color;





public class AppActivity extends ListActivity  {

	

    String toreturn=new String();
    ArrayList<String> listDataHeader;
    
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.activity_my_list);
   
    listDataHeader = new ArrayList<String>();

    for(int i =0;i<OnOffService.ConnectedApp.size();i++)
    	listDataHeader.add(OnOffService.ConnectedApp.get(i).getName());
    	
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
  

	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.setting, menu);
		menu.clear();
		return true;
	}
  



}
