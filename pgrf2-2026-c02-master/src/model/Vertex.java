package model;

import transforms.Col;
import transforms.Point3D;
import transforms.Vec2D;

/**
 *
 * @author KŠ
 * 24.02.2026 17:48
 */

public class Vertex implements Vectorizable<Vertex> {
    private final Point3D position;
    private final Col color;
    private final Vec2D uv;
    // TODO normal, UV, one

    public Vertex(Point3D position) {
        this.position = position;
        this.color = new Col(0xffffff);
        this.uv = new Vec2D();
    }

    public Vertex(Point3D position, Col color, Vec2D uv) {
        this.position = position;
        this.color = color;
        this.uv = (uv != null) ? uv : new Vec2D();
    }

    public Vertex(double x, double y, double z, Col color, Vec2D uv) {
        this.position = new Point3D(x, y, z);
        this.color = color;
        this.uv = (uv != null) ? uv : new Vec2D();
    }

    public Vertex(double x, double y, double z) {
        this.position = new Point3D(x, y, z);
        this.color = new Col(0xffffff);
        this.uv = new Vec2D();
    }

    public Point3D getPosition() {
        return position;
    }

    public Col getColor() {
        return color;
    }

    public double getX() {
        return position.getX();
    }

    public double getY() {
        return position.getY();
    }

    public double getZ() {
        return position.getZ();
    }

    public Vec2D getUv() {
        return uv;
    }

    @Override
    public Vertex mul(double d) {
        return new Vertex(this.position.mul(d), this.color.mul(d), this.uv.mul(d));
    }

    @Override
    public Vertex add(Vertex v) {
        return new Vertex(this.position.add(v.getPosition()), this.color.add(v.getColor()), this.uv.add(v.getUv()));
    }
}
