package org.example.domain;

import java.util.*;

/**
 * Represents a volunteer who can be assigned to services.
 * Each volunteer has a name, a unique ID, and a list of service preferences.
 */
public class Volunteer {
    private final String name;
    private final String id;
    private final List<Service> preferences;

    /**
     * Constructs a Volunteer with the specified name, ID, and service preferences.
     *
     * @param name        The name of the volunteer.
     * @param id          The unique ID of the volunteer.
     * @param preferences The list of services the volunteer prefers, in order of preference.
     */
    public Volunteer(String name, String id, List<Service> preferences) {
        this.name = name;
        this.id = id;
        this.preferences = new ArrayList<>(preferences); // Defensive copy
    }

    /**
     * Copy constructor to create a new Volunteer instance based on an existing one.
     * The preferences list is deeply copied to ensure immutability.
     *
     * @param other The existing Volunteer instance to copy.
     */
    public Volunteer(Volunteer other) {
        this.name = other.name;
        this.id = other.id;
        this.preferences = new ArrayList<>();
        for (Service s : other.preferences) {
            this.preferences.add(new Service(s));
        }
    }

    /**
     * Gets the unique ID of the volunteer.
     *
     * @return The ID of the volunteer.
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the name of the volunteer.
     *
     * @return The name of the volunteer.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the list of services the volunteer prefers.
     *
     * @return The list of preferred services.
     */
    public List<Service> getPreferences() {
        return preferences;
    }

    /**
     * Returns a string representation of the volunteer, which is their name.
     *
     * @return The name of the volunteer.
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Checks if this volunteer is equal to another object.
     * Two volunteers are considered equal if they have the same ID.
     *
     * @param o The object to compare with.
     * @return True if the volunteers are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Volunteer v = (Volunteer) o;
        return Objects.equals(id, v.id);
    }

    /**
     * Computes the hash code for this volunteer based on their ID.
     *
     * @return The hash code of the volunteer.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
