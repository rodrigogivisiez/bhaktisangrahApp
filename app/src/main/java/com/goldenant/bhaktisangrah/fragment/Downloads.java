package com.goldenant.bhaktisangrah.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.goldenant.bhaktisangrah.MainActivity;
import com.goldenant.bhaktisangrah.R;
import com.goldenant.bhaktisangrah.adapter.DownloadAdapter;
import com.goldenant.bhaktisangrah.common.ui.MasterFragment;
import com.goldenant.bhaktisangrah.common.util.SongsManager;
import com.goldenant.bhaktisangrah.model.SubCategoryModel;

import java.util.ArrayList;

/**
 * Created by ankita on 1/2/2016.
 */
public class Downloads extends MasterFragment
{
    ListView listView_downloads;

    MainActivity mContext;

    public ArrayList<SubCategoryModel> songsList = new ArrayList<SubCategoryModel>();
    ArrayList<SubCategoryModel> songsListData = new ArrayList<SubCategoryModel>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        mContext = (MainActivity) getMasterActivity();
        return inflater.inflate(R.layout.download_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        mContext.setTitle("Downloads");

        listView_downloads = (ListView) view.findViewById(R.id.listView_downloads);

        SongsManager plm = new SongsManager();
        // get all songs from sdcard
        this.songsList = plm.getPlayList();

        // looping through playlist
        for (int i = 0; i < songsList.size(); i++) {
            // creating new HashMap

            SubCategoryModel categoryModel = new SubCategoryModel();

            categoryModel.setItem_name(songsList.get(i).getItem_name());
            categoryModel.setItem_file(songsList.get(i).getItem_file());

            // adding HashList to ArrayList
            songsListData.add(categoryModel);
        }

        DownloadAdapter Adapter = new DownloadAdapter(mContext,R.layout.category_item, songsListData);
        listView_downloads.setAdapter(Adapter);
    }
}
