import java.awt.geom.Rectangle2D;


public class Mandelbrot extends FractalGenerator {

    public static final int MAX_ITERATIONS = 2000;


    /**
     * This method takes in a range object, and adjust its fields
     * into the range in complex plane that the user is interested
     * in.
     */
    @Override
    public void getInitialRange(Rectangle2D.Double range) {
        range.x = -2;
        range.y = -1.5;
        range.width = 3;
        range.height = 3;
    }
    /**
     * This method takes in the coordination of a pixel, and do the
     * Mandelbrot Fractal on its corresponding location in the fractal
     * space. The end condition is either reaching max iteration times
     * (returns -1) or the length of the complex vector is larger
     * than 2 (returns iteration times).
     *
     */
    @Override
    public int numIterations(double x, double y) {

        int count = 0;
        double re = 0;
        double im = 0;

        while (re * re + im * im <= 4) {
            double nextRe = re * re - im * im + x;
            double nextIm = 2 * re * im + y;

            re = nextRe;
            im = nextIm;
            if (count++ == Mandelbrot.MAX_ITERATIONS) return -1;
        }

        return count;
    }

    /**
     * This method overrides toString() method to return the type name
     * for combo-box to show in the UI graph.
     */
    public String toString() {
        return "Mandelbrot";
    }


}
