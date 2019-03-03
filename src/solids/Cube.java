package solids;

import java.awt.Color;

import transforms.Point3D;

public class Cube extends SolidBase {

		/*
           7______6
		   /|    /|
		 4/_|___/5|
		 | 3|__|_ |2
		 | /   | /
		 |/____|/
		 0     1
		 */

	private Point3D centroid;
	
    public Cube(double size){

        vertices.add(new Point3D(0, 0, 0));
        vertices.add(new Point3D(size, 0, 0));
        vertices.add(new Point3D(size, size, 0));
        vertices.add(new Point3D(0, size, 0));

        vertices.add(new Point3D(0, 0, size));
        vertices.add(new Point3D(size, 0, size));
        vertices.add(new Point3D(size, size, size));
        vertices.add(new Point3D(0, size, size));        
        
        vertices.add(centroid = new Point3D(size/2, size/2, size/2));

//        for (int i = 0; i < 4; i++) { // hrany
//            indicesLine.add(i); indicesLine.add((i + 1) % 4);
//            indicesLine.add(i); indicesLine.add(i + 4);
//            indicesLine.add(i + 4); indicesLine.add((i + 1) % 4 + 4);
//        }

        // plochy
        indicesTriangle.add(0); indicesTriangle.add(1); indicesTriangle.add(2); 
        indicesTriangle.add(0); indicesTriangle.add(2); indicesTriangle.add(3);

        indicesTriangle.add(0); indicesTriangle.add(1); indicesTriangle.add(5); //předek
        indicesTriangle.add(0); indicesTriangle.add(4); indicesTriangle.add(5);

        indicesTriangle.add(1); indicesTriangle.add(2); indicesTriangle.add(6); //vpravo
        indicesTriangle.add(1); indicesTriangle.add(5); indicesTriangle.add(6);

        indicesTriangle.add(2); indicesTriangle.add(3); indicesTriangle.add(7); //vzadu
        indicesTriangle.add(2); indicesTriangle.add(6); indicesTriangle.add(7);

        indicesTriangle.add(3); indicesTriangle.add(0); indicesTriangle.add(4); //vlevo
        indicesTriangle.add(3); indicesTriangle.add(4); indicesTriangle.add(7);

        indicesTriangle.add(4); indicesTriangle.add(5); indicesTriangle.add(6); //vršek
        indicesTriangle.add(4); indicesTriangle.add(6); indicesTriangle.add(7);
    }
    
    public Point3D getCentroid() {
    	return centroid;
    }

	@Override
	public int getColorByTriangleVertice(int index) {
		return getColor(index/6).getRGB();
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
}
