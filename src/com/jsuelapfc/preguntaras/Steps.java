package com.jsuelapfc.preguntaras;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class Steps extends Activity{

	
	   @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	 	    requestWindowFeature(Window.FEATURE_NO_TITLE);

	        setContentView(R.layout.steps);
	 	    //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


	   }
}
