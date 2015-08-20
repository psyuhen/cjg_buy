package com.cjhbuy.bean;

import java.util.Date;

public class ChatMsgItem {
	private String sendUser;
	private String sendUserId;
	private String sendUserMobile;
	private String content;
	private Date sendDate;
	private String toUser;
	private String toUserId;
	private String toUserMobile;
	private int msgType;
	private boolean isComing=true;
	
	public String getSendUser() {
		return sendUser;
	}
	public void setSendUser(String sendUser) {
		this.sendUser = sendUser;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public boolean isComing() {
		return isComing;
	}
	public void setComing(boolean isComing) {
		this.isComing = isComing;
	}
	
	public Date getSendDate() {
		return sendDate;
	}
	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}
	public String getToUser() {
		return toUser;
	}
	public void setToUser(String toUser) {
		this.toUser = toUser;
	}
	public int getMsgType() {
		return msgType;
	}
	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}
	
	public ChatMsgItem() {
		super();
	}
	public String getSendUserId() {
		return sendUserId;
	}
	public void setSendUserId(String sendUserId) {
		this.sendUserId = sendUserId;
	}
	public String getSendUserMobile() {
		return sendUserMobile;
	}
	public void setSendUserMobile(String sendUserMobile) {
		this.sendUserMobile = sendUserMobile;
	}
	public String getToUserId() {
		return toUserId;
	}
	public void setToUserId(String toUserId) {
		this.toUserId = toUserId;
	}
	public String getToUserMobile() {
		return toUserMobile;
	}
	public void setToUserMobile(String toUserMobile) {
		this.toUserMobile = toUserMobile;
	}
	
}
