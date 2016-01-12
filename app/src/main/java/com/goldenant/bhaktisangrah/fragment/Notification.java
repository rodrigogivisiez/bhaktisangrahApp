package com.goldenant.bhaktisangrah.fragment;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.goldenant.bhaktisangrah.MainActivity;
import com.goldenant.bhaktisangrah.R;
import com.goldenant.bhaktisangrah.adapter.NotificationAdapter;
import com.goldenant.bhaktisangrah.common.ui.MasterFragment;
import com.goldenant.bhaktisangrah.common.util.DatabaseHelper;
import com.goldenant.bhaktisangrah.model.NotificationRecord;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by ankita on 1/2/2016.
 */
public class Notification extends MasterFragment
{
    MainActivity mContext;

    ListView listView_notification;

    ArrayList<NotificationRecord> notif = new ArrayList<NotificationRecord>();

    NotificationAdapter adapter;

    DatabaseHelper mDbHelper, dbAdapters;
    SQLiteDatabase mDb;
    static int version_val = 1;

    LinearLayout ll_nodata;

    TextView text_nonotifiction;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        mContext =  (MainActivity) getMasterActivity();
        return inflater.inflate(R.layout.notification_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        mContext.hideDrawer();
        mContext.showDrawerBack();
        mContext.setTitle("Notification");

        listView_notification = (ListView) view.findViewById(R.id.listView_notification);

        ll_nodata = (LinearLayout) view.findViewById(R.id.ll_nodata);

        text_nonotifiction = (TextView) view.findViewById(R.id.text_nonotifiction);
        text_nonotifiction.setTypeface(mContext.getTypeFace());

        mDbHelper = new DatabaseHelper(getMasterActivity(), "Database.sqlite", null,version_val);
        dbAdapters = DatabaseHelper.getDBAdapterInstance(getMasterActivity());

        try
        {
            dbAdapters.createDataBase();

            dbAdapters.openDataBase();
            notif = dbAdapters.Select_Record();
            dbAdapters.close();


            if(notif.size() > 0)
            {
                ll_nodata.setVisibility(View.GONE);
                listView_notification.setVisibility(View.VISIBLE);

                adapter = new NotificationAdapter(mContext,R.layout.notification_list_item, notif);
                listView_notification.setAdapter(adapter);
            }
            else
            {
                listView_notification.setVisibility(View.GONE);
                ll_nodata.setVisibility(View.VISIBLE);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        mContext.drawer_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragment home = new HomeFragment();
                mContext.ReplaceFragement(home);
            }
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK)
                {
                    HomeFragment home = new HomeFragment();
                    mContext.ReplaceFragement(home);
                }
                return false;
            }
        });
    }
}
