package com.cjhbuy.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.avos.avoscloud.AVOSCloud;
import com.cjhbuy.auth.SessionManager;

@SuppressLint("NewApi")
public class BaseActivity extends FragmentActivity implements OnClickListener {
	public TextView title;
	public TextView right_text;
	public TextView back;
	public ImageButton right_imgbtn;
	public SessionManager sessionManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sessionManager = new SessionManager(getApplicationContext());
	}

	public void initView() {

		title = (TextView) findViewById(R.id.title_text);
		right_text = (TextView) findViewById(R.id.right_text);
		back = (TextView) findViewById(R.id.back);
		right_text.setVisibility(View.VISIBLE);
		right_text.setClickable(true);
		right_text.setOnClickListener(this);
		back.setOnClickListener(this);
		right_imgbtn = (ImageButton) findViewById(R.id.right_imgbtn);
		right_imgbtn.setOnClickListener(this);
	}

	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.right_text:

			break;
		case R.id.back:
			onBackPressed();
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			AlertDialog isExit = new AlertDialog.Builder(this).create();
			// 设置对话框标题
			isExit.setTitle("系统提示");
			// 设置对话框消息
			isExit.setMessage("确定要退出吗");
			// 添加选择按钮并注册监听
			isExit.setButton(DialogInterface.BUTTON_POSITIVE,"确定", listener);
			isExit.setButton(DialogInterface.BUTTON_NEGATIVE,"取消", listener);
			// 显示对话框
			isExit.show();
		}
		return false;
	}

	/** 监听对话框里面的button点击事件 */
	DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:// "确认"按钮退出程序
				sessionManager.logoutUser();
				/**
				 * 完全退出程序
				 */
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_HOME);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				android.os.Process.killProcess(android.os.Process.myPid());
				break;
			case DialogInterface.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
				break;
			default:
				break;
			}
		}
	};
}
