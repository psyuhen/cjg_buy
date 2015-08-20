/**
 * 
 */
package com.cjhbuy.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 订单信息
 * @author ps
 *
 */
public class Order implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2416617126376984743L;
	
	private String order_id;
	/*
	private int merch_id;
	private String name;
	private int amount;
	private String unit;
	private float price;
	*/
	private float amount_money;
	private int buyer_user_id;
	private String buyer_user_name;
	private int seller_user_id;
	private String seller_user_name;
	private String currency_unit;
	private String buyer_name;
//	private String buyer_area;
//	private String buyer_postcode;
	private String buyer_phone;
	private String buyer_mobile;
	private String send_type;
	private String send_no;
	private String send_time;
	private float freight;
	private String invoice_need;
	private String invoice_title;
	private String pay_type;
	private String buyer_pay_time;
	private String trad_time;
	private String trad_finish_time;
	private String update_time;
	private String seller_deliver_time;
	private String buyer_confirm_time;
	private String buyer_del;
	private String seller_del;
	private String buyer_del_time;
	private String seller_del_time;
	private String buyer_score;
	private String seller_score;
	private String status;
	
	//买家地址
	private int town_id;
	private String province_code;//
	private String city_code;//
	private String address;//
	
	//买家评价
	private String buyer_advise;
	
	private List<OrderDetail> orderDetails;//int

	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	public float getAmount_money() {
		return amount_money;
	}

	public void setAmount_money(float amount_money) {
		this.amount_money = amount_money;
	}

	public int getBuyer_user_id() {
		return buyer_user_id;
	}

	public void setBuyer_user_id(int buyer_user_id) {
		this.buyer_user_id = buyer_user_id;
	}

	public String getBuyer_user_name() {
		return buyer_user_name;
	}

	public void setBuyer_user_name(String buyer_user_name) {
		this.buyer_user_name = buyer_user_name;
	}

	public int getSeller_user_id() {
		return seller_user_id;
	}

	public void setSeller_user_id(int seller_user_id) {
		this.seller_user_id = seller_user_id;
	}

	public String getSeller_user_name() {
		return seller_user_name;
	}

	public void setSeller_user_name(String seller_user_name) {
		this.seller_user_name = seller_user_name;
	}

	public String getCurrency_unit() {
		return currency_unit;
	}

	public void setCurrency_unit(String currency_unit) {
		this.currency_unit = currency_unit;
	}

	public String getBuyer_name() {
		return buyer_name;
	}

	public void setBuyer_name(String buyer_name) {
		this.buyer_name = buyer_name;
	}


	public String getBuyer_phone() {
		return buyer_phone;
	}

	public void setBuyer_phone(String buyer_phone) {
		this.buyer_phone = buyer_phone;
	}

	public String getBuyer_mobile() {
		return buyer_mobile;
	}

	public void setBuyer_mobile(String buyer_mobile) {
		this.buyer_mobile = buyer_mobile;
	}

	public String getSend_type() {
		return send_type;
	}

	public void setSend_type(String send_type) {
		this.send_type = send_type;
	}

	public String getSend_no() {
		return send_no;
	}

	public void setSend_no(String send_no) {
		this.send_no = send_no;
	}

	public String getSend_time() {
		return send_time;
	}

	public void setSend_time(String send_time) {
		this.send_time = send_time;
	}

	public float getFreight() {
		return freight;
	}

	public void setFreight(float freight) {
		this.freight = freight;
	}

	public String getInvoice_need() {
		return invoice_need;
	}

	public void setInvoice_need(String invoice_need) {
		this.invoice_need = invoice_need;
	}

	public String getInvoice_title() {
		return invoice_title;
	}

	public void setInvoice_title(String invoice_title) {
		this.invoice_title = invoice_title;
	}

	public String getPay_type() {
		return pay_type;
	}

	public void setPay_type(String pay_type) {
		this.pay_type = pay_type;
	}

	public String getBuyer_pay_time() {
		return buyer_pay_time;
	}

	public void setBuyer_pay_time(String buyer_pay_time) {
		this.buyer_pay_time = buyer_pay_time;
	}

	public String getTrad_time() {
		return trad_time;
	}

	public void setTrad_time(String trad_time) {
		this.trad_time = trad_time;
	}

	public String getTrad_finish_time() {
		return trad_finish_time;
	}

	public void setTrad_finish_time(String trad_finish_time) {
		this.trad_finish_time = trad_finish_time;
	}

	public String getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}

	public String getSeller_deliver_time() {
		return seller_deliver_time;
	}

	public void setSeller_deliver_time(String seller_deliver_time) {
		this.seller_deliver_time = seller_deliver_time;
	}

	public String getBuyer_confirm_time() {
		return buyer_confirm_time;
	}

	public void setBuyer_confirm_time(String buyer_confirm_time) {
		this.buyer_confirm_time = buyer_confirm_time;
	}

	public String getBuyer_del() {
		return buyer_del;
	}

	public void setBuyer_del(String buyer_del) {
		this.buyer_del = buyer_del;
	}

	public String getSeller_del() {
		return seller_del;
	}

	public void setSeller_del(String seller_del) {
		this.seller_del = seller_del;
	}

	public String getBuyer_del_time() {
		return buyer_del_time;
	}

	public void setBuyer_del_time(String buyer_del_time) {
		this.buyer_del_time = buyer_del_time;
	}

	public String getSeller_del_time() {
		return seller_del_time;
	}

	public void setSeller_del_time(String seller_del_time) {
		this.seller_del_time = seller_del_time;
	}

	public String getBuyer_score() {
		return buyer_score;
	}

	public void setBuyer_score(String buyer_score) {
		this.buyer_score = buyer_score;
	}

	public String getSeller_score() {
		return seller_score;
	}

	public void setSeller_score(String seller_score) {
		this.seller_score = seller_score;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public int getTown_id() {
		return town_id;
	}

	public void setTown_id(int town_id) {
		this.town_id = town_id;
	}

	public String getProvince_code() {
		return province_code;
	}

	public void setProvince_code(String province_code) {
		this.province_code = province_code;
	}

	public String getCity_code() {
		return city_code;
	}

	public void setCity_code(String city_code) {
		this.city_code = city_code;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getBuyer_advise() {
		return buyer_advise;
	}

	public void setBuyer_advise(String buyer_advise) {
		this.buyer_advise = buyer_advise;
	}

	public List<OrderDetail> getOrderDetails() {
		return orderDetails;
	}

	public void setOrderDetails(List<OrderDetail> orderDetails) {
		this.orderDetails = orderDetails;
	}
	
	/*
	private List<Integer> merch_ids;//int
	private List<String> merch_names;//String
	private List<Integer> amounts;//int
	private List<String> units;//String
	private List<Float> prices;//float
	*/
	
}
