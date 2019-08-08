package com.example.groupmedia.fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.groupmedia.AudioActivity;
import com.example.groupmedia.R;
import com.example.groupmedia.adapters.MyDeviceAdapter;


public class JoinGroupFragment extends DialogFragment {

    RecyclerView recyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager layoutManager;
    LottieAnimationView mLottie;
    ProgressBar progressBar;

    public JoinGroupFragment(MyDeviceAdapter myDevicesAdapter) {
        mAdapter = myDevicesAdapter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String device = AudioActivity.deviceName;

        if (device != null) {
            Toast.makeText(getContext(), "device name is" + device, Toast.LENGTH_LONG).show();
        } else {

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_join_group, container, false);
        mLottie = view.findViewById(R.id.intro_lottie_animation_view);
        recyclerView = view.findViewById(R.id.my_recycler_view_devices);
        progressBar = view.findViewById(R.id.progressBar);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
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

    @Override
    public void onStop() {
        super.onStop();
    }
}