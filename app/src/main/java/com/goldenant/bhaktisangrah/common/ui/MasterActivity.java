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
import com.goldenant.bhaktisangrah.service.MusicService;

import java.util.ArrayList;

/**
 * Created by Adite on 02-01-2016.
 */
public class MasterActivity extends AppCompatActivity {

    public boolean isInternet;

    public static Typeface font;

    ProgressDialog mProgressDialog;
    public MediaPlayer mPlayer;

    public Bundle bundle = new Bundle();

    public static ArrayList<HomeModel> CatArray = new ArrayList<HomeModel>();

    public static int listScreen = 0, playScreen = 0;

    public static MusicService musicSrv = null;
    public static boolean musicBound = false;
    public static Intent playIntent;


    public static MediaPlayerService player;
    public static boolean serviceBound = false;
    public static BroadcastReceiver mPlaybackStatus;
    public static final ArrayList<MusicStateListener> mMusicStateListener = new ArrayList<>();


    public static ArrayList<SubCategoryModel> songsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isInternet = new InternetStatus().isInternetOn(this);
        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

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
        if (mPlayer != null) {

            mPlayer.release();
        }
        try {
            unregisterReceiver(mPlaybackStatus);
        } catch (final Throwable e) {
        }
        mMusicStateListener.clear();
    }

    @Override
    public void onPause() {
        super.onPause();

        /*if(mPlayer != null){

            if (mPlayer.isPlaying()) {
                if (mPlayer != null) {
                    mPlayer.pause();
                }
            }
        }*/

    }

    public void setNextTrack() {
        player.playNextTrack();
    }
    public boolean isPlayerPrepared() {
        if(player==null){
            return false;
        }
        return player.isPlayerPrepared();
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

    public void setRepeatMode(Boolean bool){
        player.setRepeat(bool);
    }

    public void setShuffleMode(Boolean bool){
        player.setShuffleMode(bool);
    }

    public void playSong() {
        player.playSong();
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

    public void songPicked(View view) {
        musicSrv.setSong(Integer.parseInt(view.getTag().toString()));
        musicSrv.playSong();
    }

    public void stopService() {
        stopService(playIntent);
        musicSrv = null;
        musicBound = false;
        // System.exit(0);
    }


    @Override
    protected void onStop() {
        super.onStop();
    }


}
