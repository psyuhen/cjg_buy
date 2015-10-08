/**
 * 
 */
package com.cjhbuy.bean;

import java.io.Serializable;

/**
 * @author pansen
 *
 */
public class Coupon implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int coupon_id;
	private int store_id;
	private float coupon_money;
	private float min_order_money;
	private int coupon_total;
	private int coupon_limit;
	private String start_time;
	private String end_time;
	private String create_time;
	private String desc;
	private int has_coupon_num;
	public int getCoupon_id() {
		return coupon_id;
	}
	public void setCoupon_id(int coupon_id) {
		this.coupon_id = coupon_id;
	}
	public float getCoupon_money() {
		return coupon_money;
	}
	public void setCoupon_money(float coupon_money) {
		this.coupon_money = coupon_money;
	}
	public float getMin_order_money() {
		return min_order_money;
	}
	public void setMin_order_money(float min_order_money) {
		this.min_order_money = min_order_money;
	}
	public int getCoupon_total() {
		return coupon_total;
	}
	public void setCoupon_total(int coupon_total) {
		this.coupon_total = coupon_total;
	}
	public int getCoupon_limit() {
		return coupon_limit;
	}
	public void setCoupon_limit(int coupon_limit) {
		this.coupon_limit = coupon_limit;
	}
	public String getStart_time() {
		return start_time;
	}
	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}
	public String getEnd_time() {
		return end_time;
	}
	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public int getHas_coupon_num() {
		return has_coupon_num;
	}
	public void setHas_coupon_num(int has_coupon_num) {
		this.has_coupon_num = has_coupon_num;
	}
	public int getStore_id() {
		return store_id;
	}
	public void setStore_id(int store_id) {
		this.store_id = store_id;
	}
	
}
