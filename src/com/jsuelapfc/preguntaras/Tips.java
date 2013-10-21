package com.jsuelapfc.preguntaras;

import java.io.InputStream;
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
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ListActivity;
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

public class Tips extends ListActivity{	
	
	// url to make request
	private static String url;
	 
	// JSON Node names
	private static final String TAG_TIPS = "tips";
	private static final String TAG_PK = "pk";
	private static final String TAG_MODEL = "model";

	private static final String TAG_FIELDS = "fields";
	private static final String TAG_FIELDS_LECCION = "leccion";
	
	private JSONParserPOST jParser;
    static InputStream is = null;
	private ArrayList<HashMap<String, String>> tipsList;

	// contacts JSONArray
	private JSONArray tips = null;

	private ProgressDialog pd;
	
	private String mensaje;
	private final Handler handler = new Handler();
    private Context mContext;
    
	private String asignatura;
	private String usuario;
    private SharedPreferences prefs;
    
    private Button lblEnvComment;
    private Button btnDisplay;


	   @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);

	        setContentView(R.layout.list_tips);

	        mContext = this;

	        // Hashmap for ListView
	        tipsList = new ArrayList<HashMap<String, String>>();
	 
	        // Creating JSON Parser instance
	        jParser = new JSONParserPOST();
	 
	        // getting JSON string from URL
	        url = "http://pfc-jsuelaplaza.libresoft.es/android/tips";	

	      //Creamos una nueva instancia y llamamos al método ejecutar
	      //pasándole el string.
	        
	        
	    	pd = ProgressDialog.show(Tips.this, "Preguntas", "Cargando...", true, false);	

	      new MiTarea().execute(url);
        
	    }

	     private class MiTarea extends AsyncTask<String, ListAdapter, ArrayList<HashMap<String, String>> >{

	          protected ArrayList<HashMap<String, String>> doInBackground(String... urls) {
			        try {
			        	
                	    String csrf = null;
              			DefaultHttpClient httpclient = new DefaultHttpClient();
						 

       			    	HttpGet httpget = new HttpGet(url);
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

                    			//obtengo el nombre de la asignatura y del usuario
       			    	        prefs = PreferenceManager.getDefaultSharedPreferences(Tips.this);
       			    	        asignatura = prefs.getString("subject", "n/a");
       			    	        usuario = prefs.getString("username", "n/a");
       			    	        
       			    			
                    			nameValuePairs.add(new BasicNameValuePair("asignatura", asignatura));
                    			nameValuePairs.add(new BasicNameValuePair("usuario", usuario));
               			    	nameValuePairs.add(new BasicNameValuePair("csrfmiddlewaretoken", csrf.split("=")[1]));

       			    		}
       			    	} 
			        	
    					HttpPost httppost = new HttpPost(url);
       			    

    			        
    			        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
    			        
    			        response = httpclient.execute(httppost);
    			        HttpEntity resEntityGet = response.getEntity();
    			        is = resEntityGet.getContent();

				        JSONObject json = jParser.getJSONFromResponse(is);
			            // Getting Array of Contacts
			            tips = json.getJSONArray(TAG_TIPS);
			 
			            // looping through All Contacts
			            for(int i = 0; i < tips.length(); i++){
			                JSONObject c = tips.getJSONObject(i);
			 
			                // Storing each json item in variable
			                String pk = c.getString(TAG_PK);
			                String model = c.getString(TAG_MODEL);
			         
			                // Respuestas is agin JSON Object
			                JSONObject fields = c.getJSONObject(TAG_FIELDS);
			                String leccion = fields.getString(TAG_FIELDS_LECCION);  

			                
			                // creating new HashMap
			                HashMap<String, String> map = new HashMap<String, String>();
			 
			                // adding each child node to HashMap key => value
			                map.put(TAG_PK, pk);
			                map.put(TAG_MODEL, model);
			                map.put(TAG_FIELDS_LECCION, leccion);
			                
			            	 
			                // adding HashList to ArrayList
			               tipsList.add(map); 
			               
			            } 
			        } catch (Exception e) {
    		        	mensaje = "Ha ocurrido un error...no se puede contactar con el servidor";
    		            handler.post(toast);
			            e.printStackTrace();

			        }	

	         
			        // Updating parsed JSON data into ListView
		            Log.i("TIPs", Integer.toString(Thread.activeCount()));
			        Log.i("TIPs sixe2", Integer.toString(tipsList.size()));
					return tipsList;

	          }

	          protected void onPostExecute(ArrayList<HashMap<String, String>> result ) {
	        	  
	        	  
    	        //Damos nombre al botón
    	        //el siguiente método se ejecutará cuando se presione el botón
    	        lblEnvComment = (Button) findViewById(R.id.envia_comment);
    	        lblEnvComment.setText("Envía comentario al profesor");
    	        addListenerOnButton();

	        	  ListAdapter adapter = new SimpleAdapter(Tips.this, result,
	  	        		R.layout.list_tips_item,
	  	        		new String[] { TAG_FIELDS_LECCION}, new int[] {
	                      R.id.tips});
	  	        
	  	        setListAdapter(adapter);
	  	        
	        	 pd.dismiss();
	          }
	    }
	     
	     
		 public void addListenerOnButton() {
			 
				btnDisplay = (Button) findViewById(R.id.envia_comment);
			 
				btnDisplay.setOnClickListener(new OnClickListener() {
			 
					@Override
					public void onClick(View v) {

			            //Starting new intent
			            Intent in = new Intent(getApplicationContext(), SendingComments.class);
			            startActivity(in);
		         	};
			 
				});
			 
			  }
      	final Runnable toast = new Runnable(){
     		public void run(){
         		Toast.makeText(mContext,
         				mensaje,
         				Toast.LENGTH_SHORT).show();

     		}
     	};

}