import java.util.ArrayList;
import java.util.Random;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class World {
    private static final double FISH_DENSITY = 0.30;
    private static final double SHARK_DENSITY = 0.10;
    private static final int FISH_BREED_TIME = 3;
    private static final int SHARK_BREED_TIME = 10;
    private static final int SHARK_ENERGY_GAIN = 3;

    private static final int[] DX = { 0, 0, -1, 1 };
    private static final int[] DY = { -1, 1, 0, 0 };

    private final int width;
    private final int height;
    private final Entity[][] grid;
    private final Random random;
    private int currentChronon = 0;

    private final List<Lock> locks = new ArrayList<>();
    private CyclicBarrier barrier;

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

    private int getNeighbors(int x, int y, int[] arrX, int[] arrY, Entity.EntityType targetType) {
        int count = 0;

        for (int i = 0; i < 4; i++) {
            int nx = getWrapX(x + DX[i]);
            int ny = getWrapY(y + DY[i]);

            if (grid[ny][nx].getType() == targetType) {
                arrX[count] = nx;
                arrY[count] = ny;
                ++count;
            }
        }

        return count;
    }

    private void processFish(int x, int y, Entity fish, Random localRandom, int[] tempX, int[] tempY) {
        int emptyCount = getNeighbors(x, y, tempX, tempY, Entity.EntityType.EMPTY);

        if (emptyCount > 0) {
            int emptyId = localRandom.nextInt(emptyCount);
            Entity oldSpace = grid[tempY[emptyId]][tempX[emptyId]];

            grid[tempY[emptyId]][tempX[emptyId]] = fish;
            grid[y][x] = oldSpace;

            if (fish.getAge() >= FISH_BREED_TIME) {
                oldSpace.becomeFish();
                oldSpace.setLastChronon(currentChronon);
                fish.setAge(0);
            }
        }
    }

    private void processShark(int x, int y, Entity shark, Random localRandom, int[] tempX, int[] tempY) {
        shark.setEnergy(shark.getEnergy() - 1);

        if (shark.getEnergy() <= 0) {
            shark.becomeEmpty();
            return;
        }

        int foodCount = getNeighbors(x, y, tempX, tempY, Entity.EntityType.FISH);
        int newX = -1, newY = -1;

        if (foodCount > 0) {
            int foodId = localRandom.nextInt(foodCount);
            newX = tempX[foodId];
            newY = tempY[foodId];

            shark.setEnergy(shark.getEnergy() + SHARK_ENERGY_GAIN);
        } else {
            int emptyCount = getNeighbors(x, y, tempX, tempY,  Entity.EntityType.EMPTY);

            if (emptyCount > 0) {
                int emptyId = localRandom.nextInt(emptyCount);

                newX = tempX[emptyId];
                newY = tempY[emptyId];
            }
        }

        if (newX != -1 && newY != -1) {
            Entity oldEntity = grid[newY][newX];

            grid[newY][newX] = shark;
            grid[y][x] = oldEntity;

            if (shark.getAge() >= SHARK_BREED_TIME) {
                oldEntity.becomeShark();
                oldEntity.setLastChronon(currentChronon);

                shark.setAge(0);
            } else {
                oldEntity.becomeEmpty();
            }
        }
    }

    public Entity getEntityAt(int x, int y) {
        return grid[getWrapY(y)][getWrapX(x)];
    }

    public void nextChronon() {
        ++currentChronon;
        int[] tempX = new int[4];
        int[] tempY = new int[4];

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                Entity current = grid[y][x];

                if (current.getType() == Entity.EntityType.EMPTY || current.getLastChronon() == currentChronon) {
                    continue;
                }

                current.setLastChronon(currentChronon);
                current.setAge(current.getAge() + 1);

                if (current.getType() == Entity.EntityType.FISH) {
                    processFish(x, y, current, random, tempX, tempY);
                } else if (current.getType() == Entity.EntityType.SHARK) {
                    processShark(x, y, current, random, tempX, tempY);
                }
            }
        }
    }

    public void runBatch(int startRow, int endRow, int batchNum, int chronons) throws BrokenBarrierException, InterruptedException {
        Random localRandom = ThreadLocalRandom.current();
        int[] tempX = new int[4];
        int[] tempY = new int[4];

        for (int c = 0; c < chronons; ++c) {
            for (int y = startRow; y <= endRow; ++y) {
                if (y == startRow || y == startRow + 1) {
                    locks.get(batchNum).lock();
                }

                if (y == endRow || y == endRow - 1) {
                    locks.get((endRow == height - 1) ? 0 : (batchNum + 1)).lock();
                }

                try {
                    for (int x = 0; x < width; ++x) {
                        Entity current = grid[y][x];

                        if (current.getType() == Entity.EntityType.EMPTY || current.getLastChronon() == currentChronon) {
                            continue;
                        }

                        current.setLastChronon(currentChronon);
                        current.setAge(current.getAge() + 1);

                        if (current.getType() == Entity.EntityType.FISH) {
                            processFish(x, y, current, localRandom, tempX, tempY);
                        } else if (current.getType() == Entity.EntityType.SHARK) {
                            processShark(x, y, current, localRandom,  tempX, tempY);
                        }
                    }
                } finally {
                    if (y == startRow || y == startRow + 1) {
                        locks.get(batchNum).unlock();
                    }

                    if (y == endRow || y == endRow - 1) {
                        locks.get((endRow == height - 1) ? 0 : (batchNum + 1)).unlock();
                    }
                }
            }

            barrier.await();
        }
    }

    public void runSimulation(int chronons, boolean visualize) {
        for (int i = 0; i < chronons; ++i) {
            nextChronon();

            if (visualize && currentChronon % 10 == 0) {
                WaTorVisualizer.saveFrame(grid, currentChronon / 10, 4);
            }
        }
    }

    public void runSimulation(int chronons, boolean visualize, int threadsCount) {
        currentChronon = 1;
        int batchSize = height / threadsCount;

        barrier = new CyclicBarrier(threadsCount, () -> {
            ++currentChronon;

            if (visualize && currentChronon % 10 == 0) {
                WaTorVisualizer.saveFrame(grid, currentChronon / 10, 4);
            }
        });

        Thread[] threads = new Thread[threadsCount];

        for (int i = 0; i < threadsCount; i++) {
            locks.add(new ReentrantLock());
        }

        for (int i = 0; i < threadsCount; i++) {
            threads[i] = new Thread(
                new WaTorWorker(
                        this,
                        i * batchSize,
                        (i == threadsCount - 1) ? height - 1 : (i + 1) * batchSize - 1,
                        i,
                        chronons
                )
            );

            threads[i].start();
        }

        for (int i = 0; i < threadsCount; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println(e.getMessage());
            }
        }

        locks.clear();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Chronon: ").append(currentChronon).append("\n");

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Entity current = grid[y][x];

                switch (current.getType()) {
                    case EMPTY:
                        sb.append(". ");
                        break;
                    case FISH:
                        sb.append("F ");
                        break;
                    case SHARK:
                        sb.append("S ");
                        break;
                }
            }

            sb.append("\n");
        }

        return sb.toString();
    }
}