package com.jsuelapfc.preguntaras;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class SubjectsRegister extends ListActivity {
	
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

	private JSONParser jParser;
	private JSONParser jParser2;

	private ArrayList<HashMap<String, String>> asignaturasalumnoList;
	private ArrayList<HashMap<String, String>> asignaturasList;

	// contacts JSONArray
	private JSONArray asignaturasalumno = null;
	private JSONArray asignaturas = null;

	
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
    

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 	    requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.subjects);
        
        lv = (ListView)findViewById(android.R.id.list);
		irA = (Button) findViewById(R.id.matricularse);
		tv = (TextView)findViewById(R.id.titulo);
		tv.setText("¡Matrículate! Listado de todas las asignaturas");
        
        // Hashmap for ListView
        asignaturasList = new ArrayList<HashMap<String, String>>();
 
        // Creating JSON Parser instance
        jParser = new JSONParser();

        prefs = PreferenceManager.getDefaultSharedPreferences(SubjectsRegister.this);
        loginusuario = prefs.getString("username", "n/a");
        // getting JSON string from URL
        
        url = "http://pfc-jsuelaplaza.libresoft.es/android/asignaturas/listado/completo/"+loginusuario;
    	pd = ProgressDialog.show(SubjectsRegister.this, "Preguntas", "Cargando...", true, false);	

	    new MiTarea().execute(url);
        
    }
	

    
    private class MiTarea extends AsyncTask<String, ListAdapter, ArrayList<HashMap<String, String>> >{


        protected ArrayList<HashMap<String, String>> doInBackground(String... urls) {


		        try {

			        JSONObject json = jParser.getJSONFromUrl(url);
		            // Getting Array of Contacts
		            asignaturas = json.getJSONArray(TAG_ASIGNATURAS);
		 
		            // looping through All Contacts
		            for(int i = 0; i < asignaturas.length(); i++){
		                JSONObject c = asignaturas.getJSONObject(i);
		 
		                // Storing each json item in variable
		                String pk = c.getString(TAG_PK);
		                String model = c.getString(TAG_MODEL);
		         
		                // Respuestas is agin JSON Object
		                JSONObject fields = c.getJSONObject(TAG_FIELDS);
		                String asignaturas = fields.getString(TAG_FIELDS_ASIGNATURA);  
   	    

		                
		                // creating new HashMap
		                HashMap<String, String> map = new HashMap<String, String>();
		 
		                // adding each child node to HashMap key => value

		                map.put(TAG_PK, pk);
		                map.put(TAG_MODEL, model);
		                map.put(TAG_FIELDS_ASIGNATURA, asignaturas);
		                asignaturasList.add(map); 

		            } 
		        } catch (Exception e) {
  				mensaje = "No se puede conectar con el servidor. Inténtelo más tarde";
  			 
  				handler.post(toast);
		            e.printStackTrace();
		        }	
				return asignaturasList;
        }

    protected void onPostExecute(ArrayList<HashMap<String, String>> result ) {
	        //Damos nombre al botón
	        //el siguiente método se ejecutará cuando se presione el botón
    		irA = (Button) findViewById(R.id.matricularse);
    		irA.setText("Matricularme");
	        addListenerOnButton();

	        
	        adapter3 = new SimpleAdapter(SubjectsRegister.this, asignaturasList,
    		  android.R.layout.simple_list_item_single_choice,
      		new String[] { TAG_FIELDS_ASIGNATURA}, new int[] {
    		  android.R.id.text1});

	        
	        lv.setAdapter(adapter3);
	        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);  
        
  	 pd.dismiss();
    }
    }
    
    
	 public void addListenerOnButton() {
		 
		 	btnDisplayirA = (Button) findViewById(R.id.matricularse);
		 
		 	btnDisplayirA.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					final ProgressDialog pd1 = ProgressDialog.show(SubjectsRegister.this, "Preguntas", "Enviando...", true, false);
					try{

						int p =  lv.getCheckedItemPosition();
						subject = ((TextView) lv.getChildAt(p)).getText().toString(); 
						Toast.makeText(SubjectsRegister.this, "Matriculando "+subject, Toast.LENGTH_LONG).show(); 
						
				        loginusuario = prefs.getString("username", "n/a");
				        url3 = "http://pfc-jsuelaplaza.libresoft.es/android/asignaturas/matricula";

						
	            		new Thread(new Runnable(){
	            			@Override
	                		public void run(){
	                     	    String csrf = null;
	                  			DefaultHttpClient httpclient = new DefaultHttpClient();
	    						try {  

                   			    	HttpGet httpget = new HttpGet(url3);
                   			    	HttpResponse response = httpclient.execute(httpget);
                       	
                   			    	Header[] headers = response.getAllHeaders();
                   			    	
                			        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);

                   			    	
                   			    	for (int i = 0; i < headers.length; i++){	
               			    			System.out.println("cabeceraaaa:"+response.getParams().toString());
                   			    		if (headers[i].toString().contains("csrftoken")){

                   			    			csrf=headers[i].toString();
                   			    			csrf = csrf.replace("Set-Cookie:","");
                   			    			csrf = csrf.replace(" ","");
                   			    			csrf = csrf.replace(";expires","");

		                        			nameValuePairs.add(new BasicNameValuePair("usuario", loginusuario));
		                        			nameValuePairs.add(new BasicNameValuePair("asignatura", subject));
	                       			    	nameValuePairs.add(new BasicNameValuePair("csrfmiddlewaretoken", csrf.split("=")[1]));

                   			    		}
                   			    	} 
	    	               			    	
                					HttpPost httppost = new HttpPost("http://pfc-jsuelaplaza.libresoft.es/android/asignaturas/matricula");                  			    	

                			        
                			        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
                			        
                			        response = httpclient.execute(httppost);
                			        HttpEntity resEntityGet = response.getEntity();
                			        
                        			//pd.dismiss();
                        			
	    							if (resEntityGet != null) {
	    								resultado = EntityUtils.toString(resEntityGet);
	    								if (resultado.equals("ok")){
	    							        //getting notification
	    		        		        	mensaje = "Matriculado correctamente";
	    		        		            handler.post(toast);
	    									finish();
	    									Intent in = new Intent(getApplicationContext(), Subjects.class);
	    		        					startActivity(in);
	    					                finish();
	    		        					

	    									
	    								}else if (resultado.equals("exist")){
	    		        		        	mensaje = "Ya estas matriculado en esta asignatura";
	    		        		            handler.post(toast);
	    		        		            
	    		
	    								}else{
	    		        		        	mensaje = "No se puede contactar con el servidor"+resultado;
	    		        		        	System.out.println(resultado);
	    		        		            handler.post(toast);
	    								}
	    								
	    							} else {
	    		
	    	        		        	mensaje = "Error al contactar con el servidor";
	    	        		            handler.post(toast);		
	    							}

	    							
	    							
	    						} catch (Exception e) {
	    							Log.i("ERROR", "CONECTION PROBLEM"+resultado);
	            		        	mensaje = "Imposible contactar con el servidor";
	            		            handler.post(toast);
	    		
	    						}
	    					    pd1.dismiss();
	    	    			}
	  
	             		}).start();		
						

						}catch( NullPointerException e){
							pd1.dismiss();
							Toast.makeText(SubjectsRegister.this,"Debes elegir una asignatura", Toast.LENGTH_LONG).show();
						}catch( Exception e){
							pd1.dismiss();
							Toast.makeText(SubjectsRegister.this,"Error ", Toast.LENGTH_LONG).show();
						}
	
				
				}

	         	});
		 
			};
		 
  	final Runnable toast = new Runnable(){
 		public void run(){
     		Toast.makeText(SubjectsRegister.this,
     				mensaje,
     				Toast.LENGTH_LONG).show();

 		}
 	};

}

