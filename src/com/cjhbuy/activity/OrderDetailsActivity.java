package com.cjhbuy.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.cjhbuy.adapter.CommonAdapter;
import com.cjhbuy.adapter.ViewHolder;
import com.cjhbuy.bean.MerchGallery;
import com.cjhbuy.bean.Order;
import com.cjhbuy.bean.OrderDetail;
import com.cjhbuy.utils.CommonsUtil;
import com.cjhbuy.utils.FileUtil;
import com.cjhbuy.utils.HttpUtil;
import com.cjhbuy.utils.JsonUtil;
import com.cjhbuy.utils.StringUtil;
import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;
/**
 * 订单详情
 * @author pansen
 *
 */
public class OrderDetailsActivity extends BaseActivity {
	private Logger LOGGER = LoggerFactory.getLogger(OrderDetailsActivity.class);

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
	private List<OrderDetail> details;
	private CommonAdapter<OrderDetail> commonAdapter;
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
		//订单详细的头和尾
		View headView = LayoutInflater.from(this).inflate(R.layout.activity_orderdetails_header,null);
		View footView = LayoutInflater.from(this).inflate(R.layout.activity_orderdetails_footer, null);
		
		//订单编号
		order_details_serial = (TextView) headView.findViewById(R.id.order_details_serial);
		//订单时间
		order_details_time = (TextView) headView.findViewById(R.id.order_details_time);
		//支付方式
		order_details_payway = (TextView) headView.findViewById(R.id.order_details_payway);
		//订单状态
		order_details_status = (TextView) headView.findViewById(R.id.order_details_status);
		//收货人
		order_details_person = (TextView) footView.findViewById(R.id.order_details_person);
		//电话
		order_details_tel = (TextView) footView.findViewById(R.id.order_details_tel);
		//地址
		order_details_address = (TextView) footView.findViewById(R.id.order_details_address);
		//收货时间
		order_details_deliverytime = (TextView) footView.findViewById(R.id.order_details_deliverytime);
		//发票抬头
		order_details_bill = (TextView) footView.findViewById(R.id.order_details_bill);
		//金额
		order_details_allmoney = (TextView) footView.findViewById(R.id.order_details_allmoney);
		//邮费
		order_details_freight = (TextView) footView.findViewById(R.id.order_details_freight);
		//应付金额
		order_details_shoulpay = (TextView) footView.findViewById(R.id.order_details_shoulpay);
		
		order_details_goodslist = (ListView) findViewById(R.id.order_details_goodslist);
		order_details_evaluate_btn=(Button) footView.findViewById(R.id.order_details_evaluate_btn);
		order_details_evaluate_btn.setOnClickListener(this);
		
		order_details_goodslist.addHeaderView(headView);
		order_details_goodslist.addFooterView(footView);
		
		details = new ArrayList<OrderDetail>();
		commonAdapter = showAdapter();
		order_details_goodslist.setAdapter(commonAdapter);
	}

	private void initData() {
		
		title.setText("订单详情");
		Intent intent = getIntent();
		order_id = intent.getStringExtra("order_id");
		
		new queryOrderByIdTask().execute();
		
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
		String url = HttpUtil.BASE_URL + "/order/getDetailsById.do?orderId="+order_id;
		
		try {
			String json = HttpUtil.getRequest(url);
			if(json == null){
				return null;
			}
			
			Order order = JsonUtil.parse2Object(json, Order.class);
			return order;
		} catch (Exception e) {
			LOGGER.error(">>> 查询订单信息失败", e);
		}
		
		return null;
	}
	private void setOrderValue(Order order){
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
		if("0".equals(status)){
			order_details_status.setText("买家下单");
		} else if("1".equals(status)){
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
		order_details_address.setText(order.getAddress());
		order_details_deliverytime.setText(order.getSend_time());
		order_details_bill.setText(order.getInvoice_title());
		order_details_allmoney.setText("￥"+order.getAmount_money());
		order_details_freight.setText("￥"+order.getFreight());//运费
		
		//商品列表
		details.addAll(order.getOrderDetails());
		commonAdapter.notifyDataSetChanged();
		
		order_details_shoulpay.setText("￥"+(order.getAmount_money()+order.getFreight()));//应付金额
		right_imgbtn.setVisibility(View.GONE);
	}
	
	
	/**
	 * 显示适配数据
	 * 
	 * @return
	 */
	public CommonAdapter<OrderDetail> showAdapter() {
		return new CommonAdapter<OrderDetail>(OrderDetailsActivity.this, details, R.layout.item_order_details) {

			@Override
			public void convert(ViewHolder helper, OrderDetail item) {
				
				helper.setText(R.id.item_order_details_title,  item.getMerch_name());
				helper.setText(R.id.order_details_num, item.getAmount() + "");
				helper.setText(R.id.order_details_money, StringUtil.format2string(item.getPrice()));
				
				List<MerchGallery> merchGallerys = item.getMerchGallerys();
				if(merchGallerys == null || merchGallerys.isEmpty()){
					helper.setImageResource(R.id.item_order_details_image, R.drawable.login_head_icon);
				}else{
					MerchGallery merchGallery = merchGallerys.get(0);
					Bitmap file = FileUtil.getCacheFile(merchGallery.getName());
					if(file == null){
						helper.setImageResource(R.id.item_order_details_image, R.drawable.login_head_icon);
					}else{
						helper.setImageBitmap(R.id.item_order_details_image, file);
					}
				}
				
			}
		};
	}
	
	
	//根据ID查询订单信息
	private class queryOrderByIdTask extends AsyncTask<Void, Void, Order>{
		@Override
		protected Order doInBackground(Void... params) {
			return queryOrderById();
		}
		
		@Override
		protected void onPostExecute(Order result) {
			super.onPostExecute(result);
			if(result == null){
				CommonsUtil.showLongToast(getApplicationContext(), "查询订单信息失败");
				return;
			}
			
			setOrderValue(result);
		}
	}
}
