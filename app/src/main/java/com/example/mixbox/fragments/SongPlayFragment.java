package com.example.mixbox.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mixbox.MainActivity;
import com.example.mixbox.R;
import com.example.mixbox.databinding.FragmentSongListBinding;
import com.example.mixbox.databinding.FragmentSongPlayBinding;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.drm.DefaultDrmSessionManager;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaDrm;
import com.google.android.exoplayer2.drm.HttpMediaDrmCallback;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;


public class SongPlayFragment extends Fragment {
    FragmentSongPlayBinding binding;

    FirebaseDatabase db;
    FirebaseStorage storage;
    String type;
    String playlistName;
    private static final String ACTION_VIEW = "com.example.mixbox.fragments.action.VIEW";
    private static final String EXTENSION_EXTRA = "extension";
    private static final String DRM_SCHEME_EXTRA = "drm_scheme";
    private static final String DRM_LICENSE_URL_EXTRA = "drm_license_url";
    private static final String OWNER_EXTRA = "owner";

    private static String DEFAULT_MEDIA_URI =
            //"https://storage.googleapis.com/exoplayer-test-media-1/mkv/android-screens-lavf-56.36.100-aac-avc-main-1280x720.mkv";
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-8.mp3";
    //https://www.soundhelix.com/examples/mp3/SoundHelix-Song-16.mp3

    private boolean isOwner;
    @Nullable
    private PlayerControlView playerControlView;

    @Nullable private static ExoPlayer player;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    //private static final String ARG_PARAM1 = "param1";
    //private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    //private String mParam1;
    //private String mParam2;

    //For test


    public SongPlayFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
  }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSongPlayBinding.inflate(inflater, container, false);

        Log.d("---","[SongPlayFragment#onCreateView]");

        db = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

        if(getArguments().get("type") != null) {
            type = getArguments().get("type").toString();
        }

        if(getArguments().get("playlistName") != null){ // playlistName is not null
            playlistName = getArguments().get("playlistName").toString();
        }

        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("---", "Back to play list");
                if(player != null){
                    player.stop();
                }
                //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new SongListFragment()).commit();

                if(type == "search"){
                    //Bundle bundle = new Bundle();
                    SearchSongFragment fragment = new SearchSongFragment();
                    //fragment.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, fragment).commit();
                }else{
                    Bundle bundle = new Bundle();
                    if(type != null && type != ""){
                        bundle.putString("type", type);
                    }
                    if(playlistName != null && playlistName != ""){
                        bundle.putString("playlistName", playlistName);
                    }

                    SongListFragment fragment = new SongListFragment();
                    fragment.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, fragment).commit();
                }

            }
        });

        TextView songTitle = binding.sTitle;
        TextView artistName = binding.sArtist;
        ImageView albumCover = binding.albumImage;

        playerControlView = binding.playerControlView;

        //isOwner = getIntent().getBooleanExtra(OWNER_EXTRA, /* defaultValue= */ true);
        isOwner = true;

        String sTitle = getArguments().get("title").toString();
        String artist = getArguments().get("artist").toString();
        songTitle.setText(sTitle);
        songTitle.setSelected(true);
        artistName.setText(artist);
        artistName.setSelected(true);
        String albumCoverName = getArguments().get("albumCover").toString();
        storageRef.child("albumcover/"+albumCoverName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

            @Override
            public void onSuccess(Uri uri) {
                Log.d("---", "image URI : " + uri);
                Glide.with(getActivity() ) //context
                        .load(uri)
                        .into(binding.albumImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("---", "Error: " + e);
            }
        });

        Log.d("---", "Initialize ExoPlayer.");
        initializePlayer();

        String fileName = sTitle;
        Log.d("---", "song title : " + sTitle);
        //If you already have download infrastructure based around URLs, or just want a URL to share, you can get the download URL for a file by calling the getDownloadUrl() method on a Cloud Storage reference.
        storageRef.child("music/"+ fileName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

            @Override
            public void onSuccess(Uri uri) {
                Log.d("---", "URI : " + uri.toString());
                startPlayer(uri.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("---", "Error: " + e);
            }
        });

        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_song_play, container, false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d("---","[SongPlayFragment#onResume]");
//        if (isOwner && player == null) {
//           initializePlayer();
//        }
//
//        PlayerControlView playerControlView = Assertions.checkNotNull(this.playerControlView);
//        playerControlView.setPlayer(player);
//        playerControlView.show();
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d("---","[SongPlayFragment#onPause]");
        //Assertions.checkNotNull(playerControlView).setPlayer(null);

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("---","[SongPlayFragment#onStart]");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("---","[SongPlayFragment#onStop]");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("---","[SongPlayFragment#onDestroy]");
        //if (isOwner && isFinishing()) {

            if (player != null) {
                player.stop();
                player.release();
                player = null;
            }
       // }
    }

    private void initializePlayer(){
        if(player == null){
            player = new ExoPlayer.Builder(getActivity()).build();
            Log.d("---", "Initialize Player. Player is created.");
        }else{
            Log.d("---", "Initialize Player. Player is alredy existed.");
        }
        playerControlView.setPlayer(player);
        playerControlView.show();
    }

    private void startPlayer(String mediaUri) {
        //Bundle bundle = getArguments();
        Intent intent = null;
        Uri data = null;
        //String action = intent.getAction();

        Log.d("---", "startPlayer play: uri - " + mediaUri);
        //DEFAULT_MEDIA_URI = "https://firebasestorage.googleapis.com/v0/b/hkkofirstproject.appspot.com/o/hopeful-piano-112621.mp3?alt=media&token=00a85881-aaf7-4063-be78-db9b16dfc8e7";

        String action = "";
        Uri uri =
                ACTION_VIEW.equals(action)
                        //? Assertions.checkNotNull(intent.getData())
                        ? Assertions.checkNotNull(data)
                        : Uri.parse(mediaUri);

        DrmSessionManager drmSessionManager;
        if (intent != null && intent.hasExtra(DRM_SCHEME_EXTRA)) {
            String drmScheme = Assertions.checkNotNull(intent.getStringExtra(DRM_SCHEME_EXTRA));
            String drmLicenseUrl = Assertions.checkNotNull(intent.getStringExtra(DRM_LICENSE_URL_EXTRA));
            UUID drmSchemeUuid = Assertions.checkNotNull(Util.getDrmUuid(drmScheme));
            HttpDataSource.Factory licenseDataSourceFactory = new DefaultHttpDataSource.Factory();
            HttpMediaDrmCallback drmCallback =
                    new HttpMediaDrmCallback(drmLicenseUrl, licenseDataSourceFactory);
            drmSessionManager =
                    new DefaultDrmSessionManager.Builder()
                            .setUuidAndExoMediaDrmProvider(drmSchemeUuid, FrameworkMediaDrm.DEFAULT_PROVIDER)
                            .build(drmCallback);
        } else {
            drmSessionManager = DrmSessionManager.DRM_UNSUPPORTED;
        }

        DataSource.Factory dataSourceFactory = new DefaultDataSource.Factory(getActivity());

        MediaSource mediaSource;
        @C.ContentType int type = Util.inferContentType(uri, (intent == null ? null : intent.getStringExtra(EXTENSION_EXTRA)));

        Log.d("---", "startPlayer play___#4 type:" + Integer.toString(type) + "---");
        if (type == C.TYPE_DASH) {
            mediaSource =
                    new DashMediaSource.Factory(dataSourceFactory)
                            .setDrmSessionManagerProvider(unusedMediaItem -> drmSessionManager)
                            .createMediaSource(MediaItem.fromUri(uri));
        } else if (type == C.TYPE_OTHER) {
            mediaSource =
                    new ProgressiveMediaSource.Factory(dataSourceFactory)
                            .setDrmSessionManagerProvider(unusedMediaItem -> drmSessionManager)
                            .createMediaSource(MediaItem.fromUri(uri));
        } else {
            throw new IllegalStateException();
        }

        //ExoPlayer player = new ExoPlayer.Builder(getActivity()).build();

        player.setMediaSource(mediaSource);
        player.prepare();
        player.play();
        player.setRepeatMode(Player.REPEAT_MODE_ALL);
    }

}