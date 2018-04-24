package com.segfault.homelessshelter;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class RafaelUnitTest {
    private Shelter[] shelters = {
        new Shelter(0, "My Sister's House", "264", "Women/Children", -84.410142, 33.780174, "921 Howell Mill Road, Atlanta, Georgia 30318", "Temporary, Emergency, Residential Recovery", "(404) 367-2465"),
        new Shelter(1, "The Shepherd's Inn", "450", "Men", -84.39265, 33.765162, "156 Mills Street, Atlanta, Georgia 30313", "Temporary, Residential Recovery", "(404) 367-2493"),
        new Shelter(2, "Fuqua Hall", "92", "Men", -84.392273, 33.76515, "144 Mills Street, Atlanta, Georgia 30313", "Transitional housing", "(404) 367-2492"),
        new Shelter(3, "Eden Village ", "32 for families, 80 singles", "Women/Children", -84.43023, 33.762316, "1300 Joseph E. Boone Blvd NW, Atlanta, GA 30314", "General recovery services", "(404)-874-2241"),
    };

    private String[][] entries = {
            {"My Sister's House", "Women", "Anyone"},
            {"The Shepherd's Inn", "Anyone", "Children"},
            {"Fuqua Hall.", "Men", "Anyone"},
            {"Eden Village", "Men", "Children"}
    };

    private Boolean[] expected = {
        true, true, false, false
    };

    private Boolean[] actual = new Boolean[4];

    @Test
    public void matchesFilter() {
        for(int i = 0; i < actual.length; i++) {
            actual[i] = shelters[i].matchesFilter(entries[i][0], entries[i][1], entries[i][2]);
        }
        assertArrayEquals(expected, actual);
    }
}
