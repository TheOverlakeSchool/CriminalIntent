package org.overlake.mat803.criminalintent;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

public class PictureUtils {
    public static Bitmap getScaledBitmap(String path, int destWidth, int destHeight){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        int srcWidth = options.outWidth;
        int srcHeight = options.outHeight;

        int scale = 1;

        if(srcWidth > destWidth || srcHeight > destHeight){
            float heightScale = srcHeight/destHeight;
            float widthScale = srcWidth/destWidth;
            scale = Math.round(Math.max(heightScale, widthScale));
        }

        options.inJustDecodeBounds = false;
        options.inSampleSize = scale;

        return BitmapFactory.decodeFile(path, options);
    }

    public static Bitmap getScaledBitmap(String path, Activity activity, int scale){
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        int destWidth = Math.round(size.x / scale);
        int destHeight = Math.round(size.y / scale);
        return getScaledBitmap(path, destWidth, destHeight);
    }

}
