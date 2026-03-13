package rasterize;

import model.Line;
import model.Vertex;
import raster.RasterBufferedImage;
import transforms.Col;

public abstract class LineRasterizer {
    protected RasterBufferedImage raster;
    protected Col color;

    public LineRasterizer(RasterBufferedImage raster) {
        this.raster = raster;
        this.color = new Col(0xffffff);
    }

    public void rasterize(int x1, int y1, int x2, int y2) {

    }

    public void rasterize(Line line) {
        rasterize(line.getX1(), line.getY1(), line.getX2(), line.getY2());
    }
    
    // rasterizaci
    public void rasterize(Vertex v1, Vertex v2) {
        rasterize((int)v1.getX(), (int)v1.getY(), (int)v2.getX(), (int)v2.getY());
    }

    public void setColor(Col color) {
        this.color = color;
    }
}
