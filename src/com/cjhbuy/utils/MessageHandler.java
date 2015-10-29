package com.cjhbuy.utils;

import android.content.Context;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.AVIMTypedMessageHandler;
import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;

/**
 * 处理离线信息等等
 */
public class MessageHandler extends AVIMTypedMessageHandler<AVIMTypedMessage> {
	private static final Logger LOGGER = LoggerFactory.getLogger(AppContext.class);

	private Context context;

	public MessageHandler(Context context) {
		this.context = context;
	}

	@Override
	public void onMessage(AVIMTypedMessage message,AVIMConversation conversation, AVIMClient client) {
		String clientID = "";
		try {
			clientID = AVImClientManager.getInstance().getClientId();
			if (client.getClientId().equals(clientID)) {
				// 过滤掉自己发的消息
				if (!message.getFrom().equals(clientID)) {
					sendEvent(message, conversation);
					sendNotification(message, conversation);
				}
			} else {
				client.close(null);
			}
		} catch (Exception e) {
			client.close(null);
			LOGGER.error("", e);
		}
	}

	/**
	 * 因为没有 db，所以暂时先把消息广播出去，由接收方自己处理 稍后应该加入 db
	 * 
	 * @param message
	 * @param conversation
	 */
	private void sendEvent(AVIMTypedMessage message, AVIMConversation conversation) {
		// ImTypeMessageEvent event = new ImTypeMessageEvent();
		// event.message = message;
		// event.conversation = conversation;
		// EventBus.getDefault().post(event);
	}

	private void sendNotification(AVIMTypedMessage message, AVIMConversation conversation) {
		// String notificationContent = message instanceof AVIMTextMessage ?
		// ((AVIMTextMessage)message).getText() :
		// context.getString(R.string.unspport_message_type);

		// Intent intent = new Intent(context,
		// NotificationBroadcastReceiver.class);
		// intent.putExtra(Constants.CONVERSATION_ID,
		// conversation.getConversationId());
		// intent.putExtra(Constants.MEMBER_ID, message.getFrom());
		// NotificationUtils.showNotification(context, "", notificationContent,
		// null, intent);
	}
}
