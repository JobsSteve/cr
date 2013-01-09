package com.gts.cr.main;

import java.util.HashMap;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class AstonMartinBestTimesActivity extends Activity {
	TableLayout aston_martin_table;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_aston_martin_best_times);
		aston_martin_table = (TableLayout)findViewById(R.id.aston_martin_table);
		TableRow.LayoutParams nameTextViewParam = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
				TableRow.LayoutParams.WRAP_CONTENT,0.6f);
		TableRow.LayoutParams scoreTextViewParam = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
			     TableRow.LayoutParams.WRAP_CONTENT,0.4f);
		if ( Common.aston_martin_highscores != null ) {
			for ( HashMap<String,String> result: Common.aston_martin_highscores ) {
				TableRow tablerow = new TableRow(this);
				TextView nameTextView = new TextView(this);
				TextView scoreTextView = new TextView(this);
				nameTextView.setText(result.get("name"));
				Log.i("name", result.get("name"));
				nameTextView.setLayoutParams(nameTextViewParam);
				scoreTextView.setText(Common.formatIntoMMSS(new Integer(result.get("score"))));
				Log.i("score", result.get("score"));
				scoreTextView.setLayoutParams(scoreTextViewParam);
				tablerow.addView(nameTextView);
				tablerow.addView(scoreTextView);
				aston_martin_table.addView(tablerow);
				
			}
		}

	}
}
