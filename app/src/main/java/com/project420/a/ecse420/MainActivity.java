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
                    Image.testEquality(bitmapBefore,bitmapBefore,display,getApplicationContext());
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
                                        if(Build.VERSION.SDK_INT >=11){
                                            map=Image.poolAccelerated(bitmapBefore,getApplicationContext());
                                        }else{
                                            map=Image.pool(bitmapBefore);
                                        }

                                    }
                                    if (position==1){
                                        if(Build.VERSION.SDK_INT >=11){
                                            map=Image.convolveAccelerated(bitmapBefore,getApplicationContext());
                                        }else{
                                            map=Image.convolve(bitmapBefore);
                                        }
                                    }
                                    if (position==2){
                                        if(Build.VERSION.SDK_INT >=11){
                                            map=Image.rectifyAccelerated(bitmapBefore,getApplicationContext());
                                        }else{
                                            map=Image.rectify(bitmapBefore);
                                        }
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


}
