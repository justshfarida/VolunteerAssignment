package org.example.server.logic;

import org.example.domain.*;

import java.util.*;

public class GeneticAlgorithm {

    private final List<Volunteer> volunteers;
    private final List<Service> services;

    private final int populationSize = 100;
    private final int generations = 500;
    private final double mutationRate = 0.1;

    public GeneticAlgorithm(List<Volunteer> volunteers, List<Service> services) {
        this.volunteers = volunteers;
        this.services = services;
    }

    public List<Assignment> optimize() {
        List<List<Assignment>> population = initializePopulation();
        List<Assignment> best = null;
        int bestCost = Integer.MAX_VALUE;

        for (int gen = 0; gen < generations; gen++) {
            List<List<Assignment>> newPopulation = new ArrayList<>();

            for (int i = 0; i < populationSize; i++) {
                List<Assignment> parent1 = select(population);
                List<Assignment> parent2 = select(population);
                List<Assignment> child = crossover(parent1, parent2);
                mutate(child);
                newPopulation.add(child);
            }

            population = newPopulation;

            List<Assignment> currentBest = getBest(population);
            int cost = calculateCost(currentBest);
            if (cost < bestCost) {
                bestCost = cost;
                best = currentBest;
            }
        }

        return best;
    }

    private List<List<Assignment>> initializePopulation() {
        List<List<Assignment>> population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            population.add(generateRandomAssignment());
        }
        return population;
    }

    private List<Assignment> generateRandomAssignment() {
        List<Assignment> assignment = new ArrayList<>();
        Map<Service, Integer> serviceCounts = new HashMap<>();

        for (Volunteer v : volunteers) {
            Service assigned = null;
            List<Service> prefs = v.getPreferences();

            for (Service s : prefs) {
                if (serviceCounts.getOrDefault(s, 0) < s.getCapacity()) {
                    assigned = s;
                    break;
                }
            }

            if (assigned == null) {
                for (Service s : services) {
                    if (serviceCounts.getOrDefault(s, 0) < s.getCapacity()) {
                        assigned = s;
                        break;
                    }
                }
            }

            if (assigned != null) {
                assignment.add(new Assignment(v, assigned));
                serviceCounts.put(assigned, serviceCounts.getOrDefault(assigned, 0) + 1);
            }
        }

        return assignment;
    }

    private int calculateCost(List<Assignment> assignment) {
        int totalCost = 0;

        for (Assignment a : assignment) {
            Volunteer v = a.getVolunteer();
            Service s = a.getService();
            int index = v.getPreferences().indexOf(s);

            if (index != -1) {
                totalCost += Math.pow(index, 2);
            } else {
                totalCost += 10 * Math.pow(2, 2);
            }
        }

        return totalCost;
    }

    private List<Assignment> select(List<List<Assignment>> population) {
        Random rand = new Random();
        List<Assignment> a = population.get(rand.nextInt(population.size()));
        List<Assignment> b = population.get(rand.nextInt(population.size()));

        return calculateCost(a) < calculateCost(b) ? a : b;
    }

    private List<Assignment> crossover(List<Assignment> p1, List<Assignment> p2) {
        List<Assignment> child = new ArrayList<>();
        Random rand = new Random();
        Map<Volunteer, Service> parent1Map = toMap(p1);
        Map<Volunteer, Service> parent2Map = toMap(p2);

        for (Volunteer v : volunteers) {
            Service chosen = rand.nextBoolean() ? parent1Map.get(v) : parent2Map.get(v);
            child.add(new Assignment(v, chosen));
        }

        return child;
    }

    private void mutate(List<Assignment> assignment) {
        Random rand = new Random();

        for (int i = 0; i < assignment.size(); i++) {
            Assignment a = assignment.get(i);
            Volunteer v = a.getVolunteer();

            if (rand.nextDouble() < mutationRate) {
                List<Service> prefs = v.getPreferences();
                if (!prefs.isEmpty()) {
                    Service newService = prefs.get(rand.nextInt(prefs.size()));
                    assignment.set(i, new Assignment(v, newService));
                }
            }
        }
    }

    private List<Assignment> getBest(List<List<Assignment>> population) {
        return population.stream()
                .min(Comparator.comparingInt(this::calculateCost))
                .orElse(null);
    }

    private Map<Volunteer, Service> toMap(List<Assignment> list) {
        Map<Volunteer, Service> map = new HashMap<>();
        for (Assignment a : list) {
            map.put(a.getVolunteer(), a.getService());
        }
        return map;
    }
}
