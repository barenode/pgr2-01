package solids;

import java.awt.image.BufferedImage;

import transforms.Point3D;

public class Triangel extends SolidBase {

	private final BufferedImage texture;
	
    public Triangel(double size, BufferedImage texture) {
    	super();
    	this.texture = texture;    	
    	vertices.add(new Point3D(0, 	0, 		0, 		0, 0));
        vertices.add(new Point3D(0, 	1, 		0, 		texture.getWidth(), 0));
        vertices.add(new Point3D(0, 	0, 		1,		0, texture.getHeight()));
        
        indicesTriangle.add(0); indicesTriangle.add(1); indicesTriangle.add(2);        
    }
    
	@Override
	public Point3D getCentroid() {
		return vertices.get(0);
	}

	@Override
	public BufferedImage getTexture() {
		return texture;
	}	
}
