package org.example;

import org.example.domain.*;
import org.example.server.logic.ApplicationLogic;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        // Step 1: Define services
        Service reception = new Service("Reception", 2);
        Service logistics = new Service("Logistics", 2);
        Service food = new Service("Food Service", 2);
        List<Service> allServices = List.of(reception, logistics, food);

        // Step 2: Initialize ApplicationLogic
        ApplicationLogic logic = new ApplicationLogic(allServices);

        // Step 3: Define volunteers with preferences
        logic.addVolunteer(new Volunteer("Alice", List.of(reception, food, logistics)));
        logic.addVolunteer(new Volunteer("Bob", List.of(logistics, food)));
        logic.addVolunteer(new Volunteer("Charlie", List.of(food, reception)));
        logic.addVolunteer(new Volunteer("Diana", List.of(logistics, reception)));
        logic.addVolunteer(new Volunteer("Ethan", List.of(food, logistics)));
        logic.addVolunteer(new Volunteer("Fiona", List.of(reception, logistics, food)));

        // Step 4: Run optimization
        List<Assignment> assignments = logic.runOptimization();

        // Step 5: Print results
        System.out.println("=== Final Assignments ===");
        for (Assignment a : assignments) {
            System.out.println(a.getVolunteer().getName() + " → " + a.getService().getName());
        }
    }
}
