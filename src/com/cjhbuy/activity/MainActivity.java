package com.cjhbuy.activity;

import com.cjhbuy.fragment.HomeFragment;
import com.cjhbuy.fragment.MeFragment;
import com.cjhbuy.fragment.OrderFragment;
import com.cjhbuy.fragment.HomeFragment.MyListener;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class MainActivity extends FragmentActivity implements OnClickListener,
		MyListener {
	// 静态fragment管理器
	private static FragmentManager fMgr;
	// 设置按钮
	private ImageButton settingBtn;
	// 编辑按钮
	private ImageButton edit_imaggbtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		settingBtn = (ImageButton) findViewById(R.id.top_more_right);
		edit_imaggbtn = (ImageButton) findViewById(R.id.top_edit_right);
		fMgr = getSupportFragmentManager();
		initFragment();
		dealBottomButtonsClickEvent();
	}

	/**
	 * 初始化首个Fragment
	 */
	@SuppressLint("Recycle")
	private void initFragment() {
		
		FragmentTransaction ft = fMgr.beginTransaction();
		HomeFragment homeFragment = new HomeFragment();
		Intent intent = getIntent();
		String city = intent.getStringExtra("city");
		String order = intent.getStringExtra("order");

		Bundle bundle = new Bundle();
		if (city == null) {
			city = "广州";
		}
		bundle.putString("city", city);
		/**
		 * 如果获取的order数据不为空,进入order界面
		 */
		homeFragment.setArguments(bundle);
		ft.add(R.id.fragmentRoot, homeFragment, "homeFragment");
		ft.addToBackStack("homeFragment");
		
		if (order==null) {
			ft.commit();
			return;
		}
		else
		{
			popAllFragmentsExceptTheBottomOne();
			ft.hide(fMgr.findFragmentByTag("homeFragment"));
			OrderFragment gf = new OrderFragment();
			ft.add(R.id.fragmentRoot, gf, "orderFragment");
			ft.addToBackStack("orderFragment");
			ft.commit();
		}
		

	}

	/**
	 * 处理底部点击事件
	 */
	private void dealBottomButtonsClickEvent() {
		
		findViewById(R.id.rbHome).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				if (fMgr.findFragmentByTag("homeFragment") != null
						&& fMgr.findFragmentByTag("homeFragment").isVisible()) {
					return;
				}
				popAllFragmentsExceptTheBottomOne();
			}
		});
		findViewById(R.id.rbOrder).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				popAllFragmentsExceptTheBottomOne();
				FragmentTransaction ft = fMgr.beginTransaction();
				ft.hide(fMgr.findFragmentByTag("homeFragment"));
				OrderFragment gf = new OrderFragment();
				ft.add(R.id.fragmentRoot, gf, "orderFragment");
				ft.addToBackStack("orderFragment");
				ft.commit();
			}

		});
		findViewById(R.id.rbMe).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				popAllFragmentsExceptTheBottomOne();
				FragmentTransaction ft = fMgr.beginTransaction();
				ft.hide(fMgr.findFragmentByTag("homeFragment"));
				MeFragment mg = new MeFragment();
				ft.add(R.id.fragmentRoot, mg, "meFragment");
				ft.addToBackStack("meFragment");
				ft.commit();
			}
		});
	}

	/**
	 * 从back stack弹出所有的fragment，保留首页的那个
	 */
	public static void popAllFragmentsExceptTheBottomOne() {
		for (int i = 0, count = fMgr.getBackStackEntryCount() - 1; i < count; i++) {
			fMgr.popBackStack();
		}
	}

	// 点击返回按钮
	@TargetApi(Build.VERSION_CODES.ECLAIR)
	@Override
	public void onBackPressed() {
		if (fMgr.findFragmentByTag("navFragment") != null
				&& fMgr.findFragmentByTag("navFragment").isVisible()) {
			MainActivity.this.finish();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		}
	}

	@Override
	public void showMessage(String city) {
		

	}

}
