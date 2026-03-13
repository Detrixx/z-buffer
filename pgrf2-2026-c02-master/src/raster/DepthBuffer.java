package raster;

import java.util.Optional;

public class DepthBuffer implements Raster<Double>{
    private final double[][] buffer;
    private final int width;
    private final int height;

    public DepthBuffer(int width, int height) {
        this.width = width;
        this.height = height;
        this.buffer = new double[width][height];
        clear();
    }

    @Override
    public void setValue(int x, int y, Double value) {
        if (isValidValue(x, y)) {
            buffer[x][y] = value;
        }
    }

    @Override
    public Optional<Double> getValue(int x, int y) {
        if (isValidValue(x, y)) {
            return Optional.of(buffer[x][y]);
        }
        return Optional.empty();
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void clear() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                buffer[i][j] = 1.0;
            }
        }
    }
}
