package org.example.domain;

public class Assignment {
    private final Volunteer volunteer;
    private final Service service;

    public Assignment(Volunteer volunteer, Service service) {
        this.volunteer = volunteer;
        this.service = service;
    }

    public Volunteer getVolunteer() {
        return volunteer;
    }

    public Service getService() {
        return service;
    }

    @Override
    public String toString() {
        return volunteer.getName() + " → " + service.getName();
    }
}
