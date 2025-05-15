package org.example.domain;

import java.util.*;

public class Volunteer {
    private final String name;
    private final String id;
    private final List<Service> preferences;

    public Volunteer(String name, String id, List<Service> preferences) {
        this.name = name;
        this.id = id;
        this.preferences = new ArrayList<>(preferences); // Defensive copy
    }

    // Copy constructor
    public Volunteer(Volunteer other) {
        this.name = other.name;
        this.id = other.id;
        this.preferences = new ArrayList<>();
        for (Service s : other.preferences) {
            this.preferences.add(new Service(s));
        }
    }

    public String getId() {
        return id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Volunteer v = (Volunteer) o;
        return Objects.equals(id, v.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
