package com.jsuelapfc.preguntaras;


import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
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
    
	private JSONParser jParser;
	private ArrayList<HashMap<String, String>> preguntasList;

	// contacts JSONArray
	JSONArray preguntas = null;
	
    private Button btnDisplay;
    private String resultado;
    
	private ProgressDialog pd;
	
	private String mensaje;
	private final Handler handler = new Handler();
    private Context mContext;
    
    private Button lblEnvResp;

	
	   @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.questionslist);

	        mContext = this;

	        // Hashmap for ListView
	        preguntasList = new ArrayList<HashMap<String, String>>();
	 
	        // Creating JSON Parser instance
	        jParser = new JSONParser();
	 
	        // getting JSON string from URL
	        prefs = PreferenceManager.getDefaultSharedPreferences(this);
	        loginusuario = prefs.getString("username", "n/a");
	        url = "http://10.0.2.2:1234/android/pidepreguntas/"+loginusuario;	
	        
	    	pd = ProgressDialog.show(QuestionsList.this, "Preguntas", "Cargando...", true, false);	

		    new MiTarea().execute(url);
	   }
	   
	   private class MiTarea extends AsyncTask<String, ListAdapter, ArrayList<HashMap<String, String>> >{

	          protected ArrayList<HashMap<String, String>> doInBackground(String... urls) {

	        	  try {
				        JSONObject json = jParser.getJSONFromUrl(url);
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
    		        	mensaje = "Error, no se puede contactar con el servidor";
    		            handler.post(toast);
			            e.printStackTrace();
			        }	
			        
			


	        return preguntasList;
	        
	        
	        
	          }
	        protected void onPostExecute(ArrayList<HashMap<String, String>> result ) {

	        //Damos nombre al botón
	        //el siguiente método se ejecutará cuando se presione el botón
	        lblEnvResp = (Button) findViewById(R.id.pregunta_extra);
	        lblEnvResp.setText("Pregunta extra");
	        
	        addListenerOnButton();
	        
	        // Updating parsed JSON data into ListView
	        //Listado con respuestas incluidas:
	        ListAdapter adapter = new SimpleAdapter(QuestionsList.this, preguntasList,
	        		R.layout.list_preguntas_item,
	        		new String[] { TAG_FIELDS_PREGUNTA, TAG_FIELDS_RESPUESTA, TAG_FIELDS_RESPUESTA2, TAG_FIELDS_RESPUESTA3}, new int[] {
                    R.id.pregunta, R.id.respuesta, R.id.respuesta2, R.id.respuesta3});
	        
	        setListAdapter(adapter);
	       	pd.dismiss();
	
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
	             
	           
		 public void addListenerOnButton() {
			 
				btnDisplay = (Button) findViewById(R.id.pregunta_extra);
			 
				btnDisplay.setOnClickListener(new OnClickListener() {
			 
					@Override
					public void onClick(View v) {
						pidePreguntaExtra();
		         	};
			 
				});
			 
			  }
		 
			public void pidePreguntaExtra(){
				
				//primero pido pregunta al servidor, luego lanzo notificacion
		        // getting new question
		        prefs = PreferenceManager.getDefaultSharedPreferences(QuestionsList.this);
		        loginusuario = prefs.getString("username", "n/a");
		        url = "http://10.0.2.2:1234/android/preguntaextra/"+loginusuario;
		        
	            final ProgressDialog pd1 = ProgressDialog.show(QuestionsList.this, "Preguntas", "Cargando...", true, false);

	    		new Thread(new Runnable(){
	    			@Override
	        		public void run(){
		        
						try {
							HttpClient client = new DefaultHttpClient();
							String getURL = url;
							HttpGet get = new HttpGet(getURL);
							HttpResponse responseGet = client.execute(get);
							HttpEntity resEntityGet = responseGet.getEntity();
							if (resEntityGet != null) {
								resultado = EntityUtils.toString(resEntityGet);
								if (resultado.equals("ok")){
							        //getting notification
									finish();
									Intent in = new Intent(getApplicationContext(), MainActivity.class);
		        					startActivity(in);
		        		        	mensaje = "Pregunta añadida";
		        		            handler.post(toast);
		
									
								}else{
		        		        	mensaje = "No hay más preguntas disponibles (de momento...)";
		        		            handler.post(toast);
		
								}
							} else {
		
	        		        	mensaje = "Error al contactar con el servidor";
	        		            handler.post(toast);		
							}
						} catch (Exception e) {
							Log.i("ERROR", "CONECTION PROBLEM");
        		        	mensaje = "No se puede contactar con el servidor";
        		            handler.post(toast);
		
						}
					    pd1.dismiss();
	    			}
	     		}).start();
		        

			}
         	final Runnable toast = new Runnable(){
         		public void run(){
             		Toast.makeText(mContext,
             				mensaje,
             				Toast.LENGTH_SHORT).show();

         		}
         	};

	     }
	   

	  