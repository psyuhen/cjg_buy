package com.cjhbuy.utils;


import java.util.Comparator;

import com.cjhbuy.bean.CityItem;

public class PinyinComparator implements Comparator<CityItem>{

	@Override
	public int compare(CityItem lhs, CityItem rhs) {
		String str1 =  PingYinUtil.getPingYin(lhs.getName());
		String str2 =  PingYinUtil.getPingYin(rhs.getName());
		
//		compa
		
		return str1.compareTo(str2);
	}
}
