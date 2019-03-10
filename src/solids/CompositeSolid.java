package solids;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import transforms.Point3D;

public class CompositeSolid implements Solid {
	
	private final List<Solid> solids;
	
	public CompositeSolid(Solid... solids) {
		super();
		this.solids = Arrays.asList(solids);
	}

	@Override
	public List<Point3D> getVertices() {		
		return solids.stream().map(s->s.getVertices()).flatMap(Collection::stream).collect(Collectors.toList());
	}

	@Override
	public List<Integer> getIndices(Primitive primitive) {
		int offset = 0;
		List<Integer> result = new ArrayList<Integer>();
		for (Solid solid : solids) {
			for (int indice : solid.getIndices(primitive)) {
				result.add(indice+offset);
			}
			offset = offset + solid.getIndices(primitive).size()-1;
		}
		return result;
	}

	@Override
	public Point3D getCentroid() {
		return solids.get(0).getCentroid();
	}

	@Override
	public BufferedImage getTexture() {
		return null;
	}
}
