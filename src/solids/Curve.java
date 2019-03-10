package solids;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.List;

import transforms.Point3D;

public class Curve extends SolidBase {	
	
	private Color color;
	
	public Curve(List<Point3D> points, Color color) {
		super();
		this.color = color;
		vertices.addAll(points);		
		for (int i = 1; i<points.size(); i++) {
			indicesLine.add(i-1); 
			indicesLine.add(i);
		}
	}
	
	@Override
	public int getColorByEdge(int index) {
        return color.getRGB();
    }

	@Override
	public Point3D getCentroid() {
		throw new UnsupportedOperationException();
	}

	@Override
	public BufferedImage getTexture() {
		// TODO Auto-generated method stub
		return null;
	}
}
