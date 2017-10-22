package com.example.ggxiaozhi.yotucomponent.view.weigth;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.MultiAutoCompleteTextView;

/**
 * 工程名 ： BaiLan
 * 包名   ： com.example.ggxiaozhi.store.the_basket.banner
 * 作者名 ： 志先生_
 * 日期   ： 2017/9/2
 * 时间   ： 19:27
 * 功能   ：本在邮箱联想控件，输入@符后开始联想
 */
public class MailBoxAssociateView extends MultiAutoCompleteTextView
{
	public MailBoxAssociateView(Context context)
	{
		super(context);
	}

	public MailBoxAssociateView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public MailBoxAssociateView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	@Override
	public boolean enoughToFilter()
	{
		return getText().toString().contains("@") && getText().toString().indexOf("@") > 0;
	}
}