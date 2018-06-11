package com.vladnamik.developer.preferences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.vladnamik.developer.activities.ProfileActivity;

public class Profile {
    private boolean gender = true;
    private int age = 18;
    private int height = 175;
    private int weight = 70;
    private int aimCalorie = 1500;
    private static Profile profile = null;

    private Profile(Context context) {
        SharedPreferences sPref = context.getSharedPreferences("profile_preferences", Context.MODE_PRIVATE);
        if (!sPref.contains("age")) {
            SharedPreferences.Editor editor = sPref.edit();
            editor.putBoolean("gender", gender);
            editor.putInt("age", age);
            editor.putInt("height", height);
            editor.putInt("weight", weight);
            aimCalorie = calculateCalories();
            editor.putInt("aimCalorie", aimCalorie);
            editor.apply();
        } else {
            gender = sPref.getBoolean("gender", gender);
            age = sPref.getInt("age", age);
            height = sPref.getInt("height", height);
            weight = sPref.getInt("weight", weight);
            aimCalorie = sPref.getInt("aimCalorie", aimCalorie);
        }
    }

    public static Profile getProfile(Context context) {
        if (profile == null) {
            profile = new Profile(context);
        }
        return profile;
    }

    public int calculateCalories() {
        if (gender)
            return (int) (66.5 + 13.75 * weight + 5.003 * height - 6.755 * age);
        else
            return (int) (655 + 9.563 * weight + 1.85 * height - 4.676 * age);
    }

    public int getAge() {
        return age;
    }

    public int getWeight() {
        return weight;
    }

    public int getHeight() {
        return height;
    }

    public boolean getGender() {
        return gender;
    }

    public int getAimCalorie() {
        return aimCalorie;
    }

    public void setGender(Activity activity, boolean gender) {
        if (activity instanceof ProfileActivity) {
            this.gender = gender;
        }
    }

    public void setAge(Activity activity, int age) {
        if (activity instanceof ProfileActivity) {
            this.age = age;
        }
    }

    public void setWeight(Activity activity, int weight) {
        if (activity instanceof ProfileActivity) {
            this.weight = weight;
        }
    }

    public void setHeight(Activity activity, int height) {
        if (activity instanceof ProfileActivity) {
            this.height = height;
        }
    }

    public void setAimCalorie(Activity activity, int aimCalorie) {
        if (activity instanceof ProfileActivity) {
            this.aimCalorie = aimCalorie;
        }
    }

    public void saveData(Context context) {
        SharedPreferences sPref = context.getSharedPreferences("profile_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();
        editor.putBoolean("gender", gender);
        editor.putInt("age", age);
        editor.putInt("height", height);
        editor.putInt("weight", weight);
        editor.putInt("aimCalorie", aimCalorie);
        editor.commit();
    }
}
