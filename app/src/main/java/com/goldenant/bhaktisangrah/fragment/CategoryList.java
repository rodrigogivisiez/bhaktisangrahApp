package com.goldenant.bhaktisangrah.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.goldenant.bhaktisangrah.R;
import com.goldenant.bhaktisangrah.common.ui.MasterActivity;
import com.goldenant.bhaktisangrah.common.ui.MasterFragment;

/**
 * Created by ankita on 1/2/2016.
 */
public class CategoryList extends MasterFragment
{
    MasterActivity mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        mContext = getMasterActivity();

        View V = inflater.inflate(R.layout.category_list_fragment, container, false);
        return V;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContext.setTitle("Play songs");
    }
}
