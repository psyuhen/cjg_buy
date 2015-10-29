package com.cjhbuy.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.cjhbuy.adapter.CommonAdapter;
import com.cjhbuy.adapter.MyOrderGoodsAdapter;
import com.cjhbuy.adapter.ViewHolder;
import com.cjhbuy.bean.AddressItem;
import com.cjhbuy.bean.Coupon;
import com.cjhbuy.bean.CouponItem;
import com.cjhbuy.bean.GoodsItem;
import com.cjhbuy.bean.Order;
import com.cjhbuy.bean.OrderDetail;
import com.cjhbuy.bean.ResponseInfo;
import com.cjhbuy.common.Constants;
import com.cjhbuy.utils.AppContext;
import com.cjhbuy.utils.CommonsUtil;
import com.cjhbuy.utils.DateUtil;
import com.cjhbuy.utils.HttpUtil;
import com.cjhbuy.utils.JsonUtil;
import com.cjhbuy.utils.StringUtil;
import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;

/**
 * 下单
 * @author pansen
 *
 */
public class MyOrderActivity extends BaseActivity {
	private static final Logger LOGGER = LoggerFactory.getLogger(MyOrderActivity.class);

	// 订单列表
	private ListView myorder_cart_listview;
	// 地址
	private RelativeLayout myorder_address_rl;
	// 收获时间
	private RelativeLayout myorder_receive_time_rl;
	// 付款方式
	private RelativeLayout myorder_pay_rl;
	// 收货时间对话框
	private AlertDialog ReceiveTimeDialog = null;
	// 现在配送按钮
	private Button nowSendBtn;
	// 预约配送按钮
	private Button preSetBtn;
	// 结算按钮
	private Button submit_goods_btn;
	// 配送费
	private TextView my_order_delivery_money;
	// 优惠金额
	private TextView myorder_favourable_money;
	// 赠品
	private TextView myorder_give;
	// 赠品数量
	private TextView myorder_give_num;
	// 赠品钱数
	private TextView myorder_give_money;
	// 总价
	private TextView money_count;
	// 商品数量
	private TextView myorder_goods_num;

	// 默认加入的优惠券
	private TextView coupons_text;
	private float coupons_money = 0f;
	// 选择优惠券
	private AlertDialog chooseCouponsDialog = null;
	// 优惠券的相对布局
	private RelativeLayout myorder_coupons_rl;
	// 删除优惠券alertdiolag布局
	private ImageButton delete_coupon_list_btn;
	private CommonAdapter<CouponItem> couponsAdapter;

	// 选择优惠券的ListView
	private ListView couponsListView;

	private List<CouponItem> couponslist;

	// 预设时间对话框
	private AlertDialog PresetTimeDialog = null;
	// 预设时间的控件
	private EditText showDate = null;
	private Button pickDate = null;
	private EditText showTime = null;
	private Button pickTime = null;
	// 时间控件常量
	private static final int SHOW_DATAPICK = 0;
	private static final int DATE_DIALOG_ID = 1;
	private static final int SHOW_TIMEPICK = 2;
	private static final int TIME_DIALOG_ID = 3;

	// 年月日时分秒
	private int mYear;
	private int mMonth;
	private int mDay;
	private int mHour;
	private int mMinute;

	private Button preset_time_confirm_btn;
	// 全局参数
	private AppContext app;
	// 列表的头和尾部
	private View headView;
	private View footView;
	// 全选按钮
//	private CheckBox select_all_checkbox;
	// 适配器
	private MyOrderGoodsAdapter adapter;
	private double allmoney;
	private double discountmoney;
	private int postage;
	private int allnum;
	
	//收货地址
	private TextView my_order_name;//收货人
	private TextView my_order_tel;//电话
	private TextView my_order_address;//详细地址
	
	//收货时间
	private TextView my_order_receive_time;//时间
	
	//付款方式
	private TextView my_order_pay;//付款方式
	private String payWayOnline = "2";//默认为货到付款
	
	//商店名称
	private TextView goods_myorder_shop_name;//商店名称
	
	//地址
	private AddressItem addressItem;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myorder);
		initView();
		initData();
	}

	private void initData() {
		coupons_text.setText("");

		// TODO 赠品以后再修改吧
		myorder_give.setText("福成有机牛奶原味[赠]");
		myorder_give_num.setText("X2");
		myorder_give_money.setText("￥ 0");
		
		Intent intent = getIntent();
		String store_name = intent.getStringExtra("store_name");
		goods_myorder_shop_name.setText(store_name);
		
		queryAddress();
		
		queryCoupon();
		
		
		// 计算金额
		initMoney();
		
		//计算金额并显示
		calMoney();
	}

	// 计算购物车里面的金额
	private void initMoney() {
		double[] listDisacount = app.getListDisacount();
		allnum = (int)listDisacount[0];
		discountmoney = listDisacount[1];
		allmoney = listDisacount[2];
	}
	
	//
	private void calMoney(){
		double money = allmoney - coupons_money;//减去优惠金额
		double discount = discountmoney + coupons_money;//加上优惠金额
		postage = CommonsUtil.postage(allmoney);//计算邮费 TODO 以后可能要修改哟
		
		my_order_delivery_money.setText("￥ " + postage);//邮费
		myorder_favourable_money.setText("￥ " + discount);//优惠金额
		myorder_favourable_money.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
		myorder_goods_num.setText("共" + allnum + "件商品");//共多少商品
		// myorder_service_money.setText("￥ 0");
		if (postage > 0) {
			money_count.setText("￥" + money + "+" + postage);
		} else {
			money_count.setText("￥" + money);
		}
	}

	@Override
	public void initView() {
		super.initView();
		title.setText("我的下单");
		//下单的头和尾
		headView = LayoutInflater.from(this).inflate(R.layout.item_myorder_top,null);
		footView = LayoutInflater.from(this).inflate(R.layout.item_myorder_bottom, null);
		
		//要购买的所有商品
		myorder_cart_listview = (ListView) findViewById(R.id.myorder_cart_listview);
		// 优惠券
		coupons_text = (TextView) headView.findViewById(R.id.my_order_coupons);
		myorder_coupons_rl = (RelativeLayout) headView.findViewById(R.id.myorder_coupons_rl);
		myorder_coupons_rl.setOnClickListener(this);

		// 地址
		myorder_address_rl = (RelativeLayout) headView
				.findViewById(R.id.myorder_address_rl);
		myorder_address_rl.setOnClickListener(this);
		
		//收货人，电话，详细地址
		my_order_name = (TextView) headView.findViewById(R.id.my_order_name);
		my_order_tel = (TextView) headView.findViewById(R.id.my_order_tel);
		my_order_address = (TextView) headView.findViewById(R.id.my_order_address);
		
		//收货时间
		myorder_receive_time_rl = (RelativeLayout) headView.findViewById(R.id.myorder_receive_time_rl);
		myorder_receive_time_rl.setOnClickListener(this);
		
		//具体时间
		my_order_receive_time = (TextView) headView.findViewById(R.id.my_order_receive_time);
		
		//支付方式
		myorder_pay_rl = (RelativeLayout) headView.findViewById(R.id.myorder_pay_rl);
		myorder_pay_rl.setOnClickListener(this);
		
		//付款方式
		my_order_pay = (TextView) headView.findViewById(R.id.my_order_pay);
		
		//商店名称
		goods_myorder_shop_name = (TextView) headView.findViewById(R.id.goods_myorder_shop_name);
		
		//结算
		submit_goods_btn = (Button) findViewById(R.id.submit_goods_btn);
		submit_goods_btn.setOnClickListener(this);

		//邮费
		my_order_delivery_money = (TextView) footView.findViewById(R.id.my_order_delivery_money);
		//优惠金额
		myorder_favourable_money = (TextView) footView.findViewById(R.id.myorder_favourable_money);
		//商品数量
		myorder_goods_num = (TextView) footView.findViewById(R.id.myorder_goods_num);
		//赠品信息（TODO 赠品信息以后可能也得修改）
		myorder_give = (TextView) footView.findViewById(R.id.myorder_give);
		myorder_give_num = (TextView) footView.findViewById(R.id.myorder_give_num);
		myorder_give_money = (TextView) footView.findViewById(R.id.myorder_give_money);
		money_count = (TextView) findViewById(R.id.money_count);
		//
		app = (AppContext) getApplication();

//		select_all_checkbox = (CheckBox) findViewById(R.id.select_all_checkbox);
		
		adapter = new MyOrderGoodsAdapter(MyOrderActivity.this,
				app.getCartGoodLists(), money_count, myorder_goods_num,
				my_order_delivery_money, myorder_favourable_money);
		// myorder_cart_listview.setAdapter(showAdapter());
		myorder_cart_listview.setAdapter(adapter);
		myorder_cart_listview.addHeaderView(headView);
		myorder_cart_listview.addFooterView(footView);
		
		couponslist = new ArrayList<CouponItem>();
	}

	/**
	 * 显示适配数据
	 * 
	 * @return
	 */
	/*public CommonAdapter<GoodsItem> showAdapter() {
		return new CommonAdapter<GoodsItem>(MyOrderActivity.this,
				app.getCartGoodLists(), R.layout.item_cart_goodslist) {

			@Override
			public void convert(ViewHolder helper, final GoodsItem item) {
				helper.setText(R.id.cart_goods_title, item.getTitle());
				helper.setText(R.id.cart_goods_price, "￥ " + item.getPrice());
				helper.setText(R.id.goods_item_stock, "0");
				helper.setImageResource(R.id.cart_goods_imageview,item.getImage());
				CheckBox checkBox = helper.getView(R.id.cart_goods_select_checkbox);
				checkBox.setChecked(item.isChoose());
			}
		};
	}*/
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case Constants.ADDRESS_REQUEST_CODE://选择地址返回的
			if(data != null){
				Bundle extras = data.getExtras();
				AddressItem item = (AddressItem)extras.getSerializable("address");
				addressItem = item;
				my_order_name.setText(item.getUser_name());
				my_order_tel.setText(item.getMobile());
				my_order_address.setText(StringUtils.trimToEmpty(item.getProvince())
						+ StringUtils.trimToEmpty(item.getCity())
						+ StringUtils.trimToEmpty(item.getTown())
						+ StringUtils.trimToEmpty(item.getAddress()));
			}
			break;
		case Constants.PAYWAY_REQUEST_CODE://付款方式
			if(data != null){
				String payWayOnline = data.getStringExtra("payWayOnline");
				String payWayText = "";
				if("2".equals(payWayOnline)){
					payWayText = "货到付款";
				}else if("1".equals(payWayOnline)){
					payWayText = "微信付款";
				}else if("0".equals(payWayOnline)){
					payWayText = "支付宝付款";
				}
				my_order_pay.setText(payWayText);
			}
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}


	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.myorder_address_rl://选择地址
			Intent intent = new Intent();
			intent.setClass(MyOrderActivity.this, AddressActivity.class);
			intent.putExtra("from", "myorder");
			startActivityForResult(intent, Constants.ADDRESS_REQUEST_CODE);
			break;
		case R.id.myorder_receive_time_rl://选择送货时间
			showReceiveTime();
			break;
		case R.id.myorder_pay_rl://选择支付方式
			Intent it = new Intent();
			it.setClass(MyOrderActivity.this, PayWayActivity.class);
			it.putExtra("from", "myorder");
			startActivityForResult(it, Constants.PAYWAY_REQUEST_CODE);
			break;
		case R.id.dialog_now_send://现在送货
			ReceiveTimeDialog.dismiss();
			my_order_receive_time.setText("现在送货");
			break;
		case R.id.dialog_pre_set://预设时间
			ReceiveTimeDialog.dismiss();
			showPresetTimeDialog();
			break;
		case R.id.submit_goods_btn://结算
			submitOrders();
			break;
		case R.id.myorder_coupons_rl: // 选取优惠券
			chooseCoupons();
			break;
		default:
			break;
		}
	}
	/**
	 * 选择优惠券
	 */
	private void chooseCoupons() {
		chooseCouponsDialog = new AlertDialog.Builder(MyOrderActivity.this).create();
		chooseCouponsDialog.show();
		chooseCouponsDialog.getWindow().setContentView(R.layout.dialog_choose_coupons);
		couponsListView = (ListView) chooseCouponsDialog.findViewById(R.id.couponsListView);
		couponsAdapter = showAdapter();
		couponsListView.setAdapter(couponsAdapter);
		delete_coupon_list_btn=(ImageButton) chooseCouponsDialog.findViewById(R.id.delete_coupon_list_btn);
		/**
		 * 关闭优惠券选择框
		 */
		delete_coupon_list_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				chooseCouponsDialog.dismiss();
			}
		});
		/**
		 * 选中优惠券
		 */
		couponsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long len) {
				chooseCouponsDialog.dismiss();
				CouponItem couponItem = (CouponItem)parent.getItemAtPosition(position);
				float price = couponItem.getPrice();
				coupons_text.setText("￥" + price + "元优惠券");
				coupons_money = price;
				adapter.setCoupons_money(coupons_money);
				calMoney();
			}
		});
	}

	public CommonAdapter<CouponItem> showAdapter() {
		return new CommonAdapter<CouponItem>(MyOrderActivity.this, couponslist,
				R.layout.item_coupon_list) {

			@Override
			public void convert(ViewHolder helper, CouponItem item) {
				helper.setText(R.id.item_price, "￥" + item.getPrice() + "元");
				helper.setText(
						R.id.item_date,DateUtil.format(item.getStartTime())
								+ " 至 "
								+ DateUtil.format(item.getEndTime()));
			}
		};
	}
	
	//默认查询收货地址
	private void queryAddress(){
		int user_id = sessionManager.getUserId();
		
		String url = HttpUtil.BASE_URL + "/freqa/query.do?user_id="+user_id;
		try {
			String json = HttpUtil.getRequest(url);
			if(json == null){
				return;
			}
			List<AddressItem> list = JsonUtil.parse2ListAddressItem(json);
			if(!list.isEmpty()){
				AddressItem item = list.get(0);
				my_order_name.setText(item.getUser_name());
				my_order_tel.setText(item.getMobile());
				my_order_address.setText(StringUtils.trimToEmpty(item.getProvince())
						+ StringUtils.trimToEmpty(item.getCity())
						+ StringUtils.trimToEmpty(item.getTown())
						+ StringUtils.trimToEmpty(item.getAddress()));
			}
			list = null;
		} catch (Exception e) {
			LOGGER.error("查询常用地址失败", e);
			CommonsUtil.showLongToast(getApplicationContext(), "查询常用地址失败");
		}
	
	}
	
	//查询优惠券
	private void queryCoupon(){
		List<GoodsItem> cartGoodLists = app.getCartGoodLists();
		if(cartGoodLists == null || cartGoodLists.isEmpty()){
			return;
		}
		
		int store_id = cartGoodLists.get(0).getStore_id();
		int user_id = sessionManager.getUserId();
		
		String url = HttpUtil.BASE_URL + "/coupon/queryByCouponUserId.do";
		
		try {
			Map<String,String> map = new HashMap<String, String>();
			map.put("store_id", store_id + "");
			map.put("buyer_user_id", user_id+"");
			String listJson = HttpUtil.postRequest(url,map);
			if(listJson == null){
				return;
			}
			
			List<Coupon> list = JsonUtil.parse2ListCoupon(listJson);
			
			int length = list.size();
			for (int i = 0; i < length; i++) {
				Coupon coupon = list.get(i);
				CouponItem couponItem = new CouponItem();
				couponItem.setRange(StringUtils.trimToEmpty(coupon.getDesc()));//优惠描述吧
				couponItem.setPrice(coupon.getCoupon_money());
				couponItem.setStartTime(DateUtil.parseDate(coupon.getStart_time(), new String[]{"yyyyMMddHHmmss"}));
				couponItem.setEndTime(DateUtil.parseDate(coupon.getEnd_time(), new String[]{"yyyyMMddHHmmss"}));
				
				Date now = new Date();
				if(couponItem.getStartTime().before(now) && couponItem.getEndTime().after(now)){
					couponslist.add(couponItem);
				}
			}
			if(couponslist.isEmpty()){
				coupons_text.setText("");
			}else{
				CouponItem couponItem = couponslist.get(0);
				coupons_text.setText("￥"+StringUtil.format2float(couponItem.getPrice())+"优惠券");
				coupons_money = couponItem.getPrice();
				adapter.setCoupons_money(coupons_money);
			}
			if(couponsAdapter != null){
				couponsAdapter.notifyDataSetChanged();
			}
			list = null;
		} catch (Exception e) {
			LOGGER.error("查询优惠券信息失败", e);
			CommonsUtil.showLongToast(getApplicationContext(), "查询优惠券信息失败");
		}
	}

	private void submitOrders(){
		//点击结算之前，先判断有没有选择地址了/有没有选择送货时间了/有没有选择商品了
		String name = my_order_name.getText().toString();//姓名
		if(TextUtils.isEmpty(StringUtils.trimToEmpty(name))){
			CommonsUtil.showLongToast(getApplicationContext(), "姓名不能为空!");
			return;
		}
		
		String tel = my_order_tel.getText().toString();//电话
		if(TextUtils.isEmpty(StringUtils.trimToEmpty(tel))){
			CommonsUtil.showLongToast(getApplicationContext(), "电话不能为空!");
			return;
		}
		
		String address = my_order_address.getText().toString();//详细地址
		if(TextUtils.isEmpty(StringUtils.trimToEmpty(address))){
			CommonsUtil.showLongToast(getApplicationContext(), "详细地址不能为空!");
			return;
		}
		
		String receiveTime = my_order_receive_time.getText().toString();//收货时间
		if(TextUtils.isEmpty(StringUtils.trimToEmpty(receiveTime))){
			CommonsUtil.showLongToast(getApplicationContext(), "收货时间不能为空!");
			return;
		}
		
		String orderPay = my_order_pay.getText().toString();//付款方式
		if(TextUtils.isEmpty(StringUtils.trimToEmpty(orderPay))){
			CommonsUtil.showLongToast(getApplicationContext(), "付款方式不能为空!");
			return;
		}
		
		//有没有选择商品了
		List<GoodsItem> cartGoodLists = app.getCartGoodLists();
		if(cartGoodLists.isEmpty()){
			CommonsUtil.showLongToast(getApplicationContext(), "没有购买的商品!");
			return;
		}
		double amount_money = adapter.getAll_money();//获取实际金额
		
		
		Order order = new Order();
		order.setBuyer_name(name);//收货人
		order.setBuyer_mobile(tel);
		order.setBuyer_phone(tel);
		order.setAddress(address);//详细地址
		order.setSend_time(receiveTime);//送货时间
		order.setPay_type(payWayOnline);//付款方式
		
		int postage = CommonsUtil.postage(amount_money);
		order.setAmount_money((float)amount_money - coupons_money + postage);//成交金额
		order.setFreight(postage);//邮费
		
		//买家ID和名称
		int user_id = sessionManager.getUserId();
		order.setBuyer_user_id(user_id);
		String buyer_user_name = sessionManager.getUserName();
		order.setBuyer_user_name(buyer_user_name);
		order.setCurrency_unit("人民币");//币种
		order.setInvoice_need("0");//默认不需要发票
		
		//卖家信息
		
		order.setBuyer_del("0");//默认未删除
		order.setSeller_del("0");//默认未删除
		order.setBuyer_score("0");//默认未评分
		order.setSeller_score("0");//默认未评分
		order.setStatus("0");//买方待付款
		
		//如果有多个商家的商品，将拆分订单。
		List<OrderDetail> orderDetails = new ArrayList<OrderDetail>();
		for (GoodsItem goodsItem : cartGoodLists) {
			OrderDetail orderDetail = new OrderDetail();
			orderDetail.setMerch_id(goodsItem.getId());
			orderDetail.setAmount(goodsItem.getSellmount());//成交数量
			orderDetail.setPrice((float)goodsItem.getPrice());//价格
			orderDetail.setUnit(goodsItem.getUnit());
			orderDetail.setMerch_name(goodsItem.getTitle());
			
			orderDetails.add(orderDetail);
		}
		order.setOrderDetails(orderDetails);
		
		//还需要计算价格呢
		
		
		createOrder(order);
	}
	
	/**
	 * 创建订单
	 * @param order
	 */
	private void createOrder(Order order){
		String url = HttpUtil.BASE_URL + "/order/createOrder.do";
		try {
			String json = HttpUtil.postRequest(url, order);
			if(json == null){
				CommonsUtil.showLongToast(getApplicationContext(), "订单生成失败！");
				return;
			}
			
			ResponseInfo responseInfo = JsonUtil.parse2Object(json, ResponseInfo.class);
			if(ResponseInfo.SUCCESS.equals(responseInfo.getStatus())){
				/*Intent submitIntent = new Intent();
				submitIntent.setClass(MyOrderActivity.this,PayConfirmActivity.class);
				Bundle bundle = new Bundle();
				order.setOrder_id(String.valueOf(responseInfo.getDataObj()));
				bundle.putSerializable("order", order);
				submitIntent.putExtras(bundle);
				startActivity(submitIntent);*/
				
				//TODO 不再跳转到支付，跳转到其他商家提示页面
				Intent intent = new Intent();
				intent.putExtra("store_id", app.getStore_id());
				intent.putExtra("store_name", app.getStore_name());
				intent.setClass(MyOrderActivity.this, WaitOrderConfirmActivity.class);
				startActivity(intent);
				
				//TODO 订单成功后，删除对应购物车里面的商品
				
			}
			
			CommonsUtil.showLongToast(getApplicationContext(), responseInfo.getDesc());
		} catch (Exception e) {
			LOGGER.error("生成订单失败", e);
			CommonsUtil.showLongToast(getApplicationContext(), "生成订单失败");
		}
	}
	
	/**
	 * 处理日期和时间控件的Handler
	 */
	Handler dateandtimeHandler = new Handler() {
		@SuppressLint("HandlerLeak")
		@SuppressWarnings("deprecation")
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MyOrderActivity.SHOW_DATAPICK:
				showDialog(DATE_DIALOG_ID);
				break;
			case MyOrderActivity.SHOW_TIMEPICK:
				showDialog(TIME_DIALOG_ID);
				break;
			}
		}
	};

	/**
	 * 日期控件的事件
	 */
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;

			updateDateDisplay();
		}
	};
	/**
	 * 时间控件事件
	 */
	private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			mHour = hourOfDay;
			mMinute = minute;

			updateTimeDisplay();
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,mDay);
		case TIME_DIALOG_ID:
			return new TimePickerDialog(this, mTimeSetListener, mHour, mMinute,true);
		}

		return null;
	}

	/**
	 * 更新时间显示
	 */
	private void updateTimeDisplay() {
		showTime.setText(new StringBuilder().append(mHour).append(":")
				.append((mMinute < 10) ? "0" + mMinute : mMinute));
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case DATE_DIALOG_ID:
			((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);
			break;
		case TIME_DIALOG_ID:
			((TimePickerDialog) dialog).updateTime(mHour, mMinute);
			break;
		}
	}

	private void showPresetTimeDialog() {
		PresetTimeDialog = new AlertDialog.Builder(MyOrderActivity.this).create();
		PresetTimeDialog.show();
		PresetTimeDialog.getWindow().setContentView(R.layout.dialog_preset_time);
		
		showDate = (EditText) PresetTimeDialog.findViewById(R.id.showdate);
		pickDate = (Button) PresetTimeDialog.findViewById(R.id.pickdate);
		showTime = (EditText) PresetTimeDialog.findViewById(R.id.showtime);
		pickTime = (Button) PresetTimeDialog.findViewById(R.id.picktime);
		preset_time_confirm_btn = (Button) PresetTimeDialog.findViewById(R.id.preset_time_confirm_btn);
		
		pickDate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Message msg = new Message();
				if (pickDate.equals(v)) {
					msg.what = MyOrderActivity.SHOW_DATAPICK;
				}
				MyOrderActivity.this.dateandtimeHandler.sendMessage(msg);
			}
		});

		pickTime.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Message msg = new Message();
				if (pickTime.equals(v)) {
					msg.what = MyOrderActivity.SHOW_TIMEPICK;
				}
				MyOrderActivity.this.dateandtimeHandler.sendMessage(msg);
			}
		});
		preset_time_confirm_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PresetTimeDialog.dismiss();
				//设置收货时间
				String date = showDate.getText().toString();
				String time = showTime.getText().toString();
				my_order_receive_time.setText(date + time);
			}
		});
	}

	/**
	 * 更新日期显示
	 */
	private void updateDateDisplay() {
		showDate.setText(new StringBuilder().append(mYear).append("-")
				.append((mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1))
				.append("-").append((mDay < 10) ? "0" + mDay : mDay));
	}

	private void showReceiveTime() {
		ReceiveTimeDialog = new AlertDialog.Builder(MyOrderActivity.this).create();
		ReceiveTimeDialog.show();
		ReceiveTimeDialog.getWindow().setContentView(R.layout.dialog_receive_time);
		nowSendBtn = (Button) ReceiveTimeDialog.findViewById(R.id.dialog_now_send);
		nowSendBtn.setOnClickListener(this);
		preSetBtn = (Button) ReceiveTimeDialog.findViewById(R.id.dialog_pre_set);
		preSetBtn.setOnClickListener(this);
	}

}
