package com.segfault.homelessshelter;

import java.util.Objects;

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

    public User() {
        this(null, null, null, false);
    }

    public static User createFromStorageEntry(String storageEntry) {
        User user = new User();
        String[] fields = storageEntry.split("\\|"); // We need to escape "|" because of regex
        for(int i = 0; i <= 3; i++) {
            switch(i) {
                case 0:
                    user.setName(fields[i]);
                    break;
                case 1:
                    user.setEmail(fields[i]);
                    break;
                case 2:
                    user.setPassword(fields[i]);
                    break;
                case 3:
                    user.setAdmin(Boolean.parseBoolean(fields[i]));
                    break;
            }
        }
        return user;
    }

    // Getters

// --Commented out by Inspection START (4/9/18 11:31 PM):
//    public String getName() {
//        return name;
//    }
// --Commented out by Inspection STOP (4/9/18 11:31 PM)

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    private boolean isAdmin() {
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
        String[] fields = {
                name,
                email,
                password,
                String.valueOf(admin)
        };
        StringBuilder entryBuilder = new StringBuilder();
        for(String field : fields) {
            entryBuilder.append(field);
            entryBuilder.append("|");
        }
        entryBuilder.setLength(entryBuilder.length() - 1); // Remove the last "|"
        return entryBuilder.toString();
    }

    // Object overrides

    @Override
    public String toString() {
        String[] fields = {
                name,
                email,
                password,
                String.valueOf(admin)
        };
        StringBuilder stringBuilder = new StringBuilder();
        for(String field : fields) {
            stringBuilder.append(field);
            stringBuilder.append(", ");
        }
        stringBuilder.setLength(stringBuilder.length() - 2); // Remove the last ", "
        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object o) {
        // Auto-generated equals() by Android Studio
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return admin == user.admin &&
                name.equals(user.name) &&
                email.equals(user.email) &&
                password.equals(user.password);
    }
}
