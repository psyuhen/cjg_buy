package com.cjhbuy.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.cjhbuy.adapter.CommonAdapter;
import com.cjhbuy.adapter.ViewHolder;
import com.cjhbuy.bean.MerchCar;
import com.cjhbuy.utils.CommonsUtil;
import com.cjhbuy.utils.HttpUtil;
import com.cjhbuy.utils.JsonUtil;
import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;

/**
 * 购物车
 * @author ps
 *
 */
public class CartActivity extends BaseActivity {
	private static final Logger LOGGER = LoggerFactory.getLogger(GoodsViewActivity.class);

	private ListView cartListView;
	// 购物车的的商品
	private List<MerchCar> goodsList;

	//下单
	private Button cart_submit_btn;
	private CommonAdapter<MerchCar> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cart);
		initView();
		initData();

	}

	@Override
	public void initView() {
		
		super.initView();
		cartListView = (ListView) findViewById(R.id.cartListView);
		cart_submit_btn = (Button) findViewById(R.id.cart_submit_btn);
		cart_submit_btn.setOnClickListener(this);
	}

	private void initData() {
		
		title.setText("购物车");
		goodsList = new ArrayList<MerchCar>();
		adapter = showAdapter();
		cartListView.setAdapter(adapter);
		
		queryMerchCar();
	}
	
	//查询购物车信息
	private void queryMerchCar(){
		String url = HttpUtil.BASE_URL + "/merchcar/queryMerchCarByUser.do?user_id=";
		
		try {
			String json = HttpUtil.getRequest(url);
			if(json == null){
				CommonsUtil.showLongToast(getApplicationContext(), "查询购物车信息失败");
				return;
			}
			
			List<MerchCar> list = JsonUtil.parse2ListMerchCar(json);
			goodsList.clear();
			goodsList.addAll(list);
			adapter.notifyDataSetChanged();
			
			
		}catch (Exception e) {
			LOGGER.error(">>> 查询商品信息失败",e);
			CommonsUtil.showLongToast(getApplicationContext(), "查询商品信息失败");
		}
	}

	/**
	 * 显示适配数据
	 * 
	 * @return
	 */
	public CommonAdapter<MerchCar> showAdapter() {
		return new CommonAdapter<MerchCar>(CartActivity.this, goodsList,
				R.layout.item_cart_goodslist) {

			@Override
			public void convert(ViewHolder helper, MerchCar item) {
				
				helper.setText(R.id.cart_goods_title, item.getName());
				helper.setText(R.id.cart_goods_price, "￥ " + item.getPrice());
				helper.setImageResource(R.id.cart_goods_imageview, item.getImage());
				helper.setText(R.id.goods_item_stock, item.getBuy_num() + "");
				
				Button goods_minus_btn = helper.getView(R.id.goods_minus_btn);
				Button goods_add_btn = helper.getView(R.id.goods_add_btn);
				goods_minus_btn.setOnClickListener(new ClickHandler(helper));
				goods_add_btn.setOnClickListener(new ClickHandler(helper));
			}
			
		};
	}
	
	//购物车里面的添加或者减少数量
	class ClickHandler implements OnClickListener{
		ViewHolder holder;
		public ClickHandler(ViewHolder holder) {
			this.holder = holder;
		}
		@Override
		public void onClick(View v) {
			EditText goods_item_stock = (EditText)holder.getView(R.id.goods_item_stock);
			String stock = goods_item_stock.getText().toString();
			stock = StringUtils.trimToEmpty(stock);
			stock = "".equals(stock) ? "0" : stock;
			int num = Integer.parseInt(stock);
			
			switch(v.getId()){
			case R.id.goods_minus_btn:
				num --;
				if(num < 0){
					num = 0;
				}
				goods_item_stock.setText(String.valueOf(num));
				break;
			case R.id.goods_add_btn:
				num ++;
				
				goods_item_stock.setText(String.valueOf(num));
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void onClick(View v) {
		
		super.onClick(v);
		switch (v.getId()) {
		case R.id.cart_submit_btn://下单
			startActivity(new Intent(CartActivity.this, MyOrderActivity.class));
			break;

		default:
			break;
		}
	}
}
