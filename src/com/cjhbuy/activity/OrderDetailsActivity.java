package com.cjhbuy.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.cjhbuy.bean.Order;
import com.cjhbuy.bean.OrderDetail;
import com.cjhbuy.utils.CommonsUtil;
import com.cjhbuy.utils.HttpUtil;
import com.cjhbuy.utils.JsonUtil;
import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;

public class OrderDetailsActivity extends BaseActivity {
	private static final Logger LOGGER = LoggerFactory.getLogger(OrderDetailsActivity.class);

	private TextView order_details_serial;
	private TextView order_details_time;
	private TextView order_details_payway;
	private TextView order_details_status;
	private TextView order_details_person;
	private TextView order_details_tel;
	private TextView order_details_address;
	private TextView order_details_deliverytime;
	private TextView order_details_bill;
	private TextView order_details_allmoney;
	private TextView order_details_freight;
	private ListView order_details_goodslist;
	private TextView order_details_shoulpay;
	private List<Map<String, Object>> maps;
	private Button order_details_evaluate_btn;
	
	private String order_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_orderdetails);
		initView();
		initData();
	}

	@Override
	public void initView() {
		
		super.initView();
		order_details_serial = (TextView) findViewById(R.id.order_details_serial);
		order_details_time = (TextView) findViewById(R.id.order_details_time);
		order_details_payway = (TextView) findViewById(R.id.order_details_payway);
		order_details_status = (TextView) findViewById(R.id.order_details_status);
		order_details_person = (TextView) findViewById(R.id.order_details_person);
		order_details_tel = (TextView) findViewById(R.id.order_details_tel);
		order_details_address = (TextView) findViewById(R.id.order_details_address);
		order_details_deliverytime = (TextView) findViewById(R.id.order_details_deliverytime);
		order_details_bill = (TextView) findViewById(R.id.order_details_bill);
		order_details_allmoney = (TextView) findViewById(R.id.order_details_allmoney);
		order_details_freight = (TextView) findViewById(R.id.order_details_freight);
		order_details_goodslist = (ListView) findViewById(R.id.order_details_goodslist);
		order_details_shoulpay = (TextView) findViewById(R.id.order_details_shoulpay);
		order_details_evaluate_btn=(Button) findViewById(R.id.order_details_evaluate_btn);
		order_details_evaluate_btn.setOnClickListener(this);
	}

	private void initData() {
		
		title.setText("订单详情");
		Intent intent = getIntent();
		order_id = intent.getStringExtra("order_id");
		
		Order order = queryOrderById();
		
		order_details_serial.setText(order_id);
		order_details_time.setText(order.getTrad_time());
		String pay_type = order.getPay_type();
		if("0".equals(pay_type)){//支付宝
			order_details_payway.setText("支付宝支付");
		}else if("1".equals(pay_type)){//微信支付
			order_details_payway.setText("微信支付");
		}else if("2".equals(pay_type)){//现金交易
			order_details_payway.setText("现金交易");
		}
		String status = order.getStatus();
		if("1".equals(status)){
			order_details_status.setText("卖方确认订单");
		} else if("2".equals(status)){
			order_details_status.setText("买方付款");
		} else if("3".equals(status)){
			order_details_status.setText("卖方发货");
		} else if("4".equals(status)){
			order_details_status.setText("订单完成/买方确认收货");
		} else if("5".equals(status)){
			order_details_status.setText("订单取消");
		} else if("6".equals(status)){
			order_details_status.setText("订单关闭");
		}
		
		order_details_person.setText(order.getBuyer_name());
		order_details_tel.setText(order.getBuyer_phone());
		order_details_address.setText("北京市东城区建国门大街东单路口懂1000号");
		order_details_deliverytime.setText(order.getSend_time());
		order_details_bill.setText(order.getInvoice_title());
		order_details_allmoney.setText("￥"+order.getAmount_money());
		order_details_freight.setText("￥"+order.getFreight());//运费
		
		//商品列表
		maps = new ArrayList<Map<String, Object>>();
		List<OrderDetail> details = order.getOrderDetails();
		for (int i = 0; i < details.size(); i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			OrderDetail detail = details.get(i);
			
			map.put("title", detail.getMerch_name());
			map.put("image", R.drawable.snack1);
			map.put("num", detail.getAmount());
			map.put("money", detail.getPrice());
			maps.add(map);
		}
		
		SimpleAdapter simpleAdapter = new SimpleAdapter(
				OrderDetailsActivity.this, maps, R.layout.item_order_details,
				new String[] { "image", "title", "num", "money" }, new int[] {
						R.id.item_order_details_image,
						R.id.item_order_details_title, R.id.order_details_num,
						R.id.order_details_money });
		order_details_goodslist.setAdapter(simpleAdapter);
		order_details_shoulpay.setText("￥"+(order.getAmount_money()+order.getFreight()));//应付金额
		right_imgbtn.setVisibility(View.GONE);
		
	}
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.order_details_evaluate_btn:
			startActivity(new Intent(OrderDetailsActivity.this, EvaluateActivity.class));
			break;

		default:
			break;
		}
	}
	private Order queryOrderById(){
		String url = HttpUtil.BASE_URL + "/order/getOrderInfoById.do?orderId="+order_id;
		
		try {
			String json = HttpUtil.getRequest(url);
			if(json == null){
				CommonsUtil.showLongToast(getApplicationContext(), "查询订单信息失败");
				return null;
			}
			
			Order order = JsonUtil.parse2Object(json, Order.class);
			return order;
		} catch (InterruptedException e) {
			LOGGER.error(">>> 查询订单信息失败", e);
			CommonsUtil.showLongToast(getApplicationContext(), "查询订单信息失败");
		} catch (ExecutionException e) {
			LOGGER.error(">>> 查询订单信息失败", e);
			CommonsUtil.showLongToast(getApplicationContext(), "查询订单信息失败");
		}
		
		return null;
	}
}
