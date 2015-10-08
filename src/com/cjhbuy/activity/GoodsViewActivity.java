package com.cjhbuy.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.cjhbuy.adapter.CommonAdapter;
import com.cjhbuy.adapter.ViewHolder;
import com.cjhbuy.bean.AddImage;
import com.cjhbuy.bean.Gallery;
import com.cjhbuy.bean.MerchDisacount;
import com.cjhbuy.bean.MerchInfo;
import com.cjhbuy.utils.CommonsUtil;
import com.cjhbuy.utils.FileUtil;
import com.cjhbuy.utils.HttpUtil;
import com.cjhbuy.utils.JsonUtil;
import com.cjhbuy.utils.StringUtil;
import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;
/**
 * 商品浏览
 * @author pansen
 *
 */
public class GoodsViewActivity extends BaseActivity {
	private static final Logger LOGGER = LoggerFactory.getLogger(GoodsViewActivity.class);

	private ListView goodsViewListView;
	private LinearLayout goods_view_head_ll;
	LinearLayout testll;
	private ImageView goods_view_iv01;//图片1
	private TextView goods_view_item_tag1;//	标签1
	private TextView goods_view_item_tag2;//	标签2
	private TextView goods_view_weight;//重量
	private TextView goods_view_standard;//规格
	private TextView goods_view_title;//标题
	private TextView goods_view_price;//价格
	private TextView goods_view_original_price;//原价
	
	private String merch_id;
	private CommonAdapter<AddImage> commonAdapter;
	private List<AddImage> lists;
	
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
		goods_view_iv01=(ImageView) goods_view_head_ll.findViewById(R.id.goods_view_iv01);
		goods_view_item_tag1=(TextView) goods_view_head_ll.findViewById(R.id.goods_view_item_tag1);
		goods_view_item_tag2=(TextView) goods_view_head_ll.findViewById(R.id.goods_view_item_tag2);
		goods_view_weight=(TextView) goods_view_head_ll.findViewById(R.id.goods_view_weight);
		goods_view_standard=(TextView) goods_view_head_ll.findViewById(R.id.goods_view_standard);
		goods_view_title=(TextView) goods_view_head_ll.findViewById(R.id.goods_view_title);
		goods_view_price=(TextView) goods_view_head_ll.findViewById(R.id.goods_view_price);
		goods_view_original_price=(TextView) goods_view_head_ll.findViewById(R.id.goods_view_original_price);
		
		
		lists = new ArrayList<AddImage>();
		commonAdapter = showAdapter();
		goodsViewListView.setAdapter(commonAdapter);
	}

	private void initData() {
		
		title.setText("预览");
		right_imgbtn.setVisibility(View.GONE);
		right_text.setVisibility(View.VISIBLE);
		right_text.setText("刷新");
		
		goods_view_iv01.setImageBitmap(null);
		goods_view_iv01.setBackgroundDrawable(null);
		
		Intent intent = getIntent();
		merch_id = intent.getStringExtra("merch_id");
		
		queryMerch();
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
	
	private CommonAdapter<AddImage> showAdapter() {
		return new CommonAdapter<AddImage>(GoodsViewActivity.this, lists, R.layout.item_goods_view) {
			@Override
			public void convert(ViewHolder helper, AddImage item) {
				helper.setImageBitmap(R.id.item_goods_view_image, item.getBitmap());
			}
		};
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
//			goods_view_content.setText(merchInfo.getDesc());
			goods_view_price.setText(String.valueOf(merchInfo.getPrice()));
			goods_view_weight.setText(StringUtil.format2string(merchInfo.getWeight())+"");
			goods_view_standard.setText(merchInfo.getStandard());
			/*String free_shipping = merchInfo.getFree_shipping();
			String free_shipping_name = "包邮";
			if("0".equals(free_shipping)){
				free_shipping_name = "不包邮";
			}else if ("1".equals(free_shipping)){
				free_shipping_name = "包邮";
			}*/
			//goods_view_postage.setText(free_shipping_name);
			
			//计算金额
			double price = merchInfo.getPrice();//价格
			List<MerchDisacount> merchDisacounts = merchInfo.getMerchDisacounts();
			if(merchDisacounts != null && !merchDisacounts.isEmpty()){
				MerchDisacount disacount  = merchDisacounts.get(0);
				
				float disacount_money = disacount.getDisacount_money();
				disacount_money = (disacount_money < 0.0f) ? 0.0f : disacount_money;
				
				goods_view_price.setText("￥" + StringUtil.format2string(price - disacount_money));
			}else{//两个都是原价
				goods_view_price.setText("￥" + StringUtil.format2string(price));
			}
			goods_view_original_price.setText("￥" + StringUtil.format2string(price));
			goods_view_original_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
			
			
			//查询图片
			url = HttpUtil.BASE_URL + "/gallery/queryByMerchId.do?merch_id="+merch_id;
			json = HttpUtil.getRequest(url);
			if(json != null){
				List<Gallery> galleries = JsonUtil.parse2ListGallery(json);
				if(galleries != null && !galleries.isEmpty()){
					int length = galleries.size();
					for (int i = 0; i < length; i++) {
						Gallery gallery = galleries.get(i);
						String fileName = gallery.getFile_name();
						
						Bitmap photo = FileUtil.getCacheFile(fileName);
						if(i == 0){
							goods_view_iv01.setImageBitmap(photo);
						}else{
							lists.add(new AddImage(gallery.getGallery_id(), fileName, photo));
						}
					}
				}
			}
			commonAdapter.notifyDataSetChanged();
			setListViewHeight(goodsViewListView);
		}catch (Exception e) {
			LOGGER.error(">>> 查询商品信息失败",e);
			CommonsUtil.showLongToast(getApplicationContext(), "查询商品信息失败");
		}
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.right_text:
			lists.clear();
			queryMerch();
			break;
		}
	}
}
