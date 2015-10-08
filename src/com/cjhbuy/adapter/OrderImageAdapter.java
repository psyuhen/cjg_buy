package com.cjhbuy.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.cjhbuy.activity.R;

public class OrderImageAdapter extends BaseAdapter {
	private List<Bitmap> list;
	private Context context;

	public OrderImageAdapter(Context context, List<Bitmap> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_order_image, null);
			viewHolder.item_image = (ImageView) convertView
					.findViewById(R.id.item_image);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		Bitmap item = list.get(position);
		viewHolder.item_image.setImageBitmap(item);
		return convertView;
	}

	class ViewHolder {
		private ImageView item_image;
	}

}
