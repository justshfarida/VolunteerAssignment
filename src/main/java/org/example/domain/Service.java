package org.example.domain;

import java.util.Objects;

/**
 * Represents a service that volunteers can be assigned to.
 * Each service has a name and a capacity indicating the maximum number of volunteers it can accommodate.
 */
public class Service {
    private final String name;
    private final int capacity;

    /**
     * Constructs a Service with the specified name and capacity.
     *
     * @param name     The name of the service.
     * @param capacity The maximum number of volunteers the service can accommodate.
     */
    public Service(String name, int capacity) {
        this.name = name;
        this.capacity = capacity;
    }

    /**
     * Copy constructor to create a new Service instance based on an existing one.
     *
     * @param other The existing Service instance to copy.
     */
    public Service(Service other) {
        this.name = other.name;
        this.capacity = other.capacity;
    }

    /**
     * Gets the name of the service.
     *
     * @return The name of the service.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the capacity of the service.
     *
     * @return The maximum number of volunteers the service can accommodate.
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Checks if this service is equal to another object.
     * Two services are considered equal if they have the same name.
     *
     * @param o The object to compare with.
     * @return True if the services are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Service)) return false;
        Service service = (Service) o;
        return Objects.equals(name, service.name);
    }

    /**
     * Computes the hash code for this service based on its name.
     *
     * @return The hash code of the service.
     */
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    /**
     * Returns a string representation of the service, which is its name.
     *
     * @return The name of the service.
     */
    @Override
    public String toString() {
        return name;
    }
}
