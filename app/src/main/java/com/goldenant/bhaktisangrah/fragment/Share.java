package com.goldenant.bhaktisangrah.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.goldenant.bhaktisangrah.R;

/**
 * Created by ankita on 1/2/2016.
 */
public class Share extends Fragment
{
    LinearLayout llFacebook,llEmail,llWhatsapp;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.share_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

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
