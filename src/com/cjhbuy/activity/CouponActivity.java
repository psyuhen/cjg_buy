package com.cjhbuy.activity;

import android.os.Bundle;
import android.view.View;

public class CouponActivity extends BaseActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_coupon);
		initView();
	}
	@Override
	public void initView() {
		// TODO Auto-generated method stub
		super.initView();
		right_text.setText("我的优惠券");
		right_imgbtn.setVisibility(View.GONE);
	}
}
