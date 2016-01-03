package com.goldenant.bhaktisangrah.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.goldenant.bhaktisangrah.R;
import com.goldenant.bhaktisangrah.common.ui.MasterFragment;

import java.util.ArrayList;

/**
 * Created by Ajay on 11-09-2015.
 */
public class HomeFragment extends MasterFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View V = inflater.inflate(R.layout.home_fragment, container, false);
        return V;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

}
