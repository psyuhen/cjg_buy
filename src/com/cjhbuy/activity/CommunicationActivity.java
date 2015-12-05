package com.cjhbuy.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
import com.avos.avoscloud.im.v2.messages.AVIMAudioMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.cjhbuy.adapter.MultipleItemAdapter;
import com.cjhbuy.bean.User;
import com.cjhbuy.common.Constants;
import com.cjhbuy.event.ImTypeMessageEvent;
import com.cjhbuy.event.ImTypeMessageResendEvent;
import com.cjhbuy.event.InputBottomBarTextEvent;
import com.cjhbuy.sqlite.ChatSQLiteHelper;
import com.cjhbuy.utils.AVImClientManager;
import com.cjhbuy.utils.AppContext;
import com.cjhbuy.utils.CommonsUtil;
import com.cjhbuy.utils.DateUtil;
import com.cjhbuy.utils.FileUtil;
import com.cjhbuy.utils.HttpUtil;
import com.cjhbuy.utils.JsonUtil;
import com.cjhbuy.utils.NotificationUtils;
import com.cjhbuy.utils.SoundMeter;
import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;

import de.greenrobot.event.EventBus;

/**
 * 聊天窗口
 * @author pansen
 *
 */
public class CommunicationActivity extends BaseActivity {
	private Logger LOGGER = LoggerFactory.getLogger(CommunicationActivity.class);

	private Button mBtnSend;
	private TextView mBtnRcd;
	private EditText mEditTextContent;
	private RelativeLayout mBottom;
	private ListView mListView;
	private MultipleItemAdapter mAdapter;
	private List<AVIMTypedMessage> mDataArrays = new ArrayList<AVIMTypedMessage>();
	private boolean isShosrt = false;
	private LinearLayout voice_rcd_hint_loading, voice_rcd_hint_rcding, voice_rcd_hint_tooshort;
	private ImageView img1, sc_img1;
	private SoundMeter mSensor;
	private View rcChat_popup;
	private LinearLayout del_re;
	private ImageView chatting_mode_btn, volume;
	private boolean btn_vocie = false;
	private int flag = 1;
	private Handler mHandler = new Handler();
	private String voiceName;
	private long startVoiceT, endVoiceT;
	
	
//	private String buyer_user_id;
//	private String buyer_user_name;
//	private String buyer_user_mobile;
//	private String buyer_user_photo;
	
	//卖家用户名与ID
	private String seller_user_id;
	private String seller_user_name;
	private String seller_user_mobile;
	private String seller_user_photo;
	//买家用户
	private User user;
	
	//对话
	protected AVIMConversation imConversation;

	//聊天记录
	private ChatSQLiteHelper openHelper;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_communication);
		try{
			// 启动activity时不自动弹出软键盘
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			initView();
			initData();
			
			EventBus.getDefault().register(this);
		}catch (Exception e) {
			LOGGER.error(e.getMessage(),e);
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (null != imConversation) {
			NotificationUtils.addTag(imConversation.getConversationId());
		}
	}

	@Override
	public void onPause() {
		super.onResume();
		if (null != imConversation) {
			NotificationUtils.removeTag(imConversation.getConversationId());
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

	public void initView() {
		super.initView();
		right_imgbtn.setVisibility(View.GONE);
		mListView = (ListView) findViewById(R.id.listview);
		mBtnSend = (Button) findViewById(R.id.btn_send);
		mBtnRcd = (TextView) findViewById(R.id.btn_rcd);
		mBtnSend.setOnClickListener(this);
		mBottom = (RelativeLayout) findViewById(R.id.btn_bottom);
		chatting_mode_btn = (ImageView) this.findViewById(R.id.ivPopUp);
		volume = (ImageView) this.findViewById(R.id.volume);
		rcChat_popup = this.findViewById(R.id.rcChat_popup);
		img1 = (ImageView) this.findViewById(R.id.img1);
		sc_img1 = (ImageView) this.findViewById(R.id.sc_img1);
		
		del_re = (LinearLayout) this.findViewById(R.id.del_re);
		voice_rcd_hint_rcding = (LinearLayout) this.findViewById(R.id.voice_rcd_hint_rcding);
		voice_rcd_hint_loading = (LinearLayout) this.findViewById(R.id.voice_rcd_hint_loading);
		voice_rcd_hint_tooshort = (LinearLayout) this.findViewById(R.id.voice_rcd_hint_tooshort);
		mSensor = new SoundMeter();
		mEditTextContent = (EditText) findViewById(R.id.et_sendmessage);

		// 语音文字切换按钮
		chatting_mode_btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (btn_vocie) {
					mBtnRcd.setVisibility(View.GONE);
					mBottom.setVisibility(View.VISIBLE);
					btn_vocie = false;
					chatting_mode_btn.setImageResource(R.drawable.chatting_setmode_msg_btn);
				} else {
					mBtnRcd.setVisibility(View.VISIBLE);
					mBottom.setVisibility(View.GONE);
					chatting_mode_btn.setImageResource(R.drawable.chatting_setmode_voice_btn);
					btn_vocie = true;
				}
			}
		});
		mBtnRcd.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				// 按下语音录制按钮时返回false执行父类OnTouch
				return false;
			}
		});
	}


	public void initData() {
		mAdapter = new MultipleItemAdapter(this, mDataArrays);
		mListView.setAdapter(mAdapter);

		
		//获取买家信息
		user = sessionManager.getUserDetails();
		//接收传输过来的数据
		Intent intent = getIntent();
		seller_user_id = intent.getStringExtra("seller_user_id");
		seller_user_name = intent.getStringExtra("seller_user_name");
		seller_user_mobile = intent.getStringExtra("seller_user_mobile");
		
		if(seller_user_id == null || "".equals(seller_user_id) || "0".equals(seller_user_id)){
			queryByStoreId();
		}else{
			queryUserInfo(seller_user_id);//查询卖家信息
		}
		
		AppContext application = (AppContext)getApplication();
		title.setText(application.getStore_name());
		getConversation(seller_user_mobile);
		
		
		initDbData();
		readChat();
	}
	
	
	private void initDbData(){
		//准备数据库，存取聊天记录
        openHelper=new ChatSQLiteHelper(this,"cjh_buyer_chat.db",null,1) ;
	}
	
	private void readChat(){
		//获取数据库中的信息
		Cursor c = null;
		try{
			SQLiteDatabase db=openHelper.getReadableDatabase();
	        String sql="select contentChat,postdateChat,isreadChat,ismineChat,typeChat,photo,url,audio_time,user_mobile from cjh_buyer_chat where user_id=? and friend_id=?";
	        c = db.rawQuery(sql,new String[]{user.getUser_id()+"", seller_user_id});
	        while(c.moveToNext()){
	        	String text = c.getString(0);
	        	String postdateChat = c.getString(1);
	        	String typeChat = c.getString(4);
	        	String photo = c.getString(5);
	        	String url = c.getString(6);
	        	String audio_time = c.getString(7);
	        	String user_mobile = c.getString(8);
	        	
	        	long timestamp = DateUtil.getDateLongByStr(postdateChat);
	        	if("text".equals(typeChat)){
	        		AVIMTextMessage avimTextMessage = new AVIMTextMessage();
	        		HashMap<String, Object> attributes = new HashMap<String, Object>();
	        		attributes.put("photo", photo);
	        		avimTextMessage.setTimestamp(timestamp);
	        		avimTextMessage.setAttrs(attributes);
	        		avimTextMessage.setFrom(user_mobile);
	        		avimTextMessage.setText(text);
	        		mDataArrays.add(avimTextMessage);
	        	}else if("audio".equals(typeChat)){
	        		FileUtil.getRemoteFile(url, text);
	        		AVIMAudioMessage audioMessage = new AVIMAudioMessage(new File(FileUtil.getAudioFolder() + "/" + text));
					HashMap<String, Object> attributes = new HashMap<String, Object>();
					attributes.put("audio_time", audio_time);
					attributes.put("photo", photo);
					attributes.put("file_name", text);
					audioMessage.setAttrs(attributes);
					audioMessage.setTimestamp(timestamp);
					audioMessage.setFrom(user_mobile);
					mDataArrays.add(audioMessage);
	        	}
	        }
	        mAdapter.notifyDataSetChanged();
	        mListView.setSelection(mListView.getCount() - 1);
		}catch (Exception e) {
			LOGGER.error("读取聊天记录报错", e);
		}finally{
			if(c != null){
				c.close();
			}
		}
	}
	
	//查询买家信息
	private void queryUserInfo(String user_id){
		String url = HttpUtil.BASE_URL + "/user/query.do?id=" + user_id;
		try{
			String json = HttpUtil.getRequest(url);
			if(json == null){
				return;
			}
			
			User sellerInfo = JsonUtil.parse2Object(json, User.class);
			seller_user_name = sellerInfo.getName();
			seller_user_mobile = sellerInfo.getMobile();
			seller_user_photo = sellerInfo.getPhoto();
		}catch (Exception e) {
			LOGGER.error("查询用户信息出错",e);
		}
	}
	
	//查询卖家用户信息
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
			seller_user_photo = user.getPhoto();
			
		}catch (Exception e) {
			LOGGER.error(">>> 查询用户信息失败", e);
			CommonsUtil.showLongToast(getApplicationContext(), "查询用户信息失败");
		}
	}

	/**
	 * 获取 conversation，为了避免重复的创建，此处先 query 是否已经存在只包含该 member 的 conversation
	 * 如果存在，则直接赋值给 ChatFragment，否者创建后再赋值
	 * @param memberId 买家手机号码
	 */
	private void getConversation(final String memberId) {
		AVImClientManager instance = AVImClientManager.getInstance();
		instance.open(user.getMobile(), new AVIMClientCallback() {
			@Override
			public void done(AVIMClient client, AVIMException e) {
				if(e == null){
					AVQuery.clearAllCachedResults();//TODO 先清空缓存再说
					AVIMConversationQuery conversationQuery = client.getQuery();
					conversationQuery.withMembers(Arrays.asList(memberId), true);
					conversationQuery.whereEqualTo("buyer_user_id", user.getUser_id());
					conversationQuery.findInBackground(new AVIMConversationQueryCallback() {
						@Override
						public void done(List<AVIMConversation> list, AVIMException e) {
							if (filterException(e)) {
								// 注意：此处仍有漏洞，如果获取了多个 conversation，默认取第一个
								if (null != list && list.size() > 0) {
									imConversation = list.get(0);
								} else {
									createConversation(memberId);
								}
							}else{
								createConversation(memberId);
							}
						}
					});
				}else{
					LOGGER.error("连接失败", e);
				}
			}
		});
	}
	/**
	 * 获取AVIMClient，若还没连接即open,否则直接返回。
	 * @return
	 */
	private AVIMClient getClient(){
		AVImClientManager instance = AVImClientManager.getInstance();
		
		return instance.getClient();
	}
	/**
	 * 创建一个对话。
	 * @param memberId
	 */
	private void createConversation(String memberId) {
		AVIMClient client = getClient();
		HashMap<String, Object> attributes = new HashMap<String, Object>();
		attributes.put("buyer_user_id", user.getUser_id());
        attributes.put("buyer_user_name", user.getName());
        attributes.put("seller_user_id", seller_user_id);
        attributes.put("seller_user_name", seller_user_name);
		client.createConversation(Arrays.asList(memberId), user.getName(),
			attributes, false, new AVIMConversationCreatedCallback() {
				@Override
				public void done(AVIMConversation avimConversation, AVIMException e) {
					imConversation = avimConversation;
				}
			});
	}
	
	/**
	 * 判断是否出错，出错返回false.
	 * @param e
	 * @return
	 */
	protected boolean filterException(Exception e) {
		if (e != null) {
			//CommonsUtil.showShortToast(getApplicationContext(), e.getMessage());
			LOGGER.error(e.getMessage(), e);
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * 发送leancloud消息
	 * @param avimTypedMessage
	 */
	public void leanCloudSend(final AVIMTypedMessage avimTypedMessage) {
		if(imConversation != null){
			imConversation.sendMessage(avimTypedMessage, new AVIMConversationCallback() {
				@Override
				public void done(AVIMException e) {
					if (e == null) {
						//发送成功
						CommonsUtil.showShortToast(getApplicationContext(), "发送成功");
//						itemAdapter.notifyDataSetChanged();
					}else{
						CommonsUtil.showShortToast(getApplicationContext(), "发送失败" + e.getMessage());
						LOGGER.error("发送失败", e);
					}
				}
			});
		}else{
			LOGGER.warn("imConversation 为空,发送不了消息");
		}
	}
	
	public void onClick(View v) {
		super.onClick(v);
		
		switch (v.getId()) {
		case R.id.btn_send:
			send();
			break;
		}
	}

	//发送
	private void send() {
		String contString = mEditTextContent.getText().toString();
		if(TextUtils.isEmpty(contString.trim())){
			CommonsUtil.showLongToast(getApplicationContext(), "内容不能为空");
			return;
		}
		
		//发送文本消息
		AVIMTextMessage avimTextMessage = new AVIMTextMessage();
		HashMap<String, Object> attributes = new HashMap<String, Object>();
		attributes.put("photo", user.getPhoto());
		avimTextMessage.setAttrs(attributes);
		avimTextMessage.setFrom(user.getMobile());
		avimTextMessage.setText(contString);
		
		leanCloudSend(avimTextMessage);//发送
		
		mDataArrays.add(avimTextMessage);
		mEditTextContent.setText("");
		mAdapter.notifyDataSetChanged();
		mListView.setSelection(mListView.getCount() - 1);
		
		openHelper.insertChat(avimTextMessage, user, seller_user_id, seller_user_mobile, Constants.READED, Constants.MINE);
	}

	
	
	// 按下语音录制按钮时
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!FileUtil.getAudioFolderFile().exists()) {
			CommonsUtil.showShortToast(this, "没有SDCrad");
			return false;
		}

		if (btn_vocie) {
			int[] location = new int[2];
			mBtnRcd.getLocationInWindow(location); // 获取在当前窗口内的绝对坐标
			int btn_rc_Y = location[1];
			int btn_rc_X = location[0];
			int[] del_location = new int[2];
			del_re.getLocationInWindow(del_location);
			int del_Y = del_location[1];
			int del_x = del_location[0];
			if (event.getAction() == MotionEvent.ACTION_DOWN && flag == 1) {
				if (event.getY() > btn_rc_Y && event.getX() > btn_rc_X) {// 判断手势按下的位置是否是语音录制按钮的范围内
					mBtnRcd.setBackgroundResource(R.drawable.voice_rcd_btn_pressed);
					rcChat_popup.setVisibility(View.VISIBLE);
					voice_rcd_hint_loading.setVisibility(View.VISIBLE);
					voice_rcd_hint_rcding.setVisibility(View.GONE);
					voice_rcd_hint_tooshort.setVisibility(View.GONE);
					mHandler.postDelayed(new Runnable() {
						public void run() {
							if (!isShosrt) {
								voice_rcd_hint_loading.setVisibility(View.GONE);
								voice_rcd_hint_rcding.setVisibility(View.VISIBLE);
							}
						}
					}, 300);
					img1.setVisibility(View.VISIBLE);
					del_re.setVisibility(View.GONE);
					startVoiceT = SystemClock.currentThreadTimeMillis();
					voiceName = DateUtil.currentTime() + startVoiceT + ".amr";//文件名
					start(voiceName);
					flag = 2;
				}
			} else if (event.getAction() == MotionEvent.ACTION_UP && flag == 2) {// 松开手势时执行录制完成
				mBtnRcd.setBackgroundResource(R.drawable.voice_rcd_btn_nor);
				if (event.getY() >= del_Y
						&& event.getY() <= del_Y + del_re.getHeight()
						&& event.getX() >= del_x
						&& event.getX() <= del_x + del_re.getWidth()) {
					rcChat_popup.setVisibility(View.GONE);
					img1.setVisibility(View.VISIBLE);
					del_re.setVisibility(View.GONE);
					stop();
					flag = 1;
					File file = new File(FileUtil.getAudioFolder() + "/" + voiceName);
					if (file.exists()) {
						file.delete();
					}
				} else {
					voice_rcd_hint_rcding.setVisibility(View.GONE);
					stop();
					endVoiceT = SystemClock.currentThreadTimeMillis();
					flag = 1;
					int time = (int) ((endVoiceT - startVoiceT) / 100);
					if (time < 1) {
						isShosrt = true;
						voice_rcd_hint_loading.setVisibility(View.GONE);
						voice_rcd_hint_rcding.setVisibility(View.GONE);
						voice_rcd_hint_tooshort.setVisibility(View.VISIBLE);
						mHandler.postDelayed(new Runnable() {
							public void run() {
								voice_rcd_hint_tooshort.setVisibility(View.GONE);
								rcChat_popup.setVisibility(View.GONE);
								isShosrt = false;
							}
						}, 500);
						return false;
					}
					
					//来到这里面 表示录制成功了。
					try{
						AVIMAudioMessage audioMessage = new AVIMAudioMessage(new File(FileUtil.getAudioFolder() + "/" + voiceName));
						HashMap<String, Object> attributes = new HashMap<String, Object>();
						attributes.put("audio_time", time + "\"");
						attributes.put("photo", user.getPhoto());
						attributes.put("file_name", voiceName);
						audioMessage.setAttrs(attributes);
						audioMessage.setFrom(user.getMobile());
						leanCloudSend(audioMessage);//发送
						
						mDataArrays.add(audioMessage);
						mAdapter.notifyDataSetChanged();
						mListView.setSelection(mListView.getCount() - 1);
						rcChat_popup.setVisibility(View.GONE);
						
						openHelper.insertChat(audioMessage, user, seller_user_id, seller_user_mobile, Constants.READED, Constants.MINE);
					}catch (Exception e) {
						LOGGER.error("录制语音出错", e);
					}
				}
			}
			if (event.getY() < btn_rc_Y) {// 手势按下的位置不在语音录制按钮的范围内
				Animation mLitteAnimation = AnimationUtils.loadAnimation(this, R.anim.cancel_rc);
				Animation mBigAnimation = AnimationUtils.loadAnimation(this, R.anim.cancel_rc2);
				img1.setVisibility(View.GONE);
				del_re.setVisibility(View.VISIBLE);
				del_re.setBackgroundResource(R.drawable.voice_rcd_cancel_bg);
				if (event.getY() >= del_Y
						&& event.getY() <= del_Y + del_re.getHeight()
						&& event.getX() >= del_x
						&& event.getX() <= del_x + del_re.getWidth()) {
					del_re.setBackgroundResource(R.drawable.voice_rcd_cancel_bg_focused);
					sc_img1.startAnimation(mLitteAnimation);
					sc_img1.startAnimation(mBigAnimation);
				}
			} else {
				img1.setVisibility(View.VISIBLE);
				del_re.setVisibility(View.GONE);
				del_re.setBackgroundResource(0);
			}
		}
		return super.onTouchEvent(event);
	}

	private static final int POLL_INTERVAL = 1;

	private Runnable mSleepTask = new Runnable() {
		public void run() {
			stop();
		}
	};
	private Runnable mPollTask = new Runnable() {
		public void run() {
			double amp = mSensor.getAmplitude();
			updateDisplay(amp);
			mHandler.postDelayed(mPollTask, POLL_INTERVAL);

		}
	};

	private void start(String name) {
		mSensor.start(name);
		mHandler.postDelayed(mPollTask, POLL_INTERVAL);
	}

	private void stop() {
		mHandler.removeCallbacks(mSleepTask);
		mHandler.removeCallbacks(mPollTask);
		mSensor.stop();
		volume.setImageResource(R.drawable.amp1);
	}

	private void updateDisplay(double signalEMA) {
		switch ((int) signalEMA) {
		case 0:
		case 1:
			volume.setImageResource(R.drawable.amp1);
			break;
		case 2:
		case 3:
			volume.setImageResource(R.drawable.amp2);

			break;
		case 4:
		case 5:
			volume.setImageResource(R.drawable.amp3);
			break;
		case 6:
		case 7:
			volume.setImageResource(R.drawable.amp4);
			break;
		case 8:
		case 9:
			volume.setImageResource(R.drawable.amp5);
			break;
		case 10:
		case 11:
			volume.setImageResource(R.drawable.amp6);
			break;
		default:
			volume.setImageResource(R.drawable.amp7);
			break;
		}
	}
	
	/**
	   * 输入事件处理，接收后构造成 AVIMTextMessage 然后发送
	   * 因为不排除某些特殊情况会受到其他页面过来的无效消息，所以此处加了 tag 判断
	   */
	public void onEvent(InputBottomBarTextEvent textEvent) {
		if (null != imConversation && null != textEvent) {
			if (!TextUtils.isEmpty(textEvent.sendContent) && imConversation.getConversationId().equals(textEvent.tag)) {
				AVIMTextMessage message = new AVIMTextMessage();
				message.setText(textEvent.sendContent);
				mDataArrays.add(message);
				mAdapter.notifyDataSetChanged();
				mListView.setSelection(mListView.getCount() - 1);
				imConversation.sendMessage(message, new AVIMConversationCallback() {
					@Override
					public void done(AVIMException e) {
						mAdapter.notifyDataSetChanged();
					}
				});
			}
		}
	}

	  /**
	   * 处理推送过来的消息
	   * 同理，避免无效消息，此处加了 conversation id 判断
	   */
	public void onEvent(ImTypeMessageEvent event) {
		LOGGER.info("event.getFrom Id: " + event.message.getFrom());
		if (null != event && seller_user_mobile.equals(event.message.getFrom())) {
			mDataArrays.add(event.message);
			mAdapter.notifyDataSetChanged();
			mListView.setSelection(mListView.getCount() - 1);
			
			//openHelper.insertChat(event.message, user, seller_user_id, seller_user_mobile, Constants.READED, Constants.YOURS);
		}
	}

	  /**
	   * 重新发送已经发送失败的消息
	   */
	public void onEvent(ImTypeMessageResendEvent event) {
		if (null != imConversation && null != event) {
			if (AVIMMessage.AVIMMessageStatus.AVIMMessageStatusFailed == event.message.getMessageStatus()
					&& imConversation.getConversationId().equals(event.message.getConversationId())) {
				imConversation.sendMessage(event.message, new AVIMConversationCallback() {
					@Override
					public void done(AVIMException e) {
						mAdapter.notifyDataSetChanged();
					}
				});
				mAdapter.notifyDataSetChanged();
			}
		}
	}

}
