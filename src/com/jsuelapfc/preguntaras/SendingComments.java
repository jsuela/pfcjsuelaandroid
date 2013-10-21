package com.jsuelapfc.preguntaras;

import java.util.ArrayList;
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
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SendingComments extends Activity{

	
    private Button lblEnvComment;
    private Button btnDisplay;
    
	private String asignatura;
	private String loginusuario;
    private SharedPreferences prefs;
    
	private String mensaje;
	private final Handler handler = new Handler();
    private Context mContext;
    
    private String resultado;
	private static String url;
	
	private EditText etcomentario;
	private String comentario;
    private Boolean continua;
    

	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 	    requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.send_comment);
        mContext = this;
        
        //Damos nombre al botón
        //el siguiente método se ejecutará cuando se presione el botón
		lblEnvComment= (Button) findViewById(R.id.enviar);
	    lblEnvComment.setText("Enviar comentario");
        addListenerOnButton();
        
	
	}
	
	 public void addListenerOnButton() {
		 
			btnDisplay = (Button) findViewById(R.id.enviar);
		 
			btnDisplay.setOnClickListener(new OnClickListener() {
		 
				@Override
				public void onClick(View v) {
            		continua=true;
            		etcomentario = (EditText) findViewById(R.id.comentario);
            		comentario = etcomentario.getText().toString();
            		if ((comentario.length()==0) || (comentario.length()>300)){
            			Toast.makeText(SendingComments.this,"Comentario vacío o demasiado largo", Toast.LENGTH_LONG).show();
            			continua=false;
            		}
		            if (continua == true){
		            	enviaComentario();
		            }
	         	};
		 
			});
		 
		  }
	 
	 
		public void enviaComentario(){
			

	        prefs = PreferenceManager.getDefaultSharedPreferences(SendingComments.this);
	        loginusuario = prefs.getString("username", "n/a");
	        url = "http://pfc-jsuelaplaza.libresoft.es/android/comentario";
	        
            final ProgressDialog pd1 = ProgressDialog.show(SendingComments.this, "Preguntas", "Enviando...", true, false);

    		new Thread(new Runnable(){
    			@Override
        		public void run(){
	        
					try {
						
						
	               	    String csrf = null;
	            		DefaultHttpClient httpclient = new DefaultHttpClient();
							 

	     			    HttpGet httpget = new HttpGet(url);
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

	     			    			prefs = PreferenceManager.getDefaultSharedPreferences(SendingComments.this);
	     			    			asignatura = prefs.getString("subject", "n/a");
	     			    			
	     			    			nameValuePairs.add(new BasicNameValuePair("asignatura", asignatura));
	     			    			nameValuePairs.add(new BasicNameValuePair("user", loginusuario));
	     			    			nameValuePairs.add(new BasicNameValuePair("comentario", comentario));
	     			    			
	             			    	nameValuePairs.add(new BasicNameValuePair("csrfmiddlewaretoken", csrf.split("=")[1]));

	     			    		}
	     			    	} 
	             			    	
	  					HttpPost httppost = new HttpPost(url);
	  			        
	  			        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
	  			        
	  			        response = httpclient.execute(httppost);
	  			        HttpEntity resEntityGet = response.getEntity();


						if (resEntityGet != null) {
							resultado = EntityUtils.toString(resEntityGet);
							if (resultado.equals("ok")){
						        //getting notification
								finish();
								Intent in = new Intent(getApplicationContext(), MainActivity.class);

	        					startActivity(in);
	        					
	        					
	        		        	mensaje = "Comentario enviado";
	        		            handler.post(toast);
	
								
							}else{
	        		        	mensaje = "Ha ocurrido un error, avisa por correo a tu profesor";
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
