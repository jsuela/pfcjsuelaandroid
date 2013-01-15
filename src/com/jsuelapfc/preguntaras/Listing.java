package com.jsuelapfc.preguntaras;




import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class Listing extends ListActivity{	
	
	// url to make request
	private static String url;
	 
	// JSON Node names
	private static final String TAG_PREGUNTAS = "preguntas";
	private static final String TAG_PK = "pk";
	private static final String TAG_MODEL = "model";

	private static final String TAG_FIELDS = "fields";
	private static final String TAG_FIELDS_PREGUNTA = "pregunta";
	private static final String TAG_FIELDS_RESPUESTA = "respuesta";
	private static final String TAG_FIELDS_RESPUESTA_DADA = "respuesta_dada";
	private static final String TAG_FIELDS_RESPUESTA_USUARIO_CORRECTA = "respuesta_usuario_correcta";
	private final static String TAG_FIELDS_ICONO="icono";
	
	
	private String loginusuario;
    private SharedPreferences prefs;


	// contacts JSONArray
	JSONArray preguntas = null;
	
	   @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.questions);
	 
	        // Hashmap for ListView
	        ArrayList<HashMap<String, Object>> preguntasList = new ArrayList<HashMap<String, Object>>();
	 
	        // Creating JSON Parser instance
	        JSONParser jParser = new JSONParser();
	 
	        // getting JSON string from URL
	        prefs = PreferenceManager.getDefaultSharedPreferences(this);
	        loginusuario = prefs.getString("username", "n/a");
	        url = "http://10.0.2.2:1234/android/listacorrectas/"+loginusuario;	
	        
	        JSONObject json = jParser.getJSONFromUrl(url);

	        try {
	            // Getting Array of Contacts
	            preguntas = json.getJSONArray(TAG_PREGUNTAS);
	 
	            // looping through All Contacts
	            for(int i = 0; i < preguntas.length(); i++){
	                JSONObject c = preguntas.getJSONObject(i);
	 
	                // Storing each json item in variable
	                String pk = c.getString(TAG_PK);
	                String model = c.getString(TAG_MODEL);
	         
	                // Respuestas is agin JSON Object
	                JSONObject fields = c.getJSONObject(TAG_FIELDS);
	                String pregunta = fields.getString(TAG_FIELDS_PREGUNTA);  
	                String respuesta = fields.getString(TAG_FIELDS_RESPUESTA);	    	    
	    	        String respuesta_dada = fields.getString(TAG_FIELDS_RESPUESTA_DADA);
	                String respuesta_usuario_correcta = fields.getString(TAG_FIELDS_RESPUESTA_USUARIO_CORRECTA);  

	                
	                
	                // creating new HashMap
	                HashMap<String, Object> map = new HashMap<String, Object>();
	 
	                // adding each child node to HashMap key => value
	                map.put(TAG_PK, pk);
	                map.put(TAG_MODEL, model);
	                map.put(TAG_FIELDS_PREGUNTA, "Pregunta: "+pregunta);
	                map.put(TAG_FIELDS_RESPUESTA_DADA, "Respuesta dada: "+respuesta_dada);
	                if (respuesta_usuario_correcta.equals("true")){
		                map.put(TAG_FIELDS_ICONO, R.drawable.tick);
	                    
	                }else{
	                	map.put(TAG_FIELDS_ICONO, R.drawable.cross);
	                }
	                map.put(TAG_FIELDS_RESPUESTA, "Respuesta correcta: "+respuesta);
	                


	 
	                // adding HashList to ArrayList
	               preguntasList.add(map); 
	            } 
	        } catch (Exception e) {
	        	Toast.makeText(Listing.this,
         				"Error al contactar con el servidor, inténtelo más tarde",
         				Toast.LENGTH_SHORT).show();
	            e.printStackTrace();
	        }	
	          
	        // Updating parsed JSON data into ListView
	        //Listado con respuestas incluidas:
	        ListAdapter adapter = new SimpleAdapter(this, preguntasList,
	        		R.layout.list_preguntas_dadas_item,
	        		new String[] { TAG_FIELDS_PREGUNTA, TAG_FIELDS_RESPUESTA, TAG_FIELDS_RESPUESTA_DADA, TAG_FIELDS_ICONO}, new int[] {
                    R.id.pregunta, R.id.respuesta, R.id.respuesta_dada, R.id.imagencita});
	        
	        setListAdapter(adapter);
	

	 
	        
	    }
}