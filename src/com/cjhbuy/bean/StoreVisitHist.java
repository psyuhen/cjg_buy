/**
 * 
 */
package com.cjhbuy.bean;

import java.io.Serializable;

/**
 * 商家访问历史
 * @author ps
 *
 */
public class StoreVisitHist  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2914418093930189108L;
	private int visit_id;
	private int store_id;
	private int user_id;
	private String create_time;
	public int getVisit_id() {
		return visit_id;
	}
	public void setVisit_id(int visit_id) {
		this.visit_id = visit_id;
	}
	public int getStore_id() {
		return store_id;
	}
	public void setStore_id(int store_id) {
		this.store_id = store_id;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	
}
