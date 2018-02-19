package com.segfault.homelessshelter;

public class User {

    private String name;
    private String email;
    private String password;
    private boolean admin;

    public User(String name, String email, String password, boolean admin) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.admin = admin;
    }

    // Getters

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
        return admin;
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

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    @Override
    public boolean equals(Object object) {
        if(object instanceof User) {
            User user = (User) object;
            return name.equals(user.name) && email.equals(user.email) && password.equals(user.password);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode() + email.hashCode() + password.hashCode();
    }
}
