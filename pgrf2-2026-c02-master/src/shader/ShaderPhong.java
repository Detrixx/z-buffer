package shader;

import model.Vertex;
import transforms.Col;
import transforms.Vec3D;

public class ShaderPhong implements Shader {

    private Vec3D lightP;


    private final Col lightColor;
    private final Col ambientColor;


    public ShaderPhong(Vec3D lightP, Col lightColor, Col ambientColor) {
        this.lightP = lightP;
        this.lightColor = lightColor;
        this.ambientColor = ambientColor;
    }


    public void setLightP(Vec3D lightP) {
        this.lightP = lightP;
    }

    @Override
    public Col getColor(Vertex pixel) {
        Col objectColor = pixel.getColor();
        
        // Ambientní
        double rA = objectColor.getR() * ambientColor.getR();
        double gA = objectColor.getG() * ambientColor.getG();
        double bA = objectColor.getB() * ambientColor.getB();

        // Vektor
        Vec3D L = lightP.sub(new Vec3D(pixel.getPosition())).normalized().orElse(new Vec3D(0,1,0));
        
        // Normála
        Vec3D N = pixel.getNormal().normalized().orElse(new Vec3D(0,0,1));
        
        // Úhel dopadu
        double diff = Math.max(0.0, N.dot(L));
        
        // Diffuse
        double rD = objectColor.getR() * lightColor.getR() * diff;
        double gD = objectColor.getG() * lightColor.getG() * diff;
        double bD = objectColor.getB() * lightColor.getB() * diff;
        
        double r = Math.min(1.0, rA + rD);
        double g = Math.min(1.0, gA + gD);
        double b = Math.min(1.0, bA + bD);

        return new Col(r, g, b);
    }
}
