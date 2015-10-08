package com.cjhbuy.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 订单详情表
 * @author wangxl
 *
 */
public class OrderDetail implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1478737471794677275L;
	/*
	private String orderId;
	private int merchId;
	private String merchName;
	private int amount;
	private String unit;
	private float price;
	*/
	
	private String order_id;
	private int merch_id;
	private String merch_name;
	private int amount;
	private String unit;
	private float price;
	
	private List<MerchGallery> merchGallerys;//商品图片

	public String getOrder_id() {
		return order_id;
	}
	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}
	public int getMerch_id() {
		return merch_id;
	}
	public void setMerch_id(int merch_id) {
		this.merch_id = merch_id;
	}
	public String getMerch_name() {
		return merch_name;
	}
	public void setMerch_name(String merch_name) {
		this.merch_name = merch_name;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public List<MerchGallery> getMerchGallerys() {
		return merchGallerys;
	}
	public void setMerchGallerys(List<MerchGallery> merchGallerys) {
		this.merchGallerys = merchGallerys;
	}
	
}
