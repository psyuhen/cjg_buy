/**
 * 
 */
package com.cjhbuy.bean;

/**
 * 订单的统计信息
 * @author ps
 *
 */
public class OrderStat {
	private String stat_date;//统计日期
	private String status;//订单状态
	private int order_sell;//订单销量
	private float amount_money;//成交金额
	public String getStat_date() {
		return stat_date;
	}
	public void setStat_date(String stat_date) {
		this.stat_date = stat_date;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getOrder_sell() {
		return order_sell;
	}
	public void setOrder_sell(int order_sell) {
		this.order_sell = order_sell;
	}
	public float getAmount_money() {
		return amount_money;
	}
	public void setAmount_money(float amount_money) {
		this.amount_money = amount_money;
	}
}
