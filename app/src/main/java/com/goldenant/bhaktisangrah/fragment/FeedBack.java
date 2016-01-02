package com.goldenant.bhaktisangrah.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.goldenant.bhaktisangrah.R;

/**
 * Created by ankita on 1/2/2016.
 */
public class FeedBack extends Fragment
{
    EditText editText_name,editText_mobile,editText_dis;

    Button button_submit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.feedback_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        editText_name = (EditText) view.findViewById(R.id.editText_name);
        editText_mobile = (EditText) view.findViewById(R.id.editText_mobile);
        editText_dis = (EditText) view.findViewById(R.id.editText_dis);

        button_submit = (Button) view.findViewById(R.id.button_submit);
    }
}
