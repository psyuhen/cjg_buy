package com.cjhbuy.bean;

import java.io.Serializable;

public class AddressItem  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int freqa_id;
	private int user_id;
	private String town;
	private String province;//详细地址
	private String city;//详细地址
	private String address;//详细地址
	private String mobile;
	private String user_name;
	private String create_time;
	public int getFreqa_id() {
		return freqa_id;
	}
	public void setFreqa_id(int freqa_id) {
		this.freqa_id = freqa_id;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public String getTown() {
		return town;
	}
	public void setTown(String town) {
		this.town = town;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
}
