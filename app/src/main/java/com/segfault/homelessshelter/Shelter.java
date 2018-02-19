package com.segfault.homelessshelter;

import android.os.Parcel;
import android.os.Parcelable;

public class Shelter implements Parcelable {

    private int uniqueKey;
    private String shelterName;
    private String capacity; // Sometimes this can be a string
    private String restrictions;
    private double longitude;
    private double latitude;
    private String address;
    private String specialNotes;
    private String phoneNumber;

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

    // Constructors

    public Shelter(int uniqueKey, String shelterName, String capacity, String restrictions, double longitude,
                   double latitude, String address, String specialNotes, String phoneNumber) {
        this.uniqueKey = uniqueKey;
        this.shelterName = shelterName;
        this.capacity = capacity;
        this.restrictions = restrictions;
        this.longitude = longitude;
        this.latitude = latitude;
        this.address = address;
        this.specialNotes = specialNotes;
        this.phoneNumber = phoneNumber;
    }

    public Shelter(String csvString) {
        StringBuilder csv = new StringBuilder(csvString);
        this.uniqueKey = getAndRemoveFirstInt(csv);
        this.shelterName = getAndRemoveFirstString(csv);
        this.capacity = getAndRemoveFirstString(csv);
        this.restrictions = getAndRemoveFirstString(csv);
        this.longitude = getAndRemoveFirstDouble(csv);
        this.latitude = getAndRemoveFirstDouble(csv);
        this.address = getAndRemoveFirstString(csv);
        this.specialNotes = getAndRemoveFirstString(csv);
        // At this point all other fields have been removed, so we just need to cast to string and remove quotes just in case
        this.phoneNumber = csv.toString().replace("\"", "");
    }

    protected Shelter(Parcel in) {
        uniqueKey = in.readInt();
        shelterName = in.readString();
        capacity = in.readString();
        restrictions = in.readString();
        longitude = in.readDouble();
        latitude = in.readDouble();
        address = in.readString();
        specialNotes = in.readString();
        phoneNumber = in.readString();
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

    // Helpers

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
    }

    // toString

    @Override
    public String toString() {
        return uniqueKey + " | " + shelterName + " | " + capacity + " | " + restrictions + " | " + longitude
                + " | " + latitude + " | " + address + " | " + specialNotes + " | " + phoneNumber;
    }
}
