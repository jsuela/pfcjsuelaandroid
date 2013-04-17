package com.jsuelapfc.preguntaras;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Subjects extends ListActivity {
	
	// url to make request
	private static String url;
	private static String url2;
	private static String url3;
	private static String subject;

	 
	// JSON Node names
	private static final String TAG_ASIGNATURASALUMNO = "asignaturasalumno";
	private static final String TAG_PK = "pk";
	private static final String TAG_MODEL = "model";

	private static final String TAG_FIELDS = "fields";
	private static final String TAG_FIELDS_ASIGNATURA = "asignatura";
	
	// JSON Node names2
	private static final String TAG_ASIGNATURAS = "asignaturas";
	//private static final String TAG_PK = "pk";
	//private static final String TAG_MODEL = "model";

	//private static final String TAG_FIELDS = "fields";
	//private static final String TAG_FIELDS_ASIGNATURA = "asignatura";

	

	private JSONParser jParser;


	private ArrayList<HashMap<String, String>> asignaturasalumnoList;


	// contacts JSONArray
	private JSONArray asignaturasalumno = null;


	
	private ProgressDialog pd;
	
	private String mensaje;
	private final Handler handler = new Handler();
    private Button irA;
    private Button irMatricula;

    private Button btnDisplayirMatricula;
    private Button btnDisplayirA;
    
    private ListAdapter adapter2;
    private ListAdapter adapter3;
    
    private ListView lv;
    private ListView lv2;
    private TextView tv;


    
	private String loginusuario;
    private SharedPreferences prefs;
    private String resultado;
    
	/*private int nPreguntasEnviadasAmigos;
	private Editor edit;
	private int limitePreguntasEnviadasAmigos = 10;*/


	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 	    requestWindowFeature(Window.FEATURE_NO_TITLE);
 	    //para que no rote setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.mysubjects);
        
        lv = (ListView)findViewById(android.R.id.list);
		tv = (TextView)findViewById(R.id.titulo);
		tv.setText("Carga los datos de una de las asignaturas matriculadas");

        
        // Hashmap for ListView
        asignaturasalumnoList = new ArrayList<HashMap<String, String>>();

 
        // Creating JSON Parser instance
        jParser = new JSONParser();


 
        
        prefs = PreferenceManager.getDefaultSharedPreferences(Subjects.this);
        loginusuario = prefs.getString("username", "n/a");
        // getting JSON string from URL
        url = "http://pfc-jsuelaplaza.libresoft.es/android/asignaturas/listado/"+loginusuario;	
    	pd = ProgressDialog.show(Subjects.this, "Preguntas", "Cargando...", true, false);	

	    new MiTarea().execute(url);
    }
	
	
    private class MiTarea extends AsyncTask<String, ListAdapter, ArrayList<HashMap<String, String>> >{


        protected ArrayList<HashMap<String, String>> doInBackground(String... urls) {


		        try {
			        JSONObject json = jParser.getJSONFromUrl(url);
			
		            // Getting Array of Contacts
		            asignaturasalumno = json.getJSONArray(TAG_ASIGNATURASALUMNO);
		 
		            // looping through All Contacts
		            for(int i = 0; i < asignaturasalumno.length(); i++){
		                JSONObject c = asignaturasalumno.getJSONObject(i);
		 
		                // Storing each json item in variable
		                String pk = c.getString(TAG_PK);
		                String model = c.getString(TAG_MODEL);
		         
		                // Respuestas is agin JSON Object
		                JSONObject fields = c.getJSONObject(TAG_FIELDS);
		                String asignaturas = fields.getString(TAG_FIELDS_ASIGNATURA);  
   	    
		  	                
		                
		                // creating new HashMap
		                HashMap<String, String> map = new HashMap<String, String>();
		 
		                // adding each child node to HashMap key => value
		                //muestro todos menos yo

		                map.put(TAG_PK, pk);
		                map.put(TAG_MODEL, model);
		                map.put(TAG_FIELDS_ASIGNATURA, asignaturas);
		                asignaturasalumnoList.add(map); 

		            } 
		        } catch (Exception e) {
  				mensaje = "No se puede conectar con el servidor. Inténtelo más tarde";
  			 
  				handler.post(toast);
		            e.printStackTrace();
		        }	
				return asignaturasalumnoList;
        }

    protected void onPostExecute(ArrayList<HashMap<String, String>> result ) {
	        //Damos nombre al botón
	        //el siguiente método se ejecutará cuando se presione el botón
    		//irA = (Button) findViewById(R.id.irA);
  	        //irA.setText("Cargar datos asignatura");
	        //addListenerOnButton();
	        
    		irMatricula = (Button) findViewById(R.id.matricularse);
    		irMatricula.setText("Quiero matricularme en asignaturas");
	        addListenerOnButton2();
	        
	        
	        
	        /*lv.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_single_choice,s));
	        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);   */
	        
	        
    	
	        adapter2 = new SimpleAdapter(Subjects.this, asignaturasalumnoList,
    		  android.R.layout.simple_list_item_1,
      		new String[] { TAG_FIELDS_ASIGNATURA}, new int[] {
    		  android.R.id.text1});
	        
      
      
      
	        lv.setAdapter(adapter2);
	        lv.setOnItemClickListener(new OnItemClickListener() {
	        	
	            @Override
	            public void onItemClick(AdapterView<?> parent, View view,
	                    int position, long id) {
	                    //getting values from selected ListItem
                    Intent in = new Intent(getApplicationContext(), MainActivity.class);
                    String name= ((TextView) view.findViewById(android.R.id.text1)).getText().toString();
                    //Una vez que tenga todo descomentar la siguiente linea
                    in.putExtra(TAG_FIELDS_ASIGNATURA, name);
                    System.out.println("***********la asignaturaes:"+name);
                    startActivity(in);
                    finish();
	        }
	        });
        
  	 pd.dismiss();
    }
    }
    
	 /*public void addListenerOnButton() {
		 
			btnDisplayirA = (Button) findViewById(R.id.irA);
		 
			btnDisplayirA.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					//do osomething
				}
		 


	         	});
		 
			};*/
			
		 public void addListenerOnButton2() {
			 
				btnDisplayirMatricula = (Button) findViewById(R.id.matricularse);
			 
				btnDisplayirMatricula.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
		                Intent in = new Intent(getApplicationContext(), SubjectsRegister.class);
		                startActivity(in);
		                finish();
					}
			 


		         	});
			 
				};
 
  	final Runnable toast = new Runnable(){
 		public void run(){
     		Toast.makeText(Subjects.this,
     				mensaje,
     				Toast.LENGTH_LONG).show();

 		}
 	};

}
