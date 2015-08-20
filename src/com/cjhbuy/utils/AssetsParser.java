package com.cjhbuy.utils;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cjhbuy.bean.CityItem;

public class AssetsParser {

	public List<CityItem> getCities(String res) {
		List<CityItem> cities = new ArrayList<CityItem>();
		try {
			JSONArray array = new JSONArray(res);
			for (int i = 0; i < array.length(); i++) {
				JSONObject object = array.getJSONObject(i);
				String cityname = object.optString("cityname");
				CityItem city = new CityItem();
				city.setName(cityname);
				cities.add(city);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return cities;
	}

}
