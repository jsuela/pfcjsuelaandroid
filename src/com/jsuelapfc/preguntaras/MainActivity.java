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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;

public class MainActivity extends TabActivity {
	
	
    private SharedPreferences prefs;
    private String asignatura;

    
    
    @Override 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 	    requestWindowFeature(Window.FEATURE_NO_TITLE);
 	    //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.main);
        
        
        


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
        
        
        

        

 	    //Quitamos la barra de android donde muestra la cobertura, batería, etc..          
 	    //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //setContentView(R.layout.main);
		
		//comprobamos las notificaciones
        //si hay entonces miraremos cual pesgtaña es la que se abre

        /*CharSequence notify = "";
        //hay q utilizar extras para abrir ciertas pestañas
        if (getIntent().getExtras() != null) {
            Bundle b = getIntent().getExtras();
            notify = b.getCharSequence("notify");
        }
        */
        // obtenemos intent data para ver si hay q abrir alguna pestaña en particular
        //o para cambiar de asignatura
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


		/*tabHost.addTab(tabHost.newTabSpec("PreferencesDemo")
	            .setIndicator("Consumidor", 
	            this.getResources().getDrawable(R.drawable.settings))
	            .setContent(new Intent(this, Consumer.class)));	*/
		//tabHost.computeScroll();	
		//para que se situe en el primero, es decir, en ranking
       // int pos = getIntent().getIntExtra("POSICION", 0); 
        //tabHost.setCurrentTab(pos);
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
            	editor.putString("subject", "n/a");
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



