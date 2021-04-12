package com.example.readmylnk;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.Map;

public class UserEmailDialog extends android.support.v4.app.DialogFragment implements OnClickListener {
    private static final String LOG_TAG = "LOG_FRAGMENT_DIALOG";

    EditText editTextUser;
    EditText editTextPass;
    private SharedPreferences userpassSettings;

    private static final String SETTINGS_USER_PASS = "User_Pass";

    private static final String USER = "USER_LOGIN";
    private static final String PASS = "USER_PASS";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_signin, null);
        editTextUser = view.findViewById(R.id.username_from_dialog_fragment);
        editTextPass = view.findViewById(R.id.password_from_dialog_fragment);
        userpassSettings = getActivity().getSharedPreferences(SETTINGS_USER_PASS, Context.MODE_PRIVATE);

        builder.setView(view)
                .setPositiveButton("да", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (editTextPass.getText().toString().equals("") | editTextUser.getText().toString().equals("")){
                            Log.d("OBO", "CRAMS");
                            toast("Введите логин и пароль от аккаунта tl.rulate.ru");
                        }else{
                            SharedPreferences.Editor editor = userpassSettings.edit();
                            editor.putString(USER, editTextUser.getText().toString());
                            editor.putString(PASS, editTextPass.getText().toString());
                            editor.apply();
                            dismiss();
                            new Authorization().execute();
                        }
                    }
                })
                .setCancelable(true);
        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case Dialog.BUTTON_POSITIVE:
                break;
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }

    private class Authorization extends AsyncTask<Void, Void, Map<String,String>> {
        @Override
        protected Map<String,String> doInBackground(Void... conn) {
            String conn_str = "https://tl.rulate.ru/";
            String conn_str_source = "https://tl.rulate.ru/mature?path=%2Fbook%2F16172";
            String _userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3690.93 Safari/537.36";
            Connection.Response response = null;
            try {
                response = Jsoup.connect(conn_str)
                        .method(Connection.Method.GET)
                        .execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                response = Jsoup.connect(conn_str)
                        .referrer(conn_str)
                        .data("login[login]", userpassSettings.getString(USER, "kalukali"))
                        .data("login[pass]", userpassSettings.getString(PASS, "89456700hfs!!!"))
                        .cookies(response.cookies())
                        .method(Connection.Method.POST)
                        .userAgent(_userAgent)
                        .execute();
                Log.d(LOG_TAG, "successful");
            } catch (IOException e) {
                toast("403 Неправильный пароль");
            }
            Connection.Response response1;
            try {
                response1 = Jsoup.connect(conn_str_source)
                        .referrer(conn_str_source)
                        .data("path", "/book/16172")
                        .data("ok", "Да")
                        .followRedirects(true)
                        .cookies(response.cookies())
                        .method(Connection.Method.POST)
                        .userAgent(_userAgent)
                        .execute();
                return response1.cookies();
            } catch (IOException e) {
//                toast("404");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Map<String, String> stringStringMap) {
            if (stringStringMap != null){
                SharedPreferences.Editor editor = userpassSettings.edit();
                for( Map.Entry<String, String> entry : stringStringMap.entrySet() ){
                    editor.putString( entry.getKey(), entry.getValue() );
                }
                editor.apply();
            }
            super.onPostExecute(stringStringMap);
        }
    }

    private void toast(String text){
        Toast toast = Toast.makeText(getContext(), text,Toast.LENGTH_SHORT);
        toast.show();
    }
}