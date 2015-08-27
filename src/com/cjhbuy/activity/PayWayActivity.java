package com.cjhbuy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
/**
 * 付款方式
 * @author ps
 *
 */
public class PayWayActivity extends BaseActivity {
	private Button pay_way_confirm_btn;
	
	//支付方式
	private RadioGroup pay_way_radioGroup;
	private RadioGroup pay_way_online_radiogroup;
	
	private LinearLayout pay_way_online_ll;
	
	//
	private String payWay = "2";
	private String payWayOnline = "1";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pay_way);
		initView();
	}

	@Override
	public void initView() {
		
		super.initView();
		title.setText("付款方式");
		pay_way_confirm_btn = (Button) findViewById(R.id.pay_way_confirm_btn);
		pay_way_confirm_btn.setOnClickListener(this);
		
		pay_way_online_ll = (LinearLayout)findViewById(R.id.pay_way_online_ll);
		
		pay_way_radioGroup = (RadioGroup)findViewById(R.id.pay_way_radioGroup);
		pay_way_radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch(checkedId){
				case R.id.cash_radio:
					//货到付款时，在线付款要隐藏吧；状态为2
					pay_way_online_ll.setVisibility(View.GONE);
					payWay = "2";
					payWayOnline = "2";
					break;
				case R.id.online_radio:
					//在线支付时，要显示哪种方式支付
					pay_way_online_ll.setVisibility(View.VISIBLE);
					payWay = "";
					payWayOnline = "1";
					break;
				default:
					break;
				}
			}
		});
		pay_way_online_radiogroup = (RadioGroup)findViewById(R.id.pay_way_online_radiogroup);
		pay_way_online_radiogroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch(checkedId){
				case R.id.weixin_radio:
					//微信支付，状态为1
					payWayOnline = "1";
					break;
				case R.id.zhifubao_radio:
					//支付宝支付，状态为0
					payWayOnline = "0";
					break;
				case R.id.yinlian_radio:
					//银联支付，暂时不支持哟
					break;
				default:
					break;
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.pay_way_confirm_btn:
			
			Intent data = new Intent();
			data.putExtra("payWayOnline", payWayOnline);
			setResult(RESULT_OK, data);
			finish();
			break;

		default:
			break;
		}
	}
}
