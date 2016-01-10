package com.goldenant.bhaktisangrah.adapter;

import android.app.ProgressDialog;
import android.content.Context;
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
import com.goldenant.bhaktisangrah.fragment.Streaming;
import com.goldenant.bhaktisangrah.model.HomeModel;
import com.goldenant.bhaktisangrah.model.SubCategoryModel;
import com.squareup.picasso.Picasso;

import org.apache.http.util.ByteArrayBuffer;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Adite on 03-01-2016.
 */
public class CategoryAdapter extends ArrayAdapter<HomeModel> {
    private LayoutInflater layoutInflater;

    public ArrayList<SubCategoryModel> mItem = new ArrayList<SubCategoryModel>();

    public MainActivity mContext;

    int resource;

    private static String fileName, imagename;

    private ProgressDialog pDialog;

    private Typeface font;
    private HomeModel homeModel;
    int pos;

    static class ViewHolder {

        private CircularImageView cat_image;
        private ImageButton play, download;
        private TextView tv_title, tv_desc, tv_duration;

    }

    public CategoryAdapter(MainActivity context, int resource, ArrayList<SubCategoryModel> list, HomeModel homeModel) {
        super(context, resource);
        mContext = context;
        this.mItem = list;
        this.resource = resource;
        this.homeModel = homeModel;
        font = Typeface.createFromAsset(mContext.getAssets(), "ProximaNova-Light.otf");
        layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

                        Fragment investProgramDetail = new Streaming();
                        Bundle bundle = new Bundle();

                        bundle.putInt("mode", 0);
                        bundle.putSerializable("data", mItem);
                        bundle.putInt("position", position);
                        bundle.putSerializable("CAT_ID", homeModel);

                        investProgramDetail.setArguments(bundle);
                        mContext.ReplaceFragement(investProgramDetail);
                    }
                });

                mViewHolder.download.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fileName = mItem.get(position).getDownload_name() + ".mp3";
                        imagename = mItem.get(position).getDownload_name() + ".jpg";
                        pos = position;
                        new DownloadFileFromURL().execute(mItem.get(position).getItem_file());
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
            pDialog.setCancelable(true);
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

                long total = 0;

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
        }

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