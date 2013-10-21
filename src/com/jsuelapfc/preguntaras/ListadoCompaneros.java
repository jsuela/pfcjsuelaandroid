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
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ListadoCompaneros extends ListActivity {
	
	// url to make request
	private static String url;
	private static String url2;
	 
	// JSON Node names
	private static final String TAG_PUNTOS = "puntos";
	private static final String TAG_PK = "pk";
	private static final String TAG_MODEL = "model";

	private static final String TAG_FIELDS = "fields";
	private static final String TAG_FIELDS_PUNTOS = "puntos";
	private static final String TAG_FIELDS_USUARIO = "usuario";

	private JSONParserPOST jParser;
    static InputStream is = null;
	
	//private ArrayList<HashMap<String, String>> puntosList;
    private String []puntosList;
    

	// contacts JSONArray
	private JSONArray puntos = null;
	
	private ProgressDialog pd;
	
	private String mensaje;
	private final Handler handler = new Handler();
    private Button lblEnvResp;
    private Button btnDisplay;
    
    private ListAdapter adapter2;
    
    private ListView lv;
    
	private String loginusuario;
    private SharedPreferences prefs;
    private String resultado;
    
	private int nPreguntasEnviadasAmigos;
	private Editor edit;
	private int limitePreguntasEnviadasAmigos = 27;
	
	private String asignatura;


	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 	    requestWindowFeature(Window.FEATURE_NO_TITLE);
 	    //para que no rote setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.ranking2);
        
        lv = (ListView)findViewById(android.R.id.list);
        
        // Hashmap for ListView
        //puntosList = new ArrayList<HashMap<String, String>>();
 
        // Creating JSON Parser instance
        jParser = new JSONParserPOST();
 
        // getting JSON string from URL
        prefs = PreferenceManager.getDefaultSharedPreferences(ListadoCompaneros.this);
        loginusuario = prefs.getString("username", "n/a");
        url = "http://pfc-jsuelaplaza.libresoft.es/android/clasificacion/"+loginusuario;
        
    	pd = ProgressDialog.show(ListadoCompaneros.this, "Preguntas", "Cargando...", true, false);	

	    new MiTarea().execute(url);
        
    }
	
	
    private class MiTarea extends AsyncTask<String, ListAdapter, String[] >{

        protected String[] doInBackground(String... urls) {
	        try {
	        	
		        prefs = PreferenceManager.getDefaultSharedPreferences(ListadoCompaneros.this);
		        loginusuario = prefs.getString("username", "n/a");

         	    String csrf = null;
         	    
	        	HttpParams params = new BasicHttpParams();
	        	HttpProtocolParams.setContentCharset(params, "utf-8");
      			DefaultHttpClient httpclient = new DefaultHttpClient(params);
				 

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
			    	        prefs = PreferenceManager.getDefaultSharedPreferences(ListadoCompaneros.this);
			    	        asignatura = prefs.getString("subject", "n/a");
			    			
            			nameValuePairs.add(new BasicNameValuePair("asignatura", asignatura));
       			    	nameValuePairs.add(new BasicNameValuePair("csrfmiddlewaretoken", csrf.split("=")[1]));

			    		}
			    	} 
       			    	
				HttpPost httppost = new HttpPost(url);

		        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
		        
		        response = httpclient.execute(httppost);
		        HttpEntity resEntityGet = response.getEntity();
		        is = resEntityGet.getContent();

    			
				if (resEntityGet != null) {
					
					
			        try{	
				        JSONObject json = jParser.getJSONFromResponse(is);
			            // Getting Array of Contacts
			            puntos = json.getJSONArray(TAG_PUNTOS);
			 
			            
			            puntosList = new String[puntos.length()];
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
			                map.put(TAG_FIELDS_USUARIO, usuario);
			                
				            puntosList[i]=usuario;
				            Log.i("ListadoCOmpaneros", "añadiendoa..... "+usuario);

			            } 
			        } catch (Exception e) {
        				mensaje = "No se puede conectar con el servidor. Inténtelo más tarde";
        			 
        				handler.post(toast);
			            e.printStackTrace();
			        }
		
					
				} else {

		        	mensaje = "Error al contactar con el servidor";
		            handler.post(toast);		
				}

				} catch (Exception e) {
					Log.i("ERROR", "CONECTION PROBLEM");
		        	mensaje = "Imposible contactar con el servidor";
		            handler.post(toast);

				}
			    pd.dismiss();
			    
				return puntosList;


      }

    protected void onPostExecute(String[] result ) {
	        //Damos nombre al botón
	        //el siguiente método se ejecutará cuando se presione el botón
    		lblEnvResp = (Button) findViewById(R.id.pregunta_amistosa);
  	        lblEnvResp.setText("Enviar pregunta a...");
	        addListenerOnButton();

	        lv.setAdapter(new ArrayAdapter<String>(ListadoCompaneros.this,android.R.layout.simple_list_item_single_choice,result));
	        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);  
        
  	 pd.dismiss();
    }
    }
    //PARA PROBAR QUE USUARIO SE SELECCIONA
    public void addListenerOnButton() {
		 
		btnDisplay = (Button) findViewById(R.id.pregunta_amistosa);
	 
		btnDisplay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
	    		//Obtenemos los valores de shared preferences
	            prefs = PreferenceManager.getDefaultSharedPreferences(ListadoCompaneros.this);
	    		nPreguntasEnviadasAmigos = prefs.getInt("nPreguntasEnviadasAmigos", 0);	
	    		
	    		//miramos si hemos llegado al limite de preguntas a enviar o no
	    		if (nPreguntasEnviadasAmigos<limitePreguntasEnviadasAmigos){	
					final ProgressDialog pd1 = ProgressDialog.show(ListadoCompaneros.this, "Preguntas", "Enviando...", true, false);
					try{
						int p =  lv.getCheckedItemPosition();
						String destinatario = lv.getItemAtPosition(p).toString();
						Toast.makeText(ListadoCompaneros.this, "Enviando pregunta a "+destinatario, Toast.LENGTH_LONG).show(); 
						
				        loginusuario = prefs.getString("username", "n/a");
				        url2 = "http://pfc-jsuelaplaza.libresoft.es/android/enviapreguntaextra/"+loginusuario+"/"+destinatario;
				        //url2 = "http://193.147.51.87:1235/android/enviapreguntaextra/"+loginusuario+"/"+destinatario;
						
	            		new Thread(new Runnable(){
	            			@Override
	                		public void run(){
	            		        
	    						try {
	    							
	    		               	    String csrf = null;
	    		            		DefaultHttpClient httpclient = new DefaultHttpClient();
	    								 

	    		     			    HttpGet httpget = new HttpGet(url2);
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

	    		     			    			prefs = PreferenceManager.getDefaultSharedPreferences(ListadoCompaneros.this);
	    		     			    			asignatura = prefs.getString("subject", "n/a");
	    		     			    			
	    		     			    			nameValuePairs.add(new BasicNameValuePair("asignatura", asignatura));
	    		             			    	nameValuePairs.add(new BasicNameValuePair("csrfmiddlewaretoken", csrf.split("=")[1]));

	    		     			    		}
	    		     			    	} 
	    		             			    	
	    		  					HttpPost httppost = new HttpPost(url2);
		        
	    		  			        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
	    		  			        
	    		  			        response = httpclient.execute(httppost);
	    		  			        HttpEntity resEntityGet = response.getEntity();
	    							
	    							

	    							if (resEntityGet != null) {
	    								resultado = EntityUtils.toString(resEntityGet);
	    								if (resultado.equals("ok")){
	    							        //getting notification
	    		        		        	mensaje = "Pregunta enviada a tu compañero";
	    		        		            handler.post(toast);
	    		        		            //incremento el valor del contador de preguntas que puedo enviar
	    		        		            nPreguntasEnviadasAmigos++;
	    		        		    		edit = prefs.edit();
	    		        	    			edit.putInt("nPreguntasEnviadasAmigos", nPreguntasEnviadasAmigos);
	    		        					edit.commit();
	    		        					Log.i("ListadoCOmpaneros", "numero prealizaamigos: "+nPreguntasEnviadasAmigos);
	    									finish();
      					

	    									
	    								}else if (resultado.equals("fail")){
	    		        		        	mensaje = "No hay preguntas disponibles para este compañero (de momento...)";
	    		        		            handler.post(toast);
	    		        		            
	    								}else if (resultado.equals("no_user")){
	    		        		        	mensaje = "El usuario no ha utilizado la app todavía";
	    		        		            handler.post(toast);
	    									
	    								}else if (resultado.equals("no_red")){
	    		        		        	mensaje = "Ha ocurrido un error en el servidor";
	    		        		            handler.post(toast);
	    		
	    								}else{
	    		        		        	mensaje = "No se puede contactar con el servidor"+resultado;
	    		        		        	System.out.println(resultado);
	    		        		            handler.post(toast);
	    								}
	    								
	    							} else {
	    		
	    	        		        	mensaje = "Error al contactar con el servidor";
	    	        		            handler.post(toast);		
	    							}
	    						} catch (Exception e) {
	    							Log.i("ERROR", "CONECTION PROBLEM");
	            		        	mensaje = "Imposible contactar con el servidor";
	            		            handler.post(toast);
	    		
	    						}
	    					    pd1.dismiss();
	    	    			}
	  
	             		}).start();		

						}catch( NullPointerException e){
							pd1.dismiss();
							Toast.makeText(ListadoCompaneros.this,"Debes elegir un compañero", Toast.LENGTH_LONG).show();
						}catch( Exception e){
							pd1.dismiss();
							Toast.makeText(ListadoCompaneros.this,"Error ", Toast.LENGTH_LONG).show();
						}
			//si hemos superado el limite de preguntas
    		}else{
    			Toast.makeText(ListadoCompaneros.this,"Has enviado "+Integer.toString(nPreguntasEnviadasAmigos)+" retos... Take it easy! Espera un ratito", Toast.LENGTH_LONG).show();
    		}
				
		}
 


     	});
 
	};
    
  	final Runnable toast = new Runnable(){
 		public void run(){
     		Toast.makeText(ListadoCompaneros.this,
     				mensaje,
     				Toast.LENGTH_LONG).show();

 		}
 	};

}