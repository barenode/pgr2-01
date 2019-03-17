package pgr2.solids;

import java.awt.Color;

import transforms.Point3D;

public class Sphere extends SolidBase {	
	public static final double SIN_60 = Math.sqrt(3.0)/2;	
	
	public Sphere() {
		super();
		
		//0-11
		addCircle(0.5, 0.0);
		//12-23
		addCircle(0.5*SIN_60, 0.25);
		//24-35
		addCircle(0.5*SIN_60, -0.25);
		//36-47
		addCircle(0.25, 0.5*SIN_60);
		//48-59
		addCircle(0.25, -0.5*SIN_60);
		
		vertices.add(new Point3D(0.0, 0.0, 0.5)); 
		vertices.add(new Point3D(0.0, 0.0, -0.5)); 
		
		connectPoints(0, 11, 12);	
		indicesTriangle.add(23); indicesTriangle.add(12); indicesTriangle.add(11); 
		indicesTriangle.add(12); indicesTriangle.add(11); indicesTriangle.add(0); 
		
		connectPoints(0, 11, 24);
		indicesTriangle.add(35); indicesTriangle.add(24); indicesTriangle.add(11); 
		indicesTriangle.add(24); indicesTriangle.add(11); indicesTriangle.add(0); 
		
		connectPoints(12, 23, 24);
		indicesTriangle.add(47); indicesTriangle.add(23); indicesTriangle.add(36); 
		indicesTriangle.add(23); indicesTriangle.add(36); indicesTriangle.add(12); 
		
		connectPoints(24, 35, 24);
		indicesTriangle.add(59); indicesTriangle.add(24); indicesTriangle.add(35); 
		indicesTriangle.add(24); indicesTriangle.add(59); indicesTriangle.add(48); 
		
		connectWithPoint(36, 47, 60);
		indicesTriangle.add(47); indicesTriangle.add(36); indicesTriangle.add(60); 
		
		connectWithPoint(48, 59, 61);
		indicesTriangle.add(59); indicesTriangle.add(48); indicesTriangle.add(61); 
		
		vertices.add(new Point3D());		
	}	
	
	@Override
	public Point3D getCentroid() {
		return vertices.get(vertices.size()-1);
	}
	
	protected void addCircle(double radius, double z) {
		int startIndex = vertices.size();
		
		vertices.add(new Point3D(radius, 0.0, z));    
		vertices.add(new Point3D(radius*SIN_60, radius*0.5, z));    
		vertices.add(new Point3D(radius*0.5, radius*SIN_60, z));   
		vertices.add(new Point3D(0.0, radius, z));  
		
		vertices.add(new Point3D(-radius*0.5, radius*SIN_60, z));
		vertices.add(new Point3D(-radius*SIN_60, radius*0.5, z));   
		vertices.add(new Point3D(-radius, 0.0, z));   
		
		vertices.add(new Point3D(-radius*SIN_60, -radius*0.5, z));   
		vertices.add(new Point3D(-radius*0.5, -radius*SIN_60, z));
		vertices.add(new Point3D(0.0, -radius, z));  
		
		vertices.add(new Point3D(radius*0.5, -radius*SIN_60, z));
		vertices.add(new Point3D(radius*SIN_60, -radius*0.5, z));  		
		
		indicesLine.add(startIndex+0); indicesLine.add(startIndex+1);
        indicesLine.add(startIndex+1); indicesLine.add(startIndex+2);
        indicesLine.add(startIndex+2); indicesLine.add(startIndex+3);
        indicesLine.add(startIndex+3); indicesLine.add(startIndex+4);
        indicesLine.add(startIndex+4); indicesLine.add(startIndex+5);
        indicesLine.add(startIndex+5); indicesLine.add(startIndex+6);
        indicesLine.add(startIndex+6); indicesLine.add(startIndex+7);
        indicesLine.add(startIndex+7); indicesLine.add(startIndex+8);
        indicesLine.add(startIndex+8); indicesLine.add(startIndex+9);
        indicesLine.add(startIndex+9); indicesLine.add(startIndex+10);
        indicesLine.add(startIndex+10); indicesLine.add(startIndex+11);
        indicesLine.add(startIndex+11); indicesLine.add(startIndex+0);
	}
	
	protected void connectPoints(int startIndex, int endIndex, int offset) {
		for (int i=startIndex; i<=endIndex; i++) {
			indicesLine.add(i); indicesLine.add(i+offset);
			if (i>startIndex) {
				indicesTriangle.add(i+offset-1); indicesTriangle.add(i+offset); indicesTriangle.add(i-1); 
				indicesTriangle.add(i+offset); indicesTriangle.add(i); indicesTriangle.add(i-1); 
			}
		}
	}
	
	protected void connectWithPoint(int startIndex, int endIndex, int pointIndex) {
		for (int i=startIndex; i<=endIndex; i++) {
			indicesLine.add(i); indicesLine.add(pointIndex);
			if (i>startIndex) {
				indicesTriangle.add(i-1); indicesTriangle.add(i); indicesTriangle.add(pointIndex); 
			}
		}
	}
	
	@Override
	public int getColorByTriangleVertice(int index) {
		return getColor(index%4).getRGB();
	}  
	
	private Color getColor(int index) {
		switch (index) {
			case 0 : return Color.YELLOW;
			case 1 : return Color.RED;
			case 2 : return Color.ORANGE;
			case 3 : return Color.PINK;
			default : throw new IllegalArgumentException("Unsupported inbdex: " + index);
			
		}
	}
}
