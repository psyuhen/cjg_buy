/**
 * 
 */
package com.cjhbuy.utils;

import java.io.File;
import java.util.concurrent.ExecutionException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

/**
 * 7牛图片上传
 * @author ps
 *
 */
public class QiNiuUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(QiNiuUtil.class);
	/**
	 * 获取7牛上的图片并转换为bitmap
	 * @param url
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public static Bitmap getQiNiu(final String url) throws InterruptedException, ExecutionException{
		LOGGER.info(">>> "+url);
		byte[] bytes = HttpUtil.getRequestBype(url);
		if(bytes != null){
			Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
			return bitmap;
		}
		return null;
	}
	/**
	 * 断点续传
	 * @param key 在7牛空间上的新名称
	 * @param file
	 * @return
	 */
	public static void resumeUploadFile(String key, File file, String user_id, UpCompletionHandler handler){
		String token = token(user_id);
		if(TextUtils.isEmpty(token)){
			LOGGER.error(">>> 获取7牛的token失败");
			return;
		}
		UploadManager uploadManager = new UploadManager();
		uploadManager.put(file, key, token, handler, null);
	}	
	/**
	 * 删除7牛上的单个文件
	 * @param key 在7牛空间里面的文件名
	 * @return true-删除成功,false-删除失败
	 */
	public static boolean deleteFile(String key){
		String url = HttpUtil.BASE_URL + "/qiniu/deleteFile.do?key="+key;
		try {
			String json = HttpUtil.getRequest(url);
			if(json != null){
				Boolean isDel = Boolean.valueOf(json);
				if(isDel){
					LOGGER.error(">>> 删除文件["+key+"]成功");
				}
				return isDel;
			}
		} catch (InterruptedException e) {
			LOGGER.error(">>> 删除文件失败",e);
		} catch (ExecutionException e) {
			LOGGER.error(">>> 删除文件失败",e);
		}
		return false;
	}
	
	/**
	 * 获取7牛的token
	 * @param user_id 用户Id
	 * @return
	 */
	public static String token(String user_id){
		String url = HttpUtil.BASE_URL + "/qiniu/getQiNiuToken.do?user_id="+user_id;
		try {
			String json = HttpUtil.getRequest(url);
			if(json != null){
				return json;
			}
		} catch (InterruptedException e) {
			LOGGER.error(">>> 获取token失败",e);
		} catch (ExecutionException e) {
			LOGGER.error(">>> 获取token失败",e);
		}
		
		return "";
	}
	/**
	 * 获取7牛的文件名
	 * @param imageName
	 * @return
	 */
	public static String getImageUrl(String imageName){
		if(imageName == null || "".equals(imageName)){
			return "";
		}
		
		String url = HttpUtil.BASE_URL + "/qiniu/downloadUrl.do?key="+imageName;
		try {
			String json = HttpUtil.getRequest(url);
			if(json != null){
				return json;
			}
		} catch (InterruptedException e) {
			LOGGER.error(">>> 获取文件URL失败",e);
		} catch (ExecutionException e) {
			LOGGER.error(">>> 获取文件URL失败",e);
		}
		
		return "";
	}
}
