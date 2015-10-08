/**
 * 
 */
package com.cjhbuy.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;

/**
 * 文件操作
 * 
 * @author ps
 * 
 */
public class FileUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);
	private final static String FOLDER = "cjh_buyer";

	/**
	 * 创建目录，相对于Environment#getExternalStorageDirectory
	 * 
	 * @param folderName
	 */
	public static void createDir(String folderName) {
		File folder = new File(Environment.getExternalStorageDirectory() + "/"
				+ folderName);
		if (!folder.exists()) {
			folder.mkdir();
		}
	}

	/**
	 * 创建APP目录
	 */
	public static void createAppFolder() {
		createDir(FOLDER);
	}

	/**
	 * 获取APP目录
	 * 
	 * @return
	 */
	public static String getAppFolder() {
		return Environment.getExternalStorageDirectory() + "/" + FOLDER;
	}
	/**
	 * 获取APP目录
	 * 
	 * @return
	 */
	public static File getAppFolderFile() {
		return new File(getAppFolder());
	}
	/**
	 * 获取APP目录
	 * @param fileName
	 * @return
	 */
	public static File getAppFolderFile(String fileName) {
		return new File(getAppFolder() + "/" + fileName);
	}
	/**
	 * 判断文件是否存在
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean isExists(String fileName){
		String folder = getAppFolder();
		File file = new File(folder+"/" + fileName);
		return file.exists();
	}

	/**
	 * 获取文件的Bitmap
	 * @param fileName
	 * @return
	 */
	public static Bitmap fromFile(String fileName){
		return BitmapFactory.decodeFile(getAppFolder() + "/" + fileName);
	}
	
	/**
	 * 从文件服务器中获取文件，如果文件名为null，返回null,
	 * 否则判断文件是否在文件中存在，否，从文件服务器中获取成功后，存在到手机中
	 * 是，直接从手机获取
	 * @param fileName 输入的文件名
	 * @return
	 */
	public static Bitmap getCacheFile(String fileName){
		//如果文件名不存在，即为还没保存文件，直接返回Null
		if(fileName == null || "".equals(fileName)){
			return null;
		}
		//判断文件是否存在
		boolean exists = FileUtil.isExists(fileName);
		if(!exists){
			String imageUrl = QiNiuUtil.getImageUrl(fileName);
			if(!"".equals(imageUrl)){
				FileOutputStream fos = null;
				BufferedOutputStream bos = null;
				try {
					Bitmap bitmap = QiNiuUtil.getQiNiu(imageUrl);
					
					if(bitmap == null){
						return null;
					}
					
					//保存文件到本地
					File local = getAppFolderFile(fileName);
					local.createNewFile();
					
					fos = new FileOutputStream(local);
					bos = new BufferedOutputStream(fos);
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
					bos.flush();
					
					return bitmap;
				} catch (Exception e) {
					LOGGER.error("从7牛中获取文件出错",e);
				}finally{
					if(bos != null){
						try {
							bos.close();
						} catch (IOException e) {
							LOGGER.error("关闭流出错",e);
						}
					}
					if(fos != null){
						try {
							fos.close();
						} catch (IOException e) {
							LOGGER.error("关闭流出错",e);
						}
					}
				}
			}
		}else if(exists){
			return fromFile(fileName);
		}
		
		return null;
	}
}
