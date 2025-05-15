package org.example.server.logic;

import org.example.domain.*;

import java.util.ArrayList;
import java.util.List;

/**
 * ApplicationLogic handles the optimization process for assigning volunteers to services.
 * It ensures thread safety and data isolation for concurrent operations.
 */
public class ApplicationLogic {

    // Thread-safe lock object for guarding optimization
    private static final Object optimizationLock = new Object();

    /**
     * Main entry point for running the optimization algorithm.
     * This method is thread-safe and ensures data isolation by creating deep copies of input lists.
     *
     * @param volunteers List of volunteers to be assigned.
     * @param services   List of services to be assigned to.
     * @return List of assignments resulting from the optimization process.
     * @throws IllegalArgumentException if volunteers or services list is null or empty.
     */
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

        // Synchronize on the lock to ensure thread-safe optimization
        synchronized (optimizationLock) {
            GeneticAlgorithm ga = new GeneticAlgorithm(safeVolunteers, safeServices);
            return ga.optimize(); 
        }
    }

    /**
     * Creates a deep copy of the volunteers list to protect shared data from concurrent modifications.
     *
     * @param volunteers Original list of volunteers.
     * @return A deep copy of the volunteers list.
     */
    private List<Volunteer> deepCopyVolunteers(List<Volunteer> volunteers) {
        List<Volunteer> copy = new ArrayList<>();
        for (Volunteer v : volunteers) {
            copy.add(new Volunteer(v));
        }
        return copy;
    }

    /**
     * Creates a deep copy of the services list to protect shared data from concurrent modifications.
     *
     * @param services Original list of services.
     * @return A deep copy of the services list.
     */
    private List<Service> deepCopyServices(List<Service> services) {
        List<Service> copy = new ArrayList<>();
        for (Service s : services) {
            copy.add(new Service(s));
        }
        return copy;
    }
}
