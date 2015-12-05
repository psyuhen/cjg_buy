package com.cjhbuy.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.cjhbuy.common.Constants;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

public class CommonsUtil {

	public static boolean hasSdcard() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}
	/**
	 * 打开相册
	 * @param activity
	 */
	public static void opengallry(Activity activity) {
		Intent intentFromGallery = new Intent();
		intentFromGallery.setType("image/*"); // 设置文件类型
		intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
		activity.startActivityForResult(intentFromGallery,
				Constants.IMAGE_REQUEST_CODE);
	}
	/**
	 *打开照相机
	 * @param activity
	 */
	public static void openCamera(Activity activity)
	{
		Intent intentFromCapture = new Intent(
				MediaStore.ACTION_IMAGE_CAPTURE);
		// 判断存储卡是否可以用，可用进行存储
		if (hasSdcard()) {
			intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri
					.fromFile(new File(FileUtil.getAppFolder(),
							Constants.IMAGE_FILE_NAME)));
		}
		activity.startActivityForResult(intentFromCapture,
				Constants.CAMERA_REQUEST_CODE);
	}
	
	public static void showLongToast(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	}

	public static void showShortToast(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}
	@SuppressLint("SimpleDateFormat")
	public static String getDateStr(Date date)
	{
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		return format.format(date);
	}
	/**
	 * 从字符串转换成整形
	 * 
	 * @param str
	 *            待转换字符串
	 * @return
	 */
	public static int String2Int(String str) {
		try {
			int value = Integer.valueOf(str);
			return value;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * 是否免邮费
	 */
	public static  int postage(Double money){
		if (money<49) {
			return 0;
		}
		return 0;
	}
	
	
	public static String getOrderStatus(char type) {
		switch (type) {
		case '1':
			return "卖方确认订单";
		case '2':
			return "等待买方付款";
		case '3':
			return "卖方发货";
		case '4':
			return "交易完成";
		case '5':
			return "交易取消";
		case '6':
			return "交易关闭";
		default:
			return "";
		}
	}
}
