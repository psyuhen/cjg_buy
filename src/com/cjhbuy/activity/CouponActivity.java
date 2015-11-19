package com.cjhbuy.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.cjhbuy.adapter.CommonAdapter;
import com.cjhbuy.adapter.ViewHolder;
import com.cjhbuy.bean.Coupon;
import com.cjhbuy.bean.CouponItem;
import com.cjhbuy.utils.CommonsUtil;
import com.cjhbuy.utils.DateUtil;
import com.cjhbuy.utils.HttpUtil;
import com.cjhbuy.utils.JsonUtil;
import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;
/**
 * 我的优惠券
 * @author pansen
 *
 */
public class CouponActivity extends BaseActivity {
	private static final Logger LOGGER = LoggerFactory.getLogger(CouponActivity.class);

	private ListView couponListView;
	private List<CouponItem> couponList;
	private CommonAdapter<CouponItem> showAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_coupon);
		initView();
		initData();
	}
	@Override
	public void initView() {
		super.initView();
		title.setText("我的优惠券");
		right_imgbtn.setVisibility(View.GONE);
		couponList = new ArrayList<CouponItem>();
		couponListView = (ListView) findViewById(R.id.couponListView);
		showAdapter = showAdapter();
		couponListView.setAdapter(showAdapter);
	}

	private void initData() {
		if(!sessionManager.isLoggedInAndLongOper()){
			CommonsUtil.showLongToast(getApplicationContext(), "请先登录");
			finish();
			return;
		}
		queryCoupon();
	}
	
	//查询优惠券
	private void queryCoupon(){
		int user_id = sessionManager.getUserId();
		
		String url = HttpUtil.BASE_URL + "/coupon/queryByUserId.do?user_id="+user_id;
		
		try {
			String listJson = HttpUtil.getRequest(url);
			if(listJson == null){
				return;
			}
			
			List<Coupon> list = JsonUtil.parse2ListCoupon(listJson);
			
			int length = list.size();
			couponList.clear();
			for (int i = 0; i < length; i++) {
				Coupon coupon = list.get(i);
				CouponItem couponItem = new CouponItem();
//				couponItem.setSerialnum("D0387677DD7");//TODO 暂时没确定要不要呢
				couponItem.setRange(StringUtils.trimToEmpty(coupon.getDesc()));//优惠描述吧
				couponItem.setPrice(coupon.getCoupon_money());
				couponItem.setStartTime(DateUtil.parseDate(coupon.getStart_time(), new String[]{"yyyyMMddHHmmss"}));
				couponItem.setEndTime(DateUtil.parseDate(coupon.getStart_time(), new String[]{"yyyyMMddHHmmss"}));
				couponList.add(couponItem);
			}
			showAdapter.notifyDataSetChanged();
		} catch (Exception e) {
			LOGGER.error("查询优惠券信息失败", e);
			CommonsUtil.showLongToast(getApplicationContext(), "查询优惠券信息失败");
		}
	
	}

	/**
	 * 显示适配数据
	 * 
	 * @return
	 */
	public CommonAdapter<CouponItem> showAdapter() {
		return new CommonAdapter<CouponItem>(CouponActivity.this, couponList,
				R.layout.item_coupon) {

			@Override
			public void convert(ViewHolder helper, CouponItem item) {
				String startTime = DateUtil.format(item.getStartTime(),"yyyy-MM-dd");
				String endTime = DateUtil.format(item.getEndTime(),"yyyy-MM-dd");
				String dateStr=startTime+"至"+endTime+"有效";
				helper.setText(R.id.item_coupon_price, "￥" + item.getPrice());
				helper.setText(R.id.item_coupon_serailnum, item.getSerialnum());
				helper.setText(R.id.item_coupon_range, "使用范围" + item.getRange());
				helper.setText(R.id.item_coupon_time, dateStr);
			}
		};
	}
}
