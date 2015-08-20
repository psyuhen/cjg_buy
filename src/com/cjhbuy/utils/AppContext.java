package com.cjhbuy.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.cjhbuy.bean.CityItem;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.AssetManager;

public class AppContext extends Application {
	private SharedPreferences preferences;
	private List<CityItem> cities;
	private String city;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		String as;
		try {
			as = getA();
			AssetsParser parser = new AssetsParser();
			List<CityItem> cities = parser.getCities(as);
			setCities(cities);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getA() throws IOException {
		AssetManager assetManager = this.getAssets();
		InputStream is = assetManager.open("city_coordinate.txt");
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length = -1;
		while ((length = is.read(buffer)) != -1) {
			stream.write(buffer, 0, length);
		}
		return stream.toString();
	}

	public SharedPreferences getPreferences() {
		return preferences;
	}

	public void setPreferences(SharedPreferences preferences) {
		this.preferences = preferences;
	}

	public List<CityItem> getCities() {
		return cities;
	}

	public void setCities(List<CityItem> cities) {
		this.cities = cities;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

}
