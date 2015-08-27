package com.cjhbuy.activity;

import java.util.ArrayList;
import java.util.List;

import com.cjhbuy.adapter.CityAdapter;
import com.cjhbuy.adapter.CityHeadViewAdapter;
import com.cjhbuy.bean.CityItem;
import com.cjhbuy.common.MyGridView;
import com.cjhbuy.common.SideBar;
import com.cjhbuy.utils.AppContext;
import com.cjhbuy.utils.PingYinUtil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
/**
 * 城市选择
 * @author ps
 *
 */
public class SelectCityActivity extends BaseActivity implements
		OnItemClickListener, OnScrollListener {
	private WindowManager mWindowManager;
	private SideBar mSideBar;
	private ListView mListView;
	private TextView mDialogText;
	private List<CityItem> cities;
	private List<CityItem> hotcities;
	private SharedPreferences preferences;
	private AppContext appContext;
	private boolean mReady;
	private boolean mShowing;
	private char mPrevLetter = Character.MIN_VALUE;
	private RemoveWindow mRemoveWindow = new RemoveWindow();
	Handler mHander = new Handler();
	private MyGridView mGridView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_select_city);
		initView();
		init();
	}

	@Override
	public void initView() {
		
		super.initView();
	}

	private void init() {
		
		title.setText("选取城市");
		hotcities = new ArrayList<CityItem>();
		appContext = (AppContext) getApplicationContext();
		preferences = getPreferences(MODE_PRIVATE);
		appContext.setPreferences(preferences);
		show();
	}

	private void show() {
		mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
						| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);
		mSideBar = (SideBar) findViewById(R.id.citySideBar);
		mListView = (ListView) findViewById(R.id.lv_city);
		mListView.setOnScrollListener(this);
		mSideBar.setListView(mListView);
		mDialogText = (TextView) LayoutInflater.from(this).inflate(
				R.layout.list_position, null);
		mDialogText.setVisibility(View.INVISIBLE);
		mSideBar.setTextView(mDialogText);
		mHander.post(new Runnable() {

			@Override
			public void run() {
				
				mReady = true;
				WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
						WindowManager.LayoutParams.TYPE_APPLICATION,
						WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
								| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
						PixelFormat.TRANSLUCENT);
				mWindowManager.addView(mDialogText, lp);
			}
		});
		cities = ((AppContext) getApplicationContext()).getCities();
		CityAdapter adapter = new CityAdapter(this, cities);
		mListView.addHeaderView(getHeadView());
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(this);
	}

	private View getHeadView() {
		
		View view = LayoutInflater.from(this).inflate(R.layout.city_headview,
				null);
		String[] data = getResources().getStringArray(R.array.hot_city);
		for (int i = 0; i < data.length; i++) {
			CityItem cityItem = new CityItem();
			cityItem.setName(data[i]);
			hotcities.add(cityItem);
		}
		mGridView = (MyGridView) view.findViewById(R.id.gv_headview_city);
		mGridView.setAdapter(new CityHeadViewAdapter(this, hotcities));
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long len) {
				
				// if (isnext == 7) {
				String city = hotcities.get(position).getName();
				AppContext appContext = (AppContext) getApplication();
				appContext.setCity(city);
				Intent intent = new Intent();
				intent.setClass(SelectCityActivity.this, HomeActivity.class);
				intent.putExtra("city", city);
				startActivity(intent);
				finish();
			}
		});
		return view;
	}

	@Override
	protected void onDestroy() {
		if (mWindowManager != null)
			mWindowManager.removeView(mDialogText);
		Editor editor = preferences.edit();
		editor.putInt("isnext", 4);
		editor.commit();
		super.onDestroy();
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		int lastItem = firstVisibleItem + visibleItemCount - 1;
		if (mReady) {
			// char firstLetter = mStrings[arg1].charAt(0);

			String name = cities.get(firstVisibleItem).getName();
			String lastCatalogString = PingYinUtil.converterToFirstSpell(name
					.substring(0, 1));

			char firstLetter = lastCatalogString.charAt(0);

			if (!mShowing && firstLetter != mPrevLetter) {
				mShowing = true;
				mDialogText.setVisibility(View.VISIBLE);
			}
			mDialogText.setText(((Character) firstLetter).toString());
			mHander.removeCallbacks(mRemoveWindow);
			mHander.postDelayed(mRemoveWindow, 3000);
			mPrevLetter = firstLetter;
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {
		

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		String city = cities.get(position - 1).getName();
		appContext.setCity(city);
		Intent intent = new Intent(this, HomeActivity.class);
		intent.putExtra("city", city);
		startActivity(intent);
		finish();
	}

	private void removeWindow() {
		if (mShowing) {
			mShowing = false;
			mDialogText.setVisibility(View.INVISIBLE);
		}
	}

	private final class RemoveWindow implements Runnable {
		@Override
		public void run() {
			removeWindow();
		}
	}
}
