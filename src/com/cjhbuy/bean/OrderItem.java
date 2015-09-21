package com.cjhbuy.bean;

import java.util.Date;
import java.util.List;
/**
 * 订单javabean
 * @author zkq
 *
 */
public class OrderItem {
	/**
	 * 编号
	 */
	private int id;
	/**
	 * 订单序列号
	 */
	private String serialnum;
	/**
	 * 买家
	 */
	private String buyer;
	/**
	 * 买家
	 */
	private String buyer_user_id;
	/**
	 * 买家手机号码
	 */
	private String buyer_user_mobile;
	/**
	 * 卖家
	 */
	private String seller;
	/**
	 * 卖家
	 */
	private String seller_user_id;
	/**
	 * 卖家手机号码
	 */
	private String seller_user_moblie;
	/**
	 *订单日期
	 */
	private Date ordertime;
	/**
	 * 地址ַ
	 */
	private String address;
	/**
	 * 图片img
	 */
	private String img;
	/**
	 * 标题名称
	 */
	private String goodtitle;
	/**
	 * 商品数量
	 */
	private float num;
	/**
	 * 商品价格
	 */
	private float price;
	/**
	 * 商品状态
	 */
	private char status;
	/**
	 * 临时本地图片
	 */
	private List<Integer> imgList;
	private int tempImage;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getSerialnum() {
		return serialnum;
	}
	public void setSerialnum(String serialnum) {
		this.serialnum = serialnum;
	}
	public String getBuyer() {
		return buyer;
	}
	public void setBuyer(String buyer) {
		this.buyer = buyer;
	}
	public String getSeller() {
		return seller;
	}
	public void setSeller(String seller) {
		this.seller = seller;
	}
	public Date getOrdertime() {
		return ordertime;
	}
	public void setOrdertime(Date ordertime) {
		this.ordertime = ordertime;
	}
	public String getGoodtitle() {
		return goodtitle;
	}
	public void setGoodtitle(String goodtitle) {
		this.goodtitle = goodtitle;
	}
	
	public float getNum() {
		return num;
	}
	public void setNum(float num) {
		this.num = num;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public String getBuyer_user_id() {
		return buyer_user_id;
	}
	public void setBuyer_user_id(String buyer_user_id) {
		this.buyer_user_id = buyer_user_id;
	}
	public String getSeller_user_id() {
		return seller_user_id;
	}
	public void setSeller_user_id(String seller_user_id) {
		this.seller_user_id = seller_user_id;
	}
	public String getBuyer_user_mobile() {
		return buyer_user_mobile;
	}
	public void setBuyer_user_mobile(String buyer_user_mobile) {
		this.buyer_user_mobile = buyer_user_mobile;
	}
	public String getSeller_user_moblie() {
		return seller_user_moblie;
	}
	public void setSeller_user_moblie(String seller_user_moblie) {
		this.seller_user_moblie = seller_user_moblie;
	}
	public char getStatus() {
		return status;
	}

	public void setStatus(char status) {
		this.status = status;
	}

	public int getTempImage() {
		return tempImage;
	}

	public void setTempImage(int tempImage) {
		this.tempImage = tempImage;
	}

	public List<Integer> getImgList() {
		return imgList;
	}

	public void setImgList(List<Integer> imgList) {
		this.imgList = imgList;
	}
}
