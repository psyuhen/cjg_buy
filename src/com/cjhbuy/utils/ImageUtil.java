package com.cjhbuy.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.graphics.Bitmap;

import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;

public class ImageUtil {
	private final static Logger LOGGER = LoggerFactory.getLogger(ImageUtil.class);


	/**
	 * 把bitmap转换为inputstream
	 * @param bitmap
	 * @return
	 */
	public static InputStream bitmap2stream(Bitmap bitmap){
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		byte[] byteArray = stream.toByteArray();
		ByteArrayInputStream bs = new ByteArrayInputStream(byteArray);
		
		return bs;
	}
	
	/**
	 * 把bitmap转换为file
	 * @param activity
	 * @param bitmap
	 * @return
	 */
	public static File bitmap2file(Activity activity,Bitmap bitmap){
		File filesDir = activity.getFilesDir();
		String currentTime = DateUtil.currentTime();
		File imageFile = new File(filesDir, currentTime + ".png");
		OutputStream os = null;
		try {
			os = new FileOutputStream(imageFile);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
			os.flush();
		} catch (Exception e) {
			LOGGER.error("Error writing bitmap", e);
			return null;
		} finally{
			if(os != null){
				try {
					os.close();
				} catch (IOException e) {
					LOGGER.error( "Error close outputstream", e);
				}
			}
		}
		
		return imageFile;
	}
	/**
	 * 把bitmap转换为file
	 * @param bitmap
	 * @return
	 */
	public static File bitmap2file(Bitmap bitmap){
		File filesDir = FileUtil.getAppFolderFile();
		String currentTime = DateUtil.currentTime();
		File imageFile = new File(filesDir, currentTime + ".png");
		OutputStream os = null;
		try {
			os = new FileOutputStream(imageFile);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
			os.flush();
		} catch (Exception e) {
			LOGGER.error("Error writing bitmap", e);
			return null;
		} finally{
			if(os != null){
				try {
					os.close();
				} catch (IOException e) {
					LOGGER.error( "Error close outputstream", e);
				}
			}
		}
		
		return imageFile;
	}
	
}
