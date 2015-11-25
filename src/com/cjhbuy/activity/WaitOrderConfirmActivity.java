package com.cjhbuy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVPush;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.SendCallback;
import com.cjhbuy.utils.AppContext;
/**
 * 订单确认
 * @author pansen
 *
 */
public class WaitOrderConfirmActivity extends BaseActivity {
	private Button confirm_order_btn;
	private Button remind_seller_btn;
	private Button contact_seller_btn;
	private TextView wait_order_info;
	
	private AppContext app;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wait_order_confirm);
		initView();
		initData();
	}

	@Override
	public void initView() {
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
	
	private void initData() {
		app = (AppContext)this.getApplication();
		AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
	      @Override
	      public void done(AVException e) {
	        AVInstallation.getCurrentInstallation().saveInBackground();
	      }
	    });
	}

	@Override
	protected void onResume() {
		super.onResume();
		pushMsg2Seller();
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.back:
			Intent intent = new Intent(WaitOrderConfirmActivity.this, GoodsActivity.class);
			intent.putExtra("store_id", app.getStore_id());
			intent.putExtra("store_name", app.getStore_name());
			startActivity(intent);
			break;
		case R.id.confirm_order_btn:
			finish();
			break;
		case R.id.remind_seller_btn:
			confirm_order_btn.setVisibility(View.VISIBLE);
			remind_seller_btn.setVisibility(View.GONE);
			pushMsg2Seller();
			break;
		case R.id.contact_seller_btn:
			Intent intent2 = new Intent(WaitOrderConfirmActivity.this, CommunicationActivity.class);
			intent2.putExtra("store_id", app.getStore_id());
			startActivity(intent2);
			break;
		default:
			break;
		}
	}
	
	/*推送订单信息给卖家*/
	private void pushMsg2Seller(){
		// 设置默认打开的 Activity
//	    PushService.setDefaultPushCallback(this, WaitOrderConfirmActivity.class);
	    // 订阅频道，当该频道消息到来的时候，打开对应的 Activity
//	    PushService.subscribe(this, "cjh_buyer", WaitOrderConfirmActivity.class);
	    // 保存 installation 到服务器
		AVPush push = new AVPush();
		
        // 设置频道
        push.setChannel("seller_" + app.getStore_id());
        // 设置消息
        push.setMessage("您有新订单，请注意查收！");
        // 设置查询条件，推送给某个商家
        push.setQuery(AVInstallation.getQuery().whereEqualTo("channels","seller_" + app.getStore_id()));
        // 推送
        push.sendInBackground(new SendCallback() {
          @Override
          public void done(AVException e) {
            if (e == null) {
              wait_order_info.setText(app.getStore_name() + "已收到您的订单，预计十分钟内送达....");
            }
          }
        });
	}
}
