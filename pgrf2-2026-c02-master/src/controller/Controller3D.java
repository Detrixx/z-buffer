package controller;

import model.Vertex;
import raster.ZBuffer;
import rasterize.LineRasterizer;
import rasterize.LineRasterizerDDA;
import rasterize.TriangleRasterizer;
import renderer.RendererSolid;
import shader.*;
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
    private int activeSIndex = 0;
    
    // Osvětlení
    private Vec3D lightPos = new Vec3D(0, 0, 3);
    private Solid light;
    
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

            //texture = ImageIO.read(new File("C:/repository/z-buffer/pgrf2-2026-c02-master/res/textures/sandstone.jpg"));

            texture = ImageIO.read(new File("pgrf2-2026-c02-master/src/textures/sandstone.jpg"));
        } catch (IOException e) {
            System.out.println("chyba textura: " + e.getMessage());
        }

        // telesa
        solids.add(new Cube());
        solids.add(new Sphere());
        solids.add(new Cone());
        
        // Rozmístění těles ve scéně
        solids.get(0).setModel(new Mat4Transl(-2, 0, 0)); 
        solids.get(1).setModel(new Mat4Transl(0, 0, 0));  
        solids.get(2).setModel(new Mat4Transl(2, 0, 0));  
        
        // Vizualizace zdroje světla
        light = new Sphere();
        light.setShader(new ShaderConstant(new Col(0xffff00)));
        light.setModel(new Mat4Scale(0.1).mul(new Mat4Transl(lightPos)));
        solids.add(light);
        
        // Inicializace kamery
        camera = new Camera()
                .withPosition(new Vec3D(0, -7, 2))
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
                Solid activeSolid = solids.get(activeSIndex);

                // Shader
                if(e.getKeyCode() == KeyEvent.VK_U)
                    activeSolid.setShader(new ShaderConstant());
                if(e.getKeyCode() == KeyEvent.VK_I)
                    activeSolid.setShader(new ShaderInterpolated());
                if(e.getKeyCode() == KeyEvent.VK_L) {
                    // Zapnutí Phongova osvětlení
                    activeSolid.setShader(new ShaderPhong(lightPos, new Col(0xffffff), new Col(0.2, 0.2, 0.2)));
                }
                if(e.getKeyCode() == KeyEvent.VK_O) {
                    if (texture != null) {
                        Shader shader = new Shader() {
                            @Override
                            public Col getColor(Vertex pixel) {

                                double u = pixel.getUv().getX();
                                double v = pixel.getUv().getY();
                                

                                u = Math.max(0, Math.min(u, 1));
                                v = Math.max(0, Math.min(v, 1));

                                int x = (int) Math.round(u * (texture.getWidth() - 1));
                                int y = (int) Math.round(v * (texture.getHeight() - 1));
                                
                                return new Col(texture.getRGB(x, y));
                            }
                        };
                        activeSolid.setShader(shader);
                    } else {
                        System.out.println("chyba textura");
                    }
                }
                
                // Přepínání tělesa
                if (e.getKeyCode() == KeyEvent.VK_TAB) {
                    activeSIndex++;
                    if (activeSIndex >= solids.size()) activeSIndex = 0;
                }
                
                // Wireframe
                if (e.getKeyCode() == KeyEvent.VK_M) {
                    wireframe = !wireframe;
                }
                
                // Transformace
                boolean change = false;
                Mat4 model = activeSolid.getModel();
                double step = 0.1;
                
                // Translace pouze pomocí šipek
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
                if (e.getKeyCode() == KeyEvent.VK_PAGE_UP) {
                    model = model.mul(new Mat4Transl(0, 0, step)); change = true;
                }
                if (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
                    model = model.mul(new Mat4Transl(0, 0, -step)); change = true;
                }

                // Rotace - R
                if (e.getKeyCode() == KeyEvent.VK_R) {
                    model = model.mul(new Mat4RotX(0.1)); change = true;
                }

                // Zmenšení a zvětšení - X a C
                if (e.getKeyCode() == KeyEvent.VK_X) {
                    model = model.mul(new Mat4Scale(1.1)); change = true;
                }
                if (e.getKeyCode() == KeyEvent.VK_C) {
                    model = model.mul(new Mat4Scale(0.9)); change = true;
                }

                if (change) {
                    activeSolid.setModel(model);
                    if (activeSolid == light) {
                        lightPos = new Vec3D(model.get(3,0), model.get(3,1), model.get(3,2));
                    }
                }



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

        renderer.setView(camera.getViewMatrix());
        renderer.setProjection(projection);
        
        renderer.setWireframe(wireframe);

        for (Solid solid : solids) {
            if (solid.getShader() instanceof ShaderPhong) {
                ((ShaderPhong) solid.getShader()).setLightP(lightPos);
            }
            renderer.render(solid);
        }
        
        panel.repaint();
    }
}
