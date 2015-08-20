/**
 * 
 */
package com.cjhbuy.bean;

/**
 * 收藏统计信息
 * @author ps
 *
 */
public class FavoriteStat {
	private int user_id;//用户
	private int store_id;//商家ID
	private String stat_date;//统计日期
	private int favorite_count;//收藏数
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public int getStore_id() {
		return store_id;
	}
	public void setStore_id(int store_id) {
		this.store_id = store_id;
	}
	public String getStat_date() {
		return stat_date;
	}
	public void setStat_date(String stat_date) {
		this.stat_date = stat_date;
	}
	public int getFavorite_count() {
		return favorite_count;
	}
	public void setFavorite_count(int favorite_count) {
		this.favorite_count = favorite_count;
	}
	
}
