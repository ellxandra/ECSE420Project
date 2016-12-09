package com.project420.a.ecse420;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.Script;
import android.renderscript.ScriptC;
import android.renderscript.ScriptIntrinsicBlur;
import android.renderscript.Type;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rs.*;
import java.io.ByteArrayOutputStream;


/**
 * Created by a on 2016-11-22.
 */
public class Image {

    public static Bitmap pool(Bitmap in) {
        Bitmap bm = in.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap copied = Bitmap.createBitmap(bm.getWidth() / 2, bm.getHeight() / 2, Bitmap.Config.ARGB_8888);

        int width = bm.getWidth();
        int height = bm.getHeight();
        int R, G, B, Rc, Rr, Rd, Gc, Gr, Gd, Bc, Br, Bd;
        for (int i = 0; i < height; i = i + 2) {
            for (int j = 0; j < width; j = j + 2) {
                R = Color.red(bm.getPixel(j, i));
                Rr = Color.red(bm.getPixel(j + 1, i));
                Rd = Color.red(bm.getPixel(j, i + 1));
                Rc = Color.red(bm.getPixel(j + 1, i + 1));
                B = Color.blue(bm.getPixel(j, i));
                Br = Color.blue(bm.getPixel(j + 1, i));
                Bd = Color.blue(bm.getPixel(j, i + 1));
                Bc = Color.blue(bm.getPixel(j + 1, i + 1));
                G = Color.green(bm.getPixel(j, i));
                Gr = Color.green(bm.getPixel(j + 1, i));
                Gd = Color.green(bm.getPixel(j, i + 1));
                Gc = Color.green(bm.getPixel(j + 1, i + 1));

                R = R > Rr ? R : Rr;
                R = R > Rd ? R : Rd;
                R = R > Rc ? R : Rc;

                B = B > Br ? B : Br;
                B = B > Bd ? B : Bd;
                B = B > Bc ? B : Bc;

                G = G > Gr ? G : Gr;
                G = G > Gd ? G : Gd;
                G = G > Gc ? G : Gc;

                copied.setPixel(j / 2, i / 2, Color.argb(255, R, G, B));
            }
        }
        return copied;
    }

    public static Bitmap convolve(Bitmap in) {
        Bitmap copied = Bitmap.createBitmap(in.getWidth() - 2, in.getHeight() - 2, Bitmap.Config.ARGB_8888);
        float[][] w = {{1, 2, -1},
                {2, 0.25f, -2},
                {1, -2, -1}
        };
        float p1,p2,p3;
        float acc1=0,acc2=0,acc3=0,acc4;
        int width = in.getWidth();
        int height = in.getHeight();
        int R, G, B;
        for (int i = 1; i < height-1; i++) {
            for (int j = 1; j < width-1; j++) {
                acc1=0;
                acc2=0;
                acc3=0;
                for (int x = 0; x < 3; x++) {
                    for (int y = 0; y < 3; y++) {
                        int xdim=x+i-1;
                        int ydim=y+j-1;
                        R = Color.red(in.getPixel(ydim, xdim));
                        B = Color.blue(in.getPixel(ydim, xdim));
                        G = Color.green(in.getPixel(ydim, xdim));
                        p1=(w[x][y])*R;
                        p2=(w[x][y])*G;
                        p3=(w[x][y])*B;

                        acc1+=p1;
                        acc2+=p2;
                        acc3+=p3;
                    }
                }


                acc1=acc1<0.0f?0:acc1;
                acc1=acc1>255.0?255:acc1;
                acc2=acc2<0.0f?0:acc2;
                acc2=acc2>255.0?255:acc2;
                acc3=acc3<0.0f?0:acc3;
                acc3=acc3>255.0?255:acc3;
                acc4=Color.alpha(in.getPixel(j, i));
                copied.setPixel(j-1,i-1,Color.argb((int)acc4 ,(int)(acc1),(int)(acc2),(int)(acc3)));
            }
        }
        return copied;

    }

    public static Bitmap rectify(Bitmap in) {
        Bitmap bm = in.copy(Bitmap.Config.ARGB_8888, true);
        int width = in.getWidth();
        int height = in.getHeight();
        int R, G, B;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                R = Color.red(bm.getPixel(j, i));
                B = Color.blue(bm.getPixel(j, i));
                G = Color.green(bm.getPixel(j, i));
                R = R < 127 ? 127 : R;
                G = G < 127 ? 127 : G;
                B = B < 127 ? 127 : B;
                bm.setPixel(j, i, Color.argb(255, R, G, B));
            }
        }
        return bm;
    }

    public static Bitmap poolAccelerated(Bitmap bm, Context ctx){
        Bitmap out = Bitmap.createBitmap(bm.getWidth()/2,bm.getHeight()/2, Bitmap.Config.ARGB_8888);

        android.support.v8.renderscript.RenderScript mRs = android.support.v8.renderscript.RenderScript.create(ctx);

        android.support.v8.renderscript.Allocation allocIn = android.support.v8.renderscript.Allocation.createFromBitmap(mRs,bm);
        android.support.v8.renderscript.Allocation allocOut = android.support.v8.renderscript.Allocation.createFromBitmap(mRs,Bitmap.createBitmap(bm.getWidth(),bm.getHeight(), Bitmap.Config.ARGB_8888));
        android.support.v8.renderscript.Allocation allocOutMod = android.support.v8.renderscript.Allocation.createFromBitmap(mRs,Bitmap.createBitmap(bm.getWidth()/2,bm.getHeight()/2, Bitmap.Config.ARGB_8888));

        ScriptC_pool pool = new ScriptC_pool(mRs,ctx.getResources(),R.raw.pool);

        pool.set_gin(allocIn);
        pool.set_gout(allocOutMod);
        pool.forEach_root(allocIn,allocOut);

        allocOutMod.copyTo(out);

        return out;
    }
    public static Bitmap convolveAccelerated(Bitmap bm,Context ctx){
        android.support.v8.renderscript.RenderScript mRS;
        ScriptC_convolve mScript;
        Bitmap bmOut=Bitmap.createBitmap(bm.getWidth()-2,bm.getHeight()-2,Bitmap.Config.ARGB_8888);
        mRS = android.support.v8.renderscript.RenderScript.create(ctx);
        android.support.v8.renderscript.Allocation allocIn = android.support.v8.renderscript.Allocation.createFromBitmap(mRS,bm);
        android.support.v8.renderscript.Allocation allocOut = android.support.v8.renderscript.Allocation.createFromBitmap(mRS,Bitmap.createBitmap(bm.getWidth(),bm.getHeight(), Bitmap.Config.ARGB_8888));
        android.support.v8.renderscript.Allocation allocOutMod = android.support.v8.renderscript.Allocation.createFromBitmap(mRS,Bitmap.createBitmap(bm.getWidth()-2,bm.getHeight()-2, Bitmap.Config.ARGB_8888));

        mScript = new ScriptC_convolve(mRS, ctx.getResources(), R.raw.convolve);
        mScript.set_gIn(allocIn);
        mScript.set_gOut(allocOutMod);
        mScript.set_width(bm.getWidth());
        mScript.set_height(bm.getHeight());


        mScript.forEach_root(allocIn,allocOut);
        allocOutMod.copyTo(bmOut);

        return bmOut;
    }
    public static Bitmap rectifyAccelerated(Bitmap bitmap, Context ctx){
        android.support.v8.renderscript.RenderScript mRS;
        android.support.v8.renderscript.Allocation allocIn;
        android.support.v8.renderscript.Allocation allocOut;
        ScriptC_rectify rectify;
        Bitmap bmOut=bitmap.copy(Bitmap.Config.ARGB_8888,true);
        mRS = android.support.v8.renderscript.RenderScript.create(ctx);
        allocIn = android.support.v8.renderscript.Allocation.createFromBitmap(mRS, bitmap,
                android.support.v8.renderscript.Allocation.MipmapControl.MIPMAP_NONE, android.support.v8.renderscript.Allocation.USAGE_SCRIPT);
        allocOut = android.support.v8.renderscript.Allocation.createTyped(mRS, allocIn.getType());
        rectify = new ScriptC_rectify(mRS, ctx.getResources(), com.project420.a.ecse420.R.raw.rectify);

        rectify.set_gIn(allocIn);
        rectify.set_gOut(allocOut);
        rectify.forEach_root(allocIn,allocOut);
        allocOut.copyTo(bmOut);

        return bmOut;
    }

    public static void testEquality(final Bitmap before, final Bitmap after, final TextView display,final Context ctx){


        if(before.getWidth()!=after.getWidth() || before.getHeight()!=after.getHeight()){
            display.setText("DIFFERENT IMAGE!");
            Log.d("DIMSIZE","dimensions don't match");
        }
        else {

            new AsyncTask<Void,Void, Boolean>() {
                double MSE=0;
                @Override
                protected void onPreExecute(){
                    display.setText("Computing...");
                }
                @Override
                protected Boolean doInBackground(Void... params) {
                    int diff, sum = 0;
                    for (int j = 0; j < before.getHeight(); j++) {
                        for (int i = 0; i < before.getWidth(); i++) {
                            diff = Math.abs(after.getPixel(i, j) - before.getPixel(i, j));
                            sum += diff * diff;
                        }
                    }
                    sum=sum<0?sum*-1:sum;
                    MSE = Math.sqrt(sum) / (before.getWidth() * before.getHeight());
                    if (MSE < 0.00001) {
                        return true;
                    } else {
                        return false;
                    }
                }
                @Override
                protected void onPostExecute(Boolean res){
                    if(res){
                        display.setText("SAME IMAGE!");
                    }
                    else{
                        display.setText("DIFFERENT IMAGE!");
                    }
                    Toast.makeText(ctx,"Done",Toast.LENGTH_SHORT).show();
                    Log.d("MSE is", Double.toString(MSE));

                }
            }.execute();
        }

    }
}
