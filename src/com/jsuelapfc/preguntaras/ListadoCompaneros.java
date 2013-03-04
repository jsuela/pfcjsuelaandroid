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
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
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
	private JSONParser jParser;
	private ArrayList<HashMap<String, String>> puntosList;

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
	private int limitePreguntasEnviadasAmigos = 10;


	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 	    requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.ranking2);
        
        lv = (ListView)findViewById(android.R.id.list);
        
        // Hashmap for ListView
        puntosList = new ArrayList<HashMap<String, String>>();
 
        // Creating JSON Parser instance
        jParser = new JSONParser();
 
        // getting JSON string from URL
        url = "http://pfc-jsuelaplaza.libresoft.es/android/clasificacion";	
        
    	pd = ProgressDialog.show(ListadoCompaneros.this, "Preguntas", "Cargando...", true, false);	

	    new MiTarea().execute(url);
        
    }
	
	
    private class MiTarea extends AsyncTask<String, ListAdapter, ArrayList<HashMap<String, String>> >{


        protected ArrayList<HashMap<String, String>> doInBackground(String... urls) {

	        prefs = PreferenceManager.getDefaultSharedPreferences(ListadoCompaneros.this);
	        loginusuario = prefs.getString("username", "n/a");
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
		                //muestro todos menos yo

		                map.put(TAG_PK, pk);
		                map.put(TAG_MODEL, model);
		                map.put(TAG_FIELDS_PUNTOS, "Puntos: "+puntos);
		                map.put(TAG_FIELDS_USUARIO, usuario);
			            if(!(loginusuario.equals(usuario))){
			                // adding HashList to ArrayList
				            puntosList.add(map); 
		                }
		            } 
		        } catch (Exception e) {
  				mensaje = "No se puede conectar con el servidor. Inténtelo más tarde";
  			 
  				handler.post(toast);
		            e.printStackTrace();
		        }	
				return puntosList;
        }

    protected void onPostExecute(ArrayList<HashMap<String, String>> result ) {
	        //Damos nombre al botón
	        //el siguiente método se ejecutará cuando se presione el botón
    		lblEnvResp = (Button) findViewById(R.id.pregunta_amistosa);
  	        lblEnvResp.setText("Send to...");
	        addListenerOnButton();
	        
	        
	        
	        /*lv.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_single_choice,s));
	        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);   */
	        
	        
    	
	        adapter2 = new SimpleAdapter(ListadoCompaneros.this, puntosList,
    		  android.R.layout.simple_list_item_single_choice,
      		new String[] { TAG_FIELDS_USUARIO}, new int[] {
    		  android.R.id.text1});
      
      
      
	        lv.setAdapter(adapter2);
	        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);  
        
  	 pd.dismiss();
    }
    }
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
							String destinatario = ((TextView) lv.getChildAt(p)).getText().toString(); 
							Toast.makeText(ListadoCompaneros.this, "Enviando pregunta a "+destinatario, Toast.LENGTH_LONG).show(); 
							
					        loginusuario = prefs.getString("username", "n/a");
					        url2 = "http://pfc-jsuelaplaza.libresoft.es/android/enviapreguntaextra/"+loginusuario+"/"+destinatario;
	
							
		            		new Thread(new Runnable(){
		            			@Override
		                		public void run(){
		            		        
		    						try {
		    							HttpClient client = new DefaultHttpClient();
		    							String getURL = url2;
		    							HttpGet get = new HttpGet(getURL);
		    							HttpResponse responseGet = client.execute(get);
		    							HttpEntity resEntityGet = responseGet.getEntity();
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
		    									//Intent in = new Intent(getApplicationContext(), MainActivity.class);
		    		        					//startActivity(in);
		    		        					

		    									
		    								}else if (resultado.equals("fail")){
		    		        		        	mensaje = "No hay preguntas disponibles (de momento...)";
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
							
		                    /*utilizamos finish para que al lanzar una nueva activity el usuario
		                    no pueda volver hacia atrás*/
							//finish();
	
							}catch( NullPointerException e){
								pd1.dismiss();
								Toast.makeText(ListadoCompaneros.this,"Debes elegir un compañero", Toast.LENGTH_SHORT).show();
							}catch( Exception e){
								pd1.dismiss();
								Toast.makeText(ListadoCompaneros.this,"Error ", Toast.LENGTH_SHORT).show();
							}
					//si hemos superado el limite de preguntas
		    		}else{
		    			Toast.makeText(ListadoCompaneros.this,"Has superado el limite de preguntas, inténtalo mañana"+Integer.toString(nPreguntasEnviadasAmigos), Toast.LENGTH_LONG).show();
		    		}
						
				}
		 


	         	});
		 
			};
		 
		  
	
	
	 /*public void addListenerOnButton() {
		 
			radioGroupRespuestas = (RadioGroup) findViewById(R.id.grupoRespuestas);
			btnDisplay = (Button) findViewById(R.id.enviar_respuesta);
		 
			btnDisplay.setOnClickListener(new OnClickListener() {
		 
				@Override
				public void onClick(View v) {
					final ProgressDialog pd = ProgressDialog.show(ListadoCompañeros.this, "Preguntas", "Enviando...", true, false);
					try{
					// get selected radio button from radioGroup
					int selectedId = radioGroupRespuestas.getCheckedRadioButtonId();
					// find the radiobutton by returned id
					radioGroupButton = (RadioButton) findViewById(selectedId);
					 
					Toast.makeText(ListadoCompañeros.this,"Respuesta elegida: "+
						radioGroupButton.getText(), Toast.LENGTH_SHORT).show();
					
		

            		new Thread(new Runnable(){
            			@Override
                		public void run(){
                 	       String csrf = null;
              			   DefaultHttpClient httpclient = new DefaultHttpClient();
             			   
            			    try {
               			    	HttpGet httpget = new HttpGet("http://10.0.2.2:1234/android/enviarespuestas");
               			    	HttpResponse response = httpclient.execute(httpget);
              
                   	
               			    	Header[] headers = response.getAllHeaders();
               			    	
            			        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
       			    			System.out.println("entras aqui?0"+response.toString());
       			    			

       			    			System.out.println("cabeceraaaa:"+response.getParams().toString());
       			    			

               			    	for (int i = 0; i < headers.length; i++){	
           			    			System.out.println("cabecera:"+headers[i].toString());
               			    		if (headers[i].toString().contains("csrftoken")){
               			    			
               			    			System.out.println("entras aqui?");
               			    			///csrf = headers[i].toString().split(":")[2];
               			    			//csrf = csrf.replace("}","");
               			    			csrf = headers[i].toString().split(" ")[2];
               			    			System.out.println("el csrf0000 es:"+ csrf);
               			    			csrf = csrf.replace(";","");

              			    			System.out.println("el csrf1 es:"+ csrf.split("=")[1]);



                       			    	nameValuePairs.add(new BasicNameValuePair("csrfmiddlewaretoken", csrf.split("=")[1]));

               			    		}
               			    	} 
    	               			   
               			 
               			    	
            					HttpPost httppost = new HttpPost("http://10.0.2.2:1234/android/enviarespuestas");
               			    	nameValuePairs.add(new BasicNameValuePair("csrfmiddlewaretoken", csrf.split("=")[1]));
               			    	System.out.println("el csrffinalll es:"+ csrf);
               			    	nameValuePairs.add(new BasicNameValuePair("respuesta", radioGroupButton.getText().toString()));
               			    	nameValuePairs.add(new BasicNameValuePair("pregunta", lblName.getText().toString()));
               			    	nameValuePairs.add(new BasicNameValuePair("usuario", loginusuario));
              			    	  
               			    	
               			    	
            			        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
            	
            			        
            			        response = httpclient.execute(httppost);
            			        

                    			
                    			if (response.getStatusLine().getStatusCode() == 500)
                        	    	mensaje = response.getStatusLine().getReasonPhrase();
                    			else if (response.getStatusLine().getStatusCode() == 403)
                        	    	mensaje = response.getStatusLine().getReasonPhrase();
                    			else if (response.getStatusLine().getStatusCode() == 404)
                        	    	mensaje = response.getStatusLine().getReasonPhrase();
                    			else if (response.getStatusLine().getStatusCode() == 200){
                    				HttpEntity resEntityGet = response.getEntity();
                    				
                    				if ((EntityUtils.toString(resEntityGet).equals("true"))){
                    					mensaje= "¡Correcto!";       
                    				}else{
                    					mensaje="Lo siento, no es correcto...";
                    				}
                    				
                					Intent in = new Intent(getApplicationContext(), MainActivity.class);
                					startActivity(in);
                					finish();

                    			}else
                    				mensaje = "No se puede conectar con el servidor en este momento. " +
                    						"Inténtelo más tarde";
                    			 
                    			handler.post(toast);
                    			
            			    } catch (ClientProtocolException e) {
            			    	mensaje = "Error en el servidor. No se puede conectar con el servidor en este momento.";
            			    	handler.post(toast);
            			    } catch (Exception e) {
            			    	mensaje = "Error en el servidor";
            			    	handler.post(toast);
            			    }
            			   
            			    
              			    pd.dismiss();
                 		}
  
             		}).start();		
					
                    //utilizamos finish para que al lanzar una nueva activity el usuario
                    //no pueda volver hacia atrás
					//finish();

					}catch( NullPointerException e){
						Toast.makeText(ListadoCompañeros.this,"Debes elegir una respuesta ", Toast.LENGTH_SHORT).show();
					}catch( Exception e){
						Toast.makeText(ListadoCompañeros.this,"Error ", Toast.LENGTH_SHORT).show();
					}
					//aquí añadir un catch para prblemas de red
					 
				}
				

		 
			});
		 
		  }*/
  	final Runnable toast = new Runnable(){
 		public void run(){
     		Toast.makeText(ListadoCompaneros.this,
     				mensaje,
     				Toast.LENGTH_LONG).show();

 		}
 	};

}
