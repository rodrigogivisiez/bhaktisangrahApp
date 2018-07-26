package com.goldenant.bhaktisangrah.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.goldenant.bhaktisangrah.MainActivity;
import com.goldenant.bhaktisangrah.R;
import com.goldenant.bhaktisangrah.common.ui.CircularImageView;
import com.goldenant.bhaktisangrah.common.util.ClickGuard;
import com.goldenant.bhaktisangrah.common.util.Constants;
import com.goldenant.bhaktisangrah.common.util.InternetStatus;
import com.goldenant.bhaktisangrah.common.util.ToastUtil;
import com.goldenant.bhaktisangrah.fragment.CategoryList;
import com.goldenant.bhaktisangrah.fragment.Streaming;

import com.goldenant.bhaktisangrah.helpers.StorageUtil;
import com.goldenant.bhaktisangrah.model.HomeModel;
import com.goldenant.bhaktisangrah.model.SubCategoryModel;
import com.squareup.picasso.Picasso;

import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.goldenant.bhaktisangrah.MainActivity.Broadcast_PLAY_NEW_AUDIO;
import static com.goldenant.bhaktisangrah.common.util.Constants.CATEGORY;


/**
 * Created by Adite on 03-01-2016.
 */
public class CategoryAdapter extends ArrayAdapter<HomeModel> {
    private LayoutInflater layoutInflater;

    public ArrayList<SubCategoryModel> mItem = new ArrayList<SubCategoryModel>();

    public MainActivity mContext;

    int resource;

    private String fileName, imagename, file_id;

    private ProgressDialog pDialog;

    private Typeface font;
    private HomeModel homeModel;
    ArrayList<String> songsID = new ArrayList<String>();
    int pos;
    private String aDataid;

    static class ViewHolder {

        private CircularImageView cat_image;
        private ImageButton play, download;
        private TextView tv_title, tv_desc, tv_duration;
        private String urls;
        private String imgpath;
        private LinearLayout layout_main;
    }

    public CategoryAdapter(MainActivity context, int resource, ArrayList<SubCategoryModel> list, HomeModel homeModel) {
        super(context, resource);
        mContext = context;
        this.mItem = list;
        this.resource = resource;
        this.homeModel = homeModel;
        font = Typeface.createFromAsset(mContext.getAssets(), "ProximaNova-Light.otf");
        layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        try {
            //Get path of song name
            File myFile = new File("/data/data/" + mContext.getApplicationContext().getPackageName() + "/Bhakti sagar/BhaktiSagarID.txt");

            if (myFile.exists()) {
                FileInputStream fIn = new FileInputStream(myFile);
                BufferedReader myReader = new BufferedReader(
                        new InputStreamReader(fIn));
                String aDataRow = "";
//            String aBuffer = "";
                while ((aDataRow = myReader.readLine()) != null) {
//                aBuffer += aDataRow + "\n";
                    songsID.add(aDataRow);
                }

                myReader.close();
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public int getCount() {
        return mItem.size();
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        final ViewHolder mViewHolder;
        LayoutInflater inflater = (LayoutInflater) (mContext).getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        try {

            if (itemView == null) {

                itemView = inflater.inflate(R.layout.subcategory_list_item, null, true);

                mViewHolder = new ViewHolder();

                mViewHolder.cat_image = (CircularImageView) itemView.findViewById(R.id.image);

                mViewHolder.play = (ImageButton) itemView.findViewById(R.id.play);
                mViewHolder.download = (ImageButton) itemView.findViewById(R.id.download);

                mViewHolder.tv_title = (TextView) itemView.findViewById(R.id.tv_title);
                mViewHolder.tv_desc = (TextView) itemView.findViewById(R.id.tv_desc);
                mViewHolder.tv_duration = (TextView) itemView.findViewById(R.id.tv_duration);

                mViewHolder.layout_main = (LinearLayout) itemView.findViewById(R.id.layout_main);

                itemView.setTag(mViewHolder);

            } else {
                mViewHolder = (ViewHolder) itemView.getTag();
            }

            if (mItem.get(position) != null) {

                mViewHolder.tv_title.setTypeface(font);
                mViewHolder.tv_desc.setTypeface(font);
                mViewHolder.tv_duration.setTypeface(font);

                mViewHolder.tv_title.setText(mItem.get(position).getItem_name());
                mViewHolder.tv_desc.setText(mItem.get(position).getItem_description());

                Picasso.with(mContext).load(mItem.get(position).getItem_image()).placeholder(R.drawable.no_image).into(mViewHolder.cat_image);

                mViewHolder.play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //clear cached playlist
                        //Store Serializable audioList to SharedPreferences
                        StorageUtil storage = new StorageUtil(getApplicationContext());
                        if (storage.loadAudio() != null) {
                            ArrayList<SubCategoryModel> nowPlayingList = storage.loadAudio();
                            SubCategoryModel currentlyPlaying = nowPlayingList.get(storage.loadAudioIndex());
                            SubCategoryModel selectedAudio = mItem.get(position);
                            if (selectedAudio.getItem_file().equalsIgnoreCase(currentlyPlaying.getItem_file())) {
                                Fragment investProgramDetail = new Streaming();
                                Bundle bundle = new Bundle();
                                bundle.putString("isFrom", CATEGORY);
                                bundle.putInt("mode", 0);
                                bundle.putSerializable("data", storage.loadAudio());
                                bundle.putInt("position", storage.loadAudioIndex());
                                bundle.putSerializable("CAT_ID", homeModel);
                                investProgramDetail.setArguments(bundle);
                                storage.storeMode(0);
                                mContext.setMode(0);
                                mContext.ReplaceFragement(investProgramDetail);
                                return;
                            } else if (mContext.serviceBound && mContext.isPlaying()) {
                                storage.clearCachedAudioPlaylist();
                                storage.storeAudio(mItem);
                                storage.storeAudioIndex(position);
                                storage.storeMode(0);
                                storage.storeIsPlayingFrom("Category");
                                mContext.setShuffleMode(false);
                                mContext.setRepeatMode(false);
                                mContext.setMode(0);
                                mContext.stopProgressHandler();
                                Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
                                mContext.sendBroadcast(broadcastIntent);
                            } else if (mContext.serviceBound) {
                                storage.clearCachedAudioPlaylist();
                                storage.storeAudio(mItem);
                                storage.storeAudioIndex(position);
                                storage.storeMode(0);
                                storage.storeIsPlayingFrom("Category");
                                mContext.setShuffleMode(false);
                                mContext.setRepeatMode(false);
                                mContext.setMode(0);
                                mContext.stopProgressHandler();
                                mContext.playSong();
                            }

                        } else if (mContext.serviceBound) {
                            storage.clearCachedAudioPlaylist();
                            storage.storeAudio(mItem);
                            storage.storeAudioIndex(position);
                            storage.storeMode(0);
                            storage.storeIsPlayingFrom("Category");
                            mContext.setShuffleMode(false);
                            mContext.setRepeatMode(false);
                            mContext.setMode(0);
                            mContext.stopProgressHandler();
                            mContext.playSong();
                        }


                        Fragment investProgramDetail = new Streaming();
                        Bundle bundle = new Bundle();
                        bundle.putString("isFrom", CATEGORY);
                        bundle.putInt("mode", 0);
                        bundle.putSerializable("data", mItem);
                        bundle.putInt("position", position);
                        bundle.putSerializable("CAT_ID", homeModel);

                        investProgramDetail.setArguments(bundle);
                        mContext.ReplaceFragement(investProgramDetail);
                    }
                });

                mViewHolder.layout_main.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //clear cached playlist
                        //Store Serializable audioList to SharedPreferences
                        StorageUtil storage = new StorageUtil(getApplicationContext());
                        if (storage.loadAudio() != null) {
                            ArrayList<SubCategoryModel> nowPlayingList = storage.loadAudio();
                            SubCategoryModel currentlyPlaying = nowPlayingList.get(storage.loadAudioIndex());
                            SubCategoryModel selectedAudio = mItem.get(position);
                            if (selectedAudio.getItem_file().equalsIgnoreCase(currentlyPlaying.getItem_file())) {
                                Fragment investProgramDetail = new Streaming();
                                Bundle bundle = new Bundle();
                                bundle.putString("isFrom", CATEGORY);
                                bundle.putInt("mode", 0);
                                bundle.putSerializable("data", storage.loadAudio());
                                bundle.putInt("position", storage.loadAudioIndex());
                                bundle.putSerializable("CAT_ID", homeModel);
                                investProgramDetail.setArguments(bundle);
                                storage.storeMode(0);
                                mContext.stopProgressHandler();
                                mContext.setMode(0);
                                mContext.ReplaceFragement(investProgramDetail);
                                return;
                            } else if (mContext.serviceBound && mContext.isPlaying()) {
                                storage.clearCachedAudioPlaylist();
                                storage.storeAudio(mItem);
                                storage.storeAudioIndex(position);
                                storage.storeMode(0);
                                storage.storeIsPlayingFrom("Category");
                                mContext.setShuffleMode(false);
                                mContext.setRepeatMode(false);
                                mContext.setNoOfRepeats(0);
                                mContext.setMode(0);
                                mContext.stopProgressHandler();
                                Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
                                mContext.sendBroadcast(broadcastIntent);
                            } else if (mContext.serviceBound) {
                                storage.clearCachedAudioPlaylist();
                                storage.storeAudio(mItem);
                                storage.storeAudioIndex(position);
                                storage.storeMode(0);
                                storage.storeIsPlayingFrom("Category");
                                mContext.setShuffleMode(false);
                                mContext.setRepeatMode(false);
                                mContext.setNoOfRepeats(0);
                                mContext.setMode(0);
                                mContext.stopProgressHandler();
                                mContext.playSong();
                            }

                        } else if (mContext.serviceBound) {
                            storage.clearCachedAudioPlaylist();
                            storage.storeAudio(mItem);
                            storage.storeAudioIndex(position);
                            storage.storeMode(0);
                            storage.storeIsPlayingFrom("Category");
                            mContext.setShuffleMode(false);
                            mContext.setRepeatMode(false);
                            mContext.setNoOfRepeats(0);
                            mContext.setMode(0);
                            mContext.playSong();
                        }


                        Fragment investProgramDetail = new Streaming();
                        Bundle bundle = new Bundle();
                        bundle.putString("isFrom", CATEGORY);
                        bundle.putInt("mode", 0);
                        bundle.putSerializable("data", mItem);
                        bundle.putInt("position", position);
                        bundle.putSerializable("CAT_ID", homeModel);

                        investProgramDetail.setArguments(bundle);
                        mContext.ReplaceFragement(investProgramDetail);
                    }
                });

                //mViewHolder.download.setVisibility(View.VISIBLE);
                mViewHolder.download.setBackgroundResource(R.drawable.download);


                if (songsID.size() > 0) {

                    for (int i = 0; i < songsID.size(); i++) {

                        if (mItem.get(position).getItem_id().equalsIgnoreCase(songsID.get(i))) {

                            mViewHolder.download.setBackgroundResource(R.drawable.download_finished);
                            mViewHolder.download.setOnClickListener(null);
                            mViewHolder.download.setClickable(false);
                            ClickGuard.guard(mViewHolder.download);
                            break;
                        }
                    }
                }

                mViewHolder.download.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fileName = mItem.get(position).getDownload_name() + ".mp3";
                        imagename = mItem.get(position).getDownload_name() + ".jpg";
                        file_id = mItem.get(position).getItem_id();
                        pos = position;

//                        mViewHolder.urls = mItem.get(position).getItem_file();
//                        mViewHolder.download.setClickable(false);
                        boolean isInternet = new InternetStatus().isInternetOn(mContext);
                        if (isInternet) {
                            new DownloadFileFromURL().execute(mItem.get(position).getItem_file());
                        } else {
                            ToastUtil.showLongToastMessage(mContext, mContext.getString(R.string.no_internet_connection_found));

                            return;
                        }
                    }
                });

            }
        } catch (Exception e) {

            e.printStackTrace();
        }

        return itemView;
    }

    //Download mp3 start
    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(mContext);
            pDialog.setMessage("Downloading file. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setMax(100);
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * Downloading file in background thread
         */
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                // getting file length
                int lenghtOfFile = conection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                File myDir = new File("/data/data/"
                        + mContext.getApplicationContext()
                        .getPackageName() + "/Bhakti sagar/mp3");
                myDir.mkdirs();

                if (!myDir.exists()) {
                    myDir.mkdirs();
                }

                // Output stream to write file
                File outputFile = new File(myDir, fileName);

//                if(!outputFile.exists())
//                {
                OutputStream output = new FileOutputStream(outputFile);

                byte data[] = new byte[1024];

                double total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();
//                }
//                else
//                {
//                    ToastUtil.showLongToastMessage(mContext,"You have already download this file");
//                }


                //Download image start
                myDir = new File("/data/data/" + mContext.getApplicationContext()
                        .getPackageName() + "/Bhakti sagar/images/");
                myDir.mkdirs();

                try {
                    File image = new File(myDir, imagename);

                    if (!image.exists()) {
                        try {
                            Bitmap Image = getImage(mItem.get(pos).getItem_image());

                            FileOutputStream out = new FileOutputStream(image);
                            Image.compress(Bitmap.CompressFormat.JPEG, 90, out);
                            out.flush();
                            out.close();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception ex) {
                    System.out.println("Error in downloading image:"
                            + ex.toString());
                }
                //End

                //Writing name into file start
                try {
                    //Write path of song name
                    File myFile = new File("/data/data/" + mContext.getApplicationContext()
                            .getPackageName() + "/Bhakti sagar/BhaktiSagar.txt");


                    if (!myFile.exists()) {
                        myFile.createNewFile();
                    }

                    String aDataRow = mItem.get(pos).getItem_name();

                    Scanner scanner = new Scanner(myFile);
                    List<String> list = new ArrayList<>();
                    while (scanner.hasNextLine()) {
                        list.add(scanner.nextLine());
                    }

                    if (!list.contains(aDataRow)) {
                        String aBuffer = "";
                        FileWriter fileWritter = new FileWriter(myFile, true);
                        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
                        aBuffer += aDataRow + "\n";
                        bufferWritter.write(aBuffer);
                        bufferWritter.close();
                    }

                    //Writing ID into file start
                    try {
                        //Write path of song name
                        File myFileid = new File("/data/data/" + mContext.getApplicationContext()
                                .getPackageName() + "/Bhakti sagar/BhaktiSagarID.txt");


                        if (!myFileid.exists()) {
                            myFileid.createNewFile();
                        }

                        aDataid = mItem.get(pos).getItem_id();

                        Scanner scannerid = new Scanner(myFileid);
                        List<String> listid = new ArrayList<>();
                        while (scannerid.hasNextLine()) {
                            listid.add(scannerid.nextLine());
                        }

                        if (!listid.contains(aDataid)) {
                            String aBuffer = "";
                            FileWriter fileWritter = new FileWriter(myFileid, true);
                            BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
                            aBuffer += aDataid + "\n";
                            bufferWritter.write(aBuffer);
                            bufferWritter.close();
                        }
                    } catch (Exception e) {

                        e.printStackTrace();
                    }

                    //Write path of image
                    File file = new File("/data/data/" + mContext.getApplicationContext()
                            .getPackageName() + "/Bhakti sagar/BhaktiSagarImage.txt");

                    if (!file.exists()) {
                        file.createNewFile();
                    }

                    String DataRow = "/data/data/" + mContext.getApplicationContext()
                            .getPackageName() + "/Bhakti sagar/images/" + imagename;

                    Scanner scanner1 = new Scanner(myFile);
                    List<String> list1 = new ArrayList<>();
                    while (scanner1.hasNextLine()) {
                        list1.add(scanner1.nextLine());
                    }

                    if (!list1.contains(DataRow)) {
                        String Buffer = "";
                        FileWriter filewritter = new FileWriter(file, true);
                        BufferedWriter bufferwritter = new BufferedWriter(filewritter);
                        Buffer += DataRow + "\n";
                        bufferwritter.write(Buffer);
                        bufferwritter.close();
                    }


                    //Write path of songs
                    File fileSong = new File("/data/data/" + mContext.getApplicationContext()
                            .getPackageName() + "/Bhakti sagar/BhaktiSagarSongs.txt");

                    if (!fileSong.exists()) {
                        fileSong.createNewFile();
                    }

                    String DataRowSongs = "/data/data/" + mContext.getApplicationContext()
                            .getPackageName() + "/Bhakti sagar/mp3/" + fileName;

                    Scanner scanner2 = new Scanner(myFile);
                    List<String> list2 = new ArrayList<>();
                    while (scanner2.hasNextLine()) {
                        list2.add(scanner2.nextLine());
                    }

                    if (!list2.contains(DataRowSongs)) {
                        String BufferSongs = "";
                        FileWriter filewritterSong = new FileWriter(fileSong, true);
                        BufferedWriter bufferwritterSong = new BufferedWriter(filewritterSong);
                        BufferSongs += DataRowSongs + "\n";
                        bufferwritterSong.write(BufferSongs);
                        bufferwritterSong.close();
                    }

                    //Writing duration into file start
                    try {
                        //Write path of song name
                        File myFileid = new File("/data/data/" + mContext.getApplicationContext()
                                .getPackageName() + "/Bhakti sagar/BhaktiSagarDuration.txt");


                        if (!myFileid.exists()) {
                            myFileid.createNewFile();
                        }

                        String aDataDuration = mItem.get(pos).getDuration();

                        Scanner scannerid = new Scanner(myFileid);
                        List<String> listid = new ArrayList<>();
                        while (scannerid.hasNextLine()) {
                            listid.add(scannerid.nextLine());
                        }

                        if (!listid.contains(aDataDuration)) {
                            String aBuffer = "";
                            FileWriter fileWritter = new FileWriter(myFileid, true);
                            BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
                            aBuffer += aDataDuration + "\n";
                            bufferWritter.write(aBuffer);
                            bufferWritter.close();
                        }
                    } catch (Exception e) {

                        e.printStackTrace();
                    }



                } catch (Exception e) {
                    Log.d("ERROR", "" + e.getMessage());
                }
                //End

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            super.onProgressUpdate(progress);
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            pDialog.dismiss();

            // Displaying downloaded image into image view
            // Reading image path from sdcard
            String imagePath = Environment.getExternalStorageDirectory()
                    .toString() + fileName;
            // setting downloaded into image view
            // my_image.setImageDrawable(Drawable.createFromPath(imagePath));
            Log.d("Path_of_file==>", "" + imagePath);
            StorageUtil storage = new StorageUtil(getApplicationContext());
            if (storage.loadIsPlayingFrom() != null) {
                if (storage.loadIsPlayingFrom().equalsIgnoreCase("Download")) {
                    SubCategoryModel currentSong = mItem.get(pos);
                    ArrayList<SubCategoryModel> currentList = storage.loadAudio();
                    String fileSource = "/data/data/" + mContext.getApplicationContext().getPackageName() + "/Bhakti sagar/mp3/" + currentSong.getDownload_name()+ ".mp3";
                    String imageSource = "/data/data/" + mContext.getApplicationContext().getPackageName() + "/Bhakti sagar/images/" + currentSong.getDownload_name()+ ".jpg";
                    currentSong.setItem_file(fileSource);
                    currentSong.setItem_image(imageSource);
                    currentList.add(currentSong);
                    storage.storeAudio(currentList);
                }
            }

            ShowAlert();
            if (aDataid != null) {
                mContext.updateSongStatus(aDataid, 0);
            }

        }

    }

    private void ShowAlert() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setMessage("Download successfully");
        alertDialogBuilder.setTitle("Bhakti Sagar");
        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int arg1) {

                Bundle bundle = new Bundle();
                bundle.putSerializable("CAT_ID", homeModel);

                CategoryList categoryList = new CategoryList();
                categoryList.setArguments(bundle);
                mContext.ReplaceFragement(categoryList);

                dialog.cancel();
            }
        });


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public static Bitmap getImage(String url) {
        try {
            URL imageUrl = new URL(url);
            URLConnection ucon = imageUrl.openConnection();

            InputStream is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

            ByteArrayBuffer baf = new ByteArrayBuffer(500000);
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }

            System.gc();
            Bitmap bitmap = BitmapFactory.decodeByteArray(baf.toByteArray(), 0,
                    baf.toByteArray().length);

            return bitmap;
        } catch (Exception e) {
            Log.d("ImageManager", "Error: " + e.toString());
        }
        return null;
    }
    //Download mp3 emd
}