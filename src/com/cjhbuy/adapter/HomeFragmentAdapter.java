package com.cjhbuy.adapter;


import com.cjhbuy.fragment.HomeSaleFragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class HomeFragmentAdapter extends FragmentStatePagerAdapter {
	private int pageCount =3; 
	private Context context;
	private HomeSaleFragment saleFragment;//按销量
	private HomeSaleFragment distanceFragment;//按距离
	private HomeSaleFragment populatiryFragment;//按人气
	
	public HomeFragmentAdapter(FragmentManager fm) {
		super(fm);
		
	}

	public HomeFragmentAdapter(FragmentManager fm, Context context) {
		super(fm);
		this.context = context;
		
		this.saleFragment = HomeSaleFragment.newInstance(this.context,0);
		this.distanceFragment = HomeSaleFragment.newInstance(this.context,1);
		this.populatiryFragment = HomeSaleFragment.newInstance(this.context,2);
	}

	@Override
	public Fragment getItem(int position) {
		
		switch (position) {
		case 0:
			return this.saleFragment;
		case 1:
			return this.distanceFragment;
		case 2:
			return this.populatiryFragment;
		}
		return null;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return super.getPageTitle(position);
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	/**
	 * 获取总数
	 */
	@Override
	public int getCount() {
		return pageCount;
	}

}
