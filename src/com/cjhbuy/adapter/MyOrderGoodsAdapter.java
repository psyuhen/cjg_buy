package com.cjhbuy.adapter;

import java.util.List;

import android.app.Activity;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cjhbuy.activity.CartActivity;
import com.cjhbuy.activity.MyOrderActivity;
import com.cjhbuy.activity.R;
import com.cjhbuy.auth.SessionManager;
import com.cjhbuy.bean.GoodsItem;
import com.cjhbuy.bean.MerchDisacount;
import com.cjhbuy.utils.AppContext;
import com.cjhbuy.utils.CommonsUtil;
import com.cjhbuy.utils.StringUtil;
import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;

public class MyOrderGoodsAdapter extends BaseAdapter {
	private static final Logger LOGGER = LoggerFactory.getLogger(MyOrderGoodsAdapter.class);

	private List<GoodsItem> goodslist;

	private Activity activity;
	private AppContext app;
	/* 邮费 */
	private int all_num;
	private double all_money;
	private double discount_money;

	private TextView all_money_text;
	private TextView all_num_text;
	private TextView postage_text;
	private TextView discount_money_text;

	public double getAll_money() {
		return all_money;
	}
	/**
	 * 构造方法
	 * 
	 * @param context
	 */
	public MyOrderGoodsAdapter(Activity activity, List<GoodsItem> goodslist,
			TextView all_money_text, TextView all_num_text,
			TextView postage_text, TextView discount_money_text) {
		this.activity = activity;
		this.goodslist = goodslist;
		app = (AppContext) activity.getApplication();
		this.all_money_text = all_money_text;
		this.all_num_text = all_num_text;
		this.postage_text = postage_text;
		this.discount_money_text = discount_money_text;
		//计算购物车中的商品价格
		for (GoodsItem goodsItem : goodslist) {
			int sellmount = goodsItem.getSellmount();
			double price = goodsItem.getPrice();//原价格
			
			double disacountMoney = 0;
			List<MerchDisacount> merchDisacounts = goodsItem.getMerchDisacounts();
			if(merchDisacounts != null && !merchDisacounts.isEmpty()){
				MerchDisacount disacount  = merchDisacounts.get(0);
				
				float disacount_money = disacount.getDisacount_money();
				disacountMoney = (disacount_money < 0.0f) ? 0.0f : disacount_money;
			}
			
			all_num = all_num + sellmount;//购买数量
			discount_money = discount_money + (disacountMoney * sellmount);//优惠金额
			all_money = all_money + sellmount * (price - disacountMoney);//实际金额
		}
	}

	public void setChildData(List<GoodsItem> goodslist) {
		this.goodslist = goodslist;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(activity).inflate(R.layout.item_cart_goodslist, null);
			holder.cart_goods_imageview = (ImageView) convertView.findViewById(R.id.cart_goods_imageview);
			holder.cart_goods_price = (TextView) convertView.findViewById(R.id.cart_goods_price);
			holder.cart_goods_title = (TextView) convertView.findViewById(R.id.cart_goods_title);
			holder.goods_item_stock = (TextView) convertView.findViewById(R.id.goods_item_stock);
			holder.cart_goods_original_price = (TextView) convertView.findViewById(R.id.cart_goods_original_price);
			holder.goods_add_btn = (Button) convertView.findViewById(R.id.goods_add_btn);
			holder.goods_minus_btn = (Button) convertView.findViewById(R.id.goods_minus_btn);
			holder.goodstockedit = (EditText) convertView.findViewById(R.id.goods_item_stock);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final GoodsItem item = goodslist.get(position);
		holder.cart_goods_title.setText(item.getTitle());
		holder.goods_item_stock.setText("" + item.getSellmount());
//		holder.cart_goods_imageview.setImageResource(item.getImage());
		holder.cart_goods_imageview.setImageBitmap(item.getBitmap());
		
		double price = app.getCalMoney(item);//价格
		double disacountMoney = app.getDisacountMoney(item);
		holder.cart_goods_price.setText("￥" + StringUtil.format2string(price));
		holder.cart_goods_original_price.setText("￥" + StringUtil.format2string(item.getPrice()));
		holder.cart_goods_original_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
		
		//购物车中的数量增减
		holder.goods_minus_btn.setOnClickListener(new CarNumClickListener(holder, item, disacountMoney));
		holder.goods_add_btn.setOnClickListener(new CarNumClickListener(holder, item, disacountMoney));
		return convertView;
	}
	
	/**
	 * 添加或者减少商品数量事件
	 *
	 */
	class CarNumClickListener implements OnClickListener{
		private ViewHolder holder;
		private GoodsItem item;
		private double disacountMoney;//优惠金额吧
		
		public CarNumClickListener(ViewHolder holder,GoodsItem item, double disacountMoney) {
			this.holder = holder;
			this.item = item;
			this.disacountMoney = disacountMoney;
		}
		@Override
		public void onClick(View v) {
			double originalPrice  = item.getPrice();//这个默认是原价的
			double price = originalPrice - disacountMoney;//优惠后的价格
			int goodstock = CommonsUtil.String2Int(holder.goodstockedit.getText().toString());//商品数量
			int oper = 1;
			
			SessionManager sessionManager = null;
			if(MyOrderGoodsAdapter.this.activity instanceof CartActivity){
				sessionManager = ((CartActivity)MyOrderGoodsAdapter.this.activity).sessionManager;
			}else if(MyOrderGoodsAdapter.this.activity instanceof MyOrderActivity){
				sessionManager = ((MyOrderActivity)MyOrderGoodsAdapter.this.activity).sessionManager;
			}
			
			
			
			switch (v.getId()) {
			case R.id.goods_add_btn:
				holder.goodstockedit.setText(goodstock + 1 + "");
				item.setSellmount(goodstock + 1);
				changeNumPrice(price, originalPrice, 1);
				if(goodstock == 0){
					oper = 0;
				}
				
				app.save2MerchCar(sessionManager,item.getId(), goodstock + 1, oper);
				break;
			case R.id.goods_minus_btn:
				if (goodstock == 1) {
					//TODO 虽然在此删除了商品，但在GoodsActivity中无法更新购物车数量，只有重新打开时才更新
					app.getCartGoodLists().remove(item);
					notifyDataSetChanged();
					oper = 2;
				} else {
					item.setSellmount(goodstock - 1);
				}
				holder.goodstockedit.setText(goodstock - 1 + "");
				changeNumPrice(-price, -originalPrice, -1);
				app.save2MerchCar(sessionManager,item.getId(), goodstock + 1, oper);
				break;

			default:
				break;
			}
		}
	}

	/**
	 * 
	 * @param price 优惠价格（也即实际金额）
	 * @param originalprice 原价格
	 * @param num 购买数量
	 */
	private void changeNumPrice(double price, double originalprice, int num) {
		all_money = all_money + price ;//这个为实际金额哟
		discount_money = discount_money + originalprice - price;
		all_num = all_num + num;
		all_num_text.setText("共" + all_num + "件商品");
		all_money_text.setText("￥ " + StringUtil.format2string(all_money));
		discount_money_text.setText("￥" + StringUtil.format2string(discount_money));
		discount_money_text.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
		postage_text.setText("￥ " + CommonsUtil.postage(all_money));
	}

	private class ViewHolder {
		ImageView cart_goods_imageview;//图片
		TextView cart_goods_title;//标题
		TextView cart_goods_price;//单价
		TextView goods_item_stock;//库库存
		Button goods_add_btn;//添加按钮
		Button goods_minus_btn;//减少按钮
		EditText goodstockedit;//数量
		TextView cart_goods_original_price;//原价
	}

	/**
	 * 获取item总数
	 */
	@Override
	public int getCount() {
		if (goodslist == null) {
			return 0;
		}
		return goodslist.size();
	}

	/**
	 * 获取某一个Item的内容
	 */
	@Override
	public Object getItem(int position) {
		return goodslist.get(position);
	}

	/**
	 * 获取当前item的ID
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}
	
}
