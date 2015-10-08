package com.cjhbuy.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.cjhbuy.adapter.ChatMsgViewAdapter;
import com.cjhbuy.bean.ChatMsgItem;
import com.cjhbuy.bean.User;
import com.cjhbuy.utils.CommonsUtil;
import com.cjhbuy.utils.SocketUtil;
import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;

public class ChatActivity extends BaseActivity {
	private static final Logger LOGGER = LoggerFactory.getLogger(ChatActivity.class);

	private ChatMsgViewAdapter mAdapter;
	private ListView mListView;
	private List<ChatMsgItem> msgList;
	private Button chat_send_message_btn;
	private EditText chat_send_edit_msg;
	
	private String seller_user_id;
	private String seller_user_name;
	private String seller_user_mobile;
	private User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		initView();
		initData();
	}

	@Override
	public void initView() {
		super.initView();
		title.setText("喜多多便利店");
		right_imgbtn.setVisibility(View.GONE);
		mListView = (ListView) findViewById(R.id.chat_msg_listview);
		chat_send_message_btn = (Button) findViewById(R.id.chat_send_message_btn);
		chat_send_message_btn.setOnClickListener(this);
		chat_send_edit_msg = (EditText) findViewById(R.id.chat_send_edit_msg);
		
		Intent intent = getIntent();
		seller_user_id = intent.getStringExtra("seller_user_id");
		seller_user_name = intent.getStringExtra("seller_user_name");
		seller_user_mobile = intent.getStringExtra("seller_user_name");
	}

	private void initData() {
		user = sessionManager.getUserDetails();
		
		msgList = new ArrayList<ChatMsgItem>();
		mAdapter = new ChatMsgViewAdapter(ChatActivity.this, msgList);
		mListView.setAdapter(mAdapter);
		
		getOffLineMsg();
	}
	//每次进来先获取离线消息
	private void getOffLineMsg(){
		//发送到后端
		try {
			List<ChatMsgItem> list = SocketUtil.receive(user.getUser_id()+"", seller_user_id);
			if(list != null && !list.isEmpty()){
				msgList.addAll(list);
				Collections.sort(list, new Comparator<ChatMsgItem>() {
					@Override
					public int compare(ChatMsgItem item1, ChatMsgItem item2) {
						Date d1 = item1.getSendDate();
						Date d2 = item2.getSendDate();
						
						if(d1 == null){
							return -1;
						}
						
						if(d2 == null){
							return 1;
						}
						
						return d1.compareTo(d2);
					}
				});
				mAdapter.notifyDataSetChanged();
			}
		} catch (Exception e) {
			LOGGER.error("接收信息失败",e);
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.chat_send_message_btn:
			send();
			break;
		default:
			break;
		}
	}
	//发送
	private void send(){
		String message = chat_send_edit_msg.getText().toString();
		if(TextUtils.isEmpty(message.trim())){
			CommonsUtil.showLongToast(ChatActivity.this, "内容不能为空");
			return;
		}

		ChatMsgItem msgItem = new ChatMsgItem();
		
		msgItem.setSendDate(new Date());
		msgItem.setComing(false);
		msgItem.setSendUser(user.getName());
		msgItem.setToUser(seller_user_name);
		msgItem.setContent(message);
		msgList.add(msgItem);
		mAdapter.notifyDataSetChanged();
		
		User toUser = new User();
		toUser.setUser_id(Integer.parseInt(seller_user_id));
		toUser.setMobile(seller_user_mobile);
		toUser.setName(seller_user_name);
		//发送到后端
		SocketUtil.send(message, toUser, user);
		
		chat_send_edit_msg.setText("");
	}
}
