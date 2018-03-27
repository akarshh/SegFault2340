package com.segfault.homelessshelter;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/**
 * Singleton for abstracting SharedPreferences
 */
public class Storage {

    static Storage storage;

    private SharedPreferences preferences;

    private Storage(Context context) {
        preferences = context.getSharedPreferences(
                "com.segfault.homelessshelter.PREFERENCES", Context.MODE_PRIVATE);
    }

    public static Storage getInstance(Context context) {
        if(storage == null) {
            storage = new Storage(context);
            return storage;
        } else {
            return storage;
        }
    }

    // Save methods

    public void saveInt(String key, int integer) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, integer);
        editor.apply();
    }

    public void saveStringSet(String key, Set<String> set) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet(key, set);
        editor.apply();
    }

    // Load methods

    public int loadInt(String key) {
        return preferences.getInt(key, -1);
    }

    public Set<String> loadStringSet(String key) {
        return preferences.getStringSet(key, new HashSet<String>());
    }

}
