package com.cjhbuy.activity;

import java.util.ArrayList;
import java.util.List;

import com.cjhbuy.adapter.CommonAdapter;
import com.cjhbuy.adapter.ViewHolder;
import com.cjhbuy.bean.GoodsItem;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myorder);
		initView();
		initData();
		myorder_cart_listview.setAdapter(showAdapter());
	}

	private void initData() {
		// TODO Auto-generated method stub
		goodsList = new ArrayList<GoodsItem>();
		for (int i = 0; i < 2; i++) {
			GoodsItem goodsItem = new GoodsItem();
			goodsItem.setTitle("丹麦进口 Kjeldsens 蓝罐 曲奇 礼盒 908g");
			goodsItem.setPrice(109);
			goodsList.add(goodsItem);
		}

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
		// TODO Auto-generated method stub
		super.initView();
		title.setText("我的下单");
		myorder_cart_listview = (ListView) findViewById(R.id.myorder_cart_listview);
		myorder_address_rl = (RelativeLayout) findViewById(R.id.myorder_address_rl);
		myorder_address_rl.setOnClickListener(this);
		myorder_receive_time_rl = (RelativeLayout) findViewById(R.id.myorder_receive_time_rl);
		myorder_receive_time_rl.setOnClickListener(this);
		myorder_pay_rl = (RelativeLayout) findViewById(R.id.myorder_pay_rl);
		myorder_pay_rl.setOnClickListener(this);
		submit_goods_btn = (Button) findViewById(R.id.submit_goods_btn);
		submit_goods_btn.setOnClickListener(this);

		my_order_delivery_money = (TextView) findViewById(R.id.my_order_delivery_money);
		myorder_favourable_money = (TextView) findViewById(R.id.myorder_favourable_money);
		myorder_service_money = (TextView) findViewById(R.id.myorder_service_money);
		myorder_give = (TextView) findViewById(R.id.myorder_give);
		myorder_give_num = (TextView) findViewById(R.id.myorder_give_num);
		myorder_give_money = (TextView) findViewById(R.id.myorder_give_money);
		money_count = (TextView) findViewById(R.id.money_count);
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
				// TODO Auto-generated method stub
				helper.setText(R.id.cart_goods_title, item.getTitle());
				helper.setText(R.id.cart_goods_price, "￥ " + item.getPrice());
				helper.setText(R.id.goods_item_stock, "0");
			}
		};
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.myorder_address_rl:
			Intent intent = new Intent();
			intent.setClass(MyOrderActivity.this, AddressActivity.class);
			startActivity(intent);
			break;
		case R.id.myorder_receive_time_rl:
			showReceiveTime();
			break;
		case R.id.myorder_pay_rl:
			Intent it = new Intent();
			it.setClass(MyOrderActivity.this, PayWayActivity.class);
			startActivity(it);
			break;
		case R.id.dialog_now_send:
			ReceiveTimeDialog.dismiss();
			break;
		case R.id.dialog_pre_set:

			ReceiveTimeDialog.dismiss();
			showPresetTimeDialog();
			break;
		case R.id.submit_goods_btn:
			Intent submitIntent = new Intent();
			submitIntent.setClass(MyOrderActivity.this,
					PayConfirmActivity.class);
			startActivity(submitIntent);
			break;
		default:
			break;
		}
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

	private void showPresetTimeDialog() {
		// TODO Auto-generated method stub
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
				// TODO Auto-generated method stub
				PresetTimeDialog.dismiss();
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
		// TODO Auto-generated method stub
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
