package org.example.tests;

import org.example.domain.*;
import org.example.server.logic.GeneticAlgorithm;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;


import static org.junit.jupiter.api.Assertions.*;

class GeneticAlgorithmTest {

    @Test
    void testBasicCapacityEnforcement() {
        Service airport = new Service("Airport Greeter", 3);
        Service other = new Service("Other", 1);
        List<Service> services = List.of(airport, other);

        List<Volunteer> volunteers = List.of(
                new Volunteer("Alice", "v1", List.of(airport)),
                new Volunteer("Bob", "v2", List.of(airport)),
                new Volunteer("Charlie", "v3", List.of(airport)),
                new Volunteer("Dave", "v4", List.of(airport)) // should overflow
        );

        GeneticAlgorithm ga = new GeneticAlgorithm(volunteers, services);
        List<Assignment> result = ga.optimize();

        Map<String, Long> counts = result.stream()
                .collect(Collectors.groupingBy(a -> a.getService().getName(), Collectors.counting()));

        assertEquals(3L, counts.getOrDefault("Airport Greeter", 0L));
        assertEquals(1L, counts.getOrDefault("Other", 0L));
    }

    @Test
    void testFallbackTriggeredOnInvalidAssignment() {
        Service greeter = new Service("Greeter", 1);

        List<Service> services = List.of(greeter);
        List<Volunteer> volunteers = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            volunteers.add(new Volunteer("V" + i, "id" + i, List.of(greeter)));
        }

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
            assertTrue(isValid(result));
        }
    }

    @Test
    void testCostReflectsPreferences() {
        Service s1 = new Service("S1", 5);
        Service s2 = new Service("S2", 5);
        List<Service> services = List.of(s1, s2);

        Volunteer v1 = new Volunteer("Ann", "id1", List.of(s1, s2));  // prefers s1
        Volunteer v2 = new Volunteer("Ben", "id2", List.of(s2, s1));  // prefers s2

        List<Volunteer> volunteers = List.of(v1, v2);

        GeneticAlgorithm ga = new GeneticAlgorithm(volunteers, services);
        List<Assignment> result = ga.optimize();

        int totalCost = result.stream()
                .mapToInt(a -> {
                    List<Service> prefs = a.getVolunteer().getPreferences();
                    int index = prefs.indexOf(a.getService());
                    return index == -1 ? 40 : index * index;
                }).sum();

        assertTrue(totalCost <= 2); // Best possible is 0, worst is 2
    }

    private boolean isValid(List<Assignment> assignments) {
        Map<Service, Integer> countMap = new HashMap<>();
        for (Assignment a : assignments) {
            Service s = a.getService();
            int count = countMap.getOrDefault(s, 0);
            if (count >= s.getCapacity()) {
                return false;
            }
            countMap.put(s, count + 1);
        }
        return true;
    }
}
