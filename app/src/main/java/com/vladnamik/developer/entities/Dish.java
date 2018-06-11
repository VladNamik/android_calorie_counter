package com.vladnamik.developer.entities;


public class Dish {
    private String name;
    private int caloriesPer100Gm;

    public Dish() {
        this("", 0);
    }

    public Dish(String name, int caloriesPer100Gm) {
        this.caloriesPer100Gm = caloriesPer100Gm;
        this.name = name;
    }

    public int parseCalories(int weight) {
        return caloriesPer100Gm * weight / 100;
    }

    public String getName() {
        return name;
    }

    public int getCaloriesPer100Gm() {
        return caloriesPer100Gm;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCalories(int caloriesPer100Gm) {
        this.caloriesPer100Gm = caloriesPer100Gm;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Dish)
            return name.equals(((Dish) o).name);
        else
            return false;
    }
}
