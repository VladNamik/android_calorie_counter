package ru.startandroid.myapplication;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DishActivity extends AppCompatActivity{
    DataBase db;//объект БД
    Map<String, Object> map;
    ArrayList<Map<String, Object>> data;//основные данные
    SimpleAdapter sAdapter;//адаптер
    long selectedElementId=-1;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish);
        findViewById(R.id.from_dishes_to_menu).setOnClickListener(new ToWindowOnClickWithClosing(this, MyMenuActivity.class));

        db = DataBase.getDataBase(this);//открываем БД
        data=DataBase.cursorToArrayList(db.getDishes());

        // формируем столбцы сопоставления
        String[] from = new String[] {DataBase.DISH_COLUMN_NAME, DataBase.DISH_COLUMN_CALORIES_PER_100_GM };//названия колонок
        int[] to = new int[] { R.id.db_item_name, R.id.db_item_right_text};//места для записи (View id)

        sAdapter = new SimpleAdapter(this, data ,  R.layout.database_item, from, to);
        listView= (ListView) findViewById(R.id.dish_list_view);
        listView.setAdapter(sAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedElementId = id;
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "По алфавиту");
        menu.add(0, 2, 1, "По калорийности");
        menu.add(0, 3, 2, "По id");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case 1: DataBase.dishSortId=1;//по алфавиту
                break;
            case 2: DataBase.dishSortId=2;//По калорийности
                break;
            case 3: DataBase.dishSortId=0;//По id
                break;
        }
        data.clear();
        data.addAll(DataBase.cursorToArrayList(db.getDishes()));
        sAdapter.notifyDataSetChanged();
        return super.onOptionsItemSelected(item);
    }

    public void onDeleteDish(View view)
    {
        if(selectedElementId<0)
            Toast.makeText(this, "Выберите блюдо", Toast.LENGTH_SHORT).show();
        else
        {
            String dishName=(String)data.get((int)selectedElementId).get(DataBase.DISH_COLUMN_NAME);
            db.deleteDish(dishName);
            data.remove((int)selectedElementId);
            selectedElementId=-1;
            sAdapter.notifyDataSetChanged();
        }
    }

    public void onEditDish(View view)
    {
        if (selectedElementId<0) {
            Toast.makeText(this, "Выберите блюдо", Toast.LENGTH_SHORT).show();
            return;
        }
        //заполняем новыми данными
        Dish dish = new Dish();
        map=data.get((int)selectedElementId);
        dish.setName(map.get(DataBase.DISH_COLUMN_NAME).toString());
        dish.setCalories(Integer.parseInt(map.get(DataBase.DISH_COLUMN_CALORIES_PER_100_GM).toString()));
        DishesEditorActivity.dish=dish;
        onCreateDish(view);
    }

    public void onCreateDish(View view)
    {
        Intent intent = new Intent(this, DishesEditorActivity.class);
        startActivity(intent);//передаём управление редактору
        selectedElementId=-1;;
    }

    @Override
    protected void onRestart() {
        data.clear();
        data.addAll(DataBase.cursorToArrayList(db.getDishes()));
        sAdapter.notifyDataSetChanged();
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();//закрываем БД
    }
}
