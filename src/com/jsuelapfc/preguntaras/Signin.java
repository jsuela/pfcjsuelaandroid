package com.jsuelapfc.preguntaras;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class Signin extends Activity{	
	
	// url to make request
	private static String url;
	 
	// JSON Node names
	private static final String TAG_COLEGIOS = "colegios";
	private static final String TAG_PK = "pk";
	private static final String TAG_MODEL = "model";

	private static final String TAG_FIELDS = "fields";
	private static final String TAG_FIELDS_COLEGIO = "colegio";
	
	private JSONParser jParser;
	private ArrayList<HashMap<String, String>> colegiosList;

	// contacts JSONArray
	private JSONArray colegios = null;

	private ProgressDialog pd;
	
	private String mensaje;
	private final Handler handler = new Handler();
    private Context mContext;
    
    private   ArrayList<String> schoolNames = new ArrayList<String>();
    
    private EditText etusuario, etpassword, etpassword2, etemail, etnombreyapellidos;
	private String loginusuario, loginpassword, loginpassword2, logincorreo, logincolegio, loginnombreyapellidos;
    private Spinner etcolegio;
    
    private Spinner mySpinner;
    private Boolean continua;

	   @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        setContentView(R.layout.signin);
	        

	        mContext = this;

	        // Hashmap for ListView
	        colegiosList = new ArrayList<HashMap<String, String>>();
	 
	        // Creating JSON Parser instance
	        jParser = new JSONParser();
	 
	        // getting JSON string from URL
	        url = "http://pfc-jsuelaplaza.libresoft.es/android/colegios";	

	      //Creamos una nueva instancia y llamamos al método ejecutar
	      //pasándole el string.
	        
	        Button button = (Button) findViewById(R.id.dardealta);
	        button.setText("Registrarme");
	        
	        
	        
	    	pd = ProgressDialog.show(Signin.this, "Preguntas", "Cargando...", true, false);	

	      new MiTarea().execute(url);
	      
	      
	      //si pulsamos al boton de entrar
	      button.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	try{
	            		etusuario = (EditText) findViewById(R.id.loginusuario);
	            		loginusuario = etusuario.getText().toString();
	                
	            		etpassword = (EditText) findViewById(R.id.loginpassword);
	            		loginpassword = etpassword.getText().toString();
	                
	            		etpassword2 = (EditText) findViewById(R.id.loginpassword2);
	            		loginpassword2 = etpassword2.getText().toString();
	            		
	            		etnombreyapellidos = (EditText) findViewById(R.id.loginnombreyapellidos);
	            		loginnombreyapellidos = etnombreyapellidos.getText().toString();
	            		
	            		etemail = (EditText) findViewById(R.id.logincorreo);
	            		logincorreo = etemail.getText().toString();
	            		
	            		etcolegio = (Spinner) findViewById(R.id.colegios_spinner);
	            		logincolegio = etcolegio.getItemAtPosition(mySpinner.getSelectedItemPosition()).toString();
	            		
	            		//debo comprobar ue no haya nada en blanco, si las contraseñas concuerdan(y si son mayor de 6), si colegio no es elija colegio
	            		//y en el servidor si el usuario ya existe, en ese caso devolvera error y debera volver a rellenar
	            		
	            		continua=true;
	            		//compruebo primero que las contraseñas introducidas sean las mismas y que tenga longitud de al menos 6
	            		if (!(loginpassword.equals(loginpassword2))){
	            			Toast.makeText(Signin.this,"Las contraseñas no concuerdan", Toast.LENGTH_LONG).show();
	            			continua=false;
	            		}
	            		//si estan vacíos
	            		if ((loginusuario.length()==0) || (loginpassword.length()==0) || (loginpassword2.length()==0) || (loginnombreyapellidos.length()==0) || (logincorreo.length()==0) || (logincolegio.equals("Elige colegio"))){
	            			Toast.makeText(Signin.this,"Debes rellenar todos los campos y escoger tu colegio", Toast.LENGTH_LONG).show();
	            			continua=false;
	            		}
	            		
	            		if ((loginpassword.length()<6) ){
	            			Toast.makeText(Signin.this,"La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_LONG).show();
	            			continua=false;
	            		}
	            		
	            		if ((loginusuario.contains(" ")) ){
	            			Toast.makeText(Signin.this,"El nombre de usuario no puede contener espacios", Toast.LENGTH_LONG).show();
	            			continua=false;
	            		}
	            		
            		
	            		
	            	}catch( NullPointerException e){
						Toast.makeText(Signin.this,"Ha ocurrido un error, inténtalo de nuevo", Toast.LENGTH_LONG).show();
					}
	            	//si ha rellenado todo correctamente continuamos, si no nos mantendremos en esa pantalla
		            if (continua == true){
		            	
		                final ProgressDialog pd = ProgressDialog.show(Signin.this, "Preguntas", "Registrando...", true, false);

                		new Thread(new Runnable(){
                			@Override
                    		public void run(){
                     	       String csrf = null;
                  			   DefaultHttpClient httpclient = new DefaultHttpClient();
                 			   
                			    try {
                   			    	HttpGet httpget = new HttpGet("http://pfc-jsuelaplaza.libresoft.es/android/signin");
                   			    	HttpResponse response = httpclient.execute(httpget);
                       	
                   			    	Header[] headers = response.getAllHeaders();
                   			    	
                			        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
                			        System.out.println("entras aqui?0"+response.toString());
                   			    	
                   			    	for (int i = 0; i < headers.length; i++){	
               			    			System.out.println("cabeceraaaa:"+response.getParams().toString());
                   			    		if (headers[i].toString().contains("csrftoken")){
                   			    			System.out.println("entras aqui?2");

                   			    			//Para la version de desarrollo,no para la de apache
                   			    			/*csrf = headers[i].toString().split(" ")[2];
                   			    			System.out.println("el csrf0000 es:"+ csrf);
                   			    			csrf = csrf.replace(";","");

                  			    			System.out.println("el csrf1 es:"+ csrf.split("=")[1]);*/
                   			    			
                   			    			//para version apache
                   			    			/*csrf = headers[i].toString().split(" ")[1];
                   			    			csrf = csrf.replace(";","");
                   			    			System.out.println("el csrf111111 es:"+ headers[i].toString());
                   			    			System.out.println("el csrf111111 es:"+ csrf.split("=")[1]);*/
                   			    			csrf=headers[i].toString();
                   			    			csrf = csrf.replace("Set-Cookie:","");
                   			    			csrf = csrf.replace(" ","");
                   			    			csrf = csrf.replace(";expires","");
                   			    			System.out.println("el csrf111111nuevo es:"+ csrf.split("=")[1]);

                   			    			
		     
                   			    			//System.out.println("CSSSSRF:"+ csrf.split("=")[1]);
		                        			nameValuePairs.add(new BasicNameValuePair("user", loginusuario));
		                        			nameValuePairs.add(new BasicNameValuePair("password", loginpassword));
		                        			nameValuePairs.add(new BasicNameValuePair("password2", loginpassword2));
		                        			nameValuePairs.add(new BasicNameValuePair("nombreyapellidos", loginnombreyapellidos));
		                        			nameValuePairs.add(new BasicNameValuePair("correo", logincorreo));
		                        			nameValuePairs.add(new BasicNameValuePair("colegio", logincolegio));
	                       			    	nameValuePairs.add(new BasicNameValuePair("csrfmiddlewaretoken", csrf.split("=")[1]));

                   			    		}
                   			    	} 
	    	               			    	
                					HttpPost httppost = new HttpPost("http://pfc-jsuelaplaza.libresoft.es/android/signin");
                   			    	//nameValuePairs.add(new BasicNameValuePair("csrfmiddlewaretoken", csrf.split("=")[1]));

                			        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                			        
                			        response = httpclient.execute(httppost);
                			        
                        			//pd.dismiss();
                        			
                        			if (response.getStatusLine().getStatusCode() == 500)
                            	    	mensaje = "El usuario ya existe";
                        			else if (response.getStatusLine().getStatusCode() == 200){
                        				
                        				mensaje = "Registrado correctamente";
                        				System.out.println("el mensajeAlamacena:"+mensaje);

                            			Intent in = new Intent(getApplicationContext(), Login.class);
                            			startActivity(in);
                            			finish();
                        			}else
                        				mensaje = "No se puede conectar con el servidor en este momento. " +
                        						"Inténtelo más tarde";
                        			 
                        			handler.post(toast);
                        			
                			    /*} catch (ClientProtocolException e) {

                			    } catch (IOException e) {*/


  
                     		
            		        } catch (Exception e) {
            		        	mensaje = "No se puede contactar con el servidor";
            		            e.printStackTrace();
            		            handler.post(toast);
            		        

            			    }
                			    pd.dismiss();
                    		}
                 		}).start();	
		            	
		            	
		            	
		            	
		            	
		            }

	            	
	            	
	            	
	            	
	            }
	            
	            
	      });
	      
	      
	      
	      
        
	    
	      
	      
	      
	   }    
	      


	     private class MiTarea extends AsyncTask<String, ListAdapter, ArrayList<String> >{

	          protected ArrayList<String> doInBackground(String... urls) {
			        try {

			        	
				        JSONObject json = jParser.getJSONFromUrl(url);
			            // Getting Array of Contacts
			            colegios = json.getJSONArray(TAG_COLEGIOS);
			 
			            // looping through all schools
			            

		                schoolNames.add("Elige colegio"); 
			            
			            for(int i = 0; i < colegios.length(); i++){
			                JSONObject c = colegios.getJSONObject(i);
			 
			                // Storing each json item in variable
			                String pk = c.getString(TAG_PK);
			                String model = c.getString(TAG_MODEL);
			         
			                // Respuestas is agin JSON Object
			                JSONObject fields = c.getJSONObject(TAG_FIELDS);
			                String school = fields.getString(TAG_FIELDS_COLEGIO);  

			                
			                schoolNames.add(school);
			                		               
			            } 
			        } catch (Exception e) {
    		        	mensaje = "No se puede contactar con el servidor, inténtalo pasados unos minutos";
    		            handler.post(toast);
			            e.printStackTrace();
			        	pd.dismiss();
			     		Intent in = new Intent(getApplicationContext(), Login.class);
			     		startActivity(in);
						finish();

			        }	

	         

					return schoolNames;

	          }

	          protected void onPostExecute(ArrayList<String> result ) {
	        	  
	        	  
	        	  mySpinner = (Spinner)findViewById(R.id.colegios_spinner);
	        	  
	        	  
	        	  ArrayAdapter<String> adapter = new ArrayAdapter<String>(Signin.this, android.R.layout.simple_spinner_item, schoolNames);
	        	  
	        	  
	        	  adapter.setDropDownViewResource(R.layout.multiline_spinner_dropdown_item);        	  
	  	        
	        	  mySpinner.setAdapter(adapter);
	        	  
	        	 pd.dismiss();
	          }
	    }
      	final Runnable toast = new Runnable(){
     		public void run(){
         		Toast.makeText(mContext,
         				mensaje,
         				Toast.LENGTH_LONG).show();

     		}
     	};

}