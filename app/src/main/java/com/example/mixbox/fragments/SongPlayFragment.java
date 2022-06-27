package com.example.mixbox.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

import java.util.UUID;


public class SongPlayFragment extends Fragment {
    FragmentSongPlayBinding binding;

    private static final String DEFAULT_MEDIA_URI =
            //"https://storage.googleapis.com/exoplayer-test-media-1/mkv/android-screens-lavf-56.36.100-aac-avc-main-1280x720.mkv";
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-8.mp3";
    //https://www.soundhelix.com/examples/mp3/SoundHelix-Song-16.mp3

    private static final String ACTION_VIEW = "com.example.mixbox.fragments.action.VIEW";
    private static final String EXTENSION_EXTRA = "extension";
    private static final String DRM_SCHEME_EXTRA = "drm_scheme";
    private static final String DRM_LICENSE_URL_EXTRA = "drm_license_url";
    private static final String OWNER_EXTRA = "owner";

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

    public SongPlayFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    /*
    public static SongPlayFragment newInstance(String param1, String param2) {
        SongPlayFragment fragment = new SongPlayFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    */



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
  }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSongPlayBinding.inflate(inflater, container, false);

        TextView songTitle = binding.sTitle;
        TextView artistName = binding.sArtist;
        playerControlView = binding.playerControlView;

        //isOwner = getIntent().getBooleanExtra(OWNER_EXTRA, /* defaultValue= */ true);
        isOwner = true;

        String title = getArguments().get("title").toString();
        String artist = getArguments().get("artist").toString();
        songTitle.setText(title);
        artistName.setText(artist);

        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_song_play, container, false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isOwner && player == null) {
           initializePlayer();
        }

        PlayerControlView playerControlView = Assertions.checkNotNull(this.playerControlView);
        playerControlView.setPlayer(player);
        playerControlView.show();
    }

    @Override
    public void onPause() {
        super.onPause();

        Assertions.checkNotNull(playerControlView).setPlayer(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //if (isOwner && isFinishing()) {

            if (player != null) {
                player.release();
                player = null;
            }
       // }
    }


    private void initializePlayer() {
        //Bundle bundle = getArguments();
        Intent intent = null;
        Uri data = null;
        //String action = intent.getAction();
        String action = "";
        Uri uri =
                ACTION_VIEW.equals(action)
                        //? Assertions.checkNotNull(intent.getData())
                        ? Assertions.checkNotNull(data)
                        : Uri.parse(DEFAULT_MEDIA_URI);
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
        ExoPlayer player = new ExoPlayer.Builder(getActivity()).build();
        player.setMediaSource(mediaSource);
        player.prepare();
        player.play();
        player.setRepeatMode(Player.REPEAT_MODE_ALL);

        this.player = player;
    }


}