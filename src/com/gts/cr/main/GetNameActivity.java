package com.gts.cr.main;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.support.v4.app.NavUtils;
import com.gts.cr.main.R;

public class GetNameActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_name);
    }
    
    public void onOKClicked(View v) {
    	SharedPreferences userPrefs = getSharedPreferences("user_details", 0);
    	SharedPreferences.Editor editor = userPrefs.edit();
    	
    	EditText nameEditText = (EditText)findViewById(R.id.nameEditText);
    	Common.user_name = nameEditText.getText().toString();
    	editor.putString("user_name", Common.user_name);
    	editor.commit();
    	Intent carSelectIntent = new Intent(this, CarSelectActivity.class);
    	startActivity(carSelectIntent);
    	this.finish();
    }

    
}
