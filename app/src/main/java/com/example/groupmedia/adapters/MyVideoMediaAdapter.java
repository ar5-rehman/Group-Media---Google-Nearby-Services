package com.example.groupmedia.adapters;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.groupmedia.MediaPlayerActivity;
import com.example.groupmedia.R;
import com.example.groupmedia.modelClass.ModelDataClass;
import com.example.groupmedia.modelClass.SongsModel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyVideoMediaAdapter extends RecyclerView.Adapter<MyVideoMediaAdapter.Holder> implements Filterable {

    Context context;
    List<SongsModel> list;
    MediaPlayer mediaPlayer;
    MediaPlayerActivity mediaPlayerActivity;
    List<SongsModel> songList;

    public MyVideoMediaAdapter(Context context, List<SongsModel> allMusicTracks,MediaPlayer mediaPlayer) {
        this.context = context;
        this.list = allMusicTracks;
        this.songList = allMusicTracks;
        this.mediaPlayer = mediaPlayer;
        mediaPlayerActivity =(MediaPlayerActivity) context;
    }

    @NonNull
    @Override
    public MyVideoMediaAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.song_layout,parent,false);
        Holder holder = new Holder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyVideoMediaAdapter.Holder holder, int position) {
        final SongsModel songsModel = songList.get(position);
        String name = songsModel.getSongTitle();
        String size = songsModel.getSongSize();
        holder.songName.setText(name);
        holder.songSize.setText(size);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mediaPlayerActivity.constraintLayout.setVisibility(View.VISIBLE);
                    mediaPlayer.reset();
                    String path = songsModel.getSongPath();
                    mediaPlayer.setDataSource(path);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.start();
                mediaPlayerActivity.play.setImageResource(R.drawable.pause);
                String name = songsModel.getSongTitle();
                mediaPlayerActivity.song.setText(name);
                String size = songsModel.getSongSize();
                mediaPlayerActivity.size.setText(size);
            }
        });
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    class Holder extends RecyclerView.ViewHolder {

        TextView songName,songSize;
        CardView cardView;


        public Holder(@NonNull View itemView) {
            super(itemView);
            songName = itemView.findViewById(R.id.songName);
            songSize = itemView.findViewById(R.id.songSize);
            cardView = itemView.findViewById(R.id.songCardView);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();

                if (charString.isEmpty()) {
                    songList = list;
                } else {
                    List<SongsModel> filteredList = new ArrayList<>();
                    for (SongsModel row : list) {
                        if (row.getSongTitle().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    songList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = songList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                songList = (ArrayList<SongsModel>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
