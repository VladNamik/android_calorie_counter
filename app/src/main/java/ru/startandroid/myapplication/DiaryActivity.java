package ru.startandroid.myapplication;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class DiaryActivity extends AppCompatActivity{
    Profile profile;
    Day viewDay;//просматриваемый день
    DataBase db;
    SimpleAdapter dishesAdapter;
    ArrayList<Map<String, Object>> dishesData;
    SimpleAdapter exercisesAdapter;
    ArrayList<Map<String, Object>> exercisesData;
    private static final int DATE_DIALOG=1;
    private static final int VIEW_DISHES_DIALOG=2;
    private static final int VIEW_EXERCISES_DIALOG=3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);
        findViewById(R.id.from_diary_to_menu).setOnClickListener(new ToWindowOnClickWithClosing(this, MyMenuActivity.class));
        findViewById(R.id.diary_set_date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG);
            }
        });
        findViewById(R.id.diary_left_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeViewDay(viewDay.getPreviousDay(db).getDate());
            }
        });
        findViewById(R.id.diary_right_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeViewDay(viewDay.getNextDay(db).getDate());
            }
        });
        findViewById(R.id.diary_dish_add_button).setOnClickListener(new ToWindowOnClick(this, PickDishActivity.class){
            @Override
            public void onClick(View v) {
                PickDishActivity.date= viewDay.getDate();
                super.onClick(v);
            }
        });
        findViewById(R.id.diary_exercise_add_button).setOnClickListener(new ToWindowOnClick(this, PickExerciseActivity.class){
            @Override
            public void onClick(View v) {
                PickExerciseActivity.date= viewDay.getDate();
                super.onClick(v);
            }
        });

        profile=Profile.getProfile(this);
        db = DataBase.getDataBase(this);
        setViewDay(new MyDate());//устанавливаем текущую дату

        //съеденные блюда
        dishesData=DataBase.cursorToArrayList(db.getAllDayDishes(viewDay.getDate()));
        // формируем столбцы сопоставления
        String[] from = new String[] {DataBase.DISH_COLUMN_NAME, DataBase.DAYS_DISH_COLUMN_WEIGHT};//названия колонок
        int[] to = new int[] { R.id.db_item_name, R.id.db_item_right_text};//места для записи (View id)

        dishesAdapter = new SimpleAdapter(this,dishesData, R.layout.database_item, from, to);


        //упражнения за день
        exercisesData=DataBase.cursorToArrayList(db.getAllDayExercises(viewDay.getDate()));
        // формируем столбцы сопоставления
        from = new String[] {DataBase.EXERCISE_COLUMN_NAME, DataBase.DAYS_EXERCISE_COLUMN_QUANTITY};//названия колонок

        exercisesAdapter = new SimpleAdapter(this,exercisesData, R.layout.database_item, from, to);


    }

    @Override
    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder adb;
        switch (id)
        {
            case DATE_DIALOG :
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(viewDay.getDate());
                return new DatePickerDialog(this, myCallBack, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            case VIEW_DISHES_DIALOG :
                adb = new AlertDialog.Builder(this);
                adb.setTitle("Съеденные блюда");
                adb.setAdapter(dishesAdapter, null);
                return adb.create();
            case VIEW_EXERCISES_DIALOG :
                adb = new AlertDialog.Builder(this);
                adb.setTitle("Упражнения за день");
                adb.setAdapter(exercisesAdapter, null);
                return adb.create();
        }
        return super.onCreateDialog(id);
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
        switch (id)
        {
            case DATE_DIALOG :
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(viewDay.getDate());
                ((DatePickerDialog)dialog).updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                break;
            case VIEW_DISHES_DIALOG :
                break;
            case VIEW_EXERCISES_DIALOG :
                break;
        }
    }

    DatePickerDialog.OnDateSetListener myCallBack = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            String newDate=dayOfMonth+"."+(monthOfYear+1)+"."+year;
            try {
                changeViewDay(new MyDate(Day.format.parse(newDate)));
            } catch (ParseException e) {
                Toast.makeText(DiaryActivity.this, "Не верно введена дата \n Введите в виде 25.12.2015", Toast.LENGTH_SHORT).show();
            } catch (NullPointerException e) {
                Toast.makeText(DiaryActivity.this, "Год должен находится в диапазоне \n от 2000 до 2100", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void onViewDayDishes(View v)
    {
        showDialog(VIEW_DISHES_DIALOG);
    }

    public void onViewDayExercises(View v)
    {
        showDialog(VIEW_EXERCISES_DIALOG);
    }
    private void changeViewDay(MyDate date)
    {
        saveViewDay();
        setViewDay(date);
    }
    private void saveViewDay()//закончить
    {
        viewDay.setRecord(((EditText) findViewById(R.id.diary_record)).getText().toString());
        for (Map<String, Object> dish : dishesData)
            viewDay.addDish(db.getDish((String)dish.get(DataBase.DISH_COLUMN_NAME)), Integer.parseInt((String)dish.get(DataBase.DAYS_DISH_COLUMN_WEIGHT)));
        for (Map<String, Object> exercise : exercisesData)
            viewDay.addExercise(db.getExercise((String) exercise.get(DataBase.EXERCISE_COLUMN_NAME)), Integer.parseInt((String)exercise.get(DataBase.DAYS_EXERCISE_COLUMN_TIME)), Integer.parseInt((String) exercise.get(DataBase.DAYS_EXERCISE_COLUMN_QUANTITY)));
        db.saveDay(viewDay);
    }
    private void setViewDay(MyDate date)
    {
        setViewDay(Day.getDayByDate(db, date));
    }
    private void setViewDay(Day day)
    {
        viewDay=day;
        if(dishesData!=null) {
            dishesData.clear();
            dishesData.addAll(DataBase.cursorToArrayList(db.getAllDayDishes(viewDay.getDate())));
            dishesAdapter.notifyDataSetChanged();
        }
        if (exercisesData!=null) {
            exercisesData.clear();
            exercisesData.addAll(DataBase.cursorToArrayList(db.getAllDayExercises(viewDay.getDate())));
            exercisesAdapter.notifyDataSetChanged();
        }
        ((TextView)findViewById(R.id.diary_weekday)).setText(Day.getDayOfWeekByDate(day.getDate()));
        ((EditText)findViewById(R.id.diary_set_date)).setText(Day.format.format(day.getDate()));
        ((EditText)findViewById(R.id.diary_record)).setText(day.getRecord());
        updateCaloriesRow();
    }

    protected void onRestart() {
        dishesData.clear();
        dishesData.addAll(DataBase.cursorToArrayList(db.getAllDayDishes(viewDay.getDate())));
        dishesAdapter.notifyDataSetChanged();
        exercisesData.clear();
        exercisesData.addAll(DataBase.cursorToArrayList(db.getAllDayExercises(viewDay.getDate())));
        exercisesAdapter.notifyDataSetChanged();
        updateCaloriesRow();
        super.onRestart();
    }

    private void updateCaloriesRow()
    {
        Integer receivedCalories=viewDay.getReceivedCalories();
        Integer spentCalories=viewDay.getSpentCalories(profile.getWeight());
        ((TextView)findViewById(R.id.diary_calorie_get)).setText(receivedCalories.toString());
        ((TextView)findViewById(R.id.diary_calorie_spend)).setText(spentCalories.toString());
        ((TextView)findViewById(R.id.diary_calorie_need)).setText(((Integer) (profile.getAimCalorie() + spentCalories - receivedCalories)).toString());
        findViewById(R.id.diary_calorie_get).requestLayout();
        findViewById(R.id.diary_calorie_spend).requestLayout();
        findViewById(R.id.diary_calorie_need).requestLayout();
    }
    @Override
    protected void onDestroy() {
        saveViewDay();
        db.close();
        super.onDestroy();
    }
}
