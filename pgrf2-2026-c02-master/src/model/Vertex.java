package model;

import transforms.*;

public class Vertex implements Vectorizable<Vertex> {
    private final Point3D position;
    private final Col color;
    private final Vec2D uv;
    private final Vec3D normal; 

    public Vertex(Point3D position, Col color, Vec2D uv, Vec3D normal) {
        this.position = position;
        this.color = color;
        this.uv = (uv != null) ? uv : new Vec2D();
        this.normal = (normal != null) ? normal : new Vec3D(0, 0, 1);
    }
    
    public Vertex(Point3D position) {
        this(position, new Col(0xffffff), null, null);
    }
    public Vertex(Point3D position, Col color, Vec2D uv) {
        this(position, color, uv, null);
    }
    
    public Point3D getPosition() { return position; }
    public Col getColor() { return color; }
    public Vec2D getUv() { return uv; }
    public Vec3D getNormal() { return normal; }
    public double getX() { return position.getX(); }
    public double getY() { return position.getY(); }
    public double getZ() { return position.getZ(); }

    @Override
    public Vertex mul(double d) {
        return new Vertex(position.mul(d), color.mul(d), uv.mul(d), normal.mul(d));
    }

    @Override
    public Vertex add(Vertex v) {
        return new Vertex(position.add(v.getPosition()), color.add(v.getColor()), uv.add(v.getUv()), normal.add(v.getNormal()));
    }
}
