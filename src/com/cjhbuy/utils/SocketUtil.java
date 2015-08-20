/**
 * 
 */
package com.cjhbuy.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import com.bgpublish.socket.FileSerial;
import com.cjhbuy.bean.ChatMsgItem;
import com.cjhbuy.bean.User;

/**
 * 
 * @author ps
 *
 */
public class SocketUtil {

	/**
	 * 发送即时消息
	 * @param chatContent 内容
	 * @param toUser 接收者
	 * @param fromUser 发送者
	 */
	public static void send(final String chatContent,final User toUser, final User fromUser){
		FutureTask<Void> task = new FutureTask<Void>(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				Client client = new Client();
				client.sendMsg(chatContent, toUser, fromUser);
				return null;
			}
		});
		
		new Thread(task).start();
	}
	
	/**
	 * 接收即时消息
	 * @param chatContent 内容
	 * @param toUser 接收者
	 * @param fromUser 发送者
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public static List<ChatMsgItem> receive(final String toUser,final String fromUser) throws InterruptedException, ExecutionException{
		FutureTask<List<ChatMsgItem>> task = new FutureTask<List<ChatMsgItem>>(new Callable<List<ChatMsgItem>>() {
			@Override
			public List<ChatMsgItem> call() throws Exception {
				Client client = new Client();
				List<FileSerial> offLineMsgs = client.findOffLineMsg(fromUser, toUser);
				List<ChatMsgItem> list = new ArrayList<ChatMsgItem>();
				for (FileSerial fileSerial : offLineMsgs) {
					ChatMsgItem msgItem = new ChatMsgItem();
					msgItem.setSendDate(new Date());
					msgItem.setComing(true);
					msgItem.setSendUser(fileSerial.getFromUserName());
					msgItem.setSendUserId(fileSerial.getFromUserId());
					msgItem.setSendUserMobile(fileSerial.getFromUserMobile());
					msgItem.setToUser(fileSerial.getToUserName());
					msgItem.setToUserId(fileSerial.getToUserId());
					msgItem.setToUserMobile(fileSerial.getToUserMobile());
					msgItem.setContent(fileSerial.getFileName());
					msgItem.setSendDate(DateUtil.parseDate(fileSerial.getChatTime(), new String[]{"yyyyMMddHHmmss"}));
					list.add(msgItem);
				}
				
				return list;
			}
		});
		
		new Thread(task).start();
		return task.get();
	}
}
