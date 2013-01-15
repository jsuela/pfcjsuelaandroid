package com.jsuelapfc.preguntaras;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class SingleQuestion extends Activity {
	
	
    private TextView lblName;
    /*private Button lblEnvResp;
    private RadioButton lblResp,lblResp2, lblResp3;*/
    
    private RadioGroup radioGroupRespuestas;
    private RadioButton radioGroupButton;
    private Button btnDisplay;
	
	// JSON node keys
	private static final String TAG_FIELDS_PREGUNTA = "pregunta";
	private static final String TAG_FIELDS_RESPUESTA = "respuesta";
	private static final String TAG_FIELDS_RESPUESTA2 = "respuesta2";
	private static final String TAG_FIELDS_RESPUESTA3 = "respuesta3";
	private String mensaje;
	final Handler handler = new Handler();
	
    private SharedPreferences prefs;
	private String loginusuario;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	//Eliminamos la linea del titulo de la app
 	    requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_list_pregunta_item);
        
        // getting intent data
        Intent in = getIntent();
        
        // Get JSON values from previous intent
        String name = in.getStringExtra(TAG_FIELDS_PREGUNTA);
        String resp = in.getStringExtra(TAG_FIELDS_RESPUESTA);
        String resp2 = in.getStringExtra(TAG_FIELDS_RESPUESTA2);
        String resp3 = in.getStringExtra(TAG_FIELDS_RESPUESTA3);
        
        // Displaying all values on the screen
        lblName = (TextView) findViewById(R.id.name_label);
        RadioButton lblResp = (RadioButton) findViewById(R.id.respuesta_label);
        RadioButton lblResp2 = (RadioButton) findViewById(R.id.respuesta2_label);
        RadioButton lblResp3 = (RadioButton) findViewById(R.id.respuesta3_label);
        Button lblEnvResp = (Button) findViewById(R.id.enviar_respuesta);
        
        lblName.setText(name);
        lblResp.setText(resp);
        lblResp2.setText(resp2);
        lblResp3.setText(resp3);
        lblEnvResp.setText("Enviar respuesta");
        //Este método se ejecutará cuando se presione el botón
        
		//recuperamos quien es el usuario para posteriormente enviar la respuesta
    	prefs = PreferenceManager.getDefaultSharedPreferences(this);
		loginusuario = prefs.getString("username", "n/a");
        
        

        addListenerOnButton();
    }
	 public void addListenerOnButton() {
		 
			radioGroupRespuestas = (RadioGroup) findViewById(R.id.grupoRespuestas);
			btnDisplay = (Button) findViewById(R.id.enviar_respuesta);
		 
			btnDisplay.setOnClickListener(new OnClickListener() {
		 
				@Override
				public void onClick(View v) {
					final ProgressDialog pd = ProgressDialog.show(SingleQuestion.this, "Preguntas", "Enviando...", true, false);
					try{
					// get selected radio button from radioGroup
					int selectedId = radioGroupRespuestas.getCheckedRadioButtonId();
					// find the radiobutton by returned id
					radioGroupButton = (RadioButton) findViewById(selectedId);
					 
					Toast.makeText(SingleQuestion.this,"Respuesta elegida: "+
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
               			    			/*csrf = headers[i].toString().split(":")[2];
               			    			csrf = csrf.replace("}","");*/
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
					
									


					
					
                    /*utilizamos finish para que al lanzar una nueva activity el usuario
                    no pueda volver hacia atrás*/
					//finish();

					}catch( NullPointerException e){
						Toast.makeText(SingleQuestion.this,"Debes elegir una respuesta ", Toast.LENGTH_SHORT).show();
					}catch( Exception e){
						Toast.makeText(SingleQuestion.this,"Error ", Toast.LENGTH_SHORT).show();
					}
					//aquí añadir un catch para prblemas de red
					 
				}
				
	         	final Runnable toast = new Runnable(){
	         		public void run(){
	             		Toast.makeText(SingleQuestion.this,
	             				mensaje,
	             				Toast.LENGTH_LONG).show();

	         		}
	         	};
		 
			});
		 
		  }

}
