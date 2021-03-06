package pgr2.solids;

import static java.util.stream.Collectors.toList;

import java.awt.Color;
import java.util.List;

import transforms.Mat4;
import transforms.Point3D;

public interface Solid {

    // vertex buffer
    List<Point3D> getVertices();

    // index buffer
    List<Integer> getIndices(Primitive primitive);
    
    Point3D getCentroid();
    
    Mat4 getTransformation();
    
    void setTransformation(Mat4 transformation);

    default int getColorByEdge(int index) {
        return Color.BLACK.getRGB();
    }
    
    default int getColorByTriangleVertice(int index) {
        return Color.RED.getRGB();
    }

    default Solid transform(Mat4 mat) {
    	List<Point3D> transformed = getVertices().stream().map(p -> {
    		Point3D t = p.mul(mat);
    		return t;
    	}).collect(toList());
    	getVertices().clear();
    	getVertices().addAll(transformed);
    	return this;
    } 
    
    default Solid transform() {
    	return transform(getTransformation());
    }    
}
