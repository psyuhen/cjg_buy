package com.cjhbuy.activity;

import java.util.ArrayList;
import java.util.List;

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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.cjhbuy.adapter.MyOrderGoodsAdapter;
import com.cjhbuy.bean.AddressItem;
import com.cjhbuy.bean.GoodsItem;
import com.cjhbuy.bean.MerchDisacount;
import com.cjhbuy.bean.Order;
import com.cjhbuy.bean.OrderDetail;
import com.cjhbuy.common.Constants;
import com.cjhbuy.utils.AppContext;
import com.cjhbuy.utils.CommonsUtil;
import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;

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
	private String payWayOnline = "1";//默认为微信支付
	
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
		// 初始化商品状态
		initMoney();
		postage = CommonsUtil.postage(allmoney);//计算邮费 TODO 以后可能要修改哟
		
		my_order_delivery_money.setText("￥ " + postage);//邮费
		myorder_favourable_money.setText("￥ " + discountmoney);//优惠金额
		myorder_favourable_money.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
		myorder_goods_num.setText("共" + allnum + "件商品");//共多少商品
		// myorder_service_money.setText("￥ 0");
		
		
		//TODO 赠品以后再修改吧
		myorder_give.setText("福成有机牛奶原味[赠]");
		myorder_give_num.setText("X2");
		myorder_give_money.setText("￥ 0");
		if (postage > 0) {
			money_count.setText("￥" + allmoney + "+" + postage);
		} else {
			money_count.setText("￥" + allmoney);
		}
		
		Intent intent = getIntent();
		String store_name = intent.getStringExtra("store_name");
		goods_myorder_shop_name.setText(store_name);
	}
	
	//计算购物车里面的金额
	private void initMoney() {
		double[] listDisacount = app.getListDisacount();
		allnum = (int)listDisacount[0];
		discountmoney = listDisacount[1];
		allmoney = listDisacount[2];
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
		
		//地址
		myorder_address_rl = (RelativeLayout) headView.findViewById(R.id.myorder_address_rl);
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
		default:
			break;
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
		order.setAddress(address);//详细地址
		order.setSend_time(receiveTime);//送货时间
		order.setPay_type(payWayOnline);//付款方式
		
		int postage = CommonsUtil.postage(amount_money);
		order.setAmount_money((float)amount_money + postage);//成交金额
		order.setFreight(postage);//邮费
		
		//买家ID和名称
		int user_id = sessionManager.getUserId();
		order.setBuyer_user_id(user_id);
		String buyer_user_name = sessionManager.getUserName();
		order.setBuyer_user_name(buyer_user_name);
		order.setCurrency_unit("人民币");//币种
		order.setInvoice_need("0");//默认不需要发票
		
		order.setBuyer_del("0");//默认未删除
		order.setSeller_del("0");//默认未删除
		order.setBuyer_score("0");//默认未评分
		order.setSeller_score("0");//默认未评分
		
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
		
		Intent submitIntent = new Intent();
		submitIntent.setClass(MyOrderActivity.this,PayConfirmActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("order", order);
		submitIntent.putExtras(bundle);
		startActivity(submitIntent);
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
