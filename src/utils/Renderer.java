package utils;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class Renderer {

    private int color;
    private BufferedImage img;

    public Renderer(BufferedImage img) {
        this.img = img;
        color = Color.RED.getRGB();
    }

    private void drawPixel(int x, int y) {
        drawPixel(x, y, color);
    }

    private void drawPixel(int x, int y, int color) {
        if (x < 0 || x >= 800) return;
        if (y < 0 || y >= 600) return;
        img.setRGB(x, y, color);
    }

    public void lineTrivial(int x1, int y1, int x2, int y2) {
        // y = kx + q
        int dx = x1 - x2;
        int dy = y1 - y2;

        if (Math.abs(dx) > Math.abs(dy)) {
            // řídící osa X
            // otočení
            if (x1 > x2) {
                int p = x1;
                x1 = x2;
                x2 = p;
                p = y1;
                y1 = y2;
                //y2 = p;
            }

            float k = (float) dy / (float) dx;
            for (int x = x1; x < x2; x++) {
                int y = y1 + (int) (k * (x - x1));
                drawPixel(x, y);
            }

        } else {
            // řídící osa Y
            // otočení
            if (y1 > y2) { // podmínka nad y
                int p = x1;
                x1 = x2;
                //x2 = p;
                p = y1;
                y1 = y2;
                y2 = p;
            }

            float k = (float) dx / (float) dy; // 1/k
            for (int y = y1; y < y2; y++) { // cyklus pro y (dopočítáváme x)
                int x = x1 + (int) (k * (y - y1));
                drawPixel(x, y);
            }
        }
    }

    public void lineDDA(int x1, int y1, int x2, int y2) {
        float k, g, h; //g = přírůstek x, h = přírůstek y
        int dy = y2 - y1;
        int dx = x2 - x1;
        k = dy / (float) dx;

        //určení řídící osy
        if (Math.abs(dx) > Math.abs(dy)) {
            //x
            g = 1;
            h = k;
            // todo prohození
        } else {
            //y
            h = 1;
            g = 1 / k;
            // todo prohození
        }
        float x = x1;
        float y = y1;
        int max = Math.max(Math.abs(dx), Math.abs(dy));

        for (int i = 0; i < max; i++) {
            drawPixel(Math.round(x), Math.round(y));
            x += g;
            y += h;
        }
    }

    public void polygon(int x1, int y1, int x2, int y2, int count) {
        // todo
    }

    public void seedFill(int x, int y, int oldColor, int newColor) {
        if (oldColor == img.getRGB(x, y)) {
            drawPixel(x, y, newColor);

            seedFill(x - 1, y, oldColor, newColor);
            seedFill(x + 1, y, oldColor, newColor);
            seedFill(x, y - 1, oldColor, newColor);
            seedFill(x, y + 1, oldColor, newColor);
        }
    }
}
