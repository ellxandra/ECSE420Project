package com.project420.a.ecse420;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
        Bitmap bm=getImage(array).copy(Bitmap.Config.ARGB_8888,true);
        Bitmap copied=Bitmap.createBitmap(bm.getWidth()/2,bm.getHeight()/2,Bitmap.Config.ARGB_8888);

        int width=bm.getWidth();
        int height=bm.getHeight();
        int R,G,B, Rc,Rr,Rd, Gc,Gr,Gd,Bc,Br,Bd;
        for (int i=0; i<height;i=i+2){
            for (int j=0 ; j<width;j=j+2){
                R=Color.red(bm.getPixel(j,i));
                Rr=Color.red(bm.getPixel(j+1,i));
                Rd=Color.red(bm.getPixel(j,i+1));
                Rc=Color.red(bm.getPixel(j+1,i+1));
                B=Color.blue(bm.getPixel(j,i));
                Br=Color.blue(bm.getPixel(j+1,i));
                Bd=Color.blue(bm.getPixel(j,i+1));
                Bc=Color.blue(bm.getPixel(j+1,i+1));
                G=Color.green(bm.getPixel(j,i));
                Gr=Color.green(bm.getPixel(j+1,i));
                Gd=Color.green(bm.getPixel(j,i+1));
                Gc=Color.green(bm.getPixel(j+1,i+1));

                R=R>Rr?R:Rr;
                R=R>Rd?R:Rd;
                R=R>Rc?R:Rc;

                B=B>Br?B:Br;
                B=B>Bd?B:Bd;
                B=B>Bc?B:Bc;

                G=G>Gr?G:Gr;
                G=G>Gd?G:Gd;
                G=G>Gc?G:Gc;

                copied.setPixel(j/2,i/2,Color.argb(255,R,G,B));
            }
        }
        return copied;
    }
    public Bitmap convolve(){
        Bitmap bm=getImage(array).copy(Bitmap.Config.ARGB_8888,true);
        Bitmap copied=Bitmap.createBitmap(bm.getWidth()-2,bm.getHeight()-2,Bitmap.Config.ARGB_8888);
        double [][] w= {{1,2,-1},
                {2,0.25,-2},
                {1,-2,-1}
        };

        int width=bm.getWidth();
        int height=bm.getHeight();
        int R,G,B;
        for (int i=1; i<height;i++){
            for (int j=1 ; j<width;j++){


                for(int x=0;x<3;x++){
                    for(int y=0;y<3;y++) {

                    }
                }
                R=Color.red(bm.getPixel(j,i));
                B=Color.blue(bm.getPixel(j,i));
                G=Color.green(bm.getPixel(j,i));
                R=R<127?127:R;
                G=G<127?127:G;
                B=B<127?127:B;
//                copied.setPixel(j,i,Color.argb(255,R,G,B));
            }
        }
        return bm;

    }

    public Bitmap rectify(){
        Bitmap bm=getImage(array).copy(Bitmap.Config.ARGB_8888,true);
        int width=bm.getWidth();
        int height=bm.getHeight();
        int R,G,B;
        for (int i=0; i<height;i++){
            for (int j=0 ; j<width;j++){
                R=Color.red(bm.getPixel(j,i));
                B=Color.blue(bm.getPixel(j,i));
                G=Color.green(bm.getPixel(j,i));
                R=R<127?127:R;
                G=G<127?127:G;
                B=B<127?127:B;
                bm.setPixel(j,i,Color.argb(255,R,G,B));
            }
        }
        return bm;
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
        if(MSE<0.00001){
            return true;
        }
        return false;

    }
}
