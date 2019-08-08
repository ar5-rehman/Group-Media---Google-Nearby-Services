package com.example.groupmedia.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groupmedia.R;
import com.example.groupmedia.fragments.UsersDialogFragment;
import com.example.groupmedia.fragments.VideoUsersDialogFragment;
import com.google.android.gms.nearby.Nearby;
import java.util.List;

public class MyVideoAdapter extends RecyclerView.Adapter<MyVideoAdapter.MyViewHolder> {

    List<String> userList;
    View view;
    Context context;
    static VideoUsersDialogFragment usersDialogFragment;

    public MyVideoAdapter(Context context,List<String> device)
    {
        this.context = context;
        userList = device;
    }

    static public void dismissDialog(VideoUsersDialogFragment dialogFragment)
    {
        usersDialogFragment = dialogFragment;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        view = layoutInflater.inflate(R.layout.joined_users_list,parent,false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.listItems.setText(userList.get(position));
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Nearby.getConnectionsClient(context).disconnectFromEndpoint(userList.get(position));
                userList.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
                notifyItemRangeChanged(holder.getAdapterPosition(), userList.size());
                usersDialogFragment.dismiss();
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return userList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView listItems;
        ImageView remove;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            listItems = itemView.findViewById(R.id.list_item);
            remove = itemView.findViewById(R.id.remove_item);
        }
    }
}