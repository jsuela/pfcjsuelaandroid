package com.jsuelapfc.preguntaras;


import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class QuestionsList extends ListActivity{	
	
	// url to make request
	private static String url;
	 
	// JSON Node names
	private static final String TAG_PREGUNTAS = "preguntas";
	private static final String TAG_PK = "pk";
	private static final String TAG_MODEL = "model";

	private static final String TAG_FIELDS = "fields";
	private static final String TAG_FIELDS_PREGUNTA = "pregunta";
	private static final String TAG_FIELDS_USUARIO_PENDIENTE = "usuario_pendiente";
	private static final String TAG_FIELDS_RESPUESTA2 = "respuesta2";
	private static final String TAG_FIELDS_RESPUESTA3 = "respuesta3";
	private static final String TAG_FIELDS_RESPUESTA = "respuesta";
	
	private String loginusuario;
    private SharedPreferences prefs;


	// contacts JSONArray
	JSONArray preguntas = null;
	
	   @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.questions);
	 
	        // Hashmap for ListView
	        ArrayList<HashMap<String, String>> preguntasList = new ArrayList<HashMap<String, String>>();
	 
	        // Creating JSON Parser instance
	        JSONParser jParser = new JSONParser();
	 
	        // getting JSON string from URL
	        prefs = PreferenceManager.getDefaultSharedPreferences(this);
	        loginusuario = prefs.getString("username", "n/a");
	        url = "http://10.0.2.2:1234/android/pidepreguntas/"+loginusuario;	
	        
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
	                String respuesta2 = fields.getString(TAG_FIELDS_RESPUESTA2);	    	    
	    	        String respuesta3 = fields.getString(TAG_FIELDS_RESPUESTA3);
	                String respuesta = fields.getString(TAG_FIELDS_RESPUESTA);  

	                String usuario_pendiente = fields.getString(TAG_FIELDS_USUARIO_PENDIENTE);	
	                
	                // creating new HashMap
	                HashMap<String, String> map = new HashMap<String, String>();
	 
	                // adding each child node to HashMap key => value
	                map.put(TAG_PK, pk);
	                map.put(TAG_MODEL, model);
	                map.put(TAG_FIELDS_PREGUNTA, pregunta);
	                map.put(TAG_FIELDS_RESPUESTA2, respuesta2);
	                map.put(TAG_FIELDS_RESPUESTA3, respuesta3);
	                map.put(TAG_FIELDS_RESPUESTA, respuesta);
	                map.put(TAG_FIELDS_USUARIO_PENDIENTE, usuario_pendiente);
	 
	                // adding HashList to ArrayList
	               preguntasList.add(map);
	            }
	        } catch (Exception e) {
	        	Toast.makeText(QuestionsList.this,
         				"Error al contactar con el servidor, inténtelo más tarde",
         				Toast.LENGTH_SHORT).show();
	            e.printStackTrace();
	        }	
	        
	        // Updating parsed JSON data into ListView
	        //Listado con respuestas incluidas:
	        ListAdapter adapter = new SimpleAdapter(this, preguntasList,
	        		R.layout.list_preguntas_item,
	        		new String[] { TAG_FIELDS_PREGUNTA, TAG_FIELDS_RESPUESTA, TAG_FIELDS_RESPUESTA2, TAG_FIELDS_RESPUESTA3}, new int[] {
                    R.id.pregunta, R.id.respuesta, R.id.respuesta2, R.id.respuesta3});
	        
	        setListAdapter(adapter);
	
	        // selecting single ListView item
	        ListView lv = getListView();
	 
	        // Launching new screen on Selecting Single ListItem
	        lv.setOnItemClickListener(new OnItemClickListener() {
	        	
	            @Override
	            public void onItemClick(AdapterView<?> parent, View view,
	                    int position, long id) {
	                    //getting values from selected ListItem

	                    String name= ((TextView) view.findViewById(R.id.pregunta)).getText().toString();
	                    String resp2 = ((TextView) view.findViewById(R.id.respuesta2)).getText().toString();
	                    String resp3 = ((TextView) view.findViewById(R.id.respuesta3)).getText().toString();
	                    String resp = ((TextView) view.findViewById(R.id.respuesta)).getText().toString();
	     
	                    //Starting new intent
	                    Intent in = new Intent(getApplicationContext(), SingleQuestion.class);
	                    in.putExtra(TAG_FIELDS_PREGUNTA, name);
	                    in.putExtra(TAG_FIELDS_RESPUESTA, resp);
	                    in.putExtra(TAG_FIELDS_RESPUESTA2, resp2);
	                    in.putExtra(TAG_FIELDS_RESPUESTA3, resp3);
	                    startActivity(in);
	                    finish();
	            }
	        });
	    }
}