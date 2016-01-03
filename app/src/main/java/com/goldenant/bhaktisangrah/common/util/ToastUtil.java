package com.goldenant.bhaktisangrah.common.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil 
{
	public static void showToastMessage(Context appContext, String message, int duration) 
	{
		Toast.makeText(appContext, message, duration).show();
	}
	
	public static void showLongToastMessage(Context appContext, String message)
	{
		showToastMessage(appContext, message, Toast.LENGTH_LONG);
	}
	
	public static void showShortToastMessage(Context appContext, String message) 
	{
		showToastMessage(appContext, message, Toast.LENGTH_SHORT);
	}
}