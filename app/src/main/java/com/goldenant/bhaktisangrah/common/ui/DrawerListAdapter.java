package com.goldenant.bhaktisangrah.common.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.goldenant.bhaktisangrah.R;
import com.goldenant.bhaktisangrah.model.NavDrawerItem;

import java.util.ArrayList;

@SuppressLint({ "ViewHolder", "InflateParams", "ResourceAsColor" })
public class DrawerListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<NavDrawerItem> navDrawerItems;
//    Typeface font;

	public DrawerListAdapter(Context context,
			ArrayList<NavDrawerItem> navDrawerItems) {
		this.context = context;
		this.navDrawerItems = navDrawerItems;
//        this.font = Typeface.createFromAsset(this.context.getAssets(), "SHRUTI.TTF");
	}

	@Override
	public int getCount() {
		return navDrawerItems.size();
	}

	@Override
	public Object getItem(int position) {
		return navDrawerItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		convertView = mInflater.inflate(R.layout.drawer_list_item, null, true);

		TextView txtTitle = (TextView) convertView.findViewById(R.id.text);
		ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
		RelativeLayout drawerItem = (RelativeLayout) convertView
				.findViewById(R.id.drawerLayout);

		if (navDrawerItems.get(position).getTitle().equalsIgnoreCase("Login")) {
			drawerItem.setBackgroundResource(R.color.header);
		}

//        txtTitle.setTypeface(font);
		txtTitle.setText(navDrawerItems.get(position).getTitle());
		imageView.setImageResource(navDrawerItems.get(position).getIcon());
		return convertView;
	}
}