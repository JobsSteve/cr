package com.gts.cr.main;

import java.util.HashMap;

import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.app.Activity;

public class KnightRiderBestTimesActivity extends Activity {
	TableLayout knight_rider_table;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_knight_rider_best_times);
		knight_rider_table = (TableLayout)findViewById(R.id.knight_rider_table);
		TableRow.LayoutParams nameTextViewParam = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
				TableRow.LayoutParams.WRAP_CONTENT,0.6f);
		TableRow.LayoutParams scoreTextViewParam = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
			     TableRow.LayoutParams.WRAP_CONTENT,0.4f);
		if ( Common.knight_rider_highscores != null ) {
			for ( HashMap<String,String> result: Common.knight_rider_highscores ) {
				TableRow tablerow = new TableRow(this);
				TextView nameTextView = new TextView(this);
				TextView scoreTextView = new TextView(this);
				nameTextView.setText(result.get("name"));
				nameTextView.setLayoutParams(nameTextViewParam);
				scoreTextView.setText(Common.formatIntoMMSS(new Integer(result.get("score"))));
				scoreTextView.setLayoutParams(scoreTextViewParam);
				tablerow.addView(nameTextView);
				tablerow.addView(scoreTextView);
				knight_rider_table.addView(tablerow);
				
			}
		}
	}


}
