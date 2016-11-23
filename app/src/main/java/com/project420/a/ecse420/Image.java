package com.project420.a.ecse420;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

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
    public static Boolean testEquality(Bitmap arr1, Bitmap arr2){
        if(arr1.getWidth()!=arr2.getWidth() || arr1.getHeight()!=arr2.getHeight()){
            return false;
        }
        double MSE;
        int diff,sum=0;
        for(int j=0; j<arr1.getHeight();j++){
            for (int i=0; i<arr1.getWidth();i++){
                diff=arr1.getPixel(i,j)-arr2.getPixel(i,j);
                sum+=diff*diff;
            }
        }
        MSE=Math.sqrt(sum)/(arr1.getWidth()*arr1.getHeight());

        Log.d("MSE is",Double.toString(MSE));
        if(MSE<0.001){
            return true;
        }
        return false;

    }
}
