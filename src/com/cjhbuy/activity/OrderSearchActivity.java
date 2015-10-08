package com.cjhbuy.activity;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.cjhbuy.adapter.OrderItemAdapter;
import com.cjhbuy.bean.MerchGallery;
import com.cjhbuy.bean.Order;
import com.cjhbuy.bean.OrderDetail;
import com.cjhbuy.bean.OrderItem;
import com.cjhbuy.utils.CommonsUtil;
import com.cjhbuy.utils.DateUtil;
import com.cjhbuy.utils.FileUtil;
import com.cjhbuy.utils.HttpUtil;
import com.cjhbuy.utils.JsonUtil;
import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;

/**
 * 订单搜索
 * @author pansen
 *
 */
public class OrderSearchActivity extends BaseActivity{
	private static final Logger LOGGER = LoggerFactory.getLogger(OrderSearchActivity.class);

	private ListView mListView;
	private TextView back;
	private OrderItemAdapter orderItemAdapter;
	private List<OrderItem> orderlist;
	private Button search_btn;
	private EditText order_search_edit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		initView();
	}
	
	@Override
	public void initView() {
		super.initView();
		mListView=(ListView) findViewById(R.id.search_order_list);
		order_search_edit = (EditText)findViewById(R.id.order_search_edit);
		back=(TextView) findViewById(R.id.back);
		orderlist = new ArrayList<OrderItem>();
		orderItemAdapter = new OrderItemAdapter(OrderSearchActivity.this, orderlist);
		mListView.setAdapter(orderItemAdapter);
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
				searchOrder();
			}
		});
	}
	
	//查询已完成的订单
	private void searchOrder(){
		int user_id = sessionManager.getUserId();
		
		String url = HttpUtil.BASE_URL + "/order/searchOrderInfo.do";
		
		Map<String,Object> conditions = new HashMap<String,Object>();
		String condition = order_search_edit.getText().toString();
		if(!TextUtils.isEmpty(condition)){
			conditions.put("order_id", condition);
			conditions.put("merch_name", condition);
			conditions.put("buyer_mobile", condition);
			conditions.put("buyer_name", condition);
		}
		
		conditions.put("buyer_user_id", user_id);
		
		try {
			String listJson = HttpUtil.postRequest(url, conditions);
			if(listJson == null){
				return;
			}
			
			List<Order> list = JsonUtil.parse2ListOrder(listJson);
			
			if(list == null){
				LOGGER.warn("转换订单信息失败");
				return;
			}
			
			int length = list.size();
			if(length == 0){
				CommonsUtil.showLongToast(getApplicationContext(), "没有查询到订单信息");
				return;
			}
			//把之前的数据先清除
			orderlist.clear();
			
			for (int i = 0; i < length; i++) {
				Order order = list.get(i);
				List<OrderDetail> orderDetails = order.getOrderDetails();
				
				OrderItem orderItem = new OrderItem();
				orderItem.setId(i);
				orderItem.setBuyer_user_id(order.getBuyer_user_id()+"");
				orderItem.setSeller_user_id(order.getSeller_user_id()+"");
				orderItem.setSerialnum(order.getOrder_id());
//							orderItem.setAddress("aaaa");
				orderItem.setBuyer(order.getBuyer_user_name());
				orderItem.setOrdertime(DateUtil.parseDate(order.getTrad_time(), new String[]{"yyyyMMddHHmmss"}));
				orderItem.setPrice(order.getAmount_money());
				orderItem.setBuyer_user_mobile(order.getBuyer_phone());
				
				String status = order.getStatus();
				if(status != null && !"".equals(status)){
					char[] statusChars = status.toCharArray();
					orderItem.setStatus(statusChars[0]);
				}
				
				if(orderDetails != null && !orderDetails.isEmpty()){
					OrderDetail orderDetail = orderDetails.get(0);
					
					orderItem.setNum(orderDetail.getAmount());//默认显示第一个商品的数量
					orderItem.setGoodtitle(orderDetail.getMerch_name());//默认为第一个商品的名称
					//默认显示第一个商品的图片
					List<MerchGallery> merchGallerys = orderDetail.getMerchGallerys();
					if(merchGallerys != null && !merchGallerys.isEmpty()){
						int gallerySize = merchGallerys.size();
						List<Bitmap> bitmapList = new ArrayList<Bitmap>();
						for (int j = 0; j < gallerySize; j++) {
							Bitmap cacheFile = FileUtil.getCacheFile(merchGallerys.get(j).getName());
							if(cacheFile == null){
								continue;
							}
							bitmapList.add(cacheFile);
						}
						orderItem.setBitmapList(bitmapList);
					}
				}
				
				orderlist.add(orderItem);
			}
			
			orderItemAdapter.notifyDataSetChanged();
		} catch (Exception e) {
			LOGGER.error("查询订单信息失败", e);
			CommonsUtil.showLongToast(getApplicationContext(), "查询订单信息失败");
		}
	}
}
