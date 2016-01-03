package com.goldenant.bhaktisangrah.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.goldenant.bhaktisangrah.R;
import com.goldenant.bhaktisangrah.common.ui.MasterActivity;
import com.goldenant.bhaktisangrah.model.HomeModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by ankita on 1/2/2016.
 */
public class HomeAdapter extends ArrayAdapter<HomeModel>
{
    private LayoutInflater layoutInflater;

    public ArrayList<HomeModel> mItem = new ArrayList<HomeModel>();

    public MasterActivity mContext;

    int resource;

    public HomeAdapter(MasterActivity context, int resource,ArrayList<HomeModel> list)
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
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View rootView = convertView;

        rootView = layoutInflater.inflate(R.layout.category_item,null, true);

        ImageView imageView_cat = (ImageView) rootView.findViewById(R.id.imageView_cat);

        TextView text_cat_name = (TextView) rootView.findViewById(R.id.text_cat_name);

        text_cat_name.setTypeface(mContext.getTypeFace());
        text_cat_name.setText(mItem.get(position).getCategory_name());

        Picasso.with(mContext).load(mItem.get(position).getCategory_image()).into(imageView_cat);

        return  rootView;

    }
}
