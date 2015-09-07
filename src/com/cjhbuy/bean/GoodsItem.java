package com.cjhbuy.bean;

public class GoodsItem {
	private int id;
	private String title;
	private int stock;
	private int sellmount;
	private double price;
	private double weight;
	private String unit;
	private String standard;
	private String img;
	private String tag1;
	private String tag2;
	
	private int image;
	
	private int tempImage;
	
	private boolean checked;

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

	public int getTempImage() {
		return tempImage;
	}

	public void setTempImage(int tempImage) {
		this.tempImage = tempImage;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public GoodsItem(int id, String title, int stock, int sellmount,
			double price, double weight, String unit, String standard,
			String img, String tag1, String tag2,int tempImage) {
		super();
		this.id = id;
		this.title = title;
		this.stock = stock;
		this.sellmount = sellmount;
		this.price = price;
		this.weight = weight;
		this.unit = unit;
		this.standard = standard;
		this.img = img;
		this.tag1 = tag1;
		this.tag2 = tag2;
		this.tempImage=tempImage;
	}

	public int getImage() {
		return image;
	}

	public void setImage(int image) {
		this.image = image;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	
}
