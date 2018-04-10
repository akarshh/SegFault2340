package com.segfault.homelessshelter;

public class User {

    private final String name;
    private final String email;
    private final String password;
    private final boolean admin;

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

// --Commented out by Inspection START (4/9/18 11:31 PM):
//    public void setName(String name) {
//        this.name = name;
//    }
// --Commented out by Inspection STOP (4/9/18 11:31 PM)

// --Commented out by Inspection START (4/9/18 11:31 PM):
//    public void setEmail(String email) {
//        this.email = email;
//    }
// --Commented out by Inspection STOP (4/9/18 11:31 PM)

// --Commented out by Inspection START (4/9/18 11:31 PM):
//    public void setPassword(String password) {
//        this.password = password;
//    }
// --Commented out by Inspection STOP (4/9/18 11:31 PM)

// --Commented out by Inspection START (4/9/18 11:31 PM):
//    public void setAdmin(boolean admin) {
//        this.admin = admin;
//    }
// --Commented out by Inspection STOP (4/9/18 11:31 PM)

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
