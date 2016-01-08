package com.goldenant.bhaktisangrah.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.goldenant.bhaktisangrah.MainActivity;
import com.goldenant.bhaktisangrah.R;
import com.goldenant.bhaktisangrah.common.ui.CircularSeekBar;
import com.goldenant.bhaktisangrah.common.ui.MasterFragment;
import com.goldenant.bhaktisangrah.common.util.ToastUtil;
import com.goldenant.bhaktisangrah.common.util.Utilities;
import com.goldenant.bhaktisangrah.model.HomeModel;
import com.goldenant.bhaktisangrah.model.SubCategoryModel;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import wseemann.media.FFmpegMediaPlayer;

/**
 * Created by Adite on 03-01-2016.
 */
public class Streaming extends MasterFragment {
    private View rootView;
    private ImageView backgroundImageView;
    private TextView artistNameView;
    private TextView albumNameView;
    private ImageView trackImageView;
    private TextView trackNameView;
    private TextView currentDuration;
    private CircularSeekBar seekBarView;
    private TextView finalDuration;
    private ImageButton prevButton;
    private ImageButton playButton;
    private ImageButton nextButton;
    private ImageButton repeatButton;
    private ImageButton shuffleButton;
     private ProgressBar progressBar;
    private Handler mHandler = new Handler();
    private Utilities utils;
    private Boolean isPlaying;
    String imageUrl;
    private Bundle bundle;
    private MediaPlayer mPlayer;
    //    protected static FFmpegMediaPlayer freePlayer;
    MainActivity mContext;
    private boolean isRepeat = false;
    private boolean isShuffle = false;
    ArrayList<SubCategoryModel> ListItem = new ArrayList<SubCategoryModel>();
    ArrayList<String> ListItemFile = new ArrayList<String>();
    ArrayList<String> ListItemName = new ArrayList<String>();
    ArrayList<String> ListItemImage = new ArrayList<String>();
    int ListPosition;
    HomeModel homeModel;
    private static final float BLUR_RADIUS = 25f;
    String item_description = null, trackUrl = null, item_id = null, item_image = null, item_name = null;
    private int mode;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // get root view
        mContext = (MainActivity) getMasterActivity();
        rootView = inflater.inflate(R.layout.streaming_player, container, false);

        mContext.hideDrawer();
        mContext.showDrawerBack();

        mPlayer = new MediaPlayer();
        utils = new Utilities();

        // initialize ui elements
        backgroundImageView = (ImageView) rootView.findViewById(R.id.backgroundImage);
        albumNameView = (TextView) rootView.findViewById(R.id.albumName);
        trackImageView = (ImageView) rootView.findViewById(R.id.trackImage);
        trackNameView = (TextView) rootView.findViewById(R.id.trackName);
        currentDuration = (TextView) rootView.findViewById(R.id.currentDuration);
        seekBarView = (CircularSeekBar) rootView.findViewById(R.id.seekBar);
        finalDuration = (TextView) rootView.findViewById(R.id.finalDuration);
        prevButton = (ImageButton) rootView.findViewById(R.id.prevButton);
        playButton = (ImageButton) rootView.findViewById(R.id.playButton);
        nextButton = (ImageButton) rootView.findViewById(R.id.nextButton);

        repeatButton = (ImageButton) rootView.findViewById(R.id.repeatButton);
        shuffleButton = (ImageButton) rootView.findViewById(R.id.shuffleButton);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar3);

        final AdView mAdView = (AdView) rootView.findViewById(R.id.adView_stream);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        bundle = getArguments();

        if (bundle != null) {

            mode = bundle.getInt("mode");
            if (mode == 1) {
                ListItemFile = (ArrayList<String>) bundle.getSerializable("item_file");
                ListItemName = (ArrayList<String>) bundle.getSerializable("item_name");
                ListItemImage = (ArrayList<String>) bundle.getSerializable("item_image");
                ListPosition = bundle.getInt("position");

                Log.d("ListItemImage", ListItemImage + " ");
            } else if (mode == 0) {
                ListItem = (ArrayList<SubCategoryModel>) bundle.getSerializable("data");
                ListPosition = bundle.getInt("position");
                homeModel = (HomeModel) bundle.getSerializable("CAT_ID");
            }
        }

        mContext.drawer_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mode == 1) {
                    Downloads downloads = new Downloads();
                    mContext.ReplaceFragement(downloads);
                } else if (mode == 0) {
                    CategoryList categoryList = new CategoryList();

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("CAT_ID", homeModel);
                    categoryList.setArguments(bundle);

                    mContext.ReplaceFragement(categoryList);
                }
            }
        });

        // Media Complete...
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (isRepeat) {
                    playSong(ListPosition);
                } else if (isShuffle) {
                    // shuffle is on - play a random song
                    Random rand = new Random();

                    if (mode == 1) {
                        ListPosition = rand.nextInt((ListItemFile.size() - 1) - 0 + 1) + 0;
                    } else {
                        ListPosition = rand.nextInt((ListItem.size() - 1) - 0 + 1) + 0;
                    }

                    playSong(ListPosition);
                } else {
                    // no repeat or shuffle ON - play next song

                    if (ListPosition < (ListItem.size() - 1)) {
                        playSong(ListPosition + 1);
                        ListPosition = ListPosition + 1;
                    } else if (ListPosition < (ListItemFile.size() - 1)) {
                        playSong(ListPosition + 1);
                        ListPosition = ListPosition + 1;
                    } else {
                        // play first song
                        playSong(0);
                        ListPosition = 0;
                    }
                }
            }
        });

        new AsyncRunner().execute();

        mPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                currentDuration.setText("00:00");
                finalDuration.setText("00:00");

                progressBar.setVisibility(View.GONE);
            }
        });

        mPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
//                progressBar.setVisibility(View.VISIBLE);
            }
        });


        playButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // check for already playing
                if (mPlayer.isPlaying()) {
                    if (mPlayer != null) {
                        mPlayer.pause();
                        // Changing button image to play button
                        playButton.setImageResource(R.drawable.ic_action_play);
                    }
                } else {
                    // Resume song
                    if (mPlayer != null) {
                        mPlayer.start();
                        // Changing button image to pause button
                        playButton.setImageResource(R.drawable.pause);
                    }
                }

            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                new AsyncRunnerNext().execute();
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new AsyncRunnerPrevious().execute();
			}
		});

        repeatButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(isRepeat){
                    isRepeat = false;
                    ToastUtil.showShortToastMessage(getActivity(), "Repeat OFF");
                    repeatButton.setImageResource(R.drawable.repeat_off);
                }else{
                    // make repeat to true
                    isRepeat = true;
                    ToastUtil.showShortToastMessage(getActivity(), "Repeat ON");
                    // make shuffle to false
                    isShuffle = false;
                    repeatButton.setImageResource(R.drawable.repeat_on);
                    shuffleButton.setImageResource(R.drawable.shuffle_off);
                }
            }
        });

        shuffleButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(isShuffle){
                    isShuffle = false;
                    ToastUtil.showShortToastMessage(getActivity(), "Shuffle OFF");
                    shuffleButton.setImageResource(R.drawable.shuffle_off);
                }else{
                    // make repeat to true
                    isShuffle= true;
                    ToastUtil.showShortToastMessage(getActivity(), "Shuffle ON");
                    // make shuffle to false
                    isRepeat = false;
                    shuffleButton.setImageResource(R.drawable.shuffle_on);
                    repeatButton.setImageResource(R.drawable.repeat_off);
                }
            }
        });


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
                int totalDuration = mPlayer.getDuration();
                int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

                progressBar.setVisibility(View.VISIBLE);

                // forward or backward to certain seconds
                mPlayer.seekTo(currentPosition);

                // update timer progress again
                updateProgressBar();
            }
        });

//        // if song running -> pause it
//        if (freePlayer != null) {
//            freePlayer.pause();
//        }
//
//        // setup ui
//        setUi();
//        prepareMusic(ListPosition);
//
//        // prev button on click listener
//        prevButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                spinner.setVisibility(View.VISIBLE);
//                freePlayer.reset();
//                if (ListPosition > 0) {
//                    prepareMusic(ListPosition - 1);
//                    ListPosition = ListPosition - 1;
//                } else {
//
//                    if(ListItem.size() > 0){
//                        prepareMusic(ListItem.size() - 1);
//                        ListPosition = ListItem.size() - 1;
//                    }
//
//                    else if(ListItemFile.size() > 0){
//                        prepareMusic(ListItemFile.size() - 1);
//                        ListPosition = ListItemFile.size() - 1;
//                    }
//                }
//            }
//        });
//
//        // next button on click listener
//        nextButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                spinner.setVisibility(View.VISIBLE);
//                freePlayer.reset();
//                if (ListPosition < (ListItem.size() - 1)) {
//                    prepareMusic(ListPosition + 1);
//                    ListPosition = ListPosition + 1;
//                }
//
//                else if (ListPosition < (ListItemFile.size() - 1)) {
//                    prepareMusic(ListPosition + 1);
//                    ListPosition = ListPosition + 1;
//                }
//
//                else {
//                    // play first song
//                    prepareMusic(0);
//                    ListPosition = 0;
//                }
//            }
//        });

        return rootView;
    }

    public void playSong(int songIndex) {
        // Play song
        try {

            mPlayer.reset();

            if(mode == 1){
                mPlayer.setDataSource(ListItemFile.get(songIndex));
            }else{
                mPlayer.setDataSource(ListItem.get(songIndex).getItem_file());
            }

            mPlayer.prepare();

            progressBar.setVisibility(View.GONE);

            mPlayer.start();
            // Displaying Song title

            if(mode == 1){
                trackNameView.setText(ListItemName.get(songIndex));
                albumNameView.setText("");

                Bitmap bitmap = BitmapFactory.decodeFile(ListItemImage.get(songIndex));
                BitmapDrawable d = new BitmapDrawable(bitmap);

                backgroundImageView.setImageBitmap(bitmap);
                trackImageView.setImageBitmap(bitmap);

            }else{
                trackNameView.setText(ListItem.get(songIndex).getItem_name());
                albumNameView.setText(ListItem.get(songIndex).getItem_description());

                Picasso.with(getActivity()).load(ListItem.get(songIndex).getItem_image()).transform(new BlurTransformation(getActivity())).placeholder(R.drawable.no_image).fit().into(backgroundImageView);
                Picasso.with(getActivity()).load(ListItem.get(songIndex).getItem_image()).placeholder(R.drawable.no_image).fit().into(trackImageView);
            }

            // Changing Button Image to pause image
            playButton.setImageResource(R.drawable.pause);

            // set Progress bar values
            seekBarView.setProgress(0);
            seekBarView.setMax(100);

            // Updating progress bar
            updateProgressBar();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private void setUi() {
//        utils = new Utilities();
//
//        seekBarView.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(CircularSeekBar seekBar, int progress, boolean fromUser) {
//
//            }
//
//            @Override
//            public void onStartTrackingTouch(CircularSeekBar seekBar) {
//                mHandler.removeCallbacks(mUpdateTimeTask);
//            }
//
//            @Override
//            public void onStopTrackingTouch(CircularSeekBar seekBar) {
//                mHandler.removeCallbacks(mUpdateTimeTask);
//                int totalDuration = freePlayer.getDuration();
//                int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);
//
//                spinner.setVisibility(View.VISIBLE);
//
//                // forward or backward to certain seconds
//                freePlayer.seekTo(currentPosition);
//
//                // update timer progress again
//                updateProgressBar();
//            }
//        });
//
//        // set start position of track
//        currentDuration.setText("00:00");
//        finalDuration.setText("00:00");
//    }

//    private void prepareMusic(int songIndex) {
//
//        try {
//
//            if(freePlayer != null){
//                freePlayer.reset();
//            }
//            else {
//                freePlayer = new FFmpegMediaPlayer();
//
//            }
//
//            freePlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//
////            if(mode == 1){
////                freePlayer.setDataSource(ListItemFile.get(songIndex));
////            }else
//              if (mode == 0){
//                freePlayer.setDataSource(ListItem.get(songIndex).getItem_file());
//            }
//
//
//            freePlayer.prepareAsync();
//            freePlayer.setLooping(true);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        // initially not playing
//        isPlaying = true;
//
//        // disable until prepared
//        playButton.setClickable(false);
//        playButton.setImageResource(R.drawable.stop);
//
//        if(mode == 1){
//            trackNameView.setText(ListItemName.get(songIndex));
//            albumNameView.setText("");
//
//            Bitmap bitmap = BitmapFactory.decodeFile(ListItemImage.get(songIndex));
//            BitmapDrawable d = new BitmapDrawable(bitmap);
//
//            backgroundImageView.setImageBitmap(bitmap);
//            trackImageView.setImageBitmap(bitmap);
//
////            Picasso.with(getActivity()).load(ListItemImage.get(songIndex)).transform(new BlurTransformation(getActivity())).placeholder(R.drawable.no_image).fit().into(backgroundImageView);
////            Picasso.with(getActivity()).load(ListItemImage.get(songIndex)).placeholder(R.drawable.no_image).fit().into(trackImageView);
//
//        }else if (mode == 0){
//            trackNameView.setText(ListItem.get(songIndex).getItem_name());
//            albumNameView.setText(ListItem.get(songIndex).getItem_description());
//
//            Picasso.with(getActivity()).load(ListItem.get(songIndex).getItem_image()).transform(new BlurTransformation(getActivity())).placeholder(R.drawable.no_image).fit().into(backgroundImageView);
//            Picasso.with(getActivity()).load(ListItem.get(songIndex).getItem_image()).placeholder(R.drawable.no_image).fit().into(trackImageView);
//
//        }
//
//        freePlayer.setOnPreparedListener(new FFmpegMediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(FFmpegMediaPlayer mp) {
//                spinner.setVisibility(View.GONE);
//
//                // restore button
//                playButton.setClickable(true);
//                playButton.setImageResource(R.drawable.pause);
//
//                freePlayer.start();
//
//                seekBarView.setProgress(0);
//                 updateProgressBar();
//
//                isPlaying = true;
//
//                // play/pause
//                playButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (!isPlaying) {
//                            freePlayer.start();
//                            freePlayer.setLooping(true);
//                            playButton.setImageResource(R.drawable.pause);
//                            isPlaying = true;
//                        } else {
//                            freePlayer.pause();
//                            isPlaying = false;
//                            playButton.setImageResource(R.drawable.ic_action_play);
//                        }
//                    }
//                });
//
//                repeatButton.setOnClickListener(new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View arg0) {
//                        if (isRepeat) {
//                            isRepeat = false;
//                            freePlayer.setLooping(false);
//                            repeatButton.setImageResource(R.drawable.repeat_off);
//                        } else {
//                            // make repeat to true
//                            isRepeat = true;
//                            freePlayer.setLooping(true);
//                            repeatButton.setImageResource(R.drawable.repeat_on);
//                        }
//                    }
//                });
//
//            }
//        });
//
//        freePlayer.setOnSeekCompleteListener(new FFmpegMediaPlayer.OnSeekCompleteListener() {
//            @Override
//            public void onSeekComplete(FFmpegMediaPlayer fFmpegMediaPlayer) {
//                currentDuration.setText("00:00");
//                finalDuration.setText("00:00");
//
//                spinner.setVisibility(View.GONE);
//            }
//        });
//
//
//        freePlayer.setOnBufferingUpdateListener(new FFmpegMediaPlayer.OnBufferingUpdateListener() {
//            @Override
//            public void onBufferingUpdate(FFmpegMediaPlayer fFmpegMediaPlayer, int i) {
//                ToastUtil.showShortToastMessage(getActivity(), "Buffering call");
//            }
//        });
//
//        freePlayer.setOnCompletionListener(new FFmpegMediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(FFmpegMediaPlayer fFmpegMediaPlayer) {
////                if (isRepeat) {
//                    // repeat is on play same song again
////                    prepareMusic(ListPosition);
////                prepareMusic(ListPosition);
//
//                playButton.setClickable(true);
//                playButton.setImageResource(R.drawable.ic_action_play);
//                fFmpegMediaPlayer.start();
//                fFmpegMediaPlayer.setLooping(true);
////                if (!isPlaying) {
////                    freePlayer.start();
////                    playButton.setImageResource(R.drawable.pause);
////                    isPlaying = true;
////                }
//
////                try {
////                    freePlayer.prepareAsync();
////                    freePlayer.setLooping(true);
////                    freePlayer.start();
////                } catch (IOException e) {
////                    e.printStackTrace();
////                }
////                freePlayer.release();
////                prepareMusic(ListPosition);
////                freePlayer.setLooping(true);
////                freePlayer.start();
////                updateProgressBar();
//                ToastUtil.showShortToastMessage(getActivity(), "REPEAT");
////                }
////                else {
////                    playButton.setClickable(true);
////                    playButton.setImageResource(R.drawable.ic_action_play);
////                    prepareMusic(ListPosition);
////                }
//            }
//        });
//
//
//    }

    class AsyncRunner extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.GONE);

            playSong(ListPosition);
        }
    }

    class AsyncRunnerNext extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.GONE);

            if (ListPosition < (ListItem.size() - 1)) {
                playSong(ListPosition + 1);
                ListPosition = ListPosition + 1;
            }

            if (ListPosition < (ListItemFile.size() - 1)) {
                playSong(ListPosition + 1);
                ListPosition = ListPosition + 1;
            }

            else {
                // play first song
                playSong(ListPosition);
            }
        }
    }

    class AsyncRunnerPrevious extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.GONE);

            if(ListPosition > 0){
                playSong(ListPosition - 1);
                ListPosition = ListPosition - 1;
            }else {
                // play last song

                if (ListItem.size() > 0) {
                    playSong(ListItem.size() - 1);
                    ListPosition = ListItem.size() - 1;
                } else if (ListItemFile.size() > 0) {
                    playSong(ListItemFile.size() - 1);
                    ListPosition = ListItemFile.size() - 1;
                }
            }
        }
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {

            long totalDuration = mPlayer.getDuration();
            long currentDurations = mPlayer.getCurrentPosition();

            // Displaying Total Duration time
            finalDuration.setText("" + utils.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            currentDuration.setText("" + utils.milliSecondsToTimer(currentDurations));

            // Updating progress bar
            int progress = (int) (utils.getProgressPercentage(currentDurations, totalDuration));
            //Log.d("Progress", ""+progress);
            seekBarView.setProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };


    public void updateProgressBar() {

        if (mPlayer != null) {
            mHandler.postDelayed(mUpdateTimeTask, 100);
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mPlayer.release();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mPlayer.isPlaying()) {
            if (mPlayer != null) {
                mPlayer.pause();
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {

                    if (mode == 1) {
                        Downloads downloads = new Downloads();
                        mContext.ReplaceFragement(downloads);
                    } else if (mode == 0) {
                        CategoryList categoryList = new CategoryList();

                        Bundle bundle = new Bundle();
                        bundle.putSerializable("CAT_ID", homeModel);
                        categoryList.setArguments(bundle);

                        mContext.ReplaceFragement(categoryList);
                    }
                }
                return false;
            }
        });
    }


    public class BlurTransformation implements Transformation {

        RenderScript rs;

        public BlurTransformation(Context context) {
            super();
            rs = RenderScript.create(context);
        }

        @Override
        public Bitmap transform(Bitmap bitmap) {
            // Create another bitmap that will hold the results of the filter.
            Bitmap blurredBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

            // Allocate memory for Renderscript to work with
            Allocation input = Allocation.createFromBitmap(rs, blurredBitmap, Allocation.MipmapControl.MIPMAP_FULL, Allocation.USAGE_SHARED);
            Allocation output = Allocation.createTyped(rs, input.getType());

            // Load up an instance of the specific script that we want to use.
            ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            script.setInput(input);

            // Set the blur radius
            script.setRadius(5f);

            // Start the ScriptIntrinisicBlur
            script.forEach(output);

            // Copy the output to the blurred bitmap
            output.copyTo(blurredBitmap);

            bitmap.recycle();

            return blurredBitmap;
        }

        @Override
        public String key() {
            return "blur";
        }
    }
}