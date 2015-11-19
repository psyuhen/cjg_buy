package com.cjhbuy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cjhbuy.adapter.OrderFragmentAdapter;
import com.cjhbuy.bean.Order;
import com.cjhbuy.fragment.OrderInFragment;
import com.cjhbuy.utils.HttpUtil;
import com.cjhbuy.view.DrawerView;
import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

/**
 * 我的订单
 * @author pansen
 *
 */
public class OrderActivity extends BaseActivity {
	private static final Logger LOGGER = LoggerFactory.getLogger(OrderInFragment.class);

	private ViewPager mViewPager;
	private OrderFragmentAdapter mFragmentAdapter;

	private RelativeLayout order_top_rl;
	private RelativeLayout order_left_rl;
	private RelativeLayout order_right_rl;

	private View order_left_line;
	private View order_right_line;
	protected SlidingMenu side_drawer;
	private ImageView top_more_left;

	private EditText order_search_edit;
	
	private TextView order_in_num;
	private TextView order_complete_num;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_order);
		initSlidingMenu();
		initView();
		initData();
	}

	private void initSlidingMenu() {
		side_drawer = new DrawerView(this).initSlidingMenu();
	}
	
	
	private void initData() {
		if(!sessionManager.isLoggedInAndLongOper()){
			Intent intent = new Intent(OrderActivity.this, LoginActivity.class);
			intent.putExtra("from", "OrderActivity");
			startActivity(intent);
			//CommonsUtil.showLongToast(getApplicationContext(), "请先登录");
			finish();
			return;
		}else{
			sessionManager.resetLoginTime();
		}
		
		count(0);
		count(1);
	}
	
	
	//统计已完成数量和进行中数量
	private void count(int oper){
		String url = "";
		if(oper == 0){
			url = HttpUtil.BASE_URL + "/order/countInOrderInfo.do";
			order_in_num.setText(countIn(url));
		}else if(oper == 1){
			url = HttpUtil.BASE_URL + "/order/countCompletedOrder.do";
			order_complete_num.setText(countIn(url));
		}
	}
	private String countIn(String url){
		int user_id = sessionManager.getUserId();
		Order order1 = new Order();
		order1.setBuyer_user_id(user_id);
		
		try{
			String json = HttpUtil.postRequest(url, order1);
			if(json == null || "".equals(json)){
				return "0";
			}
			
			return json;
		} catch (Exception e) {
			LOGGER.error("统计订单数据失败", e);
		}
		return "0";
	}

	@Override
	public void initView() {
		super.initView();
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
		
		//已完成和进行中的数量多少 
		order_in_num = (TextView) findViewById(R.id.order_left_num);
		order_complete_num = (TextView) findViewById(R.id.order_right_num);
		
		mViewPager = (ViewPager) findViewById(R.id.order_viewpager);

		mFragmentAdapter = new OrderFragmentAdapter(
				OrderActivity.this.getSupportFragmentManager(),
				OrderActivity.this);
		mViewPager.setAdapter(mFragmentAdapter);
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
		
		order_search_edit = (EditText) findViewById(R.id.order_search_edit);
		order_search_edit.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.order_left_rl://正在进行中
			order_left_line.setVisibility(View.VISIBLE);
			order_right_line.setVisibility(View.INVISIBLE);
			mViewPager.setCurrentItem(0);
			break;
		case R.id.order_right_rl://已完成
			order_left_line.setVisibility(View.VISIBLE);
			order_right_line.setVisibility(View.INVISIBLE);
			mViewPager.setCurrentItem(1);
			break;
		case R.id.top_more_left:
			if (side_drawer.isMenuShowing()) {
				side_drawer.showContent();
			} else {
				side_drawer.showMenu();
			}
			break;
		case R.id.order_search_edit:
			startActivity(new Intent(OrderActivity.this,
					OrderSearchActivity.class));
			break;
		default:
			break;
		}
	}
}
