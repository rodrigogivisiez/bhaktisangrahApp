package com.goldenant.bhaktisangrah.common.util;

/**
 * Created by ankita on 1/4/2016.
 */
import com.goldenant.bhaktisangrah.model.SubCategoryModel;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

public class SongsManager {
    // SDCard Path
    final String MEDIA_PATH = new String("/sdcard/Bhakti sagar/");

    ArrayList<SubCategoryModel> CatListItem = new ArrayList<SubCategoryModel>();

    // Constructor
    public SongsManager(){

    }

    /**
     * Function to read all mp3 files from sdcard
     * and store the details in ArrayList
     * */
    public ArrayList<SubCategoryModel> getPlayList(){
        File home = new File(MEDIA_PATH);

        if (home.listFiles(new FileExtensionFilter()).length > 0) {
            for (File file : home.listFiles(new FileExtensionFilter()))
            {
                SubCategoryModel categoryModel = new SubCategoryModel();

                categoryModel.setItem_name(file.getName().substring(0, (file.getName().length() - 4)));
                categoryModel.setItem_file(file.getPath());
//                HashMap<String, String> song = new HashMap<String, String>();
//                song.put("songTitle", file.getName().substring(0, (file.getName().length() - 4)));
//                song.put("songPath", file.getPath());

                // Adding each song to SongList
                CatListItem.add(categoryModel);
            }
        }
        // return songs list array
        return CatListItem;
    }

    /**
     * Class to filter files which are having .mp3 extension
     * */
    class FileExtensionFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return (name.endsWith(".mp3") || name.endsWith(".MP3"));
        }
    }
}