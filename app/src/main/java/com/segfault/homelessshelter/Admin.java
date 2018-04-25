package com.segfault.homelessshelter;

public class Admin implements User {

    private String name;
    private String email;
    private String password;

    public Admin(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    // Getters

    @Override
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public boolean isAdmin() {
        return true;
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

    // Helper methods

    public String toEntry() {
        return name + "|" + email + "|" + password + "|true";
    }

    // Object overrides

    @Override
    public String toString() {
        return name + " | " + email + " | " + password + " | true";
    }

    @Override
    public boolean equals(Object o) {
        // Auto-generated equals() by Android Studio
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Admin admin = (Admin) o;
        return name.equals(admin.name) &&
                email.equals(admin.email) &&
                password.equals(admin.password);
    }
}
