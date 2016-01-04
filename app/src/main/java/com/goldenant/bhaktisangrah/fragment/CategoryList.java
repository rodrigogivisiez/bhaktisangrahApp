package com.goldenant.bhaktisangrah.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.goldenant.bhaktisangrah.MainActivity;
import com.goldenant.bhaktisangrah.R;
import com.goldenant.bhaktisangrah.adapter.CategoryAdapter;
import com.goldenant.bhaktisangrah.adapter.HomeAdapter;
import com.goldenant.bhaktisangrah.common.ui.MasterActivity;
import com.goldenant.bhaktisangrah.common.ui.MasterFragment;
import com.goldenant.bhaktisangrah.common.util.Constants;
import com.goldenant.bhaktisangrah.common.util.InternetStatus;
import com.goldenant.bhaktisangrah.common.util.NetworkRequest;
import com.goldenant.bhaktisangrah.model.HomeModel;
import com.goldenant.bhaktisangrah.model.SubCategoryModel;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ankita on 1/2/2016.
 */
public class CategoryList extends MasterFragment
{
    MainActivity mContext;
    ArrayList<SubCategoryModel> CatListItem = new ArrayList<SubCategoryModel>();
    HomeModel homeModel = new HomeModel();
    private ListView mCategoryList;
    private String category_id;
    private Bundle bundle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        mContext = (MainActivity) getActivity();

        View V = inflater.inflate(R.layout.category_list_fragment, container, false);
        return V;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContext.setTitle("Play songs");
        mCategoryList = (ListView) view.findViewById(R.id.listView_cat_list);
        bundle = getArguments();

        if(bundle != null){

            homeModel = (HomeModel) bundle.getSerializable("CAT_ID");
            category_id = homeModel.getCategory_id();

        }

        isInternet = new InternetStatus().isInternetOn(mContext);
        if(isInternet){

            getCategory();
        }
    }

    private void getCategory()
    {
        mContext.showWaitIndicator(true);

        NetworkRequest dishRequest = new NetworkRequest(getMasterActivity());
        List<NameValuePair> carData = new ArrayList<NameValuePair>(1);
        carData.add(new BasicNameValuePair(
                Constants.CATEGORY_ID, category_id));
        dishRequest.sendRequest(Constants.API_GET_ITEM_BY_CAT_ID_URL,
                carData, catCallback);
    }

    NetworkRequest.NetworkRequestCallback catCallback = new NetworkRequest.NetworkRequestCallback()
    {
        @Override
        public void OnNetworkResponseReceived(JSONObject response)
        {
            Log.d("SUBCATEGORY_API ", "" + response.toString());
            mContext.showWaitIndicator(false);
            try
            {
                if (response != null)
                {
                    JSONObject jObject = new JSONObject(response.toString());
                    String status = jObject.getString("status");

                    if(status.equalsIgnoreCase("1")){


                        CatListItem = new ArrayList<SubCategoryModel>();

                        JSONArray data = jObject.getJSONArray("data");

                        Log.d("data.length()",""+data.length());

                        for (int i = 0; i < data.length(); i++)
                        {
                            SubCategoryModel categoryModel = new SubCategoryModel();

                            categoryModel.setItem_id(data.getJSONObject(i).getString(Constants.item_id));
                            categoryModel.setItem_name(data.getJSONObject(i).getString(Constants.item_name));
                            categoryModel.setItem_description(data.getJSONObject(i).getString(Constants.item_description));
                            categoryModel.setItem_file(data.getJSONObject(i).getString(Constants.item_file));
                            categoryModel.setItem_image(data.getJSONObject(i).getString(Constants.item_image));

                            CatListItem.add(categoryModel);
                        }

                        CategoryAdapter Adapter = new CategoryAdapter(mContext,R.layout.category_item, CatListItem);
                        mCategoryList.setAdapter(Adapter);
                    }
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
}
