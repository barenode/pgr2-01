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
	
	public void drawWireFrame(Solid solid) {
		Mat4 matFinal = model.mul(view).mul(projection);
		List<Integer> indices = solid.getIndices(Primitive.LINES);

		for (int i = 0; i < indices.size(); i += 2) {

			Point3D p1 = solid.getVertices().get(indices.get(i));
            Point3D p2 = solid.getVertices().get(indices.get(i + 1));
            
            p1 = p1.mul(matFinal);
            p2 = p2.mul(matFinal);
            
            renderLine(p1, p2, solid.getColorByEdge(i));
        }
    }

	public void renderTriangle(Point3D a, Point3D b, Point3D c, int color) {
		Point3D p;
		if (a.w > b.w) {
			p = a; a = b; b = p;			
		}
		if (b.w > c.w) {
			p = b; b = c; c = p;		
		}
		if (a.w > b.w) {
			p = a; a = b; b = p;						
		}
		if (c.w >= this.wmin) {
			Point3D nva;
			Point3D nvb;
			double t;
			if (b.w < this.wmin) {
				t = (this.wmin - c.w) / (a.w - c.w);
				nva = a.mul(t).add(c.mul(1.0D - t));
				t = (this.wmin - c.w) / (b.w - c.w);
				nvb = b.mul(t).add(c.mul(1.0D - t));
				this.drawTriangle(nva, nvb, c, color);
			} else if (a.w < this.wmin) {
				t = (this.wmin - b.w) / (a.w - b.w);
				nva = a.mul(t).add(b.mul(1.0D - t));
				t = (this.wmin - c.w) / (a.w - c.w);
				nvb = a.mul(t).add(c.mul(1.0D - t));
				this.drawTriangle(nva, nvb, c, color);
				this.drawTriangle(nva, b, c, color);
			} else {
				this.drawTriangle(a, b, c, color);
			}
		}
		//drawTriangle(pA, pB, pC, color, texture);
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
			
			double u1;
			double u2;
			double u;
			double v1;
			double v2;
			double v;
			
			int y;
			int x;
			
			for (y = Math.max((int)a.y+1, 0); (double)y<=Math.min(b.y, (double)(height()-1)); y++) {
				s1 = ((double) y - a.y) / (b.y - a.y);
				s2 = ((double) y - a.y) / (c.y - a.y);
				
				x1 = a.x * (1.0D - s1) + b.x * s1;
				x2 = a.x * (1.0D - s2) + c.x * s2;
				z1 = a.z * (1.0D - s1) + b.z * s1;
				z2 = a.z * (1.0D - s2) + c.z * s2;
				
				u1 = a.u * (1.0D - s1) + b.u * s1;
				u2 = a.u * (1.0D - s2) + c.u * s2;
				v1 = a.v * (1.0D - s1) + b.v * s1;
				v2 = a.v * (1.0D - s2) + c.v * s2;
				
				if (x1 > x2) {
					t = x1; x1 = x2; x2 = t;					
					t = z1; z1 = z2; z2 = t;
					t = u1; u1 = u2; u2 = t;
					t = v1; v1 = v2; v2 = t;
				}

				for (x = Math.max((int) x1 + 1, 0); (double) x <= Math.min(x2, (double) (width() - 1)); ++x) {
					t = ((double) x - x1) / (x2 - x1);
					z = z1 * (1.0D - t) + z2 * t;					
					
					if (zBuffer[x][y] > z && z > 0.0D) {
						u = u1 * (1.0D - t) + u2 * t;
						v = v1 * (1.0D - t) + v2 * t;
						zBuffer[x][y] = z;					
						image.setRGB(x, y, (new Color(color)).getRGB());
						//image.setRGB(x, y, texture.getRGB((int)u, (int)v));
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
				
				u1 = b.u * (1.0D - s1) + c.u * s1;
				u2 = a.u * (1.0D - s2) + c.u * s2;
				v1 = b.v * (1.0D - s1) + c.v * s1;
				v2 = a.v * (1.0D - s2) + c.v * s2;
				
				if (x1 > x2) {
					t = x1; x1 = x2; x2 = t;					
					t = z1; z1 = z2; z2 = t;
					t = u1; u1 = u2; u2 = t;
					t = v1; v1 = v2; v2 = t;
				}

				for (x = Math.max((int) x1 + 1, 0); (double) x <= Math.min(x2, (double)(width()-1)); ++x) {
					t = ((double) x - x1) / (x2 - x1);
					z = z1 * (1.0D - t) + z2 * t;
					if (zBuffer[x][y] > z && z > 0.0D) {
						u = u1 * (1.0D - t) + u2 * t;
						v = v1 * (1.0D - t) + v2 * t;
						zBuffer[x][y] = z;
						image.setRGB(x, y, (new Color(color)).getRGB());
						//image.setRGB(x, y, texture.getRGB((int)u, (int)v));
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
	
	//lines
	private void drawLine(final Point3D pA, final Point3D pB, final int color) {
		Vec3D a = pA.dehomog().get();
		Vec3D b = pB.dehomog().get();
		if (Math.min(a.x, b.x) > 1.0 || Math.max(a.x, b.x) < -1.0 || Math.min(a.y, b.y) > 1.0
				|| Math.max(a.y, b.y) < -1.0 || Math.min(a.z, b.z) > 1.0 || Math.max(a.z, b.z) < 0.0) {
			return;
		}
		a = a.mul(new Vec3D(1.0, -1.0, 1.0)).add(new Vec3D(1.0, 1.0, 0.0))
				.mul(new Vec3D((double) ((width() - 1) / 2), (double) ((height() - 1) / 2), 1.0));
		b = b.mul(new Vec3D(1.0, -1.0, 1.0)).add(new Vec3D(1.0, 1.0, 0.0))
				.mul(new Vec3D((double) ((width() - 1) / 2), (double) ((height() - 1) / 2), 1.0));
		if (Math.abs(a.y - b.y) > Math.abs(a.x - b.x)) {
			if (a.y > b.y) {
				final Vec3D p = a;
				a = b;
				b = p;
			}
			for (int y = Math.max((int) a.y + 1, 0); y <= Math.min(b.y, height() - 1); ++y) {
				final double s = (y - a.y) / (b.y - a.y);
				final int x = (int) (a.x * (1.0 - s) + b.x * s) + 1;
				final double z = a.z * (1.0 - s) + b.z * s;
				if (x > 0 && x < width() - 1 && this.zBuffer[x][y] > z) {
					this.zBuffer[x][y] = z;
					image.setRGB(x, y, new Color(color).getRGB());
				}
			}
		} else {
			if (a.x > b.x) {
				final Vec3D p = a;
				a = b;
				b = p;
			}
			for (int x2 = Math.max((int) a.x + 1, 0); x2 <= Math.min(b.x, width() - 1); ++x2) {
				final double s = (x2 - a.x) / (b.x - a.x);
				final int y2 = (int) (a.y * (1.0 - s) + b.y * s) + 1;
				final double z = a.z * (1.0 - s) + b.z * s;
				if (y2 > 0 && y2 < height() - 1 && this.zBuffer[x2][y2] > z) {
					this.zBuffer[x2][y2] = z;
					image.setRGB(x2, y2, new Color(color).getRGB());
				}
			}
		}
	}

	public void renderLine(Point3D pA, Point3D pB, final int color) {
		if (pA.w > pB.w) {
			final Point3D p = pA;
			pA = pB;
			pB = p;
		}
		if (pB.w < this.wmin) {
			return;
		}
		if (pA.w < this.wmin) {
			final double t = (this.wmin - pB.w) / (pA.w - pB.w);
			final Point3D nv = pA.mul(t).add(pB.mul(1.0 - t));
			this.drawLine(pB, nv, color);
			return;
		}
		this.drawLine(pA, pB, color);
	}
}