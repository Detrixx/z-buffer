package solid;

import model.Part;
import model.TopologyType;
import model.Vertex;
import transforms.Col;
import transforms.Point3D;
import transforms.Vec2D;
import transforms.Vec3D;

public class Cone extends Solid {

    public Cone(int sides) {
        double radius = 1.0;
        double height = 1.0;
        

        Vec3D bottomNormal = new Vec3D(0, 0, -1);
        int bottomCenterIndex = vertexBuffer.size();
        vertexBuffer.add(new Vertex(new Point3D(0, 0, 0), new Col(0, 255, 0), new Vec2D(0.5, 0.5), bottomNormal));
        
        int bottomStartIndex = vertexBuffer.size();
        double angleStep = 2 * Math.PI / sides;
        for (int i = 0; i < sides; i++) {
            double angle = i * angleStep;
            double x = Math.cos(angle) * radius;
            double y = Math.sin(angle) * radius;
            
            vertexBuffer.add(new Vertex(new Point3D(x, y, 0), new Col(0, 0, 255), new Vec2D((x/radius+1)/2, (y/radius+1)/2), bottomNormal));
        }
        
        for (int i = 0; i < sides; i++) {
            int current = bottomStartIndex + i;
            int next = bottomStartIndex + (i + 1) % sides;
            addIndices(bottomCenterIndex, next, current);
        }
        
        int topIndex = vertexBuffer.size();

        vertexBuffer.add(new Vertex(new Point3D(0, 0, height), new Col(255, 0, 0), new Vec2D(0.5, 0), new Vec3D(0, 0, 1)));
        
        int mantleStartIndex = vertexBuffer.size();
        for (int i = 0; i < sides; i++) {
            double angle = i * angleStep;
            double x = Math.cos(angle) * radius;
            double y = Math.sin(angle) * radius;
            
            Vec3D normal = new Vec3D(x, y, radius / height).normalized().orElse(new Vec3D(0,0,1));
            
            vertexBuffer.add(new Vertex(new Point3D(x, y, 0), new Col(0, 0, 255), new Vec2D(i / (double)sides, 1), normal));
        }
        
        for (int i = 0; i < sides; i++) {
            int current = mantleStartIndex + i;
            int next = mantleStartIndex + (i + 1) % sides;
            addIndices(topIndex, current, next);
        }

        partBuffer.add(new Part(TopologyType.TRIANGLES, 0, 2 * sides));
    }

    public Cone() {
        this(12);
    }
}
