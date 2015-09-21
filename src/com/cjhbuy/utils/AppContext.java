package com.cjhbuy.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.AssetManager;

import com.cjhbuy.bean.CityItem;
import com.cjhbuy.bean.GoodsItem;
import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;

public class AppContext extends Application {
	private static final Logger LOGGER = LoggerFactory.getLogger(AppContext.class);

	private SharedPreferences preferences;
	private List<CityItem> cities;
	private String city;
	private List<GoodsItem> cartGoodLists;
 	
	
	@Override
	public void onCreate() {
		super.onCreate();
		String as;
		cartGoodLists=new ArrayList<GoodsItem>();
		try {
			as = getA();
			AssetsParser parser = new AssetsParser();
			List<CityItem> cities = parser.getCities(as);
			setCities(cities);
		} catch (IOException e) {
			LOGGER.error("转换出错",e);
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

	public List<GoodsItem> getCartGoodLists() {
		return cartGoodLists;
	}

	public void setCartGoodLists(List<GoodsItem> cartGoodLists) {
		this.cartGoodLists = cartGoodLists;
	}

	
}
