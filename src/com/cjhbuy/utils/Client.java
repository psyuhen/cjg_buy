/**
 * 
 */
package com.cjhbuy.utils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.bgpublish.socket.FileSerial;
import com.cjhbuy.bean.User;
import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;

/**
 * 客户端
 * @author ps
 *
 */
public class Client {
	private Logger LOGGER = LoggerFactory.getLogger(Client.class);

	//private String ip = "192.168.43.191";
	private String ip = HttpUtil.IP;
	private int port = 11000;
	private Socket socket = null;
	private ObjectOutputStream oos = null;
	private ObjectInputStream ois = null;
	
	public void connect(){
		if(socket != null){
			return ;
		}
		try {
			socket = new Socket(ip,port);
			socket.setSoTimeout(300);
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
		} catch (UnknownHostException e) {
			LOGGER.error("Ip地址错误", e);
		} catch (IOException e) {
			LOGGER.error("连接服务器失败", e);
		}
	}
	
	public void send(FileSerial fileSerial){
		connect();
		if(socket == null){
			return;
		}
		try {
			oos.writeObject(fileSerial);
			oos.flush();
		} catch (IOException e) {
			LOGGER.error("获取输出流失败", e);
		}
	}
	
	public void toOnLine(String fromUser){
		FileSerial fileSerial = new FileSerial();
		fileSerial.setType(FileSerial.TYPE_ONLINE);
		fileSerial.setFromUserId(fromUser);
		send(fileSerial);
	}
	
	public void toOffLine(String fromUser){
		FileSerial fileSerial = new FileSerial();
		fileSerial.setType(FileSerial.TYPE_OFFLINE);
		fileSerial.setFromUserId(fromUser);
		send(fileSerial);
	}
	
	public List<FileSerial> findOffLineMsg(String fromUser,String toUser){
		FileSerial fileSerial = new FileSerial();
		fileSerial.setType(FileSerial.TYPE_OFFLINE_MSG);
		fileSerial.setFromUserId(fromUser);
		fileSerial.setToUserId(toUser);
		send(fileSerial);
		
		FileSerial receive = receive();
		List<FileSerial> list = new ArrayList<FileSerial>();
		while(receive != null){
			list.add(receive);
			receive = receive();
		}
		
		return list;
	}
	
	/**
	 * 发送即时消息
	 * @param chatContent
	 * @param toUser
	 * @param fromUser
	 */
	public void sendMsg(String chatContent,User toUser,User fromUser){
		FileSerial fileSerial = new FileSerial();
		fileSerial.setType(FileSerial.TYPE_TEXT);
		fileSerial.setFromUserId(fromUser.getUser_id()+"");
		fileSerial.setFromUserName(fromUser.getName());
		fileSerial.setFromUserMobile(fromUser.getMobile());
		fileSerial.setToUserId(toUser.getUser_id()+"");
		fileSerial.setToUserName(toUser.getName());
		fileSerial.setToUserMobile(toUser.getMobile());
		fileSerial.setFileName(chatContent);
		
		send(fileSerial);
	}
	
	public FileSerial receive(){
		Object obj = null;
		connect();
		if(socket == null){
			return null;
		}
		try {
			obj = ois.readObject();
		} catch (StreamCorruptedException e) {
			LOGGER.error("获取输入流失败", e);
		} catch (IOException e) {
			LOGGER.error("获取输入流失败", e);
		} catch (ClassNotFoundException e) {
			LOGGER.error("找不到类", e);
		}
		if(obj == null){
			return null;
		}
		
		return (FileSerial)obj;
	}
	
	public void close(){
		if(socket != null){
			try {
				socket.close();
			} catch (IOException e) {
				LOGGER.error("关闭流失败", e);
			}
		}
	}
}
