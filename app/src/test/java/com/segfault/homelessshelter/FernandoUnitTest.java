package com.segfault.homelessshelter;
import org.junit.Test;
import static org.junit.Assert.*;

public class FernandoUnitTest {
    // Storage entries
    private String[] entries = {
        "Fernando|femello@gatech.edu|12345678|false",
        "Rodrigo|none|abcdefgh|true",
        "1234|test@123.abc|!@#$%^&*()|false",
        "!@#$%^&*()|!@#$%^&*()|123abc!|true"
    };

    // Expected Users
    private User[] expected = {
        new User("Fernando", "femello@gatech.edu", "12345678", false),
        new User("Rodrigo", "none", "abcdefgh", true),
        new User("1234", "test@123.abc", "!@#$%^&*()", false),
        new User("!@#$%^&*()", "!@#$%^&*()", "123abc!", true)
    };

    // Empty array to be filled with newly created Users using createFromStorageEntry()
    private User[] actual = new User[4];

    @Test
    public void createFromStorageEntry() throws Exception {

        for(int i = 0; i < actual.length; i++) {
            actual[i] = User.createFromStorageEntry(entries[i]);
        }
        assertArrayEquals(expected, actual);
    }
}