import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WaTorVisualizer {
    private static final Color WATER_COLOR = new Color(30, 144, 255);
    private static final Color FISH_COLOR = new Color(50, 205, 50);
    private static final Color SHARK_COLOR = new Color(220, 20, 60);

    private static final String VISUALIZER_DIR = "frames";

    public static void saveFrame(Entity[][] grid, int chronon, int cellSize) {
        int rows = grid.length;
        int cols = grid[0].length;

        int width = cols * cellSize;
        int height = rows * cellSize;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        for (int y = 0; y < rows; ++y) {
            for (int x = 0; x < cols; ++x) {
                switch (grid[y][x].getType()) {
                    case EMPTY -> g.setColor(WATER_COLOR);
                    case FISH -> g.setColor(FISH_COLOR);
                    case SHARK -> g.setColor(SHARK_COLOR);
                    default -> g.setColor(Color.DARK_GRAY);
                }

                g.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
            }
        }

        g.dispose();

        String currentDir = Paths.get("").toAbsolutePath().toString();
        String fileName = String.format("frame_%04d.png", chronon);

        Path dir = Paths.get(currentDir, VISUALIZER_DIR);
        Path fullPath = dir.resolve(fileName);

        try {
            Files.createDirectories(dir);
            ImageIO.write(image, "png", fullPath.toFile());
        } catch (IOException e) {
            System.err.println("Error saving the frame: " + fileName);
        }
    }

    public static void clearFrames() {
        String currentDir = Paths.get("").toAbsolutePath().toString();
        File folder = Paths.get(currentDir, VISUALIZER_DIR).toFile();

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        file.delete();
                    }
                }
            }
        }
    }
}