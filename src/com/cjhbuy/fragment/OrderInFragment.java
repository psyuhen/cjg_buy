package com.cjhbuy.fragment;

import java.util.ArrayList;
import java.util.List;

import org.kymjs.aframe.ui.widget.KJListView;
import org.kymjs.aframe.ui.widget.KJListView.KJListViewListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Toast;

import com.cjhbuy.activity.OrderActivity;
import com.cjhbuy.activity.OrderDetailsActivity;
import com.cjhbuy.activity.R;
import com.cjhbuy.adapter.OrderItemAdapter;
import com.cjhbuy.bean.MerchGallery;
import com.cjhbuy.bean.Order;
import com.cjhbuy.bean.OrderDetail;
import com.cjhbuy.bean.OrderItem;
import com.cjhbuy.common.Constants;
import com.cjhbuy.utils.DateUtil;
import com.cjhbuy.utils.FileUtil;
import com.cjhbuy.utils.HttpUtil;
import com.cjhbuy.utils.JsonUtil;
import com.cjhbuy.utils.PageUtil;
import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;

/**
 * 正在进行中的订单
 * @author pansen
 *
 */
public class OrderInFragment extends Fragment {
	private Logger LOGGER = LoggerFactory.getLogger(OrderInFragment.class);

	private KJListView kjListView;
	private OrderItemAdapter orderItemAdapter;
	private List<OrderItem> orderlist;
	private Activity activity;
	
	private int start = PageUtil.START;//分页的开始
	
	private Context context;
	public void setContext(Context context) {
		this.context = context;
	}

	public static OrderInFragment newInstance(Context context) {
		OrderInFragment fragment = new OrderInFragment();
		fragment.setContext(context);
		Bundle bundle = new Bundle();
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle bundle) {
		
		View contentView = inflater.inflate(R.layout.view_in_order, container,false);
		
		try{
			activity = getActivity();

			kjListView = (KJListView) contentView.findViewById(R.id.in_order_listview);
			orderlist = new ArrayList<OrderItem>();
			orderItemAdapter = new OrderItemAdapter(getActivity(), orderlist);
			// initEvent();
			kjListView.setAdapter(orderItemAdapter);
			kjListView.setPullRefreshEnable(true);
			kjListView.setPullLoadEnable(true);
			kjListView.setLongClickable(true);
			kjListView.setSelected(true);
			kjListView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position,
						long id) {
					OrderItem item = (OrderItem)parent.getItemAtPosition(position);
					Intent intent = new Intent(getActivity(), OrderDetailsActivity.class);
					intent.putExtra("order_id", item.getSerialnum());
					startActivity(intent);
				}
			});
			kjListView.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					
					Toast.makeText(activity, "", Toast.LENGTH_SHORT).show();
					return false;
				}
			});
			//上下拉刷新
			kjListView.setPullLoadEnable(true);
			kjListView.setKJListViewListener(new KJListViewListener() {
				@Override
				public void onRefresh() {
					OrderInFragment.this.start = PageUtil.START;
					getInOrderInfo(PageUtil.START);
				}

				@Override
				public void onLoadMore() {
					getInOrderInfo(OrderInFragment.this.start);
				}
			});

			initData();
		}catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return contentView;
	}

	public void initData() {
		getInOrderInfo(PageUtil.START);
	}
	
	
	//查询订单
	private class queryOrderInfoTask extends AsyncTask<Integer, Void, List<OrderItem>>{
		private int user_id;
		private int start;
		public queryOrderInfoTask(int user_id) {
			this.user_id = user_id;
		}
		@Override
		protected List<OrderItem> doInBackground(Integer... params) {
			int start = params[0];
			this.start = start;
			int user_id = this.user_id;
			
			List<OrderItem> tmpList = new ArrayList<OrderItem>();
			
			if(user_id == 0){
				return tmpList;
			}
			
			String url = HttpUtil.BASE_URL + "/order/getInOrderInfoByPage.do?start="+start+"&limit="+PageUtil.LIMIT;
			Order order1 = new Order();
			order1.setBuyer_user_id(user_id);
			
			try {
				String listJson = HttpUtil.postRequest(url, order1);
				if(listJson == null){
					return tmpList;
				}
				
				List<Order> list = JsonUtil.parse2ListOrder(listJson);
				if(list == null){
					LOGGER.warn("转换订单列表信息失败");
					return tmpList;
				}
				
				int length = list.size();
				if(length == 0){
					return tmpList;
				}
				
				for (int i = 0; i < length; i++) {
					Order order = list.get(i);
					List<OrderDetail> orderDetails = order.getOrderDetails();
					
					OrderItem orderItem = new OrderItem();
					orderItem.setId(i);
					orderItem.setBuyer_user_id(order.getBuyer_user_id()+"");
					orderItem.setSeller_user_id(order.getSeller_user_id()+"");
					orderItem.setSeller(order.getSeller_user_name());
					orderItem.setSerialnum(order.getOrder_id());
//					orderItem.setAddress("aaaa");
					orderItem.setBuyer(order.getBuyer_user_name());
					orderItem.setOrdertime(DateUtil.parseDate(order.getTrad_time(), new String[]{"yyyyMMddHHmmss"}));//订单时间
					orderItem.setPrice(order.getAmount_money());
					
					String status = order.getStatus();
					if(status != null && !"".equals(status)){
						char[] statusChars = status.toCharArray();
						orderItem.setStatus(statusChars[0]);
					}
					
					if(orderDetails != null && !orderDetails.isEmpty()){
						OrderDetail orderDetail = orderDetails.get(0);
						
						orderItem.setNum(orderDetail.getAmount());//默认显示第一个商品的数量
						orderItem.setGoodtitle(orderDetail.getMerch_name());//默认为第一个商品的名称
						//默认显示第一个商品的图片
						List<MerchGallery> merchGallerys = orderDetail.getMerchGallerys();
						if(merchGallerys != null && !merchGallerys.isEmpty()){
							int gallerySize = merchGallerys.size();
							List<Bitmap> bitmapList = new ArrayList<Bitmap>();
							for (int j = 0; j < gallerySize; j++) {
								Bitmap cacheFile = FileUtil.getCacheFile(merchGallerys.get(j).getName());
								if(cacheFile == null){
									continue;
								}
								bitmapList.add(cacheFile);
							}
							orderItem.setBitmapList(bitmapList);
						}
					}
					
					tmpList.add(orderItem);
				}
				
			} catch (Exception e) {
				LOGGER.error("查询订单列表失败", e);
			}
			
			return tmpList;
		}
		
		@Override
		protected void onPostExecute(List<OrderItem> result) {
			super.onPostExecute(result);
			loadProgress(false);
			
			//默认开始的时候，先清空列表数据
			if(start == PageUtil.START){
				orderlist.clear();
			}
			
			orderlist.addAll(result);
			
			OrderInFragment.this.start += PageUtil.LIMIT;//每次改变start的值 
			orderItemAdapter.notifyDataSetChanged();
			kjListView.stopRefreshData();
		}
		
	}
	
	private void loadProgress(boolean isStart){
		if(context instanceof OrderActivity){
			OrderActivity activity = (OrderActivity)context;
			if(isStart){
				activity.startProgressDialog();
			}else{
				activity.stopProgressDialog();
			}
		}
	}
	
	//查询订单
	private void getInOrderInfo(int start){
		loadProgress(true);
		int user_id = 0;
		if(context instanceof OrderActivity){
			OrderActivity activity = (OrderActivity)context;
			if(activity.sessionManager.isLoggedIn()){
				user_id = activity.sessionManager.getUserId();
			}
		}
		new queryOrderInfoTask(user_id).execute(start);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case Constants.ORDERIN_REQUEST_CODE:
			getInOrderInfo(PageUtil.START);
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	//跳转到登录页面
	/*private void start2Login(){
		Intent intent = new Intent(getActivity(), LoginActivity.class);
		intent.putExtra("from", "OrderInFragment");
		startActivityForResult(intent, Constants.ORDERIN_REQUEST_CODE);
	}*/
}
