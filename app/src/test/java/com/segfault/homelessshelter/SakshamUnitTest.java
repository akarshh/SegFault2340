package com.segfault.homelessshelter;

import org.junit.Test;

import static org.junit.Assert.*;




public class SakshamUnitTest {


    // Storage entries
    private String[] entries = {
            "0|My Sister's House|264|Women/Children|-84.410142|33.780174|921 Howell Mill Road, Atlanta, Georgia 30318|Temporary, Emergency, Residential Recovery|(404) 367-2465|-1",
            "1|The Atlanta Day Center for Women & Children|140|Women/Children|-84.408771|33.784889|655 Ethel Street, Atlanta, Georgia 30318|Career Facilitation|(404) 588-4007|-1",
            "3|Fuqua Hall|92|Men|-84.392273|33.76515|144 Mills Street, Atlanta, Georgia 30313|Transitional housing|(404) 367-2492|-1",
            "4|Atlanta's Children Center|40|Families w/ Children under 5|-84.384433|33.770949|607 Peachtree Street NE Atlanta, GA 30308|Children's programs, early childhood education|(404) 892-3713|-1",


    };

    // Array of
    private Shelter[] expected = {
            new Shelter(0, "My Sister's House", "264", "Women/Children", -84.410142, 33.780174, "921 Howell Mill Road, Atlanta, Georgia 30318", "Temporary, Emergency, Residential Recovery", "(404) 367-2465"),
            new Shelter(1, "The Atlanta Day Center for Women & Children", "140", "Women/Children", -84.408771, 33.784889, "655 Ethel Street, Atlanta, Georgia 30318", "Career Facilitation", "(404) 588-4007"),
            new Shelter(3, "Fuqua Hall", "92", "Men", -84.392273, 33.76515, "144 Mills Street, Atlanta, Georgia 30313", "Transitional housing", "(404) 367-2492"),
            new Shelter(4, "Atlanta's Children Center", "40", "Families w/ Children under 5", -84.384433, 33.770949, "607 Peachtree Street NE Atlanta, GA 30308", "Children's programs, early childhood education", "(404) 892-3713"),
   };

    // Empty Array to be filled with newly created Shelter instances using createFromStorageEntry()
    private Shelter[] actual = new Shelter[4];

    @Test
    public void createFromStorageEntry() throws Exception {

        // Set vacancy to -1 for each Shelter instance
        for(int i = 0; i < expected.length; i++) {
            expected[i].setVacancy(-1);
        }

        for(int i = 0; i < actual.length; i++) {
            actual[i] = Shelter.createFromStorageEntry(entries[i]);
        }
        assertArrayEquals(expected, actual);
    }

}