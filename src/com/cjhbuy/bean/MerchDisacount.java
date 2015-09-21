/**
 * 
 */
package com.cjhbuy.bean;

import java.io.Serializable;

/**
 * 商品优惠信息
 * @author ps
 *
 */
public class MerchDisacount implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4112888621761248667L;

	private int disacount_id;
	private int merch_id;
	private float disacount_money;
	private float disacount;
	private String disacount_date;
	private String effective_date;
	private String create_time;
	public int getDisacount_id() {
		return disacount_id;
	}
	public void setDisacount_id(int disacount_id) {
		this.disacount_id = disacount_id;
	}
	public int getMerch_id() {
		return merch_id;
	}
	public void setMerch_id(int merch_id) {
		this.merch_id = merch_id;
	}
	public float getDisacount_money() {
		return disacount_money;
	}
	public void setDisacount_money(float disacount_money) {
		this.disacount_money = disacount_money;
	}
	public float getDisacount() {
		return disacount;
	}
	public void setDisacount(float disacount) {
		this.disacount = disacount;
	}
	public String getDisacount_date() {
		return disacount_date;
	}
	public void setDisacount_date(String disacount_date) {
		this.disacount_date = disacount_date;
	}
	public String getEffective_date() {
		return effective_date;
	}
	public void setEffective_date(String effective_date) {
		this.effective_date = effective_date;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	
	
}
