package com.cjhbuy.utils;

import android.text.TextUtils;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;

/**
 * LeanCloud message Client 管理器
 * 
 * @author pansen
 * 
 */
public class AVImClientManager {

	private static AVImClientManager imClientManager;

	private AVIMClient avimClient;
	private String clientId;

	/**
	 * 获取AVImClientManager实例
	 * @return
	 */
	public synchronized static AVImClientManager getInstance() {
		if (null == imClientManager) {
			imClientManager = new AVImClientManager();
		}
		return imClientManager;
	}

	private AVImClientManager() {
	}

	/**
	 * 获取clientId实例，并连接服务器
	 * @param clientId
	 * @param callback
	 */
	public void open(String clientId, AVIMClientCallback callback) {
		this.clientId = clientId;
		avimClient = AVIMClient.getInstance(clientId);
		avimClient.open(callback);
	}

	public AVIMClient getClient() {
		return avimClient;
	}
	
	public String getClientId() {
		if (TextUtils.isEmpty(clientId)) {
			throw new IllegalStateException("Please call AVImClientManager.open first");
		}
		return clientId;
	}
}
