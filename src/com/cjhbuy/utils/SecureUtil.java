package com.cjhbuy.utils;

import java.security.MessageDigest;

/**
 * 加密工具
 * 
 * @author ps
 */
public class SecureUtil {
	private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	/**
	 * 单向加密
	 * 
	 * @param algorithm 加密方式 MD5 SHA
	 * @param plaintext  明文
	 * @return 密文
	 */
	public static String encode(String algorithm, String plaintext) {
		if (plaintext == null) {
			return null;
		}
		try {
			MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
			messageDigest.update(plaintext.getBytes());
			return getFormattedText(messageDigest.digest());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * MD5加密
	 * @param plaintext 明文
	 * @return 返回密文
	 */
	public static String md5Encode(String plaintext){
		return encode("MD5", plaintext);
	}
	/**
	 * SHA加密
	 * @param plaintext 明文
	 * @return 返回密文
	 */
	public static String shaEncode(String plaintext){
		return encode("SHA", plaintext);
	}

	private static String getFormattedText(byte[] bytes) {
		int len = bytes.length;
		StringBuilder buf = new StringBuilder(len * 2);
		for (int j = 0; j < len; j++) {
			buf.append(HEX_DIGITS[(bytes[j] >> 4) & 0x0f]);
			buf.append(HEX_DIGITS[bytes[j] & 0x0f]);
		}
		return buf.toString();
	}
}
