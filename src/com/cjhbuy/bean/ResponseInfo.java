/**
 * 
 */
package com.cjhbuy.bean;

import java.io.Serializable;

/**
 * http请返回的对象
 * @author pansen
 *
 */
public class ResponseInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String SUCCESS = "0";
	public static final String FAILURE = "1";

	private String status = "";//成功或者失败0-成功，1-失败
	private String desc = "";//成功或者失败的描述信息
	private Object dataObj = null;//需要返回给前端的数据
	
	/**
	 * 
	 */
	public ResponseInfo() {
		this("", "", null);
	}
	
	/**
	 * dataObj 默认为null
	 * @param status 成功或者失败
	 * @param desc 成功或者失败的描述信息
	 */
	public ResponseInfo(String status,String desc) {
		this(status, desc, null);
	}
	
	/**
	 * @param status 成功或者失败
	 * @param desc 成功或者失败的描述信息
	 * @param dataObj 需要返回给前端的数据
	 */
	public ResponseInfo(String status,String desc,Object dataObj) {
		this.status = status;
		this.desc = desc;
		this.dataObj = dataObj;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Object getDataObj() {
		return dataObj;
	}

	public void setDataObj(Object dataObj) {
		this.dataObj = dataObj;
	}
	
}
