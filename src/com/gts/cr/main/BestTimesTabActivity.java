package com.gts.cr.main;


import android.os.Bundle;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.support.v4.app.NavUtils;

@SuppressWarnings("deprecation")
public class BestTimesTabActivity extends TabActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_best_times_tab);
		TabHost tabHost = getTabHost();

		TabSpec aston_martin_tabspec = tabHost.newTabSpec("Aston Martin");
		// setting Title and Icon for the Tab
		aston_martin_tabspec.setIndicator("Aston Martin",
				getResources().getDrawable(R.drawable.aston_martin));
		Intent aston_martin_intent = new Intent(this, AstonMartinBestTimesActivity.class);
		aston_martin_tabspec.setContent(aston_martin_intent);

		TabSpec vw_beetle_tabspec = tabHost.newTabSpec("VW Beetle");
		vw_beetle_tabspec.setIndicator("VW Beetle",
				getResources().getDrawable(R.drawable.volkswagen_beetle));
		Intent vw_beetle_intent = new Intent(this, BeetleBestTimesActivity.class);
		vw_beetle_tabspec.setContent(vw_beetle_intent);

		TabSpec knight_rider_tabspec = tabHost.newTabSpec("Knight Rider");
		knight_rider_tabspec.setIndicator("Knight Rider", getResources().getDrawable(R.drawable.knight_rider));
		Intent knight_rider_intent = new Intent(this, KnightRiderBestTimesActivity.class);
		knight_rider_tabspec.setContent(knight_rider_intent);
		
		TabSpec porsche_tabspec = tabHost.newTabSpec("Porsche");
		porsche_tabspec.setIndicator("Porsche", getResources().getDrawable(R.drawable.porsche));
		Intent porsche_intent = new Intent(this, PorscheBestTimesActivity.class);
		porsche_tabspec.setContent(porsche_intent);
		
		TabSpec vw_polo_tabspec = tabHost.newTabSpec("VW Polo");
		vw_polo_tabspec.setIndicator("VW Polo", getResources().getDrawable(R.drawable.volkswagen_polo));
		Intent vw_polo_intent = new Intent(this, PoloBestTimesActivity.class);
		vw_polo_tabspec.setContent(vw_polo_intent);
		
		// Adding all TabSpec to TabHost
		tabHost.addTab(aston_martin_tabspec);
		tabHost.addTab(vw_beetle_tabspec); 
		tabHost.addTab(knight_rider_tabspec); 
		tabHost.addTab(porsche_tabspec);
		tabHost.addTab(vw_polo_tabspec);
	}


}
