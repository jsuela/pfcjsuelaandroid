package com.jsuelapfc.preguntaras;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class Ranking extends ListActivity{	
	
	// url to make request
	private static String url;
	 
	// JSON Node names
	private static final String TAG_PUNTOS = "puntos";
	private static final String TAG_PK = "pk";
	private static final String TAG_MODEL = "model";

	private static final String TAG_FIELDS = "fields";
	private static final String TAG_FIELDS_PUNTOS = "puntos";
	private static final String TAG_FIELDS_USUARIO = "usuario";

	//servicio
	private MiServicioPreguntas s;
	private ArrayList<String> values;
	private ArrayAdapter<String> adapter;



	// contacts JSONArray
	JSONArray puntos = null;
	
	   @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.questions);
	        //Llamamos al servicio
			doBindService();
	 
	        // Hashmap for ListView
	        ArrayList<HashMap<String, String>> puntosList = new ArrayList<HashMap<String, String>>();
	 
	        // Creating JSON Parser instance
	        JSONParser jParser = new JSONParser();
	 
	        // getting JSON string from URL
	        url = "http://10.0.2.2:1234/android/clasificacion";	
	        
	        JSONObject json = jParser.getJSONFromUrl(url);

	        try {
	            // Getting Array of Contacts
	            puntos = json.getJSONArray(TAG_PUNTOS);
	 
	            // looping through All Contacts
	            for(int i = 0; i < puntos.length(); i++){
	                JSONObject c = puntos.getJSONObject(i);
	 
	                // Storing each json item in variable
	                String pk = c.getString(TAG_PK);
	                String model = c.getString(TAG_MODEL);
	         
	                // Respuestas is agin JSON Object
	                JSONObject fields = c.getJSONObject(TAG_FIELDS);
	                String puntos = fields.getString(TAG_FIELDS_PUNTOS);  
	                String usuario = fields.getString(TAG_FIELDS_USUARIO);	    	    
	  	                
	                
	                // creating new HashMap
	                HashMap<String, String> map = new HashMap<String, String>();
	 
	                // adding each child node to HashMap key => value
	                map.put(TAG_PK, pk);
	                map.put(TAG_MODEL, model);
	                map.put(TAG_FIELDS_PUNTOS, "Puntos: "+puntos);
	                map.put(TAG_FIELDS_USUARIO, usuario);
	            	 
	                // adding HashList to ArrayList
	               puntosList.add(map); 
	            } 
	        } catch (Exception e) {
	        	Toast.makeText(Ranking.this,
         				"Error al contactar con el servidor, inténtelo más tarde",
         				Toast.LENGTH_SHORT).show();
	            e.printStackTrace();
	        }	
	          
	        // Updating parsed JSON data into ListView
	        //Listado con respuestas incluidas:
	        ListAdapter adapter = new SimpleAdapter(this, puntosList,
	        		R.layout.list_puntos_item,
	        		new String[] { TAG_FIELDS_PUNTOS, TAG_FIELDS_USUARIO}, new int[] {
                    R.id.puntos, R.id.usuario});
	        
	        setListAdapter(adapter);
	

	 
	        
	    }
		void doBindService() {
			Log.i("Service JAVI", "entro en doBindService");
			getApplicationContext().bindService(new Intent(this, MiServicioPreguntas.class), mConnection,
					Context.BIND_AUTO_CREATE);
			Log.i("Service JAVI", "saldre? en doBindService");
		}
		
		private ServiceConnection mConnection = new ServiceConnection() {

			public void onServiceConnected(ComponentName className, IBinder binder) {
				Log.i("Service JAVI", "entro en Service connected");
				s = ((MiServicioPreguntas.MyBinder) binder).getService();
				Toast.makeText(Ranking.this, "Arrancando servicio",
						Toast.LENGTH_SHORT).show();
			}

			public void onServiceDisconnected(ComponentName className) {
				Toast.makeText(Ranking.this, "Parando servicio",
						Toast.LENGTH_SHORT).show();
				s = null;
			}
		};
		
		public void showServiceData(View view) {
			if (s != null) {
				List<String> wordList = s.getWordList();
				values.clear();
				values.addAll(wordList);
				adapter.notifyDataSetChanged();
			}
		}
}