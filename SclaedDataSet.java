import java.util.ArrayList;


/**
 * @author shsmchlr
 * Scaled Data Set ... like DataSet but there are min/max values that inputs/targets scaled to
 */
public class ScaledDataSet extends DataSet {

    DataItem minVals;		// has list of minimum values of each input and output
    DataItem maxVals;		// and maximum values

    /**
     * constructor which is passed the number of inputs and a string with data set
     * @param numIn		how many inputs
     * @param alldata	string defining all data
     * 		alldata  has series of strings for each item separated by ;
     * 		string for an item has numbers for inputs then targets separated by space
     */
    public ScaledDataSet(String alldata) {
        super (alldata);					// puts all data into array list
        minVals = allItems.get(0);			// copy min values currently as first item in set
        maxVals = allItems.get(1);			// max values are second
        allItems.remove(1);					// remove max values from array list
        allItems.remove(0);					// and min values, so now allItems has actual data
        numItems -= 2;						// reduce numItems as now two elements fewer
        // now go through the actual items, and normalise data
        // inputs are now in range -1 to +1
        // outputs are in range 0.1 to 0.8 (consistent with sigmoid activation
        for (int item=0; item<numItems; item++) {
            allItems.get(item).setInputs(scaleList(allItems.get(item).getInputs(), minVals.getInputs(), maxVals.getInputs(), -1, 1));
            allItems.get(item).setTargets(scaleList(allItems.get(item).getTargets(), minVals.getTargets(), maxVals.getTargets(), 0.1, 0.9));
        }
    }

    private ArrayList<Double> scaleList(ArrayList<Double> values, ArrayList<Double> minValues,
                                        ArrayList<Double> maxValues, double toMin, double toMax) {
        ArrayList<Double> ans = new ArrayList<Double>();
        for (int ct=0; ct<values.size(); ct++)
            ans.add(normalise(values.get(ct), minValues.get(ct), maxValues.get(ct), toMin, toMax));
        return ans;
    }
    /**
     * normalise value which is between min and max, so in range toMin ,, toMax
     * @param value
     * @param minValue
     * @param maxValue
     * @param toMin
     * @param toMax
     * @return
     */
    private double normalise (double value, double minValue, double maxValue, double toMin, double toMax) {
        return toMin + (value - minValue) * (toMax - toMin) / (maxValue - minValue);
    }
    /**
     * get the list of inputs of ct'th item from set ready for printing; here denormalise
     * @param ct
     * @return array list
     */
    private ArrayList<Double> deScaleList(ArrayList<Double> values, ArrayList<Double> minValues,
                                          ArrayList<Double> maxValues, double toMin, double toMax) {
        ArrayList<Double> ans = new ArrayList<Double>();
        for (int ct=0; ct<values.size(); ct++)
            ans.add(denormalise(values.get(ct), minValues.get(ct), maxValues.get(ct), toMin, toMax));
        return ans;
    }

    /**
     * denormalise value which i between minValue and maxValue so is in range toMin ..toMax
     * @param value
     * @param minValue
     * @param maxValue
     * @param toMin
     * @param toMax
     * @return
     */
    private double denormalise (double value, double minValue, double maxValue, double toMin, double toMax) {
        return minValue + (value - toMin) * (maxValue - minValue) / (toMax - toMin);
    }

    /**
     * return array list of inputs so they can be printed (ie denormalised)
     */
    protected ArrayList<Double> getPrintIns(int ct) {
        return deScaleList(getIns(ct), minVals.getInputs(), maxVals.getInputs(), -1, 1);
        // get inputs then descale to original
    }

    /**
     * get the list of targets of ct'th item from set ready for printing (ie denormalised)
     * @param ct
     * @return array list
     */
    protected ArrayList<Double> getPrintTargets(int ct) {
        return deScaleList(getTargets(ct), minVals.getTargets(), maxVals.getTargets(), 0.1, 0.9);
        // get targets and descale
    }

    /**
     * get the list of outputs of ct'th item from set ready for printing
     * @param ct
     * @return array list
     */
    public ArrayList<Double> getPrintOuts(int ct) {
        return deScaleList(getOuts(ct), minVals.getTargets(), maxVals.getTargets(), 0.1, 0.9);
        // get outputs and descale
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // Test data set
//		ScaledDataSet Test = new ScaledDataSet("2 3 %.0f %.1f %.3f;0 2 0 0 0;5 6 10 10 10;0 6 1 2 3;5 2 4 1 7;3 4 0 10 5");
        ScaledDataSet Test = new ScaledDataSet(GetFile("unseen.txt"));							// read file into set
        for (int ct=0; ct<Test.numInSet(); ct++) Test.setOutputs(ct, Test.getTargets(ct));	// copy targets to outputs
        System.out.print(Test.toString(true, true));						//print set

    }

}
