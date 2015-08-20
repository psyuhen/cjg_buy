package com.cjhbuy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class WaitOrderConfirmActivity extends BaseActivity {
	private Button confirm_order_btn;
	private Button remind_seller_btn;
	private Button contact_seller_btn;
	private TextView wait_order_info;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wait_order_confirm);
		initView();
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		super.initView();
		title.setText("等待订单确认");
		confirm_order_btn = (Button) findViewById(R.id.confirm_order_btn);
		confirm_order_btn.setOnClickListener(this);
		remind_seller_btn = (Button) findViewById(R.id.remind_seller_btn);
		remind_seller_btn.setOnClickListener(this);
		contact_seller_btn = (Button) findViewById(R.id.contact_seller_btn);
		contact_seller_btn.setOnClickListener(this);
		wait_order_info = (TextView) findViewById(R.id.wait_order_info);
		right_imgbtn.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.back:
			startActivity(new Intent(WaitOrderConfirmActivity.this,
					HomeActivity.class));
			break;
		case R.id.confirm_order_btn:
			finish();
			break;
		case R.id.remind_seller_btn:
			confirm_order_btn.setVisibility(View.VISIBLE);
			remind_seller_btn.setVisibility(View.GONE);
			wait_order_info.setText("喜多多便利店已收到您的订单，预计十分钟内送达....");
			break;
		case R.id.contact_seller_btn:
			startActivity(new Intent(WaitOrderConfirmActivity.this,
					ChatActivity.class));
			break;
		default:
			break;
		}
	}
}
