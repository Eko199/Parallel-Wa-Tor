void main() {
    Scanner input = new Scanner(System.in);
    System.out.print("Enter world width and height: ");

    int width = input.nextInt();
    int height = input.nextInt();

    while (width <= 0) {
        System.out.print("Width must be positive: ");
        width = input.nextInt();
    }

    while (height < 10) {
        System.out.print("Height must be at least 10: ");
        height = input.nextInt();
    }

    System.out.print("Enter chronons count: ");
    int chronons = input.nextInt();

    while (chronons <= 0) {
        System.out.print("Chronons count must be positive: ");
        chronons = input.nextInt();
    }

    System.out.print("Enter threads count: ");
    int threads = input.nextInt();

    while (threads <= 0) {
        System.out.print("Threads count must be positive: ");
        threads = input.nextInt();
    }

    World world = new World(width, height);

    long startTime = System.nanoTime();
    world.runSimulation(chronons, threads);
    long endTime = System.nanoTime();

    //System.out.println(world);
    System.out.printf("Parallel Execution: %.0f ms\n", (endTime - startTime) / 1000000.0);
}
