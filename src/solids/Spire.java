package solids;

import java.awt.Color;
import java.awt.image.BufferedImage;

import transforms.Point3D;

public class Spire extends SolidBase {
	
	public Spire() {
		super();
		
    	vertices.add(new Point3D(-0.5, -0.5, -0.5));    
        vertices.add(new Point3D(-0.5, 0.5, -0.5));    
        vertices.add(new Point3D(0.5, -0.5, -0.5));    
        vertices.add(new Point3D(0.5, 0.5, -0.5));   
        vertices.add(new Point3D(0, 0, 0.5));
        vertices.add(new Point3D());
        
        indicesLine.add(0); indicesLine.add(1);
        indicesLine.add(1); indicesLine.add(3);
        indicesLine.add(2); indicesLine.add(3);
        indicesLine.add(2); indicesLine.add(0);
        
        indicesLine.add(0); indicesLine.add(4);
        indicesLine.add(1); indicesLine.add(4);
        indicesLine.add(2); indicesLine.add(4);
        indicesLine.add(3); indicesLine.add(4);        
        
        indicesTriangle.add(0); indicesTriangle.add(1); indicesTriangle.add(3); 
        indicesTriangle.add(0); indicesTriangle.add(2); indicesTriangle.add(3); 
        
        indicesTriangle.add(0); indicesTriangle.add(1); indicesTriangle.add(4); 
        indicesTriangle.add(1); indicesTriangle.add(3); indicesTriangle.add(4); 
        indicesTriangle.add(0); indicesTriangle.add(2); indicesTriangle.add(4); 
        indicesTriangle.add(2); indicesTriangle.add(3); indicesTriangle.add(4); 
	}

	@Override
	public Point3D getCentroid() {
		return vertices.get(5);
	}
	
	@Override
	public int getColorByTriangleVertice(int index) {
		return getColor(index/3).getRGB();
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
	public BufferedImage getTexture() {
		// TODO Auto-generated method stub
		return null;
	}
}
