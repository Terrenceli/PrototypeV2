package com.example.prototypev2;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
	private Button listBtn;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		listBtn = (Button) findViewById(R.id.listBtn);
	       
        listBtn.setOnClickListener(new OnClickListener() {
        		public void onClick(View v) {
        			Intent intent = new Intent(MainActivity.this, DisplayActivity.class);
                    startActivity(intent);   	        
        		}
        	}     		
        );
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
