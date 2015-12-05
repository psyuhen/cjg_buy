package com.cjhbuy.activity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.cjhbuy.adapter.ChatMsgViewAdapter;
import com.cjhbuy.bean.ChatMsgItem;
import com.cjhbuy.bean.User;
import com.cjhbuy.utils.AVImClientManager;
import com.cjhbuy.utils.AppContext;
import com.cjhbuy.utils.CommonsUtil;
import com.cjhbuy.utils.HttpUtil;
import com.cjhbuy.utils.JsonUtil;
import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;

/**
 * 聊天窗口
 * 
 * @author ps
 *
 */
public class ChatActivity extends BaseActivity {
	private Logger LOGGER = LoggerFactory.getLogger(ChatActivity.class);

	private ChatMsgViewAdapter mAdapter;
	private ListView mListView;
	private List<ChatMsgItem> msgList;
	private Button chat_send_message_btn;
	private EditText chat_send_edit_msg;
	
	private String seller_user_id;
	private String seller_user_name;
	private String seller_user_mobile;
	// 头像按钮
	private ImageButton chat_face_imagebtn;
	private int imageId[];
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
		right_imgbtn.setVisibility(View.GONE);
		mListView = (ListView) findViewById(R.id.chat_msg_listview);
		chat_send_message_btn = (Button) findViewById(R.id.chat_send_message_btn);
		chat_send_message_btn.setOnClickListener(this);
		chat_send_edit_msg = (EditText) findViewById(R.id.chat_send_edit_msg);
		chat_face_imagebtn = (ImageButton) findViewById(R.id.chat_face_imagebtn);
		chat_face_imagebtn.setOnClickListener(this);
		Intent intent = getIntent();
		seller_user_id = intent.getStringExtra("seller_user_id");
		seller_user_name = intent.getStringExtra("seller_user_name");
		seller_user_mobile = intent.getStringExtra("seller_user_mobile");
	}

	private void initData() {
		title.setText("聊天");
		user = sessionManager.getUserDetails();
		
		msgList = new ArrayList<ChatMsgItem>();
		mAdapter = new ChatMsgViewAdapter(ChatActivity.this, msgList);
		mListView.setAdapter(mAdapter);
		
		if(seller_user_id == null || "".equals(seller_user_id) || "0".equals(seller_user_id)){
			queryByStoreId();
		}else{
			queryById();
		}
	}
	//每次进来先获取离线消息
	/*private void getOffLineMsg(){
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
	}*/

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.chat_send_message_btn:
			send();
			break;
		case R.id.chat_face_imagebtn:
			initFace();
			break;
		default:
			break;
		}
	}

	private void initFace() {
		imageId = new int[80];
		List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
		try {
			String prefix = "appkefu_f00";
			for (int i = 0; i < 70; i++) {
				if(i >= 10){
					prefix = "appkefu_f0";
				}
				Field field = R.drawable.class.getDeclaredField(prefix+ i);
				int ResourceID = Integer.parseInt(field.get(null).toString());
				imageId[i] = ResourceID;
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("image", ResourceID);
				maps.add(map);
			}
		} catch (Exception e) {
			LOGGER.error("加载表情失败", e);
		}
		Builder builder = new Builder(ChatActivity.this);
		LayoutInflater mInflater = LayoutInflater.from(ChatActivity.this);
		View view = mInflater.inflate(R.layout.display_face, null);
		final AlertDialog dialog = builder.setView(view).create();
		
		GridView gridView = (GridView) view.findViewById(R.id.face_gridview);
		SimpleAdapter adapter = new SimpleAdapter(this, maps,
				R.layout.display_face_detail, new String[] { "image" },
				new int[] { R.id.face_image });
		
		gridView.setAdapter(adapter);
		gridView.setNumColumns(10);
		gridView.setBackgroundColor(Color.rgb(214, 211, 214));
		gridView.setHorizontalSpacing(1);
		gridView.setVerticalSpacing(1);
		dialog.show();
		
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@SuppressLint("NewApi")
			public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
				Bitmap bitmap = null;
				bitmap = BitmapFactory.decodeResource(getResources(),imageId[position]);
				ImageSpan imageSpan = new ImageSpan(ChatActivity.this, bitmap);
				String str = null;
				if (position < 10) {
					str = "appkefu_f00" + position;
				} else {
					str = "appkefu_f0" + position;
				}
				SpannableString spannableString = new SpannableString(str);
				spannableString.setSpan(imageSpan, 0, 12, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				chat_send_edit_msg.append(spannableString);
				dialog.dismiss();
			}
		});
	}
	/**
	 * 发送leancloud消息
	 * @param avimTypedMessage
	 */
	public void leanCloudSend(final AVIMTypedMessage avimTypedMessage) {
		// 用自己的手机号码作为clientId，获取AVIMClient对象实例
		AVImClientManager clientMgr = AVImClientManager.getInstance();
		clientMgr.open(user.getMobile(), new AVIMClientCallback() {
			@Override
			public void done(AVIMClient client, AVIMException e) {
				if (e == null) {
					HashMap<String,Object> attributes=new HashMap<String, Object>();
		            attributes.put("buyer_user_id", user.getUser_id());
		            attributes.put("buyer_user_name", user.getName());
		            attributes.put("seller_user_id", seller_user_id);
		            attributes.put("seller_user_name", seller_user_name);
					// 创建与Jerry之间的对话
					client.createConversation(Arrays.asList(seller_user_mobile), seller_user_name, attributes, new AVIMConversationCreatedCallback() {
						@Override
						public void done(AVIMConversation conversation, AVIMException e) {
							if (e == null) {
								// 发送消息
								conversation.sendMessage(avimTypedMessage, new AVIMConversationCallback() {
									@Override
									public void done(AVIMException e) {
										if (e == null) {
											//发送成功
											CommonsUtil.showShortToast(getApplicationContext(), "发送成功");
										}else{
											CommonsUtil.showShortToast(getApplicationContext(), "发送失败" + e.getMessage());
											LOGGER.error("发送失败", e);
										}
									}
								});
							}
						}
					});
				}
			}
		});
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
		
		/*User toUser = new User();
		toUser.setUser_id(Integer.parseInt(seller_user_id));
		toUser.setMobile(seller_user_mobile);
		toUser.setName(seller_user_name);*/
		//发送到后端
		AVIMTextMessage avimTextMessage = new AVIMTextMessage();
		avimTextMessage.setText(message);
		
		leanCloudSend(avimTextMessage);
		
		chat_send_edit_msg.setText("");
	}
	
	private void queryById(){
		String url = HttpUtil.BASE_URL + "/user/query.do?id="+seller_user_id;
		try{
			String json = HttpUtil.getRequest(url);
			if(json == null){
				CommonsUtil.showLongToast(getApplicationContext(), "查询用户信息失败");
				return;
			}
			
			User user = JsonUtil.parse2Object(json, User.class);
			seller_user_mobile = user.getMobile();
			
		}catch (Exception e) {
			LOGGER.error(">>> 查询用户信息失败", e);
			CommonsUtil.showLongToast(getApplicationContext(), "查询用户信息失败");
		}
	}
	
	private void queryByStoreId(){
		AppContext application = (AppContext)getApplication();
		int store_id = application.getStore_id();
		String url = HttpUtil.BASE_URL + "/user/queryByStoreId.do?store_id="+store_id;
		try{
			String json = HttpUtil.getRequest(url);
			if(json == null){
				CommonsUtil.showLongToast(getApplicationContext(), "查询用户信息失败");
				return;
			}
			
			User user = JsonUtil.parse2Object(json, User.class);
			seller_user_mobile = user.getMobile();
			seller_user_id = user.getUser_id() + "";
			seller_user_name = user.getName();
			
		}catch (Exception e) {
			LOGGER.error(">>> 查询用户信息失败", e);
			CommonsUtil.showLongToast(getApplicationContext(), "查询用户信息失败");
		}
	}
/*	//发送
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
*/}
