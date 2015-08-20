package com.cjhbuy.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import com.cjhbuy.activity.HomeActivity;
import com.cjhbuy.activity.MeActivity;
import com.cjhbuy.activity.OrderActivity;
import com.cjhbuy.activity.R;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class DrawerView implements OnClickListener {
	private final Activity activity;
	SlidingMenu localSlidingMenu;

	private RelativeLayout left_home_rl;
	private RelativeLayout left_myorder_rl;
	private RelativeLayout left_me_rl;

	public DrawerView(Activity activity) {
		this.activity = activity;
	}

	public SlidingMenu initSlidingMenu() {
		localSlidingMenu = new SlidingMenu(activity);
		localSlidingMenu.setMode(SlidingMenu.LEFT_RIGHT);
		localSlidingMenu.setTouchModeAbove(SlidingMenu.SLIDING_WINDOW);
		localSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
		localSlidingMenu.setShadowDrawable(R.drawable.shadow);
		localSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		localSlidingMenu.setFadeDegree(0.35F);
		localSlidingMenu.attachToActivity(activity, SlidingMenu.RIGHT);
		localSlidingMenu.setMenu(R.layout.left_drawer_fragment);
		localSlidingMenu.setSecondaryMenu(R.layout.left_drawer_fragment);
		localSlidingMenu.setSecondaryShadowDrawable(R.drawable.shadow);
		localSlidingMenu
				.setOnOpenedListener(new SlidingMenu.OnOpenedListener() {
					@Override
					public void onOpened() {

					}
				});

		initView();
		return localSlidingMenu;
	}

	private void initView() {
		
		left_home_rl = (RelativeLayout) activity
				.findViewById(R.id.left_home_rl);
		left_home_rl.setOnClickListener(this);
		left_myorder_rl = (RelativeLayout) activity
				.findViewById(R.id.left_myorder_rl);
		left_myorder_rl.setOnClickListener(this);
		left_me_rl = (RelativeLayout) activity.findViewById(R.id.left_me_rl);
		left_me_rl.setOnClickListener(this);
	}

	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.left_home_rl://首页
			activity.startActivity(new Intent(activity, HomeActivity.class));
			activity.finish();
			activity.overridePendingTransition(R.anim.slide_in_right,
					R.anim.slide_out_left);
			break;
		case R.id.left_myorder_rl://订单
			activity.startActivity(new Intent(activity, OrderActivity.class));
			activity.finish();
			activity.overridePendingTransition(R.anim.slide_in_right,
					R.anim.slide_out_left);
			break;
		case R.id.left_me_rl://账号
			activity.startActivity(new Intent(activity, MeActivity.class));
			activity.finish();
			activity.overridePendingTransition(R.anim.slide_in_right,
					R.anim.slide_out_left);
			break;
		default:
			break;
		}
	}
}
