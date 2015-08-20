package com.cjhbuy.activity;

import android.os.Bundle;
import android.view.View;

/**
 * 关于我们
 * 
 * @author zkq
 * 
 */
public class AboutUsActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_aboutus);
		initView();
		initData();
	}

	@Override
	public void initView() {
		
		super.initView();
	}

	private void initData() {
		
		right_imgbtn.setVisibility(View.GONE);
		title.setText("关于我们");
	}

	@Override
	public void onClick(View v) {
		
		super.onClick(v);
		switch (v.getId()) {

		default:
			break;
		}
	}

}
