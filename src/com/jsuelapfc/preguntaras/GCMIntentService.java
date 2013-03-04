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
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

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
        //        ServerUtilities.register(context, registrationId);
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
		    			System.out.println("entras aqui?");
		    			/*csrf = headers[i].toString().split(":")[2];
		    			csrf = csrf.replace("}","");*/
		    			/*csrf = headers[i].toString().split(" ")[2];
		    			System.out.println("el csrf0000 es:"+ csrf);
		    			csrf = csrf.replace(";","");

		    			System.out.println("el csrf1 es:"+ csrf.split("=")[1]);*/
		    			
		    			//para version apache
		    			/*csrf = headers[i].toString().split(" ")[1];
		    			csrf = csrf.replace(";","");*/
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
		    	//nameValuePairs.add(new BasicNameValuePair("csrfmiddlewaretoken", csrf.split("=")[1]));

		        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		        
		        response = httpclient.execute(httppost);
		        

 			
 			if (response.getStatusLine().getStatusCode() == 500)
     	    	mensaje = "GCM No ok";
 			else if (response.getStatusLine().getStatusCode() == 200){
 				
 				mensaje = "GCM Correcto";
 
 			}else
 				mensaje = "GCM NO OK";
 			 
 			handler.post(toast);
 			
		    /*} catch (ClientProtocolException e) {

		    } catch (IOException e) {*/



		
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

   /* @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");
        displayMessage(context, getString(R.string.gcm_unregistered));
        if (GCMRegistrar.isRegisteredOnServer(context)) {
            ServerUtilities.unregister(context, registrationId);
        } else {
            // This callback results from the call to unregister made on
            // ServerUtilities when the registration to the server failed.
            Log.i(TAG, "Ignoring unregister callback");
        }
    }*/

    /*@Override
    protected void onMessage(Context context, Intent intent) {
        Log.i(TAG, "Received message");
        String message = getString(R.string.gcm_message);
        displayMessage(context, message);
        // notifies user
        generateNotification(context, message);
    }*/
	@Override
	protected void onMessage(Context context, Intent intent) {
		//extraemos el parámetro data.
	    String msg = intent.getExtras().getString("msg");
	    Log.d("GCMTest", "Mensaje: " + msg);
	    mostrarNotificacion(context, msg);
	}

    /*@Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        displayMessage(context, message);
        // notifies user
        generateNotification(context, message);
    }*/
    
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
     
        Intent notIntent = new Intent(contexto,
            GCMIntentService.class);
     
        PendingIntent actividad = PendingIntent.getActivity(this, 0, new Intent(this, Tips.class),0);
     
        notif.setLatestEventInfo(
            contexto, titulo, descripcion, actividad);
     
        //AutoCancel: cuando se pulsa la notificaión ésta desaparece
        notif.flags |= Notification.FLAG_AUTO_CANCEL;
     
        //Enviar notificación
        notManager.notify(3, notif);
        
        
        
    }

    /*@Override
    public void onError(Context context, String errorId) {
        Log.i(TAG, "Received error: " + errorId);
        displayMessage(context, getString(R.string.gcm_error, errorId));
    }*/

   /* @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
        displayMessage(context, getString(R.string.gcm_recoverable_error,
                errorId));
        return super.onRecoverableError(context, errorId);
    }*/

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
   /* private static void generateNotification(Context context, String message) {
        int icon = R.drawable.ic_stat_gcm;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);
        String title = context.getString(R.string.app_name);
        Intent notificationIntent = new Intent(context, DemoActivity.class);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, title, message, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notification);
    }*/

}
