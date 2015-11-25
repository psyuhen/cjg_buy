package com.cjhbuy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cjhbuy.adapter.HomeFragmentAdapter;
import com.cjhbuy.utils.AppContext;
import com.cjhbuy.view.DrawerView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class HomeActivity extends BaseActivity {

	private ViewPager fragment_home_viewpager;
	private RelativeLayout frament_home_selector_ll1;
//	private RelativeLayout frament_home_selector_ll2;
	private RelativeLayout frament_home_selector_ll3;

	private View fragment_home_left_line;
//	private View fragment_home_center_line;
	private View fragment_home_right_line;
	private HomeFragmentAdapter mFragmentAdapter;
	private ImageView frament_home_top_image;
	private RelativeLayout search_head_address_ll;
	private TextView search_head_address_text;

//	private SharedPreferences preferences;
	private AppContext appContext;

	protected SlidingMenu side_drawer;

	private ImageView search_top_more_left;
	
	//搜索框
//	private EditText search_head_edit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_home);
		appContext = (AppContext) this.getApplicationContext();
		initSlidingMenu();
		initView();
		initData();
		mFragmentAdapter = new HomeFragmentAdapter(
				HomeActivity.this.getSupportFragmentManager(),
				HomeActivity.this);
		fragment_home_viewpager.setAdapter(mFragmentAdapter);
		Intent intent = getIntent();
		String city = intent.getStringExtra("city");
		if (city != null) {
			search_head_address_text.setText(city);
		}
		
		//搜索框,实现按键事件进行搜索
		/*search_head_edit = (EditText)findViewById(R.id.search_head_edit);
		search_head_edit.setKeyListener(new KeyListener() {
			@Override
			public boolean onKeyUp(View view, Editable text, int keyCode, KeyEvent event) {
				return false;
			}
			
			@Override
			public boolean onKeyOther(View view, Editable text, KeyEvent event) {
				return false;
			}
			
			@Override
			public boolean onKeyDown(View view, Editable text, int keyCode,
					KeyEvent event) {
				return false;
			}
			
			@Override
			public int getInputType() {
				return 0;
			}
			
			@Override
			public void clearMetaKeyState(View view, Editable content, int states) {
				
			}
		});*/
	}

	private void initSlidingMenu() {
		//侧边栏
		side_drawer = new DrawerView(this).initSlidingMenu();
	}

	@Override
	public void initView() {
		
		super.initView();
		frament_home_selector_ll1 = (RelativeLayout) findViewById(R.id.frament_home_selector_ll1);
		frament_home_selector_ll1.setOnClickListener(this);
		/*frament_home_selector_ll2 = (RelativeLayout) findViewById(R.id.frament_home_selector_ll2);
		frament_home_selector_ll2.setOnClickListener(this);*/
		frament_home_selector_ll3 = (RelativeLayout) findViewById(R.id.frament_home_selector_ll3);
		frament_home_selector_ll3.setOnClickListener(this);
		fragment_home_viewpager = (ViewPager) findViewById(R.id.fragment_home_viewpager);
		fragment_home_left_line = findViewById(R.id.fragment_home_left_line);
//		fragment_home_center_line = findViewById(R.id.fragment_home_center_line);
		fragment_home_right_line = findViewById(R.id.fragment_home_right_line);
		frament_home_top_image = (ImageView) findViewById(R.id.frament_home_top_image);
		frament_home_top_image.setOnClickListener(this);
		search_head_address_ll = (RelativeLayout) findViewById(R.id.search_head_address_ll);
		search_head_address_ll.setOnClickListener(this);
		search_head_address_text = (TextView) findViewById(R.id.search_head_address_text);
		search_top_more_left = (ImageView) findViewById(R.id.search_top_more_left);
		search_top_more_left.setOnClickListener(this);
	}

	private void initData() {
		
		// Bundle bundle = getArguments();
		// String city = bundle.getString("city");
		// search_head_address_text.setText(city);
		fragment_home_viewpager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				
				switch (position) {
				case 0:
					fragment_home_left_line.setVisibility(View.VISIBLE);
//							fragment_home_center_line.setVisibility(View.INVISIBLE);
					fragment_home_right_line.setVisibility(View.INVISIBLE);
					break;
				/*case 1:
					fragment_home_left_line.setVisibility(View.INVISIBLE);
					fragment_home_center_line.setVisibility(View.VISIBLE);
					fragment_home_right_line.setVisibility(View.INVISIBLE);
					break;*/
				case 1:
					fragment_home_left_line.setVisibility(View.INVISIBLE);
//							fragment_home_center_line.setVisibility(View.INVISIBLE);
					fragment_home_right_line.setVisibility(View.VISIBLE);
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
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.frament_home_selector_ll1:
			fragment_home_left_line.setVisibility(View.VISIBLE);
//			fragment_home_center_line.setVisibility(View.INVISIBLE);
			fragment_home_right_line.setVisibility(View.INVISIBLE);
			fragment_home_viewpager.setCurrentItem(0);
			break;
/*		case R.id.frament_home_selector_ll2:
			fragment_home_left_line.setVisibility(View.INVISIBLE);
			fragment_home_center_line.setVisibility(View.VISIBLE);
			fragment_home_right_line.setVisibility(View.INVISIBLE);
			fragment_home_viewpager.setCurrentItem(1);
			break;*/
		case R.id.frament_home_selector_ll3:
			fragment_home_left_line.setVisibility(View.INVISIBLE);
//			fragment_home_center_line.setVisibility(View.INVISIBLE);
			fragment_home_right_line.setVisibility(View.VISIBLE);
			fragment_home_viewpager.setCurrentItem(1);
			break;
		case R.id.frament_home_top_image:
			startActivity(new Intent(HomeActivity.this, GoodsViewActivity.class));
			break;
		case R.id.search_head_address_ll:
			// preferences = appContext.getPreferences();
			// Log.i("zkq", preferences.edit() + "--");
			// Editor editor = preferences.edit();
			// editor.putInt("isnext", 3);
			// editor.commit();
			startActivity(new Intent(HomeActivity.this, SelectCityActivity.class));
			break;
		case R.id.search_top_more_left:
			if (side_drawer.isMenuShowing()) {
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
