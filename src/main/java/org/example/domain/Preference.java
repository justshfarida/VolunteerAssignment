package org.example.domain;

/**
 * Represents a volunteer's preference for a specific service.
 * Each preference is associated with a rank, where a lower rank indicates a higher preference.
 */
public class Preference {
    private final Service service;
    private final int rank; // 1 = top preference, higher numbers = lower preference

    /**
     * Constructs a Preference with the specified service and rank.
     *
     * @param service The service associated with this preference.
     * @param rank    The rank of the preference (1 = highest, higher numbers = lower preference).
     */
    public Preference(Service service, int rank) {
        this.service = service;
        this.rank = rank;
    }

    /**
     * Gets the service associated with this preference.
     *
     * @return The service.
     */
    public Service getService() {
        return service;
    }

    /**
     * Gets the rank of this preference.
     *
     * @return The rank (1 = highest preference).
     */
    public int getRank() {
        return rank;
    }
}
