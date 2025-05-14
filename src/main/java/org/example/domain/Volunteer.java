package org.example.domain;

import java.util.*;

public class Volunteer {
    private final String name;
    private final String id;

    private final List<Service> preferences; // Sorted by rank: 1st to 5th

    public Volunteer(String name, String id, List<Service> preferences) {
        this.name = name;
        this.id = id;
        this.preferences = new ArrayList<>(preferences); // Defensive copy

    }
    public String getId(){ return id; }

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
