package com.cjhbuy.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageHandler;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.messages.AVIMAudioMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.cjhbuy.activity.CommunicationActivity;
import com.cjhbuy.activity.R;
import com.cjhbuy.auth.SessionManager;
import com.cjhbuy.bean.User;
import com.cjhbuy.common.Constants;
import com.cjhbuy.event.ImTypeMessageEvent;
import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;

import de.greenrobot.event.EventBus;

/**
 * 处理离线信息等等
 */
public class MessageHandler extends AVIMMessageHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(AppContext.class);

	private Context context;

	public MessageHandler(Context context) {
		this.context = context;
	}

	@Override
	public void onMessage(AVIMMessage message,AVIMConversation conversation, AVIMClient client) {
		LOGGER.info("client:" + client.getClientId());
		String clientID = "";
		try {
			//因为使用手机号码做clientid,因此必须先登录 
			clientID = ((AppContext)context).sessionManager.get(SessionManager.KEY_MOBILE);
			if (client.getClientId().equals(clientID)) {
				// 过滤掉自己发的消息
				if (!message.getFrom().equals(clientID)) {//getFrom 为消息的发送者
					sendEvent(message, conversation);
					ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
					List<RunningTaskInfo> runningTasks = am.getRunningTasks(1);
				    RunningTaskInfo rti = runningTasks.get(0);
				    ComponentName component = rti.topActivity;
				    if(component.getClassName().equals(CommunicationActivity.class.getName())){
				    	return;
				    }
				    
					if (NotificationUtils.isShowNotification(conversation.getConversationId())) {
						sendNotification(message, conversation, client);
					}
				}
			} else {
				client.close(null);
			}
		} catch (IllegalStateException e) {
			client.close(null);
			LOGGER.error("接收消息出错咯", e);
		}

	}

	/**
	 * 因为没有 db，所以暂时先把消息广播出去，由接收方自己处理 稍后应该加入 db
	 * 
	 * @param message
	 * @param conversation
	 */
	private void sendEvent(AVIMMessage message, AVIMConversation conversation) {
		 ImTypeMessageEvent event = new ImTypeMessageEvent();
		 event.message = (AVIMTypedMessage)message;
		 event.conversation = conversation;
		 EventBus.getDefault().post(event);
		 
		 String seller_user_id = StringUtil.trimToEmpty(conversation.getAttribute("seller_user_id"));
		 String seller_user_moblie = message.getFrom();
		 AppContext appContext = (AppContext)context;
		 User user = appContext.sessionManager.getUserDetails();
		 
		 appContext.openHelper.insertChat(event.message, user, seller_user_id, seller_user_moblie, Constants.READED, Constants.YOURS);
	}

	/**
	 * 显示通知信息
	 * @param message
	 * @param conversation
	 */
	private void sendNotification(AVIMMessage message, AVIMConversation conversation, AVIMClient client) {
		//判断是哪一种消息类型
		String notificationContent = context.getString(R.string.unspport_message_type);
		String title = StringUtil.trimToEmpty(conversation.getAttribute("seller_user_name"));
		String seller_user_id = StringUtil.trimToEmpty(conversation.getAttribute("seller_user_id"));
		
		if(message instanceof AVIMTextMessage){//文本
			AVIMTextMessage textMsg = (AVIMTextMessage)message;
			notificationContent = textMsg.getText();
		}else if(message instanceof AVIMAudioMessage){//语音
			notificationContent = context.getString(R.string.audio_message_type);
		}

		//显示通知
		Intent intent = new Intent(context, NotificationBroadcastReceiver.class);
		intent.putExtra(Constants.CONVERSATION_ID, conversation.getConversationId());
		intent.putExtra(Constants.MEMBER_ID, message.getFrom());//买家手机号
		intent.putExtra(Constants.CLIENT_ID, client.getClientId());//卖家手机号来的
		intent.putExtra("seller_user_id", seller_user_id);//买家ID
		NotificationUtils.showNotification(context, title, notificationContent, null, intent);
	}
}
