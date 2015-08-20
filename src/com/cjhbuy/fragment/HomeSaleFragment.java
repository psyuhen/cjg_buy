package com.cjhbuy.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.kymjs.aframe.ui.widget.KJListView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.cjhbuy.activity.GoodsActivity;
import com.cjhbuy.activity.R;
import com.cjhbuy.adapter.CommonAdapter;
import com.cjhbuy.adapter.ViewHolder;
import com.cjhbuy.bean.SellerItem;
import com.cjhbuy.bean.Store;
import com.cjhbuy.utils.CommonsUtil;
import com.cjhbuy.utils.FileUtil;
import com.cjhbuy.utils.HttpUtil;
import com.cjhbuy.utils.JsonUtil;
import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;
//
public class HomeSaleFragment extends Fragment {
	private static final Logger LOGGER = LoggerFactory.getLogger(HomeSaleFragment.class);

	private KJListView kjListView;
	private List<SellerItem> sellerlist;
	private Button mBtn_buy;
	private Context context;
	private int type;
	
	public void setContext(Context context) {
		this.context = context;
	}
	
	public Context getContext() {
		return context;
	}
	public void setType(int type) {
		this.type = type;
	}

	public static HomeSaleFragment newInstance(Context  context, int type) {
		HomeSaleFragment fragment = new HomeSaleFragment();
		fragment.setType(type);
		fragment.setContext(context);
		Bundle bundle = new Bundle();
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View contentView = inflater.inflate(R.layout.view_home_sale, container, false);
		kjListView = (KJListView) contentView.findViewById(R.id.home_fragment_listview);
		sellerlist = new ArrayList<SellerItem>();
		initData();
		kjListView.setAdapter(showAdapter());
		return contentView;
	}

	private void initData() {
		List<Store> list = null;
		if(this.type == 0){
			String url = HttpUtil.BASE_URL + "/store/queryStoreBySalesVolume.do";
			list = queryStoreBy(url);
		}else if(this.type == 1){
			
		}else if(this.type == 2){
			String url = HttpUtil.BASE_URL + "/store/queryStoreByFavoriteCount.do";
			list = queryStoreBy(url);
		}
		if(list == null){
			list = new ArrayList<Store>();
		}
		
		for (Store store : list) {
			SellerItem sellerItem = new SellerItem();
			sellerItem.setId(store.getStore_id());
			sellerItem.setTitle(store.getName());
			sellerItem.setAddress(store.getAddress());
//			sellerItem.setDistance(100);
			sellerItem.setPhone(store.getPhone());
//			sellerItem.setTempImage(store.getLogo());
			//再从7牛上获取图片
			//先在本地查看，如果有就不从7牛中获取了
			String logoName = store.getLogo();
			
			Bitmap bitmap = FileUtil.getCacheFile(logoName);
			sellerItem.setBitmap(bitmap);
			
			sellerlist.add(sellerItem);
		}
	}

	/**
	 * 
	 * @return
	 */
	public CommonAdapter<SellerItem> showAdapter() {

		return new CommonAdapter<SellerItem>(getActivity(), sellerlist,
				R.layout.item_home_list) {

			@Override
			public void convert(ViewHolder helper, SellerItem item) {
				//显示商家的地址和电话
				helper.setText(R.id.item_home_address, "地址:" + item.getAddress());
				helper.setText(R.id.item_home_phone, "电话:" + item.getPhone());

				//点击抄他家按钮，进入商家
				mBtn_buy = helper.getView(R.id.item_home_buy_btn);
				mBtn_buy.setOnClickListener(new MyButtonClick(item));
				
				//显示商家的logo
				helper.setImageBitmap(R.id.item_home_image, item.getBitmap());
			}
		};
	}

	class MyButtonClick implements OnClickListener {
		private SellerItem item;
		public MyButtonClick(SellerItem item) {
			this.item = item;
		}
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(getActivity(), GoodsActivity.class);
			intent.putExtra("store_id", ""+item.getId());
			intent.putExtra("store_name", ""+item.getTitle());
			startActivity(intent);
		}
	}
	//根据销量查询商家
	private List<Store> queryStoreBy(String url){
		//更新商家信息
		String json = null;
		try {
			json = HttpUtil.getRequest(url);
			if(json == null){
				CommonsUtil.showShortToast(getContext(), "查询商家信息失败");
				return null;
			}
			List<Store> list =JsonUtil.parse2ListStore(json);
			return list;
		} catch (InterruptedException e) {
			LOGGER.error(">>> 查询商家信息失败", e);
		} catch (ExecutionException e) {
			LOGGER.error(">>> 查询商家信息失败", e);
		}
		return null;
	}
}
