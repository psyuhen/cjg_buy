package com.cjhbuy.fragment;

import com.cjhbuy.activity.GoodsViewActivity;
import com.cjhbuy.activity.R;
import com.cjhbuy.activity.SelectCityActivity;
import com.cjhbuy.adapter.HomeFragmentAdapter;
import com.cjhbuy.utils.AppContext;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HomeFragment extends Fragment implements OnClickListener {
	private ViewPager fragment_home_viewpager;
	private RelativeLayout frament_home_selector_ll1;
	private RelativeLayout frament_home_selector_ll2;
	private RelativeLayout frament_home_selector_ll3;

	private View fragment_home_left_line;
	private View fragment_home_center_line;
	private View fragment_home_right_line;
	private HomeFragmentAdapter mFragmentAdapter;
	private ImageView frament_home_top_image;
	private RelativeLayout search_head_address_ll;
	private TextView search_head_address_text;

	private SharedPreferences preferences;
	private AppContext appContext;

	public interface MyListener {
		public void showMessage(String city);
	}

	private MyListener myListener;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		myListener = (MyListener) activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View contentView = inflater.inflate(R.layout.fragment_home, container,
				false);
		appContext = (AppContext) getActivity().getApplicationContext();
		initView(contentView);
		initData();
		mFragmentAdapter = new HomeFragmentAdapter(getActivity()
				.getSupportFragmentManager(), getActivity());
		fragment_home_viewpager.setAdapter(mFragmentAdapter);

		return contentView;
	}

	private void initView(View contentView) {
		// TODO Auto-generated method stub
		frament_home_selector_ll1 = (RelativeLayout) contentView
				.findViewById(R.id.frament_home_selector_ll1);
		frament_home_selector_ll1.setOnClickListener(this);
		frament_home_selector_ll2 = (RelativeLayout) contentView
				.findViewById(R.id.frament_home_selector_ll2);
		frament_home_selector_ll2.setOnClickListener(this);
		frament_home_selector_ll3 = (RelativeLayout) contentView
				.findViewById(R.id.frament_home_selector_ll3);
		frament_home_selector_ll3.setOnClickListener(this);
		fragment_home_viewpager = (ViewPager) contentView
				.findViewById(R.id.fragment_home_viewpager);
		fragment_home_left_line = contentView
				.findViewById(R.id.fragment_home_left_line);
		fragment_home_center_line = contentView
				.findViewById(R.id.fragment_home_center_line);
		fragment_home_right_line = contentView
				.findViewById(R.id.fragment_home_right_line);
		frament_home_top_image = (ImageView) contentView
				.findViewById(R.id.frament_home_top_image);
		frament_home_top_image.setOnClickListener(this);
		search_head_address_ll = (RelativeLayout) contentView
				.findViewById(R.id.search_head_address_ll);
		search_head_address_ll.setOnClickListener(this);
		search_head_address_text = (TextView) contentView
				.findViewById(R.id.search_head_address_text);
	}

	private void initData() {
		// TODO Auto-generated method stub
		Bundle bundle = getArguments();
		String city = bundle.getString("city");
		search_head_address_text.setText(city);
		fragment_home_viewpager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				// TODO Auto-generated method stub
				switch (position) {
				case 0:
					fragment_home_left_line.setVisibility(View.VISIBLE);
					fragment_home_center_line.setVisibility(View.INVISIBLE);
					fragment_home_right_line.setVisibility(View.INVISIBLE);
					break;
				case 1:
					fragment_home_left_line.setVisibility(View.INVISIBLE);
					fragment_home_center_line.setVisibility(View.VISIBLE);
					fragment_home_right_line.setVisibility(View.INVISIBLE);
					break;
				case 2:
					fragment_home_left_line.setVisibility(View.INVISIBLE);
					fragment_home_center_line.setVisibility(View.INVISIBLE);
					fragment_home_right_line.setVisibility(View.VISIBLE);
					break;
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.frament_home_selector_ll1:
			fragment_home_left_line.setVisibility(View.VISIBLE);
			fragment_home_center_line.setVisibility(View.INVISIBLE);
			fragment_home_right_line.setVisibility(View.INVISIBLE);
			fragment_home_viewpager.setCurrentItem(0);
			break;
		case R.id.frament_home_selector_ll2:
			fragment_home_left_line.setVisibility(View.INVISIBLE);
			fragment_home_center_line.setVisibility(View.VISIBLE);
			fragment_home_right_line.setVisibility(View.INVISIBLE);
			fragment_home_viewpager.setCurrentItem(1);
			break;
		case R.id.frament_home_selector_ll3:
			fragment_home_left_line.setVisibility(View.INVISIBLE);
			fragment_home_center_line.setVisibility(View.INVISIBLE);
			fragment_home_right_line.setVisibility(View.VISIBLE);
			fragment_home_viewpager.setCurrentItem(2);
			break;
		case R.id.frament_home_top_image:
			startActivity(new Intent(getActivity(), GoodsViewActivity.class));
			break;
		case R.id.search_head_address_ll:
//			preferences = appContext.getPreferences();
//			Log.i("zkq", preferences.edit() + "--");
//			Editor editor = preferences.edit();
//			editor.putInt("isnext", 3);
//			editor.commit();
			startActivity(new Intent(getActivity(), SelectCityActivity.class));
			break;
		default:
			break;
		}
	}
}
