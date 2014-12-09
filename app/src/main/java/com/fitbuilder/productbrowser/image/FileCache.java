package com.fitbuilder.productbrowser.image;

import java.io.File;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class FileCache {
    
    private File cacheDir;
    
    public FileCache(Context context){
        //Find the dir to save cached images
        if (false && Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {

            cacheDir = new File(Environment.getExternalStorageDirectory() + "/FitBuilder");

            boolean success = true;
            if (!cacheDir.exists()) {
                success = cacheDir.mkdir();
            }

            Log.d("FBLog", "Cache create: " + String.valueOf(success));
        }
        else
            cacheDir=context.getCacheDir();
        if(!cacheDir.exists())
            cacheDir.mkdirs();

        File f = new File(Environment.getExternalStorageDirectory() + "/FitBuilder");
        if(f.isDirectory()) {
            Log.d("FBLog", "exists");
        }
        else
            Log.d("FBLog", "NOT exists");

    }
    
    public File getFile(String url){
        //I identify images by hashcode. Not a perfect solution, good for the demo.
        String filename=String.valueOf(url.hashCode());
        //Another possible solution (thanks to grantland)
        //String filename = URLEncoder.encode(url);
        File f = new File(cacheDir, filename);
        return f;
        
    }
    
    public void clear(){
        File[] files=cacheDir.listFiles();
        if(files==null)
            return;
        for(File f:files)
            f.delete();
    }

}