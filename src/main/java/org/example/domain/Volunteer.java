package org.example.domain;

import java.util.*;

public class Volunteer {
    private final String name;
    private final List<Service> preferences; // Sorted by rank: 1st to 5th

    public Volunteer(String name, List<Service> preferences) {
        this.name = name;
        this.preferences = new ArrayList<>(preferences); // Defensive copy
    }

    public String getName() {
        return name;
    }

    public List<Service> getPreferences() {
        return preferences;
    }

    @Override
    public String toString() {
        return name;
    }
}
