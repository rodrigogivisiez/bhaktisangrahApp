package com.goldenant.bhaktisangrah.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.goldenant.bhaktisangrah.R;
import com.goldenant.bhaktisangrah.common.ui.MasterActivity;
import com.goldenant.bhaktisangrah.common.ui.MasterFragment;

/**
 * Created by ankita on 1/2/2016.
 */
public class Downloads extends MasterFragment
{
    ListView listView_downloads;

    MasterActivity mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        mContext = getMasterActivity();
        return inflater.inflate(R.layout.download_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        mContext.setTitle("Downloads");

        listView_downloads = (ListView) view.findViewById(R.id.listView_downloads);
    }
}
