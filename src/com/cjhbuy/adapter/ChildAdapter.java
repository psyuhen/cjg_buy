package com.cjhbuy.adapter;

import java.util.List;
import com.cjhbuy.activity.R;
import com.cjhbuy.bean.GoodsItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.goods_item_layout, null);
			holder.goodsImage = (ImageView) convertView
					.findViewById(R.id.goods_item_image);
			holder.priceText = (TextView) convertView
					.findViewById(R.id.goods_item_price);
			holder.tag1Text = (TextView) convertView
					.findViewById(R.id.goods_item_tag1);
			holder.tag2Text = (TextView) convertView
					.findViewById(R.id.goods_item_tag2);
			holder.standardText = (TextView) convertView
					.findViewById(R.id.goods_item_standard);
			holder.titleText = (TextView) convertView
					.findViewById(R.id.goods_item_title);
			holder.weightText = (TextView) convertView
					.findViewById(R.id.goods_item_weight);
			holder.stockText = (TextView) convertView
					.findViewById(R.id.goods_item_stock);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.goodsImage.setImageResource(goodslist.get(position)
				.getTempImage());
		holder.weightText.setText(goodslist.get(position).getWeight()
				+ goodslist.get(position).getUnit());
		holder.priceText.setText("￥" + goodslist.get(position).getPrice());
		holder.tag1Text.setText(goodslist.get(position).getTag1());
		holder.tag2Text.setText(goodslist.get(position).getTag2());
		if (goodslist.get(position).getTag2() == null
				|| "".equals(goodslist.get(position).getTag2())) {
			holder.tag2Text.setVisibility(View.INVISIBLE);
		}
		holder.standardText.setText(goodslist.get(position).getStandard());
		holder.titleText.setText(goodslist.get(position).getTitle());
		holder.stockText.setText(goodslist.get(position).getStock() + "");
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
		TextView stockText;
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
