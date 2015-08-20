package com.cjhbuy.adapter;

import java.util.List;

import com.cjhbuy.activity.R;
import com.cjhbuy.bean.CityItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CityHeadViewAdapter extends BaseAdapter {

	private Context context;
	private List<CityItem> cities;

	public CityHeadViewAdapter(Context context,List<CityItem> cities) {
		this.context = context;
		this.cities=cities;
	}

	@Override
	public int getCount() {
		return cities.size();
	}

	@Override
	public Object getItem(int position) {
		return cities.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		HolderView holder = null;

		if (convertView == null) {
			holder = new HolderView();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.city_headview_item, null);
			holder.button = (TextView) convertView.findViewById(R.id.button);
			convertView.setTag(holder);
		} else {
			holder = (HolderView) convertView.getTag();
		}
		holder.button.setText(cities.get(position).getName());

		return convertView;
	}

	class HolderView {
		TextView button;
	}
}