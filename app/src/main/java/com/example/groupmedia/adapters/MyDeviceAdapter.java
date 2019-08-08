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
import com.example.groupmedia.fragments.JoinGroupFragment;
import com.google.android.gms.nearby.Nearby;
import java.util.HashMap;
import java.util.List;

public class MyDeviceAdapter extends RecyclerView.Adapter<MyDeviceAdapter.MyViewHolder>{

    List<String> userList;
    HashMap<String,String> hashMap;
    Context context;
    AudioActivity audioActivity;
    static JoinGroupFragment joinGroupFragment;

    public MyDeviceAdapter(List<String> data, HashMap<String, String> hashMap, Context context)
    {
        userList = data;
        this.hashMap = hashMap;
        this.context = context;
        this.audioActivity=(AudioActivity) context;
    }

    public static void dissmissDialog(JoinGroupFragment dialogFragment)
    {
        joinGroupFragment = dialogFragment;
    }

    @NonNull
    @Override
    public MyDeviceAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.device_available_list,parent,false);
        MyDeviceAdapter.MyViewHolder viewHolder = new MyDeviceAdapter.MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyDeviceAdapter.MyViewHolder holder,final int position) {
        final String str = userList.get(position);
        holder.listItems.setText(str);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Nearby.getConnectionsClient(context)
                        .requestConnection(context.getPackageName(), audioActivity.hashMap.get(str), audioActivity.connectionLifecycleCallback)
                        .addOnSuccessListener(
                                (Void unused) -> {
                                    Toast.makeText(context,"Connection request sent",Toast.LENGTH_LONG).show();
                                    audioActivity.isReceiver=true;
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