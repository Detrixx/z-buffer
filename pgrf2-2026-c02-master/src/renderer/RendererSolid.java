package renderer;

import model.Part;
import model.Vertex;
import rasterize.LineRasterizer;
import rasterize.TriangleRasterizer;
import shader.Shader;
import solid.Solid;
import transforms.*;

public class RendererSolid {
    private LineRasterizer lineRasterizer;
    private TriangleRasterizer triangleRasterizer;

    private Mat4 view = new Mat4Identity();
    private Mat4 projection = new Mat4Identity();
    
    private boolean wireframe = false;

    public RendererSolid(LineRasterizer lineRasterizer, TriangleRasterizer triangleRasterizer) {
        this.lineRasterizer = lineRasterizer;
        this.triangleRasterizer = triangleRasterizer;
    }

    public void setView(Mat4 view) {
        this.view = view;
    }

    public void setProjection(Mat4 projection) {
        this.projection = projection;
    }
    
    public void setWireframe(boolean wireframe) {
        this.wireframe = wireframe;
    }

    public void render(Solid solid) {
        Mat4 mvp = solid.getModel().mul(view).mul(projection);

        for (Part part : solid.getPartBuffer()) {
            switch (part.getType()) {
                case LINES:
                    int index = part.getStartIndex();
                    for (int i = 0; i < part.getCount(); i++) {
                        int indexA = solid.getIndexBuffer().get(index++);
                        int indexB = solid.getIndexBuffer().get(index++);

                        Vertex a = solid.getVertexBuffer().get(indexA);
                        Vertex b = solid.getVertexBuffer().get(indexB);

                        a = transform(a, mvp);
                        b = transform(b, mvp);
                        
                        if (a.getPosition().getW() <= 0 || b.getPosition().getW() <= 0) continue; 
                        
                        a = dehomog(a);
                        b = dehomog(b);

                        a = transformToWindow(a);
                        b = transformToWindow(b);

                        lineRasterizer.rasterize(a, b);
                    }
                    break;
                case TRIANGLES:
                    index = part.getStartIndex();
                    for (int i = 0; i < part.getCount(); i++) {
                        int indexA = solid.getIndexBuffer().get(index++);
                        int indexB = solid.getIndexBuffer().get(index++);
                        int indexC = solid.getIndexBuffer().get(index++);

                        Vertex a = solid.getVertexBuffer().get(indexA);
                        Vertex b = solid.getVertexBuffer().get(indexB);
                        Vertex c = solid.getVertexBuffer().get(indexC);

                        a = transform(a, mvp);
                        b = transform(b, mvp);
                        c = transform(c, mvp);

                        if (a.getPosition().getW() <= 0 || b.getPosition().getW() <= 0 || c.getPosition().getW() <= 0) continue;

                        a = dehomog(a);
                        b = dehomog(b);
                        c = dehomog(c);

                        a = transformToWindow(a);
                        b = transformToWindow(b);
                        c = transformToWindow(c);
                        
                        if (wireframe) {
                            lineRasterizer.rasterize(a, b);
                            lineRasterizer.rasterize(b, c);
                            lineRasterizer.rasterize(c, a);
                        } else {
                            renderTriangle(a, b, c, solid.getShader());
                        }
                    }
                    break;
            }
        }
    }

    private Vertex transform(Vertex v, Mat4 mat) {
        return new Vertex(v.getPosition().mul(mat), v.getColor(), v.getUv());
    }

    private Vertex dehomog(Vertex v) {
        return v.mul(1 / v.getPosition().getW());
    }

    private Vertex transformToWindow(Vertex v) {
        int width = 800;
        int height = 600;
        
        double x = (v.getX() + 1) * (width - 1) / 2.0;
        double y = (1 - v.getY()) * (height - 1) / 2.0;
        
        return new Vertex(new Point3D(x, y, v.getZ()), v.getColor(), v.getUv());
    }

    public void setLineRasterizer(LineRasterizer lineRasterizer) {
        this.lineRasterizer = lineRasterizer;
    }

    public void setTriangleRasterizer(TriangleRasterizer triangleRasterizer) {
        this.triangleRasterizer = triangleRasterizer;
    }

    public void renderTriangle(Vertex a, Vertex b, Vertex c, Shader shader) {
        triangleRasterizer.rasterize(a, b, c, shader);
    }
}
