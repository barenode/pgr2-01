package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
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

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import solids.Axis;
import solids.AxisWithArrows;
import solids.Cube;
import solids.Curve;
import solids.Plate;
import solids.Solid;
import solids.Spire;
import solids.Triangel;
import transforms.*;
import utils.Transformer;

public class App extends JFrame {

    static int FPS = 1000 / 30;
    static int width = 800;
    static int height = 600;
    private JPanel panel;
    private BufferedImage img;

//    private Transformer transformer;
    private Scene scene;
    private Camera camera;
    private List<Solid> solids;
    
    private Mat4 rot;

    private int beginX, beginY; // ovládání myší

    public static void main(String[] args) {
        App frame = new App();
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

        scene = new Scene(img);
        camera = new Camera();
        camera = camera.withPosition(new Vec3D(-6.6, -3.3, 4.5));
        camera = camera.withZenith(-0.549);
        camera = camera.withAzimuth(0.5);
        
        scene.setProjection(
        	new Mat4PerspRH(1, 1 ,1, 100)); 
        
        scene.setProjection(
            new Mat4OrthoRH(10, 1, 5, 100));
        
//        
//		if (this.drawPersp) {
//			scene.setProjection(
//		        new Mat4PerspRH(1, 1 ,1, 100));						
//		} else {
//			scene.setProjection((Mat4) new Mat4OrthoRH(
//				(double)(this.img.getWidth() / 5),
//				(double)(this.img.getHeight() / 10), 
//				-10.0, 
//				100.0));
//		}
        
//        if (this.drawPersp) {
//			this.pTrans = (Mat4) new Mat4PerspRH(0.7853981633974483,
//					(double) (this.img.getHeight() / this.img.getWidth()), 0.01, 100.0);
//		} else {
//			this.pTrans = (Mat4) new Mat4OrthoRH((double) (this.img.getWidth() / 10),
//					(double) (this.img.getHeight() / 10), -10.0, 100.0);
//		}

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
                        
                        
                    case KeyEvent.VK_P: {
                    	drawPersp = !drawPersp;
						break;
					}
                    case KeyEvent.VK_L: {
                    	drawMode = !drawMode;
						break;
					}

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
//		return 		
//				new Mat4Transl(-solid.getCentroid().getX(), -solid.getCentroid().getY(), -solid.getCentroid().getZ()).mul(	
//				new Mat4RotZ(randomDouble(Math.PI / 100))).mul(
//				new Mat4RotY(randomDouble(Math.PI / 100))).mul(
//				new Mat4RotX(randomDouble(Math.PI / 100))).mul(
//				new Mat4Transl(solid.getCentroid().getX(), solid.getCentroid().getY(), solid.getCentroid().getZ()));
    	
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
	
	private Solid axisX;
	private Solid axisY;
	private Solid axisZ;
	
	private Solid spireZ;
	private Solid spireX;
	private Solid spireY;
	
    private void initSolids() {
    	BufferedImage texture = loadTexture("texture-metal");
    	axisX = new Cube(1, texture);
    	axisX.transform(new Mat4Scale(3.0, 0.05, 0.05).mul(new Mat4Transl(1.5, 0, 0)));
    	
    	axisY = new Cube(1, texture);
    	axisY.transform(new Mat4Scale(0.05, 3.0, 0.05).mul(new Mat4Transl(0, 1.5, 0)));
    	
    	axisZ = new Cube(1, texture);
    	axisZ.transform(new Mat4Scale(0.05, 0.05, 3.0).mul(new Mat4Transl(0, 0, 1.5)));
    	
    	spireZ = new Spire();
    	spireZ.transform(new Mat4Scale(0.3).mul(new Mat4Transl(0, 0, 3.0)));
    	
    	spireX = new Spire();
    	spireX.transform(new Mat4RotY(Math.PI / 2.0).mul(new Mat4Scale(0.3).mul(new Mat4Transl(3.0, 0, 0))));
    	
    	spireY = new Spire();
    	spireY.transform(new Mat4RotX(-Math.PI / 2.0).mul(new Mat4Scale(0.3).mul(new Mat4Transl(0, 3.0, 0))));
    	
    	
    	solids.add(new Plate());
    	
    	//Triangel triangel = new Triangel(1, loadTexture("texture-metal"));
    	
        Cube cube = new Cube(1, texture);
        cube.transform(new Mat4Transl(2, 2, 2));
        
        rot = rotation(cube);
        
        solids.add(cube);
        
        coons();
        
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
    
	
	private BufferedImage loadTexture(String name) {
		try {
			return ImageIO.read(Scene.class.getResourceAsStream("/" + name + ".jpg"));
		} catch (Exception e) {
			throw new IllegalStateException("Unable to load texture: " + e.getMessage(), e);
		}
	}

    private void draw() {
        // clear
    	img.getGraphics().setColor(new Color(0));
        img.getGraphics().fillRect(0, 0, img.getWidth(), img.getHeight());
        help(img.getGraphics());

		if (this.drawPersp) {
			scene.setProjection(
		        new Mat4PerspRH(1, 1 ,1, 100));						
		} else {
			scene.setProjection((Mat4) new Mat4OrthoRH(
//				(double)(this.img.getWidth() / 10),
//				(double)(this.img.getHeight() / 10),
					10,10,
				-10.0, 100.0));
		}
		
        // transformer.setModel() todo
        scene.clear();
        scene.setView(camera.getViewMatrix());

        //solids.forEach(s->s.transform(rot));
        
        scene.drawWireFrame(axisX);
        scene.drawWireFrame(axisY); 
        scene.drawWireFrame(axisZ); 
        scene.drawWireFrame(spireZ); 
        scene.drawWireFrame(spireX); 
        scene.drawWireFrame(spireY); 
        
        for (Solid solid : solids) {
        	if (this.drawMode) {
        		scene.draw(solid);
        	} else {
        		scene.drawWireFrame(solid); // výkres solids	
        	}        	
        }

        panel.getGraphics().drawImage(img, 0, 0, null);
        panel.paintComponents(getGraphics());
    }
    
    private boolean drawAxis;
    private boolean drawMode;
    private boolean drawPersp = true;
    public void help(Graphics g) {		
		g.setColor(new Color(16777215));
		g.fillRect(30, 20, 400, 120);
		g.setColor(new Color(0));
		g.setFont(new Font("SansSerif", 1, 12));
		g.drawString("ZBuffer renderer", 50, 50);
		g.setFont(new Font("SansSerif", 0, 12));
		g.drawString("[WASD][Ctrl,Shift], mouse - move, view", 70, 70);
		String s = new String("");
		if (this.drawAxis) {
			s = new String(String.valueOf(s) + "[O] - axes, ");
		} else {
			s = new String(String.valueOf(s) + "[o] - axes, ");
		}
		if (this.drawMode) {
			s = new String(String.valueOf(s) + "[L] - line, ");
		} else {
			s = new String(String.valueOf(s) + "[l] - line, ");
		}
		if (this.drawPersp) {
			s = new String(String.valueOf(s) + "[P] - perspective, ");
		} else {
			s = new String(String.valueOf(s) + "[p] - perspective, ");
		}
		g.drawString(s, 70, 90);
		g.drawString("[C] - reset, [1,2] - show objects, [H]elp", 70, 110);
		g.drawString("[space][R/F] - 1st/3rd person, distance", 70, 130);		
	}
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    private Curve curve(Mat4 baseMat, Point3D[] points, Color color) {
		Cubic cubic = new Cubic(baseMat, points);
		List<Point3D> result = new ArrayList<>();
		for (double d=0.0; d<=2.0; d+=0.01) {
			result.add(cubic.compute(d));
		}
		return new Curve(result, color);
	}
    
    private void coons() {
		List<Point3D> points = new ArrayList<>();
		points.add(new Point3D(0, -3, -2));
		points.add(new Point3D(0, 0, -1));
		points.add(new Point3D(0, 3, -2));
		points.add(new Point3D(0, 6, -1));
		
		Color color = Color.RED;
		
    	solids.add(curve(Cubic.COONS, new Point3D[]{
			points.get(0),
			points.get(0),
			points.get(0),
			points.get(1)
		}, color));		
    	solids.add(curve(Cubic.COONS, new Point3D[]{
			points.get(0),
			points.get(0),
			points.get(1),
			points.get(2)
    	}, color));				
    	solids.add(curve(Cubic.COONS, new Point3D[]{
			points.get(0),
			points.get(1),
			points.get(2),
			points.get(3)
    	}, color));		
    	solids.add(curve(Cubic.COONS, new Point3D[]{
			points.get(1),
			points.get(2),
			points.get(3),
			points.get(3)
    	}, color));		
    	solids.add(curve(Cubic.COONS, new Point3D[]{
			points.get(2),
			points.get(3),
			points.get(3),
			points.get(3)
    	}, color));		
    	
    	points = new ArrayList<>();
		points.add(new Point3D(1, -3, -2));
		points.add(new Point3D(1, 0, -1));
		points.add(new Point3D(1, 3, -2));
		points.add(new Point3D(1, 6, -1));
		
		color = Color.GREEN;
    	solids.add(curve(Cubic.COONS, new Point3D[]{
			points.get(0),
			points.get(0),
			points.get(0),
			points.get(1)
    	}, color));	
    	solids.add(curve(Cubic.COONS, new Point3D[]{
			points.get(0),
			points.get(0),
			points.get(1),
			points.get(2)
    	}, color));			
    	solids.add(curve(Cubic.COONS, new Point3D[]{
			points.get(0),
			points.get(1),
			points.get(2),
			points.get(3)
    	}, color));	
    	solids.add(curve(Cubic.COONS, new Point3D[]{
			points.get(1),
			points.get(2),
			points.get(3),
			points.get(3)
    	}, color));	
    	solids.add(curve(Cubic.COONS, new Point3D[]{
			points.get(2),
			points.get(3),
			points.get(3),
			points.get(3)
    	}, color));	
    	
    	
    	points = new ArrayList<>();
    	points.add(new Point3D(2, -3, -2));
		points.add(new Point3D(2, 0, -1));
		points.add(new Point3D(2, 3, -2));
		points.add(new Point3D(2, 6, -1));
		
		color = Color.BLUE;
    	solids.add(curve(Cubic.COONS, new Point3D[]{
			points.get(0),
			points.get(0),
			points.get(0),
			points.get(1)
    	}, color));			
    	solids.add(curve(Cubic.COONS, new Point3D[]{
			points.get(0),
			points.get(0),
			points.get(1),
			points.get(2)
    	}, color));			
//    	solids.add(curve(Cubic.COONS, new Point3D[]{
//			points.get(0),
//			points.get(1),
//			points.get(2),
//			points.get(3)
//		}));
//    	solids.add(curve(Cubic.COONS, new Point3D[]{
//			points.get(1),
//			points.get(2),
//			points.get(3),
//			points.get(3)
//		}));
//    	solids.add(curve(Cubic.COONS, new Point3D[]{
//			points.get(2),
//			points.get(3),
//			points.get(3),
//			points.get(3)
//		}));
    	
    	
    	points = new ArrayList<>();
    	points.add(new Point3D(3, -3, -2));
		points.add(new Point3D(3, 0, -1));
		points.add(new Point3D(3, 3, -2));
		points.add(new Point3D(3, 6, -1));
		
		color = Color.YELLOW;
    	solids.add(curve(Cubic.COONS, new Point3D[]{
			points.get(0),
			points.get(0),
			points.get(0),
			points.get(1)
    	}, color));			

	}
}
