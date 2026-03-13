package solid;

import model.Part;
import model.TopologyType;
import model.Vertex;
import transforms.Col;
import transforms.Point3D;

public class Cube extends Solid {

    public Cube() {

        // Horní podstava
        vertexBuffer.add(new Vertex(new Point3D(-0.5, -0.5, 0.5), new Col(255, 0, 0), null));
        vertexBuffer.add(new Vertex(new Point3D(0.5, -0.5, 0.5), new Col(0, 255, 0), null));
        vertexBuffer.add(new Vertex(new Point3D(0.5, 0.5, 0.5), new Col(0, 0, 255), null));
        vertexBuffer.add(new Vertex(new Point3D(-0.5, 0.5, 0.5), new Col(255, 255, 0), null));

        // Dolní podstava
        vertexBuffer.add(new Vertex(new Point3D(-0.5, -0.5, -0.5), new Col(0, 255, 255), null));
        vertexBuffer.add(new Vertex(new Point3D(0.5, -0.5, -0.5), new Col(255, 0, 255), null));
        vertexBuffer.add(new Vertex(new Point3D(0.5, 0.5, -0.5), new Col(255, 255, 255), null));
        vertexBuffer.add(new Vertex(new Point3D(-0.5, 0.5, -0.5), new Col(0, 0, 0), null));

        // Indexy
        addIndices(0, 1, 2);
        addIndices(0, 2, 3);

        addIndices(4, 6, 5);
        addIndices(4, 7, 6);

        addIndices(4, 5, 1);
        addIndices(4, 1, 0);

        addIndices(3, 2, 6);
        addIndices(3, 6, 7);

        addIndices(4, 0, 3);
        addIndices(4, 3, 7);

        addIndices(1, 5, 6);
        addIndices(1, 6, 2);

        partBuffer.add(new Part(TopologyType.TRIANGLES, 0, 12));
    }
}
