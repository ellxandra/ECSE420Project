package com.project420.a.ecse420;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.rs.ScriptC_convolve;
import com.example.rs.ScriptC_pool;
import com.example.rs.ScriptC_rectify;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private Spinner spinner;
    private Spinner load;
    private Button test;
    private ImageView before;
    private ImageView after;
    private TextView display;
    private Bitmap bitmapBefore;
    private Bitmap bitmapAfter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        before=(ImageView)findViewById(R.id.before);
        after=(ImageView)findViewById(R.id.result);


        display=(TextView) findViewById(R.id.display_test);

        load= (Spinner)findViewById(R.id.load);
        load.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadAndSetImage(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        test= (Button)findViewById(R.id.test);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bitmapBefore!=null && bitmapAfter!=null) {
                    compare();
                }
            }
        });
        spinner=(Spinner)findViewById(R.id.dropdown);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                if(bitmapBefore!=null) {
                    final long startTime = System.currentTimeMillis();
                        new AsyncTask<Void, Void, Void>() {
                            Bitmap map=null;
                            @Override
                            protected Void doInBackground(Void... params) {
                                try{
                                    if (position==0){
                                        map=pool(bitmapBefore);
                                    }
                                    if (position==1){
                                        map=convolve(bitmapBefore);
                                    }
                                    if (position==2){
                                        map=rectify(bitmapBefore);
                                    }

                                }
                                catch (Exception e){
                                    Log.d("Error loading", e.toString());
                                }
                                return null;
                            }
                            @Override
                            protected void onPostExecute(Void params){
                                after.setImageBitmap(map);
                                bitmapAfter=map;
                                long endTime = System.currentTimeMillis();
                                Log.d("Time in ms" ,Long.toString(endTime - startTime));
                                Toast.makeText(getApplicationContext(),"DONE!",Toast.LENGTH_SHORT).show();
                            }
                        }.execute();
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void loadAndSetImage(int position){
        final int pos=position;
        new AsyncTask<Void, Void, Void>() {
            Bitmap bmp=null;
            @Override
            protected Void doInBackground(Void... params) {
                try{
                    URL url;
                    if(pos==0){ //initial
                        url = new URL("https://raw.githubusercontent.com/ellxandra/ECSE420Project/master/test.png");
                    }
                    else if(pos==1){ //pool
                        url = new URL("https://raw.githubusercontent.com/ellxandra/ECSE420Project/master/test_pool.png");
                    }
                    else if(pos==2){ //convolve
                        url = new URL("https://raw.githubusercontent.com/ellxandra/ECSE420Project/master/test_convolve.png");
                    }
                    else { //rectify
                        url = new URL("https://raw.githubusercontent.com/ellxandra/ECSE420Project/master/test_rectify.png");
                    }

                    HttpURLConnection conn=(HttpURLConnection)url.openConnection() ;
                    conn.setInstanceFollowRedirects(true);
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream stream=conn.getInputStream();
                    bmp = BitmapFactory.decodeStream(stream);

                }
                catch (Exception e){
                    Log.d("Error loading", e.toString());
                }

                return null;
            }
            @Override
            protected void onPostExecute(Void params){
                bitmapBefore =bmp;
                before.setImageBitmap(bmp);

            }
        }.execute();

    }

    public Bitmap rectify(Bitmap bitmap){
        RenderScript mRS;
        Allocation allocIn;
        Allocation allocOut;
        ScriptC_rectify rectify;
        Bitmap bmOut=bitmap.copy(Bitmap.Config.ARGB_8888,true);

        if(Build.VERSION.SDK_INT >=11){
            mRS = RenderScript.create(getApplicationContext());

            allocIn = Allocation.createFromBitmap(mRS, bitmap,
                    Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
            allocOut = Allocation.createTyped(mRS, allocIn.getType());

            rectify = new ScriptC_rectify(mRS, getResources(), com.project420.a.ecse420.R.raw.rectify);

            rectify.set_gIn(allocIn);
            rectify.set_gOut(allocOut);
            rectify.forEach_root(allocIn,allocOut);
            allocOut.copyTo(bmOut);
        }
        return bmOut;
    }
    public Bitmap pool(Bitmap bm){
        Bitmap out = Bitmap.createBitmap(bm.getWidth()/2,bm.getHeight()/2, Bitmap.Config.ARGB_8888);

        RenderScript mRs = RenderScript.create(getApplicationContext());

        Allocation allocIn = Allocation.createFromBitmap(mRs,bm);
        Allocation allocOut = Allocation.createFromBitmap(mRs,Bitmap.createBitmap(bm.getWidth(),bm.getHeight(), Bitmap.Config.ARGB_8888));
        Allocation allocOutMod = Allocation.createFromBitmap(mRs,Bitmap.createBitmap(bm.getWidth()/2,bm.getHeight()/2, Bitmap.Config.ARGB_8888));

        ScriptC_pool pool = new ScriptC_pool(mRs,getResources(),R.raw.pool);

        pool.set_gin(allocIn);
        pool.set_gout(allocOutMod);
        pool.forEach_root(allocIn,allocOut);

        allocOutMod.copyTo(out);

        return out;
    }
    public Bitmap convolve(Bitmap bm){
        RenderScript mRS;
        ScriptC_convolve mScript;
        Bitmap bmOut=Bitmap.createBitmap(bm.getWidth()-2,bm.getHeight()-2,Bitmap.Config.ARGB_8888);
        mRS = RenderScript.create(getApplicationContext());
        Allocation allocIn = Allocation.createFromBitmap(mRS,bm);
        Allocation allocOut = Allocation.createFromBitmap(mRS,Bitmap.createBitmap(bm.getWidth(),bm.getHeight(), Bitmap.Config.ARGB_8888));
        Allocation allocOutMod = Allocation.createFromBitmap(mRS,Bitmap.createBitmap(bm.getWidth()-2,bm.getHeight()-2, Bitmap.Config.ARGB_8888));

        mScript = new ScriptC_convolve(mRS, getResources(), R.raw.convolve);
        mScript.set_gIn(allocIn);
        mScript.set_gOut(allocOutMod);
        mScript.set_width(bm.getWidth());
        mScript.set_height(bm.getHeight());


        mScript.forEach_root(allocIn,allocOut);
        allocOutMod.copyTo(bmOut);

        return bmOut;
    }
    public void compare(){


        if(bitmapBefore.getWidth()!=bitmapAfter.getWidth() || bitmapBefore.getHeight()!=bitmapAfter.getHeight()){
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
                    for (int j = 0; j < bitmapBefore.getHeight(); j++) {
                        for (int i = 0; i < bitmapBefore.getWidth(); i++) {
                            diff = Math.abs(bitmapAfter.getPixel(i, j) - bitmapBefore.getPixel(i, j));
                            sum += diff * diff;
                        }
                    }
                    sum=sum<0?sum*-1:sum;
                    MSE = Math.sqrt(sum) / (bitmapBefore.getWidth() * bitmapBefore.getHeight());
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
                    Toast.makeText(getApplicationContext(),"Done",Toast.LENGTH_SHORT).show();
                    Log.d("MSE is", Double.toString(MSE));

                }
            }.execute();
        }

    }
}
