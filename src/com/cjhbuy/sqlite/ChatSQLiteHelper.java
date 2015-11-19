/**
 * 
 */
package com.cjhbuy.sqlite;

import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.messages.AVIMAudioMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.cjhbuy.bean.User;
import com.cjhbuy.common.Constants;
import com.cjhbuy.utils.DateUtil;
import com.cjhbuy.utils.StringUtil;
/**
 * 
 * @author pansen
 *
 */
public class ChatSQLiteHelper extends SQLiteOpenHelper {
	// 调用父类构造器
	public ChatSQLiteHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	/**
	 * 当数据库首次创建时执行该方法，一般将创建表等初始化操作放在该方法中执行. 重写onCreate方法，调用execSQL方法创建表
	 * */
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "CREATE TABLE cjh_buyer_chat (id INTEGER PRIMARY KEY,";
		sql += " user_id varchar(64), friend_id varchar(64), ";
//		sql += " user_name varchar(64), friend_name varchar(64), ";
		sql += " user_mobile varchar(64), friend_mobile varchar(64), ";
		sql += " photo varchar(64), ";//谁发的消息，这photo就属于谁的
		sql += " contentChat varchar(255), typeChat varchar(8), ";//typeChat 为text或者audio.
		sql += " postdateChat varchar(30), isreadChat integer, ";
		sql += " url varchar(400), audio_time varchar(10), ";//audio文件url,audio为语音时间
		sql += " ismineChat integer NOT NULL DEFAULT (0), "; //0：对方的消息  1：自己发送的消息
		sql += " deleteChat integer); ";
		db.execSQL(sql);
	}

	// 当打开数据库时传入的版本号与当前的版本号不同时会调用该方法
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	/**
	 * 
	 * @param message
	 * @param user
	 * @param other_id
	 * @param other_mobile
	 * @param isRead 0:未读  1：已读
	 * @param isMineChat 0：对方的消息  1：自己发送的消息
	 */
	public void insertChat(AVIMTypedMessage message, User user, String other_id, String other_mobile, String isRead, String isMineChat){
		String content = "";
		String typeChat = "";
		String audioTime = "";
		String url = "";
		String photo = "";
		if(message instanceof AVIMTextMessage){//文本
			AVIMTextMessage textMsg = (AVIMTextMessage)message;
			content = textMsg.getText();
			
			typeChat = "text";
			
			Map<String, Object> attrs = textMsg.getAttrs();
			photo = StringUtil.trimToEmpty(attrs.get("photo"));
		}else if(message instanceof AVIMAudioMessage){//语音
			AVIMAudioMessage audio = (AVIMAudioMessage)message;
			AVFile avFile = audio.getAVFile();
			url = avFile.getUrl();
			
			typeChat = "audio";
			Map<String, Object> attrs = audio.getAttrs();
			
			content = StringUtil.trimToEmpty(attrs.get("file_name"));
			audioTime = StringUtil.trimToEmpty(attrs.get("audio_time"));
			photo = StringUtil.trimToEmpty(attrs.get("photo"));
		}
		
		String time = DateUtil.today("yyyy-MM-dd HH:mm");
		long timestamp = message.getTimestamp();
		if(timestamp != 0){
			time = DateUtil.getDateStrByLong(timestamp);
		}
		
		String user_mobile = "";
		String friend_mobile = "";
		if(Constants.MINE.equals(isMineChat) && user.getMobile().equals(message.getFrom())){
			user_mobile = user.getMobile();
			friend_mobile = other_mobile;
		}else{
			user_mobile = message.getFrom();
			friend_mobile = user.getMobile();
		}
		
		SQLiteDatabase db = getReadableDatabase();
		ContentValues values = new ContentValues();
        values.put("user_id", user.getUser_id() + "");
        values.put("friend_id", other_id);
        values.put("user_mobile", user_mobile);
        values.put("friend_mobile", friend_mobile);
        values.put("photo", photo);
        values.put("url", url);
        values.put("audio_time", audioTime);
        values.put("deleteChat", "0");
        values.put("contentChat", content);//如果是文本，即为文件内容；如果是语音，即是文件名称
        values.put("typeChat", typeChat);
        values.put("postdateChat", time);
        values.put("isreadChat", isRead);//0:未读  1：已读
        values.put("ismineChat", isMineChat);//0：对方的消息  1：自己发送的消息
		db.insert("cjh_buyer_chat",null,values);
        db.close();
	}
}