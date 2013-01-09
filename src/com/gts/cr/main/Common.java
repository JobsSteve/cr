package com.gts.cr.main;

import com.gts.cr.main.R;
import java.util.ArrayList;
import java.util.HashMap;

public class Common {
	/*Available memory in megs*/
	public static long available_memory;
	
	public static String user_name;
	public static int selectedCar;
	public static int user_score;
	public static int position;
	public static ArrayList<HashMap<String,String>> aston_martin_highscores;
	public static ArrayList<HashMap<String,String>> beetle_highscores;
	public static ArrayList<HashMap<String,String>> knight_rider_highscores;
	public static ArrayList<HashMap<String,String>> porsche_highscores;
	public static ArrayList<HashMap<String,String>> polo_highscores;
	public static String formatIntoMMSS(int pValue) {
		int minutes = pValue/6000;
		int remainder = pValue%6000;
		int seconds = remainder/100;
		int smallSec = remainder%100;
		return String.format("%02d:%02d:%02d", minutes,seconds,smallSec);
	}
}
