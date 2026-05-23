public class WaTorWorker implements Runnable {
    private final World world;
    private final int startRow;
    private final int endRow;
    private final int index;
    private final int chronons;

    public WaTorWorker(World world, int startRow, int endRow, int index, int chronons) {
        this.world = world;
        this.startRow = startRow;
        this.endRow = endRow;
        this.index = index;
        this.chronons = chronons;
    }

    @Override
    public void run() {
        try {
            world.runBatch(startRow, endRow, index, chronons);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            System.out.println(e.getMessage());
        }
    }
}
