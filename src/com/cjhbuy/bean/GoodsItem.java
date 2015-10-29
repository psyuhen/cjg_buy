package com.cjhbuy.bean;

import java.util.List;

import android.graphics.Bitmap;

public class GoodsItem {
	private int id; //编号
	private String title; //标题
	private int stock;  //库存
	private int sellmount;  //出售数量
	private double price;   //价格
	private double originalprice;   //原始价格 
	private int image;   //图片
	private double weight;   //重量
	private String unit;   //单位
	private String standard;   //规格
	private String img;  //图片
	private String tag1;   //标签1
	private String tag2;   //标签2
	private boolean choose;   // 是否选中
	
	private String create_time;
	private Bitmap bitmap;
	
	//商品所属商家
	private int store_id;
	private String store_name;
	private List<MerchDisacount> merchDisacounts;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}

	public int getSellmount() {
		return sellmount;
	}

	public void setSellmount(int sellmount) {
		this.sellmount = sellmount;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public String getStandard() {
		return standard;
	}

	public void setStandard(String standard) {
		this.standard = standard;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getTag1() {
		return tag1;
	}

	public void setTag1(String tag1) {
		this.tag1 = tag1;
	}

	public String getTag2() {
		return tag2;
	}

	public void setTag2(String tag2) {
		this.tag2 = tag2;
	}

	public GoodsItem() {
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}


	public void setPrice(double price) {
		this.price = price;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getOriginalprice() {
		return originalprice;
	}

	public void setOriginalprice(double originalprice) {
		this.originalprice = originalprice;
	}

	public int getImage() {
		return image;
	}

	public void setImage(int image) {
		this.image = image;
	}
	
	
	public boolean isChoose() {
		return choose;
	}

	public void setChoose(boolean choose) {
		this.choose = choose;
	}

	public GoodsItem(int id, String title, int stock, int sellmount,double originalprice,
			double price, double weight, String unit, String standard,
			String img, String tag1, String tag2,int image) {
		super();
		this.id = id;
		this.title = title;
		this.stock = stock;
		this.sellmount = sellmount;
		this.originalprice=originalprice;
		this.price = price;
		this.weight = weight;
		this.unit = unit;
		this.standard = standard;
		this.img = img;
		this.tag1 = tag1;
		this.tag2 = tag2;
		this.image=image;
	}
	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public List<MerchDisacount> getMerchDisacounts() {
		return merchDisacounts;
	}

	public void setMerchDisacounts(List<MerchDisacount> merchDisacounts) {
		this.merchDisacounts = merchDisacounts;
	}

	public int getStore_id() {
		return store_id;
	}

	public void setStore_id(int store_id) {
		this.store_id = store_id;
	}

	public String getStore_name() {
		return store_name;
	}

	public void setStore_name(String store_name) {
		this.store_name = store_name;
	}
	
}
