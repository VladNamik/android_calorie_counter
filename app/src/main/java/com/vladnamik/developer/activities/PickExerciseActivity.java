package com.vladnamik.developer.activities;

import android.app.Dialog;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.vladnamik.developer.db.DataBase;
import com.vladnamik.developer.utils.MyDate;
import com.vladnamik.developer.R;

import java.util.ArrayList;
import java.util.Map;

public class PickExerciseActivity extends AppCompatActivity {
    private DataBase db;
    private Map<String, Object> map;
    private ArrayList<Map<String, Object>> data;
    private SimpleAdapter sAdapter;
    private long selectedElementId = -1;
    ListView listView;
    public static MyDate date;
    private static final int SET_EXERCISE_DATA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_exercise);

        db = DataBase.getDataBase(this);
        data = DataBase.cursorToArrayList(db.getExercises());

        String[] from = new String[]{DataBase.EXERCISE_COLUMN_NAME, DataBase.DAYS_EXERCISE_COLUMN_TIME};
        int[] to = new int[]{R.id.db_item_name, R.id.db_item_right_text};

        sAdapter = new SimpleAdapter(this, data, R.layout.database_item, from, to);
        listView = findViewById(R.id.pick_exercise_list);
        listView.setAdapter(sAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) findViewById(R.id.pick_exercise_picked_exercise_name)).setText(((TextView) view.findViewById(R.id.db_item_name)).getText().toString());
                selectedElementId = id;
                onSetPickedExercise(view);
            }
        });

        findViewById(R.id.pick_exercise_OK_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedElementId < 0) {
                    finish();
                    return;
                }
                String pickedExerciseName = (String) data.get((int) selectedElementId).get(DataBase.EXERCISE_COLUMN_NAME);
                int pickedTime = Integer.parseInt(((EditText) findViewById(R.id.pick_exercise_picked_exercise_time)).getText().toString());
                int pickedQuantity = Integer.parseInt(((EditText) findViewById(R.id.pick_exercise_picked_exercise_quantity)).getText().toString());
                Cursor cursor = db.getDayExercise(date, pickedExerciseName);
                if (cursor == null)
                    db.addDayExercise(date, pickedExerciseName, pickedTime, pickedQuantity);
                else
                    db.updateDayExercise(date, pickedExerciseName, pickedTime + cursor.getInt(cursor.getColumnIndex(DataBase.DAYS_EXERCISE_COLUMN_TIME)), pickedQuantity + cursor.getInt(cursor.getColumnIndex(DataBase.DAYS_EXERCISE_COLUMN_QUANTITY)));
                finish();
            }
        });
        findViewById(R.id.pick_exercise_cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    public void onSetPickedExercise(View view) {
        showDialog(SET_EXERCISE_DATA);
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == SET_EXERCISE_DATA) {
            final Dialog dialog = new Dialog(this);
            dialog.setTitle(getString(R.string.enter_exercise_time_quantity));
            dialog.setContentView(R.layout.set_exercise_data_dialog);
            Button okButton = dialog.findViewById(R.id.set_exercise_data_dialog_OK_button);
            Button cancelButton = dialog.findViewById(R.id.set_exercise_data_dialog_cancel_button);
            final NumberPicker minuteNumber = dialog.findViewById(R.id.set_exercise_data_dialog_set_minute);
            final NumberPicker sekNumber = dialog.findViewById(R.id.set_exercise_data_dialog_set_sek);
            final NumberPicker quantityNumber = dialog.findViewById(R.id.set_exercise_data_dialog_set_quantity);
            minuteNumber.setMaxValue(59);
            minuteNumber.setMinValue(0);
            sekNumber.setMaxValue(59);
            sekNumber.setMinValue(0);
            quantityNumber.setMinValue(0);
            quantityNumber.setMaxValue(10000);
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Integer timeSek = minuteNumber.getValue() * 60 + sekNumber.getValue();
                    ((EditText) findViewById(R.id.pick_exercise_picked_exercise_time)).setText(timeSek.toString());
                    ((EditText) findViewById(R.id.pick_exercise_picked_exercise_quantity)).setText(((Integer) quantityNumber.getValue()).toString());
                    dialog.dismiss();
                }
            });
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            return dialog;
        }
        return super.onCreateDialog(id);
    }

}
