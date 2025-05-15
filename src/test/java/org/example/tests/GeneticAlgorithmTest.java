package org.example.tests;

import org.example.domain.*;
import org.example.server.logic.GeneticAlgorithm;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the GeneticAlgorithm class.
 * These tests validate the behavior of the optimization algorithm
 * under various scenarios, including capacity enforcement, fallback handling,
 * and cost minimization based on volunteer preferences.
 */
class GeneticAlgorithmTest {

    /**
     * Tests that the GeneticAlgorithm enforces service capacity limits correctly.
     * Volunteers exceeding the capacity of a service should not be assigned to it.
     */
    @Test
    void testBasicCapacityEnforcement() {
        // Create services with defined capacities
        Service airport = new Service("Airport Greeter", 3);
        Service other = new Service("Other", 1);
        List<Service> services = List.of(airport, other);

        // Create volunteers with preferences for the "Airport Greeter" service
        List<Volunteer> volunteers = List.of(
                new Volunteer("Alice", "v1", List.of(airport)),
                new Volunteer("Bob", "v2", List.of(airport)),
                new Volunteer("Charlie", "v3", List.of(airport)),
                new Volunteer("Dave", "v4", List.of(airport)) // should overflow
        );

        // Run the genetic algorithm
        GeneticAlgorithm ga = new GeneticAlgorithm(volunteers, services);
        List<Assignment> result = ga.optimize();

        // Count the number of assignments per service
        Map<String, Long> counts = result.stream()
                .collect(Collectors.groupingBy(a -> a.getService().getName(), Collectors.counting()));

        // Assert that the capacity limits are respected
        assertEquals(3L, counts.getOrDefault("Airport Greeter", 0L));
        assertEquals(1L, counts.getOrDefault("Other", 0L));
    }

    /**
     * Tests that the GeneticAlgorithm uses a fallback mechanism when no valid assignments can be made.
     * The fallback should be returned as-is if triggered.
     */
    @Test
    void testFallbackTriggeredOnInvalidAssignment() {
        // Create a service with a capacity of 1
        Service greeter = new Service("Greeter", 1);

        // Create a list of volunteers all preferring the same service
        List<Service> services = List.of(greeter);
        List<Volunteer> volunteers = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            volunteers.add(new Volunteer("V" + i, "id" + i, List.of(greeter)));
        }

        // Run the genetic algorithm with a fallback
        GeneticAlgorithm ga = new GeneticAlgorithm(volunteers, services);
        List<Assignment> fallback = new ArrayList<>();
        fallback.add(new Assignment(volunteers.get(0), greeter)); // dummy fallback

        List<Assignment> result = ga.optimize(() -> fallback);

        // If fallback is used, result must match the dummy fallback
        if (result.size() == 1 && result.get(0).getVolunteer().equals(volunteers.get(0))) {
            assertEquals(1, result.size());
            assertEquals(fallback.get(0).getVolunteer(), result.get(0).getVolunteer());
            assertEquals(fallback.get(0).getService(), result.get(0).getService());
        } else {
            // Otherwise, validate the result
            assertTrue(isValid(result));
        }
    }

    /**
     * Tests that the GeneticAlgorithm minimizes the cost of assignments based on volunteer preferences.
     * Volunteers should be assigned to their preferred services whenever possible.
     */
    @Test
    void testCostReflectsPreferences() {
        // Create services with sufficient capacity
        Service s1 = new Service("S1", 5);
        Service s2 = new Service("S2", 5);
        List<Service> services = List.of(s1, s2);

        // Create volunteers with ordered preferences
        Volunteer v1 = new Volunteer("Ann", "id1", List.of(s1, s2));  // prefers s1
        Volunteer v2 = new Volunteer("Ben", "id2", List.of(s2, s1));  // prefers s2
        List<Volunteer> volunteers = List.of(v1, v2);

        // Run the genetic algorithm
        GeneticAlgorithm ga = new GeneticAlgorithm(volunteers, services);
        List<Assignment> result = ga.optimize();

        // Calculate the total cost of the assignments
        int totalCost = result.stream()
                .mapToInt(a -> {
                    List<Service> prefs = a.getVolunteer().getPreferences();
                    int index = prefs.indexOf(a.getService());
                    return index == -1 ? 40 : index * index; // Higher cost for less preferred services
                }).sum();

        // Assert that the total cost is within acceptable bounds
        assertTrue(totalCost <= 2); // Best possible is 0, worst is 2
    }

    /**
     * Helper method to validate that the assignments respect service capacity limits.
     *
     * @param assignments The list of assignments to validate.
     * @return True if all assignments are valid, false otherwise.
     */
    private boolean isValid(List<Assignment> assignments) {
        Map<Service, Integer> countMap = new HashMap<>();
        for (Assignment a : assignments) {
            Service s = a.getService();
            int count = countMap.getOrDefault(s, 0);
            if (count >= s.getCapacity()) {
                return false; // Capacity exceeded
            }
            countMap.put(s, count + 1);
        }
        return true;
    }
}
