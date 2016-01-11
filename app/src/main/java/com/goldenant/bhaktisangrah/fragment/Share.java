package com.goldenant.bhaktisangrah.fragment;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.goldenant.bhaktisangrah.MainActivity;
import com.goldenant.bhaktisangrah.R;
import com.goldenant.bhaktisangrah.common.ui.MasterFragment;
import com.goldenant.bhaktisangrah.common.util.FUtils;
import com.goldenant.bhaktisangrah.common.util.ToastUtil;

/**
 * Created by ankita on 1/2/2016.
 */
public class Share extends MasterFragment
{
    LinearLayout llFacebook,llEmail,llWhatsapp;

    MainActivity mContext;

    CallbackManager callbackManager;
    ShareDialog shareDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        mContext = (MainActivity) getMasterActivity();

        FacebookSdk.sdkInitialize(mContext);
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        return inflater.inflate(R.layout.share_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        mContext.setTitle("Share");

        llFacebook = (LinearLayout) view.findViewById(R.id.llFacebook);
        llEmail  = (LinearLayout) view.findViewById(R.id.llEmail);
        llWhatsapp = (LinearLayout) view.findViewById(R.id.llWhatsapp);

        llFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentTitle("Bhakti Sagar")
                            .setContentDescription(
                                    "This is the awesome Devotional songs app")
                            .setContentUrl(Uri.parse("https://i.ytimg.com/vi/bIqfItDy0do/hqdefault.jpg"))
                            .build();

                    shareDialog.show(linkContent);
                }


//                            .setContentUrl(Uri.parse("market://details?id=" + mContext.getPackageName()))
            }
        });

        llEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext.isInternet)
                {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri
                            .fromParts("mailto", "goldenant.apps@gmail.com", null));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Bhakti Sagar");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "market://details?id=" + mContext.getPackageName());
                    startActivity(Intent.createChooser(emailIntent, "Send email..."));
                }
                else
                {
                    ToastUtil.showLongToastMessage(mContext, "No internet connection found");
                }
            }
        });

        llWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    PackageManager pm= mContext.getPackageManager();
                    Intent waIntent = new Intent(Intent.ACTION_SEND);
                    waIntent.setType("text/plain");
                    String text = "market://details?id=" + mContext.getPackageName();

                    PackageInfo info=pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
                    //Check if package exists or not. If not then code
                    //in catch block will be called
                    waIntent.setPackage("com.whatsapp");

                    waIntent.putExtra(Intent.EXTRA_TEXT, text);
                    startActivity(Intent.createChooser(waIntent, "Share with"));

                }
                catch (PackageManager.NameNotFoundException e)
                {
                    ToastUtil.showLongToastMessage(mContext, "WhatsApp not Installed");
                }
            }
        });
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);


        if(resultCode == -1)
        {
            ToastUtil.showShortToastMessage(mContext,"Successfully share");
        }

    }

    @Override
    public void onResume()
    {
        super.onResume();

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    HomeFragment home = new HomeFragment();
                    mContext.ReplaceFragement(home);
                }
                return false;
            }
        });
    }
}
