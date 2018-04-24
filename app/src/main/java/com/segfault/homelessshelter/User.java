package com.segfault.homelessshelter;

import java.util.Objects;

public interface User {

    String name = null;
    String email = null;
    String password = null;

    // Getters

    String getName();

    String getEmail();

    String getPassword();

    boolean isAdmin();

    // Setters

    void setName(String name);

    void setEmail(String email);

    void setPassword(String password);

    // Helper methods

    String toEntry();

    // Object overrides

    @Override
    String toString();

    @Override
    boolean equals(Object o);
}
