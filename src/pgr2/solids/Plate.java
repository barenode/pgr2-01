package pgr2.solids;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import transforms.Bicubic;
import transforms.Cubic;
import transforms.Point3D;

public class Plate extends SolidBase {	
	
	private final int startX = -3;
	private final int startY = -3;
	private final int step = 3;
	private final double amplitude = 2*Math.PI/3;		
	
	public Plate() {
		super();
		List<Point3D> points = new ArrayList<>();
		int x = startX;		
		for (int i=0; i<4; i++) {
			int y = startY;
			for (int j=0; j<4; j++) {
				points.add(new Point3D(x, y, -3*Math.cos(i*j*amplitude)));				
				y+=step;
			}
			x+=step;
		}		
		Bicubic cubic = new Bicubic(Cubic.BEZIER, points.toArray(new Point3D[16]));			
		int lastIndex = 0;
		List<Point3D> last = null;
		for (double i=0.0; i<=4.0; i+=0.1) {
			List<Point3D> current = new ArrayList<>();
			for (double j=0.0; j<=4.0; j+=0.1) {
				current.add(cubic.compute(i, j));				
			}
			vertices.addAll(current);			
			for (int k=1; k<current.size(); k++) {
											
			}
			
			for (int k=0; k<current.size(); k++) {
				if (last!=null) {
					indicesLine.add(lastIndex+k); 
					indicesLine.add(lastIndex-last.size()+k);					
				}
				if (k>0) {
					indicesLine.add(lastIndex+k-1); 
					indicesLine.add(lastIndex+k);	
					if (last!=null) {
						indicesLine.add(lastIndex+k); 
						indicesLine.add(lastIndex-last.size()+k-1);
						
						//triangle
						indicesTriangle.add(lastIndex+k); 
						indicesTriangle.add(lastIndex+k-1); 
						indicesTriangle.add(lastIndex-last.size()+k-1); 
						
						indicesTriangle.add(lastIndex+k); 
						indicesTriangle.add(lastIndex-last.size()+k); 
						indicesTriangle.add(lastIndex-last.size()+k-1); 
					}
				}
			}

			lastIndex+=current.size();		
			last = current;
		}	
	}
	
	@Override
	public int getColorByTriangleVertice(int index) {
		return getColor(index%4).getRGB();
	}  
	
	private Color getColor(int index) {
		switch (index) {
			case 0 : return Color.BLUE;
			case 1 : return Color.CYAN;
			case 2 : return Color.YELLOW;
			case 3 : return Color.GREEN;
			case 4 : return Color.RED; 
			case 5 : return Color.ORANGE;
			default : throw new IllegalArgumentException("Unsupported inbdex: " + index);
			
		}
	}
	
	@Override
	public Point3D getCentroid() {
		throw new UnsupportedOperationException();
	}	
}
