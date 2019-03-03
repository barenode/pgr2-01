package solids;

import transforms.Point3D;

import java.util.ArrayList;
import java.util.List;

public abstract class SolidBase implements Solid {

    protected List<Point3D> vertices;
    protected List<Integer> indicesLine;
    protected List<Integer> indicesTriangle;

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
}
