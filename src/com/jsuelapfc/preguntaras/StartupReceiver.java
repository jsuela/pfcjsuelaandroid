package com.jsuelapfc.preguntaras;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartupReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Intent serviceIntent = new Intent();
		serviceIntent.setAction(MiServicioPreguntas.class.getName());
		context.startService(serviceIntent);
	}
	
}
