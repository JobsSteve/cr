package com.gts.cr.main;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import com.gts.cr.main.R;

public class AdvertiseSplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_advertise_splash);
		
	}
	public void onAdvertiseContinueClick(View v) {
		Intent menuScreenIntent = new Intent(this, MenuScreenActivity.class);
		startActivity(menuScreenIntent);
		this.finish();
	}

	

}
