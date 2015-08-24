package com.cjhbuy.activity;

import java.util.ArrayList;
import java.util.List;

import com.cjhbuy.adapter.CommonAdapter;
import com.cjhbuy.adapter.ViewHolder;
import com.cjhbuy.bean.GoodsItem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

public class CartActivity extends BaseActivity {
	private ListView cartListView;
	// 购物车的的商品
	private List<GoodsItem> goodsList;

	private Button cart_submit_btn;

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
		goodsList = new ArrayList<GoodsItem>();
	}

	private void initData() {
		
		title.setText("购物车");
		goodsList = new ArrayList<GoodsItem>();
		for (int i = 0; i < 10; i++) {
			GoodsItem goodsItem = new GoodsItem();
			if (i % 4 == 0) {
				goodsItem.setTitle("丹麦进口 Kjeldsens 蓝罐 曲奇 礼盒 908g");
				goodsItem.setPrice(109);
				goodsItem.setImage(R.drawable.snack1);
				goodsItem.setSellmount(2);
			} else if (i % 4 == 1) {
				goodsItem.setTitle("烟台红富士苹果新鲜水果");
				goodsItem.setImage(R.drawable.fruit1);
				goodsItem.setPrice(109);
				goodsItem.setStock(3);
				goodsItem.setSellmount(1);
			} else if (i % 4 == 2) {
				goodsItem.setTitle("浙江水蜜桃黄桃约4.5斤装");
				goodsItem.setPrice(109);
				goodsItem.setImage(R.drawable.fruit4);
				goodsItem.setStock(2);
				goodsItem.setSellmount(4);
			} else {
				goodsItem.setTitle("越南进口零食品 丰灵tipo面包干");
				goodsItem.setPrice(109);
				goodsItem.setImage(R.drawable.snack3);
				goodsItem.setStock(1);
				goodsItem.setSellmount(3);
			}

			goodsList.add(goodsItem);
		}
		cartListView.setAdapter(showAdapter());
	}

	/**
	 * 显示适配数据
	 * 
	 * @return
	 */
	public CommonAdapter<GoodsItem> showAdapter() {
		return new CommonAdapter<GoodsItem>(CartActivity.this, goodsList,
				R.layout.item_cart_goodslist) {

			@Override
			public void convert(ViewHolder helper, GoodsItem item) {
				
				helper.setText(R.id.cart_goods_title, item.getTitle());
				helper.setText(R.id.cart_goods_price, "￥ " + item.getPrice());
				helper.setImageResource(R.id.cart_goods_imageview,
						item.getImage());
				helper.setText(R.id.goods_item_stock, item.getSellmount() + "");
			}
		};
	}

	@Override
	public void onClick(View v) {
		
		super.onClick(v);
		switch (v.getId()) {
		case R.id.cart_submit_btn:
			startActivity(new Intent(CartActivity.this, MyOrderActivity.class));
			break;

		default:
			break;
		}
	}
}
