package com.goldenant.bhaktisangrah.common.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.goldenant.bhaktisangrah.common.util.InternetStatus;

/**
 * Created by Adite on 02-01-2016.
 */
public class MasterFragment extends Fragment {

    public boolean isInternet;

    public MasterActivity getMasterActivity() {
        MasterActivity masterActivity = (MasterActivity) getActivity();
        return masterActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        isInternet = new InternetStatus().isInternetOn(getMasterActivity());
        return super.onCreateView(inflater, container, savedInstanceState);

    }


}
