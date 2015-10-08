package com.cjhbuy.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cjhbuy.utils.CommonsUtil;
import com.cjhbuy.view.DrawerView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class MeActivity extends BaseActivity {
	protected SlidingMenu side_drawer;
	private ImageView top_more_left;

	private Button me_login_btn;
	private RelativeLayout me_order_rl;
	private RelativeLayout me_address_rl;
	private RelativeLayout me_coupon_rl;
	private RelativeLayout me_share_rl;
	private RelativeLayout me_remind_ll;
	private RelativeLayout me_customerservice_rl;
	private RelativeLayout me_aboutus_rl;
	
	private RelativeLayout goods_top_rl;
	private RelativeLayout order_top_rl;
	
	private TextView order_title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragmeng_me);
		initSlidingMenu();
		initView();
		initData();
	}

	private void initSlidingMenu() {
		side_drawer = new DrawerView(this).initSlidingMenu();
	}

	public void initView(){
		top_more_left = (ImageView) findViewById(R.id.top_more_left);
		top_more_left.setOnClickListener(this);

		me_login_btn = (Button) findViewById(R.id.me_login_btn);
		me_login_btn.setOnClickListener(this);
		me_order_rl = (RelativeLayout) findViewById(R.id.me_order_rl);
		me_order_rl.setOnClickListener(this);
		me_address_rl = (RelativeLayout) findViewById(R.id.me_address_rl);
		me_address_rl.setOnClickListener(this);
		me_coupon_rl = (RelativeLayout) findViewById(R.id.me_coupon_rl);
		me_coupon_rl.setOnClickListener(this);
		me_share_rl = (RelativeLayout) findViewById(R.id.me_share_rl);
		me_share_rl.setOnClickListener(this);
		me_remind_ll = (RelativeLayout) findViewById(R.id.me_remind_ll);
		me_remind_ll.setOnClickListener(this);
		me_customerservice_rl = (RelativeLayout) findViewById(R.id.me_customerservice_rl);
		me_customerservice_rl.setOnClickListener(this);
		me_aboutus_rl = (RelativeLayout) findViewById(R.id.me_aboutus_rl);
		me_aboutus_rl.setOnClickListener(this);
		//登录信息
		goods_top_rl = (RelativeLayout) findViewById(R.id.goods_top_rl);
		order_top_rl = (RelativeLayout) findViewById(R.id.order_top_rl);
		order_title = (TextView) findViewById(R.id.order_title);
	}
	
	public void initData(){
		boolean isLogin = sessionManager.isLoggedIn();
		if(isLogin){
			goods_top_rl.setVisibility(View.GONE);
			order_top_rl.setVisibility(View.VISIBLE);
			order_title.setText("用户:"+sessionManager.getUserName());
		}else{
			goods_top_rl.setVisibility(View.VISIBLE);
			order_top_rl.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.me_login_btn:
			startActivity(new Intent(MeActivity.this, LoginActivity.class));
			break;
		case R.id.me_order_rl:
			if(!sessionManager.isLoggedIn()){
				CommonsUtil.showLongToast(getApplicationContext(), "请先登录");
				return;
			}
			Intent intent = new Intent();
			intent.putExtra("order", "order");
			intent.setClass(MeActivity.this, OrderActivity.class);
			startActivity(intent);
			break;
		case R.id.me_address_rl:
			startActivity(new Intent(MeActivity.this, AddressActivity.class));
			break;
		case R.id.me_coupon_rl://优惠券
			startActivity(new Intent(MeActivity.this, CouponActivity.class));
			break;
		case R.id.me_share_rl:
			Toast.makeText(MeActivity.this, "分享超家伙(友盟或者别的分享)",Toast.LENGTH_LONG).show();
			break;
		case R.id.me_remind_ll:

			break;
		case R.id.me_customerservice_rl:
//			startActivity(new Intent(MeActivity.this, ChatActivity.class));
			AlertDialog isExit = new AlertDialog.Builder(MeActivity.this).create();
			// 设置对话框标题
			isExit.setTitle("联系客服");
			// 设置对话框消息
			isExit.setMessage("客服中心还在建设中");
			// 添加选择按钮并注册监听
			isExit.setButton(DialogInterface.BUTTON_POSITIVE, "确定", listener);
			// 显示对话框
			isExit.show();
			break;
		case R.id.me_aboutus_rl:
			startActivity(new Intent(MeActivity.this, AboutUsActivity.class));
			break;
		case R.id.top_more_left:
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
	
	/** 监听对话框里面的button点击事件 */
	DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:// "确认"按钮退出程序
				dialog.dismiss();
				break;
			default:
				break;
			}
		}
	};

}
