/**
 * 
 */
package com.cjhbuy.bean;

import java.util.List;

/**
 * 购物车
 * @author ps
 *
 */
public class MerchCar {
	private int car_id;
	private int merch_id;
	private int buy_num;
	private String create_time;
	
	private String name;
	private String desc;
	private float price;
	
	private int image;
	
	private String image_name;
	
	private List<MerchDisacount> merchDisacounts;
	public int getCar_id() {
		return car_id;
	}
	public void setCar_id(int car_id) {
		this.car_id = car_id;
	}
	public int getMerch_id() {
		return merch_id;
	}
	public void setMerch_id(int merch_id) {
		this.merch_id = merch_id;
	}
	public int getBuy_num() {
		return buy_num;
	}
	public void setBuy_num(int buy_num) {
		this.buy_num = buy_num;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public int getImage() {
		return image;
	}
	public void setImage(int image) {
		this.image = image;
	}
	public String getImage_name() {
		return image_name;
	}
	public void setImage_name(String image_name) {
		this.image_name = image_name;
	}
	public List<MerchDisacount> getMerchDisacounts() {
		return merchDisacounts;
	}
	public void setMerchDisacounts(List<MerchDisacount> merchDisacounts) {
		this.merchDisacounts = merchDisacounts;
	}
	
}