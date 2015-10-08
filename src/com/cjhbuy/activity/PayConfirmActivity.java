package com.cjhbuy.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cjhbuy.bean.Order;
import com.cjhbuy.common.PwdInputView;
import com.cjhbuy.utils.StringUtil;

/**
 * 确认交易
 * @author ps
 *
 */
public class PayConfirmActivity extends BaseActivity {
	private TextView pay_confirm_shop_name;
	private TextView pay_confirm_price;
	private TextView pay_confirm_receive;
	private TextView pay_confirm_goods;
	private Button pay_btn;
	private AlertDialog payPwdDialog;

	private TextView pay_pwd_shop_name;
	private TextView pay_pwd_price;
	private ImageButton cancel_dialog_icon_btn;

	// 密码输入框
	private PwdInputView password_edit;
	// 更换银行
	private TextView pay_way_choose_bank;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_payconfirm);
		initView();
		initData();
	}

	@Override
	public void initView() {
		super.initView();
		title.setText("确认交易");
		
		pay_confirm_shop_name = (TextView) findViewById(R.id.pay_confirm_shop_name);
		pay_confirm_price = (TextView) findViewById(R.id.pay_confirm_price);
		pay_confirm_receive = (TextView) findViewById(R.id.pay_confirm_receive);
		pay_confirm_goods = (TextView) findViewById(R.id.pay_confirm_goods);
		right_imgbtn.setVisibility(View.GONE);
		pay_btn = (Button) findViewById(R.id.pay_btn);
		pay_btn.setOnClickListener(this);

	}

	private void initData() {
		Intent intent = getIntent();
		Order order = (Order)intent.getSerializableExtra("order");
		
		pay_confirm_shop_name.setText("超家伙即时送");
		pay_confirm_price.setText("￥ "+StringUtil.format2string(order.getAmount_money()));
		pay_confirm_receive.setText("超家伙");
		pay_confirm_goods.setText("超家伙即时送");
	}

	@Override
	public void onClick(View v) {
		
		super.onClick(v);
		switch (v.getId()) {
		case R.id.pay_btn:
			showPayPwd();
			break;
		case R.id.cancel_dialog_icon_btn:
			payPwdDialog.dismiss();
			break;
		case R.id.pay_way_choose_bank:
			Toast.makeText(PayConfirmActivity.this, "更换", Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}
	}

	private void showPayPwd() {
		
		payPwdDialog = new AlertDialog.Builder(PayConfirmActivity.this).create();
		payPwdDialog.show();
		/**
		 * 解决键盘不显示
		 */
		payPwdDialog.getWindow().clearFlags(
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		payPwdDialog.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		payPwdDialog.getWindow().setContentView(R.layout.dialog_pay_pwd);
		pay_pwd_shop_name = (TextView) payPwdDialog.findViewById(R.id.pay_pwd_shop_name);
		pay_pwd_price = (TextView) payPwdDialog.findViewById(R.id.pay_pwd_price);
		cancel_dialog_icon_btn = (ImageButton) payPwdDialog.findViewById(R.id.cancel_dialog_icon_btn);
		cancel_dialog_icon_btn.setOnClickListener(this);
		pay_way_choose_bank = (TextView) payPwdDialog.findViewById(R.id.pay_way_choose_bank);
		pay_way_choose_bank.setOnClickListener(this);
		password_edit = (PwdInputView) payPwdDialog.findViewById(R.id.password_edit);
		password_edit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence text, int start,
					int lengthBefore, int lengthAfter) {
				
				int textLength = text.toString().length();
				if (textLength >= 6) {
					payPwdDialog.dismiss();
					Intent intent = new Intent();
					intent.setClass(PayConfirmActivity.this,
							WaitOrderConfirmActivity.class);
					startActivity(intent);

				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				

			}
		});
		pay_pwd_shop_name.setText("超家伙");
		pay_pwd_price.setText("￥ 35.00");

	}

}
