package com.cjhbuy.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.cjhbuy.adapter.MyOrderGoodsAdapter;
import com.cjhbuy.bean.GoodsItem;
import com.cjhbuy.bean.MerchCar;
import com.cjhbuy.bean.MerchDisacount;
import com.cjhbuy.bean.MerchInfo;
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
 * 购物车
 * @author ps
 *
 */
public class CartActivity extends BaseActivity {
	private static final Logger LOGGER = LoggerFactory.getLogger(CartActivity.class);

	private ListView cartListView;

	//下单
	private Button cart_submit_btn;
//	private Button goods_minus_btn;
//	private Button goods_add_btn;
	private AppContext app;
	/* 商品数量 */
	private TextView all_num;
	/* 总金额 */
	private TextView all_money;
	/* 邮费 */
	private TextView all_postage;

	private TextView all_discount_money;
	private int num = 0;
	private double money = 0;
	private double discount_money;

	private MyOrderGoodsAdapter adapter;

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
		all_discount_money = (TextView) findViewById(R.id.all_discount_money);
		cart_submit_btn.setOnClickListener(this);
		all_num = (TextView) findViewById(R.id.all_num);
		all_money = (TextView) findViewById(R.id.all_money);
		all_postage = (TextView) findViewById(R.id.all_postage);
		app = (AppContext) getApplication();
	}

	private void initData() {
		title.setText("我的购物车");
		initMoney();
		all_num.setText("共" + num + "件商品");
		all_money.setText("￥ " + StringUtil.format2string(money));
		all_postage.setText("￥" + CommonsUtil.postage(money));
		all_discount_money.setText("￥" + StringUtil.format2string(discount_money));
		all_discount_money.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
		
		adapter = new MyOrderGoodsAdapter(CartActivity.this,
				app.getCartGoodLists(), all_money, all_num, all_postage,
				all_discount_money);
		cartListView.setAdapter(adapter);
		
		//登录了，查询数据库的购物车
		if(sessionManager.isLoggedIn()){
			queryMerchCar();
		}
	}
	
	//计算购物车里面的金额
	private void initMoney() {
		for (GoodsItem goodsItem : app.getCartGoodLists()) {
			int sellmount = goodsItem.getSellmount();
			double price = goodsItem.getPrice();//原价格
			
			double disacountMoney = 0;
			List<MerchDisacount> merchDisacounts = goodsItem.getMerchDisacounts();
			if(merchDisacounts != null && !merchDisacounts.isEmpty()){
				MerchDisacount disacount  = merchDisacounts.get(0);
				
				float disacount_money = disacount.getDisacount_money();
				disacountMoney = (disacount_money < 0.0f) ? 0.0f : disacount_money;
			}
			
			num = num + sellmount;//购买数量
			discount_money = discount_money + (disacountMoney * sellmount);//优惠金额
			money = money + sellmount * (price - disacountMoney);//实际金额
		}
	}
	
	//查询购物车信息
	private void queryMerchCar(){
		int user_id = sessionManager.getUserId();
		String url = HttpUtil.BASE_URL + "/merchcar/queryMerchCarByUser.do?user_id="+user_id;
		
		try {
			String json = HttpUtil.getRequest(url);
			if(json == null){
				//CommonsUtil.showLongToast(getApplicationContext(), "查询购物车信息失败");
				return;
			}
			
			List<MerchCar> list = JsonUtil.parse2ListMerchCar(json);
			if(list != null){
				int length = list.size();
				List<GoodsItem> goodsList = new ArrayList<GoodsItem>();
				for (int i = 0; i < length; i++) {
					MerchCar merchCar = list.get(i);
					
					GoodsItem goodsItem = new GoodsItem();
					goodsItem.setId(merchCar.getMerch_id());//商品ID
					goodsItem.setSellmount(merchCar.getBuy_num());//购买数量
					
					MerchInfo merchInfo = merchCar.getMerch();
					if(merchInfo != null){
						goodsItem.setTitle(merchInfo.getName());
						goodsItem.setPrice(merchInfo.getPrice());//价格
						
						goodsItem.setUnit(merchInfo.getUnit());//单位
						goodsItem.setStandard(merchInfo.getStandard());//规格
						goodsItem.setWeight(merchInfo.getWeight());
						
						goodsItem.setBitmap(FileUtil.getCacheFile(merchInfo.getImage_name()));//商品图片
						
					}
					
					List<MerchDisacount> merchDisacounts = merchCar.getMerchDisacounts();
					goodsItem.setMerchDisacounts(merchDisacounts);
					
					goodsList.add(goodsItem);
				}
				
				app.getCartGoodLists().clear();
				app.getCartGoodLists().addAll(goodsList);
				initMoney();
			}
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
	/*public CommonAdapter<MerchCar> showAdapter() {
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
	}*/
	
	//购物车里面的添加或者减少数量
	/*class ClickHandler implements OnClickListener{
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
	}*/

	@Override
	public void onClick(View v) {
		
		super.onClick(v);
		switch (v.getId()) {
		case R.id.cart_submit_btn://下单
			if(!sessionManager.isLoggedIn()){
				start2Login();
				return;
			}
			startActivity(new Intent(CartActivity.this, MyOrderActivity.class));
			break;

		default:
			break;
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case Constants.CAR_REQUEST_CODE:
			if(!sessionManager.isLoggedIn()){//如果还没登录，即直接返回
				return;
			}
			startActivity(new Intent(CartActivity.this, MyOrderActivity.class));
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	//跳转到登录页面
	private void start2Login(){
		Intent intent = new Intent(CartActivity.this, LoginActivity.class);
		intent.putExtra("from", "CartActivity");
		startActivityForResult(intent, Constants.CAR_REQUEST_CODE);
	}
}
