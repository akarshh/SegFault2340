package com.segfault.homelessshelter;

import android.os.Parcel;
import android.os.Parcelable;

public final class Shelter implements Parcelable {

    private int uniqueKey;
    private String shelterName;
    private String capacity; // Sometimes this can be a string
    private String restrictions;
    private double longitude;
    private double latitude;
    private String address;
    private String specialNotes;
    private String phoneNumber;
    private int vacancy;

    // Constructors

    public Shelter(int uniqueKey, String shelterName, String capacity, String restrictions,
                    double longitude, double latitude, String address, String specialNotes,
                    String phoneNumber) {
        this.uniqueKey = uniqueKey;
        this.shelterName = shelterName;
        this.capacity = capacity;
        this.restrictions = restrictions;
        this.longitude = longitude;
        this.latitude = latitude;
        this.address = address;
        this.specialNotes = specialNotes;
        this.phoneNumber = phoneNumber;
        try {
            this.vacancy = Integer.parseInt(this.capacity);
        } catch (Exception e) {
            this.vacancy = -1;
        }
    }

    public Shelter() {
        this(-1, null, null, null, 0.0, 0.0, null, null, null);
    }

    private Shelter(Parcel in) {
        uniqueKey = in.readInt();
        shelterName = in.readString();
        capacity = in.readString();
        restrictions = in.readString();
        longitude = in.readDouble();
        latitude = in.readDouble();
        address = in.readString();
        specialNotes = in.readString();
        phoneNumber = in.readString();
        vacancy = in.readInt();
    }

    // Static factories

    public static Shelter createFromCSVEntry(String csvEntry) {
        StringBuilder csv = new StringBuilder(csvEntry);
        int uniqueKey = getAndRemoveFirstInt(csv);
        String shelterName = getAndRemoveFirstString(csv);
        String capacity = getAndRemoveFirstString(csv);
        String restrictions = getAndRemoveFirstString(csv);
        double longitude = getAndRemoveFirstDouble(csv);
        double latitude = getAndRemoveFirstDouble(csv);
        String address = getAndRemoveFirstString(csv);
        String specialNotes = getAndRemoveFirstString(csv);
        /* At this point all other fields have been removed, so we just need to cast to string and
        remove quotes just in case. */
        String phoneNumber = csv.toString();
        phoneNumber = phoneNumber.replace("\"", "");
        return new Shelter(uniqueKey, shelterName, capacity, restrictions, longitude, latitude,
                address, specialNotes, phoneNumber);
    }

    public static Shelter createFromStorageEntry(String storageEntry) {
        Shelter shelter = new Shelter();
        String[] fields = storageEntry.split("\\|"); // We need to escape "|" like this because of regex
        for(int i = 0; i <= 9; i++) {
            switch(i) {
                case 0:
                    shelter.setUniqueKey(Integer.parseInt(fields[i]));
                    break;
                case 1:
                    shelter.setShelterName(fields[i]);
                    break;
                case 2:
                    shelter.setCapacity(fields[i]);
                    break;
                case 3:
                    shelter.setRestrictions(fields[i]);
                    break;
                case 4:
                    shelter.setLongitude(Double.parseDouble(fields[i]));
                    break;
                case 5:
                    shelter.setLatitude(Double.parseDouble(fields[i]));
                    break;
                case 6:
                    shelter.setAddress(fields[i]);
                    break;
                case 7:
                    shelter.setSpecialNotes(fields[i]);
                    break;
                case 8:
                    shelter.setPhoneNumber(fields[i]);
                    break;
                case 9:
                    shelter.setVacancy(Integer.parseInt(fields[i]));
                    break;
            }
        }
        return shelter;
    }

    // Getters

    public int getUniqueKey() {
        return uniqueKey;
    }

    public String getShelterName() {
        return shelterName;
    }

    public String getCapacity() {
        return capacity;
    }

    public String getRestrictions() {
        return restrictions;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getAddress() {
        return address;
    }

    public String getSpecialNotes() {
        return specialNotes;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getVacancy() {
        return vacancy;
    }

    // Setters

    public void setUniqueKey(int uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public void setShelterName(String shelterName) {
        this.shelterName = shelterName;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public void setRestrictions(String restrictions) {
        this.restrictions = restrictions;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setSpecialNotes(String specialNotes) {
        this.specialNotes = specialNotes;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setVacancy(int vacancy) {
        this.vacancy = vacancy;
    }

    // Private helpers

    private static int getAndRemoveFirstInt(StringBuilder csv) {
        int field = Integer.parseInt(csv.substring(0, csv.indexOf(",")));
        csv.delete(0, csv.indexOf(",") + 1);
        return field;
    }

    private static double getAndRemoveFirstDouble(StringBuilder csv) {
        double field = Double.parseDouble(csv.substring(0, csv.indexOf(",")));
        csv.delete(0, csv.indexOf(",") + 1);
        return field;
    }

    private static String getAndRemoveFirstString(StringBuilder csv) {
        // The field can be surrounded by quotes and have a comma inside, or no quotes and no comma,
        // so we need to deal with both
        String field;
        if(csv.charAt(0) == '"') {
            field = csv.substring(1, csv.indexOf("\"", 1));
            csv.delete(0, csv.indexOf("\"", 1) + 2);
        } else {
            field = csv.substring(0, csv.indexOf(","));
            csv.delete(0, csv.indexOf(",") + 1);
        }
        return field;
    }

    // Public helpers

    public String toEntry() {
        String[] fields = {
                String.valueOf(uniqueKey),
                shelterName,
                capacity,
                restrictions,
                String.valueOf(longitude),
                String.valueOf(latitude),
                address,
                specialNotes,
                phoneNumber,
                String.valueOf(vacancy)
        };
        StringBuilder entryBuilder = new StringBuilder();
        for(String field : fields) {
            entryBuilder.append(field);
            entryBuilder.append("|");
        }
        entryBuilder.setLength(entryBuilder.length() - 1); // Remove the last "|"
        return entryBuilder.toString();
    }

    public boolean matchesFilter(String nameFilter, String genderFilter, String ageFilter) {
        // Return true if we're not searching, or if we match all three filters
        return matchesShelterName(nameFilter) && matchesGender(genderFilter) && matchesAgeRange(ageFilter);
    }

    private boolean matchesShelterName(String nameFilter) {
        String lowerCaseShelterName = shelterName.toLowerCase();
        return lowerCaseShelterName.contains(nameFilter.toLowerCase());
    }

    private boolean matchesGender(String genderFilter) {
        return "Anyone".equals(genderFilter) // Gender wasn't filtered
                || hasUnspecifiedGender() // Shelter has unspecified gender restriction
                || restrictions.contains(genderFilter); // Shelter matches gender restriction
    }

    private boolean hasUnspecifiedGender() {
        String[] genders = {"Men", "Women", "Trans men", "Trans women"};
        for(String gender : genders) {
            if(restrictions.contains(gender)) {
                return false; // Restriction specifies some gender
            }
        }
        return true; // Restriction didn't specify any gender
    }

    private boolean matchesAgeRange(String ageFilter) {
        return "Anyone".equals(ageFilter) // Age range wasn't filtered
                || hasUnspecifiedAgeRange() // Shelter has unspecified age range restriction
                || restrictions.contains(ageFilter);  // Shelter matches age range restriction
    }

    private boolean hasUnspecifiedAgeRange() {
        String[] ageRanges = {"Newborns", "Children", "Young adults"};
        for(String ageRange : ageRanges) {
            if(restrictions.contains(ageRange)) {
                return false; // Restriction specifies some age range
            }
        }
        return true; // Restriction didn't specify any age range
    }

    // Object overrides

    @Override
    public String toString() {
        return uniqueKey + " | " + shelterName + " | " + capacity + " | " + restrictions + " | "
                + longitude + " | " + latitude + " | " + address + " | " + specialNotes + " | "
                + phoneNumber;
    }

    @Override
    public boolean equals(Object o) {
        // Auto-generated equals() by Android Studio
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shelter shelter = (Shelter) o;
        return uniqueKey == shelter.uniqueKey &&
                Double.compare(shelter.longitude, longitude) == 0 &&
                Double.compare(shelter.latitude, latitude) == 0 &&
                vacancy == shelter.vacancy &&
                shelterName.equals(shelter.shelterName) &&
                capacity.equals(shelter.capacity) &&
                restrictions.equals(shelter.restrictions) &&
                address.equals(shelter.address) &&
                specialNotes.equals(shelter.specialNotes) &&
                phoneNumber.equals(shelter.phoneNumber);
    }

    // Parcelable methods

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(uniqueKey);
        parcel.writeString(shelterName);
        parcel.writeString(capacity);
        parcel.writeString(restrictions);
        parcel.writeDouble(longitude);
        parcel.writeDouble(latitude);
        parcel.writeString(address);
        parcel.writeString(specialNotes);
        parcel.writeString(phoneNumber);
        parcel.writeInt(vacancy);
    }

    // Weird thing from Parcelable

    public static final Creator<Shelter> CREATOR = new Creator<Shelter>() {
        @Override
        public Shelter createFromParcel(Parcel in) {
            return new Shelter(in);
        }

        @Override
        public Shelter[] newArray(int size) {
            return new Shelter[size];
        }
    };
}
