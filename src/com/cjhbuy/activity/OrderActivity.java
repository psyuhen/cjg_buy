package com.cjhbuy.activity;

import com.cjhbuy.adapter.OrderFragmentAdapter;
import com.cjhbuy.view.DrawerView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class OrderActivity extends BaseActivity {
	private ViewPager mViewPager;
	private OrderFragmentAdapter mFragmentAdapter;

	private RelativeLayout order_top_rl;
	private RelativeLayout order_left_rl;
	private RelativeLayout order_right_rl;

	private View order_left_line;
	private View order_right_line;
	protected SlidingMenu side_drawer;
	private ImageView top_more_left;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_order);
		initSlidingMenu();
		order_top_rl = (RelativeLayout) findViewById(R.id.order_top_rl);
		order_top_rl.setOnClickListener(this);
		order_left_rl = (RelativeLayout) findViewById(R.id.order_left_rl);
		order_left_rl.setOnClickListener(this);
		order_right_rl = (RelativeLayout) findViewById(R.id.order_right_rl);
		order_right_rl.setOnClickListener(this);
		order_left_line = findViewById(R.id.order_left_line);
		order_right_line = findViewById(R.id.order_right_line);
		top_more_left=(ImageView) findViewById(R.id.top_more_left);
		top_more_left.setOnClickListener(this);
		mViewPager = (ViewPager) findViewById(R.id.order_viewpager);

		mFragmentAdapter = new OrderFragmentAdapter(
				OrderActivity.this.getSupportFragmentManager(),
				OrderActivity.this);
		mViewPager.setAdapter(mFragmentAdapter);
		initView();
		initData();
	}

	private void initSlidingMenu() {
		side_drawer = new DrawerView(this).initSlidingMenu();
	}
	private void initData() {
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				switch (position) {
				case 0:
					order_left_line.setVisibility(View.VISIBLE);
					order_right_line.setVisibility(View.INVISIBLE);
					break;
				case 1:
					order_left_line.setVisibility(View.INVISIBLE);
					order_right_line.setVisibility(View.VISIBLE);
					break;
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	@Override
	public void initView() {
		super.initView();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.order_left_rl:
			order_left_line.setVisibility(View.VISIBLE);
			order_right_line.setVisibility(View.INVISIBLE);
			mViewPager.setCurrentItem(0);
			break;
		case R.id.order_right_rl:
			order_left_line.setVisibility(View.VISIBLE);
			order_right_line.setVisibility(View.INVISIBLE);
			mViewPager.setCurrentItem(1);
			break;
		case R.id.top_more_left:
			if (side_drawer.isMenuShowing()) 
			{
				side_drawer.showContent();
			} else {
				side_drawer.showMenu();
			}
			break;
		default:
			break;
		}
	}
}
