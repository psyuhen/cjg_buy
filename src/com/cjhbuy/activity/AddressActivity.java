package com.cjhbuy.activity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang.StringUtils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.cjhbuy.adapter.CommonAdapter;
import com.cjhbuy.adapter.ViewHolder;
import com.cjhbuy.bean.AddressItem;
import com.cjhbuy.bean.CityModel;
import com.cjhbuy.bean.DistrictModel;
import com.cjhbuy.bean.ProvinceModel;
import com.cjhbuy.common.Constants;
import com.cjhbuy.service.XmlParserHandler;
import com.cjhbuy.utils.CommonsUtil;
import com.cjhbuy.utils.HttpUtil;
import com.cjhbuy.utils.JsonUtil;
import com.cjhbuy.widget.OnWheelChangedListener;
import com.cjhbuy.widget.WheelView;
import com.cjhbuy.widght.adapter.ArrayWheelAdapter;
import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;

/**
 * 地址管理
 * @author pansen
 *
 */
public class AddressActivity extends BaseActivity implements OnWheelChangedListener {
	private Logger LOGGER = LoggerFactory.getLogger(AddressActivity.class);
	// 所在省
	protected String[] mProvinceDatas;
	// key - 省 value - 市
	protected Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
	// key - 市 values - 区
	protected Map<String, String[]> mDistrictDatasMap = new HashMap<String, String[]>();
	// 区 values - 邮编
	protected Map<String, String> mZipcodeDatasMap = new HashMap<String, String>();
	// 当前省的名称
	protected String mCurrentProviceName;
	// 当前市的名称
	protected String mCurrentCityName;
	// 当前区的名称
	protected String mCurrentDistrictName = "";
	// 当前区的邮政编码
	protected String mCurrentZipCode = "";
	// 地址的列表
	private ListView address_listview;
	// 地址适配器
	private List<AddressItem> addressList;
	// 新加地址按钮
	private Button new_address_btn;
	// 新建地址的dialog
	private AlertDialog AddAddressDialog;
	// 地址提交按钮
	private Button submit_btn;
	// 地址编辑按钮
	private Button item_address_edit;

	
	//自定义城市控件
	private WheelView mViewProvince;
	private WheelView mViewCity;
	private WheelView mViewDistrict;
	

	private AddressItem deleteAddress;
	private CommonAdapter<AddressItem> showAdapter = null;
	
	//取消添加和修改按钮
	private ImageButton add_address_btn;
	
	private String from = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address);
		initView();
		initData();
	}

	@Override
	public void initView() {
		
		super.initView();
		title.setText("收货地址");
		right_imgbtn.setVisibility(View.GONE);
		new_address_btn = (Button) findViewById(R.id.new_address_btn);
		new_address_btn.setOnClickListener(this);
		address_listview = (ListView) findViewById(R.id.address_listview);
		address_listview.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent,
					View view, int position, long id) {
				
				AddressItem item = (AddressItem)parent.getItemAtPosition(position);
				deleteAddress = item;
				
				AlertDialog isExit = new AlertDialog.Builder(AddressActivity.this).create();
				// 设置对话框标题
				isExit.setTitle("删除当前地址");
				// 设置对话框消息
				isExit.setMessage("确定要删除吗");
				// 添加选择按钮并注册监听
				isExit.setButton(DialogInterface.BUTTON_POSITIVE, "确定", listener);
				isExit.setButton(DialogInterface.BUTTON_NEGATIVE,"取消", listener);
				// 显示对话框
				isExit.show();
				return false;
			}
		});
		//点击选择
		address_listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if("myorder".equals(from)){//返回给订单
					AddressItem item = (AddressItem)parent.getItemAtPosition(position);
					
					Intent returnIntent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putSerializable("address", item);
					returnIntent.putExtras(bundle);
					setResult(RESULT_OK,returnIntent);
					finish();
				}
			}
		});
		
		Intent intent = getIntent();
		from = intent.getStringExtra("from");
		
		//如果没登录要先登录
		boolean isLogin = sessionManager.isLoggedInAndLongOper();
		if(!isLogin){
			/*Intent intent1 = new Intent();
			intent1.setClass(AddressActivity.this, LoginActivity.class);
			intent1.putExtra("from", "AddressActivity");
			startActivityForResult(intent1, Constants.ADDRESS_REQUEST_CODE);*/
			CommonsUtil.showLongToast(getApplicationContext(), "请先登录");
			finish();
			return;
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case Constants.ADDRESS_REQUEST_CODE:
			if(data != null){
				
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void initData() {
		addressList = new ArrayList<AddressItem>();
		showAdapter = showAdapter();
		address_listview.setAdapter(showAdapter);
		
		queryAddress();
	}
	
	//查询常用地址哟
	private void queryAddress(){
		int user_id = sessionManager.getUserId();
		
		String url = HttpUtil.BASE_URL + "/freqa/query.do?user_id="+user_id;
		try {
			String json = HttpUtil.getRequest(url);
			if(json == null){
				CommonsUtil.showLongToast(getApplicationContext(), "查询常用地址失败");
				return;
			}
			List<AddressItem> list = JsonUtil.parse2ListAddressItem(json);
			addressList.clear();
			addressList.addAll(list);
			showAdapter.notifyDataSetChanged();
		} catch (Exception e) {
			LOGGER.error("查询常用地址失败", e);
			CommonsUtil.showLongToast(getApplicationContext(), "查询常用地址失败");
		}
	}
	
	
	/** 监听对话框里面的button点击事件 */
	DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:// "确认"按钮退出程序
				if(deleteAddress != null){//删除常用地址
					delAddress(deleteAddress);
				}
				break;
			case DialogInterface.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
				deleteAddress = null;
				break;
			default:
				break;
			}
		}
	};
	
	//删除常用地址
	private void delAddress(AddressItem tmpAddress){
		String url = HttpUtil.BASE_URL + "/freqa/delete.do?freqa_id="+tmpAddress.getFreqa_id();
		try {
			String json = HttpUtil.getRequest(url);
			if(json == null){
				CommonsUtil.showLongToast(getApplicationContext(), "删除地址失败");
				return;
			}
			queryAddress();
		} catch (Exception e) {
			LOGGER.error("删除地址失败", e);
			CommonsUtil.showLongToast(getApplicationContext(), "删除地址失败");
		}
	}

	/**
	 * 显示适配数据
	 * 
	 * @return
	 */
	public CommonAdapter<AddressItem> showAdapter() {
		return new CommonAdapter<AddressItem>(AddressActivity.this,
				addressList, R.layout.item_address) {

			@Override
			public void convert(ViewHolder helper, AddressItem item) {
				
				helper.setText(R.id.item_address_name, item.getUser_name());
				helper.setText(R.id.item_address_details, StringUtils.trimToEmpty(item.getProvince())
						+ " " + StringUtils.trimToEmpty(item.getCity())
						+ " " + StringUtils.trimToEmpty(item.getTown())
						+ " " + item.getAddress());
				helper.setText(R.id.item_address_tel, item.getMobile());
				item_address_edit = helper.getView(R.id.item_address_edit);
				item_address_edit.setTag("MODIFY");
				item_address_edit.setOnClickListener(new MyButtonClick(item));
			}
		};
	}

	class MyButtonClick implements OnClickListener {
		
		private AddressItem tmpAddress = null;
		public MyButtonClick(AddressItem tmpAddress) {
			this.tmpAddress = tmpAddress;
		}
		
		@Override
		public void onClick(View v) {
			String mOpt = v.getTag().toString();
			if (mOpt.equals("MODIFY")) {
				AddAddressDialog = new AlertDialog.Builder(AddressActivity.this).create();
				AddAddressDialog.show();
				AddAddressDialog.getWindow().setContentView(R.layout.dialog_add_address);
				AddAddressDialog.getWindow().clearFlags(
								WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
										| WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
				AddAddressDialog.getWindow().setSoftInputMode(
						WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
				
				final EditText add_address_address = (EditText) AddAddressDialog.findViewById(R.id.add_address_address);
				final EditText add_address_tel = (EditText) AddAddressDialog.findViewById(R.id.add_address_tel);
				final EditText add_address_name = (EditText) AddAddressDialog.findViewById(R.id.add_address_name);
				
				add_address_address.setText(tmpAddress.getAddress());
				add_address_tel.setText(tmpAddress.getMobile());
				add_address_name.setText(tmpAddress.getUser_name());
				TextView add_address_title_text = (TextView) AddAddressDialog.findViewById(R.id.add_address_title_text);
				add_address_title_text.setText("修改地址");
				
				setUpViews();
				setUpListener();
				setUpData();
				
				//根据省来查询
				int currentIndex = findIndexBy(mProvinceDatas,tmpAddress.getProvince());
				mViewProvince.setCurrentItem(currentIndex);
				
				//根据省份和城市查找对应的index
				currentIndex = findIndexBy(mCitisDatasMap, tmpAddress.getProvince(), tmpAddress.getCity());
				mViewCity.setCurrentItem(currentIndex);
				
				//根据城市和区查找对应的index
				currentIndex = findIndexBy(mDistrictDatasMap, tmpAddress.getCity(), tmpAddress.getTown());
				mViewDistrict.setCurrentItem(currentIndex);
				
				add_address_btn=(ImageButton) AddAddressDialog.findViewById(R.id.add_address_btn);
				add_address_btn.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						AddAddressDialog.dismiss();
					}
				});
				
				submit_btn = (Button) AddAddressDialog.findViewById(R.id.submit_btn);
				submit_btn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						tmpAddress.setAddress(add_address_address.getText().toString());
						tmpAddress.setUser_name(add_address_name.getText().toString());
						tmpAddress.setMobile(add_address_tel.getText().toString());
						
						//省
						int currentItem = mViewProvince.getCurrentItem();
						String province = mProvinceDatas[currentItem];
						
						//市
						currentItem = mViewCity.getCurrentItem();
						String city = mCitisDatasMap.get(province)[currentItem];
						
						//区或者县
						currentItem = mViewDistrict.getCurrentItem();
						String district = mDistrictDatasMap.get(city)[currentItem];
						
						tmpAddress.setProvince(province);
						tmpAddress.setCity(city);
						tmpAddress.setTown(district);
						
						updateAddress(tmpAddress);
						AddAddressDialog.dismiss();
					}
				});
			}
		}
	}
	
	//更新地址
	private void updateAddress(AddressItem tmpAddress){
		String url = HttpUtil.BASE_URL + "/freqa/update.do";
		try {
			String json = HttpUtil.postRequest(url,tmpAddress);
			if(json == null){
				CommonsUtil.showLongToast(getApplicationContext(), "修改地址失败");
				return;
			}
			queryAddress();
		} catch (Exception e) {
			LOGGER.error("修改地址失败", e);
			CommonsUtil.showLongToast(getApplicationContext(), "修改地址失败");
		} 
	}

	@Override
	public void onClick(View v) {
		
		super.onClick(v);
		switch (v.getId()) {
		case R.id.new_address_btn://添加新地址
			showAddAdressDialog();
			break;
		default:
			break;
		}
	}

	//添加新地址
	private void showAddAdressDialog() {
		
		AddAddressDialog = new AlertDialog.Builder(AddressActivity.this).create();
		AddAddressDialog.show();
		AddAddressDialog.getWindow().setContentView(R.layout.dialog_add_address);
		AddAddressDialog.getWindow().clearFlags(
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		AddAddressDialog.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		
		setUpViews();
		setUpListener();
		setUpData();
		
		submit_btn = (Button) AddAddressDialog.findViewById(R.id.submit_btn);
		submit_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AddAddressDialog.dismiss();
				
				EditText add_address_address = (EditText) AddAddressDialog.findViewById(R.id.add_address_address);
				EditText add_address_tel = (EditText) AddAddressDialog.findViewById(R.id.add_address_tel);
				EditText add_address_name = (EditText) AddAddressDialog.findViewById(R.id.add_address_name);
				
				AddressItem tmpAddress = new AddressItem();
				
				tmpAddress.setUser_id(sessionManager.getUserId());
				tmpAddress.setAddress(add_address_address.getText().toString());//详细地址
				tmpAddress.setUser_name(add_address_name.getText().toString());//姓名
				tmpAddress.setMobile(add_address_tel.getText().toString());//电话
				
				//省
				int currentItem = mViewProvince.getCurrentItem();
				String province = mProvinceDatas[currentItem];
				
				//市
				currentItem = mViewCity.getCurrentItem();
				String city = mCitisDatasMap.get(province)[currentItem];
				
				//区或者县
				currentItem = mViewDistrict.getCurrentItem();
				String district = mDistrictDatasMap.get(city)[currentItem];
				
				tmpAddress.setProvince(province);
				tmpAddress.setCity(city);
				tmpAddress.setTown(district);
				
//				CommonsUtil.showLongToast(getApplicationContext(), province+";"+city+";"+district);
				addAddress(tmpAddress);
			}
		});
	}
	
	//新增地址
	private void addAddress(AddressItem tmpAddress){
		String url = HttpUtil.BASE_URL + "/freqa/add.do";
		try {
			String json = HttpUtil.postRequest(url,tmpAddress);
			if(json == null){
				CommonsUtil.showLongToast(getApplicationContext(), "新增地址失败");
				return;
			}
			
			queryAddress();
		} catch (Exception e) {
			LOGGER.error("新增地址失败", e);
			CommonsUtil.showLongToast(getApplicationContext(), "新增地址失败");
		}
	}
	
	private void setUpViews() {
		mViewProvince = (WheelView) AddAddressDialog.findViewById(R.id.id_province);
		mViewCity = (WheelView) AddAddressDialog.findViewById(R.id.id_city);
		mViewDistrict = (WheelView) AddAddressDialog.findViewById(R.id.id_district);
	}

	private void setUpListener() {
		// 添加change事件
		mViewProvince.addChangingListener(this);
		// 添加change事件
		mViewCity.addChangingListener(this);
		// 添加change事件
		mViewDistrict.addChangingListener(this);
		// 添加onclick事件
	}

	private void setUpData() {
		initProvinceDatas();
		mViewProvince.setViewAdapter(new ArrayWheelAdapter<String>(AddressActivity.this, mProvinceDatas));
		// 设置可见条目数量
		mViewProvince.setVisibleItems(7);
		mViewCity.setVisibleItems(7);
		mViewDistrict.setVisibleItems(7);
		updateCities();
		updateAreas();
	}

	/**
	 * 根据当前的省，更新市WheelView的信息
	 */
	private void updateCities() {
		int pCurrent = mViewProvince.getCurrentItem();
		mCurrentProviceName = mProvinceDatas[pCurrent];
		String[] cities = mCitisDatasMap.get(mCurrentProviceName);
		if (cities == null) {
			cities = new String[] { "" };
		}
		mViewCity.setViewAdapter(new ArrayWheelAdapter<String>(this, cities));
		mViewCity.setCurrentItem(0);
		updateAreas();
	}

	/**
	 * 根据当前的市，更新区WheelView的信息
	 */
	private void updateAreas() {
		int pCurrent = mViewCity.getCurrentItem();
		mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
		String[] areas = mDistrictDatasMap.get(mCurrentCityName);

		if (areas == null) {
			areas = new String[] { "" };
		}
		mViewDistrict.setViewAdapter(new ArrayWheelAdapter<String>(this, areas));
		mViewDistrict.setCurrentItem(0);
		
		mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[mViewDistrict.getCurrentItem()];
	}

	/**
	 * 解析省市区的XML数据
	 */
	protected void initProvinceDatas() {
		List<ProvinceModel> provinceList = null;
		AssetManager asset = getAssets();
		try {
			InputStream input = asset.open("province_data.xml");
			// 创建一个解析xml的工厂对象
			SAXParserFactory spf = SAXParserFactory.newInstance();
			// 解析xml
			SAXParser parser = spf.newSAXParser();
			XmlParserHandler handler = new XmlParserHandler();
			parser.parse(input, handler);
			input.close();
			// 获取解析出来的数据
			provinceList = handler.getDataList();
			// */ 初始化默认选中的省、市、区
			if (provinceList != null && !provinceList.isEmpty()) {
				mCurrentProviceName = provinceList.get(0).getName();
				List<CityModel> cityList = provinceList.get(0).getCityList();
				if (cityList != null && !cityList.isEmpty()) {
					mCurrentCityName = cityList.get(0).getName();
					List<DistrictModel> districtList = cityList.get(0).getDistrictList();
					mCurrentDistrictName = districtList.get(0).getName();
					mCurrentZipCode = districtList.get(0).getZipcode();
				}
			}
			// */
			mProvinceDatas = new String[provinceList.size()];
			for (int i = 0; i < provinceList.size(); i++) {
				// 遍历所有省的数据
				mProvinceDatas[i] = provinceList.get(i).getName();
				List<CityModel> cityList = provinceList.get(i).getCityList();
				String[] cityNames = new String[cityList.size()];
				for (int j = 0; j < cityList.size(); j++) {
					// 遍历省下面的所有市的数据
					cityNames[j] = cityList.get(j).getName();
					List<DistrictModel> districtList = cityList.get(j).getDistrictList();
					String[] distrinctNameArray = new String[districtList.size()];
					DistrictModel[] distrinctArray = new DistrictModel[districtList.size()];
					for (int k = 0; k < districtList.size(); k++) {
						// 遍历市下面所有区/县的数据
						DistrictModel districtModel = new DistrictModel(
								districtList.get(k).getName(), districtList
										.get(k).getZipcode());
						// 区/县对于的邮编，保存到mZipcodeDatasMap
						mZipcodeDatasMap.put(districtList.get(k).getName(),
								districtList.get(k).getZipcode());
						distrinctArray[k] = districtModel;
						distrinctNameArray[k] = districtModel.getName();
					}
					// 市-区/县的数据，保存到mDistrictDatasMap
					mDistrictDatasMap.put(cityNames[j], distrinctNameArray);
				}
				// 省-市的数据，保存到mCitisDatasMap
				mCitisDatasMap.put(provinceList.get(i).getName(), cityNames);
			}
		} catch (Throwable e) {
			LOGGER.error("加载城市数据失败", e);
			CommonsUtil.showLongToast(getApplicationContext(), "加载城市数据失败");
		} finally {

		}
	}

	@Override
	public void onChanged(WheelView wheel, int oldValue, int newValue) {
		if (wheel == mViewProvince) {
			updateCities();
		} else if (wheel == mViewCity) {
			updateAreas();
		} else if (wheel == mViewDistrict) {
			mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[newValue];
			mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);
		}
	}
	
	//根据省份查找对应的index
	private int findIndexBy(String[] strs,String up){
		if(strs == null || strs.length == 0){
			return 0;
		}
		
		for (int i = 0; i < strs.length; i++) {
			String up1 = strs[i];
			if(up.equals(up1)){
				return i;
			}
		}
		
		return 0;
	}
	//查询index
	private int findIndexBy(Map<String, String[]> map,String up,String down){
		if(map == null || map.isEmpty()){
			return 0;
		}
		
		String[] downs = map.get(up);
		return findIndexBy(downs, down);
	}
}
