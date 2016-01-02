package com.goldenant.bhaktisangrah.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.goldenant.bhaktisangrah.R;
import com.goldenant.bhaktisangrah.common.ui.MasterActivity;
import com.goldenant.bhaktisangrah.common.ui.MasterFragment;

/**
 * Created by ankita on 1/2/2016.
 */
public class Share extends MasterFragment
{
    LinearLayout llFacebook,llEmail,llWhatsapp;

    MasterActivity mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        mContext = getMasterActivity();
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
            public void onClick(View v) {

            }
        });

        llEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        llWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
