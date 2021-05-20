import java.awt.geom.Rectangle2D;

/**
 * This class computes the Burning Ship Fractal on complex plain, and
 * also contains method to initialize the range of interests on 
 * complex plane.
 * @author BOTAO
 *
 */
public class BurningShip extends FractalGenerator {

    public static final int MAX_ITERATIONS = 2000;

    /**
     * This method takes in a range object, and adjust its fields
     * into the range in complex plane that the user is interested
     * in.
     */
    public void getInitialRange(Rectangle2D.Double range) {
        range.x = -2;
        range.y = -2.5;
        range.width = 4;
        range.height = 4;
    }

    /**
     * This method takes in the coordination of a pixel, and do the
     * Burning Ship Fractal on its corresponding location in the fractal
     * space. The end condition is either reaching max iteration times
     * (returns -1) or the length of the complex vector is larger
     * than 2 (returns iteration times).
     *
     */
    public int numIterations(double x, double y) {

        int count = 0;
        double re = 0;
        double im = 0;

        while (re * re + im * im <= 4) {
            double nextRe = re * re - im * im + x;
            double nextIm = 2 * re * im + y;

            re = Math.abs(nextRe);
            im = Math.abs(nextIm);
            if (count++ == Mandelbrot.MAX_ITERATIONS) return -1;
        }

        return count;
    }

    /**
     * This method overrides toString() method to return the type name
     * for combo-box to show in the UI graph.
     */
    public String toString() {
        return "Burning Ship";
    }
}