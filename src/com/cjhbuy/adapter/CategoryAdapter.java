package com.cjhbuy.adapter;

import java.util.List;

import com.cjhbuy.activity.R;
import com.cjhbuy.activity.R.color;
import com.cjhbuy.bean.CategoryItem;
import com.cjhbuy.common.TopTagTextView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 父ListView适配器
 * 
 * @author zihao
 * 
 */
public class CategoryAdapter extends BaseAdapter {

	Context mContext;// 上下文对象
	private List<CategoryItem> list;
	int mPosition = 0;// 选中的位置

	/**
	 * 构造方法
	 * 
	 * @param context
	 *            // 上下文对象
	 * @param groupArr
	 *            // item标题数组
	 */
	public CategoryAdapter(Context context, List<CategoryItem> list) {
		this.mContext = context;
		this.list = list;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;

		// 初始化布局控件
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.group_item_layout, null);
			holder.groupName = (TextView) convertView
					.findViewById(R.id.group_textView);
			holder.category_item_line = convertView
					.findViewById(R.id.category_item_line);
			holder.group_tag_imageview = (ImageView) convertView
					.findViewById(R.id.group_tag_imageview);
			holder.top_tag_text = (TopTagTextView) convertView
					.findViewById(R.id.top_tag_text);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// 设置控件内容
		holder.groupName.setText(list.get(position).getTitle());
		if (list.get(position).getTag() == null 
				|| "".equals(list.get(position).getTag())) {
			holder.group_tag_imageview.setVisibility(View.GONE);
		} else {
			holder.top_tag_text.setText(list.get(position).getTag());
		}

		if (mPosition == position) {
			holder.groupName.setTextColor(mContext.getResources().getColor(
					R.color.list_item_text_pressed_bg));
			convertView.setBackgroundColor(mContext.getResources().getColor(
					R.color.group_item_pressed_bg));
			holder.category_item_line.setBackgroundColor(color.cyan);
			holder.category_item_line.setVisibility(View.VISIBLE);
		} else {
			holder.groupName.setTextColor(mContext.getResources().getColor(
					android.R.color.black));
			convertView.setBackgroundColor(mContext.getResources().getColor(
					R.color.group_item_bg));

			holder.category_item_line.setVisibility(View.INVISIBLE);
		}
		return convertView;
	}

	/**
	 * 获取item总数
	 */
	@Override
	public int getCount() {
		return list.size();
	}

	/**
	 * 获取某一个Item的内容
	 */
	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	/**
	 * 获取当前item的ID
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	static class ViewHolder {
		/** 父Item名称 **/
		TextView groupName;
		View category_item_line;
		ImageView group_tag_imageview;
		TopTagTextView top_tag_text;
	}

	/**
	 * 设置选择的位置
	 * 
	 * @param position
	 */
	public void setSelectedPosition(int position) {
		this.mPosition = position;
	}

}
