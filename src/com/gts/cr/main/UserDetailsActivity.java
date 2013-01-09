package com.gts.cr.main;

import java.util.HashMap;

import com.gts.cr.main.R;
import com.gts.cr.webclient.WebClient;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class UserDetailsActivity extends Activity {
	TextView headerText;
	EditText nameText;
	EditText emailText;
	EditText phoneNumberText;
	ImageView positionImageView;
	String version = "";
	UserDetailsActivity detailsIntent;
	SharedPreferences userPrefs;
	ConnectivityManager connectivityManager;
	NetworkInfo activeNetworkInfo;
	PackageInfo pInfo;
	
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_details);
		
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			version = pInfo.versionName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		userPrefs = getSharedPreferences("user_details", 0);
		headerText = (TextView)findViewById(R.id.headerTextView);
		headerText.setText("Your Time: "+Common.formatIntoMMSS(Common.user_score));
		nameText = (EditText)findViewById(R.id.nameTextField);
		emailText = (EditText)findViewById(R.id.emailTextField);
		positionImageView = (ImageView)findViewById(R.id.position_imageView);
		phoneNumberText = (EditText)findViewById(R.id.phoneNoTextField);
		nameText.setText(Common.user_name);
		emailText.setText(userPrefs.getString("email", ""));
		phoneNumberText.setText(userPrefs.getString("phone_no", ""));
		switch ( Common.position ) {
		case 1:
			positionImageView.setImageResource(R.drawable.position_1);
			break;
		case 2:
			positionImageView.setImageResource(R.drawable.position_2);
			break;
		case 3:
			positionImageView.setImageResource(R.drawable.position_3);
			break;
		case 4:
			positionImageView.setImageResource(R.drawable.position_4);
			break;
			
		}
		detailsIntent = this;
	
	}

	public void onSubmitClick(View v) {
		if ( isNetworkAvailable() ) {
			HashMap<String,String> params = new HashMap<String, String>();
			SharedPreferences.Editor editor = userPrefs.edit();
			editor.putString("email", emailText.getText().toString());
			editor.putString("phone_no", phoneNumberText.getText().toString());
			editor.commit();
			params.put("name", nameText.getText().toString());
			params.put("email", emailText.getText().toString());
			params.put("phone_no", phoneNumberText.getText().toString());
			params.put("score", String.valueOf(Common.user_score));
			params.put("car_id", String.valueOf(Common.selectedCar));
			params.put("version_id", version);
			new SubmitScoreAsyncTask().execute(params);
		}
		else Toast.makeText(getApplicationContext(), "You need to have an Internet connection to submit your score", Toast.LENGTH_LONG).show();
	}
	public void onCancelButtonClick(View v) {
		this.finish();
	}
	class SubmitScoreAsyncTask extends AsyncTask<HashMap<String,String>, Void, Boolean> {
		protected ProgressDialog progressDialog;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = ProgressDialog.show(UserDetailsActivity.this, "Submitting Score", "Please wait...", true, false);
		}
		@Override
		protected Boolean doInBackground(HashMap<String, String>... hashMaps) {
			HashMap<String, String> result = WebClient.postRequest("add", hashMaps[0]);
			if ( result.get("message").equals("SUCCESS")) {
				return true;
			}
			else return false;
		}
		@Override
		protected void onPostExecute(Boolean success) {
			super.onPostExecute(success);
			progressDialog.dismiss();
			if ( success ) {
				Toast.makeText(getApplicationContext(), "Your score submitted successfully.", Toast.LENGTH_SHORT).show();
				detailsIntent.finish();
			}
			else {
				Toast.makeText(getApplicationContext(), "Score submission failed.", Toast.LENGTH_SHORT).show();
				detailsIntent.finish();
			}
		}
	}
}
