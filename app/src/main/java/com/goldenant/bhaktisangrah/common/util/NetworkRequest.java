package com.goldenant.bhaktisangrah.common.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

@SuppressWarnings("deprecation")
public class NetworkRequest extends AsyncTask<String, Void, JSONObject> {
	Context mContext;
	List<NameValuePair> mPostData;
	HttpMethod mHttpMethod;
	NetworkRequestCallback mNetworkRequestCallback;

	private boolean ERROR_STATE = false;
	private String ERROR_STRING = "";

	enum HttpMethod {
		GET, POST
	}

	public interface NetworkRequestCallback {
		// TODO: wrap json response in a response class with raw string property
		public void OnNetworkResponseReceived(JSONObject response);

		public void OnNetworkErrorReceived(String error);
	}

	public NetworkRequest(Context context) {
		mContext = context;
	}

	@Override
	protected JSONObject doInBackground(String... urls) {
		return sendHttpRequest(urls[0]);
	}

	@Override
	protected void onPostExecute(JSONObject response) {
		// process response here

		if (!ERROR_STATE) {
			mNetworkRequestCallback.OnNetworkResponseReceived(response);
		} else {
			mNetworkRequestCallback.OnNetworkErrorReceived(ERROR_STRING);
			Log.d("ERROR_STRING " ,""+ ERROR_STRING);
			
			if(ERROR_STRING == null)
			{
				ToastUtil.showLongToastMessage(mContext,
						"Something want wrong,please try after some time");
			}
			else  if(ERROR_STRING.contains("No address associated with hostname"))
			{
				ToastUtil.showLongToastMessage(mContext,
						"Check if you are connected to network !!");
			}
			
			else
			{
				ToastUtil.showLongToastMessage(mContext,
						"Connection refused,please try after some time");
			}
			
		}
	}

	public void setPostData(List<NameValuePair> data) {
		mPostData = data;
	}

	public void setRequestType(HttpMethod httpMethod) {
		mHttpMethod = httpMethod;
	}

	// send request to server for getting response of all api
	public void sendRequest(String url, List<NameValuePair> data,
			NetworkRequestCallback callback) {
		mPostData = data;
		mNetworkRequestCallback = callback;
		mHttpMethod = HttpMethod.POST;
		execute(new String[] { url });
	}

	private void setErrorState(String errorMessage) {
		ERROR_STATE = true;
		ERROR_STRING = errorMessage;

	}

	private JSONObject sendHttpRequest(String url) {
		JSONObject json = null;

		if (mHttpMethod == HttpMethod.GET) {

		} else if (mHttpMethod == HttpMethod.POST) {
			json = sendHttpPostException(url);
		}
		return json;
	}

	private JSONObject sendHttpPostException(String url) {
		JSONObject responseJson = null;
		try 
		{
			String response = sendHttpPostRequest(url);
			responseJson = new JSONObject(response);
		} 
		catch (ParseException e) 
		{
			setErrorState(e.getLocalizedMessage());
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			setErrorState(e.getLocalizedMessage());
			e.printStackTrace();
		} 
		catch (JSONException e) 
		{
			setErrorState(e.getLocalizedMessage());
		} 
		catch (NullPointerException e) 
		{
			setErrorState(e.getLocalizedMessage());
		}
		return responseJson;
	}

	@SuppressWarnings("resource")
	private String sendHttpPostRequest(String url) throws ParseException,
			IOException {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);
		Log.d("TAG", "URL : " + url);

		if (mPostData != null) {
			httppost.setEntity(new UrlEncodedFormEntity(mPostData));
		}


		HttpResponse response = httpclient.execute(httppost);
		if (checkResponseStatus(response)) {
			return EntityUtils.toString(response.getEntity());
		}
		return null;
	}

	private boolean checkResponseStatus(HttpResponse response) {
		int statusCode = response.getStatusLine().getStatusCode();

		if (statusCode == HttpStatus.SC_OK
				|| statusCode == HttpStatus.SC_CREATED) {
			return true;
		}
		return false;
	}
}