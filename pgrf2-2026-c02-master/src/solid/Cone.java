package solid;

import model.Part;
import model.TopologyType;
import model.Vertex;
import transforms.Col;
import transforms.Point3D;
import transforms.Vec2D;

public class Cone extends Solid {

    public Cone(int sides) {
        // Vrchol
        Vertex top = new Vertex(new Point3D(0, 0, 1), new Col(255, 0, 0), new Vec2D(0.5, 0));
        vertexBuffer.add(top);

        // Střed
        Vertex center = new Vertex(new Point3D(0, 0, 0), new Col(0, 255, 0), new Vec2D(0.5, 1));
        vertexBuffer.add(center);

        double angleStep = 2 * Math.PI / sides;
        for (int i = 0; i < sides; i++) {
            double angle = i * angleStep;
            double x = Math.cos(angle);
            double y = Math.sin(angle);
            vertexBuffer.add(new Vertex(new Point3D(x, y, 0), new Col(0, 0, 255), new Vec2D(i / (double)sides, 1)));
        }

        // Indexy
        for (int i = 0; i < sides; i++) {
            int currentBase = 2 + i;
            int nextBase = 2 + (i + 1) % sides;

            addIndices(0, currentBase, nextBase);

            addIndices(1, nextBase, currentBase);
        }

        partBuffer.add(new Part(TopologyType.TRIANGLES, 0, 2 * sides));
    }

    public Cone() {
        this(12);
    }
}
