package com.goldenant.bhaktisangrah.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.goldenant.bhaktisangrah.MainActivity;
import com.goldenant.bhaktisangrah.R;
import com.goldenant.bhaktisangrah.common.ui.CircularImageView;
import com.goldenant.bhaktisangrah.common.ui.MasterActivity;
import com.goldenant.bhaktisangrah.fragment.CategoryList;
import com.goldenant.bhaktisangrah.fragment.Streaming;
import com.goldenant.bhaktisangrah.model.HomeModel;
import com.goldenant.bhaktisangrah.model.SubCategoryModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Adite on 03-01-2016.
 */
public class CategoryAdapter extends ArrayAdapter<HomeModel>
{
    private LayoutInflater layoutInflater;

    public ArrayList<SubCategoryModel> mItem = new ArrayList<SubCategoryModel>();

    public MainActivity mContext;

    int resource;

    public CategoryAdapter(MainActivity context, int resource,ArrayList<SubCategoryModel> list)
    {
        super(context, resource);
        mContext = context;
        this.mItem = list;
        this.resource = resource;
        layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

        rootView = layoutInflater.inflate(R.layout.subcategory_list_item,null, true);

        CircularImageView cat_image = (CircularImageView) rootView.findViewById(R.id.image);
        ImageButton play = (ImageButton) rootView.findViewById(R.id.play);

        TextView tv_title = (TextView) rootView.findViewById(R.id.tv_title);
        TextView tv_desc = (TextView) rootView.findViewById(R.id.tv_desc);
        TextView tv_duration = (TextView) rootView.findViewById(R.id.tv_duration);

        tv_title.setTypeface(mContext.getTypeFace());
        tv_desc.setTypeface(mContext.getTypeFace());
        tv_duration.setTypeface(mContext.getTypeFace());

        tv_title.setText(mItem.get(position).getItem_name());
        tv_desc.setText(mItem.get(position).getItem_description());
//        tv_duration.setText(mItem.get(position).getIte);

        Picasso.with(mContext).load(mItem.get(position).getItem_image()).into(cat_image);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment investProgramDetail = new Streaming();

                Bundle bundle = new Bundle();
                bundle.putString("item_file", mItem.get(position).getItem_file());
                investProgramDetail.setArguments(bundle);
                mContext.ReplaceFragement(investProgramDetail);
            }
        });

        return  rootView;

    }
}