package com.jsuelapfc.preguntaras;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;

public class Refresh extends TabActivity{

	   @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
			Intent in = new Intent(getApplicationContext(), MainActivity.class);
			startActivity(in);
			finish();
	   }
}
