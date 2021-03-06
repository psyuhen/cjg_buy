package com.cjhbuy.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cjhbuy.adapter.CategoryAdapter;
import com.cjhbuy.adapter.ChildAdapter;
import com.cjhbuy.bean.CategoryItem;
import com.cjhbuy.bean.ClassifyInfo;
import com.cjhbuy.bean.GoodsItem;
import com.cjhbuy.bean.MerchCar;
import com.cjhbuy.bean.MerchDisacount;
import com.cjhbuy.bean.MerchInfo;
import com.cjhbuy.bean.MerchVisitHist;
import com.cjhbuy.common.Constants;
import com.cjhbuy.utils.AppContext;
import com.cjhbuy.utils.CommonsUtil;
import com.cjhbuy.utils.FileUtil;
import com.cjhbuy.utils.HttpUtil;
import com.cjhbuy.utils.JsonUtil;
import com.cjhbuy.utils.StringUtil;
import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;
/**
 * 商家商品
 * @author ps
 *
 */
public class GoodsActivity extends BaseActivity {
	private Logger LOGGER = LoggerFactory.getLogger(GoodsActivity.class);

	private List<CategoryItem> categoryList;
	private List<GoodsItem> allgoodsList;
	ListView groupListView = null;
	ListView childListView = null;
	CategoryAdapter groupAdapter = null;
	ChildAdapter childAdapter = null;
	TranslateAnimation animation;// 出现的动画效果
	public static int screen_width = 0;
	public static int screen_height = 0;
	private Button submit_goods_btn;//结算按钮

	//商家ID和商家名称
	private int store_id;
	private String store_name;
	
	//合计金额
	private TextView goods_calculate;
	private double calculate_money;
	
	private ImageView goods_cart_image;//购物车图标
	//购物车的商品数量
	private TextView goods_cart_num_text;
	private int good_cart_num;
	
	private AppContext app;
	
	private boolean isAddToMerchCar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try{
			setContentView(R.layout.activity_goods);
			initView();
			initData();
		}catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	@Override
	public void initView() {
		super.initView();
		right_imgbtn.setVisibility(View.GONE);
		groupListView = (ListView) findViewById(R.id.listView1);
		childListView = (ListView) findViewById(R.id.listView2);
		goods_cart_image=(ImageView) findViewById(R.id.goods_cart_image);
		goods_cart_image.setOnClickListener(this);
		goods_cart_num_text = (TextView) findViewById(R.id.goods_cart_num_text);
		goods_calculate = (TextView) findViewById(R.id.goods_calculate);
		/**
		 * 加载类别
		 */
		categoryList = new ArrayList<CategoryItem>();
		groupAdapter = new CategoryAdapter(this, categoryList);
		groupListView.setAdapter(groupAdapter);
		groupListView.setOnItemClickListener(new MyItemClick());
		/**
		 * 加载商品
		 */
		allgoodsList = new ArrayList<GoodsItem>();
		childAdapter = new ChildAdapter(GoodsActivity.this, good_cart_num, goods_cart_num_text);
		childAdapter.setCalculate_money(calculate_money);
		childAdapter.setGoods_calculate(goods_calculate);
		childListView.setAdapter(childAdapter);
		childAdapter.setChildData(allgoodsList);

		childListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				GoodsItem item = (GoodsItem)parent.getItemAtPosition(position);
				
				addMerchVisit(item.getId());
				
				/* 先隐藏 商品查看  */
				/*Intent intent = new Intent();
				intent.setClass(GoodsActivity.this, GoodsViewActivity.class);
				intent.putExtra("merch_id", ""+item.getId());
				startActivity(intent);*/
			}
		});

		//结算
		submit_goods_btn = (Button) findViewById(R.id.submit_goods_btn);
		submit_goods_btn.setOnClickListener(this);
		
		
		app = (AppContext) getApplication();
	}

	private void initData() {
		Intent intent = getIntent();
		store_id = intent.getIntExtra("store_id",0);
		store_name = intent.getStringExtra("store_name");
		title.setText(store_name);
		
		queryclassify();
	}
	/**
	 * 当activity可见时会回调此方法
	 */
	@Override
	protected void onResume() {
		super.onResume();
		
		//登录了，查询数据库的购物车
		if(sessionManager.isLoggedInAndLongOper()){
			queryMerchCar();
		}
	}
	
	//查询购物车的异步操作
	private class queryMerchCarTask extends AsyncTask<Void, Void, List<GoodsItem>>{
		@Override
		protected List<GoodsItem> doInBackground(Void... params) {
			int user_id = sessionManager.getUserId();
			String url = HttpUtil.BASE_URL + "/merchcar/queryMerchCarByUser.do?user_id="+user_id;
			
			List<GoodsItem> goodsList = new ArrayList<GoodsItem>();
			try {
				String json = HttpUtil.getRequest(url);
				if(json == null){
					return null;
				}
				
				List<MerchCar> list = JsonUtil.parse2ListMerchCar(json);
				if(list != null){
					int length = list.size();
					for (int i = 0; i < length; i++) {
						MerchCar merchCar = list.get(i);
						
						GoodsItem goodsItem = new GoodsItem();
						goodsItem.setStore_name(goodsItem.getStore_name());
						
						goodsItem.setId(merchCar.getMerch_id());//商品ID
						goodsItem.setSellmount(merchCar.getBuy_num());//购买数量
						
						MerchInfo merchInfo = merchCar.getMerch();
						if(merchInfo != null){
							goodsItem.setStore_id(merchInfo.getStore_id());
							goodsItem.setTitle(merchInfo.getName());
							goodsItem.setPrice(merchInfo.getPrice());//价格
							
							goodsItem.setUnit(merchInfo.getUnit());//单位
							goodsItem.setStandard(merchInfo.getStandard());//规格
							goodsItem.setWeight(merchInfo.getWeight());//重量
							
							goodsItem.setBitmap(FileUtil.getCacheFile(merchInfo.getImage_name()));//商品图片
							
						}
						
						List<MerchDisacount> merchDisacounts = merchCar.getMerchDisacounts();
						goodsItem.setMerchDisacounts(merchDisacounts);
						
						goodsList.add(goodsItem);
					}
				}
			}catch (Exception e) {
				LOGGER.error(">>> 查询商品信息失败",e);
				CommonsUtil.showLongToast(getApplicationContext(), "查询商品信息失败");
			}
			return goodsList;
		}
		
		@Override
		protected void onPostExecute(List<GoodsItem> result) {
			super.onPostExecute(result);
			if(result == null){
				return;
			}
			app.getCartGoodLists().clear();//清空不太好吧,改为不清空咯
			app.getCartGoodLists().addAll(result);
			//购物图标中显示数量
			good_cart_num = app.getListNumber();
			goods_cart_num_text.setText(String.valueOf(good_cart_num));
			
			//合计金额
			calculate_money = app.getListCalMoney();
			goods_calculate.setText(StringUtil.format2string(calculate_money));
		}
	}
	
	//查询购物车信息
	private void queryMerchCar(){
		new queryMerchCarTask().execute();
	}

	//选择类型时，
	class MyItemClick implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			CategoryItem item = (CategoryItem)parent.getItemAtPosition(position);
			groupAdapter.setSelectedPosition(position);

			if (childAdapter == null) {
				childAdapter = new ChildAdapter(GoodsActivity.this, good_cart_num, goods_cart_num_text);
				childListView.setAdapter(childAdapter);
			}
			queryByClassifyId(item.getId());
		}

	}
	
	//购物车的异步操作
	private class doMerchCarTask extends AsyncTask<Integer, Void, String>{
		private List<MerchCar> merchCars;
		public doMerchCarTask(List<MerchCar> merchCars) {
			this.merchCars = merchCars;
		}
		@Override
		protected String doInBackground(Integer... params) {
			String url = HttpUtil.BASE_URL + "/merchcar/batchAddMerchCar.do";
			
			try {
				String postRequest = HttpUtil.postRequest(url, merchCars);
				return postRequest;
			} catch (Exception e) {
				LOGGER.error(">>> 保存商品信息到购物车失败",e);
			}
			return null;
		}
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			stopProgressDialog();
		}
	}
	
	//如果还没登录时添加的，那么就先保存到购物车先
	private void addToMerchCar(){
		List<GoodsItem> cartGoodLists = app.getCartGoodLists();
		if(cartGoodLists.isEmpty()){
			return;
		}
		
		int user_id = sessionManager.getUserId();
		List<MerchCar> merchCars = new ArrayList<MerchCar>();
		for (GoodsItem goodsItem : cartGoodLists) {
			MerchCar merchCar = new MerchCar();
			merchCar.setBuy_num(goodsItem.getSellmount());//购买数量
			merchCar.setMerch_id(goodsItem.getId());
			merchCar.setUser_id(user_id);
			merchCars.add(merchCar);
		}
		
		startProgressDialog();
		new doMerchCarTask(merchCars).execute();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case Constants.GOODS_REQUEST_CODE:
			if(!sessionManager.isLoggedInAndLongOper()){
				return;
			}
			if(isAddToMerchCar){
				isAddToMerchCar = false;
				addToMerchCar();
			}
			start2MyOrderActivity();
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.submit_goods_btn:
			if(!sessionManager.isLoggedInAndLongOper()){
				isAddToMerchCar = true;
				Intent intent = new Intent(GoodsActivity.this, LoginActivity.class);
				intent.putExtra("from", "GoodsActivity");
//				startActivity(intent);
				startActivityForResult(intent, Constants.GOODS_REQUEST_CODE);
				return;
			}else{
				sessionManager.resetLoginTime();
			}
			start2MyOrderActivity();
			
			break;
		case R.id.goods_cart_image:
			Intent intent = new Intent(GoodsActivity.this, CartActivity.class);
			startActivity(intent);//购物车
			break;
		default:
			break;
		}
	}
	
	//跳转到结算页面
	private void start2MyOrderActivity(){
		Intent intent = new Intent();
		intent.putExtra("store_id", store_id);
		intent.putExtra("store_name", store_name);
		intent.setClass(GoodsActivity.this, MyOrderActivity.class);//结算
		startActivity(intent);
	}

	//查询分类的异步操作
	private class queryClassifyTask extends AsyncTask<Void, Void, List<CategoryItem>>{
		@Override
		protected List<CategoryItem> doInBackground(Void... params) {
			String url = HttpUtil.BASE_URL + "/classify/queryClassifyByStoreId.do?store_id="+store_id;
			List<CategoryItem> categoryList = new ArrayList<CategoryItem>();
			try {
				String jsons = HttpUtil.getRequest(url);
				if(jsons == null){
					return categoryList;
				}
				List<ClassifyInfo> list = JsonUtil.parse2ListClassifyInfo(jsons);
				if(list == null){
					LOGGER.error(">>> 转换分类列表信息失败");
					return categoryList;
				}
				
				for (ClassifyInfo classifyInfo : list) {
					CategoryItem categoryItem=new CategoryItem();
					categoryItem.setId(classifyInfo.getClassify_id());
					categoryItem.setNum(classifyInfo.getClassify_num());
					categoryItem.setTitle(classifyInfo.getName());
					
					categoryList.add(categoryItem);
				}
			} catch (Exception e) {
				LOGGER.error(">>> 查询分类列表失败",e);
			}
			return categoryList;
		}
		@Override
		protected void onPostExecute(List<CategoryItem> result) {
			super.onPostExecute(result);
			stopProgressDialog();
			
			categoryList.clear();
			categoryList.addAll(result);
			
			if(groupAdapter != null){
				groupAdapter.notifyDataSetChanged();
			}
			
			if(!categoryList.isEmpty()){//默认查询出第一个分类的商品信息
				CategoryItem categoryItem = categoryList.get(0);
				queryByClassifyId(categoryItem.getId());
			}
		}
	}
	
	//查询所有商品分类
	private void queryclassify() {
		startProgressDialog();
		new queryClassifyTask().execute();
	}
	
	//查询根据分类Id查询商品的异步操作
	private class queryMerchByIdTask extends AsyncTask<Integer, Void, List<GoodsItem>>{
		@Override
		protected List<GoodsItem> doInBackground(Integer ... params) {
			int classify_id = params[0];
			
			String url = HttpUtil.BASE_URL + "/merch/queryby.do";
			MerchInfo info = new MerchInfo();
			info.setStore_id(store_id);
			info.setClassify_id(classify_id);
			info.setOut_published("0");
			String json = null;
			List<GoodsItem> goodsList = new ArrayList<GoodsItem>();
			try {
				json = HttpUtil.postRequest(url, info);
				if(json != null){
					List<MerchInfo> merchList = JsonUtil.parse2ListMerchInfo(json);
					for (MerchInfo merchInfo : merchList) {
						GoodsItem goodsItem = new GoodsItem();
						goodsItem.setStore_id(merchInfo.getStore_id());
						goodsItem.setStore_name(merchInfo.getStore_name());
						
						goodsItem.setId(merchInfo.getMerch_id());//ID
						goodsItem.setTitle(merchInfo.getName());//名称
						goodsItem.setStock(merchInfo.getIn_stock());//库存
						goodsItem.setSellmount(merchInfo.getSales_volume());//销量
						goodsItem.setPrice(merchInfo.getPrice());//价格
						goodsItem.setUnit(merchInfo.getUnit());//单位
						goodsItem.setStandard(merchInfo.getStandard());//规格
						goodsItem.setWeight(merchInfo.getWeight());//重量
						
						goodsItem.setBitmap(FileUtil.getCacheFile(merchInfo.getImage_name()));//商品图片
						goodsItem.setMerchDisacounts(merchInfo.getMerchDisacounts());//商品优惠信息
						goodsList.add(goodsItem);
					}
				}
			} catch (Exception e) {
				LOGGER.error(">>> 查询商品信息失败",e);
			}
			return goodsList;
		}
		@Override
		protected void onPostExecute(List<GoodsItem> result) {
			super.onPostExecute(result);
			stopProgressDialog();
			
			allgoodsList.clear();
			allgoodsList.addAll(result);
			
			childAdapter.setChildData(allgoodsList);
			childAdapter.notifyDataSetChanged();
			groupAdapter.notifyDataSetChanged();
		}
	}
	
	//根据商店ID和类型查询商品信息
	private void queryByClassifyId(int classify_id){
		startProgressDialog();
		new queryMerchByIdTask().execute(classify_id);
	}
	
	private class addMerchVisitTask extends AsyncTask<Integer, Void, Void>{
		@Override
		protected Void doInBackground(Integer... params) {
			try {
				int merch_id = params[0];
				int user_id = 0;
				if(sessionManager.isLoggedInAndLongOper()){
					user_id = sessionManager.getUserId();
				}
				
				MerchVisitHist merchVisitHist = new MerchVisitHist();
				merchVisitHist.setMerch_id(merch_id);
				merchVisitHist.setUser_id(user_id);
				
				String url =  HttpUtil.BASE_URL + "/merchvisit/addMerchVisitHist.do";
				HttpUtil.postRequest(url,merchVisitHist);
			} catch (Exception e) {
				LOGGER.error(">>> 新增商品访问记录失败", e);
			}
			return null;
		}	
	}
	//增加商品访问量
	private void addMerchVisit(int merch_id){
		new addMerchVisitTask().execute(merch_id);
	}
}
