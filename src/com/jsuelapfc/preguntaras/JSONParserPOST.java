package com.jsuelapfc.preguntaras;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
 
public class JSONParserPOST {
  
    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";
 
    // constructor
    public JSONParserPOST() {
 
    }
 
    public JSONObject getJSONFromResponse(InputStream is) {

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "n");
            }
            is.close();
            json = sb.toString();
            
	        

        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());

        }
 
        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(json);
            
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing dataaaa " + e.toString()+ json);
        	
        }
 
        // return JSON String
        System.out.println("*J*J*J*J*J*J json es:"+jObj);
        return jObj;
 
    }
}
