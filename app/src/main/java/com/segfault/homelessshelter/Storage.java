package com.segfault.homelessshelter;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/**
 * Singleton for abstracting SharedPreferences
 */
public final class Storage {

    private static Storage storage;
    private final SharedPreferences preferences;

    private Storage(Context context) {
        preferences = context.getSharedPreferences(
                "com.segfault.homelessshelter.PREFERENCES", Context.MODE_PRIVATE);
    }

    public static Storage getInstance(Context context) {
        if(storage == null) {
            storage = new Storage(context);
        }
        return storage;
    }

    // Save methods

    public void saveInt(String key, int toSave) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, toSave);
        editor.apply();
    }

    public void saveLong(String key, long toSave) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(key, toSave);
        editor.apply();
    }

    public void saveStringSet(String key, Set<String> toSave) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet(key, toSave);
        editor.apply();
    }

    // Load methods

    public int loadInt(String key) {
        return loadInt(key, -1);
    }

    public int loadInt(String key, int defaultValue) {
        return preferences.getInt(key, defaultValue);
    }

    public long loadLong(String key) {
        return loadLong(key, -1);
    }

    public long loadLong(String key, long defaultValue) {
        return preferences.getLong(key, defaultValue);
    }

    public Set<String> loadStringSet(String key) {
        return preferences.getStringSet(key, new HashSet<String>());
    }

}
