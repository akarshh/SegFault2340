package com.segfault.homelessshelter;

public class UserFactory {

    public enum UserType {
        ADMIN, CLIENT
    }

    public static User createFromStorageEntry(UserType userType, String storageEntry) {
        String[] fields = storageEntry.split("\\|"); // We need to escape "|" because of regex
        if(userType == UserType.ADMIN) {
            return new Admin(fields[0], fields[1], fields[2]);
        } else {
            return new Client(fields[0], fields[1], fields[2]);
        }
    }
}
