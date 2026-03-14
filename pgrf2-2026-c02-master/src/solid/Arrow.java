package solid;

import model.Part;
import model.TopologyType;
import model.Vertex;
import transforms.Col;
import transforms.Point3D;
import transforms.Vec2D;

public class Arrow extends Solid {

    public Arrow() {
        vertexBuffer.add(new Vertex(new Point3D(600.0, 300.0, 0.5)));
        vertexBuffer.add(new Vertex(new Point3D(300.0, 300.0, 0.5)));
        vertexBuffer.add(new Vertex(new Point3D(300.0, 450.0, 0.5), new Col(0xff0000), new Vec2D(1, 1)));
        vertexBuffer.add(new Vertex(new Point3D(100.0, 300.0, 0.5), new Col(0x00ff00), new Vec2D(0, 0.5)));
        vertexBuffer.add(new Vertex(new Point3D(300.0, 150.0, 0.5), new Col(0x0000ff), new Vec2D(1, 0)));

        addIndices(0, 1); // lines
        addIndices(4, 3, 2); // triangles

        partBuffer.add(new Part(TopologyType.LINES, 0, 1));
        partBuffer.add(new Part(TopologyType.TRIANGLES, 2, 1));
    }
}
