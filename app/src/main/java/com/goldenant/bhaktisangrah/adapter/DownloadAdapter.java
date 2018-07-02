package com.goldenant.bhaktisangrah.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.goldenant.bhaktisangrah.MainActivity;
import com.goldenant.bhaktisangrah.R;
import com.goldenant.bhaktisangrah.common.ui.CircularImageView;
import com.goldenant.bhaktisangrah.common.util.Constants;
import com.goldenant.bhaktisangrah.fragment.Streaming;
import com.goldenant.bhaktisangrah.helpers.StorageUtil;
import com.goldenant.bhaktisangrah.model.SubCategoryModel;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.goldenant.bhaktisangrah.MainActivity.Broadcast_PLAY_NEW_AUDIO;
import static com.goldenant.bhaktisangrah.common.util.Constants.CATEGORY;
import static com.goldenant.bhaktisangrah.common.util.Constants.DOWNLOADS;

/**
 * Created by Adite on 03-01-2016.
 */
public class DownloadAdapter extends ArrayAdapter<String> {
    private LayoutInflater layoutInflater;

    public ArrayList<String> mItem = new ArrayList<String>();
    public ArrayList<String> mItemImage = new ArrayList<String>();
    public ArrayList<String> mItemSongPath = new ArrayList<String>();
    public ArrayList<String> mItemSongId = new ArrayList<String>();


    public MainActivity mContext;

    int resource;

    private static String fileName;

    private ProgressDialog pDialog;
    ArrayList<SubCategoryModel> songList;

    public DownloadAdapter(MainActivity context, int resource, ArrayList<String> list, ArrayList<String> listImage, ArrayList<String> listSongs, ArrayList<String> songsId) {
        super(context, resource);
        mContext = context;
        this.mItem = list;
        mItemImage = listImage;
        mItemSongPath = listSongs;
        this.resource = resource;
        mItemSongId = songsId;
        layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        songList = new ArrayList<>();
        for (int i = 0; i < mItem.size(); i++) {
            SubCategoryModel subCategoryModel = new SubCategoryModel();
            subCategoryModel.setItem_name(mItem.get(i));
            subCategoryModel.setItem_image(mItemImage.get(i));
            subCategoryModel.setItem_file(mItemSongPath.get(i));
            subCategoryModel.setItem_description(mItem.get(i));
            subCategoryModel.setItem_id(mItemSongId.get(i));
            songList.add(subCategoryModel);
        }

        Log.d("Adapter Call", "Size: " + mItem.size());
    }

    public int getCount() {
        return mItem.size();
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rootView = convertView;

        rootView = layoutInflater.inflate(R.layout.downloads_list_item, null, true);

        CircularImageView cat_image = (CircularImageView) rootView.findViewById(R.id.image_download);

        ImageButton play = (ImageButton) rootView.findViewById(R.id.play_download);

        TextView tv_title = (TextView) rootView.findViewById(R.id.tv_title_download);

        tv_title.setTypeface(mContext.getTypeFace());

        tv_title.setText(mItem.get(position));

        Log.d("TITLE", "" + mItem.get(position));
        Log.d("IMAGE", "" + mItemImage.get(position));

        try {
            Bitmap bitmap = BitmapFactory.decodeFile(mItemImage.get(position));

            BitmapDrawable d = new BitmapDrawable(bitmap);
            cat_image.setImageBitmap(bitmap);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SONG_PATH", "" + mItemSongPath.get(position));
                StorageUtil storage = new StorageUtil(getApplicationContext());
                if (storage.loadAudio() != null) {
                    ArrayList<SubCategoryModel> nowPlayingList = storage.loadAudio();
                    SubCategoryModel currentlyPlaying = nowPlayingList.get(storage.loadAudioIndex());
                    SubCategoryModel selectedAudio = songList.get(position);
                    if (selectedAudio.getItem_file().equalsIgnoreCase(currentlyPlaying.getItem_file())) {
                        Fragment streaming = new Streaming();
                        Bundle bundle = new Bundle();
                        bundle.putInt("mode", 1);
                        bundle.putString("isFrom", DOWNLOADS);
                        bundle.putInt("position", position);
                        bundle.putSerializable("data", songList);
                        streaming.setArguments(bundle);
                        mContext.ReplaceFragement(streaming);
                        mContext.setMode(1);
                        storage.storeMode(1);
                        return;
                    }
                    if (mContext.serviceBound && mContext.isPlaying()) {
                        storage.clearCachedAudioPlaylist();
                        storage.storeAudio(songList);
                        storage.storeAudioIndex(position);
                        storage.storeMode(1);
                        mContext.setMode(1);
                        Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
                        mContext.sendBroadcast(broadcastIntent);
                    } else if (mContext.serviceBound) {
                        storage.clearCachedAudioPlaylist();
                        storage.storeAudio(songList);
                        storage.storeAudioIndex(position);
                        storage.storeMode(1);
                        mContext.setMode(1);
                        mContext.playSong();
                    }

                } else if (mContext.serviceBound) {
                    storage.clearCachedAudioPlaylist();
                    storage.storeAudio(songList);
                    storage.storeAudioIndex(position);
                    storage.storeMode(1);
                    mContext.setMode(1);
                    mContext.playSong();
                }


                Fragment streaming = new Streaming();

                Bundle bundle = new Bundle();
                bundle.putInt("mode", 1);
                bundle.putString("isFrom", DOWNLOADS);
                bundle.putSerializable("item_file", mItemSongPath);
                bundle.putSerializable("item_name", mItem);
                bundle.putSerializable("item_image", mItemImage);
                bundle.putInt("position", position);
                bundle.putSerializable("data", songList);

                streaming.setArguments(bundle);
                mContext.ReplaceFragement(streaming);
            }
        });


        return rootView;
    }
}