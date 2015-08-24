package com.cjhbuy.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.cjhbuy.adapter.CategoryAdapter;
import com.cjhbuy.adapter.ChildAdapter;
import com.cjhbuy.bean.CategoryItem;
import com.cjhbuy.bean.ClassifyInfo;
import com.cjhbuy.bean.GoodsItem;
import com.cjhbuy.bean.MerchInfo;
import com.cjhbuy.utils.CommonsUtil;
import com.cjhbuy.utils.HttpUtil;
import com.cjhbuy.utils.JsonUtil;
import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;

public class GoodsActivity extends BaseActivity {
	private static final Logger LOGGER = LoggerFactory.getLogger(GoodsActivity.class);

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

	private String store_id;
	private String store_name;
	
	private ImageView goods_cart_image;//购物车
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_goods);
		initView();
		right_imgbtn.setVisibility(View.GONE);
		// right_imgbtn.setVisibility(View.GONE);
		groupListView = (ListView) findViewById(R.id.listView1);
		childListView = (ListView) findViewById(R.id.listView2);
		initData();
		/**
		 * 加载类别
		 */
		groupAdapter = new CategoryAdapter(this, categoryList);
		groupListView.setAdapter(groupAdapter);
		groupListView.setOnItemClickListener(new MyItemClick());
		/**
		 * 加载商品
		 */
		childAdapter = new ChildAdapter(GoodsActivity.this);
		childListView.setAdapter(childAdapter);
		childAdapter.setChildData(allgoodsList);

		childListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				GoodsItem item = (GoodsItem)parent.getItemAtPosition(position);
				
				Intent intent = new Intent();
				intent.setClass(GoodsActivity.this, GoodsViewActivity.class);
				intent.putExtra("merch_id", ""+item.getId());
				startActivity(intent);
			}
		});

		submit_goods_btn = (Button) findViewById(R.id.submit_goods_btn);
		submit_goods_btn.setOnClickListener(this);

	}
	
	@Override
	public void initView() {
		super.initView();
		goods_cart_image=(ImageView) findViewById(R.id.goods_cart_image);
		goods_cart_image.setOnClickListener(this);
	}

	private void initData() {
		Intent intent = getIntent();
		store_id = intent.getStringExtra("store_id");
		store_name = intent.getStringExtra("store_name");
		title.setText(store_name);
		
		
		categoryList = new ArrayList<CategoryItem>();
		queryclassify();

		allgoodsList = new ArrayList<GoodsItem>();
		if(!categoryList.isEmpty()){
			CategoryItem categoryItem = categoryList.get(0);
			queryByClassifyId(categoryItem.getId()+"");
		}
	}

	//选择类型时，
	class MyItemClick implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			CategoryItem item = (CategoryItem)parent.getItemAtPosition(position);
			groupAdapter.setSelectedPosition(position);

			if (childAdapter == null) {
				childAdapter = new ChildAdapter(GoodsActivity.this);
				childListView.setAdapter(childAdapter);
			}

			queryByClassifyId(item.getId()+"");
			childAdapter.setChildData(allgoodsList);
			childAdapter.notifyDataSetChanged();
			groupAdapter.notifyDataSetChanged();
			
		}

	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.submit_goods_btn:
			Intent intent = new Intent();
			intent.setClass(GoodsActivity.this, MyOrderActivity.class);
			startActivity(intent);
			break;
		case R.id.goods_cart_image:
			startActivity(new Intent(GoodsActivity.this, CartActivity.class));
			break;
		default:
			break;
		}
	};

	//查询所有分类
	private void queryclassify() {
		String url = HttpUtil.BASE_URL + "/classify/querybytype.do?classify_type="+1;
		try {
			String jsons = HttpUtil.getRequest(url);
			if(jsons == null){
				CommonsUtil.showShortToast(getApplicationContext(), "查询分类信息失败");
				return;
			}
			List<ClassifyInfo> list = JsonUtil.parse2ListClassifyInfo(jsons);
			if(list == null){
				LOGGER.error(">>> 转换分类列表信息失败");
				return;
			}
			//先清除
			categoryList.clear();
			for (ClassifyInfo classifyInfo : list) {
				CategoryItem categoryItem=new CategoryItem();
				categoryItem.setId(classifyInfo.getClassify_id());
				categoryItem.setNum(classifyInfo.getClassify_num());
				categoryItem.setTitle(classifyInfo.getName());
				
				categoryList.add(categoryItem);
			}
			if(groupAdapter != null){
				groupAdapter.notifyDataSetChanged();
			}
		} catch (InterruptedException e) {
			LOGGER.error(">>> 查询分类列表失败",e);
			CommonsUtil.showShortToast(getApplicationContext(), "查询分类列表失败");
		} catch (ExecutionException e) {
			LOGGER.error(">>> 查询分类列表失败",e);
			CommonsUtil.showShortToast(getApplicationContext(), "查询分类列表失败");
		}
	}
	
	//根据商店ID和类型查询商品信息
	private void queryByClassifyId(String classify_id){
		String url = HttpUtil.BASE_URL + "/merch/queryMerchByMap.do";
		Map<String,String> map = new HashMap<String, String>();
		map.put("store_id", store_id);
		map.put("classify_id", ""+classify_id);
		String json = null;
		try {
			json = HttpUtil.postRequest(url, map);
			if(json != null){
				List<MerchInfo> merchList = JsonUtil.parse2ListMerchInfo(json);
				allgoodsList.clear();
				for (MerchInfo merchInfo : merchList) {
					GoodsItem goodsItem = new GoodsItem();
					goodsItem.setId(merchInfo.getMerch_id());//ID
					goodsItem.setTitle(merchInfo.getName());//名称
					goodsItem.setStock(merchInfo.getIn_stock());//库存
					goodsItem.setSellmount(merchInfo.getSales_volume());//销量
					goodsItem.setPrice(merchInfo.getPrice());//单价
					goodsItem.setStandard(merchInfo.getUnit());//规格
					
					allgoodsList.add(goodsItem);
				}
			}
		} catch (InterruptedException e) {
			LOGGER.error(">>> 查询商品信息失败",e);
			CommonsUtil.showShortToast(getApplicationContext(), "查询商品信息失败");
		} catch (ExecutionException e) {
			LOGGER.error(">>> 查询商品信息失败",e);
			CommonsUtil.showShortToast(getApplicationContext(), "查询商品信息失败");
		}
		
	}
}
