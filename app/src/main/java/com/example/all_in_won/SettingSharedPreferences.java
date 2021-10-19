package com.example.all_in_won;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingSharedPreferences {
    private SharedPreferences settingSharedPreferences;

    SettingSharedPreferences(Context context){
        settingSharedPreferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
    }

    public SharedPreferences getSettingSharedPreferences() {
        return settingSharedPreferences;
    }
}
