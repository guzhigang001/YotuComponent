package com.example.ggxiaozhi.minesdk.okhttp3.exception;

/**
 * 工程名 ： YotuComponent
 * 包名   ： com.example.ggxiaozhi.minesdk.okhttp3.listener
 * 作者名 ： 志先生_
 * 日期   ： 2017/10/10
 * 功能   ：封装自定义网络请求异常
 */
public class OkHttpException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * the server return code
	 */
	private int ecode;

	/**
	 * the server return error message
	 */
	private Object emsg;

	public OkHttpException(int ecode, Object emsg) {
		this.ecode = ecode;
		this.emsg = emsg;
	}

	public int getEcode() {
		return ecode;
	}

	public Object getEmsg() {
		return emsg;
	}
}