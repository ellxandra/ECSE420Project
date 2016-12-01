package com.project420.a.ecse420;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private Spinner spinner;
    private Button load;
    private Button test;
    private ImageView before;
    private ImageView after;
    private TextView display;
    private Image image;
    private Bitmap bitmapBefore;
    private Bitmap bitmapAfter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(Build.VERSION.SDK_INT >=11){
           // getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        }
        before=(ImageView)findViewById(R.id.before);
        after=(ImageView)findViewById(R.id.result);


        display=(TextView) findViewById(R.id.display_test);

        load= (Button)findViewById(R.id.load);
        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAndSetImage();
            }
        });
        test= (Button)findViewById(R.id.test);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bitmapBefore!=null && bitmapAfter!=null){
                    if(Image.testEquality(bitmapAfter,bitmapBefore)){
                        display.setText("SAME IMAGE!");
                    }
                    else{
                        display.setText("DIFFERENT IMAGE!");
                    }
                }
            }
        });
        spinner=(Spinner)findViewById(R.id.dropdown);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                try {
                    Method method = View.class.getMethod("setLayerType", int.class, Paint.class);
                    method.invoke(view, View.LAYER_TYPE_HARDWARE, null);
                } catch (Exception e) {
                    Log.e("RD", "Hardware Acceleration not supported on API " + android.os.Build.VERSION.SDK_INT, e);
                }
                boolean isHWAccelerated = view.isHardwareAccelerated();

                if(image!=null) {
                    Log.d("isHardwareAccelerated: " , String.valueOf(isHWAccelerated));
                    long startTime = System.currentTimeMillis();
                    if (position == 0) {

                        after.setImageBitmap(image.pool());
                        bitmapAfter=after.getDrawingCache();
                        Log.d("Finished"," pool");

                    }
                    else if(position==1){
                        after.setImageBitmap(image.rectify());
                        bitmapAfter=after.getDrawingCache();
                        Log.d("Finished"," convolve");
                    }
                    else if(position==2){
                        //ON GPU
                        //after.setImageBitmap(image.rectify());
                        //bitmapAfter=after.getDrawingCache();
                        Log.d("Finished"," rectify");
                        //ON CPU
                        new AsyncTask<Void, Void, Void>() {
                            Bitmap map=null;
                            @Override
                            protected Void doInBackground(Void... params) {
                                try{
                                    map=image.rectify();
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
                                Log.d("Finished"," processing");


                            }
                        }.execute();
                    }
                    long endTime = System.currentTimeMillis();
                    Log.d("Time in ms: " ,Long.toString(endTime - startTime));
                }


            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void loadAndSetImage(){
        new AsyncTask<Void, Void, Void>() {
            Bitmap bmp=null;
            @Override
            protected Void doInBackground(Void... params) {
                try{
                    URL url = new URL("https://raw.githubusercontent.com/ellxandra/ECSE420Project/master/test.png");
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
                image=new Image(bitmapBefore);
                before.setImageBitmap(bmp);


            }
        }.execute();

    }
}
