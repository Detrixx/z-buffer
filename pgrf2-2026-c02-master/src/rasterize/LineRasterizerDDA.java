package rasterize;

import model.Vertex;
import raster.RasterBufferedImage;
import raster.ZBuffer;
import transforms.Col;

public class LineRasterizerDDA extends LineRasterizer {

    private final ZBuffer zBuffer;

    public LineRasterizerDDA(ZBuffer zBuffer) {
        super((RasterBufferedImage) zBuffer.getImageBuffer());
        this.zBuffer = zBuffer;
    }

    @Override
    public void rasterize(Vertex v1, Vertex v2) {
        double x1 = v1.getX();
        double y1 = v1.getY();
        double z1 = v1.getZ();
        Col c1 = v1.getColor();

        double x2 = v2.getX();
        double y2 = v2.getY();
        double z2 = v2.getZ();
        Col c2 = v2.getColor();

        double dx = x2 - x1;
        double dy = y2 - y1;
        double dz = z2 - z1;

        int steps = (int) Math.max(Math.abs(dx), Math.abs(dy));

        if (steps == 0) {
            zBuffer.setPixelWithZTest((int)x1, (int)y1, z1, c1);
            return;
        }

        double xStep = dx / steps;
        double yStep = dy / steps;
        double zStep = dz / steps;

        double x = x1;
        double y = y1;
        double z = z1;

        for (int i = 0; i <= steps; i++) {
            double t = (double) i / steps;
            
            // interpolace
            Col color = c1.mul(1 - t).add(c2.mul(t));

            int pixelX = (int) Math.round(x);
            int pixelY = (int) Math.round(y);
            zBuffer.setPixelWithZTest(pixelX, pixelY, z, color);

            x += xStep;
            y += yStep;
            z += zStep;
        }
    }

    @Override
    public void rasterize(int x1, int y1, int x2, int y2) {

    }
}
