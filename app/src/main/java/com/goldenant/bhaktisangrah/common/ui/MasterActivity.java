package com.goldenant.bhaktisangrah.common.ui;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.goldenant.bhaktisangrah.R;
import com.goldenant.bhaktisangrah.common.util.InternetStatus;
import com.goldenant.bhaktisangrah.helpers.MusicStateListener;
import com.goldenant.bhaktisangrah.model.HomeModel;
import com.goldenant.bhaktisangrah.model.SubCategoryModel;
import com.goldenant.bhaktisangrah.service.MediaPlayerService;


import java.util.ArrayList;

/**
 * Created by Adite on 02-01-2016.
 */
public class MasterActivity extends AppCompatActivity {

    public boolean isInternet;

    public static Typeface font;

    ProgressDialog mProgressDialog;

    public Bundle bundle = new Bundle();

    public static ArrayList<HomeModel> CatArray = new ArrayList<HomeModel>();

    public static int listScreen = 0, playScreen = 0;

    public static boolean musicBound = false;
    public static Intent playIntent;


    public static MediaPlayerService player;
    public static boolean serviceBound = false;
    public static BroadcastReceiver mPlaybackStatus;
    public static final ArrayList<MusicStateListener> mMusicStateListener = new ArrayList<>();
    public static boolean isFromRadio=false;


    public static ArrayList<SubCategoryModel> songsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isInternet = new InternetStatus().isInternetOn(this);
    }

    public void setTitle(String title) {
        TextView titleTextView = (TextView) findViewById(R.id.title_text);
        titleTextView.setVisibility(View.VISIBLE);
        titleTextView.setText(title);

    }

    public void setTextSize(int title_text_size) {
        TextView titleTextView = (TextView) findViewById(R.id.title_text);
        titleTextView.setTextSize(title_text_size);
    }

    public Typeface getTypeFace() {
        font = Typeface.createFromAsset(getAssets(), "ProximaNova-Light.otf");
        return font;
    }

    public void showDrawer() {
        ImageView drawer_indicator = (ImageView) findViewById(R.id.drawer_indicator);
        drawer_indicator.setVisibility(View.VISIBLE);
    }

    public void hideDrawer() {
        ImageView drawer_indicator = (ImageView) findViewById(R.id.drawer_indicator);
        drawer_indicator.setVisibility(View.INVISIBLE);
    }

    public void showDrawerBack() {
        ImageButton drawer_back = (ImageButton) findViewById(R.id.drawer_back);
        drawer_back.setVisibility(View.VISIBLE);
    }

    public void hideDrawerBack() {
        ImageButton drawer_back = (ImageButton) findViewById(R.id.drawer_back);
        drawer_back.setVisibility(View.INVISIBLE);
    }


    public void showWaitIndicator(boolean state) {
        showWaitIndicator(state, "");
    }

    public void showWaitIndicator(boolean state, String message) {
        try {
            try {

                if (state) {

                    mProgressDialog = new ProgressDialog(MasterActivity.this,
                            R.style.TransparentProgressDialog);
                    mProgressDialog
                            .setProgressStyle(android.R.style.Widget_ProgressBar_Large);
                    mProgressDialog.setCancelable(false);
                    mProgressDialog.show();
                } else {
                    mProgressDialog.dismiss();
                }

            } catch (Exception e) {


            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(mPlaybackStatus);
        } catch (final Throwable e) {
        }

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void setNextTrack() {
        player.playNextTrack();
    }
    public static boolean isPlayerPrepared() {
        if(player==null){
            return false;
        }
        return player.isPlayerPrepared();
    }
    public static boolean isSongCompleted() {
        if(player==null){
            return false;
        }
        return player.isSongCompleted();
    }

    public void setPreviousTrack() {
        player.playPreviousTrack();
    }

    public void seekTo(int currentPosition) {
        player.seekTo(currentPosition);
    }

    public long getDuration() {
        return player.getDuration();
    }

    public long getCurrentPosition() {
        return player.getCurrentPosition();
    }

    public boolean isPlaying() {
       if(player!=null)
        return player.isMediaPlaying();
       else
           return false;
    }

    public void setShuffleMode(Boolean bool){
        player.setShuffleMode(bool);
    }

    public void setRepeatMode(Boolean bool){
        player.setRepeat(bool);
    }

    public boolean isShuffle() {
        return player.isShuffle();
    }

    public boolean isRepeat() {
        return player.isRepeat();
    }

    public void setNoOfRepeats(int count){
        player.setNoOfRepeats(count);
    }
    public int getNoOfRepeats(){
       return player.getNoOfRepeats();
    }


    public void playSong() {
        player.playSong();
    }

    public void setMode(int mode){ //from download=1 or Streaming=0
        player.setMode(mode);
    }

    public void startPlaying() {
        player.resumeMediaPlay();
    }

    public void setSongList(ArrayList<SubCategoryModel> ListItem) {
        //pass list
      //  player.setAudioList(null);
        player.setAudioList(ListItem);
    }

    public void setSongPosition(int position) {
        //pass list
        player.setAudioIndex(position);
    }

    public void pauseSong() {
        player.pauseMediaPlay();
    }

    public SubCategoryModel getActiveAudio() {
        return player.getActiveAudio();
    }
    public int getAudioIndex() {
        return player.getAudioIndex();
    }



    @Override
    protected void onStop() {
        super.onStop();
    }


}
