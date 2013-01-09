package com.gts.cr.main;

import java.util.HashMap;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class BeetleBestTimesActivity extends Activity {
	TableLayout beetle_table;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_beetle_best_times);
		beetle_table = (TableLayout)findViewById(R.id.beetle_table);
		TableRow.LayoutParams nameTextViewParam = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
				TableRow.LayoutParams.WRAP_CONTENT,0.6f);
		TableRow.LayoutParams scoreTextViewParam = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
			     TableRow.LayoutParams.WRAP_CONTENT,0.4f);
		if ( Common.beetle_highscores != null ) {
			for ( HashMap<String,String> result: Common.beetle_highscores) {
				TableRow tablerow = new TableRow(this);
				TextView nameTextView = new TextView(this);
				TextView scoreTextView = new TextView(this);
				nameTextView.setText(result.get("name"));
				nameTextView.setLayoutParams(nameTextViewParam);
				scoreTextView.setText(Common.formatIntoMMSS(new Integer(result.get("score"))));
				scoreTextView.setLayoutParams(scoreTextViewParam);
				tablerow.addView(nameTextView);
				tablerow.addView(scoreTextView);
				beetle_table.addView(tablerow);
				
			}
		}
	}



}
