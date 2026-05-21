import java.util.ArrayList;
import java.util.Random;
import java.util.List;

public class World {
    private static final double FISH_DENSITY = 0.30;
    private static final double SHARK_DENSITY = 0.10;
    private static final int FISH_BREED_TIME = 3;
    private static final int SHARK_BREED_TIME = 10;
    private static final int SHARK_ENERGY_GAIN = 3;

    private final int width;
    private final int height;
    private final Entity[][] grid;
    private final Random random;
    private int currentChronon = 0;

    public World(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new Entity[height][width];
        this.random = new Random();

        initializeWorld();
    }

    private void initializeWorld() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double chance = random.nextDouble();

                if (chance < SHARK_DENSITY) {
                    grid[y][x] = new Entity(Entity.EntityType.SHARK);
                } else if (chance < SHARK_DENSITY + FISH_DENSITY) {
                    grid[y][x] = new Entity(Entity.EntityType.FISH);
                } else {
                    grid[y][x] = new Entity(Entity.EntityType.EMPTY);
                }
            }
        }
    }

    private int getWrapX(int x) {
        return (x + width) % width;
    }

    private int getWrapY(int y) {
        return (y + height) % height;
    }

    private List<Point> getNeighbors(int x, int y, Entity.EntityType targetType) {
        List<Point> neighbors = new ArrayList<>(4);

        int[] dx = {0, 0, -1, 1};
        int[] dy = {-1, 1, 0, 0};

        for (int i = 0; i < 4; i++) {
            int nx = getWrapX(x + dx[i]);
            int ny = getWrapY(y + dy[i]);

            if (grid[ny][nx].getType() == targetType) {
                neighbors.add(new Point(nx, ny));
            }
        }

        return neighbors;
    }

    private void processFish(int x, int y, Entity fish) {
        List<Point> emptySpaces = getNeighbors(x, y, Entity.EntityType.EMPTY);

        if (!emptySpaces.isEmpty()) {
            Point newPos = emptySpaces.get(random.nextInt(emptySpaces.size()));
            grid[newPos.y][newPos.x] = fish;

            if (fish.getAge() >= FISH_BREED_TIME) {
                Entity newborn = new Entity(Entity.EntityType.FISH);
                newborn.setLastChronon(currentChronon);
                grid[y][x] = newborn;
                fish.setAge(0);
            } else {
                grid[y][x] = new Entity(Entity.EntityType.EMPTY);
            }
        }
    }

    private void processShark(int x, int y, Entity shark) {
        shark.setEnergy(shark.getEnergy() - 1);

        if (shark.getEnergy() <= 0) {
            grid[y][x] = new Entity(Entity.EntityType.EMPTY);
            return;
        }

        List<Point> food = getNeighbors(x, y, Entity.EntityType.FISH);
        Point newPos = null;

        if (!food.isEmpty()) {
            newPos = food.get(random.nextInt(food.size()));
            shark.setEnergy(shark.getEnergy() + SHARK_ENERGY_GAIN);
        } else {
            List<Point> emptySpaces = getNeighbors(x, y, Entity.EntityType.EMPTY);

            if (!emptySpaces.isEmpty()) {
                newPos = emptySpaces.get(random.nextInt(emptySpaces.size()));
            }
        }

        if (newPos != null) {
            grid[newPos.y][newPos.x] = shark;

            if (shark.getAge() >= SHARK_BREED_TIME) {
                Entity newborn = new Entity(Entity.EntityType.SHARK);
                newborn.setLastChronon(currentChronon);
                grid[y][x] = newborn;
                shark.setAge(0);
            } else {
                grid[y][x] = new Entity(Entity.EntityType.EMPTY);
            }
        }
    }

    public Entity getEntityAt(int x, int y) {
        return grid[getWrapY(y)][getWrapX(x)];
    }

    public void nextChronon() {
        ++currentChronon;

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                Entity current = grid[y][x];

                if (current.getType() == Entity.EntityType.EMPTY || current.getLastChronon() == currentChronon) {
                    continue;
                }

                current.setLastChronon(currentChronon);
                current.setAge(current.getAge() + 1);

                if (current.getType() == Entity.EntityType.FISH) {
                    processFish(x, y, current);
                } else if (current.getType() == Entity.EntityType.SHARK) {
                    processShark(x, y, current);
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Chronon: ").append(currentChronon).append("\n");

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Entity current = grid[y][x];

                switch (current.getType()) {
                    case EMPTY -> sb.append(". ");
                    case FISH  -> sb.append("F ");
                    case SHARK -> sb.append("S ");
                }
            }
            sb.append("\n"); // Move to the next row
        }

        return sb.toString();
    }
}