package com.example.readmylnk;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;

public class ImageActivity extends AppCompatActivity {
    LinearLayout layout;
    String conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        conn = getIntent().getStringExtra("doc");
        layout = findViewById(R.id.layout_add_image);
        new Parse().execute();
    }
    @SuppressLint("StaticFieldLeak")
    private class Parse extends AsyncTask<Void, Void, Elements> {
        @Override
        protected Elements doInBackground(Void... voids) {
            Elements elements = null;
            Document doc = null;
            try{
                doc = Jsoup.connect(conn).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(doc != null){
                elements = doc.getElementsByClass("content-text").select("img");
            }
            return elements;
        }

        @Override
        protected void onPostExecute(Elements elements) {

            for(Element el : elements){
                if(el.select("img").first().absUrl("src").contains("://")){
                    ImageView imageView = new ImageView(getApplicationContext());
                    imageView.setLayoutParams(
                            new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT)
                    );
                    try{
                        new DownloadImageTask(imageView).execute(el.select("img").first().absUrl("src"));
                    }catch (Exception ignore){
                        Log.d("EXC", "FDFG");
                    }
                    layout.addView(imageView);
                }
            }
            super.onPostExecute(elements);
        }
        private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
            ImageView bmImage;

            DownloadImageTask(ImageView bmImage) {
                this.bmImage = bmImage;
            }

            protected Bitmap doInBackground(String... urls) {
                String urldisplay = urls[0];
                Bitmap mIcon11 = null;
                try {
                    InputStream in = new java.net.URL(urldisplay).openStream();
                    mIcon11 = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return mIcon11;
            }

            protected void onPostExecute(Bitmap result) {
                bmImage.setImageBitmap(result);
            }
        }
    }
}
