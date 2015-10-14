package com.cjhbuy.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.AssetManager;

import com.cjhbuy.auth.SessionManager;
import com.cjhbuy.bean.CityItem;
import com.cjhbuy.bean.GoodsItem;
import com.cjhbuy.bean.MerchCar;
import com.cjhbuy.bean.MerchDisacount;
import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;

public class AppContext extends Application {
	private static final Logger LOGGER = LoggerFactory.getLogger(AppContext.class);

	private SharedPreferences preferences;
	private List<CityItem> cities;
	private String city;
	private List<GoodsItem> cartGoodLists;
 	
	
	@Override
	public void onCreate() {
		super.onCreate();
		String as;
		cartGoodLists=new ArrayList<GoodsItem>();
		try {
			as = getA();
			AssetsParser parser = new AssetsParser();
			List<CityItem> cities = parser.getCities(as);
			setCities(cities);
		} catch (IOException e) {
			LOGGER.error("转换出错",e);
		}
	}

	public String getA() throws IOException {
		AssetManager assetManager = this.getAssets();
		InputStream is = assetManager.open("city_coordinate.txt");
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length = -1;
		while ((length = is.read(buffer)) != -1) {
			stream.write(buffer, 0, length);
		}
		return stream.toString();
	}

	public SharedPreferences getPreferences() {
		return preferences;
	}

	public void setPreferences(SharedPreferences preferences) {
		this.preferences = preferences;
	}

	public List<CityItem> getCities() {
		return cities;
	}

	public void setCities(List<CityItem> cities) {
		this.cities = cities;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public List<GoodsItem> getCartGoodLists() {
		return cartGoodLists;
	}

	public void setCartGoodLists(List<GoodsItem> cartGoodLists) {
		this.cartGoodLists = cartGoodLists;
	}

	/**
	 * 获取单个商品的优惠金额
	 * @param goodsItem
	 * @return
	 */
	public double getDisacountMoney(GoodsItem goodsItem){
		if(goodsItem == null){
			return 0;
		}
		//计算金额
		List<MerchDisacount> merchDisacounts = goodsItem.getMerchDisacounts();
		if(merchDisacounts != null && !merchDisacounts.isEmpty()){
			MerchDisacount disacount  = merchDisacounts.get(0);
			
			float disacount_money = disacount.getDisacount_money();
			disacount_money = (disacount_money < 0.0f) ? 0.0f : disacount_money;
			
			return disacount_money;
		}
		
		return 0;
	}
	/**
	 * 获取商品的优惠金额
	 * @param goodsItem
	 * @return
	 */
	public double getListDisacountMoney(){
		if(cartGoodLists == null || cartGoodLists.isEmpty()){
			return 0;
		}
		//计算金额
		int length = cartGoodLists.size();
		double price = 0;
		for (int i = 0; i < length; i++) {
			GoodsItem goodsItem = cartGoodLists.get(i);
			
			double discountmoney = getDisacountMoney(goodsItem);
			int sellmount = goodsItem.getSellmount();
			
			discountmoney = discountmoney * sellmount;
			
			price += discountmoney;
		}
		
		return price;
	}
	/**
	 * 获取单个商品的金额，原价减去优惠价
	 * @param goodsItem
	 * @return
	 */
	public double getCalMoney(GoodsItem goodsItem){
		if(goodsItem == null){
			return 0;
		}
		//计算金额
		double price = goodsItem.getPrice();//价格
		double disacount_money = getDisacountMoney(goodsItem);
		
		return price - disacount_money;
	}
	
	/**
	 * 获取购物车的合计金额
	 * @return
	 */
	public double getListCalMoney(){
		if(cartGoodLists == null || cartGoodLists.isEmpty()){
			return 0;
		}
		
		int length = cartGoodLists.size();
		double price = 0;
		for (int i = 0; i < length; i++) {
			GoodsItem goodsItem = cartGoodLists.get(i);
			
			double calMoney = getCalMoney(goodsItem);
			int sellmount = goodsItem.getSellmount();
			
			calMoney = calMoney * sellmount;
			
			price += calMoney;
		}
		
		return price;
	}
	/**
	 * 获取购物车的商品数量
	 * @return
	 */
	public int getListNumber(){
		if(cartGoodLists == null || cartGoodLists.isEmpty()){
			return 0;
		}
		
		int length = cartGoodLists.size();
		int number = 0;
		for (int i = 0; i < length; i++) {
			GoodsItem goodsItem = cartGoodLists.get(i);
			
			int sellmount = goodsItem.getSellmount();
			number += sellmount;
		}
		
		return number;
	}
	/**
	 * 获取购物车的商品数量、所有商品的优惠金额、以及合计金额
	 * 返回一个数组，第一位为商品数量，第二位为优惠金额，第三位为合计金额
	 * @return
	 */
	public double[] getListDisacount(){
		if(cartGoodLists == null || cartGoodLists.isEmpty()){
			return new double[]{0,0,0};
		}
		
		int length = cartGoodLists.size();
		int number = 0;
		double price0 = 0;
		double price1 = 0;
		for (int i = 0; i < length; i++) {
			GoodsItem goodsItem = cartGoodLists.get(i);
			
			//原价格
			double price = goodsItem.getPrice();
			//单个商品的优惠金额
			double disacountMoney = 0;
			List<MerchDisacount> merchDisacounts = goodsItem.getMerchDisacounts();
			if(merchDisacounts != null && !merchDisacounts.isEmpty()){
				MerchDisacount disacount  = merchDisacounts.get(0);
				
				float disacount_money = disacount.getDisacount_money();
				disacountMoney = (disacount_money < 0.0f) ? 0.0f : disacount_money;
			}
			
			//商品数量 
			int sellmount = goodsItem.getSellmount();
			number += sellmount;
			price0 += (disacountMoney * sellmount);//多个商品的优惠金额累加
			price1 += (price - disacountMoney) * sellmount;//多个商品的金额累加
		}
		
		return new double[]{number,price0,price1};
	}
	
	
	/**
	 * 保存购物车中的商品到数据库
	 * @param sessionManager
	 * @param merch_id
	 * @param buy_num
	 * @param oper
	 */
	public void save2MerchCar(SessionManager sessionManager, int merch_id,int buy_num,int oper){
		//增加把购物车中的数据保存到DB中，因此要先检测用户是否登录，如果登录了，则保存，否则不保存
		if(!sessionManager.isLoggedIn()){
			return;
		}
		
		String url = "";
		MerchCar merchCar = new MerchCar();
		int user_id = sessionManager.getUserId();
		if(oper == 0){//新增
			url = HttpUtil.BASE_URL + "/merchcar/addMerchCar.do";
		}else if(oper == 1){//修改
			url = HttpUtil.BASE_URL + "/merchcar/updateMerchCarBy.do";
		}else if(oper == 2){//删除
			url = HttpUtil.BASE_URL + "/merchcar/deleteMerchCarBy.do";
		}
		
		try {
			merchCar.setMerch_id(merch_id);
			merchCar.setBuy_num(buy_num);
			merchCar.setUser_id(user_id);
			
			HttpUtil.postRequest(url, merchCar);
		} catch (Exception e) {
			LOGGER.error(">>> 保存商品信息到购物车失败",e);
		}
		
	}
}
