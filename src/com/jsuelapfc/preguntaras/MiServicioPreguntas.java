package com.jsuelapfc.preguntaras;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

public class MiServicioPreguntas extends Service {
	private Timer timer = new Timer();
	private static final long UPDATE_INTERVAL = 5000;
	private final IBinder mBinder = new MyBinder();
	private ArrayList<String> list = new ArrayList<String>();

	public String mensaje;
	public String app;
	public int contadorAppsOciosas;
	public int limiteOcio = 2;
	
	private static final int ID_NOTIFICATION1 = 1;
	private static final int ID_NOTIFICATION2 = 2;
	
	final Handler handler = new Handler();
	
	private String loginusuario;
    private SharedPreferences prefs;
    
	// url to make request
	private static String url;
	
	//segundo timer
	private Timer timer2 = new Timer();
	//24horas
	private static final long UPDATE_INTERVAL2 = 86400000;
	public String mensaje2;
	public int limitePreguntasAlDia = 6;
	public int numeroPreguntasRealizadas=0;
	

	public void onCreate() {
		super.onCreate();
		pollForUpdates();
		//controlaremos el min y max de preguntas
		checkNumberOfQuestions();
		
	}
	public String miraTasks(){
		
    	ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
		// get the info from the currently running task
		List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);

		Log.i("Service LOG", "CURRENT Activity ::"
		+ taskInfo.get(0).topActivity.getClassName());


		//ComponentName componentInfo = taskInfo.get(0).topActivity;
		return taskInfo.get(0).topActivity.getClassName();
	}

	public void lanzaNotificacion(){
		String resultado;
		
		//primero pido pregunta al servidor, luego lanzo notificacion
        // getting new question
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        loginusuario = prefs.getString("username", "n/a");
        url = "http://10.0.2.2:1234/android/sumapregunta/"+loginusuario;	
		try {
			HttpClient client = new DefaultHttpClient();
			String getURL = url;
			HttpGet get = new HttpGet(getURL);
			HttpResponse responseGet = client.execute(get);
			HttpEntity resEntityGet = responseGet.getEntity();
			if (resEntityGet != null) {
				resultado = EntityUtils.toString(resEntityGet);
				if (resultado.equals("ok")){
			        //getting notification
					NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
					Notification notification = new Notification(R.drawable.home, "Tienes preguntas por responder", System.currentTimeMillis());
					//hacemos que la notificacion no se borre hasta que se abra la app
					notification.flags |= Notification.FLAG_NO_CLEAR;
					PendingIntent actividad = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class),0);
					notification.setLatestEventInfo(this, "Tienes preguntas nuevas", "Ya dedicaste mucho tiempo al ocio...", actividad);
					nm.notify(ID_NOTIFICATION1, notification);
					Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.jsuelapfc.preguntaras");
					startActivity(launchIntent);
				}else{
			        //getting notification
					NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
					Notification notification = new Notification(R.drawable.home, "Deberías estar estudiando", System.currentTimeMillis());
					//hacemos que la notificacion no cree nuevo intent, y que se borre en cuanto se pulse
					notification.flags |= Notification.FLAG_AUTO_CANCEL;
					PendingIntent actividad = PendingIntent.getActivity(this, 0,  new Intent(),0);
					notification.setLatestEventInfo(this, "¡ESTUDIA!", "Sólo es un consejo :). Ánimo", actividad);
					nm.notify(ID_NOTIFICATION2, notification);

				}
			} else {

				Log.i("ERROR", "no ok");

			}
		} catch (Exception e) {
			Log.i("ERROR", "CONECTION PROBLEM");
		}
        

	}
	
	private void checkNumberOfQuestions() {
		timer2.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				//cada 24 horas reinicio para controlar el max preguntas al dia y el minimo
				if (numeroPreguntasRealizadas == 0){
					lanzaNotificacion();
					numeroPreguntasRealizadas++;			
				}else{
					numeroPreguntasRealizadas=0;
	    			contadorAppsOciosas=0;
	    			Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT")); 
	    			//cal.set(year + 1900, month, day, hour, minute, second); cal.getTime().getTime();
	    			cal.getTime();
	    			Log.i("Timer2 Hora", cal.getTime().toGMTString());
				}
			}
	     //este timer comenzará a funcionar cuando pasen 24horas
		}, 86400000, UPDATE_INTERVAL2);
		Log.i(getClass().getSimpleName(), "Timer2 started.");

	}
	
	
	private void pollForUpdates() {
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
	        	String tarea = miraTasks();
	    		Log.i("NQUEST", "numeropregrealizadas"
	    				+ numeroPreguntasRealizadas);

	    		/*mensaje = tarea.split("[.]")[1];*/
	    		/* Pra mostrar en un toast la tarea
	    		handler.post(toast);*/
	    		app = tarea.split("[.]")[1];
	    		if ((app.equals("facebook")) || (app.equals("instagram")) || (app.equals("clau"))|| (app.equals("twitter"))){
	    			contadorAppsOciosas++;
	    		}
	    		if ((contadorAppsOciosas == limiteOcio) && (numeroPreguntasRealizadas < limitePreguntasAlDia)){
	    			lanzaNotificacion();
	    			contadorAppsOciosas=0;
	    			numeroPreguntasRealizadas++;
	    		}
	    			
	        	

			}

		}, 0, UPDATE_INTERVAL);
		Log.i(getClass().getSimpleName(), "Timer started.");

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (timer != null) {
			timer.cancel();
		}
		Log.i(getClass().getSimpleName(), "Timer stopped.");

	}

	
	@Override
	public IBinder onBind(Intent arg0) {
		Log.i("Service JAVI", "entro en IBinder onBind");
		return mBinder;
	}

	public class MyBinder extends Binder {
		MiServicioPreguntas getService() {
			Log.i("Service JAVI", "entro en Mybinder binder");
			return MiServicioPreguntas.this;
		}
	}

	public List<String> getWordList() {
		return list;
	}
}
