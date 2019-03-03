package ui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import solids.Axis;
import solids.Cube;
import solids.Solid;
import transforms.*;
import utils.Transformer;

public class PgrfWireFrame extends JFrame {

    static int FPS = 1000 / 30;
    static int width = 800;
    static int height = 600;
    private JPanel panel;
    private BufferedImage img;

    private Transformer transformer;
    private Camera camera;
    private List<Solid> solids;
    
    private Mat4 rot;

    private int beginX, beginY; // ovládání myší

    public static void main(String[] args) {
        PgrfWireFrame frame = new PgrfWireFrame();
        frame.init(width, height);
    }

    private void init(int width, int height) {
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // nastavení frame
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setSize(width, height);
        setTitle("Drátový model");
        panel = new JPanel();
        add(panel);
        solids = new ArrayList<>();

        transformer = new Transformer(img);
        camera = new Camera();
        camera = camera.withPosition(new Vec3D(-6.6, -3.3,4.5));
        camera = camera.withZenith(-0.549);
        camera = camera.withAzimuth(0.5);
        transformer.setProjection(
                new Mat4PerspRH(1,1,1,100));

        // vytvoření objektů
        initSolids();

        // listeners
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                beginX = e.getX();
                beginY = e.getY();
                super.mousePressed(e);
            }
        });
        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                camera = camera.addAzimuth((Math.PI / 1000) * (beginX - e.getX()));
                camera = camera.addZenith((Math.PI / 1000) * (beginY - e.getY()));
                beginX = e.getX();
                beginY = e.getY();
                super.mouseDragged(e);
            }
        });
        addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        camera = camera.forward(0.1);
                        break;
                    case KeyEvent.VK_DOWN:
                        camera = camera.backward(0.1);
                        break;
                    case KeyEvent.VK_LEFT:
                        camera = camera.left(0.1);
                        break;
                    case KeyEvent.VK_RIGHT:
                        camera = camera.right(0.1);
                        break;

                }
                super.keyReleased(e);
            }
        });

        // timer pro refresh draw()
        setLocationRelativeTo(null);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                draw();
            }
        }, 100, FPS);

    }

    private Mat4 rotation(Solid solid) {
		return
			new Mat4Transl(-solid.getCentroid().getX(), -solid.getCentroid().getY(), -solid.getCentroid().getZ()).mul(	
			new Mat4RotZ(randomDouble(Math.PI / 100))).mul(
			new Mat4RotY(randomDouble(Math.PI / 100))).mul(
			new Mat4RotX(randomDouble(Math.PI / 100))).mul(
			new Mat4Transl(solid.getCentroid().getX(), solid.getCentroid().getY(), solid.getCentroid().getZ()));
	}
    
    private static final Random random = new Random();	
	public static double randomDouble(double limit) {
		return Math.abs(2.0*limit) * random.nextDouble() - limit;
	}
    
    private void initSolids() {
        // todo více objektů
        solids.add(new Axis());
        
        Cube cube = new Cube(1);
        rot = rotation(cube);
        
        solids.add(cube);
        
        
        
//        int count = 5;
//        for (int i = 0; i < count; i++) {
//            Cube cube = new Cube(1);
//            for (int j = 0; j < cube.getVertices().size(); j++) {
//                Point3D p = cube.getVertices().get(j);
//                Point3D n = p
//                        .mul(new Mat4Transl(0,2,0))
//                        .mul(new Mat4RotZ((double)
//                                i * 2d * Math.PI / (double) count));
//                cube.getVertices().set(j, n);
//            }
//            solids.add(cube);
//        }

    }

    private void draw() {
        // clear
        img.getGraphics().fillRect(0, 0, img.getWidth(), img.getHeight());
        // TODO transformer - clear zBuffer !

        // transformer.setModel() todo
        transformer.clear();
        transformer.setView(camera.getViewMatrix());

        solids.get(1).transform(rot);
        
        for (Solid solid : solids) {
            // TODO if na drát / model
            transformer.drawFilledFrame(solid);
            transformer.drawWireFrame(solid); // výkres solids
        }

        panel.getGraphics().drawImage(img, 0, 0, null);
        panel.paintComponents(getGraphics());
    }
}
