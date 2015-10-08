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
	private int user_id;
	private String create_time;
	
	private int image;
	
	private MerchInfo merch;
	
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
	public MerchInfo getMerch() {
		return merch;
	}
	public void setMerch(MerchInfo merch) {
		this.merch = merch;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	
}