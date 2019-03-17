package pgr2;

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

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import pgr2.solids.Cube;
import pgr2.solids.Plate;
import pgr2.solids.Solid;
import pgr2.solids.Sphere;
import pgr2.solids.Spire;
import transforms.Camera;
import transforms.Mat4;
import transforms.Mat4OrthoRH;
import transforms.Mat4PerspRH;
import transforms.Mat4RotX;
import transforms.Mat4RotY;
import transforms.Mat4RotZ;
import transforms.Mat4Scale;
import transforms.Mat4Transl;
import transforms.Vec3D;

public class ZBufferApplication extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private static final Random RANDOM 	= new Random();	
	private static final int FPS 		= 1000 / 30;
	private static final int WIDTH 		= 800;
	private static final int HEIGHT 	= 600;
   
	private JPanel panel;
    private BufferedImage img;
    private Renderer scene;
    private Camera camera;
    private List<Solid> solids;    

    //mouse control    
    private int beginX, beginY;    

    private boolean drawMode = true;
    private boolean drawPersp = true;

    public static void main(String[] args) {
        ZBufferApplication frame = new ZBufferApplication();
        frame.init(WIDTH, HEIGHT);
    }

    private void init(int width, int height) {
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setSize(width, height);
        setTitle("Z Buffer");
        panel = new JPanel();
        add(panel);
        solids = new ArrayList<>();

        scene = new Renderer(img);
        camera = new Camera();
        camera = camera.withPosition(new Vec3D(-6.6, -3.3, 4.5));
        camera = camera.withZenith(-0.549);
        camera = camera.withAzimuth(0.5);
        
        scene.setProjection(
        	new Mat4PerspRH(1, 1 ,1, 100)); 
        
        scene.setProjection(
            new Mat4OrthoRH(10, 1, 5, 100));       

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
                    case KeyEvent.VK_W:
                        camera = camera.forward(0.15);
                        break;
                    case KeyEvent.VK_S:
                        camera = camera.backward(0.15);
                        break;
                    case KeyEvent.VK_A:
                        camera = camera.left(0.15);
                        break;
                    case KeyEvent.VK_D:
                        camera = camera.right(0.15);
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
        setLocationRelativeTo(null);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                draw();
            }
        }, 100, FPS);
    }    
    
	public double randomDouble(double limit) {
		return Math.abs(2.0*limit) * RANDOM.nextDouble() - limit;
	}   
	
    private void initSolids() {
    	//axis
    	//x
    	solids.add(new Cube(1).transform(new Mat4Scale(3.0, 0.05, 0.05).mul(new Mat4Transl(1.5, 0, 0))));
    	solids.add(new Spire().transform(new Mat4RotY(Math.PI / 2.0).mul(new Mat4Scale(0.3).mul(new Mat4Transl(3.0, 0, 0)))));
    	//y   	
    	solids.add(new Cube(1).transform(new Mat4Scale(0.05, 3.0, 0.05).mul(new Mat4Transl(0, 1.5, 0))));
    	solids.add(new Spire().transform(new Mat4RotX(-Math.PI / 2.0).mul(new Mat4Scale(0.3).mul(new Mat4Transl(0, 3.0, 0)))));
    	//z
    	solids.add(new Cube(1).transform(new Mat4Scale(0.05, 0.05, 3.0).mul(new Mat4Transl(0, 0, 1.5))));
    	solids.add(new Spire().transform(new Mat4Scale(0.3).mul(new Mat4Transl(0, 0, 3.0))));   	
    	
    	//bicubic
    	solids.add(new Plate());    	
    	
    	//huge rotating cube:/
        Cube cube = new Cube(1);        
        cube.transform(new Mat4Transl(2, 2, 2));
        cube.setTransformation(
        	new Mat4Transl(-cube.getCentroid().getX(), -cube.getCentroid().getY(), -cube.getCentroid().getZ()).mul(	
			new Mat4RotZ(randomDouble(Math.PI / 100))).mul(
			new Mat4RotY(randomDouble(Math.PI / 100))).mul(
			new Mat4RotX(randomDouble(Math.PI / 100))).mul(
			new Mat4Transl(cube.getCentroid().getX(), cube.getCentroid().getY(), cube.getCentroid().getZ())));               
        solids.add(cube);
        
        //huge rotating cube:/
        Sphere sphere = new Sphere();
        sphere.transform(new Mat4Scale(3.0, 3.0, 3.0).mul(new Mat4Transl(-2, 0, 0)));
        sphere.setTransformation(
			new Mat4RotZ(Math.PI / 100).mul(
			new Mat4RotY(Math.PI / 100)).mul(
			new Mat4RotX(Math.PI / 100))
		);				
        solids.add(sphere);
    }

    private void draw() {
        // clear
    	img.getGraphics().setColor(new Color(0));
        img.getGraphics().fillRect(0, 0, img.getWidth(), img.getHeight());        

		if (this.drawPersp) {
			scene.setProjection(
		        new Mat4PerspRH(1, 1 ,1, 100));						
		} else {
			scene.setProjection((Mat4) new Mat4OrthoRH(10, 10, -10.0, 100.0));		
		}	        
        scene.clear();
        scene.setView(camera.getViewMatrix());
        solids.forEach(s->s.transform()); 
        
        for (Solid solid : solids) {
        	if (this.drawMode) {
        		scene.draw(solid);
        	} else {
        		scene.drawWireFrame(solid);
        	}        	
        }
        
        help(img.getGraphics());
        
        panel.getGraphics().drawImage(img, 0, 0, null);
        panel.paintComponents(getGraphics());
    }
    

    public void help(Graphics g) {
		g.setColor(new Color(0));
		g.setFont(new Font("SansSerif", 0, 12));
		g.drawString("[WSAD], myš - pohyb, pohled", 30, 30);
		String s = new String("");
		if (this.drawMode) {
			s = new String(String.valueOf(s) + "[L] - drátový model, ");
		} else {
			s = new String(String.valueOf(s) + "[L] - vyplněné plochy, ");
		}
		if (this.drawPersp) {
			s = new String(String.valueOf(s) + "[P] - pravoúhlá projekce");
		} else {
			s = new String(String.valueOf(s) + "[P] - perspektivní projekce");
		}
		g.drawString(s, 30, 50);		
		
		g.setColor(new Color(0));
		g.setFont(new Font("SansSerif", 3, 12));
		g.drawString("František Hylmar KPGR2 2019 ", 600, 550);//680, 580);
	}
}
