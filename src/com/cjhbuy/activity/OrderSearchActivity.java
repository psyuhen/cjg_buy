package com.cjhbuy.activity;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cjhbuy.adapter.OrderItemAdapter;
import com.cjhbuy.bean.OrderItem;
import com.cjhbuy.utils.CommonsUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 订单搜索
 * @author pansen
 *
 */
public class OrderSearchActivity extends Activity{
	private ListView mListView;
	private TextView back;
	private OrderItemAdapter orderItemAdapter;
	private List<OrderItem> orderlist;
	private Button search_btn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		initData();
		mListView=(ListView) findViewById(R.id.search_order_list);
		back=(TextView) findViewById(R.id.back);
		orderItemAdapter = new OrderItemAdapter(OrderSearchActivity.this, orderlist);
		search_btn=(Button) findViewById(R.id.search_btn);
		back.setOnClickListener(new OnClickListener() {
			
			@SuppressLint("NewApi")
			@Override
			public void onClick(View arg0) {
				onBackPressed();
			}
		});
		search_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mListView.setAdapter(orderItemAdapter);
			}
		});
		
	}
	public void initData() {
		orderlist = new ArrayList<OrderItem>();
		for (int i = 0; i < 10; i++) {
			OrderItem orderItem = new OrderItem();
			orderItem.setId(i);
			orderItem.setOrdertime(new Date());
			orderItem
					.setSerialnum(CommonsUtil.getDateStr(new Date()) + "-" + i);
			if (i % 4 == 0) {
				orderItem.setTempImage(R.drawable.order_item_image1);
				orderItem.setAddress("北京市东城区建国门内大街999号");
				orderItem.setGoodtitle("【口口福-薄壳巴旦木200gx2袋】零食 坚果特产 手剥巴旦木包邮");
				orderItem.setBuyer("科比");
				orderItem.setStatus('4');
			} else if (i % 4 == 1) {
				orderItem.setTempImage(R.drawable.fruit3);
				orderItem.setAddress("北京市东城区建国门内大街999号");
				orderItem.setGoodtitle("烟台红富士苹果新鲜水果");
				orderItem.setBuyer("詹姆斯");
				orderItem.setStatus('3');
			} else if (i % 4 == 2) {
				orderItem.setTempImage(R.drawable.fruit4);
				orderItem.setAddress("北京市东城区建国门内大街999号");
				orderItem.setGoodtitle("浙江水蜜桃黄桃约4.5斤装");
				orderItem.setBuyer("奥巴马");
				orderItem.setStatus('2');
			} else if (i % 4 == 3) {
				orderItem.setTempImage(R.drawable.fruit5);
				orderItem.setAddress("北京市东城区建国门内大街999号");
				orderItem.setGoodtitle("浙江水蜜桃黄桃约4.5斤装");
				orderItem.setBuyer("张凯强");
				orderItem.setStatus('1');
			}

			List<Integer> imglist = new ArrayList<Integer>();
			imglist.add(R.drawable.fruit4);
			imglist.add(R.drawable.fruit3);
			imglist.add(R.drawable.fruit5);
			imglist.add(R.drawable.fruit4);
			imglist.add(R.drawable.fruit3);
			imglist.add(R.drawable.fruit5);
			imglist.add(R.drawable.fruit4);
			imglist.add(R.drawable.fruit3);
			imglist.add(R.drawable.fruit5);
			orderItem.setImgList(imglist);
			orderItem.setNum(10);
			orderItem.setOrdertime(new Date());
			orderItem.setPrice(15);
			orderlist.add(orderItem);
		}
	}
}
