package com.segfault.homelessshelter;
import org.junit.Test;
import static org.junit.Assert.*;

public class AkarshUnitTest {
    // Storage entries
    private String[] entries = {
            "test1|test1@gmail.com|12345678|false",
            "test2|test2@gmail.com|abcdefgh|true",
            "test3|test3|abcdefgh|false",
            "test4|test4|12345678|true",
            "test1|test1@gmail.com|12345678|false",
            "test2|test2@gmail.com|abcdefgh|true",
            "test3|test3|abcdefgh|false",
            "test4|test4|12345678|true"
    };

    // Expected Users
    private User[] expected = {
            new User("test1", "test1@gmail.com", "12345678", false),
            new User("test2", "test2@gmail.com", "abcdefgh", true),
            new User("test3", "test3", "abcdefgh", false),
            new User("test4", "test4", "12345678", true),
            new User("test1", "test1@gmail.com", "12345678", false),
            new User("test2", "test2@gmail.com", "abcdefgh", true),
            new User("test3", "test3", "abcdefgh", false),
            new User("test4", "test4", "12345678", true)
    };

    // Empty array to be filled with newly created Users using createFromStorageEntry()
    private User[] actual = new User[8];

    @Test
    public void createFromStorageEntry() throws Exception {

        for(int i = 0; i < actual.length; i++) {
            actual[i] = User.createFromStorageEntry(entries[i]);
        }
        assertArrayEquals(expected, actual);
    }
}