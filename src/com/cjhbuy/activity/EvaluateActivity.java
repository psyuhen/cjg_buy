package com.cjhbuy.activity;

import java.util.concurrent.ExecutionException;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import com.cjhbuy.bean.Order;
import com.cjhbuy.utils.CommonsUtil;
import com.cjhbuy.utils.HttpUtil;
import com.cjhbuy.utils.JsonUtil;
import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;

public class EvaluateActivity extends BaseActivity{
	private static final Logger LOGGER = LoggerFactory.getLogger(EvaluateActivity.class);

	private RatingBar evaluate_ratingBar;
	private EditText evaluate_edit;
	private Button evaluate_btn;
	
	private String orderId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_evaluate);
		initView();
		initData();
	}
	@Override
	public void initView() {
		super.initView();
		title.setText("我要评价");
		right_imgbtn.setVisibility(View.GONE);
		evaluate_ratingBar = (RatingBar)findViewById(R.id.evaluate_ratingBar);
		evaluate_edit = (EditText)findViewById(R.id.evaluate_edit);
		evaluate_btn = (Button)findViewById(R.id.evaluate_btn);
		evaluate_btn.setOnClickListener(this);
	}
	
	public void initData() {
		Intent intent = getIntent();
		orderId = intent.getStringExtra("order_id");
		queryBuyerAdvise(orderId);
	}
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.evaluate_btn:
			updateBuyerScore();
			break;
		default:
			break;
		}
	}
	//查询评分信息
	private void queryBuyerAdvise(String orderId){
		String url = HttpUtil.BASE_URL + "/order/getOrderInfoById.do?orderId="+orderId;
		try {
			String json = HttpUtil.getRequest(url);
			if(json == null){
				CommonsUtil.showLongToast(getApplicationContext(), "还没评分");
				return;
			}
			
			Order order = JsonUtil.parse2Object(json, Order.class);
			evaluate_edit.setText(order.getBuyer_advise());
			String buyerScore = order.getBuyer_score();
			if(TextUtils.isEmpty(buyerScore)){
				buyerScore = "0";
			}
			evaluate_ratingBar.setRating(Float.valueOf(buyerScore));
			
		} catch (InterruptedException e) {
			LOGGER.error("查询买家评分信息失败", e);
			CommonsUtil.showLongToast(getApplicationContext(), "查询买家评分信息失败");
		} catch (ExecutionException e) {
			LOGGER.error("查询买家评分信息失败", e);
			CommonsUtil.showLongToast(getApplicationContext(), "查询买家评分信息失败");
		}
	}
	
	//更新评分信息
	public void updateBuyerScore(){
		String url = HttpUtil.BASE_URL + "/order/updateOrderInfo.do";
		try {
			float f = evaluate_ratingBar.getRating();
			
			String advise = evaluate_edit.getText().toString();
			if(TextUtils.isEmpty(advise)){
				CommonsUtil.showLongToast(getApplicationContext(), "请输入建议");
				return;
			}
			
			Order order = new Order();
			order.setOrder_id(orderId);
			order.setBuyer_advise(advise);
			order.setBuyer_score(Float.valueOf(f).intValue() + "");
			
			String json = HttpUtil.postRequest(url,order);
			if(json == null){
				CommonsUtil.showLongToast(getApplicationContext(), "还没评分");
				return;
			}
			CommonsUtil.showLongToast(getApplicationContext(), "评分成功");
		} catch (InterruptedException e) {
			LOGGER.error("评分失败", e);
			CommonsUtil.showLongToast(getApplicationContext(), "评分失败");
		} catch (ExecutionException e) {
			LOGGER.error("评分失败", e);
			CommonsUtil.showLongToast(getApplicationContext(), "评分失败");
		}
	}
}
