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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.cjhbuy.adapter.CommonAdapter;
import com.cjhbuy.adapter.ViewHolder;
import com.cjhbuy.bean.AddressItem;
import com.cjhbuy.bean.GoodsItem;
import com.cjhbuy.bean.Order;
import com.cjhbuy.common.Constants;
import com.cjhbuy.utils.CommonsUtil;

public class MyOrderActivity extends BaseActivity {
	// 订单列表
	private ListView myorder_cart_listview;
	// 购物车的的商品
	private List<GoodsItem> goodsList;
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
	// 服务费
	private TextView myorder_service_money;
	// 赠品
	private TextView myorder_give;
	// 赠品数量
	private TextView myorder_give_num;
	// 赠品钱数
	private TextView myorder_give_money;
	// 总价
	private TextView money_count;

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
	
	private TextView my_order_name;//姓名
	private TextView my_order_tel;//电话
	private TextView my_order_address;//详细地址
	private TextView my_order_receive_time;//收货时间
	private TextView my_order_pay;//付款方式
	
	private String payWayOnline = "";//支付方式

	private CheckBox select_all_checkbox;//全选
	private CommonAdapter<GoodsItem> adapter;
	private int selectedNum = 0;
	
	private List<View> selectedList = new ArrayList<View>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myorder);
		initView();
		initData();
	}

	private void initData() {
		
		for (int i = 0; i < 2; i++) {
			GoodsItem goodsItem = new GoodsItem();
			goodsItem.setTitle("丹麦进口 Kjeldsens 蓝罐 曲奇 礼盒 908g");
			goodsItem.setPrice(109);
			goodsList.add(goodsItem);
		}

		adapter.notifyDataSetChanged();
		
		my_order_delivery_money.setText("￥ 5");
		myorder_favourable_money.setText("￥ 0");
		myorder_service_money.setText("￥ 0");
		myorder_give.setText("福成有机牛奶原味[赠]");
		myorder_give_num.setText("X2");
		myorder_give_money.setText("￥ 0");
		money_count.setText("￥ 7+5");

	}

	@Override
	public void initView() {
		super.initView();
		title.setText("我的下单");
		goodsList = new ArrayList<GoodsItem>();
		adapter = showAdapter();
		myorder_cart_listview = (ListView) findViewById(R.id.myorder_cart_listview);
		myorder_cart_listview.setAdapter(adapter);
		
		myorder_address_rl = (RelativeLayout) findViewById(R.id.myorder_address_rl);
		myorder_address_rl.setOnClickListener(this);
		myorder_receive_time_rl = (RelativeLayout) findViewById(R.id.myorder_receive_time_rl);
		myorder_receive_time_rl.setOnClickListener(this);
		myorder_pay_rl = (RelativeLayout) findViewById(R.id.myorder_pay_rl);
		myorder_pay_rl.setOnClickListener(this);
		submit_goods_btn = (Button) findViewById(R.id.submit_goods_btn);//结算
		submit_goods_btn.setOnClickListener(this);

		my_order_delivery_money = (TextView) findViewById(R.id.my_order_delivery_money);
		myorder_favourable_money = (TextView) findViewById(R.id.myorder_favourable_money);
		myorder_service_money = (TextView) findViewById(R.id.myorder_service_money);
		myorder_give = (TextView) findViewById(R.id.myorder_give);
		myorder_give_num = (TextView) findViewById(R.id.myorder_give_num);
		myorder_give_money = (TextView) findViewById(R.id.myorder_give_money);
		money_count = (TextView) findViewById(R.id.money_count);
		
		//姓名
		my_order_name = (TextView) findViewById(R.id.my_order_name);
		my_order_tel = (TextView) findViewById(R.id.my_order_tel);
		my_order_address = (TextView) findViewById(R.id.my_order_address);
		my_order_receive_time = (TextView) findViewById(R.id.my_order_receive_time);
		my_order_pay = (TextView) findViewById(R.id.my_order_pay);
		
		//全选
		select_all_checkbox = (CheckBox)findViewById(R.id.select_all_checkbox);
		select_all_checkbox.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				CheckBox checkBox = (CheckBox)view;
				boolean isChecked = checkBox.isChecked();
				int length = goodsList.size();
				if(isChecked){//全选
					for (int i = 0; i < length; i++) {
						GoodsItem item = goodsList.get(i);
						item.setChecked(true);
					}
					selectedNum = length;
				}else{//反选
					for (int i = 0; i < length; i++) {
						GoodsItem item = goodsList.get(i);
						item.setChecked(false);
					}
					selectedNum = 0;
					selectedList.clear();
				}
				adapter.notifyDataSetChanged();
			}
		});
	}

	/**
	 * 显示适配数据
	 * 
	 * @return
	 */
	public CommonAdapter<GoodsItem> showAdapter() {
		return new CommonAdapter<GoodsItem>(MyOrderActivity.this, goodsList,
				R.layout.item_cart_goodslist) {

			@Override
			public void convert(ViewHolder helper, GoodsItem item) {
				CheckBox checkbox = (CheckBox)helper.getView(R.id.cart_goods_select_checkbox);
				checkbox.setChecked(item.isChecked());
				
				helper.setText(R.id.cart_goods_title, item.getTitle());
				helper.setText(R.id.cart_goods_price, "￥ " + item.getPrice());
				helper.setText(R.id.goods_item_stock, "0");
				
				Button goods_minus_btn = helper.getView(R.id.goods_minus_btn);
				Button goods_add_btn = helper.getView(R.id.goods_add_btn);
				
				goods_minus_btn.setOnClickListener(new ClickHandler(helper));
				goods_add_btn.setOnClickListener(new ClickHandler(helper));
				checkbox.setOnClickListener(new CheckedHandler(helper));
			}
		};
	}
	//checkbox点击事件
	class CheckedHandler implements OnClickListener{
		ViewHolder holder;
		public CheckedHandler(ViewHolder holder) {
			this.holder = holder;
		}
		@Override
		public void onClick(View view) {
			CheckBox checkBox = (CheckBox)view;
			boolean isChecked = checkBox.isChecked();
			if(isChecked){
				selectedNum ++;
			}else{
				selectedNum --;
			}
			
			int length = goodsList.size();
			
			if(selectedNum != length){
				select_all_checkbox.setChecked(false);
			}else{
				select_all_checkbox.setChecked(true);
			}
		}
	}
	
	//购物车里面的添加或者减少数量
	class ClickHandler implements OnClickListener{
		ViewHolder holder;
		public ClickHandler(ViewHolder holder) {
			this.holder = holder;
		}
		@Override
		public void onClick(View v) {
			EditText goods_item_stock = (EditText)holder.getView(R.id.goods_item_stock);
			String stock = goods_item_stock.getText().toString();
			stock = StringUtils.trimToEmpty(stock);
			stock = "".equals(stock) ? "0" : stock;
			int num = Integer.parseInt(stock);
			
			switch(v.getId()){
			case R.id.goods_minus_btn:
				num --;
				if(num < 0){
					num = 0;
				}
				goods_item_stock.setText(String.valueOf(num));
				break;
			case R.id.goods_add_btn:
				num ++;
				
				goods_item_stock.setText(String.valueOf(num));
				break;
			default:
				break;
			}
		}
	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case Constants.ADDRESS_REQUEST_CODE://选择地址返回的
			if(data != null){
				Bundle extras = data.getExtras();
				AddressItem item = (AddressItem)extras.getSerializable("address");
				my_order_name.setText(item.getUser_name());
				my_order_tel.setText(item.getMobile());
				my_order_address.setText(item.getAddress());
			}
			break;
		case Constants.PAYWAY_REQUEST_CODE:
			if(data != null){
				payWayOnline = data.getStringExtra("payWayOnline");
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
		if(selectedNum == 0){
			CommonsUtil.showLongToast(getApplicationContext(), "请选择一个需要购买的商品!");
			return;
		}
		
		Order order = new Order();
		order.setBuyer_name(name);//收货人
		order.setAddress(address);//详细地址
		order.setSend_time(receiveTime);//送货时间
		order.setPay_type(orderPay);//付款方式
		
		//还需要计算价格呢
		
		Intent submitIntent = new Intent();
		submitIntent.setClass(MyOrderActivity.this,
				PayConfirmActivity.class);
		startActivity(submitIntent);
	}

	// /**
	// * 设置时间
	// */
	// private void setTimeOfDay() {
	// final Calendar c = Calendar.getInstance();
	// mHour = c.get(Calendar.HOUR_OF_DAY);
	// mMinute = c.get(Calendar.MINUTE);
	// updateTimeDisplay();
	// }
	//
	// /**
	// * 设置日期
	// */
	// private void setDateTime() {
	// final Calendar c = Calendar.getInstance();
	//
	// mYear = c.get(Calendar.YEAR);
	// mMonth = c.get(Calendar.MONTH);
	// mDay = c.get(Calendar.DAY_OF_MONTH);
	// updateDateDisplay();
	// }

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
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
					mDay);
		case TIME_DIALOG_ID:
			return new TimePickerDialog(this, mTimeSetListener, mHour, mMinute,
					true);
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

	//预设时间
	private void showPresetTimeDialog() {
		
		PresetTimeDialog = new AlertDialog.Builder(MyOrderActivity.this)
				.create();
		PresetTimeDialog.show();
		PresetTimeDialog.getWindow()
				.setContentView(R.layout.dialog_preset_time);
		showDate = (EditText) PresetTimeDialog.findViewById(R.id.showdate);
		pickDate = (Button) PresetTimeDialog.findViewById(R.id.pickdate);
		showTime = (EditText) PresetTimeDialog.findViewById(R.id.showtime);
		pickTime = (Button) PresetTimeDialog.findViewById(R.id.picktime);
		preset_time_confirm_btn = (Button) PresetTimeDialog
				.findViewById(R.id.preset_time_confirm_btn);
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
				//
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
		
		ReceiveTimeDialog = new AlertDialog.Builder(MyOrderActivity.this)
				.create();
		ReceiveTimeDialog.show();
		ReceiveTimeDialog.getWindow().setContentView(
				R.layout.dialog_receive_time);
		nowSendBtn = (Button) ReceiveTimeDialog
				.findViewById(R.id.dialog_now_send);
		nowSendBtn.setOnClickListener(this);
		preSetBtn = (Button) ReceiveTimeDialog
				.findViewById(R.id.dialog_pre_set);
		preSetBtn.setOnClickListener(this);
	}

}
