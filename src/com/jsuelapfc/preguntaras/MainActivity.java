package com.jsuelapfc.preguntaras;

import static com.jsuelapfc.preguntaras.CommonUtilities.SENDER_ID;
import android.app.NotificationManager;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;

public class MainActivity extends TabActivity {
	
	
    private SharedPreferences prefs;
    private String asignatura;

    
    
    @Override 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 	    requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.main);
 
        //GCM check device y checkmanifest
        GCMRegistrar.checkDevice(this);
        GCMRegistrar.checkManifest(this);
        
        //Si no estamos registrados, nos registramos en GCM
        final String regId = GCMRegistrar.getRegistrationId(MainActivity.this);
        if (regId.equals("")) {
        	Toast.makeText(MainActivity.this,"resgistrando ", Toast.LENGTH_SHORT).show();
            GCMRegistrar.register(MainActivity.this, SENDER_ID); //Sender ID
        } else {
            Log.v("main activity", "Ya registrado");
        }
        
 
        Intent in = getIntent();
        
    	String TAG_FIELDS_ASIGNATURA = "asignatura";
        String cambiaAsignatura = in.getStringExtra(TAG_FIELDS_ASIGNATURA);

        //almacenamos la asignatura en shared preferences si cambia, es decir, si es distinto de null
        if (cambiaAsignatura!=null){

    		prefs = PreferenceManager.getDefaultSharedPreferences(this);
			Editor edit = prefs.edit();
			edit.putString("subject", cambiaAsignatura);
			edit.commit();
        	
        }
 
    	String TAG_FIELDS_NOTIFY = "notify";
        String notify = in.getStringExtra(TAG_FIELDS_NOTIFY);

        
		NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        //Toast.makeText(MainActivity.this,"nm es "+ nm., Toast.LENGTH_LONG).show();
		//1 es el ID de la notificacion
		nm.cancel(1);
		nm.cancel(2);
		nm.cancel(3);
		nm.cancel(4);
        
        
		final TabHost tabHost = getTabHost();
		
		tabHost.addTab(tabHost.newTabSpec("Ranking")
	            .setIndicator("Ranking", 
	            this.getResources().getDrawable(R.drawable.ic_action_ranking))
	            .setContent(new Intent(this, Ranking.class)));
		tabHost.addTab(tabHost.newTabSpec("Preguntas")
	            .setIndicator("Pendiente", 
	            this.getResources().getDrawable(R.drawable.ic_action_question))
	            .setContent(new Intent(this, QuestionsList.class)));	
		tabHost.addTab(tabHost.newTabSpec("Listado")
	            .setIndicator("Realizado", 
	            this.getResources().getDrawable(R.drawable.ic_action_done))
	            .setContent(new Intent(this, Listing.class)));	
		tabHost.addTab(tabHost.newTabSpec("Tips")
	            .setIndicator("Avisos", 
	            this.getResources().getDrawable(R.drawable.ic_action_tips))
	            .setContent(new Intent(this, Tips.class)));	
		tabHost.addTab(tabHost.newTabSpec("Refresh")
	            .setIndicator("Actualizar", 
	            this.getResources().getDrawable(R.drawable.ic_action_example))
	            .setContent(new Intent(this, Refresh.class)));

		if (notify==null){
            tabHost.setCurrentTab(0);
		}else if(notify.equals("4")){
            tabHost.setCurrentTab(1);
        }else if(notify.equals("5")){
        	tabHost.setCurrentTab(3);
        }else if(notify.equals("1")){
        	tabHost.setCurrentTab(1);
        }else if(notify.equals("2")){
        	tabHost.setCurrentTab(2);
        } else {
            tabHost.setCurrentTab(0);
        }
        
        Button buttonSteps = (Button) findViewById(R.id.steps);
    	buttonSteps.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent in = new Intent(getApplicationContext(), Steps.class);
                startActivity(in);
            	
            }
    	});
            
        Button currentSubject = (Button) findViewById(R.id.currentsubject);
        //obtengo el nombre de la asignatura
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
        asignatura = prefs.getString("subject", "n/a");
        currentSubject.setText(asignatura);
        currentSubject.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent in = new Intent(getApplicationContext(), Subjects.class);
                startActivity(in);
                finish();
            	
            }
            

    	});
        Button buttonSubjects = (Button) findViewById(R.id.subjects);
    	buttonSubjects.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent in = new Intent(getApplicationContext(), SubjectsRegister.class);
                startActivity(in);
            	
            }
    	});
        
        
    }
    
}



