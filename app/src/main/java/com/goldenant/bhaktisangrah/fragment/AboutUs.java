package com.goldenant.bhaktisangrah.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.goldenant.bhaktisangrah.MainActivity;
import com.goldenant.bhaktisangrah.R;
import com.goldenant.bhaktisangrah.common.ui.MasterFragment;
import com.goldenant.bhaktisangrah.common.util.Constants;
import com.goldenant.bhaktisangrah.common.util.NetworkRequest;
import com.goldenant.bhaktisangrah.common.util.ToastUtil;

import org.json.JSONObject;

/**
 * Created by ankita on 1/2/2016.
 */
public class AboutUs extends MasterFragment
{
    WebView webview;

    MainActivity mContext;

    String mContent;

    TextView textView1,textView2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        mContext = (MainActivity) getMasterActivity();
        return inflater.inflate(R.layout.aboutus_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        mContext.hideDrawer();
        mContext.showDrawerBack();
        mContext.setTitle("AboutUs");

        webview = (WebView) view.findViewById(R.id.webView);

        textView1 = (TextView) view.findViewById(R.id.textView1_about_us);
        textView2 = (TextView) view.findViewById(R.id.textView2_about_us);

        webview.getSettings();
        webview.setBackgroundColor(0x00000000);
        textView1.setTypeface(getMasterActivity().getTypeFace());
        textView2.setTypeface(getMasterActivity().getTypeFace());

        textView2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.goldenant.in"));
                startActivity(browserIntent);
            }
        });



        if(mContext.isInternet == true)
        {
            getAboutUs();
        }
        else
        {
            ToastUtil.showLongToastMessage(mContext, "No internet connection found");
        }

        mContext.drawer_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragment home = new HomeFragment();
                mContext.ReplaceFragement(home);
            }
        });

    }

    private void getAboutUs()
    {
        mContext.showWaitIndicator(true);
        NetworkRequest dishRequest = new NetworkRequest(mContext);
        dishRequest.sendRequest(Constants.API_ABOUT_US_URL, null, Callback);
    }

    NetworkRequest.NetworkRequestCallback Callback = new NetworkRequest.NetworkRequestCallback() {
        @Override
        public void OnNetworkResponseReceived(JSONObject response) {
            Log.d("ABOUTUS_API","" + response.toString());
            mContext.showWaitIndicator(false);
            try {
                if (response != null) {
                    JSONObject jObject = new JSONObject(response.toString());
                    String status = jObject.getString("status");

                    if (status.equalsIgnoreCase("1"))
                    {
                        JSONObject data = jObject.getJSONObject("data");

                        mContent = data.getString("about_us");

                        String text = "<html><body style=\"color:#FFFFFF;font-family:file:///android_asset//ProximaNova-Light.otf;font-size:16px;\"'background-color:#1e5159' >"
                                + "<p align=\"justify\">"
                                + mContent
                                + "</p>"
                                + "</body></html>";

                        webview.loadData("<font>" + text + "</font>","text/html; charset=UTF-8", null);
                    }
                    else
                    {
                        String message = jObject.getString("message");

                        String text = "<html><body style=\"color:#FFFFFF;font-family:file:///android_asset//ProximaNova-Light.otf;font-size:16px;\"'background-color:#1e5159' >"
                                + "<p align=\"justify\">"
                                + message
                                + "</p>"
                                + "</body></html>";

                        webview.loadData("<font>" + text + "</font>","text/html; charset=UTF-8", null);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                mContext.showWaitIndicator(false);
            }
        }

        @Override
        public void OnNetworkErrorReceived(String error) {
            // TODO Auto-generated method stub
            mContext.showWaitIndicator(false);
        }
    };

    @Override
    public void onResume()
    {
        super.onResume();

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK)
                {
                    HomeFragment home = new HomeFragment();
                    mContext.ReplaceFragement(home);
                }
                return false;
            }
        });
    }
}
