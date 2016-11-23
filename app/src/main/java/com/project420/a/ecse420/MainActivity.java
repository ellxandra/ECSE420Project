package com.project420.a.ecse420;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private Spinner spinner;
    private Button load;
    private ImageView before;
    private ImageView after;
    private Image image;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        before=(ImageView)findViewById(R.id.before);
        after=(ImageView)findViewById(R.id.result);


        load= (Button)findViewById(R.id.load);
        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAndSetImage();
            }
        });

        spinner=(Spinner)findViewById(R.id.dropdown);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(image!=null){
                    Log.d("not",image.pool().toString());
                    if(position==0){
                        after.setImageBitmap(image.pool());
                    }
                    else if(position==1){
                        after.setImageBitmap(image.convolve());
                    }
                    else if(position==2){
                        after.setImageBitmap(image.rectify());
                    }
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
                before.setImageBitmap(bmp);
                bitmap=bmp;
                image=new Image(bitmap);
            }
        }.execute();

    }
}
