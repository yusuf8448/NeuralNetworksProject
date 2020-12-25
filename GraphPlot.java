import java.util.ArrayList;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;

/**
 * Class for plotting graphs, tadpole plots, etc
 * Allows series of graphs to be plotted one under each other
 * Can be copied to clipboard
 * @author shsmchlr
 *
 */
public class GraphPlot {
    GraphicsContext gc;			// context into which graphs are plotted
    int graphX; 				// maximum size in X directiopn
    int graphY;
    int graphYOffset;			// offset into which graphs are plotted
    int maxX;					// maximum value of x data
    int maxY = 0;				// used to remember coord of bottom graph for copy to clipboard
    double minVal, maxVal;		// min and maximum y values

    /**
     * Constructor for class
     * @param gcval		graphics context
     * @param xMax		max in x direction
     * @param yMax		and in y
     */
    GraphPlot(GraphicsContext gcval, int xMax, int yMax) {
        gc = gcval;							// set these values
        graphX = xMax;
        graphY = yMax;
        gc.setFont(Font.font(16));			// define font size
    }
    /**
     * clear part of graph area
     * @param minY				minimum y value tobe cleared
     * @param maxY				and max
     */
    public void clearGraph(int minY, int maxY) {
        gc.setFill(Color.BEIGE);
        gc.fillRect(0,  minY,  graphX,  maxY);		// clear the graph area
        maxY = 0;									// remember bottom of latest graph
    }
    /** show unique identifier
     *
     * @param u	identifier
     */
    public void showUnique(String u) {
        gc.setFill(Color.BLACK);
        gc.fillText("Graphs for " + u, graphX/3, graphY*3-5);
    }
    /**
     * Is there a graph there
     * @return		true if is
     */
    public boolean graphShown() {
        return maxY>0;								// if an area has been cleared, graph is there
    }
    /**
     * return an image of the canvas (for copying to clip board)
     * @return	the image
     */
    public WritableImage getCanvasImage() {
        WritableImage wim = new WritableImage(graphX, maxY);	// create answer
        gc.getCanvas().snapshot(null, wim);						// take snapshot from canvas
        return wim;												// return image
    }

    /**
     * find the minimum and maximum values of the two sets of y data
     * @param target			// first set
     * @param actual			// second (which may be null if only one set
     */
    private void findMinMax(ArrayList<Double> target, ArrayList<Double> actual) {
        minVal = 0.0;											// get default values
        maxVal = 0.001;
        maxX = target.size();									// + 1 as plot from 0 ..
        for (int ct=0; ct<maxX; ct++) {							// search both arrays
            if (target.get(ct)<minVal) minVal = target.get(ct);	// check if value < min
            if (target.get(ct)>maxVal) maxVal = target.get(ct); // or > max
            if (actual != null) {
                if (actual.get(ct)<minVal) minVal = actual.get(ct);
                if (actual.get(ct)>maxVal) maxVal = actual.get(ct);
            }
        }
    }
    /**
     * scale the x value so fits on the canvas
     * @param x		raw x value
     * @return		scaled x value
     */
    private double xScale(double x) {
        return 50 + (x * (graphX-90)) / maxX;
    }

    /**
     * scale the y value, so within relevant part of anvas
     * @param y		raw y value
     * @return		scaled y value
     */
    private double yScale(double y) {
        return graphYOffset + 30 + (y - maxVal) * (graphY - 50) / (minVal - maxVal);
    }

    /**
     * label the graph with the given x value
     * @param x
     */
    private void labelX(double x) {
        gc.fillText(String.format("%.0f", x), xScale(x)+2, yScale(0));
    }

    /**
     * label the graph with the given y value
     * @param y
     */
    private void labelY(double y) {
        gc.fillText(String.format("%.2f", y), 5, yScale(y));
    }

    /**
     * set up graphs (min max values having already been found so scaling works)
     * @param title		title of graph
     * @param graphYOff	offset of where in canvas
     */
    private void setUpGraph(String title, int graphYOff) {
        graphYOffset = graphYOff;						// set offset
        clearGraph(graphYOff, graphYOff+graphY-10);		// clear the graph
        gc.setStroke(Color.BLACK);						// set up to draw axes
        gc.setLineWidth(2);
        gc.strokeLine(xScale(0), yScale(0), xScale(maxX), yScale(0));			// draw x axis
        gc.strokeLine(xScale(0), yScale(minVal), xScale(0), yScale(maxVal));	// draw y axis
        gc.setFill(Color.BLACK);						// set the fill colour for plotting text
        gc.fillText(title, graphX/2, graphYOff+15);		// show title
        labelY(maxVal);									// label max and min y values
        labelY(minVal);
        maxY = graphYOff+graphY; 						// set max Y value, for copy to clipboard
    }

    /**
     * do a tadpole plot of the given target and actual data
     * @param title		title of plot
     * @param target	data
     * @param actual	data
     * @param graphYOff	offset as to where plot within cavas
     */
    public void tadpolePlot(String title, ArrayList<Double> target, ArrayList<Double> actual, int graphYOff) {
        findMinMax(target, actual);						// set up scaling values
        setUpGraph(title, graphYOff);
        for (int ct=0; ct<target.size(); ct++) {		// for each point
            double xval = xScale(ct+0.5);				// scale the x,y values
            double ytar = yScale(target.get(ct));
            double yact = yScale(actual.get(ct));

            gc.setStroke(Color.BLUE);					// set the stroke colour
            gc.setLineWidth(3);
            gc.strokeLine(xval, yact, xval, ytar);		// draw line from actual to target
            gc.setFill(Color.RED);						// set the fill colour
            gc.fillArc(xval-2, ytar-2, 4, 4, 0, 360, ArcType.ROUND);
        }												// draw target as circle

    }

    /**
     * Plot a graph of the given y values
     * @param title		title of graph
     * @param yVals		the y values
     * @param graphYOff	offset of where on canvas graph shown
     */
    public void yPlot(String title, ArrayList<Double> yVals, int graphYOff) {
        findMinMax(yVals, null);
        setUpGraph(title, graphYOff);
        labelX(maxX);
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(2);
        gc.beginPath();
        for (int ct=0; ct<yVals.size(); ct++) {
            double xval = xScale(ct+0.5);
            double yval = yScale(yVals.get(ct));
            if (ct>0) gc.lineTo(xval, yval); else gc.moveTo(xval, yval);
        }
        gc.stroke();
    }

}