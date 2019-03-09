package solids;

import java.awt.Color;
import java.awt.image.BufferedImage;

import transforms.Point3D;

public class Axis extends SolidBase {

	private Point3D centroid;
	
    public Axis(){
        vertices.add(new Point3D(0,0,0));
        vertices.add(new Point3D(1,0,0));
        vertices.add(new Point3D(0,1,0));
        vertices.add(new Point3D(0,0,1));
        
        vertices.add(centroid = new Point3D(0, 0, 0));

        indicesLine.add(0); indicesLine.add(1);
        indicesLine.add(0); indicesLine.add(2);
        indicesLine.add(0); indicesLine.add(3);
    }

    @Override
    public int getColorByEdge(int index) {
        switch (index){
            case 0: return Color.RED.getRGB();
            case 1: return Color.GREEN.getRGB();
            case 2: return Color.BLUE.getRGB();
        }
        return super.getColorByEdge(index);
    }
    
    public Point3D getCentroid() {
    	return centroid;
    }

	@Override
	public BufferedImage getTexture() {
		// TODO Auto-generated method stub
		return null;
	}
}
