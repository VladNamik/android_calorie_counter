package ru.startandroid.myapplication;


public class Dish {
    private String name;
    private int calories_per_100_gm;
    public Dish()
    {
        this("", 0);
    }
    public Dish(String name, int calories_per_100_gm)
    {
        this.calories_per_100_gm=calories_per_100_gm;
        this.name=name;
    }

    public int parseCalories(int weight)
    {
        return calories_per_100_gm*weight/100;
    }
    public String getName() {
        return name;
    }
    public int getCalories_per_100_gm(){ return calories_per_100_gm;}

    public void setName(String name)
    {
        this.name=name;
    }
    public void setCalories(int calories_per_100_gm)
    {
        this.calories_per_100_gm=calories_per_100_gm;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Dish)
            return name.equals(((Dish)o).name);
        else
            return false;
    }
}
