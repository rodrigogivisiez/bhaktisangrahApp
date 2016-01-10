package com.goldenant.bhaktisangrah.common.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@SuppressLint("NewApi")
public class FUtils {

	public static void getAppPackageAppHash(Activity activity) {
		PackageInfo info;
		try {
			info = activity.getPackageManager().getPackageInfo(
					"com.goldenant.bhaktisangrah", PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				String keyHash = Base64.encodeToString(md.digest(),
						Base64.DEFAULT);
				Log.d("Facebook KeyHash = ", keyHash);
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

	}

	public static boolean isConnectedToNetwork(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		@SuppressWarnings("deprecation")
		NetworkInfo[] networks = cm.getAllNetworkInfo();

		if (networks != null) {
			for (int i = 0; i < networks.length; i++) {
				NetworkInfo networkInfo = networks[i];
				if (networkInfo.isConnected()) {
					return true;
				}
			}
		}

		return false;
	}
}
