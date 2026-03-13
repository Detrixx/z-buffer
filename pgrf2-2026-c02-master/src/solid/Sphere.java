package solid;

import model.Part;
import model.TopologyType;
import model.Vertex;
import transforms.Col;
import transforms.Point3D;
import transforms.Vec2D;

public class Sphere extends Solid {

    public Sphere() {
        int horizontal = 10;
        int vertical = 10;
        double radius = 1.0;

        for (int i = 0; i <= horizontal; i++) {
            double v = i / (double) horizontal;
            double lat = (v - 0.5) * Math.PI;
            double cosLat = Math.cos(lat);
            double sinLat = Math.sin(lat);

            for (int j = 0; j <= vertical; j++) {
                double u = j / (double) vertical;
                double lon = u * 2 * Math.PI;
                double cosLon = Math.cos(lon);
                double sinLon = Math.sin(lon);

                double x = cosLat * cosLon * radius;
                double y = cosLat * sinLon * radius;
                double z = sinLat * radius;

                Col color = new Col((int) (u * 255), (int) (v * 255), 255);
                vertexBuffer.add(new Vertex(new Point3D(x, y, z), color, new Vec2D(u, v)));
            }
        }

        // index
        for (int i = 0; i < horizontal; i++) {
            for (int j = 0; j < vertical; j++) {
                int index = i * (vertical + 1) + j;
                int nextIndex = index + 1;
                int bottomIndex = (i + 1) * (vertical + 1) + j;
                int bottomNextIndex = bottomIndex + 1;

                addIndices(index, bottomIndex, nextIndex);
                addIndices(nextIndex, bottomIndex, bottomNextIndex);
            }
        }
        
        partBuffer.add(new Part(TopologyType.TRIANGLES, 0, horizontal * vertical * 2));
    }
}
