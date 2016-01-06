package com.goldenant.bhaktisangrah.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.goldenant.bhaktisangrah.R;
import com.goldenant.bhaktisangrah.common.util.DatabaseHelper;
import com.goldenant.bhaktisangrah.model.NotificationRecord;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends ArrayAdapter<NotificationRecord>
{
	int resource;
	private LayoutInflater layoutInflater;
	public static List<NotificationRecord> mItems;
	public Context mContext;

	DatabaseHelper mDbHelper, dbAdapters;
	SQLiteDatabase mDb;
	static int version_val = 1;

	public NotificationAdapter(Context context, int notificationItems,ArrayList<NotificationRecord> temp) 
	{
		// TODO Auto-generated constructor stub
		super(context, notificationItems, temp);
		mContext = context;
		mItems = temp;
		resource = notificationItems;
		layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mDbHelper = new DatabaseHelper(mContext, "Database.sqlite", null,version_val);
		dbAdapters = DatabaseHelper.getDBAdapterInstance(mContext);
	}

	public int getCount() 
	{
		return mItems.size();
	}

	public long getItemId(int position) 
	{
		return position;
	}

	@SuppressLint("ViewHolder")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		View rootView = convertView;

		rootView = layoutInflater.inflate(R.layout.notification_list_item, null,true);

		TextView text_title = (TextView) rootView.findViewById(R.id.text_content);
		TextView text_time = (TextView) rootView.findViewById(R.id.text_date);

		ImageButton clear = (ImageButton) rootView.findViewById(R.id.imageButton_clear);

		text_title.setText(mItems.get(position).getContent());
		text_time.setText(mItems.get(position).getDate());

		clear.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					dbAdapters.createDataBase();

					dbAdapters.openDataBase();
					dbAdapters.delete_record(mItems.get(position).getId());
					dbAdapters.close();

				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});

		return rootView;
	}
}