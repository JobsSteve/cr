package com.gts.cr.main;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import com.gts.cr.main.R;

public class CarSelectActivity extends Activity {
	Intent gameIntent;
	MediaPlayer mediaPlayer;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_select);
        gameIntent = new Intent(this, com.gts.cr.main.MainGameActivity.class);
        TextView nameText = (TextView)findViewById(R.id.greeting_textview);
        nameText.setText("Hi "+Common.user_name+". Select your car.");
        mediaPlayer = MediaPlayer.create(this, R.raw.start_sound);
    	
    }
    
    public void onCar01Select(View v) {
    	Common.selectedCar = 1;
    	mediaPlayer.start();
    	startActivity(gameIntent);
    	this.finish();
    }
    public void onCar02Select(View v) {
    	Common.selectedCar = 2;
    	mediaPlayer.start();
    	startActivity(gameIntent);
    	this.finish();
    }
    public void onCar03Select(View v) {
    	Common.selectedCar = 3;
    	mediaPlayer.start();
    	startActivity(gameIntent);
    	this.finish();
    }
    public void onCar04Select(View v) {
    	Common.selectedCar = 4;
    	mediaPlayer.start();
    	startActivity(gameIntent);
    	this.finish();
    }
    public void onCar05Select(View v) {
    	Common.selectedCar = 5;
    	mediaPlayer.start();
    	startActivity(gameIntent);
    	this.finish();
    }
    
}
