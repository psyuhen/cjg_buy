package com.cjhbuy.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.cjhbuy.bean.MerchInfo;
import com.cjhbuy.utils.CommonsUtil;
import com.cjhbuy.utils.HttpUtil;
import com.cjhbuy.utils.JsonUtil;
import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;

public class GoodsViewActivity extends BaseActivity {
	private static final Logger LOGGER = LoggerFactory.getLogger(GoodsViewActivity.class);

	private ListView goodsViewListView;
	private List<Map<String, Object>> maps;
	private LinearLayout goods_view_head_ll;
	LinearLayout testll;
	private TextView goods_view_item_tag1;//	标签1
	private TextView goods_view_item_tag2;//	标签2
	private TextView goods_view_weight;//重量
	private TextView goods_view_standard;//规格
	private TextView goods_view_title;//标题
	private TextView goods_view_price;//价格
	private TextView goods_view_original_price;//原价
	
	private String merch_id;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_goods_view_new);
		initView();
		initData();
	}

	@Override
	public void initView() {
		
		super.initView();
		goods_view_head_ll=(LinearLayout) LayoutInflater.from(GoodsViewActivity.this).inflate(R.layout.headview_goodsview, null);
		goodsViewListView = (ListView) findViewById(R.id.goodsViewListView);
		maps = new ArrayList<Map<String, Object>>();
		goods_view_item_tag1=(TextView) goods_view_head_ll.findViewById(R.id.goods_view_item_tag1);
		goods_view_item_tag2=(TextView) goods_view_head_ll.findViewById(R.id.goods_view_item_tag2);
		goods_view_weight=(TextView) goods_view_head_ll.findViewById(R.id.goods_view_weight);
		goods_view_standard=(TextView) goods_view_head_ll.findViewById(R.id.goods_view_standard);
		goods_view_title=(TextView) goods_view_head_ll.findViewById(R.id.goods_view_title);
		goods_view_price=(TextView) goods_view_head_ll.findViewById(R.id.goods_view_price);
		goods_view_original_price=(TextView) goods_view_head_ll.findViewById(R.id.goods_view_original_price);
		
		Intent intent = getIntent();
		merch_id = intent.getStringExtra("merch_id");
	}

	private void initData() {
		
		title.setText("预览");
		right_imgbtn.setVisibility(View.GONE);
		right_text.setVisibility(View.VISIBLE);
		right_text.setText("刷新");
		goods_view_item_tag1.setText("买一送一");
		goods_view_item_tag2.setText("限时特价");
		goods_view_weight.setText("500g");
		goods_view_standard.setText("箱装");
		goods_view_title.setText("霜降更甜10斤 山东烟台栖霞苹果水果新鲜 红富士苹果");
		goods_view_price.setText("￥10.0");
		goods_view_original_price.setText("￥18.0");
		goods_view_original_price .getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG );
		Map<String, Object> map1 = new HashMap<String, Object>();
		map1.put("image", R.drawable.pg4);
		maps.add(map1);
		Map<String, Object> map2 = new HashMap<String, Object>();
		map2.put("image", R.drawable.pg6);
		maps.add(map2);
		Map<String, Object> map3 = new HashMap<String, Object>();
		map3.put("image", R.drawable.pg4);
		maps.add(map3);
		Map<String, Object> map4 = new HashMap<String, Object>();
		map4.put("image", R.drawable.pg6);
		maps.add(map4);
		Map<String, Object> map5 = new HashMap<String, Object>();
		map5.put("image", R.drawable.pg4);
		maps.add(map5);
		Map<String, Object> map6 = new HashMap<String, Object>();
		map6.put("image", R.drawable.pg6);
		maps.add(map6);
		Map<String, Object> map7 = new HashMap<String, Object>();
		map7.put("image", R.drawable.pg4);
		maps.add(map7);
		SimpleAdapter simpleAdapter = new SimpleAdapter(GoodsViewActivity.this,
				maps, R.layout.item_goods_view, new String[] { "image" },
				new int[] { R.id.item_goods_view_image });
		// goodsViewListView.addHeaderView(goods_view_head_ll);
		goodsViewListView.setAdapter(simpleAdapter);
		goodsViewListView.addHeaderView(goods_view_head_ll);
		// setListViewHeight(goodsViewListView);
	}

	/**
	 * 设置listview高度，防止Listview只显示第一条
	 * 
	 * @param listView
	 */
	public static void setListViewHeight(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(1, 1);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1))
				+ listView.getPaddingTop() + listView.getPaddingBottom();
		listView.setLayoutParams(params);
	}
	
	//查询商品信息
	private void queryMerch(){
		String url = HttpUtil.BASE_URL + "/merch/querybyid.do?merch_id="+merch_id;
		
		try {
			String json = HttpUtil.getRequest(url);
			if(json == null){
				CommonsUtil.showLongToast(getApplicationContext(), "查询商品信息失败");
				return;
			}
			
			MerchInfo merchInfo = JsonUtil.parse2Object(json, MerchInfo.class);
			goods_view_title.setText(merchInfo.getName());
			//goods_view_content.setText(merchInfo.getDesc());
			goods_view_price.setText(String.valueOf(merchInfo.getPrice()));
			String free_shipping = merchInfo.getFree_shipping();
			String free_shipping_name = "包邮";
			if("0".equals(free_shipping)){
				free_shipping_name = "不包邮";
			}else if ("1".equals(free_shipping)){
				free_shipping_name = "包邮";
			}
			//goods_view_postage.setText(free_shipping_name);
			
		}catch (Exception e) {
			LOGGER.error(">>> 查询商品信息失败",e);
			CommonsUtil.showLongToast(getApplicationContext(), "查询商品信息失败");
		}
	}
}
