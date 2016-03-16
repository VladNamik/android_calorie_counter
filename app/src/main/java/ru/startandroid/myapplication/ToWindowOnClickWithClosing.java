package ru.startandroid.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.view.View;


public class ToWindowOnClickWithClosing extends ToWindowOnClick{
    public ToWindowOnClickWithClosing(Activity fromActivity, Class<? extends Activity> toActivityClass)
    {
        super(fromActivity, toActivityClass);
    }
    @Override
    public void onClick(View v) {
        if (!MyMenuActivity.isOpen)
        {
            super.onClick(v);
        }
        fromActivity.finish();
    }
}
