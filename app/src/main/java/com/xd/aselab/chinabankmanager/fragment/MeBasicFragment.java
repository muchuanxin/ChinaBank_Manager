package com.xd.aselab.chinabankmanager.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.my.BaseMyAllPerformanceActivity;
import com.xd.aselab.chinabankmanager.my.PersonalInfo;
import com.xd.aselab.chinabankmanager.util.CircleImageView;
import com.xd.aselab.chinabankmanager.util.Constants;
import com.xd.aselab.chinabankmanager.util.ImageLoader;
import com.xd.aselab.chinabankmanager.util.SharePreferenceUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeBasicFragment extends Fragment {

    private View root;
    private SharePreferenceUtil spu;
    private RelativeLayout back;
   // private CircleImageView iv_head_photo;
    private CircleImageView iv_head_photo;
    private TextView tv_user_name;
    private RelativeLayout rl_my_info;
    private RelativeLayout rl_my_performance;
    private RelativeLayout rl_my_Information;
    private ImageLoader imageLoader;

    public MeBasicFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_me_me, container, false);
        imageLoader = ImageLoader.getInstance();

        //    iv_head_photo = (CircleImageView)root.findViewById(R.id.manager_my_head);
        iv_head_photo = (CircleImageView) root.findViewById(R.id.manager_my_head);
        imageLoader.loadBitmap(getActivity(),spu.getPhotoUrl(),iv_head_photo, R.drawable.portrait);

        tv_user_name = (TextView) root.findViewById(R.id.user_name);
        tv_user_name.setText(spu.getName());

        rl_my_info = (RelativeLayout)root.findViewById(R.id.manager_my_info);
        rl_my_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(),PersonalInfo.class);
                startActivityForResult(intent, Constants.MAIN_ME_TO_INFO);
            }
        });

        rl_my_performance = (RelativeLayout)root.findViewById(R.id.manager_my_performance);
        rl_my_performance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(),BaseMyAllPerformanceActivity.class);
                intent.putExtra("account",spu.getAccount());
                intent.putExtra("type","1");
                startActivity(intent);
            }
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        imageLoader.loadBitmap(getActivity(), spu.getPhotoUrl(), iv_head_photo, R.drawable.default_head);
    }
}
