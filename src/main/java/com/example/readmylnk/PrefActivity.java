package com.example.readmylnk;

import android.os.Bundle;
import android.preference.PreferenceActivity;


public class PrefActivity extends PreferenceActivity {
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref);
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//        prefs.registerOnSharedPreferenceChangeListener(
//                new SharedPreferences.OnSharedPreferenceChangeListener() {
//                    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
//                        switch (key){
//                            case "fontsize":
//
//                                break;
//                        }
//                    }
//                });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
}
