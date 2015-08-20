package com.cjhbuy.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.cjhbuy.activity.ChatActivity;
import com.cjhbuy.activity.EvaluateActivity;
import com.cjhbuy.activity.R;
import com.cjhbuy.bean.OrderItem;
import com.cjhbuy.utils.DateUtil;

public class OrderItemAdapter extends BaseAdapter {
	private List<OrderItem> orderlist;
	private Context context;

	public OrderItemAdapter(Context context, List<OrderItem> orderlist) {
		
		this.context = context;
		this.orderlist = orderlist;
	}

	@Override
	public int getCount() {
		
		return orderlist.size();
	}

	@Override
	public Object getItem(int position) {
		
		return orderlist.get(position);
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
					R.layout.item_in_order, null);
			viewHolder.ordertime_text = (TextView) convertView
					.findViewById(R.id.order_time);
			viewHolder.serialnumber_text = (TextView) convertView
					.findViewById(R.id.order_serial);
//			viewHolder.img_image = (ImageView) convertView
//					.findViewById(R.id.order_item_image);
			viewHolder.buyer_text = (TextView) convertView
					.findViewById(R.id.order_buyer);
			viewHolder.number_text = (TextView) convertView
					.findViewById(R.id.order_number);
			viewHolder.title_text = (TextView) convertView
					.findViewById(R.id.order_title);
			viewHolder.price_text = (TextView) convertView
					.findViewById(R.id.order_price);
			viewHolder.order_ratingBar_btn = (Button) convertView
					.findViewById(R.id.order_ratingBar_btn);
			viewHolder.order_communication_btn = (Button) convertView
					.findViewById(R.id.order_communication_btn);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		final OrderItem orderItem = orderlist.get(position);
		viewHolder.ordertime_text.setText(DateUtil.format(orderItem.getOrdertime()));
		viewHolder.serialnumber_text.setText(orderItem
				.getSerialnum());
		viewHolder.buyer_text.setText(orderItem.getBuyer());
		viewHolder.price_text.setText(orderItem.getPrice() + "");
		viewHolder.number_text.setText(orderItem.getNum() + "");
		viewHolder.title_text.setText(orderItem.getGoodtitle());

		//评分
		viewHolder.order_ratingBar_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, EvaluateActivity.class);
				intent.putExtra("order_id", orderItem.getSerialnum());
				context.startActivity(intent);
			}
		});
		//再次联系
		viewHolder.order_communication_btn
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(context, ChatActivity.class);
						intent.putExtra("seller_user_id", orderItem.getSeller_user_id());
						intent.putExtra("seller_user_name", orderItem.getSeller());
						intent.putExtra("seller_user_mobile", orderItem.getSeller_user_moblie());
						context.startActivity(intent);
					}
				});
		return convertView;
	}

	private class ViewHolder {
		private TextView buyer_text;
		private TextView ordertime_text;
		private TextView serialnumber_text;
	//	private ImageView img_image;
		private TextView price_text;
		private TextView title_text;
		private TextView number_text;
		private Button order_ratingBar_btn;
		private Button order_communication_btn;
	}

}
