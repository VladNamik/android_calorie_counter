package ru.startandroid.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class DishesEditorActivity extends AppCompatActivity {
    static Dish dish;
    DataBase db;
    private String firstName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dishes_editor);

        db=DataBase.getDataBase(this);
        if (dish==null) {
            dish = new Dish();
        }
        firstName=dish.getName();
        ((EditText)findViewById(R.id.dish_editor_dish_name)).setText(dish.getName());
        ((EditText)findViewById(R.id.dish_editor_calories)).setText(((Integer)dish.getCalories_per_100_gm()).toString());
        findViewById(R.id.dish_editor_OK_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dish.setName(((EditText) findViewById(R.id.dish_editor_dish_name)).getText().toString());
                dish.setCalories(Integer.parseInt(((EditText) findViewById(R.id.dish_editor_calories)).getText().toString()));
                if (!firstName.equals(dish.getName()))
                    db.deleteDish(firstName);
                if (db.getDish(dish.getName()) == null)
                    db.addDish(dish);
                else
                    db.updateDish(dish);
                dish = null;
                finish();
            }
        });
        findViewById(R.id.dish_editor_cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dish = null;
                finish();
            }
        });

    }
}
