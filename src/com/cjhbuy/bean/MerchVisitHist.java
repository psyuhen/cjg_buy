/**
 * 
 */
package com.cjhbuy.bean;

import java.io.Serializable;

/**
 * 商品访问历史
 * @author ps
 *
 */
public class MerchVisitHist  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2914418093930189108L;
	private int visit_id;
	private int merch_id;
	private int user_id;
	private String create_time;
	public int getVisit_id() {
		return visit_id;
	}
	public void setVisit_id(int visit_id) {
		this.visit_id = visit_id;
	}
	public int getMerch_id() {
		return merch_id;
	}
	public void setMerch_id(int merch_id) {
		this.merch_id = merch_id;
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
