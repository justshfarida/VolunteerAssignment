package org.example.server.logic;

import org.example.domain.*;
import java.util.stream.Collectors;
import java.util.*;
import java.util.function.Supplier;

/**
 * GeneticAlgorithm is a class that implements a genetic algorithm to optimize
 * the assignment of volunteers to services based on their preferences and service capacities.
 */
public class GeneticAlgorithm {

    private final List<Volunteer> volunteers;
    private final List<Service> services;

    private final int populationSize = 100; // Number of individuals in the population
    private final int generations = 500;   // Maximum number of generations
    //private final double mutationRate = 0.02; // Mutation rate (currently commented out)

    /**
     * Constructs a GeneticAlgorithm instance with the given volunteers and services.
     * Ensures that all volunteers reference canonical service objects.
     *
     * @param volunteers List of volunteers with their preferences.
     * @param services   List of available services with their capacities.
     */
    public GeneticAlgorithm(List<Volunteer> volunteers, List<Service> services) {
        Map<String, Service> serviceMap = new HashMap<>();
        for (Service s : services) {
            serviceMap.put(s.getName(), s);
        }
    
        this.services = new ArrayList<>(services);
        this.volunteers = new ArrayList<>();
    
        for (Volunteer v : volunteers) {
            List<Service> normalizedPrefs = new ArrayList<>();
            for (Service s : v.getPreferences()) {
                Service ref = serviceMap.get(s.getName());
                if (ref != null) normalizedPrefs.add(ref);
            }
            this.volunteers.add(new Volunteer(v.getName(), v.getId(), normalizedPrefs));
        }
    }

    /**
     * Optimizes the assignment of volunteers to services using the genetic algorithm.
     *
     * @return A list of assignments representing the optimized solution.
     */
    public List<Assignment> optimize() {
        return optimize(this::generateRandomAssignment);
    }

    /**
     * Optimizes the assignment of volunteers to services using the genetic algorithm.
     * Falls back to a provided fallback assignment generator if the final assignment is invalid.
     *
     * @param fallback A supplier that generates a fallback assignment.
     * @return A list of assignments representing the optimized solution.
     */
    public List<Assignment> optimize(Supplier<List<Assignment>> fallback) {
        List<List<Assignment>> population = initializePopulation();
        List<Assignment> best = getBest(population);
        int bestCost = calculateCost(best);
    
        int stagnation = 0;
    
        for (int gen = 0; gen < generations; gen++) {
            List<List<Assignment>> newPopulation = new ArrayList<>();
            newPopulation.add(best); // Elitism: carry forward the best individual
    
            for (int i = 1; i < populationSize; i++) {
                List<Assignment> parent1 = select(population);
                List<Assignment> parent2 = select(population);
                List<Assignment> child = crossover(parent1, parent2);
                //mutate(child); // Mutation step (currently commented out)
                newPopulation.add(child);
            }
    
            population = newPopulation;
            List<Assignment> currentBest = getBest(population);
            int cost = calculateCost(currentBest);
    
            System.out.printf("Gen %d: best cost = %d%n", gen, cost);
    
            if (cost < bestCost) {
                bestCost = cost;
                best = currentBest;
                stagnation = 0;
            } else {
                stagnation++;
            }
    
            if (stagnation >= 50) {
                System.out.println("Stopping early due to stagnation.");
                break;
            }
        }
    
        if (!isValidAssignment(best)) {
            System.err.println("Final assignment invalid. Using fallback.");
            return fallback.get();
        }
    
        return best;
    }

    /**
     * Initializes the population with random assignments.
     *
     * @return A list of randomly generated assignments representing the initial population.
     */
    private List<List<Assignment>> initializePopulation() {
        List<List<Assignment>> population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            population.add(generateRandomAssignment());
        }
        return population;
    }

    /**
     * Generates a random assignment of volunteers to services.
     *
     * @return A list of assignments representing a random solution.
     */
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

    /**
     * Calculates the cost of a given assignment based on volunteer preferences.
     *
     * @param assignment A list of assignments to evaluate.
     * @return The total cost of the assignment.
     */
    private int calculateCost(List<Assignment> assignment) {
        int totalCost = 0;
    
        for (Assignment a : assignment) {
            Volunteer v = a.getVolunteer();
            Service s = a.getService();
            List<Service> prefs = v.getPreferences();
            int index = prefs.indexOf(s);
    
            if (index != -1) {
                totalCost += (int) Math.pow(index, 2);
            } else {
                totalCost += 40;  // 10 * 2^2
            }
        }
    
        return totalCost;
    }

    /**
     * Selects a parent from the population using a selection strategy.
     * The selection favors individuals with lower costs.
     *
     * @param population The current population of assignments.
     * @return A selected parent assignment.
     */
    private List<Assignment> select(List<List<Assignment>> population) {
        List<List<Assignment>> sorted = population.stream()
            .sorted(Comparator.comparingInt(this::calculateCost))
            .collect(Collectors.toList());
    
        int eliteSize = Math.max(1, populationSize / 5); // top 20%
        Random rand = new Random();
        return sorted.get(rand.nextInt(eliteSize)); // pick from best few
    }

    /**
     * Performs crossover between two parent assignments to produce a child assignment.
     *
     * @param p1 The first parent assignment.
     * @param p2 The second parent assignment.
     * @return A child assignment generated from the parents.
     */
    private List<Assignment> crossover(List<Assignment> p1, List<Assignment> p2) {
        Random rand = new Random();
        Map<Volunteer, Service> parent1Map = toMap(p1);
        Map<Volunteer, Service> parent2Map = toMap(p2);
    
        List<Assignment> child = new ArrayList<>();
        Map<Service, Integer> serviceCounts = new HashMap<>();
    
        for (Volunteer v : volunteers) {
            Service s1 = parent1Map.get(v);
            Service s2 = parent2Map.get(v);
    
            Service chosen = rand.nextBoolean() ? s1 : s2;
            if (chosen != null && serviceCounts.getOrDefault(chosen, 0) < chosen.getCapacity()) {
                child.add(new Assignment(v, chosen));
                serviceCounts.put(chosen, serviceCounts.getOrDefault(chosen, 0) + 1);
            } else {
                // fallback to any valid service
                for (Service fallback : services) {
                    if (serviceCounts.getOrDefault(fallback, 0) < fallback.getCapacity()) {
                        child.add(new Assignment(v, fallback));
                        serviceCounts.put(fallback, serviceCounts.getOrDefault(fallback, 0) + 1);
                        break;
                    }
                }
            }
        }
    
        return child;
    }

    /*
     * Mutates an assignment by randomly changing some of its assignments.
     * Currently commented out.
     */
    // private void mutate(List<Assignment> assignment) {
    //     Random rand = new Random();
    //     Map<Service, Integer> serviceCounts = new HashMap<>();
    
    //     // Initialize service counts
    //     for (Assignment a : assignment) {
    //         Service s = a.getService();
    //         serviceCounts.put(s, serviceCounts.getOrDefault(s, 0) + 1);
    //     }
    
    //     for (int i = 0; i < assignment.size(); i++) {
    //         Assignment a = assignment.get(i);
    //         Volunteer v = a.getVolunteer();
    
    //         if (rand.nextDouble() < mutationRate) {
    //             List<Service> prefs = v.getPreferences();
    //             Collections.shuffle(prefs); // randomize choices
    
    //             for (Service newService : prefs) {
    //                 if (serviceCounts.getOrDefault(newService, 0) < newService.getCapacity()) {
    //                     Service oldService = a.getService();
    //                     serviceCounts.put(oldService, serviceCounts.get(oldService) - 1);
    //                     assignment.set(i, new Assignment(v, newService));
    //                     serviceCounts.put(newService, serviceCounts.getOrDefault(newService, 0) + 1);
    //                     break;
    //                 }
    //             }
    //         }
    //     }
    // }

    /**
     * Validates an assignment to ensure no service exceeds its capacity.
     *
     * @param assignments The assignment to validate.
     * @return True if the assignment is valid, false otherwise.
     */
    private boolean isValidAssignment(List<Assignment> assignments) {
        Map<Service, Integer> countMap = new HashMap<>();
        for (Assignment a : assignments) {
            Service s = a.getService();
            int count = countMap.getOrDefault(s, 0);
            if (count >= s.getCapacity()) {
                System.err.printf("Overcapacity for %s: %d assigned (limit %d)%n", 
                    s.getName(), count + 1, s.getCapacity());
                return false;
            }
            countMap.put(s, count + 1);
        }
        return true;
    }

    /**
     * Retrieves the best assignment from the population based on cost.
     *
     * @param population The current population of assignments.
     * @return The best assignment in the population.
     */
    private List<Assignment> getBest(List<List<Assignment>> population) {
        return population.stream()
                .min(Comparator.comparingInt(this::calculateCost))
                .orElse(null);
    }

    /**
     * Converts a list of assignments to a map for easier lookup.
     *
     * @param list The list of assignments to convert.
     * @return A map of volunteers to their assigned services.
     */
    private Map<Volunteer, Service> toMap(List<Assignment> list) {
        Map<Volunteer, Service> map = new HashMap<>();
        for (Assignment a : list) {
            map.put(a.getVolunteer(), a.getService());
        }
        return map;
    }
}
