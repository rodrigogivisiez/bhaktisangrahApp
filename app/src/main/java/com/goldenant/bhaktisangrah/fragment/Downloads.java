package com.goldenant.bhaktisangrah.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.goldenant.bhaktisangrah.R;

/**
 * Created by ankita on 1/2/2016.
 */
public class Downloads extends Fragment
{
    ListView listView_downloads;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.download_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        listView_downloads = (ListView) view.findViewById(R.id.listView_downloads);
    }
}
