package com.example.readmylnk;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
//класс реализующий получение Document
public class GetDoc{
    private SharedPreferences userpassSettings;
    private String filename;
    private Context context;
    private boolean isExist;
    private boolean isOnlyOnline;
    private String conn;

    private static final String TAG = "TAG_LOG_GETDOC";


    public GetDoc(SharedPreferences _userpassSettings, String _filename, Context _context,String _conn){
        conn = _conn;
        userpassSettings = _userpassSettings;
        filename = _filename;
        context = _context;
        isOnlyOnline = false;
    }

    public GetDoc(SharedPreferences _userpassSettings, String _filename, Context _context, boolean _isOnlyOnline,String _conn){
        conn = _conn;
        userpassSettings = _userpassSettings;
        filename = _filename;
        context = _context;
        isOnlyOnline = _isOnlyOnline;
    }

    private Map<String, String> getGlobalCookies(){
        Map<String, String> _cookies = new HashMap<>();
        for( Map.Entry<String, ?> entry : userpassSettings.getAll().entrySet())
            _cookies.put( entry.getKey(), entry.getValue().toString());
        return _cookies;
    }

    Document start(){
        onPreExecute();
        try {
            return doInBackground(conn);
        } catch (IOException e) {
            return null;
        }
    }


    private void onPreExecute() {
        new File(filename);
        FileInputStream inputStream;
        try {
            inputStream = context.openFileInput(filename);
            byte[] bytes = new byte[inputStream.available()];
            if (bytes.length > 0){
                isExist = true;
            }
            Log.d(TAG, String.valueOf(bytes.length));
        } catch (Exception e) {
            Log.d(TAG, "ERROR_READ");
            isExist = false;
        }
    }

    private Document doInBackground(String... conn) throws IOException {
        Connection.Response doc_response;
        if(isExist && !isOnlyOnline){
            Log.d(TAG, "SAVE_FILE_SUCCESS_READ");
            return Jsoup.parse(context.getFileStreamPath(filename), "UTF-8");
        }
        String _userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3690.93 Safari/537.36";
        doc_response = Jsoup.connect(conn[0]).cookies(getGlobalCookies()).userAgent(_userAgent).execute();
        Document doc = doc_response.parse();
        if (context != null && !isExist && !isOnlyOnline){
            FileOutputStream outputStream;
            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(doc.toString().getBytes());
            outputStream.close();
        }
        Log.d(TAG, "doInBackground: OK");
        return doc;
    }
}
