package com.ct.butler;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {
	
	ListView mListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	public void test(){
		BaseAdapter a = null;
		a.notifyDataSetChanged();
	}

}
