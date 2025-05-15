package org.example.server.logic;

import org.example.domain.*;

import java.util.ArrayList;
import java.util.List;

public class ApplicationLogic {

    // Thread-safe lock object for guarding optimization
    private static final Object optimizationLock = new Object();

    // Main entry point: safe to call from any thread
    public List<Assignment> runOptimization(List<Volunteer> volunteers, List<Service> services) {
        if (volunteers == null || volunteers.isEmpty()) {
            throw new IllegalArgumentException("Volunteer list is null or empty.");
        }

        if (services == null || services.isEmpty()) {
            throw new IllegalArgumentException("Service list is null or empty.");
        }

        // Deep copy to isolate data per thread
        List<Volunteer> safeVolunteers = deepCopyVolunteers(volunteers);
        List<Service> safeServices = deepCopyServices(services);

        synchronized (optimizationLock) {
            GeneticAlgorithm ga = new GeneticAlgorithm(safeVolunteers, safeServices);
            return ga.optimize(); // this is thread-safe now
        }
    }

    // Clone the volunteers list to protect shared data
    private List<Volunteer> deepCopyVolunteers(List<Volunteer> volunteers) {
        List<Volunteer> copy = new ArrayList<>();
        for (Volunteer v : volunteers) {
            copy.add(new Volunteer(v)); // Uses your copy constructor
        }
        return copy;
    }

    // Clone the services list
    private List<Service> deepCopyServices(List<Service> services) {
        List<Service> copy = new ArrayList<>();
        for (Service s : services) {
            copy.add(new Service(s)); // Uses your copy constructor
        }
        return copy;
    }
}
