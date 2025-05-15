package org.example.domain;

import java.util.Objects;

public class Service {
    private final String name;
    private final int capacity;

    public Service(String name, int capacity) {
        this.name = name;
        this.capacity = capacity;
    }

    // Copy constructor
    public Service(Service other) {
        this.name = other.name;
        this.capacity = other.capacity;
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Service)) return false;
        Service service = (Service) o;
        return Objects.equals(name, service.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
