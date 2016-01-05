package com.goldenant.bhaktisangrah.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.app.Fragment;
/*import android.support.v7.graphics.Palette;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;*/
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.goldenant.bhaktisangrah.R;
/*import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.PlayerStateCallback;
import com.spotify.sdk.android.player.Spotify;*/
import com.goldenant.bhaktisangrah.common.ui.BlurBuilder;
import com.goldenant.bhaktisangrah.common.ui.CircularSeekBar;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.List;

import wseemann.media.FFmpegMediaPlayer;


import wseemann.media.FFmpegMediaPlayer;

/**
 * Created by Adite on 03-01-2016.
 */
public class Streaming extends Fragment
{
    // views
    private View rootView;
//    private android.support.v7.app.ActionBar actionBar;
    private ImageView backgroundImageView;
    private TextView artistNameView;
    private TextView albumNameView;
    private ImageView trackImageView;
//    private ImageView youTubeButtonView;
    private TextView trackNameView;
    private TextView currentDuration;
    private CircularSeekBar seekBarView;
    private TextView finalDuration;
    private ImageButton prevButton;
    private ImageButton playButton;
    private ImageButton nextButton;
//    private ProgressBar spinner;

    private Boolean isPlaying;
//    private int songPosition;
    String imageUrl;
    private Bundle bundle;
    protected static FFmpegMediaPlayer freePlayer;

//    protected static Player premiumPlayer;
//
//    private YouTube youtube;
//    private YouTube.Search.List query;

    private Handler seekHandler = new Handler();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // get root view
        rootView = inflater.inflate(R.layout.streaming_player, container, false);

        // initialize ui elements
//        actionBar = PlayerActivity.actionBar;
        backgroundImageView = (ImageView) rootView.findViewById(R.id.backgroundImage);
//        artistNameView = (TextView) rootView.findViewById(R.id.artistName);
        albumNameView = (TextView) rootView.findViewById(R.id.albumName);
        trackImageView = (ImageView) rootView.findViewById(R.id.trackImage);
//        youTubeButtonView = (ImageButton) rootView.findViewById(R.id.youTubeButton);
        trackNameView = (TextView) rootView.findViewById(R.id.trackName);
        currentDuration = (TextView) rootView.findViewById(R.id.currentDuration);
        seekBarView = (CircularSeekBar) rootView.findViewById(R.id.seekBar);
        finalDuration = (TextView) rootView.findViewById(R.id.finalDuration);
        prevButton = (ImageButton) rootView.findViewById(R.id.prevButton);
        playButton = (ImageButton) rootView.findViewById(R.id.playButton);
        nextButton = (ImageButton) rootView.findViewById(R.id.nextButton);
//        spinner = (ProgressBar) rootView.findViewById(R.id.progressBar3);

        bundle = getArguments();

        // if song running -> pause it
        if (freePlayer != null) {
            freePlayer.pause();
        }
        /*if (premiumPlayer != null) {
            premiumPlayer.pause();
        }*/

        // Progress Bar to display loading while everything is being set up
      //  spinner.setVisibility(View.VISIBLE);

        // get song number from list of songs
//        songPosition = Integer.parseInt(getActivity().getIntent().getStringExtra(Intent.EXTRA_TEXT));

        // setup ui
        setUi();

        prepareMusic();


        // prepare music


        return rootView;
    }


    private void setUi() {

        // set track name and album name in the actionbar
       /* actionBar.setTitle(TopTenTracksActivityFragment.topTenTrackList.get(songPosition).trackName);
        actionBar.setSubtitle(TopTenTracksActivityFragment.topTenTrackList.get(songPosition).trackAlbum);

        // TODO: remove once ui is modified
        // artist and album name
        artistNameView.setText(TopTenTracksActivityFragment.topTenTrackList.get(songPosition).trackArtist);
        albumNameView.setText(TopTenTracksActivityFragment.topTenTrackList.get(songPosition).trackAlbum);

        // get image url
        imageUrl = TopTenTracksActivityFragment.topTenTrackList.get(songPosition).trackImageLarge;*/


//        Picasso.with(getActivity()).load(imageUrl).placeholder(R.drawable.ic_album).fit().centerCrop().into(backgroundImageView);


        Picasso.with(rootView.getContext()).load(imageUrl).placeholder(R.drawable.ic_album).error(R.drawable.ic_album).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                // blur and set background image with animation
                Bitmap backgroundBitmap = bitmap;
                backgroundBitmap = BlurBuilder.blur(getActivity(), bitmap);

                backgroundImageView.setImageBitmap(backgroundBitmap);
                AlphaAnimation alpha = new AlphaAnimation(1, 0.5F);
                alpha.setDuration(1000);
                alpha.setFillAfter(true);
                backgroundImageView.startAnimation(alpha);

                backgroundImageView.setImageBitmap(bitmap);

                // set track image
                trackImageView.setImageBitmap(bitmap);

                // update ui elements based on average color of track image
                Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {

                        int color = palette.getMutedColor(android.R.color.black);
                        int alphaColor = Color.argb(Math.round(Color.alpha(color) * 0.9f), Color.red(color), Color.green(color), Color.blue(color));

                        // action bar
//                        actionBar.setBackgroundDrawable(new ColorDrawable(palette.getMutedColor(android.R.color.black)));

                        // status bar
//                        getActivity().getWindow().setStatusBarColor(alphaColor);
//                        getActivity().getWindow();

                        // navigation bar
//                        getActivity().getWindow().setNavigationBarColor(alphaColor);

                        // playback buttons
//                        prevButton.setColor(palette.getMutedColor(android.R.color.black));
//                        playButton.setColor(palette.getMutedColor(android.R.color.black));
//                        nextButton.setColor(palette.getMutedColor(android.R.color.black));

                        // seek bar
                       // seekBarView.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(palette.getMutedColor(android.R.color.black), PorterDuff.Mode.MULTIPLY));
                        // TODO: Add change color of thumb
                    }
                });

                /*// search and link music video on youtube
                // TODO: Disable youtube linkage on freeplayer
                youTubeButtonView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        SearchVideoId searchVideoId = new SearchVideoId();
                        searchVideoId.execute(TopTenTracksActivityFragment.topTenTrackList.get(songPosition).trackArtist + " " + TopTenTracksActivityFragment.topTenTrackList.get(songPosition).trackName);
                    }
                });*/
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                // try again
                setUi();
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        });


        // track Name
//        trackNameView.setText(TopTenTracksActivityFragment.topTenTrackList.get(songPosition).trackName);

        // seekbar setup and progress listener
        seekBarView.setMax(730000);

        seekBarView.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
                if (fromUser) {

                    if (freePlayer != null) {
                        freePlayer.seekTo(progress);
                    }
                }
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {

            }
        });

        // set start position of track
        currentDuration.setText("00:00");

        // set end duration of track
        //freePlayer.getDuration()
        String duration = String.valueOf(73000);
        int seconds = ((Integer.parseInt(duration) / 1000) % 60);
        int minutes = ((Integer.parseInt(duration) / 1000) / 60);
        if (seconds < 10) {
            finalDuration.setText(String.valueOf(minutes) + ":0" + String.valueOf(seconds));
        } else {
            finalDuration.setText(String.valueOf(minutes) + ":" + String.valueOf(seconds));
        }

    }

    private void prepareMusic() {

       /* // check for free or premium user
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String userType = prefs.getString(getString(R.string.user_type_key),
                getString(R.string.user_type_key));

        // free user
        if (userType.equals("free")) {*/

            // get preview track

        String trackUrl = null;
        if(bundle != null){

            trackUrl =  bundle.getString("item_file");
        }

            freePlayer = new FFmpegMediaPlayer();
            freePlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            try {
                freePlayer.setDataSource(trackUrl);
                freePlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // initially not playing
            isPlaying = false;

            // disable until prepared
            playButton.setClickable(false);
            playButton.setImageResource(R.drawable.ic_stop);

            freePlayer.setOnPreparedListener(new FFmpegMediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(FFmpegMediaPlayer mp) {
//                    spinner.setVisibility(View.GONE);

                    // restore button
                    playButton.setClickable(true);
                    playButton.setImageResource(R.drawable.pause);

                    freePlayer.start();
                    setSeekBar();

                    isPlaying = true;

                    // play/pause
                    playButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!isPlaying) {
                                freePlayer.start();
                                playButton.setImageResource(R.drawable.pause);
                                isPlaying = true;
                            } else {
                                freePlayer.pause();
                                isPlaying = false;
                                playButton.setImageResource(R.drawable.play);
                            }
                        }
                    });

                    // prev button on click listener
                    prevButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                spinner.setVisibility(View.VISIBLE);
//                songPosition = songPosition - 1;
//                if (songPosition < 0) {
//                    songPosition = 0;
//                }
//                setUi();
//                playButton.setImageResource(R.drawable.ic_play);
//                if (freePlayer != null) {
//                    freePlayer.reset();
//                }
//                prepareMusic();
                        }
                    });

                    // next button on click listener
                    nextButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                spinner.setVisibility(View.VISIBLE);
//                songPosition = songPosition + 1;
//                if (songPosition > TopTenTracksActivityFragment.topTenTrackList.size() - 1) {
//                    songPosition = 0;
//                }
//                setUi();
//                playButton.setImageResource(R.drawable.ic_play);
//                if (freePlayer != null) {
//                    freePlayer.reset();
//                }
//                prepareMusic();
                        }
                    });
                }
            });
        }
//    }

    // set up seek bar properties and also update current and max duration
    private void setSeekBar() {

        if (freePlayer != null) {
            seekBarView.setProgress(freePlayer.getDuration());
        }

        // ping for updated position every second
        seekHandler.postDelayed(run, 1000);
    }

    // seperate thread for pinging seekbar position
    Runnable run = new Runnable() {
        @Override
        public void run() {
            setSeekBar();
        }
    };

}