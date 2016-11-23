package com.project420.a.ecse420;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

/**
 * Created by a on 2016-11-22.
 */
public class Image{

    private byte[] array;

    public Image(Bitmap bitmap){
        array=getBytes(bitmap);
    }
    public Bitmap pool(){
        return getImage(array);
    }
    public Bitmap convolve(){
        return getImage(array);
    }
    public Bitmap rectify(){
        return getImage(array);
    }

    public  byte[] getBytes(Bitmap bitmap) {
        if (bitmap!=null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
            return stream.toByteArray();
        }
        return null;
    }
    public  Bitmap getImage(byte[] image) {
        if (image!=null) {
            Bitmap bm = BitmapFactory.decodeByteArray(image, 0, image.length);
            return bm;
        }
        return null;
    }
}
