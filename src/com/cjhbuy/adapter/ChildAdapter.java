package com.cjhbuy.adapter;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cjhbuy.activity.R;
import com.cjhbuy.bean.GoodsItem;
import com.cjhbuy.utils.StringUtil;

/**
 * 子ListView适配器
 * 
 * @author zihao
 * 
 */
public class ChildAdapter extends BaseAdapter {

	Context mContext;
	// String[] mChildArr;// 子item标题数组
	private List<GoodsItem> goodslist;

	/**
	 * 构造方法
	 * 
	 * @param context
	 */
	public ChildAdapter(Context context) {
		mContext = context;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.goods_item_layout, null);
			holder.goodsImage = (ImageView) convertView.findViewById(R.id.goods_item_image);
			holder.priceText = (TextView) convertView.findViewById(R.id.goods_item_price);
			holder.tag1Text = (TextView) convertView.findViewById(R.id.goods_item_tag1);
			holder.tag2Text = (TextView) convertView.findViewById(R.id.goods_item_tag2);
			holder.standardText = (TextView) convertView.findViewById(R.id.goods_item_standard);
			holder.titleText = (TextView) convertView.findViewById(R.id.goods_item_title);
			holder.weightText = (TextView) convertView.findViewById(R.id.goods_item_weight);
			holder.stockText = (TextView) convertView.findViewById(R.id.goods_item_stock);
			holder.goods_minus_btn = (Button) convertView.findViewById(R.id.goods_minus_btn);
			holder.goods_minus_btn.setOnClickListener(new ClickHandler(holder));
			
			holder.goods_add_btn = (Button) convertView.findViewById(R.id.goods_add_btn);
			holder.goods_add_btn.setOnClickListener(new ClickHandler(holder));
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		GoodsItem goodsItem = goodslist.get(position);
		
		holder.goodsImage.setImageResource(goodsItem.getTempImage());
		holder.weightText.setText(StringUtil.format(goodsItem.getWeight())+ StringUtils.trimToEmpty(goodsItem.getUnit()));
		holder.priceText.setText("￥" + StringUtil.format(goodsItem.getPrice()));
		holder.tag1Text.setText(goodsItem.getTag1());
		holder.tag2Text.setText(goodsItem.getTag2());
		
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
		holder.stockText.setText(goodsItem.getStock() + "");
		return convertView;
	}

	static class ViewHolder {
		ImageView goodsImage;
		TextView priceText;
		TextView tag1Text;
		TextView tag2Text;
		TextView weightText;
		TextView standardText;
		TextView titleText;
		TextView stockText;//购买的数量
		Button goods_minus_btn;//减少
		Button goods_add_btn;//增加
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

	class ClickHandler implements OnClickListener{
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
	}
}
