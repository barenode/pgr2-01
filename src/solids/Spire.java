package solids;

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
        
	}

	@Override
	public Point3D getCentroid() {
		return vertices.get(5);
	}

	@Override
	public BufferedImage getTexture() {
		// TODO Auto-generated method stub
		return null;
	}
}
