package com.gts.cr.main;

import java.util.ArrayList;
import java.util.HashMap;

import com.gts.cr.main.R;
import com.gts.cr.webclient.WebClient;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

/**
 * Name, mobile, email, score, timestamp
 * @author Ramindu
 *
 */
public class MenuScreenActivity extends Activity {
	ConnectivityManager connectivityManager;
	NetworkInfo activeNetworkInfo;
	public long getAvailableMemory() {
		MemoryInfo mi = new MemoryInfo();
		ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		activityManager.getMemoryInfo(mi);
		long availableMegs = mi.availMem / 1048576L;
		return availableMegs;
	}
	/**
	 * Method to check whether there is an active network connection available
	 * @return boolean true if network available
	 */
	public boolean isNetworkAvailable() {
		connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null;
	}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_menu_screen);
        /*Common.available_memory = getAvailableMemory();
        Log.i("Available Memory", String.valueOf(Common.available_memory));*/
    }
    
    
    public void onStartClick(View v) {
    	SharedPreferences userPrefs = getSharedPreferences("user_details", 0);
    	if (userPrefs.getString("user_name", null) != null) {
    		Common.user_name = userPrefs.getString("user_name", null);
    		Intent carIntent = new Intent(this, CarSelectActivity.class);
    		startActivity(carIntent);
        	
        }
    	else {
    		Intent carIntent = new Intent(this, GetNameActivity.class);
    		startActivity(carIntent);
    	}
    }
    public void onExitClick(View v) {
    	this.finish();
    }
    
    public void onHelpClick(View v) {
    	Intent helpIntent = new Intent(this, HelpActivity.class);
    	startActivity(helpIntent);
    }
    
    public void onAboutClick(View v) {
    	Intent aboutIntent = new Intent(this, AboutActivity.class);
    	startActivity(aboutIntent);
    }
    public void onHighscoresClick(View v) {
    	//Toast.makeText(this, "Sorry, there are no High Scores to show at this time.", Toast.LENGTH_LONG).show();
    	if ( !isNetworkAvailable() ) {
    		Toast.makeText(this, "You need to have an active Internet Connection to view High Scores", Toast.LENGTH_LONG).show();
    	}
    	else {
    		new GetHighscoresAsyncTask().execute();
    	}
    }
    class GetHighscoresAsyncTask extends AsyncTask<Void, Void, Boolean> {
		protected ProgressDialog progressDialog;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = ProgressDialog.show(MenuScreenActivity.this, "Getting Highscores", "Please wait...", true, false);
		}
		@Override
		protected Boolean doInBackground(Void... hashMaps) {
			boolean done = false;
			HashMap<String,String> params = new HashMap<String,String>();
			params.put("car_id", "1");
			ArrayList<HashMap<String, String>> aston_martin_result = WebClient.postRequestForArrayList("getscores",
					params);
			/*for ( HashMap<String,String> results: aston_martin_result ) {
				Log.i("name",results.get("name"));;
			}*/
			if ( aston_martin_result.size() != 0 && aston_martin_result.get(0).containsKey("message") ) {
				Common.aston_martin_highscores = null;
				done = false;
			}
			else {
				done = true;
				Common.aston_martin_highscores = aston_martin_result;
			}
			params.clear();
			params.put("car_id", "2");
			ArrayList<HashMap<String, String>> beetle_result = WebClient.postRequestForArrayList("getscores",
					params);
			if ( beetle_result.size() != 0 &&beetle_result.get(0).containsKey("message") ) {
				Common.beetle_highscores = null;
				done = false;
			}
			else {
				done = true;
				Common.beetle_highscores = beetle_result;
			}
			params.clear();
			params.put("car_id", "3");
			ArrayList<HashMap<String, String>> knight_rider_result = WebClient.postRequestForArrayList("getscores",
					params);
			if ( knight_rider_result.size() != 0 && knight_rider_result.get(0).containsKey("message") ) {
				Common.knight_rider_highscores = null;
				done = false;
			}
			else {
				done = true;
				Common.knight_rider_highscores = knight_rider_result;
			}
			params.clear();
			params.put("car_id", "4");
			ArrayList<HashMap<String, String>> porsche_result = WebClient.postRequestForArrayList("getscores",
					params);
			if ( porsche_result.size() != 0 && porsche_result.get(0).containsKey("message") ) {
				Common.porsche_highscores = null;
				done = false;
			}
			else {
				done = true;
				Common.porsche_highscores = porsche_result;
			}
			params.clear();
			params.put("car_id", "5");
			ArrayList<HashMap<String, String>> polo_result = WebClient.postRequestForArrayList("getscores",
					params);
			if ( polo_result.size() != 0 && polo_result.get(0).containsKey("message") ) {
				Common.polo_highscores = null;
				done = false;
			}
			else {
				done = true;
				Common.polo_highscores = polo_result;
			}
			params.clear();
			return done;
		}
		@Override
		protected void onPostExecute(Boolean success) {
			super.onPostExecute(success);
			progressDialog.dismiss();
			if ( success ) {
				Toast.makeText(getApplicationContext(), "Finished retrieving Highscores.", Toast.LENGTH_SHORT).show();
				Intent bestTimesIntent = new Intent(getApplicationContext(), BestTimesTabActivity.class);
				startActivity(bestTimesIntent);
			}
			else {
				Toast.makeText(getApplicationContext(), "Not all highscores could be loaded.", Toast.LENGTH_SHORT).show();
				Intent bestTimesIntent = new Intent(getApplicationContext(), BestTimesTabActivity.class);
				startActivity(bestTimesIntent);

			}
		}
	}
    
    
}
