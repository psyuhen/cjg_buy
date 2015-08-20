/**
 * 
 */
package com.cjhbuy.bean;

/**
 * 商家访问统计信息
 * @author ps
 *
 */
public class VisitStat {
	private int user_id;//用户
	private int store_id;//商家ID
	private String stat_date;//统计日期
	private int visit_count;//收藏数
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
	public int getVisit_count() {
		return visit_count;
	}
	public void setVisit_count(int visit_count) {
		this.visit_count = visit_count;
	}
	
}
