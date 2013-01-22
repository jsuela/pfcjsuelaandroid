package com.jsuelapfc.preguntaras;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class Tips extends ListActivity{	
	
	// url to make request
	private static String url;
	 
	// JSON Node names
	private static final String TAG_TIPS = "tips";
	private static final String TAG_PK = "pk";
	private static final String TAG_MODEL = "model";

	private static final String TAG_FIELDS = "fields";
	private static final String TAG_FIELDS_LECCION = "leccion";


	// contacts JSONArray
	JSONArray tips = null;
	
	   @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.questions);

	        // Hashmap for ListView
	        ArrayList<HashMap<String, String>> tipsList = new ArrayList<HashMap<String, String>>();
	 
	        // Creating JSON Parser instance
	        JSONParser jParser = new JSONParser();
	 
	        // getting JSON string from URL
	        url = "http://10.0.2.2:1234/android/tips";	
	        
	        JSONObject json = jParser.getJSONFromUrl(url);

	        try {
	            // Getting Array of Contacts
	            tips = json.getJSONArray(TAG_TIPS);
	 
	            // looping through All Contacts
	            for(int i = 0; i < tips.length(); i++){
	                JSONObject c = tips.getJSONObject(i);
	 
	                // Storing each json item in variable
	                String pk = c.getString(TAG_PK);
	                String model = c.getString(TAG_MODEL);
	         
	                // Respuestas is agin JSON Object
	                JSONObject fields = c.getJSONObject(TAG_FIELDS);
	                String leccion = fields.getString(TAG_FIELDS_LECCION);  
 	    
	  	                
	                
	                // creating new HashMap
	                HashMap<String, String> map = new HashMap<String, String>();
	 
	                // adding each child node to HashMap key => value
	                map.put(TAG_PK, pk);
	                map.put(TAG_MODEL, model);
	                map.put(TAG_FIELDS_LECCION, leccion);

	            	 
	                // adding HashList to ArrayList
	               tipsList.add(map); 
	            } 
	        } catch (Exception e) {
	        	Toast.makeText(Tips.this,
         				"Error al contactar con el servidor, inténtelo más tarde",
         				Toast.LENGTH_SHORT).show();
	            e.printStackTrace();
	        }	
	          
	        // Updating parsed JSON data into ListView
	        //Listado con respuestas incluidas:
	        ListAdapter adapter = new SimpleAdapter(this, tipsList,
	        		R.layout.list_tips_item,
	        		new String[] { TAG_FIELDS_LECCION}, new int[] {
                    R.id.tips});
	        
	        setListAdapter(adapter);
		        
	    }

}