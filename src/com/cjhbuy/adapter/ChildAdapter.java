package com.cjhbuy.adapter;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
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

import com.cjhbuy.activity.GoodsActivity;
import com.cjhbuy.activity.R;
import com.cjhbuy.bean.GoodsItem;
import com.cjhbuy.utils.AppContext;
import com.cjhbuy.utils.CommonsUtil;
import com.cjhbuy.utils.StringUtil;
import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;

/**
 * 子ListView适配器
 * 
 * @author zihao
 * 
 */
public class ChildAdapter extends BaseAdapter {
	private static final Logger LOGGER = LoggerFactory.getLogger(GoodsActivity.class);

	Context mContext;
	// String[] mChildArr;// 子item标题数组
	private List<GoodsItem> goodslist;

	private AppContext app;
	private Activity activity;
	private int good_cart_num;
	private TextView goods_cart_num_text;
	//合计金额
	private TextView goods_calculate;
	private double calculate_money;

	// private IDataLaunch delegate;
	//
	// public IDataLaunch getDelegate() {
	// return delegate;
	// }
	//
	// public void setDelegate(IDataLaunch delegate) {
	// this.delegate = delegate;
	// }

	/**
	 * 构造方法
	 * 
	 * @param context
	 */
	public ChildAdapter(Activity activity, int good_cart_num, TextView goods_cart_num_text) {
		this.activity = activity;
		app = (AppContext) activity.getApplication();
		this.good_cart_num = good_cart_num;
		this.goods_cart_num_text = goods_cart_num_text;
	}

	public TextView getGoods_calculate() {
		return goods_calculate;
	}

	public void setGoods_calculate(TextView goods_calculate) {
		this.goods_calculate = goods_calculate;
	}

	public double getCalculate_money() {
		return calculate_money;
	}

	public void setCalculate_money(double calculate_money) {
		this.calculate_money = calculate_money;
	}

	/**
	 * 为子ListVitem设置要显示的数据
	 * 
	 * @param childArr
	 */
	// public void setChildData(String[] childArr) {
	// this.mChildArr = childArr;
	// }
	public void setChildData(List<GoodsItem> goodslist) {
		this.goodslist = goodslist;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(activity).inflate(R.layout.goods_item_layout, null);
			holder.goodsImage = (ImageView) convertView.findViewById(R.id.goods_item_image);
			holder.priceText = (TextView) convertView.findViewById(R.id.goods_item_price);
			holder.tag1Text = (TextView) convertView.findViewById(R.id.goods_item_tag1);
			holder.tag2Text = (TextView) convertView.findViewById(R.id.goods_item_tag2);
			holder.standardText = (TextView) convertView.findViewById(R.id.goods_item_standard);
			holder.titleText = (TextView) convertView.findViewById(R.id.goods_item_title);
			holder.weightText = (TextView) convertView.findViewById(R.id.goods_item_weight);
			//holder.stockText = (TextView) convertView.findViewById(R.id.goods_item_stock);
			holder.goods_minus_btn = (Button) convertView.findViewById(R.id.goods_minus_btn);
			holder.goods_add_btn = (Button) convertView.findViewById(R.id.goods_add_btn);
			holder.goods_item_stock = (EditText) convertView.findViewById(R.id.goods_item_stock);
			holder.originalpriceText = (TextView) convertView.findViewById(R.id.originalpriceText);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final GoodsItem goodsItem = goodslist.get(position);
		
		Bitmap bitmap = goodsItem.getBitmap();
		if(bitmap != null){
			holder.goodsImage.setImageBitmap(bitmap);//商品图片
		}else{
			holder.goodsImage.setImageResource(R.drawable.login_head_icon);
		}
		holder.weightText.setText(StringUtil.format2string(goodsItem.getWeight()) + StringUtils.trimToEmpty(goodsItem.getUnit()));
		holder.tag1Text.setText(goodsItem.getTag1());
		holder.tag2Text.setText(goodsItem.getTag2());
		
		//计算金额
		double price = app.getCalMoney(goodsItem);//价格
		holder.priceText.setText("￥" + StringUtil.format2string(price));
		holder.originalpriceText.setText("￥" + StringUtil.format2string(goodsItem.getPrice()));
		holder.originalpriceText.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
		
		if (goodsItem.getTag1() == null
				|| "".equals(goodsItem.getTag1())) {
			holder.tag1Text.setVisibility(View.INVISIBLE);
		}
		if (goodsItem.getTag2() == null
				|| "".equals(goodsItem.getTag2())) {
			holder.tag2Text.setVisibility(View.INVISIBLE);
		}
		holder.standardText.setText(goodsItem.getStandard());
		holder.titleText.setText(goodsItem.getTitle());
		//holder.stockText.setText(goodsItem.getStock() + "");
		
		int buyNumer = findInCar(goodsItem.getId());
		holder.goods_item_stock.setText(""+(buyNumer < 0 ? 0 : buyNumer));//默认为0

		holder.goods_add_btn.setOnClickListener(new CarNumClickListener(holder, goodsItem));
		holder.goods_minus_btn.setOnClickListener(new CarNumClickListener(holder, goodsItem));
		return convertView;
	}
	
	/**
	 * 添加或者减少商品数量事件
	 *
	 */
	class CarNumClickListener implements OnClickListener{
		private ViewHolder holder;
		private GoodsItem goodsItem;
		
		public CarNumClickListener(ViewHolder holder,GoodsItem goodsItem) {
			this.holder = holder;
			this.goodsItem = goodsItem;
		}
		@Override
		public void onClick(View v) {
			
			switch (v.getId()) {
			case R.id.goods_add_btn:
				addOne();
				break;
			case R.id.goods_minus_btn:
				subOne();
				break;

			default:
				break;
			}
		}
		
		private void addCartGoods() {
			// 存在于购物车商品的位置
			int cartposition = isExistCart(goodsItem.getId());
			int oper = 1;
			int nowSellmount = 0;
			if (cartposition >= 0) {
				GoodsItem goodsItem2 = app.getCartGoodLists().get(cartposition);
				nowSellmount = goodsItem2.getSellmount();
				goodsItem2.setSellmount(nowSellmount + 1);
			} else {//添加到购物车中
				goodsItem.setSellmount(1);
				app.getCartGoodLists().add(goodsItem);
				oper = 0;
			}
			
			//在购物图标中显示数量
			goods_cart_num_text.setText(app.getListNumber()+"");
			//显示金额
			calculate_money = app.getListCalMoney();
			goods_calculate.setText(StringUtil.format2string(calculate_money));
			
//			save2MerchCar(goodsItem.getId(), nowSellmount + 1, oper);
			app.save2MerchCar(((GoodsActivity)ChildAdapter.this.activity).sessionManager,goodsItem.getId(), nowSellmount + 1, oper);
		}

		//增加一个数量
		private void addOne() {
			changeStockEditTextStatus(1);
			addCartGoods();
		}
		//减少一个数量
		private void subOne(){
			changeStockEditTextStatus(-1);
			minusCartGoods();
		}
		
		private void changeStockEditTextStatus(int one) {
			String itemstockStr = holder.goods_item_stock.getText().toString();
			int itemstock = CommonsUtil.String2Int(itemstockStr);
			itemstock = itemstock + one;
			
			if (itemstock <= 0) {
				itemstock = 0;
			}
			
			holder.goods_item_stock.setText(itemstock + "");
		}
		
		private void minusCartGoods() {
			int cartposition = isExistCart(goodsItem.getId());
			if (cartposition >= 0) {
				GoodsItem goodsItem2 = app.getCartGoodLists().get(cartposition);
				int nowSellmount = goodsItem2.getSellmount();
				
				//在购物车中删除对应的商品
				int oper = 1;
				if(nowSellmount == 1){
					app.getCartGoodLists().remove(goodsItem2);
					//只有在删除商品时才更新在购物图标中显示数量
					good_cart_num --;
					good_cart_num = (good_cart_num < 0) ? 0 : good_cart_num;
					String good_cart_numstr = String.valueOf(good_cart_num);
					goods_cart_num_text.setText(good_cart_numstr);
					oper = 2;
				}else{
					goodsItem2.setSellmount(nowSellmount - 1);
				}
				//在购物图标中显示数量
				goods_cart_num_text.setText(app.getListNumber()+"");
				//显示金额
				calculate_money = app.getListCalMoney();
				goods_calculate.setText(StringUtil.format2string(calculate_money));
				
//				save2MerchCar(goodsItem.getId(), nowSellmount - 1, oper);
				app.save2MerchCar(((GoodsActivity)ChildAdapter.this.activity).sessionManager,goodsItem.getId(), nowSellmount - 1, oper);
			}
		}
	}
	
	/**
	 * 判断在购物车中是否存在
	 * 
	 * @param id
	 * @return
	 */
	private int isExistCart(int id) {
		int currentposition = 0;
		for (GoodsItem goodsItem : app.getCartGoodLists()) {
			if (goodsItem.getId() == id) {
				return currentposition;
			}
			currentposition++;
		}
		return -1;
	}
	
	/**
	 * 在购物中查询购买数量
	 * @param merch_id
	 * @return
	 */
	private int findInCar(int merch_id){
		for (GoodsItem goodsItem : app.getCartGoodLists()) {
			if (goodsItem.getId() == merch_id) {
				return goodsItem.getSellmount();
			}
		}
		return -1;
	}

	static class ViewHolder {
		ImageView goodsImage;
		TextView priceText;
		TextView tag1Text;
		TextView tag2Text;
		TextView weightText;
		TextView standardText;
		TextView titleText;
		TextView stockText;
		TextView originalpriceText;
		Button goods_minus_btn;
		Button goods_add_btn;
		EditText goods_item_stock;
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

	/*class ClickHandler implements OnClickListener{
		ViewHolder holder;
		public ClickHandler(ViewHolder holder) {
			this.holder = holder;
		}
		@Override
		public void onClick(View v) {
			String stock = holder.stockText.getText().toString();
			stock = StringUtils.trimToEmpty(stock);
			stock = "".equals(stock) ? "0" : stock;
			int num = Integer.parseInt(stock);
			
			switch(v.getId()){
			case R.id.goods_minus_btn:
				num --;
				if(num < 0){
					num = 0;
				}
				holder.stockText.setText(String.valueOf(num));
				break;
			case R.id.goods_add_btn:
				num ++;
				
				holder.stockText.setText(String.valueOf(num));
				break;
			default:
				break;
			}
		}
	}*/
}
