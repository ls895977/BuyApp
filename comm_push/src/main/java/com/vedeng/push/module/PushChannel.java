package com.vedeng.push.module;

import android.text.TextUtils;

/**********************************************************
 * @文件名称：PushChannel.java
 * @文件作者：聂中泽
 * @创建时间：2016年1月7日 下午3:20:08
 * @文件描述：推送渠道
 * @修改历史：2016年1月7日创建初始版本
 **********************************************************/
public enum PushChannel
{
	NONE("-1"), TENCENTXG("0"), GCM("1"), UMENG_TM("2");

	private String value;

	private PushChannel(String value)
	{
		this.value = value;
	}

	public static PushChannel getValueByTag(String type)
	{
		if (TextUtils.isEmpty(type))
		{
			return NONE;
		}
		for (PushChannel target : PushChannel.values())
		{
			if (target.value.equals(type))
			{
				return target;
			}
		}
		return NONE;
	}

	@Override
	public String toString()
	{
		return this.value;
	}
}
