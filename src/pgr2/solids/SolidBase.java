package pgr2.solids;

import transforms.Mat4;
import transforms.Mat4Identity;
import transforms.Point3D;

import java.util.ArrayList;
import java.util.List;

public abstract class SolidBase implements Solid {

    protected List<Point3D> vertices;
    protected List<Integer> indicesLine;
    protected List<Integer> indicesTriangle;
    
    private Mat4 transformation = new Mat4Identity();
    
    public SolidBase() {
        vertices = new ArrayList<>();
        indicesLine = new ArrayList<>();
        indicesTriangle = new ArrayList<>();
    }

    @Override
    public List<Point3D> getVertices() {
        return vertices;
    }

    @Override
    public List<Integer> getIndices(Primitive primitive) {
        if (primitive == Primitive.LINES) {
            return indicesLine;
        } else {
            return indicesTriangle;
        }
    }
    
    @Override
    public void setTransformation(Mat4 transformation) {
		this.transformation = transformation;
	}    
    
    @Override
   	public Mat4 getTransformation() {
   		return transformation;
   	}
}
