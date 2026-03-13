package rasterize;

import model.Vertex;
import raster.ZBuffer;
import shader.Shader;
import transforms.Col;

public class TriangleRasterizer {
    private final ZBuffer zBuffer;

    public TriangleRasterizer(ZBuffer zBuffer) {
        this.zBuffer = zBuffer;
    }

    public void rasterize(Vertex v1, Vertex v2, Vertex v3, Shader shader) {
        // Seřazení podle Y
        if (v1.getY() > v2.getY()) { Vertex temp = v1; v1 = v2; v2 = temp; }
        if (v2.getY() > v3.getY()) { Vertex temp = v2; v2 = v3; v3 = temp; }
        if (v1.getY() > v2.getY()) { Vertex temp = v1; v1 = v2; v2 = temp; }
        
        if (Math.abs(v1.getY() - v3.getY()) < 0.1) return;

        // Rozsah Y
        int yStart = (int) Math.max(0, Math.ceil(v1.getY()));
        int yEnd = (int) Math.min(zBuffer.getImageBuffer().getHeight() - 1, Math.floor(v3.getY()));

        for (int y = yStart; y <= yEnd; y++) {
            double tA = (y - v1.getY()) / (v3.getY() - v1.getY());
            Vertex va = v1.mul(1 - tA).add(v3.mul(tA));

            Vertex vb;

            if (y < v2.getY()) {
                if (Math.abs(v2.getY() - v1.getY()) < 0.1) continue;
                double tB = (y - v1.getY()) / (v2.getY() - v1.getY());
                vb = v1.mul(1 - tB).add(v2.mul(tB));
            } 
            else {
                if (Math.abs(v3.getY() - v2.getY()) < 0.1) continue;
                double tB = (y - v2.getY()) / (v3.getY() - v2.getY());
                vb = v2.mul(1 - tB).add(v3.mul(tB));
            }

            // Seřazení podle X
            if (va.getX() > vb.getX()) { Vertex temp = va; va = vb; vb = temp; }
            
            int xStart = (int) Math.max(0, Math.ceil(va.getX()));
            int xEnd = (int) Math.min(zBuffer.getImageBuffer().getWidth() - 1, Math.floor(vb.getX()));

            double dx = vb.getX() - va.getX();
            
            for (int x = xStart; x <= xEnd; x++) {
                double t = (dx == 0) ? 0 : (x - va.getX()) / dx;
                
                Vertex p = va.mul(1 - t).add(vb.mul(t));
                

                Col color = shader.getColor(p);
                

                zBuffer.setPixelWithZTest(x, y, p.getZ(), color);
            }
        }
    }
}
