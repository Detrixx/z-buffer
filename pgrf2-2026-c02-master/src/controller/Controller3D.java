package controller;

import model.Vertex;
import raster.ZBuffer;
import rasterize.LineRasterizer;
import rasterize.LineRasterizerDDA;
import rasterize.TriangleRasterizer;
import renderer.RendererSolid;
import shader.Shader;
import shader.ShaderConstant;
import shader.ShaderInterpolated;
import solid.*;
import transforms.*;
import view.Panel;

import javax.imageio.ImageIO;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Controller3D {
    private final Panel panel;
    private final ZBuffer zBuffer;
    private LineRasterizer lineRasterizer;
    private TriangleRasterizer triangleRasterizer;
    private RendererSolid renderer;

    private BufferedImage texture;

    private final List<Solid> solids = new ArrayList<>();
    private int activeSolidIndex = 0;
    
    private boolean wireframe = false;

    private Camera camera;
    private Mat4 projection;
    
    private int startX, startY;

    public Controller3D(Panel panel) {
        this.panel = panel;
        this.zBuffer = new ZBuffer(panel.getRaster());

        lineRasterizer = new LineRasterizerDDA(zBuffer);
        triangleRasterizer = new TriangleRasterizer(zBuffer);
        renderer = new RendererSolid(lineRasterizer, triangleRasterizer);

        try {
            texture = ImageIO.read(new File("./res/textures/sandstone.jpg"));
        } catch (IOException e) {
            System.out.println("chyba textura");
        }

        // telesa
        solids.add(new Cube());
        solids.add(new Sphere());
        solids.add(new Cone());
        
        solids.get(0).setModel(new Mat4Transl(-2, 0, 0));
        solids.get(1).setModel(new Mat4Transl(0, 0, 0));
        solids.get(2).setModel(new Mat4Transl(2, 0, 0));
        
        // kamera
        camera = new Camera()
                .withPosition(new Vec3D(0, -5, 2))
                .withAzimuth(Math.toRadians(90))
                .withZenith(Math.toRadians(-20));
        
        // projekce
        projection = new Mat4PerspRH(Math.PI / 3, 800.0 / 600.0, 0.1, 20.0);

        initListeners();
        
        panel.setFocusable(true);
        panel.grabFocus();
        panel.setFocusTraversalKeysEnabled(false);

        drawScene();
    }

    private void initListeners() {
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startX = e.getX();
                startY = e.getY();
            }
        });

        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int dx = startX - e.getX();
                int dy = startY - e.getY();

                double sensitivity = 0.005;
                
                camera = camera.addAzimuth(dx * sensitivity);
                camera = camera.addZenith(dy * sensitivity);
                
                startX = e.getX();
                startY = e.getY();
                
                drawScene();
            }
        });

        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                Solid activeSolid = solids.get(activeSolidIndex);

                if(e.getKeyCode() == KeyEvent.VK_U)
                    activeSolid.setShader(new ShaderConstant());
                if(e.getKeyCode() == KeyEvent.VK_I)
                    activeSolid.setShader(new ShaderInterpolated());
                if(e.getKeyCode() == KeyEvent.VK_O && texture != null) {
                        Shader shader = new Shader() {
                            @Override
                            public Col getColor(Vertex pixel) {
                                int x = (int) (pixel.getUv().getX() * (texture.getWidth() - 1));
                                int y = (int) (pixel.getUv().getY() * (texture.getHeight() - 1));
                                if (x < 0) x = 0; if (x >= texture.getWidth()) x = texture.getWidth() - 1;
                                if (y < 0) y = 0; if (y >= texture.getHeight()) y = texture.getHeight() - 1;
                                return new Col(texture.getRGB(x, y));
                            }
                        };
                        activeSolid.setShader(shader);
                }
                
                // Přepínání těles
                if (e.getKeyCode() == KeyEvent.VK_TAB) {
                    activeSolidIndex++;
                    if (activeSolidIndex >= solids.size()) activeSolidIndex = 0;
                }
                
                // Wireframe
                if (e.getKeyCode() == KeyEvent.VK_M) {
                    wireframe = !wireframe;
                }
                
                // Transformace
                boolean change = false;
                Mat4 model = activeSolid.getModel();
                double step = 0.1;
                
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    model = model.mul(new Mat4Transl(-step, 0, 0)); change = true;
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    model = model.mul(new Mat4Transl(step, 0, 0)); change = true;
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    model = model.mul(new Mat4Transl(0, step, 0)); change = true;
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    model = model.mul(new Mat4Transl(0, -step, 0)); change = true;
                }
                if ( e.getKeyCode() == KeyEvent.VK_PAGE_UP) {
                    model = model.mul(new Mat4Transl(0, 0, step)); change = true;
                }
                if ( e.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
                    model = model.mul(new Mat4Transl(0, 0, -step)); change = true;
                }
                
                if (e.getKeyCode() == KeyEvent.VK_R) {
                    model = model.mul(new Mat4RotX(0.1)); change = true;
                }

                if (e.getKeyCode() == KeyEvent.VK_X) {
                    model = model.mul(new Mat4Scale(1.1)); change = true;
                }
                if (e.getKeyCode() == KeyEvent.VK_C) {
                    model = model.mul(new Mat4Scale(0.9)); change = true;
                }

                if (change) activeSolid.setModel(model);

                // Ovládání kamery WSAD
                if (e.getKeyCode() == KeyEvent.VK_W) camera = camera.forward(0.1);
                if (e.getKeyCode() == KeyEvent.VK_S) camera = camera.backward(0.1);
                if (e.getKeyCode() == KeyEvent.VK_A) camera = camera.left(0.1);
                if (e.getKeyCode() == KeyEvent.VK_D) camera = camera.right(0.1);

                drawScene();
            }
        });
    }

    private void drawScene() {
        panel.getRaster().clear();
        zBuffer.getImageBuffer().clear();
        zBuffer.getDepthBuffer().clear();

        //renderer.setView();
        renderer.setView(camera.getViewMatrix());
        renderer.setProjection(projection);

        renderer.setWireframe(wireframe);

        for (int i = 0; i < solids.size(); i++) {
            renderer.render(solids.get(i));
        }
        
        panel.repaint();
    }
}
