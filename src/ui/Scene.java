package ui;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.List;

import solids.Primitive;
import solids.Solid;
import transforms.Mat4;
import transforms.Mat4Identity;
import transforms.Point3D;
import transforms.Vec3D;

public class Scene {
	
	private final BufferedImage image;
	private final double[][] zBuffer;
	
	private double wmin = 0.5;	
    private Mat4 model;
    private Mat4 view;
    private Mat4 projection;

	public Scene(BufferedImage image) {
		this.image = image;
		this.zBuffer = new double[width()][height()];
		this.model = new Mat4Identity();
		this.view = new Mat4Identity();
		this.projection = new Mat4Identity();
	}	
	
	public void clear() {
		for (int x = 0; x < width(); ++x) {
			for (int y = 0; y < height(); ++y) {
				this.zBuffer[x][y] = 1;
			}
		}
	}	
	
	public void draw(Solid solid) {
        Mat4 matFinal = model.mul(view).mul(projection);
        List<Integer> indices = solid.getIndices(Primitive.TRIANGLES);
        for (int i = 0; i < indices.size() - 2; i += 3) {
            Point3D p1 = solid.getVertices().get(indices.get(i));
            Point3D p2 = solid.getVertices().get(indices.get(i + 1));
            Point3D p3 = solid.getVertices().get(indices.get(i + 2));
            
            p1 = p1.mul(matFinal);
            p2 = p2.mul(matFinal);
            p3 = p3.mul(matFinal);
            
            renderTriangle(p1, p2, p3, solid.getColorByTriangleVertice(i));
        }
    }

	public void renderTriangle(Point3D pA, Point3D pB, Point3D pC, int color) {		
		drawTriangle(pA, pB, pC, color);
	}

	private void drawTriangle(Point3D pA, Point3D pB, Point3D pC, int color) {		
		Vec3D a = pA.dehomog().get();
		Vec3D b = pB.dehomog().get();
		Vec3D c = pC.dehomog().get();
		
		if (Math.min(Math.min(a.x, b.x), c.x) <= 1.0D && 
			Math.max(Math.max(a.x, b.x), c.x) >= -1.0D &&
			Math.min(Math.min(a.y, b.y), c.y) <= 1.0D && 
			Math.max(Math.max(a.y, b.y), c.y) >= -1.0D &&
			Math.min(Math.min(a.z, b.z), c.z) <= 1.0D && 
			Math.max(Math.max(a.z, b.z), c.z) >= 0.0D) 
		{
			a = a
				.mul(new Vec3D(1, -1, 1))
				.add(new Vec3D(1, +1, 0))
				.mul(new Vec3D((width()-1)/2.0, (height()-1)/2.0, 1.0));
			
			b = b
				.mul(new Vec3D(1, -1, 1))
				.add(new Vec3D(1, +1, 0))
				.mul(new Vec3D((width()-1)/2.0, (height()-1)/2.0, 1.0));
			
			c = c
				.mul(new Vec3D(1, -1, 1))
				.add(new Vec3D(1, +1, 0))
				.mul(new Vec3D((width()-1)/2.0, (height()-1)/2.0, 1.0));			

			Vec3D p;
			if (a.y > b.y) {
				p = a;
				a = b;
				b = p;
			}
			if (b.y > c.y) {
				p = b;
				b = c;
				c = p;
			}
			if (a.y > b.y) {
				p = a;
				a = b;
				b = p;
			}

			double s1;
			double s2;
			double t;
			double x1;
			double x2;
			double z1;
			double z2;
			double z;
			int y;
			int x;
			
			for (y = Math.max((int)a.y+1, 0); (double)y<=Math.min(b.y, (double)(height()-1)); y++) {
				s1 = ((double) y - a.y) / (b.y - a.y);
				s2 = ((double) y - a.y) / (c.y - a.y);
				x1 = a.x * (1.0D - s1) + b.x * s1;
				x2 = a.x * (1.0D - s2) + c.x * s2;
				z1 = a.z * (1.0D - s1) + b.z * s1;
				z2 = a.z * (1.0D - s2) + c.z * s2;
				if (x1 > x2) {
					t = x1;
					x1 = x2;
					x2 = t;
					t = z1;
					z1 = z2;
					z2 = t;
				}

				for (x = Math.max((int) x1 + 1, 0); (double) x <= Math.min(x2, (double) (width() - 1)); ++x) {
					t = ((double) x - x1) / (x2 - x1);
					z = z1 * (1.0D - t) + z2 * t;
					if (zBuffer[x][y] > z && z > 0.0D) {
						zBuffer[x][y] = z;
						image.setRGB(x, y, (new Color(color)).getRGB());
					}
				}
			}

			for (y = Math.max((int) b.y + 1, 0); (double) y <= Math.min(c.y, (double)(height() - 1)); ++y) {
				s1 = ((double) y - b.y) / (c.y - b.y);
				s2 = ((double) y - a.y) / (c.y - a.y);
				x1 = b.x * (1.0D - s1) + c.x * s1;
				x2 = a.x * (1.0D - s2) + c.x * s2;
				z1 = b.z * (1.0D - s1) + c.z * s1;
				z2 = a.z * (1.0D - s2) + c.z * s2;
				if (x1 > x2) {
					t = x1;
					x1 = x2;
					x2 = t;
					t = z1;
					z1 = z2;
					z2 = t;
				}

				for (x = Math.max((int) x1 + 1, 0); (double) x <= Math.min(x2, (double)(width()-1)); ++x) {
					t = ((double) x - x1) / (x2 - x1);
					z = z1 * (1.0D - t) + z2 * t;
					if (zBuffer[x][y] > z && z > 0.0D) {
						zBuffer[x][y] = z;
						image.setRGB(x, y, (new Color(color)).getRGB());
					}
				}
			}
		}
	}
	
	//getters
	public int width() {
		return image.getWidth();
	}
	
	public int height() {
		return image.getHeight();
	}

	public double getWmin() {
		return this.wmin;
	}

	public void setWmin(double wmin) {
		this.wmin = wmin;
	}
	
	public Mat4 getModel() {
		return model;
	}

	public void setModel(Mat4 model) {
		this.model = model;
	}

	public Mat4 getView() {
		return view;
	}

	public void setView(Mat4 view) {
		this.view = view;
	}

	public Mat4 getProjection() {
		return projection;
	}

	public void setProjection(Mat4 projection) {
		this.projection = projection;
	}
}