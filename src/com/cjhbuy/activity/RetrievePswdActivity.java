package com.cjhbuy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cjhbuy.bean.ResponseInfo;
import com.cjhbuy.bean.User;
import com.cjhbuy.fragment.HomeSaleFragment;
import com.cjhbuy.utils.CommonsUtil;
import com.cjhbuy.utils.HttpUtil;
import com.cjhbuy.utils.JsonUtil;
import com.cjhbuy.utils.SecureUtil;
import com.cjhbuy.utils.SwitchButton;
import com.cjhbuy.utils.SwitchButton.OnCheckChangeListener;
import com.cjhbuy.utils.Validator;
import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;
/**
 * 找回密码
 * @author pansen
 *
 */
public class RetrievePswdActivity extends BaseActivity {
	private static final Logger LOGGER = LoggerFactory.getLogger(HomeSaleFragment.class);

	//手机号
	private EditText retrieve_pswd_tel_edit;	
	//验证码
	private EditText retrieve_pswd_code_edit;
	//新密码
	private EditText retrieve_pswd_new_pswd_edit;
	//显示密码
	private SwitchButton retrieve_pswd_show_password;
	//获取验证码
	private Button retrieve_pswd_tel_send_code_btn;
	//确认
	private Button retrieve_pswd_confirm_btn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_retrive_pswd);
		initView();
	}

	@Override
	public void initView() {
		super.initView();
		title.setText("找回密码");
		right_imgbtn.setVisibility(View.GONE);
		retrieve_pswd_tel_edit=(EditText) findViewById(R.id.retrieve_pswd_tel_edit);
		retrieve_pswd_code_edit=(EditText) findViewById(R.id.retrieve_pswd_code_edit);
		retrieve_pswd_new_pswd_edit=(EditText) findViewById(R.id.retrieve_pswd_new_pswd_edit);
		retrieve_pswd_show_password=(SwitchButton) findViewById(R.id.retrieve_pswd_show_password);
		retrieve_pswd_tel_send_code_btn=(Button) findViewById(R.id.retrieve_pswd_tel_send_code_btn);
		retrieve_pswd_confirm_btn = (Button) findViewById(R.id.retrieve_pswd_confirm_btn);
		
		retrieve_pswd_tel_send_code_btn.setOnClickListener(this);
		retrieve_pswd_confirm_btn.setOnClickListener(this);
		retrieve_pswd_show_password.setOnCheckChangeListener(new OnCheckChangeListener() {
			@Override
			public void OnCheck(SwitchButton switchButton, boolean isChecked) {
				if(!isChecked){
					retrieve_pswd_new_pswd_edit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
				}else{
					retrieve_pswd_new_pswd_edit.setTransformationMethod(PasswordTransformationMethod.getInstance());
				}
			}
		});
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		/**
		 * 验证码
		 */
		case R.id.retrieve_pswd_tel_send_code_btn:
			//TODO 验证码获取
			retrieve_pswd_code_edit.setText("kefagd");
			break;
		/**
		 * 确认
		 */
		case R.id.retrieve_pswd_confirm_btn:
			attemptResetPasswd();
			break;
		default:
			break;
		}
	}

	//确认重置密码
	private void attemptResetPasswd(){
		String mobile = retrieve_pswd_tel_edit.getText().toString();
		String newPasswd = retrieve_pswd_new_pswd_edit.getText().toString();
		String code = retrieve_pswd_code_edit.getText().toString();
		boolean cancel = false;
		View focusView = null;
		
		// 手机号码为必输入的以及正确输入.
		if (TextUtils.isEmpty(mobile)) {
			retrieve_pswd_tel_edit.setError(getString(R.string.error_field_required));
			focusView = retrieve_pswd_tel_edit;
			cancel = true;
		} else if (!Validator.isMoblie(mobile)) {
			retrieve_pswd_tel_edit.setError(getString(R.string.error_invalid_mobile));
			focusView = retrieve_pswd_tel_edit;
			cancel = true;
		}
		
		// 检验密码.
		if (TextUtils.isEmpty(code)){
			retrieve_pswd_code_edit.setError(getString(R.string.error_field_required));
			focusView = retrieve_pswd_code_edit;
			cancel = true;
		}
		
		// 检验密码.
		if (TextUtils.isEmpty(newPasswd)){
			retrieve_pswd_new_pswd_edit.setError(getString(R.string.error_field_required));
			focusView = retrieve_pswd_new_pswd_edit;
			cancel = true;
		}else if(!Validator.isPwdLength(newPasswd)){
			retrieve_pswd_new_pswd_edit.setError(getString(R.string.error_length_password));
			focusView = retrieve_pswd_new_pswd_edit;
			cancel = true;
		}
		
		//TODO 还有验证码部分没弄
		if (cancel) {
			focusView.requestFocus();
			return;
		}
		String password = newPasswd;
		newPasswd = SecureUtil.shaEncode(newPasswd);
		String url = HttpUtil.BASE_URL + "/user/forgetpwd.do";
		
		User user = new User();
		user.setPassword(newPasswd);
		user.setMobile(mobile);
		user.setUser_type("1");
		
		try {
			String json = HttpUtil.postRequest(url, user);
			if(json == null){
				CommonsUtil.showLongToast(getApplicationContext(), "找回密码失败");
				return;
			}
			
			ResponseInfo responseInfo = JsonUtil.parse2Object(json, ResponseInfo.class);
			CommonsUtil.showLongToast(getApplicationContext(), responseInfo.getDesc());
			if(ResponseInfo.SUCCESS.equals(responseInfo.getStatus())){
				Intent intent = new Intent(RetrievePswdActivity.this, LoginActivity.class);
				intent.putExtra("mobile", mobile);
				intent.putExtra("password", password);
				intent.putExtra("from", "RetrievePswdActivity");
				startActivity(intent);
				finish();
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(),e);
			CommonsUtil.showLongToast(getApplicationContext(), "找回密码失败:"+e.getMessage());
		}
	}
}
