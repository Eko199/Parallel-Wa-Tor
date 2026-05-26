import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        int width, height, chronons, threads;

        if (args.length == 4) {
            try {
                width = Integer.parseInt(args[1]);
                height = Integer.parseInt(args[2]);
                chronons = Integer.parseInt(args[3]);
                threads = Integer.parseInt(args[0]);

                if (width <= 0 || height < 10 || chronons <= 0 || threads <= 0) {
                    throw new IllegalArgumentException("Invalid parameters!");
                }
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid parameters!");
                return;
            }
        } else {
            Scanner input = new Scanner(System.in);
            System.out.print("Enter world width and height: ");

            width = input.nextInt();
            height = input.nextInt();

            while (width <= 0) {
                System.out.print("Width must be positive: ");
                width = input.nextInt();
            }

            while (height < 10) {
                System.out.print("Height must be at least 10: ");
                height = input.nextInt();
            }

            System.out.print("Enter chronons count: ");
            chronons = input.nextInt();

            while (chronons <= 0) {
                System.out.print("Chronons count must be positive: ");
                chronons = input.nextInt();
            }

            System.out.print("Enter threads count: ");
            threads = input.nextInt();

            while (threads <= 0) {
                System.out.print("Threads count must be positive: ");
                threads = input.nextInt();
            }
        }

        World world = new World(width, height);

        long startTime = System.nanoTime();
        world.runSimulation(chronons, threads);
        long endTime = System.nanoTime();

        //System.out.println(world);
        System.out.printf("Parallel Execution: %.0f ms\n", (endTime - startTime) / 1000000.0);
    }
}