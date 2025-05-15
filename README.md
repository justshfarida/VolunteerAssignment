# Volunteer Matching System

This project is a Java-based desktop and server application that matches volunteers to community services based on their personal preferences. The backend uses a genetic algorithm to optimize these assignments based on availability and rankings.

## Project Overview

The system consists of:
- A **Swing-based UI client** for volunteers to enter their preferences and view their assignments.
- A **multithreaded HTTP server** to store submissions and perform optimization in memory.
- A **genetic algorithm** that computes near-optimal matches considering service capacity constraints.

## Directory Structure

```
src/
├── main/
│   └── java/
│       ├── org.example.client/         # Client-side (UI + networking)
│       │   └── api/                    # HTTP client logic
│       │   └── ui/                     # Swing-based UI
│       ├── org.example.server/        # Server-side
│       │   └── network/               # HTTP endpoints
│       │   └── logic/                 # Optimization engine
│       └── org.example.domain/        # Shared domain classes (Volunteer, Service, Assignment)
```

## Requirements

- Java 21 or higher
- Maven 3.6+
- No database required — all data is stored in-memory

## How to Run

### 1. Compile the project

```bash
mvn clean compile
```

### 2. Run the server (in a terminal)

```bash
mvn exec:java@run-server
```

### 3. Run the client (in a separate terminal)

```bash
mvn exec:java@run-client
```

> You can open multiple terminals and repeat step 3 to simulate multiple volunteers.

## How to Use the Client

1. Launch the client.
2. Go to **My Preferences**.
3. Enter your name and select 5 unique preferences from the dropdowns.
4. Click **Submit Preferences**.
5. Switch to the **My Assignments** tab.
6. Press **Run Optimization**.
7. Your assignment will appear shortly.

## Sample Preferences

Example of 5 unique preferences:
- Soup Kitchen
- Beach Cleanup
- Animal Shelter
- Community Garden
- Youth Mentor

## Optimization Details

The server runs a genetic algorithm that:
- Tries to assign each volunteer one of their top preferences.
- Respects the capacity constraints of each service.
- Minimizes total dissatisfaction by assigning higher weight to top-ranked choices.

## Notes

- Every time the client starts, a new unique volunteer ID is generated automatically using `System.nanoTime()`.
- Assignments are refreshed automatically via polling.
- All state is reset when the server is restarted.
