package solid;

import model.Part;
import model.TopologyType;
import model.Vertex;
import transforms.Col;
import transforms.Point3D;
import transforms.Vec2D;
import transforms.Vec3D;

public class Cube extends Solid {

    public Cube() {

        addFace(
            new Point3D(-0.5, -0.5, 0.5), new Point3D(0.5, -0.5, 0.5), 
            new Point3D(0.5, 0.5, 0.5), new Point3D(-0.5, 0.5, 0.5),
            new Vec3D(0, 0, 1), 
            new Col(255, 0, 0), new Col(0, 255, 0), new Col(0, 0, 255), new Col(255, 255, 0)
        );
        
        // Zadní stena
        addFace(
            new Point3D(0.5, -0.5, -0.5), new Point3D(-0.5, -0.5, -0.5), 
            new Point3D(-0.5, 0.5, -0.5), new Point3D(0.5, 0.5, -0.5),
            new Vec3D(0, 0, -1), 
            new Col(0, 255, 255), new Col(255, 0, 255), new Col(255, 255, 255), new Col(0, 0, 0)
        );
        
        // Pravá stena
        addFace(
            new Point3D(0.5, -0.5, 0.5), new Point3D(0.5, -0.5, -0.5), 
            new Point3D(0.5, 0.5, -0.5), new Point3D(0.5, 0.5, 0.5),
            new Vec3D(1, 0, 0), 
            new Col(0, 255, 0), new Col(255, 0, 255), new Col(255, 255, 255), new Col(0, 0, 255)
        );
        
        // Levá stena
        addFace(
            new Point3D(-0.5, -0.5, -0.5), new Point3D(-0.5, -0.5, 0.5), 
            new Point3D(-0.5, 0.5, 0.5), new Point3D(-0.5, 0.5, -0.5),
            new Vec3D(-1, 0, 0), 
            new Col(0, 255, 255), new Col(255, 0, 0), new Col(255, 255, 0), new Col(0, 0, 0)
        );
        
        // Horní stena
        addFace(
            new Point3D(-0.5, 0.5, 0.5), new Point3D(0.5, 0.5, 0.5), 
            new Point3D(0.5, 0.5, -0.5), new Point3D(-0.5, 0.5, -0.5),
            new Vec3D(0, 1, 0), 
            new Col(255, 255, 0), new Col(0, 0, 255), new Col(255, 255, 255), new Col(0, 0, 0)
        );
        
        // Dolni stena
        addFace(
            new Point3D(-0.5, -0.5, -0.5), new Point3D(0.5, -0.5, -0.5), 
            new Point3D(0.5, -0.5, 0.5), new Point3D(-0.5, -0.5, 0.5),
            new Vec3D(0, -1, 0), 
            new Col(0, 255, 255), new Col(255, 0, 255), new Col(0, 255, 0), new Col(255, 0, 0)
        );
        
        partBuffer.add(new Part(TopologyType.TRIANGLES, 0, 12));
    }
    
    private void addFace(Point3D p1, Point3D p2, Point3D p3, Point3D p4, Vec3D normal, Col c1, Col c2, Col c3, Col c4) {
        int startIndex = vertexBuffer.size();
        
        vertexBuffer.add(new Vertex(p1, c1, new Vec2D(0, 0), normal));
        vertexBuffer.add(new Vertex(p2, c2, new Vec2D(1, 0), normal));
        vertexBuffer.add(new Vertex(p3, c3, new Vec2D(1, 1), normal));
        vertexBuffer.add(new Vertex(p4, c4, new Vec2D(0, 1), normal));
        
        addIndices(startIndex, startIndex + 1, startIndex + 2);
        addIndices(startIndex, startIndex + 2, startIndex + 3);
    }
}
