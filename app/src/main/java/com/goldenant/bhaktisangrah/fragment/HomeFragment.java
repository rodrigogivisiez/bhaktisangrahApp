package com.goldenant.bhaktisangrah.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.goldenant.bhaktisangrah.R;
import com.goldenant.bhaktisangrah.common.ui.MasterActivity;
import com.goldenant.bhaktisangrah.common.ui.MasterFragment;

/**
 * Created by Ajay on 11-09-2015.
 */
public class HomeFragment extends MasterFragment {

    MasterActivity mContext;

    ListView listView_category;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        mContext = getMasterActivity();

        View V = inflater.inflate(R.layout.home_fragment, container, false);
        return V;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContext.setTitle("Home");

        listView_category = (ListView) view.findViewById(R.id.listView_category);
    }

}
