package com.jsuelapfc.preguntaras;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class Ranking extends ListActivity{	
	
	// url to make request
	private static String url;
	 
	// JSON Node names
	private static final String TAG_PUNTOS = "puntos";
	private static final String TAG_PK = "pk";
	private static final String TAG_MODEL = "model";

	private static final String TAG_FIELDS = "fields";
	private static final String TAG_FIELDS_PUNTOS = "puntos";
	private static final String TAG_FIELDS_USUARIO = "usuario";

	//servicio
	/*private MiServicioPreguntas s;
	private ArrayList<String> values;
	private ArrayAdapter<String> adapter;*/
	private JSONParserPOST jParser;
    static InputStream is = null;
	private ArrayList<HashMap<String, String>> puntosList;

	// contacts JSONArray
	private JSONArray puntos = null;
	
	private ProgressDialog pd;
	
	private String mensaje;
	private final Handler handler = new Handler();
    private Context mContext;
    
    private Button lblEnvResp;
    private Button btnDisplay;
    
	private String asignatura;
    private SharedPreferences prefs;
	private String loginusuario;
	
	
	   @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.ranking);
	 	    //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

	        mContext = this;

	        //Llamamos al servicio
				        
	        Intent intent = new Intent(this, MiServicioPreguntas.class);
	        PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);

	        AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
			Calendar cal1 = new GregorianCalendar(TimeZone.getTimeZone("GMT")); 

			// Start every 60 seconds
	        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal1.getTimeInMillis(), 60*1000, pintent); 
	 
	        // Hashmap for ListView
	        puntosList = new ArrayList<HashMap<String, String>>();
	 
	        // Creating JSON Parser instance
	        jParser = new JSONParserPOST();
	 
	        // getting JSON string from URL
	        //url = "http://193.147.51.87:1235/android/clasificacion";	
	        url = "http://pfc-jsuelaplaza.libresoft.es/android/clasificacion";	
	        
	    	pd = ProgressDialog.show(Ranking.this, "Preguntas", "Cargando...", true, false);	

		    new MiTarea().execute(url);
	   }
	   
	     private class MiTarea extends AsyncTask<String, ListAdapter, ArrayList<HashMap<String, String>> >{


	          protected ArrayList<HashMap<String, String>> doInBackground(String... urls) {

			        try {
			        	
	        	
			        	
			        	

                 	    String csrf = null;
				        	HttpParams params = new BasicHttpParams();
				        	HttpProtocolParams.setContentCharset(params, "utf-8");
	              			DefaultHttpClient httpclient = new DefaultHttpClient(params);
						 

           			    	HttpGet httpget = new HttpGet(url);
           			    	HttpResponse response = httpclient.execute(httpget);
               	
           			    	Header[] headers = response.getAllHeaders();
           			    	

           			    	//para imprimir por pantalla
           			    	HttpEntity resEntityGet2 = response.getEntity();
           			    	String resultado2 = EntityUtils.toString(resEntityGet2);
           			    	System.out.println("*A*A*A"+ resultado2);
           			    	
           			    	//Log.e("*A*A*A", "Error" + resultado2);
           			    	//borrar
           			    	
        			        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

           			    	
           			    	for (int i = 0; i < headers.length; i++){	
       			    			
           			    		if (headers[i].toString().contains("csrftoken")){
           			    			System.out.println("cabeceraaaa:"+headers[i].toString());
           			    			csrf=headers[i].toString();
           			    			csrf = csrf.replace("Set-Cookie:","");
           			    			csrf = csrf.replace(" ","");
           			    			csrf = csrf.replace(";expires","");
           			    			System.out.println("*C*C*C*C*C*C el csrf111111nuevo es:"+ csrf.split("=")[1]);

           			    			
     
           			    			//System.out.println("CSSSSRF:"+ csrf.split("=")[1]);
                        			//obtengo el nombre de la asignatura
           			    	        prefs = PreferenceManager.getDefaultSharedPreferences(Ranking.this);
           			    	        asignatura = prefs.getString("subject", "n/a");

           			    			
                        			nameValuePairs.add(new BasicNameValuePair("asignatura", asignatura));
                   			    	nameValuePairs.add(new BasicNameValuePair("csrfmiddlewaretoken", csrf.split("=")[1]));

           			    		}
           			    	} 
	               			    	
        					HttpPost httppost = new HttpPost(url);
           			    	//nameValuePairs.add(new BasicNameValuePair("csrfmiddlewaretoken", csrf.split("=")[1]));

        			        
        			        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
        			        
        			        response = httpclient.execute(httppost);
        			        HttpEntity resEntityGet = response.getEntity();
        			        is = resEntityGet.getContent();
        			        //System.out.println("*****R*R*R*R* is es:"+is.toString());
        			        

           			    	//para imprimir por pantalla si hay error
           			    	//HttpEntity resEntityGet3 = response.getEntity();
           			    	//String resultado3 = EntityUtils.toString(resEntityGet);
           			    	//System.out.println("*A*A*A"+ resultado3);
           			    	//Log.e("*A*A*A", "Error" + resultado2);
           			    	//borrareste trozo

        			        
        			        
                			
							try{	
							    JSONObject json = jParser.getJSONFromResponse(is);
							    // Getting Array of Contacts
							    puntos = json.getJSONArray(TAG_PUNTOS);
 
							    // looping through All Contacts
							    for(int i = 0; i < puntos.length(); i++){
							        JSONObject c = puntos.getJSONObject(i);
 
							        // Storing each json item in variable
							        String pk = c.getString(TAG_PK);
							        String model = c.getString(TAG_MODEL);
							 
							        // Respuestas is agin JSON Object
							        JSONObject fields = c.getJSONObject(TAG_FIELDS);
							        String puntos = fields.getString(TAG_FIELDS_PUNTOS);  
							        String usuario = fields.getString(TAG_FIELDS_USUARIO);	    	    
							            
							        
							        // creating new HashMap
							        HashMap<String, String> map = new HashMap<String, String>();
 
							        // adding each child node to HashMap key => value
							        map.put(TAG_PK, pk);
							        map.put(TAG_MODEL, model);
							        map.put(TAG_FIELDS_PUNTOS, "Puntos: "+puntos);
							        map.put(TAG_FIELDS_USUARIO, usuario);
							    	  
							        // adding HashList to ArrayList
							       puntosList.add(map); 
							    } 
							} catch (Exception e) {
								mensaje = "No se puede conectar con el servidor. Inténtelo más tarde";
							 
								handler.post(toast);
							    e.printStackTrace();
							}

							
							
						} catch (Exception e) {
							Log.i("ERROR", "CONECTION PROBLEM");

        		        	mensaje = "Imposible contactar con el servidor";
        		            handler.post(toast);
		
						}
					    pd.dismiss();
					    
						return puntosList;
	    			
	
	

	          }
	     

          protected void onPostExecute(ArrayList<HashMap<String, String>> result ) {
        	  
  	        //Damos nombre al botón
  	        //el siguiente método se ejecutará cuando se presione el botón
  	        lblEnvResp = (Button) findViewById(R.id.pregunta_amistosa);
  	        lblEnvResp.setText("Reta a un amigo");
	        addListenerOnButton();
  	        
  	        // Updating parsed JSON data into ListView
	        ListAdapter adapter = new SimpleAdapter(Ranking.this, puntosList,
	        		R.layout.list_puntos_item,
	        		new String[] { TAG_FIELDS_PUNTOS, TAG_FIELDS_USUARIO}, new int[] {
                    R.id.puntos, R.id.usuario});
	        
	        setListAdapter(adapter);
  	        
        	 pd.dismiss();
          }
	     }
	     
	     
	     
	     
		 public void addListenerOnButton() {
			 
				btnDisplay = (Button) findViewById(R.id.pregunta_amistosa);
			 
				btnDisplay.setOnClickListener(new OnClickListener() {
			 
					@Override
					public void onClick(View v) {

						pidepreguntaAmistosa();
		         	};
			 
				});
			 
			  }
		 
		public void pidepreguntaAmistosa(){
            //Starting new intent
            Intent in = new Intent(getApplicationContext(), ListadoCompaneros.class);
            startActivity(in);
            //finish();
			
		}
	     
      	final Runnable toast = new Runnable(){
     		public void run(){
         		Toast.makeText(mContext,
         				mensaje,
         				Toast.LENGTH_SHORT).show();

     		}
     	};
	     
}
   
	/*
	 * 
	 *    
	 *    
	 *    
	 *    
	 *    
	 *    
	 *    SERVICIO
	 */
		/*void doBindService() {
			Log.i("Service JAVI", "entro en doBindService");
			getApplicationContext().bindService(new Intent(this, MiServicioPreguntas.class), mConnection,
					Context.BIND_AUTO_CREATE);
			Log.i("Service JAVI", "saldre? en doBindService");
		}
		
		private ServiceConnection mConnection = new ServiceConnection() {

			public void onServiceConnected(ComponentName className, IBinder binder) {
				Log.i("Service JAVI", "entro en Service connected");
				s = ((MiServicioPreguntas.MyBinder) binder).getService();
				Toast.makeText(Ranking.this, "Arrancando servicio",
						Toast.LENGTH_SHORT).show();
			}

			public void onServiceDisconnected(ComponentName className) {
				Toast.makeText(Ranking.this, "Parando servicio",
						Toast.LENGTH_SHORT).show();
				s = null;
			}
		};
		
		public void showServiceData(View view) {
			if (s != null) {
				List<String> wordList = s.getWordList();
				values.clear();
				values.addAll(wordList);
				adapter.notifyDataSetChanged();
			}
		}*/