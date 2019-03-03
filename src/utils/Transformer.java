package utils;

import solids.Axis;
import solids.Primitive;
import solids.Solid;
import transforms.Mat4;
import transforms.Mat4Identity;
import transforms.Point3D;
import transforms.Vec3D;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Optional;

public class Transformer {

    private BufferedImage img;
    private Zbuffer Zbuffer;
    
    private Mat4 model;
    private Mat4 view;
    private Mat4 projection;

    private double[][] zBuffer;

    public Transformer(BufferedImage img) {
        this.img = img;
        this.Zbuffer = new Zbuffer(img);
        // prozatĂ­m jednotkovĂ© matice
        this.model = new Mat4Identity();
        this.view = new Mat4Identity();
        this.projection = new Mat4Identity();

        this.zBuffer = new double[img.getWidth()][img.getHeight()];
    }

    public void drawWireFrame(Solid solid) {

        Mat4 matFinal;
        if (solid instanceof Axis) {
            matFinal = view.mul(projection);
        } else {
            matFinal = model.mul(view).mul(projection);
        }

        List<Integer> indices = solid.getIndices(Primitive.LINES);
        for (int i = 0; i < indices.size(); i += 2) {
            Point3D a = solid.getVertices().get(indices.get(i));
            Point3D b = solid.getVertices().get(indices.get(i + 1));
            transformEdge(matFinal, a, b,
                    solid.getColorByEdge(i / 2));
        }
    }

    public void drawFilledFrame(Solid solid) { // z-buffer
        Mat4 matFinal = model.mul(view).mul(projection);

        List<Integer> indices = solid.getIndices(Primitive.TRIANGLES);
        for (int i = 0; i < indices.size() - 2; i += 3) {
            Point3D p1 = solid.getVertices().get(indices.get(i));
            Point3D p2 = solid.getVertices().get(indices.get(i + 1));
            Point3D p3 = solid.getVertices().get(indices.get(i + 2));
            transformTriangles(p1, p2, p3, matFinal, solid.getColorByTriangleVertice(i));
        }
    }

    private void transformTriangles(Point3D p1, Point3D p2, Point3D p3, Mat4 matFinal, int color) {

        p1 = p1.mul(matFinal);
        p2 = p2.mul(matFinal);
        p3 = p3.mul(matFinal);

        Zbuffer.renderTriangle(p1, p2, p3, color);
        
//        // Ĺ™azenĂ­
//        if (p1.getZ() < p2.getZ()) {
//            Point3D p = p1;
//            p1 = p2;
//            p2 = p;
//        }
//
//        if (p2.getZ() < p3.getZ()) {
//            Point3D p = p2;
//            p2 = p3;
//            p3 = p;
//        }
//
//        if (p1.getZ() < p2.getZ()) {
//            Point3D p = p1;
//            p1 = p2;
//            p2 = p;
//        }
//
//        double zMin = 0;
//        // 1. scĂ©nĂˇĹ™ (all in)
//        if (p3.getZ() > zMin) {
//            rasterizeTriangles(p1, p2, p3);
//            return;
//        }
//
//        // 2. scĂ©nĂˇĹ™ (p3 out)
//        if (p2.getZ() > zMin) {
//            // t mezi p1 a p3
//            double ta = ((zMin - p3.getZ()) / (p1.getZ() - p3.getZ()));
//            Point3D p3a = p3.mul(1 - ta).add(p1.mul(ta));
//
//            double tb = ((zMin - p3.getZ()) / (p2.getZ() - p3.getZ()));
//            Point3D p3b = p3.mul(1 - tb).add(p2.mul(tb));
//
//            rasterizeTriangles(p1, p2, p3a);
//            rasterizeTriangles(p2, p3a, p3b);
//            return;
//        }
//        // 3. scĂ©nĂˇĹ™ (only p1 in)
//        if (p1.getZ() > zMin) {
//            double t3 = ((zMin - p3.getZ()) / (p1.getZ() - p3.getZ()));
//            double t2 = ((zMin - p2.getZ()) / (p1.getZ() - p2.getZ()));
//
//            p3 = p3.mul(1 - t3).add(p1.mul(t3));
//            p2 = p2.mul(1 - t2).add(p1.mul(t2));
//
//            rasterizeTriangles(p1, p2, p3);
//        }
//
//        // 4. scĂ©nĂˇĹ™ (all out)
//        return;

    }

    private void rasterizeTriangles(Point3D p1, Point3D p2, Point3D p3) {

        // dehomogenizace
        Optional<Vec3D> vo1 = p1.dehomog();
        Optional<Vec3D> vo2 = p2.dehomog();
        Optional<Vec3D> vo3 = p3.dehomog();

        // Ĺ™eĹˇenĂ­ Optional<E>
        if (!vo1.isPresent() || !vo2.isPresent() || !vo3.isPresent()) return;

        Vec3D v1 = vo1.get();
        Vec3D v2 = vo2.get();
        Vec3D v3 = vo3.get();

        // oĹ™ez zobrazovacĂ­m objemem TODO
        //if ((Math.min(Math.min(v1.getX(), v2.getX()), v3.getX()) > 1.0f) || (Math.max(Math.max(v1.getX(), v2.getX()), v3.getX()) <= -1.0f))
        // return (TODO)

        // transformace do okna
        v1 = v1.mul(new Vec3D(1, -1, 1))
                .add(new Vec3D(1, 1, 0))
                .mul(new Vec3D(
                        0.5 * (img.getWidth() - 1),
                        0.5 * (img.getHeight() - 1),
                        0.5
                ));
        v2 = v2.mul(new Vec3D(1, -1, 1))
                .add(new Vec3D(1, 1, 0))
                .mul(new Vec3D(
                        0.5 * (img.getWidth() - 1),
                        0.5 * (img.getHeight() - 1),
                        0.5
                ));
        v3 = v3.mul(new Vec3D(1, -1, 1))
                .add(new Vec3D(1, 1, 0))
                .mul(new Vec3D(
                        0.5 * (img.getWidth() - 1),
                        0.5 * (img.getHeight() - 1),
                        0.5
                ));


        scanLine(v1, v2, v3, Color.RED.getRGB());

    }

    private void scanLine(Vec3D v1, Vec3D v2, Vec3D v3, int color) {
        // seĹ™azenĂ­ dle Y
        if (v1.getY() > v2.getY()) {
            Vec3D v = v1;
            v1 = v2;
            v2 = v;
        }
        if (v2.getY() > v3.getY()) {
            Vec3D v = v2;
            v2 = v3;
            v3 = v;
        }
        if (v1.getY() > v2.getY()) {
            Vec3D v = v1;
            v1 = v2;
            v2 = v;
        }

        // od V1 do V2
        for (int y = (int) v1.getY() + 1; y <= v2.getY(); y++) {
            double s1 = (y - v1.getY()) / (v2.getY() - v1.getY());
            double s2 = (y - v1.getY()) / (v3.getY() - v1.getY());

            double x1 = (v1.getX() * (1 - s1) + v2.getX() * s1);
            double x2 = (v1.getX() * (1 - s2) + v3.getX() * s2);

            double z1 = (v1.getZ() * (1 - s1) + v2.getZ() * s1);
            double z2 = (v1.getZ() * (1 - s2) + v3.getZ() * s2);

            // seĹ™azenĂ­ dle X
            if (x1 > x2) {
                double a = x1;
                x1 = x2;
                x2 = a;
                a = z1;
                z1 = z2;
                z2 = a;
            }

            for (int x = (int) x1; x <= x2; x++) {
                double t = (x - x1) / (x2 - x1);
                double z = (z1 * (1 - t) + z2 * t);

                if (x > 0
                        && y > 0
                        && x < img.getWidth() - 1
                        && y < img.getHeight() - 1
                        && z < zBuffer[x][y]) {
                    drawPixel(x, y, color);
                    zBuffer[x][y] = z;
                }
            }
        }

        // od V2 do V3 TODO
    }

    public void clear() {
    	Zbuffer.clear();
    	
//        for(int x = 0; x < img.getWidth(); x++)
//            for(int y = 0; y < img.getHeight(); y++)
//                zBuffer[x][y] = 1.0;
    }

    private void transformEdge(Mat4 matFinal, Point3D p1, Point3D p2, int color) {
        // 1.) vynĂˇsobit body maticĂ­
        p1 = p1.mul(matFinal);
        p2 = p2.mul(matFinal);

        // 2.) oĹ™ez dle W bodĹŻ
        if (p1.getW() <= 0 && p2.getW() <= 0) return;

        // 3.) tvorba z vektorĹŻ dehomogenizacĂ­ (Point3D.dehomog())
        Optional<Vec3D> vo1 = p1.dehomog();
        Optional<Vec3D> vo2 = p2.dehomog();

        if (!vo1.isPresent() || !vo2.isPresent()) return;

        Vec3D v1 = vo1.get();
        Vec3D v2 = vo2.get();

        v1 = v1.mul(new Vec3D(1, -1, 1))
                .add(new Vec3D(1, 1, 0))
                .mul(new Vec3D(
                        0.5 * (img.getWidth() - 1),
                        0.5 * (img.getHeight() - 1),
                        1
                ));
        v2 = v2.mul(new Vec3D(1, -1, 1))
                .add(new Vec3D(1, 1, 0))
                .mul(new Vec3D(
                        0.5 * (img.getWidth() - 1),
                        0.5 * (img.getHeight() - 1),
                        1
                ));

        // TODO 4.) oĹ™ez zobrazovacĂ­ objem

        lineDDA((int) v1.getX(), (int) v1.getY(), (int) v2.getX(), (int) v2.getY(), color);
    }

    // metody vykreslovĂˇnĂ­
    public void lineDDA(int x1, int y1, int x2, int y2, int color) {

        float k, g, h; //G = PĹ�ĂŤRĹ®STEK X, H = PĹ�ĂŤRĹ®STEK Y
        int dy = y2 - y1;
        int dx = x2 - x1;
        k = dy / (float) dx;

        //urÄŤenĂ­ Ĺ™Ă­dĂ­cĂ­ osy
        if (Math.abs(dx) > Math.abs(dy)) {
            g = 1;
            h = k;
            if (x1 > x2) { // prohozenĂ­
                int temp = x1;
                x1 = x2;
                x2 = temp;
                temp = y1;
                y1 = y2;
                y2 = temp;
            }
        } else {
            g = 1 / k;
            h = 1;
            if (y1 > y2) { //otoÄŤenĂ­
                int temp = x1;
                x1 = x2;
                x2 = temp;
                temp = y1;
                y1 = y2;
                y2 = temp;
            }
        }

        float x = x1;
        float y = y1;
        int max = Math.max(Math.abs(dx), Math.abs(dy));

        for (int i = 0; i <= max; i++) {
            drawPixel(Math.round(x), Math.round(y), color);
            x += g;
            y += h;
        }
    }

    private void drawPixel(int x, int y, int color) {
        if (x < 0 || x >= 800) return;
        if (y < 0 || y >= 600) return;
        img.setRGB(x, y, color);
    }

    // setters for matrix
    public void setModel(Mat4 model) {
        this.model = model;
    }

    public void setView(Mat4 view) {
        this.view = view;
    }

    public void setProjection(Mat4 projection) {
        this.projection = projection;
    }
}
