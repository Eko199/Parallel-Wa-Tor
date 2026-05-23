void main() {
    World world = new World(1500, 1500);

    long startTime = System.nanoTime();
    world.runSimulation(1000, 4);
    long endTime = System.nanoTime();

    //System.out.println(world);
    System.out.printf("Parallel Execution: %.0f ms\n", (endTime - startTime) / 1000000.0);
}
