/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jsuelapfc.preguntaras;

import static com.jsuelapfc.preguntaras.CommonUtilities.SENDER_ID;

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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gcm.GCMBaseIntentService;

/**
 * IntentService responsible for handling GCM messages.
 */


public class GCMIntentService extends GCMBaseIntentService {

    @SuppressWarnings("hiding")
    private static final String TAG = "GCMIntentService";
    private SharedPreferences prefs;
	private String loginusuario;
	private String mensaje;
	private final Handler handler = new Handler();

    public GCMIntentService() {
        super(SENDER_ID);
    }
    
    protected void onRegistered(Context context, String registrationId) {
    	Toast.makeText(this,"android/gcm/registro onregistered ", Toast.LENGTH_SHORT).show();
        Log.d("GCMINtentService", "REGISTRATION: Registrado OK.");
        
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
		loginusuario = prefs.getString("username", "n/a");
          
        registroServidor(loginusuario, registrationId);
    }
    
    
	private void registroServidor(String usuario, String regId)
	{

       String csrf = null;
	   DefaultHttpClient httpclient = new DefaultHttpClient();
		   
		    try {
		    	HttpGet httpget = new HttpGet("http://pfc-jsuelaplaza.libresoft.es/android/gcm/registro");
		           Toast.makeText(this,"android/gcm/registro ", Toast.LENGTH_SHORT).show();
		    	HttpResponse response = httpclient.execute(httpget);
	
		    	Header[] headers = response.getAllHeaders();
		    	
		        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
		    	
		    	for (int i = 0; i < headers.length; i++){
		    		if (headers[i].toString().contains("csrftoken")){
		    			csrf=headers[i].toString();
		    			csrf = csrf.replace("Set-Cookie:","");
		    			csrf = csrf.replace(" ","");
		    			csrf = csrf.replace(";expires","");		    			

	         			nameValuePairs.add(new BasicNameValuePair("user", usuario));
	         			nameValuePairs.add(new BasicNameValuePair("codigoGCM", regId));

    			    	nameValuePairs.add(new BasicNameValuePair("csrfmiddlewaretoken", csrf.split("=")[1]));

		    		}
		    	} 
    			    	
				HttpPost httppost = new HttpPost("http://pfc-jsuelaplaza.libresoft.es/android/gcm/registro");

		        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		        
		        response = httpclient.execute(httppost);
 			
 			if (response.getStatusLine().getStatusCode() == 500)
     	    	mensaje = "GCM No ok";
 			else if (response.getStatusLine().getStatusCode() == 200){
 				
 				mensaje = "GCM Correcto";
 
 			}else
 				mensaje = "GCM NO OK";
 			 	
		    } catch (Exception e) {
		     	mensaje = "No se puede contactar con el servidor";
		         e.printStackTrace();
		         handler.post(toast);
		     
		
			}

		
	}
 	final Runnable toast = new Runnable(){
 		public void run(){
     		Toast.makeText(GCMIntentService.this,
     				mensaje,
     				Toast.LENGTH_SHORT).show();

 		}
 	};
	
	@Override
	protected void onUnregistered(Context context, String regId) {
	    Log.d("GCMTest", "REGISTRATION: Desregistrado OK.");
	}
	 
	@Override
	protected void onError(Context context, String errorId) {
	    Log.d("GCMTest", "REGISTRATION: Error -> " + errorId);
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		//extraemos el parámetro data.
	    String msg = intent.getExtras().getString("msg");
	    Log.d("GCMTest", "Mensaje: " + msg);
	    mostrarNotificacion(context, msg);
	}

    
    private void mostrarNotificacion(Context context, String msg)
    {
        //Obtenemos una referencia al servicio de notificaciones
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager notManager =
            (NotificationManager) context.getSystemService(ns);
     
        //Configuramos la notificación
        int icono = R.drawable.ic_launcher;
        CharSequence textoEstado = "¡Alerta!";
        long hora = System.currentTimeMillis();
     
        Notification notif =
            new Notification(icono, textoEstado, hora);
     
        //Configuramos el Intent
        Context contexto = context.getApplicationContext();
        CharSequence titulo = "Nuevo aviso";
        CharSequence descripcion = msg;
     
        Intent notIntent = new Intent(this,
            MainActivity.class);
        
	    //Bundle b = new Bundle();
        System.out.println(msg);
        if (msg.contains("Te ha enviado una pregunta")){
        	//el mensaje viene asi: asigantura += Te ha enviado una pregunta + usuario
        	String msgAsignatura = msg.split("=")[0];
        	descripcion= msg.split("=")[1];
        	//notificacion de pregunta
        	notIntent.putExtra("asignatura", msgAsignatura);
        	notIntent.putExtra("notify", "4");
        }else{
        	String msgAsignatura2 = msg.split("=")[0];
        	descripcion= msg.split("=")[1];
        	notIntent.putExtra("asignatura", msgAsignatura2);
		    notIntent.putExtra("notify", "5");
        }
        
        PendingIntent actividad = PendingIntent.getActivity(this, 0, notIntent,0);
     
        notif.setLatestEventInfo(
            contexto, titulo, descripcion, actividad);
     
        //AutoCancel: cuando se pulsa la notificaión ésta desaparece
        notif.flags |= Notification.FLAG_AUTO_CANCEL;
		notif.defaults |= Notification.DEFAULT_SOUND;
		notif.defaults |= Notification.DEFAULT_VIBRATE;
		notif.defaults |= Notification.DEFAULT_LIGHTS;
     
        //Enviar notificación
        notManager.notify(4, notif);
       
    }
}
