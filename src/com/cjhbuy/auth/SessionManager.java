/**
 * 
 */
package com.cjhbuy.auth;

import java.util.Date;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.cjhbuy.activity.LoginActivity;
import com.cjhbuy.bean.User;
import com.cjhbuy.utils.DateUtil;

/**
 * Session管理员
 * 
 * @author ps
 * 
 */
@SuppressLint("CommitPrefEdits")
public class SessionManager {
	// Shared Preferences
	SharedPreferences pref;

	// Editor for Shared preferences
	Editor editor;

	// Context
	Context _context;

	// Shared pref mode
	int PRIVATE_MODE = 0;

	// Sharedpref file name
	private static final String PREF_NAME = "SgamsbuyAppPref";

	// All Shared Preferences Keys
	public static final String IS_LOGIN = "IsLoggedIn";

	// user id (make variable public to access from outside)
	public static final String KEY_USER_ID = "user_id";

	// qq (make variable public to access from outside)
	public static final String KEY_QQ = "qq";

	// we chat (make variable public to access from outside)
	public static final String KEY_WE_CHAT = "we_chat";

	// User name (make variable public to access from outside)
	public static final String KEY_NAME = "name";

	// mobile (make variable public to access from outside)
	public static final String KEY_MOBILE = "mobile";
	
	//登录时间
	public static final String LOGIN_TIME = "login_time";
	//头像
	public static final String KEY_PHOTO = "photo";

	// Constructor
	public SessionManager(Context context) {
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}

	/**
	 * Create login session
	 * */
	public void createLoginSession(User user) {
		// Storing login value as TRUE
		editor.putBoolean(IS_LOGIN, true);

		// Storing user id in pref
		editor.putInt(KEY_USER_ID, user.getUser_id());

		// Storing name in pref
		editor.putString(KEY_NAME, user.getName());

		// Storing mobile in pref
		editor.putString(KEY_MOBILE, user.getMobile());

		// Storing qq in pref
		editor.putString(KEY_QQ, user.getQq());

		// Storing we chat in pref
		editor.putString(KEY_WE_CHAT, user.getWe_chat());
		
		//存储登录时间
		editor.putString(LOGIN_TIME, DateUtil.currentTime());

		//头像
		editor.putString(KEY_PHOTO, user.getPhoto());
		// commit changes
		editor.commit();
	}
	
	//重置登录时间
	public void resetLoginTime(){
		put(LOGIN_TIME, DateUtil.currentTime());
	}
	
	/**
	 * 把数据存在SgamsSaleAppPref中
	 * @param key
	 * @param value
	 */
	public void put(String key,String value){
		editor.putString(key, value);
		editor.commit();
	}
	/**
	 * 把数据存在SgamsSaleAppPref中
	 * @param key
	 * @param value
	 */
	public void putInt(String key,int value){
		editor.putInt(key, value);
		editor.commit();
	}
	/**
	 * 把数据存在SgamsSaleAppPref中
	 * @param key
	 * @param value
	 */
	public void putBoolean(String key,boolean value){
		editor.putBoolean(key, value);
		editor.commit();
	}
	/**
	 * 返回key对应的键值
	 * @param key
	 * @return
	 */
	public String get(String key){
		return pref.getString(key, "");
	}
	/**
	 * 返回key对应的键值
	 * @param key
	 * @return
	 */
	public int getInt(String key){
		return pref.getInt(key, 0);
	}
	/**
	 * 获取用户ID
	 * @return
	 */
	public int getUserId(){
		return getInt(KEY_USER_ID);
	}
	/**
	 * 获取商家ID
	 * @return
	 */
	public int getStoreId(){
		return getInt("store_id");
	}

	/**
	 * 获取用户名称
	 * @return
	 */
	public String getUserName(){
		return get(KEY_NAME);
	}
	/**
	 * Create login session
	 * */
	public void createLoginSession(String userId, String name, String mobile,
			String qq, String weChat, String photo) {
		User user = new User();
		user.setUser_id(Integer.parseInt(userId));
		user.setName(name);
		user.setMobile(mobile);
		user.setQq(qq);
		user.setWe_chat(weChat);
		user.setPhoto(photo);

		createLoginSession(user);
	}

	public int loginLongTime(){
		Date startday = DateUtil.parseDate(get(LOGIN_TIME), new String[]{"yyyyMMddHHmmss"});
		if(startday == null){
			return 10031;
		}
		Date endday = new Date();
		return DateUtil.getDiffMins(startday, endday);
	}
	/**
	 * Check login method wil check user login status If false it will redirect
	 * user to login page Else reset login time
	 * */
	public void checkLogin() {
		// Check login status
		if (!this.isLoggedIn() || (this.isLoggedIn() && loginLongTime() > 30)) {
			// user is not logged in redirect him to Login Activity
			Intent i = new Intent(_context, LoginActivity.class);
			// Closing all the Activities
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			// Add new Flag to start new Activity
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			// Staring Login Activity
			_context.startActivity(i);
		}else{
			resetLoginTime();
		}

	}

	/**
	 * Get stored session data
	 * */
	public HashMap<String, String> getUserMapDetails() {
		HashMap<String, String> user = new HashMap<String, String>();

		// user id
		user.put(KEY_USER_ID, getInt(KEY_USER_ID) + "");

		// user name
		user.put(KEY_NAME, pref.getString(KEY_NAME, null));

		// user mobile
		user.put(KEY_MOBILE, pref.getString(KEY_MOBILE, null));

		// user qq
		user.put(KEY_QQ, pref.getString(KEY_QQ, null));

		// user we chat
		user.put(KEY_WE_CHAT, pref.getString(KEY_WE_CHAT, null));
		
		user.put(KEY_PHOTO, pref.getString(KEY_PHOTO, null));

		// return user
		return user;
	}

	/**
	 * Get stored session data
	 * */
	public User getUserDetails() {

		User user = new User();
		user.setUser_id(pref.getInt(KEY_USER_ID, 0));
		user.setName(pref.getString(KEY_NAME, null));
		user.setMobile(pref.getString(KEY_MOBILE, null));
		user.setQq(pref.getString(KEY_QQ, null));
		user.setWe_chat(pref.getString(KEY_WE_CHAT, null));
		user.setPhoto(pref.getString(KEY_PHOTO, null));

		// return user
		return user;
	}

	/**
	 * Clear session details
	 * */
	public void logoutUser() {
		// Clearing all data from Shared Preferences
		editor.clear();
		editor.commit();

		// After logout redirect user to Loing Activity
		Intent i = new Intent(_context, LoginActivity.class);
		// Closing all the Activities
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		// Add new Flag to start new Activity
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		// Staring Login Activity
		_context.startActivity(i);
	}

	/**
	 * Quick check for login
	 * 
	 */
	// Get Login State
	public boolean isLoggedIn() {
		return pref.getBoolean(IS_LOGIN, false);
	}
}
