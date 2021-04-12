package com.example.readmylnk;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.concurrent.ExecutionException;

public class ReadActivity extends AppCompatActivity {
    String conn;
    DBInterface dbInterface;
    SharedPreferences sp;
    Document doc;
    ConstraintLayout layoutOnRead;
    WebView webView;
    //no save

    private SharedPreferences mSettings;
//    private SharedPreferences mainSettings;
    private SharedPreferences userpassSettings;
    private SharedPreferences visualSettings;

//    public static final String APP_MAIN_PREFERENCES = "Settings_Main";

    private static final String TAG = "TAG_LOG_READ";

    private static final String SETTINGS_USER_PASS = "User_Pass";
    private static final String SETTINGS_VISUAL_READACT = "Visual_ReadAct";

    private static final String SCROLL_COUNTER = "Scroll_Y_Return_Counter";
    private static final String DIALOG_HIDE_NEGATIVE = "Hide_Negative_Button";
    private static final String BACKGROUND_COLOR = "Background_ReadAct_Color";
    private static final String TEXT_COLOR = "TEXT_ReadAct_Color";


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.read, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_read);

        conn = getIntent().getStringExtra("conn");

        userpassSettings = getSharedPreferences(SETTINGS_USER_PASS, Context.MODE_PRIVATE);
        mSettings = getSharedPreferences(normalizeFilename(conn), Context.MODE_PRIVATE);
        visualSettings = getSharedPreferences(SETTINGS_VISUAL_READACT, Context.MODE_PRIVATE);
        sp = PreferenceManager.getDefaultSharedPreferences(this);

        dbInterface = new DBInterface(getBaseContext());

        layoutOnRead = findViewById(R.id.layout_web_read);
        webView = findViewById(R.id.web_view);

        webView.setBackgroundColor(Color.parseColor("#2f1500"));
        webView.scrollTo(0,saved_scroll());

        new AsTaskOnUI(getApplicationContext()).execute(conn);
    }
    @Override
    protected void onPause() {
        super.onPause();
        savePref();
    }
    @Override
    public void onBackPressed() {
        openQuitDialog();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem v) {
        switch (v.getItemId()){
            case R.id.Return:
                saved_scroll();
                return true;
            case R.id.ShowAllImage:
                Intent intent_image = new Intent(this, ImageActivity.class);
                intent_image.putExtra("doc", conn);
                startActivity(intent_image);
                return true;
            case R.id.Settings:
                Intent intent_settings = new Intent(this, PrefActivity.class);
                startActivity(intent_settings);
                return true;
            case R.id.Retrying:
                this.recreate();
                return true;
            case R.id.Next:
                Intent intent = new Intent(getApplicationContext(), ReadActivity.class);
                if (doc != null && doc.getElementsByTag("a") != null){
                    for (Element element : doc.getElementsByTag("a")){
                        if (element.text().equals("Следующая глава")){
                            String conn_next =  String.format("%s%s",
                                    "https://tl.rulate.ru",
                                    element.attr("href"));
                            Document doc_next = null;
                            if (!conn_next.equals("")){
                                try {
                                    doc_next = new AsTaskOnUI(getApplicationContext(),false,false).execute(conn_next).get();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (doc_next != null){
                                String img_src = null;
                                if (doc_next.getElementsByClass("content-text").select("img").first() != null){
                                    img_src = doc_next.getElementsByClass("content-text").select("img").first().attr("src");
                                }
                                dbInterface.addBook(
                                        doc_next.title(),
                                        conn_next,
                                        img_src
                                );
                                intent.putExtra("conn", conn_next);
                                startActivity(intent);
                            }
                            break;
                        }
                    }
                    if (intent.getStringExtra("conn") == null){
                        toastEx("Упс, следующей главы нет или ошибочка.");
                    }
                }else {
                    toastEx("Нет интернета или на сайте ошибка.");
                }
                return true;
            default:
                return super.onOptionsItemSelected(v);
        }
    }

    private class AsTaskOnUI extends AsyncTask<String, Void, Document> {
        ProgressBar progressBar;
        Boolean execPre=true;
        Boolean execPost=true;
        Context contexts;

        AsTaskOnUI(Context contexts){
            this.contexts = contexts;
        }
        AsTaskOnUI(Context contexts, Boolean execPre, Boolean execPost){
            this.contexts = contexts;
            this.execPre = execPre;
            this.execPost = execPost;
        }

        @Override
        protected void onPreExecute() {
            if (execPre){
                progressBar = new ProgressBar(getApplicationContext());
                progressBar.setBackgroundColor(Color.parseColor("#2f1500"));
                progressBar.setLayoutParams(
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT)
                );
                Log.d(TAG, "onPreExecute: on");
                layoutOnRead.addView(progressBar);
            }
            super.onPreExecute();
        }

        @Override
        protected Document doInBackground(String... str_conn) {
            return new GetDoc(userpassSettings, normalizeFilename(str_conn[0]), contexts, str_conn[0]).start();
        }

        @Override
        protected void onPostExecute(Document doc_result) {
            if (execPost){
                if (doc_result == null){
                    popupForNotify(
                            "Ошибка!",
                            "Переподключитесь и повторите попытку позже.",
                            "понял",
                            null,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    finish();
                                }
                            },
                            null);
                }else{
                    doc = doc_result;
                    initWebView(doc.getElementsByClass("content-text").select("p"));
                    Log.d(TAG, "onPostExecute: on");
                    layoutOnRead.removeView(progressBar);
                }
            }
            super.onPostExecute(doc_result);
        }
    }
    private void initWebView(Elements elements) {
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        String data = elements.toString();
        webView.loadDataWithBaseURL("file:///android_asset/", getHtmlData(data), "text/html", "utf-8", null);
    }
    //STYLE CSS FOR WEB_VIEW
    private String getHtmlData(String bodyHTML) {
        String head =
                "<head>" +
                "<style>" +
                "img{max-width: 100%; width:auto; height: auto;}" +
                "body{color: #f7c741; background-color: #2f1500;}" +
                "</style>" +
                "</head>";
        return "<html>" + head + "<body>" + bodyHTML + "</body></html>";
    }

    //UTILS
    private void openQuitDialog() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(
                this);
        quitDialog.setTitle("Выход: Вы уверены?");

        quitDialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                savePref();
                startActivity(new Intent(ReadActivity.this, MainActivity.class));
            }
        });

        quitDialog.setNegativeButton("Нет", null);
        quitDialog.show();
    }

    private void toastEx(String text){
        Toast toast = Toast.makeText(ReadActivity.this, text,Toast.LENGTH_SHORT);
        toast.show();
    }

    private String normalizeFilename(String conn){
        return conn.replace("/", "");
    }

    private int saved_scroll(){
        return mSettings.getInt(SCROLL_COUNTER, 0);
    }

    private void savePref(){
        SharedPreferences.Editor editor = mSettings.edit();
        editor.clear();
        editor.putInt(SCROLL_COUNTER, webView.getScrollY());
        editor.apply();
    }

    private void popupForNotify(String title,
                                String message,
                                String positive_message,
                                String negative_message,
                                DialogInterface.OnClickListener onClkPositive,
                                DialogInterface.OnClickListener onClkNegative) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ReadActivity.this);
        builder.setTitle(title)
                .setMessage(message)
                .setIcon(R.mipmap.ic_launcher)
                .setCancelable(false)
                .setPositiveButton(positive_message, onClkPositive)
                .setNegativeButton(negative_message, onClkNegative);
//                    new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            sp.edit().putBoolean(DIALOG_HIDE_NEGATIVE, true).apply();
//                        }
//                    }
        AlertDialog alert = builder.create();
        alert.show();
    }
}