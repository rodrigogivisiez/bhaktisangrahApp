package com.goldenant.bhaktisangrah.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.view.KeyEvent;
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

import com.goldenant.bhaktisangrah.MainActivity;
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
import com.goldenant.bhaktisangrah.common.ui.CircularSeekBar;
import com.goldenant.bhaktisangrah.common.ui.MasterFragment;
import com.goldenant.bhaktisangrah.common.util.ToastUtil;
import com.goldenant.bhaktisangrah.common.util.Utilities;
import com.goldenant.bhaktisangrah.model.SubCategoryModel;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import wseemann.media.FFmpegMediaPlayer;


import wseemann.media.FFmpegMediaPlayer;

/**
 * Created by Adite on 03-01-2016.
 */
public class Streaming extends MasterFragment {
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
    private ImageButton repeatButton;

    private ProgressBar spinner;
    private Handler mHandler = new Handler();
    ;
    private Utilities utils;

    private Boolean isPlaying;
    private int songPosition = 0;
    String imageUrl;
    private Bundle bundle;
    protected static FFmpegMediaPlayer freePlayer;
    MainActivity mContext;
    private boolean isRepeat = false;
    ArrayList<SubCategoryModel> ListItem = new ArrayList<SubCategoryModel>();
    SubCategoryModel ListPos;

    //    protected static Player premiumPlayer;
//
//    private YouTube youtube;
//    private YouTube.Search.List query;
    String item_description = null, trackUrl = null, item_id = null, item_image = null, item_name = null;

    private Handler seekHandler = new Handler();
//    SeekBar seekBar2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // get root view
        mContext = (MainActivity) getMasterActivity();
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
//        seekBar2 = (SeekBar) rootView.findViewById(R.id.seekBar2);
        finalDuration = (TextView) rootView.findViewById(R.id.finalDuration);
        prevButton = (ImageButton) rootView.findViewById(R.id.prevButton);
        playButton = (ImageButton) rootView.findViewById(R.id.playButton);
        nextButton = (ImageButton) rootView.findViewById(R.id.nextButton);

        repeatButton = (ImageButton) rootView.findViewById(R.id.repeatButton);

        spinner = (ProgressBar) rootView.findViewById(R.id.progressBar3);


        bundle = getArguments();

        if (bundle != null) {
            ListItem = (ArrayList<SubCategoryModel>) bundle.getSerializable("data");
            ListPos = (SubCategoryModel) bundle.getSerializable("data_pos");

//            item_description = bundle.getString("item_description");
//            trackUrl = bundle.getString("item_file");
//            item_id = bundle.getString("item_id");
//            item_image = bundle.getString("item_image");
//            item_name = bundle.getString("item_name");

//            trackNameView.setText(item_name);
//            albumNameView.setText(item_description);
//
//            Log.d("selected trackUrl... ", trackUrl);
//
//            Picasso.with(getActivity()).load(item_image).placeholder(R.drawable.no_image).fit().into(backgroundImageView);
//            Picasso.with(getActivity()).load(item_image).placeholder(R.drawable.no_image).fit().into(trackImageView);
        }

        // if song running -> pause it
        if (freePlayer != null) {
            freePlayer.pause();
        }
        /*if (premiumPlayer != null) {
            premiumPlayer.pause();
        }*/

        // Progress Bar to display loading while everything is being set up


        // get song number from list of songs
//        songPosition = Integer.parseInt(getActivity().getIntent().getStringExtra(Intent.EXTRA_TEXT));

        // setup ui
        setUi();
        prepareMusic();

        freePlayer.setOnCompletionListener(new FFmpegMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(FFmpegMediaPlayer fFmpegMediaPlayer) {

                if (isRepeat) {
                    // repeat is on play same song again
//                    getRepeatTrack();
                    prepareMusic();
                    ToastUtil.showShortToastMessage(getActivity(), "Repeat");
                } else {
                    playButton.setClickable(true);
                    playButton.setImageResource(R.drawable.ic_action_play);
                    //prepareMusic();

                    if(songPosition < (ListItem.size() - 1)){
				        prepareMusic(songPosition + 1);
                        songPosition = songPosition + 1;
                    } else {
                        // play first song
                        prepareMusic(0);
                        songPosition = 0;
                    }
                }



            }
        });

        freePlayer.setOnSeekCompleteListener(new FFmpegMediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(FFmpegMediaPlayer fFmpegMediaPlayer) {
                spinner.setVisibility(View.GONE);
            }
        });

        return rootView;
    }

    private void getRepeatTrack() {
        try {

            freePlayer = new FFmpegMediaPlayer();
            freePlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            ToastUtil.showShortToastMessage(getActivity(), trackUrl);

            freePlayer.setDataSource(trackUrl);
            freePlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

        freePlayer.start();

        seekBarView.setProgress(0);
        seekBarView.setMax(100);

        updateProgressBar();
        isPlaying = true;
    }


    private void setUi() {
        utils = new Utilities();

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


//            Picasso.with(getActivity()).load(bundle.getString("item_image")).placeholder(R.drawable.ic_album).error(R.drawable.ic_album).into(new Target() {
//                @Override
//                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//
//                    // blur and set background image with animation
//                    Bitmap backgroundBitmap = bitmap;
//                    backgroundBitmap = BlurBuilder.blur(getActivity(), bitmap);
//
//                    backgroundImageView.setImageBitmap(backgroundBitmap);
//                    AlphaAnimation alpha = new AlphaAnimation(1, 0.5F);
//                    alpha.setDuration(1000);
//                    alpha.setFillAfter(true);
//                    backgroundImageView.startAnimation(alpha);
//
//
//                    // set track image
//                    trackImageView.setImageBitmap(bitmap);
//
//                    // update ui elements based on average color of track image
//                    Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener() {
//                        @Override
//                        public void onGenerated(Palette palette) {
//
//                            int color = palette.getMutedColor(android.R.color.black);
//                            int alphaColor = Color.argb(Math.round(Color.alpha(color) * 0.9f), Color.red(color), Color.green(color), Color.blue(color));
//
//                            // action bar
////                        actionBar.setBackgroundDrawable(new ColorDrawable(palette.getMutedColor(android.R.color.black)));
//
//                            // status bar
////                        getActivity().getWindow().setStatusBarColor(alphaColor);
////                        getActivity().getWindow();
//
//                            // navigation bar
////                        getActivity().getWindow().setNavigationBarColor(alphaColor);
//
//                            // playback buttons
////                        prevButton.setColor(palette.getMutedColor(android.R.color.black));
////                        playButton.setColor(palette.getMutedColor(android.R.color.black));
////                        nextButton.setColor(palette.getMutedColor(android.R.color.black));
//
//                            // seek bar
//                            // seekBarView.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(palette.getMutedColor(android.R.color.black), PorterDuff.Mode.MULTIPLY));
//                            // TODO: Add change color of thumb
//                        }
//                    });
//
//                /*// search and link music video on youtube
//                // TODO: Disable youtube linkage on freeplayer
//                youTubeButtonView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        SearchVideoId searchVideoId = new SearchVideoId();
//                        searchVideoId.execute(TopTenTracksActivityFragment.topTenTrackList.get(songPosition).trackArtist + " " + TopTenTracksActivityFragment.topTenTrackList.get(songPosition).trackName);
//                    }
//                });*/
//                }
//
//                @Override
//                public void onBitmapFailed(Drawable errorDrawable) {
//                    // try again
//                    setUi();
//                }
//
//                @Override
//                public void onPrepareLoad(Drawable placeHolderDrawable) {
//                }
//            });


        // track Name
//        trackNameView.setText(TopTenTracksActivityFragment.topTenTrackList.get(songPosition).trackName);

        // seekbar setup and progress listener
        //seekBarView.setMax(730000);

//        seekBarView.setProgress(0);
//        seekBarView.setMax(100);

        seekBarView.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {
                mHandler.removeCallbacks(mUpdateTimeTask);
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {
                mHandler.removeCallbacks(mUpdateTimeTask);
                int totalDuration = freePlayer.getDuration();
                int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

                spinner.setVisibility(View.VISIBLE);

                // forward or backward to certain seconds
                freePlayer.seekTo(currentPosition);

                // update timer progress again
                updateProgressBar();
            }
        });

//        seekBar2.setOnSeekBarChangeListener(this);
//        seekBarView.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
//                if (fromUser) {
//
//                    if (freePlayer != null) {
//                        freePlayer.seekTo(progress);
//                        seekBarView.setProgress(progress);
//                    }
//                }
//            }
//
//            @Override
//            public void onStopTrackingTouch(CircularSeekBar seekBar) {
//                mHandler.removeCallbacks(mUpdateTimeTask);
//                int totalDuration = freePlayer.getDuration();
//                int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);
//
//                // forward or backward to certain seconds
//                freePlayer.seekTo(currentPosition);
//
//                // update timer progress again
//                updateProgressBar();
//            }
//
//            @Override
//            public void onStartTrackingTouch(CircularSeekBar seekBar) {
//                mHandler.removeCallbacks(mUpdateTimeTask);
//            }
//        });

        // set start position of track
        currentDuration.setText("00:00");
        finalDuration.setText("00:00");

        // set end duration of track


        //freePlayer.getDuration()
//        String duration = String.valueOf(730000);
//        int seconds = ((Integer.parseInt(duration) / 1000) % 60);
//        int minutes = ((Integer.parseInt(duration) / 1000) / 60);
//        if (seconds < 10) {
//            finalDuration.setText(String.valueOf(minutes) + ":0" + String.valueOf(seconds));
//        } else {
//            finalDuration.setText(String.valueOf(minutes) + ":" + String.valueOf(seconds));
//        }

    }

    private void prepareMusic() {

        try {

            freePlayer = new FFmpegMediaPlayer();
            freePlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            freePlayer.setDataSource(ListPos.getItem_file());
            freePlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // initially not playing
        isPlaying = false;

        // disable until prepared
        playButton.setClickable(false);
        playButton.setImageResource(R.drawable.stop);
        //playButton.setVisibility(View.GONE);

        trackNameView.setText(ListPos.getItem_name());
        albumNameView.setText(ListPos.getItem_description());

        Picasso.with(getActivity()).load(ListPos.getItem_image()).placeholder(R.drawable.no_image).fit().into(backgroundImageView);
        Picasso.with(getActivity()).load(ListPos.getItem_image()).placeholder(R.drawable.no_image).fit().into(trackImageView);

        freePlayer.setOnPreparedListener(new FFmpegMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(FFmpegMediaPlayer mp) {
                spinner.setVisibility(View.GONE);

                // restore button
                playButton.setClickable(true);
                playButton.setImageResource(R.drawable.pause);

                freePlayer.start();

                seekBarView.setProgress(0);
                seekBarView.setMax(100);
                // Updating progress bar
                updateProgressBar();

                isPlaying = true;

                // prev button on click listener
                prevButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        spinner.setVisibility(View.VISIBLE);
                        if (songPosition > 0) {
                            prepareMusic(songPosition - 1);
                            songPosition = songPosition - 1;
                        } else {
                            // play last song
                            prepareMusic(ListItem.size() - 1);
                            songPosition = ListItem.size() - 1;
                        }
                    }
                });

                // next button on click listener
                nextButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        spinner.setVisibility(View.VISIBLE);
                        if (songPosition < (ListItem.size() - 1)) {
                            prepareMusic(songPosition + 1);
                            songPosition = songPosition + 1;
                        } else {
                            // play first song
                            prepareMusic(0);
                            songPosition = 0;
                        }
                    }
                });
            }
        });
    }

    private void prepareMusic(int songIndex) {
        //spinner.setVisibility(View.GONE);
       /* // check for free or premium user
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String userType = prefs.getString(getString(R.string.user_type_key),
                getString(R.string.user_type_key));

        // free user
        if (userType.equals("free")) {*/

        // get preview track

        try {

            freePlayer = new FFmpegMediaPlayer();
            freePlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            freePlayer.setDataSource(ListItem.get(songIndex).getItem_file());
            freePlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // initially not playing
        isPlaying = false;

        // disable until prepared
        playButton.setClickable(false);
        playButton.setImageResource(R.drawable.stop);
        //playButton.setVisibility(View.GONE);

        trackNameView.setText(ListItem.get(songIndex).getItem_name());
        albumNameView.setText(ListItem.get(songIndex).getItem_description());

        Picasso.with(getActivity()).load(ListItem.get(songIndex).getItem_image()).placeholder(R.drawable.no_image).fit().into(backgroundImageView);
        Picasso.with(getActivity()).load(ListItem.get(songIndex).getItem_image()).placeholder(R.drawable.no_image).fit().into(trackImageView);

        freePlayer.setOnPreparedListener(new FFmpegMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(FFmpegMediaPlayer mp) {
                spinner.setVisibility(View.GONE);

                // restore button
                playButton.setClickable(true);
                playButton.setImageResource(R.drawable.pause);

                freePlayer.start();

                seekBarView.setProgress(0);
                seekBarView.setMax(100);
                // Updating progress bar
                updateProgressBar();

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
                            playButton.setImageResource(R.drawable.ic_action_play);
                        }
                    }
                });

                repeatButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        if (isRepeat) {
                            isRepeat = false;
                            repeatButton.setImageResource(R.drawable.repeat_off);
                        } else {
                            // make repeat to true
                            isRepeat = true;
                            repeatButton.setImageResource(R.drawable.repeat_on);
                        }
                    }
                });

                // prev button on click listener
                prevButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        spinner.setVisibility(View.VISIBLE);
                        if (songPosition > 0) {
                            prepareMusic(songPosition - 1);
                            songPosition = songPosition - 1;
                        } else {
                            // play last song
                            prepareMusic(ListItem.size() - 1);
                            songPosition = ListItem.size() - 1;
                        }

//
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
                        spinner.setVisibility(View.VISIBLE);
                        if (songPosition < (ListItem.size() - 1)) {
                            prepareMusic(songPosition + 1);
                            songPosition = songPosition + 1;
                        } else {
                            // play first song
                            prepareMusic(0);
                            songPosition = 0;
                        }

//                        spinner.setVisibility(View.VISIBLE);
//                        songPosition = songPosition + 1;
//                        if (songPosition > ListItem.size() - 1) {
//                            songPosition = 0;
//                        }
//                        setUi();
//                        playButton.setImageResource(R.drawable.ic_play);
//                        if (freePlayer != null) {
//                            freePlayer.reset();
//                        }
//

                    }
                });
            }
        });
    }


    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {

            long totalDuration = freePlayer.getDuration();
            long currentDurations = freePlayer.getCurrentPosition();

            // Displaying Total Duration time
            finalDuration.setText("" + utils.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            currentDuration.setText("" + utils.milliSecondsToTimer(currentDurations));

            // Updating progress bar
            int progress = (int) (utils.getProgressPercentage(currentDurations, totalDuration));
            seekBarView.setProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };


    public void updateProgressBar() {

        if (freePlayer != null) {
            mHandler.postDelayed(mUpdateTimeTask, 100);
        }
    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        freePlayer.release();
//    }

    @Override
    public void onResume() {
        super.onResume();

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    HomeFragment home = new HomeFragment();
                    mContext.ReplaceFragement(home);
                }
                return false;
            }
        });
    }
}