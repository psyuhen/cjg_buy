package com.cjhbuy.fragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.kymjs.aframe.ui.widget.KJListView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.cjhbuy.bean.Order;
import com.cjhbuy.bean.OrderItem;
import com.cjhbuy.bean.User;
import com.cjhbuy.utils.CommonsUtil;
import com.cjhbuy.utils.HttpUtil;
import com.cjhbuy.utils.JsonUtil;

public class OrderInFragment extends Fragment {
	public static final String TAG = "OrderInFragment";
	private KJListView kjListView;
	private OrderItemAdapter orderItemAdapter;
	private List<OrderItem> orderlist;
	private Activity activity;
	
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
		
		View contentView = inflater.inflate(R.layout.view_in_order, container,
				false);
		initData();
		activity = getActivity();

		kjListView = (KJListView) contentView
				.findViewById(R.id.in_order_listview);
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

		return contentView;
	}

	public void initData() {
		
		orderlist = new ArrayList<OrderItem>();
		List<Order> orderList = getInOrderInfo();
		if(orderList == null){
			return;
		}
		
		for (int i = 0; i < orderList.size(); i++) {
			Order order = orderList.get(i);
			
			OrderItem orderItem = new OrderItem();
			orderItem.setId(i);
			orderItem.setBuyer_user_id(order.getBuyer_user_id()+"");
			orderItem.setSeller_user_id(order.getSeller_user_id()+"");
			orderItem.setOrdertime(new Date());
			orderItem.setSerialnum(order.getOrder_id());
//			orderItem.setAddress("aaaa");
			orderItem.setGoodtitle(order.getOrderDetails().get(0).getMerch_name());
			orderItem.setBuyer(order.getBuyer_user_name());
			orderItem.setNum(order.getOrderDetails().get(0).getAmount());
			orderItem.setOrdertime(new Date());
			orderItem.setPrice(order.getAmount_money());
			orderlist.add(orderItem);
		}
	}
	
	private List<Order> getInOrderInfo(){
		
		User user = null;
		
		
		
		if(context instanceof OrderActivity){
			OrderActivity activity = (OrderActivity)context;
			if(activity.sessionManager.isLoggedIn()){
				user = activity.sessionManager.getUserDetails();
			}
		}
		/*else if(context instanceof MainActivity){
			MainActivity activity = (MainActivity)context;
			user = activity.sessionManager.getUserDetails();
		}*/
		
		if(user == null){
			CommonsUtil.showLongToast(getActivity(), "请先登录");
			return null;
		}
		
		String url = HttpUtil.BASE_URL + "/order/getInOrderInfo.do";
		Order order = new Order();
		order.setBuyer_user_id(user.getUser_id());
		
		try {
			String listJson = HttpUtil.postRequest(url, order);
			if(listJson == null){
				CommonsUtil.showLongToast(getActivity(), "查询订单列表失败");
				return null;
			}
			
			List<Order> list = JsonUtil.parse2ListOrder(listJson);
			return list;
		} catch (InterruptedException e) {
			Log.e(TAG, "查询订单列表失败", e);
			CommonsUtil.showLongToast(getActivity(), "查询订单列表失败");
		} catch (ExecutionException e) {
			Log.e(TAG, "查询订单列表失败", e);
			CommonsUtil.showLongToast(getActivity(), "查询订单列表失败");
		}
		
		return null;
	}

}
