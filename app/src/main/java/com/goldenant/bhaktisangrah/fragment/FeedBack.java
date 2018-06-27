package com.goldenant.bhaktisangrah.fragment;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.goldenant.bhaktisangrah.MainActivity;
import com.goldenant.bhaktisangrah.R;
import com.goldenant.bhaktisangrah.common.ui.MasterFragment;
import com.goldenant.bhaktisangrah.common.util.ToastUtil;

/**
 * Created by Jaydeep Jikadra on 1/2/2018.
 */
public class FeedBack extends MasterFragment
{
    EditText editText_name,editText_mobile,editText_dis;

    Button button_submit;

    MainActivity mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        mContext = (MainActivity) getMasterActivity();
        return inflater.inflate(R.layout.feedback_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        mContext.hideDrawer();
        mContext.showDrawerBack();

        mContext.setTitle("Feedback");

        editText_name = (EditText) view.findViewById(R.id.editText_name);
        editText_mobile = (EditText) view.findViewById(R.id.editText_mobile);
        editText_dis = (EditText) view.findViewById(R.id.editText_dis);

        button_submit = (Button) view.findViewById(R.id.button_submit);

        editText_name.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    //check if the right key was pressed
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        onResume();
                        return true;
                    }
                }
                return false;
            }
        });

        editText_mobile.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    //check if the right key was pressed
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        onResume();
                        return true;
                    }
                }
                return false;
            }
        });

        editText_dis.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    //check if the right key was pressed
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        onResume();
                        return true;
                    }
                }
                return false;
            }
        });

        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText_name.getText().toString().length() == 0) {
                    ToastUtil.showLongToastMessage(mContext, "Please enter your name");
                } else if (editText_mobile.getText().toString().length() == 0) {
                    ToastUtil.showLongToastMessage(mContext, "Please enter your mobile number");
                } else if (editText_dis.getText().toString().length() == 0) {
                    ToastUtil.showLongToastMessage(mContext, "Please enter proper description");
                } else {
                    mContext.feedback(mContext, editText_name.getText().toString(), editText_mobile.getText().toString(),
                            editText_dis.getText().toString());
                }
            }
        });

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
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    HomeFragment home = new HomeFragment();
                    mContext.ReplaceFragement(home);
                }
                return false;
            }
        });
    }
}
