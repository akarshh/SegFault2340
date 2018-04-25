package com.segfault.homelessshelter;

public class UserFactory {

    public enum UserType {
        ADMIN, CLIENT
    }

    public static User create(UserType userType, String name, String email, String password) {
        if(userType == UserType.ADMIN) {
            return new Admin(name, email, password);
        } else {
            return new Client(name, email, password);
        }
    }

    public static User createFromStorageEntry(String storageEntry) {
        String[] fields = storageEntry.split("\\|"); // We need to escape "|" because of regex
        if(fields[3].equals("true")) {
            return new Admin(fields[0], fields[1], fields[2]);
        } else {
            return new Client(fields[0], fields[1], fields[2]);
        }
    }
}
