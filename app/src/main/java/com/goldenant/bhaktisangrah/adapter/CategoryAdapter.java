package com.goldenant.bhaktisangrah.adapter;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.TextView;

import com.goldenant.bhaktisangrah.MainActivity;
import com.goldenant.bhaktisangrah.R;
import com.goldenant.bhaktisangrah.common.ui.CircularImageView;
import com.goldenant.bhaktisangrah.fragment.Streaming;
import com.goldenant.bhaktisangrah.model.HomeModel;
import com.goldenant.bhaktisangrah.model.SubCategoryModel;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by Adite on 03-01-2016.
 */
public class CategoryAdapter extends ArrayAdapter<HomeModel>
{
    private LayoutInflater layoutInflater;

    public ArrayList<SubCategoryModel> mItem = new ArrayList<SubCategoryModel>();

    public MainActivity mContext;

    int resource;

    private static String fileName = "god_mantra.mp3";

    private ProgressDialog pDialog;

    public CategoryAdapter(MainActivity context, int resource,ArrayList<SubCategoryModel> list)
    {
        super(context, resource);
        mContext = context;
        this.mItem = list;
        this.resource = resource;
        layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return mItem.size();
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        View rootView = convertView;

        rootView = layoutInflater.inflate(R.layout.subcategory_list_item,null, true);

        CircularImageView cat_image = (CircularImageView) rootView.findViewById(R.id.image);

        ImageButton play = (ImageButton) rootView.findViewById(R.id.play);
        ImageButton download = (ImageButton) rootView.findViewById(R.id.download);

        TextView tv_title = (TextView) rootView.findViewById(R.id.tv_title);
        TextView tv_desc = (TextView) rootView.findViewById(R.id.tv_desc);
        TextView tv_duration = (TextView) rootView.findViewById(R.id.tv_duration);

        tv_title.setTypeface(mContext.getTypeFace());
        tv_desc.setTypeface(mContext.getTypeFace());
        tv_duration.setTypeface(mContext.getTypeFace());

        tv_title.setText(mItem.get(position).getItem_name());
        tv_desc.setText(mItem.get(position).getItem_description());
//        tv_duration.setText(mItem.get(position).getIte);

        Picasso.with(mContext).load(mItem.get(position).getItem_image()).into(cat_image);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Fragment streaming = new Streaming();

                Bundle bundle = new Bundle();
                bundle.putString("item_file", mItem.get(position).getItem_file());
                streaming.setArguments(bundle);
                mContext.ReplaceFragement(streaming);
            }
        });

        download.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                fileName = mItem.get(position).getDownload_name()+".mp3";
                new DownloadFileFromURL().execute( mItem.get(position).getItem_file());
            }
        });

        return  rootView;
    }

    //Download mp3 start
    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Bar Dialog
         * */
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
         * */
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                // getting file length
                int lenghtOfFile = conection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);

                String PATH = Environment.getExternalStorageDirectory()
                    + "/sdcard/Bhakti sagar";
                Log.v("log_tag", "PATH: " + PATH);
                File file = new File(PATH);
                if (!file.exists()) {
                    file.mkdirs();
                }

                // Output stream to write file
                File outputFile = new File(file, fileName);
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

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
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
            Log.d("Path_of_file==>",""+imagePath);
        }

    }


    //Download mp3 emd
}