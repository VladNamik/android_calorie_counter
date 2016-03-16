package ru.startandroid.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ScrollView;
import android.widget.Toast;

public class MyMenuActivity extends AppCompatActivity {
    public static boolean isOpen=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyMenuActivity.isOpen=true;
        setContentView(R.layout.activity_my_menu);
        findViewById(R.id.from_menu_to_diary).setOnClickListener(new ToWindowOnClick(this, DiaryActivity.class));
        findViewById(R.id.from_menu_to_diary_menu).setOnClickListener(new ToWindowOnClick(this,DiaryActivity.class));
        findViewById(R.id.from_menu_to_profile).setOnClickListener(new ToWindowOnClick(this,ProfileActivity.class));
        findViewById(R.id.from_menu_to_dishes).setOnClickListener(new ToWindowOnClick(this,DishActivity.class));
        findViewById(R.id.from_menu_to_exercises).setOnClickListener(new ToWindowOnClick(this, ExerciseActivity.class));
        findViewById(R.id.from_menu_to_statistic).setOnClickListener(new ToWindowOnClick(this, StatisticActivity.class));
    }

    public void onExitButton(final View view)
    {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isOpen=false;
    }
}
