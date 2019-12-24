package com.example.groupmedia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.groupmedia.adapters.MediaAdapter;
import com.example.groupmedia.modelClass.SongsModel;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class MediaPlayerActivity extends AppCompatActivity implements View.OnClickListener {


    String songTitle, songPath, songLength, songSize;
    int numberOfSongs;
    MediaAdapter mediaAdapter;
    MediaPlayer mediaPlayer;
    public ImageView play, forward;
    public TextView song, size,channel;
    RecyclerView recyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager layoutManager;
    public RelativeLayout constraintLayout;
    String sizee;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);

        mediaPlayer = new MediaPlayer();
        play = findViewById(R.id.playButton);
        song = findViewById(R.id.song1);
        size = findViewById(R.id.size1);
        constraintLayout = findViewById(R.id.constraintLayout2);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        play.setOnClickListener(this);

        mediaAdapter = new MediaAdapter(MediaPlayerActivity.this, getAllMusicTracks(), mediaPlayer);
        mAdapter = mediaAdapter;

        recyclerView = findViewById(R.id.recycle);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
    }
    private SearchView searchView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater in = getMenuInflater();
        in.inflate(R.menu.menu_search,menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mediaAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mediaAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.menu.menu_search)
        {

        }
        return true;
    }

    List<SongsModel> getAllMusicTracks() {
        ArrayList<SongsModel> mTracks = new ArrayList<>();
        mTracks.clear();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String sortOrder = MediaStore.MediaColumns.DATE_MODIFIED + " DESC";
        Cursor cursor = MediaPlayerActivity.this.getContentResolver().query(
                uri, // Uri
                null,
                null,
                null,
                sortOrder);
        if (cursor == null) {


        } else if (!cursor.moveToFirst()) {

            Toast.makeText(this, "No Music Found on SD Card.", Toast.LENGTH_LONG);

        } else {

            int Title = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int size = cursor.getColumnIndex(MediaStore.Audio.Media.SIZE);
            int length = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int path = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

            do {
                songTitle = cursor.getString(Title);
                songPath = cursor.getString(path);
                songSize = cursor.getString(size);
                songLength = cursor.getString(length);
                numberOfSongs = cursor.getInt(length);
                sizee = getFileSize(songSize);
                mTracks.add(new SongsModel(songTitle, sizee, songPath));
            } while (cursor.moveToNext());
        }

        return mTracks;
    }


    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.playButton) {
            if (mediaPlayer.isPlaying()) {
                play.setImageResource(R.drawable.play1hdpi);
                mediaPlayer.pause();
            } else {
                play.setImageResource(R.drawable.pause);
                mediaPlayer.start();
            }
        }
    }

    public String getFileSize(String size) {
        String modifiedFileSize = null;
        double fileSize = 0.0;
        if (size!=null) {
            //fileSize = Double.longBitsToDouble();//in Bytes
            fileSize= Double.parseDouble(size);
            if (fileSize < 1024) {
                modifiedFileSize = String.valueOf(fileSize).concat("B");
            } else if (fileSize > 1024 && fileSize < (1024 * 1024))
            {
                modifiedFileSize = String.valueOf(Math.round((fileSize / 1024 * 100.0)) / 100.0).concat("KB");
            } else {
                modifiedFileSize = String.valueOf(Math.round((fileSize / (1024 * 1204) * 100.0)) / 100.0).concat("MB");
            }
        } else {
            modifiedFileSize = "Unknown";
        }
        return modifiedFileSize;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mediaPlayer.reset();
        Intent ii = new Intent(this,MainScreenActivity.class);
        startActivity(ii);
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
    }

}
