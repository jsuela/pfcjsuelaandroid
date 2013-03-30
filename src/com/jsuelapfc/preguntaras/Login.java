package com.jsuelapfc.preguntaras;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity {
    /** Called when the activity is first created. */
	

    private SharedPreferences prefs;
    private EditText etusuario, etpassword;
	private String loginusuario, loginpassword;
	private String mensaje = "Incorrecto";
	private final Handler handler = new Handler();
    private Context mContext;
    private String asignatura;

    
    @Override
    public void onCreate(Bundle savedInstanceState){
    	//Eliminamos la linea del titulo de la app
 	    requestWindowFeature(Window.FEATURE_NO_TITLE);
 	    
		super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
		
		


		//Para login descomentar desde aqui hasta.....y poner activity en vez de Tabactivity
        setContentView(R.layout.login);
 	    //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        mContext = this;
        
        Button button = (Button) findViewById(R.id.entrar);
        button.setText("Entrar");
        
        
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		loginusuario = prefs.getString("username", "n/a");
		
		
		//Comprobamos si ya ha habido login, y si es así saltamos a la pantalla principal
		if (!loginusuario.equals("n/a")){
			//miramos si ya ha escogido alguna asignatura, sino le obligamos a que escoja
	        asignatura = prefs.getString("subject", "n/a");
			if (asignatura.equals("n/a")){
				Intent in = new Intent(getApplicationContext(), Subjects.class);
	            startActivity(in);
	            finish();
			}else{
			
	     		Toast.makeText(mContext,
	     				"¡Hola " + loginusuario+"!",
	     				Toast.LENGTH_SHORT).show();
	     		Intent in = new Intent(getApplicationContext(), MainActivity.class);
	     		startActivity(in);
				finish();
			}
		}
		
		
    	button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	
            	try{
            		etusuario = (EditText) findViewById(R.id.loginusuario);
            		loginusuario = etusuario.getText().toString();
                
            		etpassword = (EditText) findViewById(R.id.loginpassword);
            		loginpassword = etpassword.getText().toString();
            	}catch( NullPointerException e){
					Toast.makeText(Login.this,"Usuario o contrase ", Toast.LENGTH_SHORT).show();
				}
 
                
                final ProgressDialog pd = ProgressDialog.show(Login.this, "Preguntas", "Accediendo...", true, false);

                		new Thread(new Runnable(){
                			@Override
                    		public void run(){
                     	       String csrf = null;
                  			   DefaultHttpClient httpclient = new DefaultHttpClient();
                 			   
                			    try {
                   			    	HttpGet httpget = new HttpGet("http://pfc-jsuelaplaza.libresoft.es/android/login");
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
		                        			nameValuePairs.add(new BasicNameValuePair("type", "1"));
		                        			nameValuePairs.add(new BasicNameValuePair("idmovil", "1a2b"));
	                       			    	nameValuePairs.add(new BasicNameValuePair("csrfmiddlewaretoken", csrf.split("=")[1]));

                   			    		}
                   			    	} 
	    	               			    	
                					HttpPost httppost = new HttpPost("http://pfc-jsuelaplaza.libresoft.es/android/login");
                   			    	//nameValuePairs.add(new BasicNameValuePair("csrfmiddlewaretoken", csrf.split("=")[1]));

                			        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                			        
                			        response = httpclient.execute(httppost);
                			        
                        			//pd.dismiss();
                        			
                        			if (response.getStatusLine().getStatusCode() == 500)
                            	    	mensaje = "Usuario o contraseña incorrecto";
                        			else if (response.getStatusLine().getStatusCode() == 200){
                        				
                        				mensaje = "Correcto";
                        				System.out.println("el mensajeAlamacena:"+mensaje);
                        				prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
                        				Editor edit = prefs.edit();
                        				edit.putString("username", loginusuario);
                        				edit.putString("password", loginpassword);
                        				edit.putString("registrationId", "1a2b");
                        				edit.commit();
                            			Intent in = new Intent(getApplicationContext(), Subjects.class);
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

                		System.out.println("l mensaje es: " +mensaje);
                		if (mensaje.equals("Correcto")){
                			Intent in = new Intent(getApplicationContext(), MainActivity.class);
                			startActivity(in);
                		}
                        /*utilizamos finish para que al lanzar una nueva activity el usuario
                        no pueda volver hacia atrás*/
    					//finish();
    					
    			
         
             }
            
             
         	final Runnable toast = new Runnable(){
         		public void run(){
             		Toast.makeText(mContext,
             				mensaje,
             				Toast.LENGTH_SHORT).show();

         		}
         	};
         	
             
         });
    }
}

