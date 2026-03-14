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

    public void rasterize(Vertex p1, Vertex p2, Vertex p3, Vertex w1, Vertex w2, Vertex w3, Shader shader) {

        if (p1.getY() > p2.getY()) {

            Vertex tempP = p1; p1 = p2; p2 = tempP; 
            Vertex tempW = w1; w1 = w2; w2 = tempW; 
        }
        if (p2.getY() > p3.getY()) { 
            Vertex tempP = p2; p2 = p3; p3 = tempP; 
            Vertex tempW = w2; w2 = w3; w3 = tempW; 
        }
        if (p1.getY() > p2.getY()) { 
            Vertex tempP = p1; p1 = p2; p2 = tempP; 
            Vertex tempW = w1; w1 = w2; w2 = tempW; 
        }
        
        if (Math.abs(p1.getY() - p3.getY()) < 0.1) return;

        int yStart = (int) Math.max(0, Math.ceil(p1.getY()));
        int yEnd = (int) Math.min(zBuffer.getImageBuffer().getHeight() - 1, Math.floor(p3.getY()));

        for (int y = yStart; y <= yEnd; y++) {
            double tA = (y - p1.getY()) / (p3.getY() - p1.getY());
            Vertex pA = p1.mul(1 - tA).add(p3.mul(tA));
            Vertex wA = w1.mul(1 - tA).add(w3.mul(tA));

            Vertex pB;
            Vertex wB;
            
            if (y < p2.getY()) {
                if (Math.abs(p2.getY() - p1.getY()) < 0.1) continue; 
                double tB = (y - p1.getY()) / (p2.getY() - p1.getY());
                pB = p1.mul(1 - tB).add(p2.mul(tB));
                wB = w1.mul(1 - tB).add(w2.mul(tB));
            } else {
                if (Math.abs(p3.getY() - p2.getY()) < 0.1) continue;
                double tB = (y - p2.getY()) / (p3.getY() - p2.getY());
                pB = p2.mul(1 - tB).add(p3.mul(tB));
                wB = w2.mul(1 - tB).add(w3.mul(tB));
            }

            if (pA.getX() > pB.getX()) { 
                Vertex tempP = pA; pA = pB; pB = tempP; 
                Vertex tempW = wA; wA = wB; wB = tempW; 
            }
            
            int xStart = (int) Math.max(0, Math.ceil(pA.getX()));
            int xEnd = (int) Math.min(zBuffer.getImageBuffer().getWidth() - 1, Math.floor(pB.getX()));

            double dx = pB.getX() - pA.getX();
            
            for (int x = xStart; x <= xEnd; x++) {
                double t = (dx == 0) ? 0 : (x - pA.getX()) / dx;
                
                double z = pA.getZ() * (1 - t) + pB.getZ() * t;
                
                Vertex wPixel = wA.mul(1 - t).add(wB.mul(t));
                
                Col color = shader.getColor(wPixel);
                zBuffer.setPixelWithZTest(x, y, z, color);
            }
        }
    }
}
