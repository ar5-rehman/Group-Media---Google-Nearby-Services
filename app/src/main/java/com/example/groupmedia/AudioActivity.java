package com.example.groupmedia;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.SimpleArrayMap;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.groupmedia.adapters.MyAdapter;
import com.example.groupmedia.adapters.MyDeviceAdapter;
import com.example.groupmedia.adapters.SongsAdapter;
import com.example.groupmedia.fragments.CreateFragment;
import com.example.groupmedia.fragments.ItemListDialogFragment;
import com.example.groupmedia.fragments.JoinGroupFragment;
import com.example.groupmedia.fragments.UsersDialogFragment;
import com.example.groupmedia.modelClass.ModelDataClass;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AudioActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    TextView connectedOrNot, songName;
    String id = null;
    public static String deviceName;
    MyDeviceAdapter myDevicesAdapter;
    public static String userName;
    public static String device_name;
    List<String> foundDevices = new ArrayList<>();
    List<String> connectedDevices = new ArrayList<>();
    public HashMap<String, String> hashMap;
    MyAdapter myAdapter;
    ImageView logout, play, next, prev, listofSongs, userList,artist;
    MediaPlayer mediaPlayer;
    Context context;
    File transferFile;
    SongsAdapter songsAdapter;
    String songTitle, songPath, songSize, songLength;
    float numberOfSongs;
    ItemListDialogFragment itemListDialogFragment;
    ProgressDialog progress;
    SeekBar seekBar;
    boolean oneTimeAdvertiser = false;
    CreateFragment createFragment;
    FragmentTransaction transaction;
    FragmentManager manager;
    public static final String PREFS_NAME = "LoginPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myDevicesAdapter = new MyDeviceAdapter(foundDevices, hashMap, this);
        if(oneTimeAdvertiser == false) {
            Intent receive = getIntent();
            Bundle e = receive.getExtras();
            String create = (String) e.get("create");
            if (create.equals("create") && create!=null) {
                startAdvertiser();
                createFragment = new CreateFragment(AudioActivity.this);
                manager = getSupportFragmentManager();
                transaction = manager.beginTransaction();
                transaction.add(R.id.linearLayout,createFragment,"Test Fragment");
                transaction.commit();
            } else if (create.equals("join")) {
                startDiscovery();
                JoinGroupFragment joinGroupFragment = new JoinGroupFragment(myDevicesAdapter);
                FragmentManager fm = getSupportFragmentManager();
                joinGroupFragment.show(fm, "join_fragment_tag");
                MyDeviceAdapter.dissmissDialog(joinGroupFragment);
            }else if(create.equals("go"))
            {

            }
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_audio);

        mediaPlayer = new MediaPlayer();
        seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);
        seekBar.setMax(100);

        listofSongs = findViewById(R.id.listofSongs);
        userList = findViewById(R.id.usersList);
        logout = findViewById(R.id.logout);
        artist = findViewById(R.id.artist);

        play = findViewById(R.id.play);
        connectedOrNot = findViewById(R.id.connectedOrNot);
        songName = findViewById(R.id.songName);
        next = findViewById(R.id.next);
        prev = findViewById(R.id.prev);

        context = getBaseContext();
        logout.setOnClickListener(this);
        play.setOnClickListener(this);
        next.setOnClickListener(this);
        prev.setOnClickListener(this);

        hashMap = new HashMap<>();

        progress = new ProgressDialog(AudioActivity.this);

        listofSongs.setOnClickListener(this);
        userList.setOnClickListener(this);

        disable();

        myAdapter = new MyAdapter(AudioActivity.this, connectedDevices);
        List<ModelDataClass>  ts = getAllMusicTracks();
        songsAdapter = new SongsAdapter(AudioActivity.this, ts, connectedDevices);

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    List<ModelDataClass> getAllMusicTracks() {
        ArrayList<ModelDataClass> mTracks = new ArrayList<>();
        mTracks.clear();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String sortOrder = MediaStore.MediaColumns.DATE_MODIFIED + " DESC";
        Cursor cursor = AudioActivity.this.getContentResolver().query(
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
            int type = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE);

            do {
                songTitle = cursor.getString(Title);
                songPath = cursor.getString(path);
                songSize = cursor.getString(size);
                songLength = cursor.getString(length);
                numberOfSongs = cursor.getInt(length);
                String type1 = cursor.getString(type);
                mTracks.add(new ModelDataClass(songTitle, songPath, type1));
            } while (cursor.moveToNext());
        }

        return mTracks;
    }

    public void resetMedia() {
        mediaPlayer.reset();
        mediaPlayer.release();
        mediaPlayer = null;
        mediaPlayer = new MediaPlayer();
        isStartOnce = false;
        play.setImageResource(R.drawable.play);
    }

    private final Handler payloadHandler = new Handler();

    private void sendingPayTask() {
        if (mediaPlayer.isPlaying()) {
            Runnable notification = new Runnable() {
                public void run() {
                    int seekToo = mediaPlayer.getCurrentPosition();
                    String str = "seekTo/" + seekToo;
                    Payload payloadNext1 = Payload.fromBytes(str.getBytes(StandardCharsets.UTF_8));
                    Nearby.getConnectionsClient(AudioActivity.this).sendPayload(connectedDevices, payloadNext1);
                    sendingPayTask();
                }
            };
            payloadHandler.postDelayed(notification, 2000);
        }
    }

    public void playMusicInGroup(String path) throws IOException {

        File file = new File(path);
        mediaPlayer.setDataSource(file.getAbsolutePath());
        mediaPlayer.prepare();
        mediaPlayer.start();
        mediaFileLengthInMilliseconds = mediaPlayer.getDuration();
        primarySeekBarProgressUpdater();
    }

    public boolean isReceiver = false;

    public void setGroupName(String groupName) {
        connectedOrNot.setVisibility(View.VISIBLE);
        connectedOrNot.setText("Connected to " + groupName);
    }


    private final SimpleArrayMap<Long, Payload> incomingFilePayloads = new SimpleArrayMap<>();
    private final SimpleArrayMap<String, String> filePayloadFilenames = new SimpleArrayMap<>();
    public final SimpleArrayMap<Long, String> outgoingidpath = new SimpleArrayMap<>();
    public final SimpleArrayMap<Long, Integer> incomingFilePayloadsType = new SimpleArrayMap<>();
    public long sentId = 10;
    public boolean firstTime = true;


    PayloadCallback payloadCallback = new PayloadCallback() {

        @Override
        public void onPayloadReceived(@NonNull String s, @NonNull Payload payload) {
            if (payload.getType() == Payload.Type.FILE) {
                incomingFilePayloads.put(payload.getId(), payload);
                incomingFilePayloadsType.put(payload.getId(), payload.getType());
                progress.setMessage("File receiving...");
                progress.setCancelable(false);
                progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progress.setIndeterminate(false);
                progress.setCanceledOnTouchOutside(false);
                progress.setProgress(0);
                progress.show();
            } else if (payload.getType() == Payload.Type.BYTES) {
                String payloadFilenameMessage = new String(payload.asBytes(), StandardCharsets.UTF_8);
                String[] parts = payloadFilenameMessage.split("/");
                String payloadId = parts[0];
                String filename = parts[1];
                filePayloadFilenames.put(payloadId, filename);
                if (parts[0].equals("play") && !parts[1].equals("again")) {
                    File file = Environment.getExternalStorageDirectory();
                    file = new File(file, "Download");
                    file = new File(file, "Nearby");
                    file = new File(file, parts[1]);
                    try {

                        playMusicInGroup(file.getAbsolutePath());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (parts[0].equals("pause")) {
                    mediaPlayer.pause();
                } else if (parts[0].equals("play") && parts[1].equals("again")) {
                    mediaPlayer.start();
                    primarySeekBarProgressUpdater();
                } else if (parts[0].equals("seekTo")) {
                    mediaPlayer.seekTo(Integer.parseInt(parts[1]) + 250);
                } else if (parts[0].equals("reset")) {
                    resetMedia();
                }
            }
        }

        @Override
        public void onPayloadTransferUpdate(@NonNull String s, @NonNull PayloadTransferUpdate
                update) {
            String id = String.valueOf(update.getPayloadId());
            if ((isReceiver) && (incomingFilePayloadsType.get(update.getPayloadId()) != null) && (incomingFilePayloadsType.get(update.getPayloadId()) == Payload.Type.FILE)) {
                float total = update.getTotalBytes();
                float received = update.getBytesTransferred();
                float factor = (float) received / total;
                float percent = (float) factor * 100;
                progress.setProgress((int) percent);
            }
            if (update.getPayloadId() == sentId) {
                if (firstTime) {
                    itemListDialogFragment.dismiss();
                    progress.setMessage("Mp3 file transferring...");
                    progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    progress.setIndeterminate(false);
                    progress.setCanceledOnTouchOutside(false);
                    progress.setCancelable(false);
                    progress.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Nearby.getConnectionsClient(AudioActivity.this).cancelPayload(update.getPayloadId()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    firstTime = true;
                                    dialog.dismiss();
                                    Toast.makeText(AudioActivity.this, "Transfer cancelled", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                    firstTime = false;
                    progress.show();
                } else {
                    float total = update.getTotalBytes();
                    float received = update.getBytesTransferred();
                    float factor = (float) received / total;
                    float percent = (float) factor * 100;
                    progress.setProgress((int) percent);

                }
            }
            if (update.getStatus() == PayloadTransferUpdate.Status.SUCCESS) {
                String filename = filePayloadFilenames.get(id);
                if (incomingFilePayloads.get(update.getPayloadId()) != null) {
                    Payload payload = incomingFilePayloads.get(update.getPayloadId());
                    File payloadFile = payload.asFile().asJavaFile();
                    transferFile = new File(payloadFile.getParentFile(), filename);
                    payloadFile.renameTo(transferFile);
                    Toast.makeText(AudioActivity.this, "File Received", Toast.LENGTH_SHORT).show();
                    songName.setVisibility(View.VISIBLE);
                    songName.setText(Uri.parse(filename).getLastPathSegment());
                    progress.dismiss();
                }

                if (update.getPayloadId() == sentId) {
                    firstTime = true;
                    progress.dismiss();
                    progress.setProgress(0);
                    Toast.makeText(AudioActivity.this, "Transfer completed", Toast.LENGTH_SHORT).show();
                }
            }

        }
    };

    public void disable() {
        seekBar.setEnabled(false);
        listofSongs.setClickable(false);
        userList.setClickable(false);
        next.setClickable(false);
        prev.setClickable(false);
        play.setClickable(false);
    }

    public void enable() {
        seekBar.setEnabled(true);
        listofSongs.setClickable(true);
        userList.setClickable(true);
        next.setClickable(true);
        prev.setClickable(true);
        play.setClickable(true);
    }

    public void invisible() {
        next.setVisibility(View.GONE);
        prev.setVisibility(View.GONE);
        play.setVisibility(View.GONE);
        userList.setVisibility(View.GONE);
        listofSongs.setVisibility(View.GONE);
    }

    public ConnectionLifecycleCallback connectionLifecycleCallback = new ConnectionLifecycleCallback() {
        @Override
        public void onConnectionInitiated(@NonNull final String endpointId, @NonNull ConnectionInfo connectionInfo) {
            if (isAdvertiser == true) {
                new AlertDialog.Builder(AudioActivity.this)
                        .setTitle("Accept connection to " + connectionInfo.getEndpointName())
                        .setMessage("Confirm the code matches on both devices: " + connectionInfo.getAuthenticationToken())
                        .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Nearby.getConnectionsClient(AudioActivity.this)
                                        .acceptConnection(endpointId, payloadCallback).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(AudioActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }).setNegativeButton(
                        android.R.string.cancel,
                        (DialogInterface dialog, int which) ->
                                Nearby.getConnectionsClient(AudioActivity.this).rejectConnection(endpointId))
                        .show();
            } else {
                Nearby.getConnectionsClient(AudioActivity.this)
                        .acceptConnection(endpointId, payloadCallback);
            }
        }

        @Override
        public void onConnectionResult(@NonNull String s, @NonNull ConnectionResolution result) {
            switch (result.getStatus().getStatusCode()) {
                case ConnectionsStatusCodes.STATUS_OK:
                    id = s;
                    userName = s;
                    connectedDevices.add(s);
                    device_name = "DeviceName/" + Build.DEVICE;
                    if (isReceiver == true) {
                        setGroupName(deviceName);
                        invisible();

                    } else {
                        enable();
                    }
                    Payload sendMessage = (Payload) Payload.fromBytes(device_name.getBytes(StandardCharsets.UTF_8));
                    Nearby.getConnectionsClient(AudioActivity.this).sendPayload(id, sendMessage);
                    Toast.makeText(getApplicationContext(), "Connection granted", Toast.LENGTH_LONG).show();
                    break;
                case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
                    Toast.makeText(getApplicationContext(), "Connection rejected", Toast.LENGTH_LONG).show();
                    break;
                case ConnectionsStatusCodes.STATUS_ERROR:
                    Toast.makeText(getApplicationContext(), "Error occurred", Toast.LENGTH_LONG).show();
                    break;
                default:
            }
        }

        @Override
        public void onDisconnected(@NonNull String s) {
            Toast.makeText(getApplicationContext(), device_name + " disconnected", Toast.LENGTH_LONG).show();
        }
    };


    EndpointDiscoveryCallback endpointDiscoveryCallback = new EndpointDiscoveryCallback() {
        @Override
        public void onEndpointFound(@NonNull String endpointId, @NonNull DiscoveredEndpointInfo discoveredEndpointInfo) {
            deviceName = discoveredEndpointInfo.getEndpointName();
            foundDevices.add(deviceName);
            myDevicesAdapter.notifyDataSetChanged();
            hashMap.put(deviceName, endpointId);
        }

        @Override
        public void onEndpointLost(@NonNull String s) {

        }
    };

    boolean isAdvertiser = false;

    public void startAdvertiser() {
        isAdvertiser = true;
        oneTimeAdvertiser = true;
        AdvertisingOptions advertisingOptions =
                new AdvertisingOptions.Builder().setStrategy(Strategy.P2P_STAR).build();
        Nearby.getConnectionsClient(getApplicationContext())
                .startAdvertising(
                        Build.DEVICE, getPackageName(), connectionLifecycleCallback, advertisingOptions)
                .addOnSuccessListener(
                        (Void unused) -> {
                            FragmentTransaction trans = manager.beginTransaction();
                            trans.remove(createFragment);
                            trans.commit();

                            Toast.makeText(getApplicationContext(), "Channel  created!", Toast.LENGTH_LONG).show();
                        })
                .addOnFailureListener(
                        (Exception e) -> {
                            Toast.makeText(getApplicationContext(), "Channel creation fails!", Toast.LENGTH_LONG).show();
                        });
    }

    public void startDiscovery() {
        oneTimeAdvertiser= true;
        DiscoveryOptions discoveryOptions =
                new DiscoveryOptions.Builder().setStrategy(Strategy.P2P_STAR).build();
        Nearby.getConnectionsClient(this)
                .startDiscovery(getPackageName(), endpointDiscoveryCallback, discoveryOptions)
                .addOnSuccessListener(
                        (Void unused) -> {
                            Toast.makeText(this, "Searching for channel", Toast.LENGTH_LONG).show();
                        })
                .addOnFailureListener(
                        (Exception e) -> {
                            Toast.makeText(this, "Searching fail", Toast.LENGTH_LONG).show();
                        });
    }

    public void listFragment() {
        itemListDialogFragment = new ItemListDialogFragment(songsAdapter);
        itemListDialogFragment.show(getSupportFragmentManager(), "fragDialog");
    }

    public void joinedDevices() {
        UsersDialogFragment newFragment = new UsersDialogFragment(myAdapter);
        FragmentManager fmm = getSupportFragmentManager();
        newFragment.show(fmm, "fragment_tag");
        MyAdapter.dismissDialog(newFragment);
    }

    static boolean isStartOnce = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.logout:

                SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                settings.edit().clear().commit();
                Intent intent22 = new Intent(this,UserProfileActivity.class);
                startActivity(intent22);
                mediaPlayer.reset();
                break;

            case R.id.listofSongs:
                    listFragment();
                break;

            case R.id.usersList:
                joinedDevices();
                break;
            case R.id.play:
                Uri uri = null;
                String outGoingPath = outgoingidpath.get(sentId);
                if (!outgoingidpath.isEmpty() && isStartOnce == false) {
                    uri = Uri.parse(outGoingPath);
                    String msg = "play/" + uri.getLastPathSegment();
                    Payload payload = Payload.fromBytes(msg.getBytes(StandardCharsets.UTF_8));
                    Nearby.getConnectionsClient(AudioActivity.this).sendPayload(connectedDevices, payload);
                    try {
                        if (!uri.getPath().isEmpty()) {
                            playMusicInGroup(outGoingPath);
//                            int a =  mediaPlayer.getDuration();
                            sendingPayTask();
                            play.setImageResource(R.drawable.stop);
                            isStartOnce = true;
                        } else {
                            Toast.makeText(AudioActivity.this, "No Mp3 file", Toast.LENGTH_SHORT).show();

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (mediaPlayer.isPlaying()) {
                        String pauseMsg = "pause/msg";
                        Payload payload = Payload.fromBytes(pauseMsg.getBytes(StandardCharsets.UTF_8));
                        Nearby.getConnectionsClient(AudioActivity.this).sendPayload(connectedDevices, payload);
                        play.setImageResource(R.drawable.play);
                        mediaPlayer.pause();
                    } else {
                        mediaPlayer.start();
                        primarySeekBarProgressUpdater();
                        play.setImageResource(R.drawable.stop);
                        String playMsg = "play/again";
                        Payload payload = Payload.fromBytes(playMsg.getBytes(StandardCharsets.UTF_8));
                        Nearby.getConnectionsClient(AudioActivity.this).sendPayload(connectedDevices, payload);
                    }
                }
                break;
            case R.id.next:
                int gcp = mediaPlayer.getCurrentPosition() + 10000;
                String seekTo;
                if (gcp < mediaPlayer.getDuration()) {
                    seekTo = "seekTo/" + gcp;

                } else {
                    seekTo = "seekTo/" + mediaPlayer.getCurrentPosition();
                    gcp = mediaPlayer.getCurrentPosition();
                }
                Payload payloadNext = Payload.fromBytes(seekTo.getBytes(StandardCharsets.UTF_8));
                Nearby.getConnectionsClient(AudioActivity.this).sendPayload(connectedDevices, payloadNext);
                mediaPlayer.seekTo(gcp);
                break;
            case R.id.prev:
                int gcp1 = mediaPlayer.getCurrentPosition() - 10000;
                String seekTo1;
                if (gcp1 > 0) {
                    seekTo1 = "seekTo/" + gcp1;

                } else {
                    seekTo1 = "seekTo/" + mediaPlayer.getCurrentPosition();
                    gcp1 = mediaPlayer.getCurrentPosition();
                }
                Payload payloadNext1 = Payload.fromBytes(seekTo1.getBytes(StandardCharsets.UTF_8));
                Nearby.getConnectionsClient(AudioActivity.this).sendPayload(connectedDevices, payloadNext1);
                mediaPlayer.seekTo(gcp1);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mediaPlayer.reset();
        if (connectedDevices.size() > 0) {
            for (String device : connectedDevices) {
                Nearby.getConnectionsClient(AudioActivity.this).disconnectFromEndpoint(device);
            }
        }
        if (isAdvertiser) {
            try {
                Nearby.getConnectionsClient(AudioActivity.this).stopAdvertising();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                Nearby.getConnectionsClient(AudioActivity.this).stopDiscovery();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Intent ii = new Intent(this,MainActivity.class);
        ii.putExtra("check", "audio");
        startActivity(ii);
    }

    private final Handler handler = new Handler();
    private int mediaFileLengthInMilliseconds;

    private void primarySeekBarProgressUpdater() {
        seekBar.setProgress((int) (((float) mediaPlayer.getCurrentPosition() / mediaFileLengthInMilliseconds) * 100));
        if (mediaPlayer.isPlaying()) {
            Runnable notification = new Runnable() {
                public void run() {
                    primarySeekBarProgressUpdater();
                }
            };
            handler.postDelayed(notification, 1000);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            float factor = (float) (progress / 100.0);
            int seekToo = (int) (factor * mediaPlayer.getDuration());
            String str = "seekTo/" + seekToo;
            Payload payloadNext1 = Payload.fromBytes(str.getBytes(StandardCharsets.UTF_8));
            Nearby.getConnectionsClient(AudioActivity.this).sendPayload(connectedDevices, payloadNext1);
            mediaPlayer.seekTo((int) seekToo);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}