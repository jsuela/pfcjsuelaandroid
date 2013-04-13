package com.jsuelapfc.preguntaras;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
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
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class SubjectsRegister extends ListActivity {
	
	// url to make request
	private static String url;
	private static String url2;
	private static String url3;
	private static String subject;

	 
	// JSON Node names
	private static final String TAG_ASIGNATURASALUMNO = "asignaturasalumno";
	private static final String TAG_PK = "pk";
	private static final String TAG_MODEL = "model";

	private static final String TAG_FIELDS = "fields";
	private static final String TAG_FIELDS_ASIGNATURA = "asignatura";
	
	// JSON Node names2
	private static final String TAG_ASIGNATURAS = "asignaturas";
	//private static final String TAG_PK = "pk";
	//private static final String TAG_MODEL = "model";

	//private static final String TAG_FIELDS = "fields";
	//private static final String TAG_FIELDS_ASIGNATURA = "asignatura";

	

	private JSONParser jParser;
	private JSONParser jParser2;

	private ArrayList<HashMap<String, String>> asignaturasalumnoList;
	private ArrayList<HashMap<String, String>> asignaturasList;

	// contacts JSONArray
	private JSONArray asignaturasalumno = null;
	private JSONArray asignaturas = null;

	
	private ProgressDialog pd;
	
	private String mensaje;
	private final Handler handler = new Handler();
    private Button irA;
    private Button irMatricula;

    private Button btnDisplayirMatricula;
    private Button btnDisplayirA;
    
    private ListAdapter adapter2;
    private ListAdapter adapter3;
    
    private ListView lv;
    private ListView lv2;
    private TextView tv;

    
	private String loginusuario;
    private SharedPreferences prefs;
    private String resultado;
    
	/*private int nPreguntasEnviadasAmigos;
	private Editor edit;
	private int limitePreguntasEnviadasAmigos = 10;*/


	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 	    requestWindowFeature(Window.FEATURE_NO_TITLE);
 	    //para que no rote setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.subjects);
        
        lv = (ListView)findViewById(android.R.id.list);
		irA = (Button) findViewById(R.id.matricularse);
		tv = (TextView)findViewById(R.id.titulo);
		tv.setText("Listado de todas las asignaturas");
		


        
        // Hashmap for ListView
        asignaturasList = new ArrayList<HashMap<String, String>>();
 
        // Creating JSON Parser instance
        jParser = new JSONParser();

 
        
        prefs = PreferenceManager.getDefaultSharedPreferences(SubjectsRegister.this);
        loginusuario = prefs.getString("username", "n/a");
        // getting JSON string from URL
        
        url = "http://pfc-jsuelaplaza.libresoft.es/android/asignaturas/listado/completo/"+loginusuario;
    	pd = ProgressDialog.show(SubjectsRegister.this, "Preguntas", "Cargando...", true, false);	

	    new MiTarea().execute(url);
        
    }
	
	
    
    

    
    
    private class MiTarea extends AsyncTask<String, ListAdapter, ArrayList<HashMap<String, String>> >{


        protected ArrayList<HashMap<String, String>> doInBackground(String... urls) {


		        try {

			        JSONObject json = jParser.getJSONFromUrl(url);
		            // Getting Array of Contacts
		            asignaturas = json.getJSONArray(TAG_ASIGNATURAS);
		 
		            // looping through All Contacts
		            for(int i = 0; i < asignaturas.length(); i++){
		                JSONObject c = asignaturas.getJSONObject(i);
		 
		                // Storing each json item in variable
		                String pk = c.getString(TAG_PK);
		                String model = c.getString(TAG_MODEL);
		         
		                // Respuestas is agin JSON Object
		                JSONObject fields = c.getJSONObject(TAG_FIELDS);
		                String asignaturas = fields.getString(TAG_FIELDS_ASIGNATURA);  
   	    

		                
		                // creating new HashMap
		                HashMap<String, String> map = new HashMap<String, String>();
		 
		                // adding each child node to HashMap key => value

		                map.put(TAG_PK, pk);
		                map.put(TAG_MODEL, model);
		                map.put(TAG_FIELDS_ASIGNATURA, asignaturas);
		                asignaturasList.add(map); 

		            } 
		        } catch (Exception e) {
  				mensaje = "No se puede conectar con el servidor. Inténtelo más tarde";
  			 
  				handler.post(toast);
		            e.printStackTrace();
		        }	
				return asignaturasList;
        }

    protected void onPostExecute(ArrayList<HashMap<String, String>> result ) {
	        //Damos nombre al botón
	        //el siguiente método se ejecutará cuando se presione el botón
    		irA = (Button) findViewById(R.id.matricularse);
    		irA.setText("Matricularme");
	        addListenerOnButton();
	        
	        
	        
	        /*lv.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_single_choice,s));
	        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);   */
	        
	        
    	
	        /*adapter2 = new SimpleAdapter(Subjects.this, asignaturasalumnoList,
    		  android.R.layout.simple_list_item_single_choice,
      		new String[] { TAG_FIELDS_ASIGNATURA}, new int[] {
    		  android.R.id.text1});*/

	        
	        
	        adapter3 = new SimpleAdapter(SubjectsRegister.this, asignaturasList,
    		  android.R.layout.simple_list_item_single_choice,
      		new String[] { TAG_FIELDS_ASIGNATURA}, new int[] {
    		  android.R.id.text1});
      
      
      
	        /*lv.setAdapter(adapter3);
	        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);  */
	        
	        lv.setAdapter(adapter3);
	        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);  
        
  	 pd.dismiss();
    }
    }
    
    
	 public void addListenerOnButton() {
		 
		 	btnDisplayirA = (Button) findViewById(R.id.matricularse);
		 
		 	btnDisplayirA.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					final ProgressDialog pd1 = ProgressDialog.show(SubjectsRegister.this, "Preguntas", "Enviando...", true, false);
					try{

						int p =  lv.getCheckedItemPosition();
						subject = ((TextView) lv.getChildAt(p)).getText().toString(); 
						Toast.makeText(SubjectsRegister.this, "Matriculando "+subject, Toast.LENGTH_LONG).show(); 
						
				        loginusuario = prefs.getString("username", "n/a");
				        url3 = "http://pfc-jsuelaplaza.libresoft.es/android/asignaturas/matricula";

						
	            		new Thread(new Runnable(){
	            			@Override
	                		public void run(){
	                     	    String csrf = null;
	                  			DefaultHttpClient httpclient = new DefaultHttpClient();
	    						try {  

                   			    	HttpGet httpget = new HttpGet(url3);
                   			    	HttpResponse response = httpclient.execute(httpget);
                       	
                   			    	Header[] headers = response.getAllHeaders();
                   			    	
                			        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);

                   			    	
                   			    	for (int i = 0; i < headers.length; i++){	
               			    			System.out.println("cabeceraaaa:"+response.getParams().toString());
                   			    		if (headers[i].toString().contains("csrftoken")){

                   			    			csrf=headers[i].toString();
                   			    			csrf = csrf.replace("Set-Cookie:","");
                   			    			csrf = csrf.replace(" ","");
                   			    			csrf = csrf.replace(";expires","");
                   			    			System.out.println("el csrf111111nuevo es:"+ csrf.split("=")[1]);

                   			    			
		     
                   			    			//System.out.println("CSSSSRF:"+ csrf.split("=")[1]);
		                        			nameValuePairs.add(new BasicNameValuePair("usuario", loginusuario));
		                        			nameValuePairs.add(new BasicNameValuePair("asignatura", subject));
	                       			    	nameValuePairs.add(new BasicNameValuePair("csrfmiddlewaretoken", csrf.split("=")[1]));

                   			    		}
                   			    	} 
	    	               			    	
                					HttpPost httppost = new HttpPost("http://pfc-jsuelaplaza.libresoft.es/android/asignaturas/matricula");
                   			    	//nameValuePairs.add(new BasicNameValuePair("csrfmiddlewaretoken", csrf.split("=")[1]));

                			        
                			        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
                			        
                			        response = httpclient.execute(httppost);
                			        HttpEntity resEntityGet = response.getEntity();
                			        
                        			//pd.dismiss();
                        			
	    							if (resEntityGet != null) {
	    								resultado = EntityUtils.toString(resEntityGet);
	    								if (resultado.equals("ok")){
	    							        //getting notification
	    		        		        	mensaje = "Matriculado correctamente";
	    		        		            handler.post(toast);
	    									finish();
	    									Intent in = new Intent(getApplicationContext(), Subjects.class);
	    		        					startActivity(in);
	    					                finish();
	    		        					

	    									
	    								}else if (resultado.equals("exist")){
	    		        		        	mensaje = "Ya estas matriculado en esta asignatura";
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
	    							Log.i("ERROR", "CONECTION PROBLEM"+resultado);
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
							Toast.makeText(SubjectsRegister.this,"Debes elegir una asignatura", Toast.LENGTH_LONG).show();
						}catch( Exception e){
							pd1.dismiss();
							Toast.makeText(SubjectsRegister.this,"Error ", Toast.LENGTH_LONG).show();
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
     		Toast.makeText(SubjectsRegister.this,
     				mensaje,
     				Toast.LENGTH_LONG).show();

 		}
 	};

}

