package solid;

import model.Part;
import model.TopologyType;
import model.Vertex;
import transforms.Col;
import transforms.Vec2D;

public class Arrow extends Solid {

    public Arrow() {
        vertexBuffer.add(new Vertex(600, 300, 0.5)); // v0
        vertexBuffer.add(new Vertex(300, 300, 0.5)); // v1
        vertexBuffer.add(new Vertex(300, 450, 0.5, new Col(0xff0000), new Vec2D(1, 1))); // v2
        vertexBuffer.add(new Vertex(100, 300, 0.5, new Col(0x00ff00), new Vec2D(0, 0.5))); // v3
        vertexBuffer.add(new Vertex(300, 150, 0.5, new Col(0x0000ff), new Vec2D(1, 0))); // v4

        addIndices(0, 1); // lines
        addIndices(4, 3, 2); // triangles

        partBuffer.add(new Part(TopologyType.LINES, 0, 1));
        partBuffer.add(new Part(TopologyType.TRIANGLES, 2, 1));
    }
}
