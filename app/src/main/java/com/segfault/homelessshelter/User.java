package com.segfault.homelessshelter;

import android.util.Log;

import java.util.Arrays;

public class User {

    private String name;
    private String email;
    private String password;
    private boolean admin;

    public User(String name, String email, String password, boolean admin) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.admin = admin;
    }

    public static User createFromStorageEntry(String storageEntry) {
        String[] fields = storageEntry.split("\\|"); // We need to escape "|" because of regex
        return new User(fields[0], fields[1], fields[2], Boolean.parseBoolean(fields[3]));
    }

    // Getters

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public boolean isAdmin() {
        return admin;
    }

    // Setters

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    // Helper methods

    public String toEntry() {
        return name + "|" + email + "|" + password + "|" + String.valueOf(isAdmin());
    }

    // toString

    @Override
    public String toString() {
        return name + " | " + email + " | " + password + " | " + String.valueOf(isAdmin());
    }
}
