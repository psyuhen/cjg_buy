package com.cjhbuy.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PayWayActivity extends BaseActivity {
	private Button pay_way_confirm_btn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pay_way);
		initView();
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		super.initView();
		title.setText("付款方式");
		pay_way_confirm_btn = (Button) findViewById(R.id.pay_way_confirm_btn);
		pay_way_confirm_btn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.pay_way_confirm_btn:
			finish();
			break;

		default:
			break;
		}
	}
}
