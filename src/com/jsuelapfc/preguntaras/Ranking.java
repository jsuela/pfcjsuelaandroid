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
	/*private MiServicioPreguntas s;
	private ArrayList<String> values;
	private ArrayAdapter<String> adapter;*/
	private JSONParser jParser;
	private ArrayList<HashMap<String, String>> puntosList;

	// contacts JSONArray
	private JSONArray puntos = null;
	
	private ProgressDialog pd;
	
	private String mensaje;
	private final Handler handler = new Handler();
    private Context mContext;
	
	
	   @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.questions);
	        mContext = this;

	        //Llamamos al servicio
			//doBindService();
	 
	        // Hashmap for ListView
	        puntosList = new ArrayList<HashMap<String, String>>();
	 
	        // Creating JSON Parser instance
	        jParser = new JSONParser();
	 
	        // getting JSON string from URL
	        url = "http://10.0.2.2:1234/android/clasificacion";	
	        
	    	pd = ProgressDialog.show(Ranking.this, "Preguntas", "Cargando...", true, false);	

		    new MiTarea().execute(url);
	   }
	   
	     private class MiTarea extends AsyncTask<String, ListAdapter, ArrayList<HashMap<String, String>> >{


	          protected ArrayList<HashMap<String, String>> doInBackground(String... urls) {

			        try {
				        JSONObject json = jParser.getJSONFromUrl(url);
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
        				mensaje = "No se puede conectar con el servidor. Inténtelo más tarde";
        			 
        				handler.post(toast);
			            e.printStackTrace();
			        }	
					return puntosList;
	          }

          protected void onPostExecute(ArrayList<HashMap<String, String>> result ) {
  	        // Updating parsed JSON data into ListView
	        ListAdapter adapter = new SimpleAdapter(Ranking.this, puntosList,
	        		R.layout.list_puntos_item,
	        		new String[] { TAG_FIELDS_PUNTOS, TAG_FIELDS_USUARIO}, new int[] {
                    R.id.puntos, R.id.usuario});
	        
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
   
	/*
	 * 
	 *    
	 *    
	 *    
	 *    
	 *    
	 *    
	 *    SERVICIO
	 */
		/*void doBindService() {
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
		}*/
