package com.cjhbuy.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kymjs.aframe.ui.widget.KJListView;
import org.kymjs.aframe.ui.widget.KJListView.KJListViewListener;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.cjhbuy.activity.GoodsActivity;
import com.cjhbuy.activity.HomeActivity;
import com.cjhbuy.activity.R;
import com.cjhbuy.adapter.CommonAdapter;
import com.cjhbuy.adapter.ViewHolder;
import com.cjhbuy.auth.SessionManager;
import com.cjhbuy.bean.SellerItem;
import com.cjhbuy.bean.Store;
import com.cjhbuy.bean.StoreVisitHist;
import com.cjhbuy.utils.AppContext;
import com.cjhbuy.utils.CommonsUtil;
import com.cjhbuy.utils.FileUtil;
import com.cjhbuy.utils.HttpUtil;
import com.cjhbuy.utils.JsonUtil;
import com.cjhbuy.utils.PageUtil;
import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;
/**
 * 首页的按销量、距离、人气类
 * @author pansen
 *
 */
public class HomeSaleFragment extends Fragment {
	private Logger LOGGER = LoggerFactory.getLogger(HomeSaleFragment.class);

	private KJListView kjListView;
	private List<SellerItem> sellerlist;
	private Button mBtn_buy;
	private Context context;
	private int type;
	
	private CommonAdapter<SellerItem> commonAdapter;
	private int start = PageUtil.START;//分页的开始
//	private boolean isLoadFreshList = false;
	
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
		try{
			kjListView = (KJListView) contentView.findViewById(R.id.home_fragment_listview);
			sellerlist = new ArrayList<SellerItem>();
			commonAdapter = showAdapter();
			kjListView.setAdapter(commonAdapter);
			//上下拉刷新
			kjListView.setPullLoadEnable(true);
			kjListView.setKJListViewListener(new KJListViewListener() {
				@Override
				public void onRefresh() {
					HomeSaleFragment.this.start = PageUtil.START;
					queryData(PageUtil.START);
				}

				@Override
				public void onLoadMore() {
					queryData(HomeSaleFragment.this.start);
				}
			});
			
			initData();
		}catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		
		return contentView;
	}
	
	private void loadProgress(boolean isStart){
		if(context instanceof HomeActivity){
			HomeActivity activity = (HomeActivity)context;
			if(isStart){
				activity.startProgressDialog();
			}else{
				activity.stopProgressDialog();
			}
		}
	}

	private void initData() {
		queryData(PageUtil.START);
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
				helper.setText(R.id.item_home_address, "地址:" + StringUtils.trimToEmpty(item.getAddress()));
				helper.setText(R.id.item_home_phone, "电话:" + StringUtils.trimToEmpty(item.getPhone()));

				//点击抄他家按钮，进入商家
				mBtn_buy = helper.getView(R.id.item_home_buy_btn);
				mBtn_buy.setOnClickListener(new MyButtonClick(item));
				
				//显示商家的logo
				Bitmap bitmap = item.getBitmap();
				if(bitmap != null){
					helper.setImageBitmap(R.id.item_home_image, bitmap);
				}else{
					helper.setImageResource(R.id.item_home_image, R.drawable.login_head_icon);
				}
			}
		};
	}

	//抄他家点击事件
	class MyButtonClick implements OnClickListener {
		private SellerItem item;
		public MyButtonClick(SellerItem item) {
			this.item = item;
		}
		@Override
		public void onClick(View v) {
			addStoreVisit(item.getId());
			
			Intent intent = new Intent(getActivity(), GoodsActivity.class);
			intent.putExtra("store_id", item.getId());
			intent.putExtra("store_name", ""+item.getTitle());
			
			if(context instanceof HomeActivity){
				HomeActivity homeActivity = (HomeActivity)context;
				AppContext app = (AppContext)homeActivity.getApplication();
				app.setStore_id(item.getId());
				app.setStore_name(item.getTitle());
			}
			startActivity(intent);
		}
	}
	
	//增加商家访问量
	private class addStoreVisitTask extends AsyncTask<Integer, Void, Void>{
		private int user_id = 0;
		public addStoreVisitTask(int user_id) {
			this.user_id = user_id;
		}
		@Override
		protected Void doInBackground(Integer... params) {
			try {
				int store_id = params[0];
				int user_id = this.user_id;
				
				
				StoreVisitHist storeVisitHist = new StoreVisitHist();
				storeVisitHist.setStore_id(store_id);
				storeVisitHist.setUser_id(user_id);
				
				String url =  HttpUtil.BASE_URL + "/storevisit/addStoreVisitHist.do";
				HttpUtil.postRequest(url,storeVisitHist);
			} catch (Exception e) {
				LOGGER.error(">>> 新增商家访问记录失败", e);
			}
			return null;
		}	
	}
	
	//增加商家访客记录
	private void addStoreVisit(int store_id){
		int user_id = 0;
		if(context instanceof HomeActivity){
			HomeActivity homeActivity = (HomeActivity)context;
			
			SessionManager sessionManager = homeActivity.sessionManager;
			if(sessionManager.isLoggedIn()){
				user_id = sessionManager.getInt(SessionManager.KEY_USER_ID);
			}
		}
		new addStoreVisitTask(user_id).execute(store_id);
	}
	
	//查询商家
	private class queryStoreTask extends AsyncTask<Integer, Void, List<Store>>{
		private int start;
		@Override
		protected List<Store> doInBackground(Integer... params) {
			int start = params[0];
			this.start = start;

			List<Store> list = new ArrayList<Store>();
			try{
				if(HomeSaleFragment.this.type == 0){
					String url = HttpUtil.BASE_URL + "/store/queryStoreBySalesVolumePage.do?start="+start+"&limit="+PageUtil.LIMIT;
					list = queryStoreBy(url);
				}else if(HomeSaleFragment.this.type == 1){
					
				}else if(HomeSaleFragment.this.type == 2){
					String url = HttpUtil.BASE_URL + "/store/queryStoreByVisitCountPage.do?start="+start+"&limit="+PageUtil.LIMIT;
					list = queryStoreBy(url);
				}
				
				if(list == null){
					list = new ArrayList<Store>();
				}
				
				return list;
			}catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
			
			return list;
		}
		
		@Override
		protected void onPostExecute(List<Store> result) {
			super.onPostExecute(result);
			loadProgress(false);
			
			if(result == null){
				result = new ArrayList<Store>();
			}
			
			//默认开始的时候，先清空列表数据
			if(start == PageUtil.START){
				sellerlist.clear();
			}
			
			for (Store store : result) {
				SellerItem sellerItem = new SellerItem();
				sellerItem.setId(store.getStore_id());
				sellerItem.setTitle(store.getName());
				sellerItem.setAddress(store.getAddress());
//				sellerItem.setDistance(100);
				sellerItem.setPhone(store.getPhone());
//				sellerItem.setTempImage(store.getLogo());
				//再从7牛上获取图片
				//先在本地查看，如果有就不从7牛中获取了
				String logoName = store.getLogo();
				
				Bitmap bitmap = FileUtil.getCacheFile(logoName);
				sellerItem.setBitmap(bitmap);
				
				sellerlist.add(sellerItem);
			}
			
			HomeSaleFragment.this.start += PageUtil.LIMIT;//每次改变start的值 
			
			commonAdapter.notifyDataSetChanged();
			kjListView.stopRefreshData();
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
		} catch (Exception e) {
			LOGGER.error(">>> 查询商家信息失败", e);
		}
		return null;
	}
	
	//查询商家数据
	private void queryData(int start){
		loadProgress(true);
		new queryStoreTask().execute(start);
	}
}
