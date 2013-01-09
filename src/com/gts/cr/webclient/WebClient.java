package com.gts.cr.webclient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.util.Log;

import com.google.gson.reflect.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class WebClient {
	//static final String SERVER_URL = "http://192.168.42.162/colomboracer/webService.php";
	
	static final String SERVER_URL = "http://colomboracer.com/db/colomboracer/webService.php";

	private static String encodeData(HashMap<String,String> Data) {
		Gson gson = new Gson();

		// convert java object to JSON format
		String json = gson.toJson(Data);

		//Log.i("JSON: ", json);

		return json;

	}

	// with only the action
	public static HashMap<String,String> postRequestForAction(String action) {
		HashMap<String,String> data1 = new HashMap<String,String>();
		try {
			String data = URLEncoder.encode("action", "UTF-8") + "="
					+ URLEncoder.encode(action, "UTF-8");
			URL url = new URL(SERVER_URL);
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(
					conn.getOutputStream());
			wr.write(data);
			wr.flush();
			StringBuffer answer = new StringBuffer();
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String line = "";
			while ((line = rd.readLine()) != null) {
				answer.append(line);
			}

			wr.close();
			rd.close();

			Gson gson = new Gson();
			Type type = new TypeToken<Map<String, String>>() {
			}.getType();

			data1 = gson.fromJson(answer.toString(), type);
			return data1;

		} catch (Exception e) {
			data1.put("message", "Error");
			return data1;
		}
	}

	// with action and data
	public static HashMap<String,String> postRequest(String action, HashMap<String,String> params) {
		String json_data = encodeData(params);
		HashMap<String,String> data1 = new HashMap<String,String>();
		try {

			// Construct data
			String data = URLEncoder.encode("action", "UTF-8") + "="
					+ URLEncoder.encode(action, "UTF-8");
			data += "&" + URLEncoder.encode("data", "UTF-8") + "="
					+ URLEncoder.encode(json_data, "UTF-8");

			// Send data
			URL url = new URL(SERVER_URL);
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(
					conn.getOutputStream());
			wr.write(data);
			wr.flush();
			StringBuffer answer = new StringBuffer();
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String line = "";
			while ((line = rd.readLine()) != null) {
				answer.append(line);
			}

			wr.close();
			rd.close();

			Gson gson = new Gson();
			Type type = new TypeToken<Map<String, String>>() {
			}.getType();

			data1 = gson.fromJson(answer.toString(), type);
			return data1;

		} catch (Exception e) {
			e.printStackTrace();
			data1.put("message", "Error");
			return data1;
		}
	}

	public static ArrayList<HashMap<String, String>> postRequestForArrayList(String action,
			HashMap<String,String> params) {
		String json_data = encodeData(params);
		ArrayList<HashMap<String, String>> data1 = new ArrayList<HashMap<String, String>>();
		try {

			// Construct data
			String data = URLEncoder.encode("action", "UTF-8") + "="
					+ URLEncoder.encode(action, "UTF-8");
			data += "&" + URLEncoder.encode("data", "UTF-8") + "="
					+ URLEncoder.encode(json_data, "UTF-8");

			// Send data
			URL url = new URL(SERVER_URL);
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(
					conn.getOutputStream());
			wr.write(data);
			wr.flush();
			StringBuffer answer = new StringBuffer();
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String line = "";
			while ((line = rd.readLine()) != null) {
				answer.append(line);
			}

			wr.close();
			rd.close();

			Gson gson = new Gson();
			Type type = new TypeToken<ArrayList<HashMap<String, String>>>() {
			}.getType();

			data1 = gson.fromJson(answer.toString(), type);
			return data1;

		} catch (Exception e) {
			e.printStackTrace();
			HashMap<String, String> res = new HashMap<String, String>();
			res.put("message", "error");
			data1.add(res);
			return data1;
		}
	}
	
	
	/**
	 * Load image from a URL (For FB Profile pics)
	 * 
	 * @param url
	 *            URL to load the image from
	 * @param path
	 *            path to save the image to
	 * @return the absolute path of the downloaded image
	 */
	public static boolean loadImageFromWebOperations(String url, String path) {
		try {
			InputStream is = (InputStream) new URL(url).getContent();

			System.out.println(path);
			File f = new File(path);


			f.createNewFile();
			FileOutputStream fos = new FileOutputStream(f);
			try {

				byte[] b = new byte[100];
				int l = 0;
				while ((l = is.read(b)) != -1)
					fos.write(b, 0, l);

			} catch (Exception e) {
				e.printStackTrace();
			}

			return true;
		} catch (Exception e) {
			System.out.println("Exc=" + e);
			return false;

		}
	}

}
