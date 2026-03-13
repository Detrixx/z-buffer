package raster;

import transforms.Col;

import java.util.Optional;

public class ZBuffer {
    private final Raster<Col> imageBuffer;
    private final Raster<Double> depthBuffer;

    public ZBuffer(Raster<Col> imageBuffer) {
        this.imageBuffer = imageBuffer;
        this.depthBuffer = new DepthBuffer(imageBuffer.getWidth(), imageBuffer.getHeight());
    }

    public void setPixelWithZTest(int x, int y, double z, Col color) {
        Optional<Double> zOptional = depthBuffer.getValue(x, y);

        if (zOptional.isPresent()) {
            double oldZ = zOptional.get();

            if (z < oldZ) {
                depthBuffer.setValue(x, y, z);
                imageBuffer.setValue(x, y, color);
            }
        }
    }

    public Raster<Col> getImageBuffer() {
        return imageBuffer;
    }

    public Raster<Double> getDepthBuffer() {
        return depthBuffer;
    }
}
