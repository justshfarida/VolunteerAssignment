package org.example.domain;

/**
 * Represents an assignment of a volunteer to a service.
 * This class encapsulates the relationship between a volunteer and a service.
 */
public class Assignment {
    private final Volunteer volunteer;
    private final Service service;

    /**
     * Constructs an Assignment with the specified volunteer and service.
     *
     * @param volunteer The volunteer being assigned.
     * @param service   The service to which the volunteer is assigned.
     */
    public Assignment(Volunteer volunteer, Service service) {
        this.volunteer = volunteer;
        this.service = service;
    }

    /**
     * Gets the volunteer associated with this assignment.
     *
     * @return The volunteer.
     */
    public Volunteer getVolunteer() {
        return volunteer;
    }

    /**
     * Gets the service associated with this assignment.
     *
     * @return The service.
     */
    public Service getService() {
        return service;
    }

    /**
     * Returns a string representation of the assignment in the format:
     * "VolunteerName → ServiceName".
     *
     * @return A string representation of the assignment.
     */
    @Override
    public String toString() {
        return volunteer.getName() + " → " + service.getName();
    }
}
