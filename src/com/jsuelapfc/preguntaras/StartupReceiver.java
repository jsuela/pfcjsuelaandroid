package com.jsuelapfc.preguntaras;


import java.util.Calendar;
import java.util.TimeZone;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import java.util.GregorianCalendar;


public class StartupReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("StartupReceiver", "entor en oncreate");

	    Intent service = new Intent(context, MiServicioPreguntas.class);
	    context.startService(service);
		
	}
	
/*	 // Restart service every 30 seconds
	  private static final long REPEAT_TIME = 1000 * 5;

	  @Override
	  public void onReceive(Context context, Intent intent) {
		  /*
	    AlarmManager service = (AlarmManager) context
	        .getSystemService(Context.ALARM_SERVICE);
	    Intent i = new Intent(context, MiServicioPreguntas.class);
	    PendingIntent pending = PendingIntent.getBroadcast(context, 0, i,
	        PendingIntent.FLAG_CANCEL_CURRENT);
	    Calendar cal = Calendar.getInstance();
	    // Start 30 seconds after boot completed
	    cal.add(Calendar.SECOND, 30);
	    //
	    // Fetch every 30 seconds
	    // InexactRepeating allows Android to optimize the energy consumption
	    service.setInexactRepeating(AlarmManager.RTC_WAKEUP,
	        cal.getTimeInMillis(), REPEAT_TIME, pending);

	    // service.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
	    // REPEAT_TIME, pending);
		  
	        Intent in = new Intent(context, MiServicioPreguntas.class);
	        PendingIntent pintent = PendingIntent.getService(context, 0, intent, 0);

	        AlarmManager alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
			Calendar cal1 = new GregorianCalendar(TimeZone.getTimeZone("GMT")); 

			// Start every 30 seconds
	        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal1.getTimeInMillis(), 5*1000, pintent); 
	  }*/
	
}
