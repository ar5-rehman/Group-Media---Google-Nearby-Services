package com.example.groupmedia.fragments;


import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.groupmedia.AudioActivity;
import com.example.groupmedia.R;
import com.example.groupmedia.adapters.MyAdapter;

public class UsersDialogFragment extends DialogFragment {

    RecyclerView recyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager layoutManager;

    public UsersDialogFragment()
    {

    }

    public UsersDialogFragment(MyAdapter myAdapter) {
        this.mAdapter = myAdapter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String devices = AudioActivity.userName;
        if(devices==null) {
            Toast.makeText(getActivity(),"No devices are connected!",Toast.LENGTH_SHORT).show();
            return;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(AudioActivity.userName!=null) {
            View view = inflater.inflate(R.layout.fragment_users_dialog, container, false);
            recyclerView = view.findViewById(R.id.my_recycler_view);
            recyclerView.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(mAdapter);
            return view;
        }
        return null;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
