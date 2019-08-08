package com.example.groupmedia.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.example.groupmedia.R;


public class CreateFragment extends Fragment {

    LottieAnimationView mLottie;
    ImageView userImage;
    TextView userName;
    Context context;
    ProgressBar progressBar;

    public static final String PREFS_NAME = "LoginPrefs";

    public CreateFragment()
    {

    }

    public CreateFragment(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create, container, false);
        userImage = view.findViewById(R.id.userPic);
        userName = view.findViewById(R.id.ownerName);
        mLottie = view.findViewById(R.id.creatingAnmation);
        progressBar = view.findViewById(R.id.progressBarr);

        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String name = settings.getString("UserName","");
        String pic = settings.getString("UserImage","");

        Uri uri = Uri.parse(pic);
        userImage.setImageURI(uri);
        userName.setText(name);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            mLottie.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            if (mLottie != null) {
                mLottie.playAnimation();
            }
        }

    }

}
