package com.vladnamik.developer.listeners;

import android.app.Activity;
import android.content.Intent;
import android.view.View;


public class ToWindowOnClick implements View.OnClickListener {
    protected Activity fromActivity;
    protected Class toActivityClass;

    public ToWindowOnClick(Activity fromActivity, Class<? extends Activity> toActivityClass) {
        this.fromActivity = fromActivity;
        this.toActivityClass = toActivityClass;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(fromActivity, toActivityClass);
        fromActivity.startActivity(intent);
    }
}
