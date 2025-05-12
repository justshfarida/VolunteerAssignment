package org.example.domain;

public class Preference {
    private final Service service;
    private final int rank; // 1 = top preference, 5 = lowest

    public Preference(Service service, int rank) {
        this.service = service;
        this.rank = rank;
    }

    public Service getService() {
        return service;
    }

    public int getRank() {
        return rank;
    }
}
