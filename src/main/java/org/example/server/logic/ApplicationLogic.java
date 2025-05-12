package org.example.server.logic;

import org.example.domain.*;

import java.util.*;

public class ApplicationLogic {

    private List<Volunteer> volunteers;
    private List<Service> services;

    public ApplicationLogic(List<Service> services) {
        this.volunteers = new ArrayList<>();
        this.services = services;
    }

    public void addVolunteer(Volunteer v) {
        volunteers.add(v);
    }

    public List<Assignment> runOptimization() {
        if (volunteers.isEmpty() || services.isEmpty()) {
            throw new IllegalStateException("Volunteers or services list is empty.");
        }

        GeneticAlgorithm ga = new GeneticAlgorithm(volunteers, services);
        return ga.optimize();
    }

    public void reset() {
        volunteers.clear();
    }
}
