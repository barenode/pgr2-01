package solids;

import java.awt.Color;
import java.awt.image.BufferedImage;

import transforms.Point3D;

public class Axis extends SolidBase {
	
    public Axis(){
        vertices.add(new Point3D(0,0,0));
        vertices.add(new Point3D(3,0,0));
        vertices.add(new Point3D(0,3,0));
        vertices.add(new Point3D(0,0,3));       

        //indicesLine.add(0); indicesLine.add(1);
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
    	return vertices.get(0);
    }

	@Override
	public BufferedImage getTexture() {
		return null;
	}
}
