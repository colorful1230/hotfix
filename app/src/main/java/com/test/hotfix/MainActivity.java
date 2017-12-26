package com.test.hotfix;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onCrashClick(View v) {
        MyString myString = new MyString(null);
        Log.d(TAG, "onCrashClick: " + myString.getLength());
    }
}
