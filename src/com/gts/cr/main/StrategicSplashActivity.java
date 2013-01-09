package com.gts.cr.main;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import com.gts.cr.main.R;
//
public class StrategicSplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_strategic_splash);
	}
	public void onStrategicContinueClick(View v) {
		Intent advertiseIntent = new Intent(this, AdvertiseSplashActivity.class);
		startActivity(advertiseIntent);
		this.finish();
	}
	
}
