package com.example.groupmedia.adapters;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groupmedia.AudioActivity;
import com.example.groupmedia.R;
import com.example.groupmedia.modelClass.ModelDataClass;
import com.flipboard.bottomsheet.commons.BottomSheetFragment;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.Payload;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.MyViewHolder>  {

    List<ModelDataClass> modelDataClassList;
    Context context;
    List<String> connectedDevices;

    public SongsAdapter(Context context, List<ModelDataClass> modelDataClassList, List<String> connectedDevices) {
        this.modelDataClassList = modelDataClassList;
        this.context = context;
        this.connectedDevices = connectedDevices;
    }


    View view;

    @NonNull
    @Override
    public SongsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        view = inflater.inflate(R.layout.songs_list, parent, false);
        SongsAdapter.MyViewHolder myViewHolder = new SongsAdapter.MyViewHolder(view);
        return myViewHolder;
    }

    public void sendMp3(String path) throws IOException {

        File file = new File(path);
        Uri uri = Uri.parse(path);

        Payload streamPayload = Payload.fromFile(file);
        //getting the name of file
        String filenameMessage = streamPayload.getId() + "/" + uri.getLastPathSegment();
        Payload filenameBytesPayload = Payload.fromBytes(filenameMessage.getBytes(StandardCharsets.UTF_8));
        Activity act = (AudioActivity) context;
        ((AudioActivity) act).sentId = streamPayload.getId();
        ((AudioActivity) act).outgoingidpath.put(streamPayload.getId(), path);
        //Sending the file name and id
        Nearby.getConnectionsClient(context).sendPayload(connectedDevices, filenameBytesPayload);
        //sending the complete file
        Nearby.getConnectionsClient(context).sendPayload(connectedDevices, streamPayload);
    }


    @Override
    public void onBindViewHolder(@NonNull SongsAdapter.MyViewHolder holder, int position) {
        ModelDataClass modelDataClass = modelDataClassList.get(position);
        holder.songName.setText(modelDataClass.getTitle() + "." + modelDataClass.getType());
        String path = modelDataClass.getPath();
        holder.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity act = (AudioActivity) context;
                try {
                    ((AudioActivity) act).resetMedia();
                    String resetPayload = "reset/media";
                    Payload payload = Payload.fromBytes(resetPayload.getBytes(StandardCharsets.UTF_8));
                    Nearby.getConnectionsClient(context).sendPayload(connectedDevices, payload);
                    sendMp3(path);
                    ((AudioActivity) act).firstTime = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return modelDataClassList.size();
    }


        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView songName;
            CardView cardView;
            ImageView send;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                songName = itemView.findViewById(R.id.song_name);
                cardView = itemView.findViewById(R.id.cardView);
                send = itemView.findViewById(R.id.sendMp3Button);
            }
        }
}