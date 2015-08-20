package com.cjhbuy.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.cjhbuy.adapter.CommonAdapter;
import com.cjhbuy.adapter.ViewHolder;
import com.cjhbuy.auth.SessionManager;
import com.cjhbuy.bean.AddressItem;
import com.cjhbuy.utils.CommonsUtil;
import com.cjhbuy.utils.HttpUtil;
import com.cjhbuy.utils.JsonUtil;
import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;

public class AddressActivity extends BaseActivity {
	private static final Logger LOGGER = LoggerFactory.getLogger(AddressActivity.class);

	private ListView address_listview;
	private List<AddressItem> addressList;
	private Button new_address_btn;
	private AlertDialog AddAddressDialog;
	private Button submit_btn;
	private Button item_address_edit;
	
	private AddressItem deleteAddress;
	private CommonAdapter<AddressItem> showAdapter = null;
	
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
		address_listview = (ListView) findViewById(R.id.address_listview);
		right_imgbtn.setVisibility(View.GONE);
		new_address_btn = (Button) findViewById(R.id.new_address_btn);
		new_address_btn.setOnClickListener(this);
	}

	private void initData() {
		addressList = new ArrayList<AddressItem>();
		showAdapter = showAdapter();
		address_listview.setAdapter(showAdapter);
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
		
		queryAddress();
	}
	
	//查询常用地址哟
	private void queryAddress(){
		int user_id = sessionManager.getInt(SessionManager.KEY_USER_ID);
		
		String url = HttpUtil.BASE_URL + "/freqa/query.do?user_id="+user_id;
		try {
			String json = HttpUtil.getRequest(url);
			if(json == null){
				CommonsUtil.showLongToast(getApplicationContext(), "还没评分");
				return;
			}
			List<AddressItem> list = JsonUtil.parse2ListAddressItem(json);
			addressList.clear();
			addressList.addAll(list);
			showAdapter.notifyDataSetChanged();
		} catch (InterruptedException e) {
			LOGGER.error("评分失败", e);
			CommonsUtil.showLongToast(getApplicationContext(), "查询买家评分信息失败");
		} catch (ExecutionException e) {
			LOGGER.error("评分失败", e);
			CommonsUtil.showLongToast(getApplicationContext(), "查询买家评分信息失败");
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
		} catch (InterruptedException e) {
			LOGGER.error("删除地址失败", e);
			CommonsUtil.showLongToast(getApplicationContext(), "删除地址失败");
		} catch (ExecutionException e) {
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
				helper.setText(R.id.item_address_details, item.getAddress());
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
				
				final EditText add_address_address = (EditText) AddAddressDialog
						.findViewById(R.id.add_address_address);
				final EditText add_address_tel = (EditText) AddAddressDialog
						.findViewById(R.id.add_address_tel);
				final EditText add_address_name = (EditText) AddAddressDialog
						.findViewById(R.id.add_address_name);
				
				add_address_address.setText(tmpAddress.getAddress());
				add_address_tel.setText(tmpAddress.getMobile());
				add_address_name.setText(tmpAddress.getUser_name());
				TextView add_address_title_text = (TextView) AddAddressDialog.findViewById(R.id.add_address_title_text);
				add_address_title_text.setText("修改地址");
				
				submit_btn = (Button) AddAddressDialog.findViewById(R.id.submit_btn);
				submit_btn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						tmpAddress.setAddress(add_address_address.getText().toString());
						tmpAddress.setUser_name(add_address_name.getText().toString());
						tmpAddress.setMobile(add_address_tel.getText().toString());
						
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
		} catch (InterruptedException e) {
			LOGGER.error("修改地址失败", e);
			CommonsUtil.showLongToast(getApplicationContext(), "修改地址失败");
		} catch (ExecutionException e) {
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
		submit_btn = (Button) AddAddressDialog.findViewById(R.id.submit_btn);
		submit_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AddAddressDialog.dismiss();
				
				EditText add_address_address = (EditText) AddAddressDialog.findViewById(R.id.add_address_address);
				EditText add_address_tel = (EditText) AddAddressDialog.findViewById(R.id.add_address_tel);
				EditText add_address_name = (EditText) AddAddressDialog.findViewById(R.id.add_address_name);
				
				AddressItem tmpAddress = new AddressItem();
				tmpAddress.setAddress(add_address_address.getText().toString());
				tmpAddress.setUser_name(add_address_name.getText().toString());
				tmpAddress.setMobile(add_address_tel.getText().toString());
				addAddress(tmpAddress);
			}
		});
	}
	
	//更新地址
	private void addAddress(AddressItem tmpAddress){
		String url = HttpUtil.BASE_URL + "/freqa/add.do";
		try {
			String json = HttpUtil.postRequest(url,tmpAddress);
			if(json == null){
				CommonsUtil.showLongToast(getApplicationContext(), "新增地址失败");
				return;
			}
			
			queryAddress();
		} catch (InterruptedException e) {
			LOGGER.error("新增地址失败", e);
			CommonsUtil.showLongToast(getApplicationContext(), "新增地址失败");
		} catch (ExecutionException e) {
			LOGGER.error("新增地址失败", e);
			CommonsUtil.showLongToast(getApplicationContext(), "新增地址失败");
		}
	}
}
