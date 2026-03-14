package renderer;

import model.Part;
import model.TopologyType;
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
        Mat4 model = solid.getModel();
        Mat4 mvp = model.mul(view).mul(projection);

        for (Part part : solid.getPartBuffer()) {
            if (part.getType() == TopologyType.TRIANGLES) {
                for (int i = 0; i < part.getCount(); i++) {
                    int indexA = solid.getIndexBuffer().get(part.getStartIndex() + i * 3);
                    int indexB = solid.getIndexBuffer().get(part.getStartIndex() + i * 3 + 1);
                    int indexC = solid.getIndexBuffer().get(part.getStartIndex() + i * 3 + 2);

                    Vertex vA = solid.getVertexBuffer().get(indexA);
                    Vertex vB = solid.getVertexBuffer().get(indexB);
                    Vertex vC = solid.getVertexBuffer().get(indexC);

                    Vertex pA = new Vertex(vA.getPosition().mul(mvp), vA.getColor(), vA.getUv(), vA.getNormal());
                    Vertex pB = new Vertex(vB.getPosition().mul(mvp), vB.getColor(), vB.getUv(), vB.getNormal());
                    Vertex pC = new Vertex(vC.getPosition().mul(mvp), vC.getColor(), vC.getUv(), vC.getNormal());
                    

                    Mat3 rotScale = new Mat3(
                        new Vec3D(model.get(0,0), model.get(0,1), model.get(0,2)),
                        new Vec3D(model.get(1,0), model.get(1,1), model.get(1,2)),
                        new Vec3D(model.get(2,0), model.get(2,1), model.get(2,2))
                    );
                    
                    Vec3D nA = vA.getNormal().mul(rotScale);
                    Vec3D nB = vB.getNormal().mul(rotScale);
                    Vec3D nC = vC.getNormal().mul(rotScale);
                    
                    Vertex wA = new Vertex(vA.getPosition().mul(model), vA.getColor(), vA.getUv(), nA);
                    Vertex wB = new Vertex(vB.getPosition().mul(model), vB.getColor(), vB.getUv(), nB);
                    Vertex wC = new Vertex(vC.getPosition().mul(model), vC.getColor(), vC.getUv(), nC);

                    if (pA.getPosition().getW() <= 0 || pB.getPosition().getW() <= 0 || pC.getPosition().getW() <= 0) continue;

                    pA = dehomog(pA);
                    pB = dehomog(pB);
                    pC = dehomog(pC);

                    pA = transformToWindow(pA);
                    pB = transformToWindow(pB);
                    pC = transformToWindow(pC);
                    
                    if (wireframe) {
                        lineRasterizer.rasterize(pA, pB);
                        lineRasterizer.rasterize(pB, pC);
                        lineRasterizer.rasterize(pC, pA);
                    } else {
                        renderTriangle(pA, pB, pC, wA, wB, wC, solid.getShader());
                    }
                }
            }
        }
    }

    private Vertex dehomog(Vertex v) {
        return v.mul(1 / v.getPosition().getW());
    }

    private Vertex transformToWindow(Vertex v) {
        int width = 800;
        int height = 600;
        
        double x = (v.getX() + 1) * (width - 1) / 2.0;
        double y = (1 - v.getY()) * (height - 1) / 2.0;
        
        return new Vertex(new Point3D(x, y, v.getZ()), v.getColor(), v.getUv(), v.getNormal());
    }

    public void renderTriangle(Vertex pA, Vertex pB, Vertex pC, Vertex wA, Vertex wB, Vertex wC, Shader shader) {
        triangleRasterizer.rasterize(pA, pB, pC, wA, wB, wC, shader);
    }
}
