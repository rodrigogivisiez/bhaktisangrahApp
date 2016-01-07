package com.goldenant.bhaktisangrah.adapter;

import android.app.ProgressDialog;
import android.content.Context;
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
import com.goldenant.bhaktisangrah.fragment.Streaming;

import java.util.ArrayList;

/**
 * Created by Adite on 03-01-2016.
 */
public class DownloadAdapter extends ArrayAdapter<String>
{
    private LayoutInflater layoutInflater;

    public ArrayList<String> mItem = new ArrayList<String>();
    public ArrayList<String> mItemImage = new ArrayList<String>();
    public ArrayList<String> mItemSongPath = new ArrayList<String>();

    public MainActivity mContext;

    int resource;

    private static String fileName;

    private ProgressDialog pDialog;

    public DownloadAdapter(MainActivity context, int resource, ArrayList<String> list, ArrayList<String> listImage, ArrayList<String> listSongs)
    {
        super(context, resource);
        mContext = context;
        this.mItem = list;
        mItemImage = listImage;
        mItemSongPath = listSongs;
        this.resource = resource;
        layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Log.d("Adapter Call","Size: "+mItem.size());
    }

    public int getCount() {
        return mItem.size();
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        View rootView = convertView;

        rootView = layoutInflater.inflate(R.layout.downloads_list_item,null, true);

        CircularImageView cat_image = (CircularImageView) rootView.findViewById(R.id.image_download);

        ImageButton play = (ImageButton) rootView.findViewById(R.id.play_download);

        TextView tv_title = (TextView) rootView.findViewById(R.id.tv_title_download);

        tv_title.setTypeface(mContext.getTypeFace());

        tv_title.setText(mItem.get(position));

        try
        {
            Bitmap bitmap = BitmapFactory.decodeFile( mItemImage.get(position));

            BitmapDrawable d = new BitmapDrawable(bitmap);
            cat_image.setImageBitmap(bitmap);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Log.d("SONG_PATH",""+mItemSongPath.get(position));

                Fragment streaming = new Streaming();

                Bundle bundle = new Bundle();
                bundle.putInt("mode", 1);
                bundle.putSerializable("item_file", mItemSongPath);
                bundle.putSerializable("item_name", mItem);
                bundle.putSerializable("item_image", mItemImage);
                bundle.putInt("position", position);

                streaming.setArguments(bundle);
                mContext.ReplaceFragement(streaming);
            }
        });


        return  rootView;
    }
}