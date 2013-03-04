package com.jsuelapfc.preguntaras;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
	
	private JSONParser jParser;
	private ArrayList<HashMap<String, String>> tipsList;

	// contacts JSONArray
	private JSONArray tips = null;

	private ProgressDialog pd;
	
	private String mensaje;
	private final Handler handler = new Handler();
    private Context mContext;


	   @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.questions);

	        mContext = this;

	        // Hashmap for ListView
	        tipsList = new ArrayList<HashMap<String, String>>();
	 
	        // Creating JSON Parser instance
	        jParser = new JSONParser();
	 
	        // getting JSON string from URL
	        url = "http://pfc-jsuelaplaza.libresoft.es/android/tips";	

	      //Creamos una nueva instancia y llamamos al método ejecutar
	      //pasándole el string.
	        
	        
	    	pd = ProgressDialog.show(Tips.this, "Preguntas", "Cargando...", true, false);	

	      new MiTarea().execute(url);
        
	    }

	     private class MiTarea extends AsyncTask<String, ListAdapter, ArrayList<HashMap<String, String>> >{

	          protected ArrayList<HashMap<String, String>> doInBackground(String... urls) {
			        try {

			        	
				        JSONObject json = jParser.getJSONFromUrl(url);
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
    		        	mensaje = "Ha ocurrido un error...no se puede contactar con el servidor";
    		            handler.post(toast);
			            e.printStackTrace();

			        }	

	         
			        // Updating parsed JSON data into ListView
		            Log.i("TIPs", Integer.toString(Thread.activeCount()));
			        Log.i("TIPs sixe2", Integer.toString(tipsList.size()));
					return tipsList;

	          }

	          protected void onPostExecute(ArrayList<HashMap<String, String>> result ) {

	        	  ListAdapter adapter = new SimpleAdapter(Tips.this, result,
	  	        		R.layout.list_tips_item,
	  	        		new String[] { TAG_FIELDS_LECCION}, new int[] {
	                      R.id.tips});
	  	        
	  	        setListAdapter(adapter);
	  	        
	        	 pd.dismiss();
	          }
	    }
      	final Runnable toast = new Runnable(){
     		public void run(){
         		Toast.makeText(mContext,
         				mensaje,
         				Toast.LENGTH_SHORT).show();

     		}
     	};

}