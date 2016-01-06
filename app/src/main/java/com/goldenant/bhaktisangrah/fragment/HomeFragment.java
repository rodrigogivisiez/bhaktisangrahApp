package com.goldenant.bhaktisangrah.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.goldenant.bhaktisangrah.MainActivity;
import com.goldenant.bhaktisangrah.R;
import com.goldenant.bhaktisangrah.adapter.HomeAdapter;
import com.goldenant.bhaktisangrah.common.ui.MasterActivity;
import com.goldenant.bhaktisangrah.common.ui.MasterFragment;
import com.goldenant.bhaktisangrah.common.util.Constants;
import com.goldenant.bhaktisangrah.common.util.NetworkRequest;
import com.goldenant.bhaktisangrah.common.util.ToastUtil;
import com.goldenant.bhaktisangrah.model.HomeModel;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Ajay on 11-09-2015.
 */
public class HomeFragment extends MasterFragment {

    MainActivity mContext;

    ListView listView_category;

    ArrayList<HomeModel> CatArray = new ArrayList<HomeModel>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        mContext = (MainActivity) getMasterActivity();

        View V = inflater.inflate(R.layout.home_fragment, container, false);
        return V;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContext.setTitle("Categories");

        listView_category = (ListView) view.findViewById(R.id.listView_category);

        AdView mAdView = (AdView) view.findViewById(R.id.adView_home);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        if(MasterActivity.CatArray.size() > 0)
        {
            HomeAdapter Adapter = new HomeAdapter(mContext,R.layout.category_item, MasterActivity.CatArray);
            listView_category.setAdapter(Adapter);
        }
        else
        {
            if(mContext.isInternet == true)
            {
                getCategory();
            }
            else
            {
                ToastUtil.showLongToastMessage(mContext,"No internet connection found");
            }
        }

        listView_category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub

                if(MasterActivity.CatArray.size() > 0)
                {
                    Fragment investProgramDetail = new CategoryList();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("CAT_ID", MasterActivity.CatArray.get(position));
                    investProgramDetail.setArguments(bundle);
                    mContext.ReplaceFragement(investProgramDetail);
                }else{
                    Fragment investProgramDetail = new CategoryList();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("CAT_ID", CatArray.get(position));
                    investProgramDetail.setArguments(bundle);
                    mContext.ReplaceFragement(investProgramDetail);
                }
            }
        });
    }

    private void getCategory()
    {
        mContext.showWaitIndicator(true);
        NetworkRequest dishRequest = new NetworkRequest(getMasterActivity());
        dishRequest.sendRequest(Constants.API_GET_CATEGORY_URL,
                null, catCallback);
    }

    NetworkRequest.NetworkRequestCallback catCallback = new NetworkRequest.NetworkRequestCallback()
    {
        @Override
        public void OnNetworkResponseReceived(JSONObject response)
        {
            Log.d("CATEGORY_API ", "" + response.toString());
            mContext.showWaitIndicator(false);
            try
            {
                if (response != null)
                {
                    JSONObject jObject = new JSONObject(response.toString());
                    String status = jObject.getString("status");

                    CatArray = new ArrayList<HomeModel>();

                    JSONArray data = jObject.getJSONArray("data");

                    Log.d("data.length()",""+data.length());

                    for (int i = 0; i < data.length(); i++)
                    {
                        HomeModel home = new HomeModel();

                        home.setCategory_id(data.getJSONObject(i).getString(Constants.CATEGORY_ID));
                        home.setCategory_image(data.getJSONObject(i).getString(Constants.CATEGORY_IMAGE));
                        home.setCategory_name(data.getJSONObject(i).getString(Constants.CATEGORY_NAME));

                        CatArray.add(home);
                    }

                    MasterActivity.CatArray = CatArray;

                    HomeAdapter Adapter = new HomeAdapter(mContext,R.layout.category_item, CatArray);
                    listView_category.setAdapter(Adapter);
                }
            } catch (Exception e) {
                e.printStackTrace();
                mContext.showWaitIndicator(false);
            }
        }

        @Override
        public void OnNetworkErrorReceived(String error) {
            mContext.showWaitIndicator(false);
        }
    };

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {

            private boolean doubleBackToExitPressedOnce;

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP
                        && keyCode == KeyEvent.KEYCODE_BACK) {

                    if (doubleBackToExitPressedOnce) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        System.exit(1);
                    }

                    this.doubleBackToExitPressedOnce = true;
                    ToastUtil.showLongToastMessage(mContext,getString(R.string.exit_title));

                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            doubleBackToExitPressedOnce = false;
                        }
                    }, 2000);

                    return true;
                }
                return false;
            }
        });
    }
}
