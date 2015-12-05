package com.cjhbuy.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.cjhbuy.bean.User;
import com.cjhbuy.utils.CommonsUtil;
import com.cjhbuy.utils.HttpUtil;
import com.cjhbuy.utils.SecureUtil;
import com.cjhbuy.utils.Validator;
import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;

public class RegisteActivity extends BaseActivity{
	private Logger LOGGER = LoggerFactory.getLogger(RegisteActivity.class);
	private EditText mMobileView;
	private EditText mPasswordView;
	private EditText mComfirmPasswordView;
	
	private CheckBox register_agree;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		initView();
		initData();
	}

	@Override
	public void initView() {
		super.initView();
		mMobileView = (EditText) findViewById(R.id.register_tel_edit);
		mPasswordView = (EditText) findViewById(R.id.register_password);
		mComfirmPasswordView = (EditText) findViewById(R.id.register_password_comfirm);
		
		Button btnRegister = (Button) findViewById(R.id.register_confirm_btn);
		btnRegister.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				attemptRegister();
			}
		});
		
		mMobileView.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus){
					String mobile = mMobileView.getText().toString();
					if(TextUtils.isEmpty(mobile)){
						return;
					}
					String url0 = HttpUtil.BASE_URL + "/user/isRegister.do?mobile=" + mobile + "&user_type=1";
					try {
						String request = HttpUtil.getRequest(url0);
						if(request != null && "true".equals(request)){
							mMobileView.setError("手机号码已被注册！");
							CommonsUtil.showLongToast(getApplicationContext(), "手机号码已被注册！");
						}
						if(request == null){
							CommonsUtil.showLongToast(getApplicationContext(), "网络或者服务器异常");
						}
					} catch (Exception e) {
						LOGGER.error(">>> 判断手机号码是否被注册失败", e);
					}
				}
			}
		});
		
		register_agree = (CheckBox)findViewById(R.id.register_agree);
	}
	
	private void initData() {
		
		title.setText("注册");
		right_imgbtn.setVisibility(View.GONE);
	}
	
	//注册
	private void attemptRegister() {
		// 清空错误
		mMobileView.setError(null);
		mPasswordView.setError(null);
		mComfirmPasswordView.setError(null);
		
		String mobile = mMobileView.getText().toString();
		String password = mPasswordView.getText().toString();
		String comfirmPassword = mComfirmPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;
		
		// 手机号码为必输入的以及正确输入.
		if (TextUtils.isEmpty(mobile)) {
			mMobileView.setError(getString(R.string.error_field_required));
			focusView = mMobileView;
			cancel = true;
		} else if (!Validator.isMoblie(mobile)) {
			mMobileView.setError(getString(R.string.error_invalid_mobile));
			focusView = mMobileView;
			cancel = true;
		}else if (TextUtils.isEmpty(password)){// 检验密码.
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		}else if(!Validator.isPwdLength(password)){
			mPasswordView.setError(getString(R.string.error_length_password));
			focusView = mPasswordView;
			cancel = true;
		}
		/*else if(!Validator.isPassword(password)){
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}*/
		else if(!password.equals(comfirmPassword)){
			mComfirmPasswordView.setError(getString(R.string.error_comfirm_password));
			focusView = mComfirmPasswordView;
			cancel = true;
		}
		
		//检查是否勾选协议
		if(!register_agree.isChecked()){
			focusView = register_agree;
			cancel = true;
			CommonsUtil.showLongToast(getApplicationContext(), "请勾选注册协议!");
		}

		
		if (cancel) {
			// 有错误，不登录，焦点在错误的输入框中，并显示错误
			focusView.requestFocus();
		} else {
			String tmpPwd = password;
			password = SecureUtil.shaEncode(password);
			//显示进度条，并发送登录请求，准备登录
			
			User user = new User();
			user.setName(mobile);
			user.setMobile(mobile);
			user.setPassword(password);
			user.setUser_type("1");//买家
			
			startProgressDialog();
			new RegisteTask(user, tmpPwd).execute();
		}
	}
	private class RegisteTask extends AsyncTask<Void, Void, String>{
		private User user;
		private String tmpPwd;
		public RegisteTask(User user, String tmpPwd) {
			this.user = user;
			this.tmpPwd = tmpPwd;
		}
		
		@Override
		protected String doInBackground(Void... params) {
			String url1 = HttpUtil.BASE_URL + "/user/register.do";
			// 发送登录请求
			try{
				String request = HttpUtil.postRequest(url1, user);
				
				return request;
			}catch (Exception e) {
				LOGGER.error(">>> 注册失败",e);
				return "注册失败";
			}
		}
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			stopProgressDialog();
			if(result == null){
				CommonsUtil.showLongToast(getApplicationContext(), "网络或者服务器异常!");
			}else{
				CommonsUtil.showLongToast(getApplicationContext(), result);
				if("注册成功!".equals(result)){
					Intent intent = new Intent(RegisteActivity.this, LoginActivity.class);
					intent.putExtra("mobile", user.getMobile());
					intent.putExtra("password", tmpPwd);
					startActivity(intent);
					finish();
				}
			}
		}
	}
}
