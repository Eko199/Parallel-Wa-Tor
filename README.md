# Parallel Implementation of the Wa-Tor Simulation

This project is a high-performance parallel implementation of the **Wa-Tor** (Water-Torus) cellular automaton, a predator-prey model originally introduced by Alexander Dewdney. It is developed in **Java** and optimized for multi-core systems using shared memory.

## Simulation Overview
The Wa-Tor world is a discrete grid shaped like a **torus**, where the edges wrap around (the last row/column is followed by the first). The simulation proceeds in discrete time steps called **chronons**.

### Inhabitant Rules:
*   **Fish:** Move to an adjacent empty cell. If a fish reaches a certain reproduction age, it leaves an offspring in its previous position upon moving.
*   **Sharks:** Hunt for adjacent fish to gain energy. If no fish are available, they move to an empty cell. Sharks lose energy each chronon and die if it reaches zero. Like fish, they reproduce upon reaching a specific age.

## Parallel Architecture
The implementation utilizes a **local-synchronous model** to maximize speedup on multi-core processors.

*   **Data Decomposition:** The matrix is divided into horizontal strips (rows), where each worker thread processes exactly one strip (coarse granularity).
*   **Synchronization:** 
    *   **CyclicBarrier:** Ensures all threads complete the current chronon before any proceed to the next, maintaining time consistency.
    *   **ReentrantLocks:** Protect shared memory at the boundaries between strips to prevent race conditions when entities move between regions.
*   **Static Balancing:** Tasks are distributed fixedly at startup, which is optimal for the relatively homogeneous load of the Wa-Tor world.

## Project Structure
The application consists of the following core classes:
*   `Main`: Application entry point for user configuration.
*   `World`: Manages the simulation logic, grid matrix, and thread orchestration.
*   `WaTorWorker`: Implements the `Runnable` interface for parallel processing of grid strips.
*   `Entity` / `EntityType`: Defines the grid cells (Empty, Fish, or Shark) and their properties.
*   `WaTorVisualizer`: Handles the generation of visual output frames.

## Getting Started

### Compilation
Compile the source files using `javac`:
```bash
javac *.java
```

### Running the Application
Run the main class. You will be prompted to enter the grid size, number of chronons, and thread count:
```bash
java Main
```

### Running Tests
To replicate the performance benchmarks provided in the documentation, you can use these standard test configurations:
*   **Scenario A:** 1500x1500x500 chronons
*   **Scenario B:** 2000x2000x1000 chronons

### Visualization and GIF Generation
If visualization is enabled, the simulation exports state frames every 10 chronons as `.png` files in the `/frames` folder.
*   **Water:** Light Blue
*   **Fish:** Green
*   **Shark:** Red

To generate an **animated GIF** from these frames at 10 fps, use **FFmpeg**:
```bash
ffmpeg -framerate 10 -i frames/frame_%d.png -vf "scale=iw:-1" simulation_output.gif
```