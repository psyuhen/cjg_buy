/**
 * 
 */
package com.cjhbuy.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.math.NumberUtils;

/**
 * 客户端检验类
 * 
 * @author ps
 * 
 */
public class Validator {
	/**
	 * 校验密码是否为数字、字母、下划线
	 * 
	 * @param pwd
	 * @return
	 */
	public static boolean isPassword(String pwd) {
		String regex = "^[A-Za-z]\\w";
		return match(regex, pwd);
	}

	/**
	 * 校验密码长度为6到20位
	 * 
	 * @param pwd
	 * @return
	 */
	public static boolean isPwdLength(String pwd) {
		String regex = "^.{6,20}$";
		return match(regex, pwd);
	}

	/**
	 * 检验手机号码
	 * @param mobile
	 * @return
	 */
	public static boolean isMoblie(String mobile) {
		String regex = "^0?(13[0-9]|15[012356789]|17[678]|18[0-9]|14[57])[0-9]{8}$";
		return match(regex, mobile);
	}
	
	/**
	 * 判断输入的字符是否实数
	 * @param number
	 * @return
	 */
	public static boolean isNumber(String number){
		return NumberUtils.isNumber(number);
	}
	
	/**
	 * 判断输入的字符是否全部为数字
	 * @param digits
	 * @return
	 */
	public static boolean isDigits(String digits){
		return NumberUtils.isDigits(digits);
	}
	
	private static boolean match(String regex, String str) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		return matcher.lookingAt();
	}
}
