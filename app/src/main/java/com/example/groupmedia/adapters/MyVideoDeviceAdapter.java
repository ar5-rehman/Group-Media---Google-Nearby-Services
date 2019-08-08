package com.example.groupmedia.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groupmedia.AudioActivity;
import com.example.groupmedia.R;
import com.example.groupmedia.VideoActivity;
import com.example.groupmedia.fragments.JoinGroupFragment;
import com.example.groupmedia.fragments.VideoJoinGroupFragment;
import com.google.android.gms.nearby.Nearby;
import java.util.HashMap;
import java.util.List;

public class MyVideoDeviceAdapter extends RecyclerView.Adapter<MyVideoDeviceAdapter.MyViewHolder>{

    List<String> userList;
    HashMap<String,String> hashMap;
    Context context;
    VideoActivity videoActivity;
    static VideoJoinGroupFragment joinGroupFragment;

    public MyVideoDeviceAdapter(List<String> data, HashMap<String, String> hashMap, Context context)
    {
        userList = data;
        this.hashMap = hashMap;
        this.context = context;
        this.videoActivity=(VideoActivity) context;
    }

    public static void dissmissDialog(VideoJoinGroupFragment dialogFragment)
    {
        joinGroupFragment = dialogFragment;
    }

    @NonNull
    @Override
    public MyVideoDeviceAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.device_available_list,parent,false);
        MyVideoDeviceAdapter.MyViewHolder viewHolder = new MyVideoDeviceAdapter.MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyVideoDeviceAdapter.MyViewHolder holder,final int position) {
        final String str = userList.get(position);
        holder.listItems.setText(str);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Nearby.getConnectionsClient(context)
                        .requestConnection(context.getPackageName(), videoActivity.hashMap.get(str), videoActivity.connectionLifecycleCallback)
                        .addOnSuccessListener(
                                (Void unused) -> {
                                    Toast.makeText(context,"Connection request sent",Toast.LENGTH_LONG).show();
                                    videoActivity.isReceiver=true;
                                    joinGroupFragment.dismiss();

                                })
                        .addOnFailureListener(
                                (Exception e) -> {
                                    Toast.makeText(context,"Could not sent the request",Toast.LENGTH_LONG).show();
                                });

            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView listItems;
        CardView cardView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            listItems = itemView.findViewById(R.id.devices_list);
            cardView = itemView.findViewById(R.id.card);
        }
    }
}