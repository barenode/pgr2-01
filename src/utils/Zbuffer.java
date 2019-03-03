package utils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import transforms.Point3D;
import transforms.Vec3D;

public class Zbuffer {
	
	protected BufferedImage image;
	protected double[][] zBuffer;
	protected double wmin = 0.5;	

	public Zbuffer(BufferedImage image) {
		this.image = image;
		this.zBuffer = new double[width()][height()];
	}
	
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
	
	public void clear() {
		for (int x = 0; x < width(); ++x) {
			for (int y = 0; y < height(); ++y) {
				this.zBuffer[x][y] = 1;
			}
		}
	}
	
	public void renderTriangle(Point3D pA, Point3D pB, Point3D pC, int color) {
//		Point3D p;
//		if (pA.w > pB.w) {
//			p = pA;
//			pA = pB;
//			pB = p;
//		}
//
//		if (pB.w > pC.w) {
//			p = pB;
//			pB = pC;
//			pC = p;
//		}
//
//		if (pA.w > pB.w) {
//			p = pA;
//			pA = pB;
//			pB = p;
//		}
//
//		if (pC.w >= this.wmin) {
//			Point3D nva;
//			Point3D nvb;
//			double t;
//			if (pB.w < this.wmin) {
//				t = (this.wmin - pC.w) / (pA.w - pC.w);
//				nva = pA.mul(t).add(pC.mul(1.0D - t));
//				t = (this.wmin - pC.w) / (pB.w - pC.w);
//				nvb = pB.mul(t).add(pC.mul(1.0D - t));
//				this.drawTriangle(nva, nvb, pC, color, this.mode);
//			} else if (pA.w < this.wmin) {
//				t = (this.wmin - pB.w) / (pA.w - pB.w);
//				nva = pA.mul(t).add(pB.mul(1.0D - t));
//				t = (this.wmin - pC.w) / (pA.w - pC.w);
//				nvb = pA.mul(t).add(pC.mul(1.0D - t));
//				this.drawTriangle(nva, nvb, pC, color, this.mode);
//				this.drawTriangle(nva, pB, pC, color, this.mode);
//			} else {
//				this.drawTriangle(pA, pB, pC, color, this.mode);
//			}
//		}
		
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

//	void drawMyLine(Graphics g, double x1, double y1, double x2, double y2, int color) {
//		((Graphics2D) g).setStroke(new BasicStroke(3.0F));
//		g.setColor(new Color(color));
//		g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
//		((Graphics2D) g).setStroke(new BasicStroke(1.0F));
//		g.setColor(new Color(11184810));
//		int size = 3;
//		g.fillOval((int) x1 - size, (int) y1 - size, 2 * size, 2 * size);
//		g.fillOval((int) x2 - size, (int) y2 - size, 2 * size, 2 * size);
//	}
//
//
//
//	private void drawLine(Point3D pA, Point3D pB, int color) {
//		Vec3D a = pA.dehomog().get();
//		Vec3D b = pB.dehomog().get();
//		if (Math.min(a.x, b.x) <= 1.0D && Math.max(a.x, b.x) >= -1.0D && Math.min(a.y, b.y) <= 1.0D
//				&& Math.max(a.y, b.y) >= -1.0D && Math.min(a.z, b.z) <= 1.0D && Math.max(a.z, b.z) >= 0.0D) {
//			a = a.mul(new Vec3D(1.0D, -1.0D, 1.0D)).add(new Vec3D(1.0D, 1.0D, 0.0D))
//					.mul(new Vec3D((double) ((this.w - 1) / 2), (double) ((this.h - 1) / 2), 1.0D));
//			b = b.mul(new Vec3D(1.0D, -1.0D, 1.0D)).add(new Vec3D(1.0D, 1.0D, 0.0D))
//					.mul(new Vec3D((double) ((this.w - 1) / 2), (double) ((this.h - 1) / 2), 1.0D));
//			Vec3D p;
//			double s;
//			double z;
//			int y;
//			int x;
//			if (Math.abs(a.y - b.y) > Math.abs(a.x - b.x)) {
//				if (a.y > b.y) {
//					p = a;
//					a = b;
//					b = p;
//				}
//
//				for (y = Math.max((int) a.y + 1, 0); (double) y <= Math.min(b.y, (double) (this.h - 1)); ++y) {
//					s = ((double) y - a.y) / (b.y - a.y);
//					x = (int) (a.x * (1.0D - s) + b.x * s) + 1;
//					z = a.z * (1.0D - s) + b.z * s;
//					if (x > 0 && x < this.w - 1 && this.zBuffer[x][y] > z) {
//						this.zBuffer[x][y] = z;
//						this.img.setRGB(x, y, (new Color(color)).getRGB());
//					}
//				}
//			} else {
//				if (a.x > b.x) {
//					p = a;
//					a = b;
//					b = p;
//				}
//
//				for (y = Math.max((int) a.x + 1, 0); (double) y <= Math.min(b.x, (double) (this.w - 1)); ++y) {
//					s = ((double) y - a.x) / (b.x - a.x);
//					x = (int) (a.y * (1.0D - s) + b.y * s) + 1;
//					z = a.z * (1.0D - s) + b.z * s;
//					if (x > 0 && x < this.h - 1 && this.zBuffer[y][x] > z) {
//						this.zBuffer[y][x] = z;
//						this.img.setRGB(y, x, (new Color(color)).getRGB());
//					}
//				}
//			}
//
//		}
//	}
//
//	public void renderLine(Point3D pA, Point3D pB, int color) {
//		if (pA.w > pB.w) {
//			Point3D p = pA;
//			pA = pB;
//			pB = p;
//		}
//
//		if (pB.w >= this.wmin) {
//			if (pA.w < this.wmin) {
//				double t = (this.wmin - pB.w) / (pA.w - pB.w);
//				Point3D nv = pA.mul(t).add(pB.mul(1.0D - t));
//				this.drawLine(pB, nv, color);
//			} else {
//				this.drawLine(pA, pB, color);
//			}
//		}
//	}
}