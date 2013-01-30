package com.jsuelapfc.preguntaras;

import com.google.android.gcm.GCMRegistrar;
import static com.jsuelapfc.preguntaras.CommonUtilities.SENDER_ID;

import android.app.NotificationManager;
import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TabHost;
import android.widget.Toast;

public class MainActivity extends TabActivity {
	
	
    private SharedPreferences prefs;

    
    
    @Override 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //GCM check device y checkmanifest se puede borrar antes de publicar
        GCMRegistrar.checkDevice(this);
        GCMRegistrar.checkManifest(this);
        
        //Si no estamos registrados, nos registramos en GCM
        final String regId = GCMRegistrar.getRegistrationId(MainActivity.this);
        if (regId.equals("")) {
        	Toast.makeText(MainActivity.this,"resgistrando ", Toast.LENGTH_SHORT).show();
            GCMRegistrar.register(MainActivity.this, SENDER_ID); //Sender ID
        } else {
            Log.v("main activity", "Ya registrado");
            Toast.makeText(MainActivity.this,"ya registradooo ", Toast.LENGTH_SHORT).show();
        }
        
        
        //setContentView(R.layout.activity_main);
        
        
 	    requestWindowFeature(Window.FEATURE_NO_TITLE);
 	    //Quitamos la barra de android donde muestra la cobertura, batería, etc..          
 	    //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //setContentView(R.layout.main);
		
		//comprobamos las notificaciones
		NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		//1 es el ID de la notificacion
		nm.cancel(1);
		nm.cancel(2);
        
        
		final TabHost tabHost = getTabHost();
		
		tabHost.addTab(tabHost.newTabSpec("Ranking")
	            .setIndicator("Ranking", 
	            this.getResources().getDrawable(R.drawable.ic_action_ranking))
	            .setContent(new Intent(this, Ranking.class)));
		tabHost.addTab(tabHost.newTabSpec("Preguntas")
	            .setIndicator("To do", 
	            this.getResources().getDrawable(R.drawable.ic_action_question))
	            .setContent(new Intent(this, QuestionsList.class)));	
		tabHost.addTab(tabHost.newTabSpec("Listado")
	            .setIndicator("Done", 
	            this.getResources().getDrawable(R.drawable.ic_action_done))
	            .setContent(new Intent(this, Listing.class)));	
		tabHost.addTab(tabHost.newTabSpec("Tips")
	            .setIndicator("Tips", 
	            this.getResources().getDrawable(R.drawable.ic_action_tips))
	            .setContent(new Intent(this, Tips.class)));	
		tabHost.addTab(tabHost.newTabSpec("Refresh")
	            .setIndicator("Refresh", 
	            this.getResources().getDrawable(R.drawable.ic_action_example))
	            .setContent(new Intent(this, Refresh.class)));


		/*tabHost.addTab(tabHost.newTabSpec("PreferencesDemo")
	            .setIndicator("Consumidor", 
	            this.getResources().getDrawable(R.drawable.settings))
	            .setContent(new Intent(this, Consumer.class)));	*/
		tabHost.computeScroll();	
        
        
        
        
    }
    
 // Añadiendo las opciones de menú
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main, menu);
        return true;
    }
    
 // Añadiendo funcionalidad a las opciones de menú
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       switch (item.getItemId()) {
            case R.id.btLogout:
            	//eliminamos de Shared preferences el usuario
            	
        		prefs = PreferenceManager.getDefaultSharedPreferences(this);
            	SharedPreferences.Editor editor = prefs.edit();
            	editor.putString("username", "n/a");
            	editor.commit();
            	
                //Si estamos registrados --> Nos des-registramos en GCM
                final String regId = GCMRegistrar.getRegistrationId(MainActivity.this);
                if (!regId.equals("")) {
                    GCMRegistrar.unregister(MainActivity.this);
                } else {
                    Log.v("Main Activity", "Ya desregistrado");
                }
            
               Toast.makeText(getApplicationContext(), "Cerrando sesión", 
                     Toast.LENGTH_SHORT).show();
               finish();
               return true;
            default:
               return super.onOptionsItemSelected(item);
        }
    }
    
}



