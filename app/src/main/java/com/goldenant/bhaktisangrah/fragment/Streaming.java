package com.goldenant.bhaktisangrah.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.goldenant.bhaktisangrah.MainActivity;
import com.goldenant.bhaktisangrah.R;
import com.goldenant.bhaktisangrah.common.ui.CircularSeekBar;
import com.goldenant.bhaktisangrah.common.ui.MasterActivity;
import com.goldenant.bhaktisangrah.common.ui.MasterFragment;
import com.goldenant.bhaktisangrah.common.util.Constants;
import com.goldenant.bhaktisangrah.common.util.ToastUtil;
import com.goldenant.bhaktisangrah.common.util.Utilities;
import com.goldenant.bhaktisangrah.helpers.MusicStateListener;
import com.goldenant.bhaktisangrah.helpers.StorageUtil;
import com.goldenant.bhaktisangrah.model.HomeModel;
import com.goldenant.bhaktisangrah.model.SubCategoryModel;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Adite on 03-01-2016.
 */
public class Streaming extends MasterFragment implements MusicStateListener {
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
    private Handler mHandler = new Handler();
    private Utilities utils;
    private ProgressBar progressBar;
    //  private AsyncRunner mTask;
    private Boolean isPlaying;
    String imageUrl;
    private Bundle bundle;
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
    ProgressDialog mProgressDialog;

    private InterstitialAd interstitial;

    private AdView adView;
    private InterstitialAd interstitialAd;
    private SubCategoryModel currentlyPlaying;
    private String isFrom;
    private int noOfRepeats;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // get root view
        mContext = (MainActivity) getMasterActivity();
        rootView = inflater.inflate(R.layout.streaming_player, container, false);

        mContext.hideDrawer();
        mContext.showDrawerBack();

        utils = new Utilities();
        ((MainActivity) getActivity()).setMusicStateListenerListener(this);
        MasterActivity.playScreen = MasterActivity.playScreen + 1;
        if (MasterActivity.playScreen == 8) {
            MasterActivity.playScreen = 0;
            loadBigAds();
        }

        bundle = getArguments();

        if (bundle != null) {

            mode = bundle.getInt("mode");
            isFrom = bundle.getString("isFrom");
            Log.e("mode", ":" + String.valueOf(mode));
            Log.e("isFrom", ":" + isFrom);

            ListItem = (ArrayList<SubCategoryModel>) bundle.getSerializable("data");
            ListPosition = bundle.getInt("position");

            Log.d("ListPosition", "" + ListPosition);
            if (bundle.containsKey("CAT_ID")) {
                homeModel = (HomeModel) bundle.getSerializable("CAT_ID");
            }
            mContext.setTitle(" " + ListItem.get(ListPosition).getItem_name());
        }

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

        mContext.drawer_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performBackOperation();
            }
        });


        if (ListItem != null || !ListItem.isEmpty()) {
            updateView(ListItem.get(ListPosition));
        }
        if (mContext.isRepeat()) {
            repeatButton.setBackgroundResource(R.drawable.repeat_on);
            shuffleButton.setBackgroundResource(R.drawable.shuffle_off);
        } else if (mContext.isShuffle()) {
            shuffleButton.setBackgroundResource(R.drawable.shuffle_on);
            repeatButton.setBackgroundResource(R.drawable.repeat_off);
        }


        playButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // check for already playing
                if (mContext.player != null) {

                    if (mContext.isPlaying()) {
                        mContext.pauseSong();
                        // Changing button image to play button
                        playButton.setImageResource(R.drawable.ic_action_play);
                        // }
                    } else {
                        // Resume song
                        mContext.startPlaying();
                        // Changing button image to pause button
                        playButton.setImageResource(R.drawable.pause);
                        // }
                    }
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mHandler.removeCallbacks(mUpdateTimeTask);
                mContext.setNextTrack();
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mHandler.removeCallbacks(mUpdateTimeTask);
                mContext.setPreviousTrack();
            }
        });

        repeatButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                alertFormElements();
              /*  if (isRepeat) {
                    isRepeat = false;
                    ToastUtil.showShortToastMessage(getActivity(), "Repeat OFF");
                    repeatButton.setBackgroundResource(R.drawable.repeat_off);
                } else {
                    // make repeat to true
                    isRepeat = true;
                    ToastUtil.showShortToastMessage(getActivity(), "Repeat ON");
                    // make shuffle to false
                    isShuffle = false;
                    mContext.setShuffleMode(isShuffle);
                    repeatButton.setBackgroundResource(R.drawable.repeat_on);
                    shuffleButton.setBackgroundResource(R.drawable.shuffle_off);
                }
                mContext.setRepeatMode(isRepeat);*/
            }
        });

        shuffleButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (isShuffle) {
                    isShuffle = false;
                    ToastUtil.showShortToastMessage(getActivity(), "Shuffle OFF");
                    shuffleButton.setBackgroundResource(R.drawable.shuffle_off);
                } else {
                    // make repeat to true
                    isShuffle = true;
                    ToastUtil.showShortToastMessage(getActivity(), "Shuffle ON");
                    // make shuffle to false
                    isRepeat = false;
                    mContext.setRepeatMode(isRepeat);
                    shuffleButton.setBackgroundResource(R.drawable.shuffle_on);
                    repeatButton.setBackgroundResource(R.drawable.repeat_off);
                }
                mContext.setShuffleMode(isShuffle);
            }
        });


        seekBarView.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar seekBar, int progress, boolean fromUser) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {
                mHandler.removeCallbacks(mUpdateTimeTask);
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {

                mHandler.removeCallbacks(mUpdateTimeTask);
                //int totalDuration = mContext.mPlayer.getDuration();
                int totalDuration = (int) mContext.getDuration();
                int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

                progressBar.setVisibility(View.VISIBLE);

                //showWaitIndicator(true);
                // forward or backward to certain seconds
                mContext.seekTo(currentPosition);

                // update timer progress again
                updateProgressBar();
                //   }

//                Log.e("isPlayerPrepared------------>>>>>>:" , String.valueOf(currentPosition));
            }
        });


        return rootView;
    }

    private void updateView(SubCategoryModel currentlyPlaying) {
        progressBar.setVisibility(View.GONE);
        Log.e("update view", "UPDATE");
        albumNameView.setText(currentlyPlaying.getItem_description());
        trackNameView.setText(currentlyPlaying.getItem_name());
        if (isVisible()) {
            if (currentlyPlaying.getItem_name() != null) {
                mContext.setTitle(" " + currentlyPlaying.getItem_name());
            }
        }
        if (mode == 1) {
            albumNameView.setText("");
            if (currentlyPlaying.getItem_image() != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(currentlyPlaying.getItem_image());
                BitmapDrawable d = new BitmapDrawable(bitmap);
                backgroundImageView.setImageBitmap(bitmap);
                trackImageView.setImageBitmap(bitmap);
            }
        } else {
            Picasso.with(getActivity()).load(currentlyPlaying.getItem_image()).transform(new BlurTransformation(mContext)).placeholder(R.drawable.no_image).fit().into(backgroundImageView);
            Picasso.with(getActivity()).load(currentlyPlaying.getItem_image()).placeholder(R.drawable.no_image).fit().into(trackImageView);
        }

        if (mContext.isPlayerPrepared()) {
            // Changing button image to play button
            if (!mContext.isPlaying()) {
                playButton.setImageResource(R.drawable.ic_action_play);

            } else {
                playButton.setImageResource(R.drawable.pause);
            }
        }
        mHandler.removeCallbacks(mUpdateTimeTask);
        if (mContext.isPlayerPrepared()) {
            Log.e("restartLoader", "UPDATE_PROGRESS");
            updateProgressBar();
        }
    }

    private void loadBigAds() {
        // Prepare the Interstitial Ad
        interstitial = new InterstitialAd(mContext);
        // Insert the Ad Unit ID
        interstitial.setAdUnitId(getString(R.string.add_unit_id));

        // Request for Ads
        AdRequest adRequest = new AdRequest.Builder().build();
        interstitial.loadAd(adRequest);

        // Prepare an Interstitial Ad Listener
        interstitial.setAdListener(new AdListener() {
            public void onAdLoaded() {
                interstitial.show();
            }
        });
    }

    @Override
    public void restartLoader() {
        mHandler.removeCallbacks(mUpdateTimeTask);
        Log.e("restartLoader", "UPDATE_PROGRESS");
        if (mContext.isPlaying()) {
            updateProgressBar();
        }
    }

    @Override
    public void stopProgressHandler() {
        Log.e("stopProgressHandler", "REMOVE_CALLBACKS");
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onMetaChanged() {
        Log.e("onMetaChanged", "UPDATE_VIEW");
        updateBottomPlayer();

    }

    private void updateBottomPlayer() {
        StorageUtil storage = new StorageUtil(getApplicationContext());
        ArrayList<SubCategoryModel> audioList = storage.loadAudio();
        int audioIndex = storage.loadAudioIndex();
        int mode = storage.loadMode();
        if (audioList == null) {
            return;
        }
        updateView(audioList.get(audioIndex));
    }


    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {

            try {
                if (mContext.isPlaying()) {

                    long totalDuration = mContext.getDuration();
                    long currentDurations = mContext.getCurrentPosition();

                    // Displaying Total Duration time
                    long reduse = totalDuration - currentDurations;
                    finalDuration.setText("-" + utils.milliSecondsToTimer(reduse));
                    // Displaying time completed playing
                    currentDuration.setText("" + utils.milliSecondsToTimer(currentDurations));

                    // Updating progress bar
                    int progress = (int) (utils.getProgressPercentage(currentDurations, totalDuration));
                    //Log.d("Progress", ""+progress);
                    seekBarView.setProgress(progress);

                    // Running this thread after 100 milliseconds
                    mHandler.postDelayed(this, 100);
                }
            } catch (Exception e) {

                e.printStackTrace();
            }


        }
    };


    public void updateProgressBar() {

        try {

            // if (mContext.mPlayer != null) {
            mHandler.postDelayed(mUpdateTimeTask, 100);
            //    }

        } catch (Exception e) {

            e.printStackTrace();

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
                    performBackOperation();
                }
                return false;
            }
        });
    }


    public void performBackOperation() {
        if (isFrom.equalsIgnoreCase(Constants.DOWNLOADS)) {
            Downloads downloads = new Downloads();
            mContext.ReplaceFragement(downloads);
        } else if (isFrom.equalsIgnoreCase(Constants.CATEGORY)) {
            CategoryList categoryList = new CategoryList();

            Bundle bundle = new Bundle();
            if (homeModel != null) {
                bundle.putSerializable("CAT_ID", homeModel);
            }
            categoryList.setArguments(bundle);

            mContext.ReplaceFragement(categoryList);
        } else if (isFrom.equalsIgnoreCase(Constants.HOME)) {
            HomeFragment homeFragment = new HomeFragment();
            mContext.ReplaceFragement(homeFragment);
        }
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


    //* Show AlertDialog with some form elements.

    public void alertFormElements() {


        // * Inflate the XML view. activity_main is in
        // * res/layout/form_elements.xml

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View formElementsView = inflater.inflate(R.layout.form_elements,
                null, false);

        // You have to list down your form elements

        final RadioGroup countRadioGroup = (RadioGroup) formElementsView
                .findViewById(R.id.countRadioGroup);

        RadioButton radio_3=formElementsView.findViewById(R.id.radio_3);
        RadioButton radio_5=formElementsView.findViewById(R.id.radio_5);
        RadioButton radio_9=formElementsView.findViewById(R.id.radio_9);
        RadioButton radio_11=formElementsView.findViewById(R.id.radio_11);
        RadioButton radio_108=formElementsView.findViewById(R.id.radio_108);

        final EditText countEditText = (EditText) formElementsView
                .findViewById(R.id.countEditText);
        Button buttonOn = formElementsView.findViewById(R.id.onButton);
        Button buttonOff = formElementsView.findViewById(R.id.offButton);

        if (mContext.getNoOfRepeats() != 0) {
            if (MasterActivity.isFromRadio) {
                int checkValue = mContext.getNoOfRepeats();
                switch (checkValue){
                    case 3:radio_3.setChecked(true);break;
                    case 5:radio_5.setChecked(true);break;
                    case 9:radio_9.setChecked(true);break;
                    case 11:radio_11.setChecked(true);break;
                    case 108:radio_108.setChecked(true);break;
                }
            } else {
                countEditText.setText(String.valueOf(mContext.getNoOfRepeats()));
            }
        }
        // the alert dialog
        final AlertDialog dialog = new AlertDialog.Builder(mContext).setView(formElementsView)
                .setTitle("How many times to repeat").show();

        buttonOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toastString = "";
                // get selected radio button from radioGroup
                int selectedId = countRadioGroup.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                RadioButton selectedRadioButton = (RadioButton) formElementsView.findViewById(selectedId);
                if (selectedRadioButton != null) {
                    toastString += "Selected radio button is: " + selectedRadioButton.getTag() + "\n";
                    ToastUtil.showLongToastMessage(mContext, toastString);
                    noOfRepeats = Integer.parseInt(selectedRadioButton.getTag().toString());
                    MasterActivity.isFromRadio = true;
                } else {
                    if (TextUtils.isEmpty(countEditText.getText())) {
                        ToastUtil.showLongToastMessage(mContext, "Select how many times to repeat.!");
                        return;
                    }
                    toastString += "Selection is: " + countEditText.getText() + "\n";
                    ToastUtil.showLongToastMessage(mContext, toastString);
                    noOfRepeats = Integer.parseInt(countEditText.getText().toString());
                    MasterActivity.isFromRadio = false;
                }
                // make repeat to true
                isRepeat = true;
                ToastUtil.showShortToastMessage(getActivity(), "Repeat ON");
                // make shuffle to false
                isShuffle = false;
                mContext.setShuffleMode(isShuffle);
                repeatButton.setBackgroundResource(R.drawable.repeat_on);
                shuffleButton.setBackgroundResource(R.drawable.shuffle_off);
                mContext.setNoOfRepeats(noOfRepeats);
                mContext.setRepeatMode(isRepeat);
                dialog.cancel();
            }

        });
        buttonOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // if (isRepeat) {
                    isRepeat = false;
                    ToastUtil.showShortToastMessage(getActivity(), "Repeat OFF");
                    repeatButton.setBackgroundResource(R.drawable.repeat_off);
                    dialog.cancel();
              //  }
                mContext.setNoOfRepeats(0);
                mContext.setRepeatMode(isRepeat);
            }
        });
    }
}