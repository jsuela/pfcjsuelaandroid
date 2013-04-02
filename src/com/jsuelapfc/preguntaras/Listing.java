package com.jsuelapfc.preguntaras;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
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
    
	private JSONParserPOST jParser;
    static InputStream is = null;
	private ArrayList<HashMap<String, Object>> preguntasList;
	
	private ProgressDialog pd;
	
	private String mensaje;
	private final Handler handler = new Handler();
    private Context mContext;

	// contacts JSONArray
	private JSONArray preguntas = null;
	
	private String asignatura;
	
	   @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	 	    //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

	        setContentView(R.layout.questions);
	        
	        mContext = this;
	 
	        // Hashmap for ListView
	        preguntasList = new ArrayList<HashMap<String, Object>>();
	 
	        // Creating JSON Parser instance
	        jParser = new JSONParserPOST();
	 
	        // getting JSON string from URL
	        prefs = PreferenceManager.getDefaultSharedPreferences(this);
	        loginusuario = prefs.getString("username", "n/a");
	        url = "http://pfc-jsuelaplaza.libresoft.es/android/listacorrectas/"+loginusuario;	
	        
	    	pd = ProgressDialog.show(Listing.this, "Preguntas", "Cargando...", true, false);	

		    new MiTarea().execute(url);
	        
	    }
	   
	     private class MiTarea extends AsyncTask<String, ListAdapter, ArrayList<HashMap<String, Object>> >{

	          protected ArrayList<HashMap<String, Object>> doInBackground(String... urls) {
	        	  try {
	        		  
               	    String csrf = null;
            		DefaultHttpClient httpclient = new DefaultHttpClient();
						 

     			    HttpGet httpget = new HttpGet(url);
     			    HttpResponse response = httpclient.execute(httpget);
         	
     			    Header[] headers = response.getAllHeaders();
     			    	
  			        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);	        		  
	        		  
 			    	for (int i = 0; i < headers.length; i++){	
			    			System.out.println("cabeceraaaa:"+response.getParams().toString());
 			    		if (headers[i].toString().contains("csrftoken")){

 			    			csrf=headers[i].toString();
 			    			csrf = csrf.replace("Set-Cookie:","");
 			    			csrf = csrf.replace(" ","");
 			    			csrf = csrf.replace(";expires","");
 			    			System.out.println("el csrf111111nuevo es:"+ csrf.split("=")[1]);

 			    			

 			    			//System.out.println("CSSSSRF:"+ csrf.split("=")[1]);
 			    			//obtengo el nombre de la asignatura
 			    			prefs = PreferenceManager.getDefaultSharedPreferences(Listing.this);
 			    			asignatura = prefs.getString("subject", "n/a");
 			    			
 			    			nameValuePairs.add(new BasicNameValuePair("asignatura", asignatura));
         			    	nameValuePairs.add(new BasicNameValuePair("csrfmiddlewaretoken", csrf.split("=")[1]));

 			    		}
 			    	} 	        		  
  					HttpPost httppost = new HttpPost(url);
 			    	//nameValuePairs.add(new BasicNameValuePair("csrfmiddlewaretoken", csrf.split("=")[1]));

			        
			        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
			        
			        response = httpclient.execute(httppost);
			        HttpEntity resEntityGet = response.getEntity();
			        is = resEntityGet.getContent();	        		  			
						
				    try{  
  
					    JSONObject json = jParser.getJSONFromResponse(is);
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
        				mensaje = "Error contactando con el servidor. Inténtelo más tarde";
           			 
        				handler.post(toast);
			            e.printStackTrace();
			        }	
					} catch (Exception e) {
						Log.i("ERROR", "CONECTION PROBLEM");
    		        	mensaje = "Imposible contactar con el servidor";
    		            handler.post(toast);
	
					}
				    pd.dismiss();
	        	  return preguntasList;


	          }

	          protected void onPostExecute(ArrayList<HashMap<String, Object>> result ) {

	  	        ListAdapter adapter = new SimpleAdapter(Listing.this, preguntasList,
		        		R.layout.list_preguntas_dadas_item,
		        		new String[] { TAG_FIELDS_PREGUNTA, TAG_FIELDS_RESPUESTA, TAG_FIELDS_RESPUESTA_DADA, TAG_FIELDS_ICONO}, new int[] {
	                    R.id.pregunta, R.id.respuesta, R.id.respuesta_dada, R.id.imagencita});
		        
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