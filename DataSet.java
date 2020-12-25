import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


/**
 * @author shsmchlr
 * Class for data set of logic values, for neural nets
 * Each item in set has inputs, targets and outputs
 */


public class DataSet {

    /**
     * has variables for number of inputs, outputs and items in the data set
     * and then an array list of items
     * each item is in a private class which has inputs, output and targets
     */
    protected int numIns, numOuts, numItems, tWidth;				// information on numbers in set
    protected String inFormat, outFormat, rawFormat;				// how the inputs /outputs formated when printed
    protected ArrayList<String>inNames = new ArrayList<String>();	// names of all the inputs
    protected ArrayList<String>outNames = new ArrayList<String>();	// names of all the outputs/targets
    protected ArrayList<DataItem> allItems = new ArrayList<DataItem>();	// the actual data
    private ArrayList<ArrayList<Double>> sseLog = new ArrayList<ArrayList<Double>>();	// log of sse during learning

    /**
     * class for an item in data set
     * @author shsmchlr
     *
     */
    protected class DataItem {
        private ArrayList<Double> dataInputs = new ArrayList<Double>();		// space for inputs
        private ArrayList<Double> dataOutputs = new ArrayList<Double>();	// actual outputs (calculated separately)
        private ArrayList<Double> dataTargets = new ArrayList<Double>();	// targets

        /**
         * constructor which is passed the number of inputs and a string with data set
         * @param numIns	how many inputs
         * @param data		string with inputs and targets separated by space
         */
        public DataItem(int numIns, String data) {
            String dataSplit [] = data.split(" ");			// split into strings for each input/target
            for (int ct=0; ct<dataSplit.length; ct++) {		// for each
                double d = Double.parseDouble(dataSplit[ct]);	// convert to double
                if (ct<numIns) dataInputs.add(d);			// if is input, add to dataInputs
                else {
                    dataTargets.add(d);						// add to dataTargets
                    dataOutputs.add(0.0);					// add space in dataOutputs
                }
            }
        }
        /**
         * getInputs
         * @return	array list of the inputs
         */
        public ArrayList<Double> getInputs() {
            return dataInputs;
        }
        /**
         * getTargets
         * @return	array list of the targets
         */
        public ArrayList<Double> getTargets() {
            return dataTargets;
        }
        /**
         * getOutputs
         * @return	array list of the outputs
         */
        public ArrayList<Double> getOutputs() {
            return dataOutputs;
        }
        /**
         * getErrors
         * @return	array list of the errors
         */
        public ArrayList<Double> getErrors() {
            ArrayList<Double> dataErrors = new ArrayList<Double>();		// create list of errors
            for (int ct=0; ct<dataTargets.size(); ct++)					// for each target
                dataErrors.add(dataTargets.get(ct) - dataOutputs.get(ct));	// add the error
            return dataErrors;											// return result
        }
        /**
         * store the pos'th output into the data set
         * @param pos	which output
         * @param d		value of output
         */
        public void setOutput(int pos, double d) {
            dataOutputs.set(pos,  d);
        }
        /**
         * store the outputs in d into this item in data set
         * @param d		value of output
         */
        public void setOutputs(ArrayList<Double> d) {
            for (int ct=0; ct<d.size(); ct++) dataOutputs.set(ct,  d.get(ct));
        }
        /**
         * set the inputs
         * @param ins
         */
        public void setInputs (ArrayList<Double> ins) {
            dataInputs = ins;
        }
        /**
         * and the targets
         * @param tars
         */
        public void setTargets (ArrayList<Double> tars) {
            dataTargets = tars;
        }
    }

    /**
     * constructor which is a string with data set
     * @param alldata	string defining all data
     * 		alldata  has series of strings for each item separated by ;
     * 		first string has numIns numOuts, rest are for each item
     * 		second string has names of input and output variables
     * 		string for an item has numbers for inputs then targets separated by space
     */
    public DataSet(String alldata) {
        String allSplit[] = alldata.split(";");					// split all lines strings

        String fLine[] = allSplit[0].split(" ");				// split first line into strings
        numIns = Integer.parseInt(fLine[0]);					// first number is number of inputs
        numOuts = Integer.parseInt(fLine[1]);					// then number of outputs
        if (fLine.length>2) inFormat = fLine[2]; else inFormat = "%.1f";	// get format for printing ins
        if (fLine.length>3) outFormat = fLine[3]; else outFormat = "%.1f";	// and for outputs
        if (fLine.length>4) rawFormat = fLine[4]; else rawFormat = "%.2f";	// and for raw outputs

        setNames(allSplit[1].split(" "));						// process second line to get all the names

        for (int ct=2; ct<allSplit.length; ct++)				// process remaining lines
            allItems.add(new DataItem(numIns, allSplit[ct]));	// add each line
        numItems = allItems.size();								// (note how many items)
    }
    /**
     * set up names of inputs and outputs
     * @param names		string with the names
     */
    private void setNames(String[] names) {
        for (int ct=0; ct<names.length;ct++)
            if (ct<numIns) inNames.add(names[ct]); else outNames.add(names[ct]);
    }
    /**
     * read the data for the set from the given file name
     * @param name
     * @return		String with each line separated by ;
     */
    public static String GetFile(String name) {
        String ans = "";
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(name));	// read file
            String line;									// for processing each line in turn
            while ((line = br.readLine()) != null && (line.length() > 0)) {		// for all lines in file
                line = line.replace("\t", " ");				// replace tabs with spaces
                if (line.length()>0) { 						// if not empty string
                    if (ans.length()>0) ans = ans + ";";	// add to answer
                    ans = ans + line;
                }
            }
        } catch (IOException e) {			// report error if cant read file
            e.printStackTrace();
        } finally {							// at end try to close file
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return ans;
    }

    /**
     * return how many inputs in data set
     * @return
     */
    public int numInputs() {
        return numIns;
    }
    /**
     * return how many outputs in data set
     * @return
     */
    public int numOutputs() {
        return numOuts;
    }
    /**
     * return how many items in data set
     * @return
     */
    public int numInSet() {
        return numItems;
    }
    /**
     * return the inputs of the item'th item in the data set
     * @param item
     * @return		arraylist of inputs
     */
    public ArrayList<Double> getIns(int item) {
        return allItems.get(item).getInputs();
    }
    /**
     * return the targets of the item'th item in the data set
     * @param item
     * @return		arraylist of targets
     */
    public ArrayList<Double> getTargets(int item) {
        return allItems.get(item).getTargets();
    }
    /**
     * return the outputs of the item'th item in the data set
     * @param item
     * @return		arraylist of outputs
     */
    public ArrayList<Double> getOuts(int item) {
        return allItems.get(item).getOutputs();
    }
    /**
     * return the errors of the item'th item in the data set
     * @param item
     * @return		arraylist of errors
     */
    public ArrayList<Double> getErrors(int item) {
        return allItems.get(item).getErrors();
    }
    /**
     * store d as the first value in the item'th data record
     * @param item
     * @param d
     */
    public void setOutput(int item, double d) {
        allItems.get(item).setOutput(0, d);
    }
    /**
     * store the output d as the pos'th output of the item'th item in the data set
     * @param item
     * @param pos
     * @param d
     */
    public void setOutput(int item, int pos, double d) {
        allItems.get(item).setOutput(pos, d);
    }
    /**
     * store the outputs into the data set
     * @param item	which output
     * @param d		arraylist of outputs
     */
    public void setOutputs(int item, ArrayList<Double>d) {
        allItems.get(item).setOutputs(d);
    }
    /**
     * return s as a string of width mnum
     * @param s
     * @param mnum
     * @return
     */
    private String fixedWidth(String s, int mnum) {
        String ans = "";
        for(int ct=0; ct<mnum-s.length(); ct++) ans = ans + ' ';
        ans = ans + s;
        return ans;
    }
    /**
     * return s as string whose width is determined by the class variable tWdith
     * @param s
     * @return
     */
    private String fixedWidth(String s) {
        return fixedWidth(s, tWidth);
    }
    /**
     * return s as a string of width num, but s is centred
     * @param s
     * @param num
     * @return
     */
    private String fixedWidthCentre(String s, int num) {
        int maxw = num*tWidth;
        String s2 = fixedWidth(s, (maxw+s.length())/2);
        return s2 + fixedWidth(" ", maxw-s2.length());
    }
    /**
     * function which turns array of vals into strings
     * @param vals
     * @return
     */
    private String valstoString(ArrayList<Double> vals, String fStr) {
        String s = "";
        for (int ct=0; ct<vals.size(); ct++)
            s = s + fixedWidth(String.format(fStr, vals.get(ct)));
        return s;
    }
    /**
     * get the list of inputs of ct'th item from set ready for printing
     * @param ct
     * @return array list
     */
    protected ArrayList<Double> getPrintIns(int ct) {
        return getIns(ct);				// raw inputs needed here
    }
    /**
     * get the list of targets of ct'th item from set ready for printing
     * @param ct
     * @return array list
     */
    protected ArrayList<Double> getPrintTargets(int ct) {
        return getTargets(ct);			// raw targets needed here
    }
    /**
     * get the list of outputs of ct'th item from set ready for printing
     * @param ct
     * @return array list
     */
    public ArrayList<Double> getPrintOuts(int ct) {
        return getOuts(ct);				// raw outputs needed here
    }
    /**
     * get the ct'th target of all items in the data set
     * @param ct
     * @return array list of targets
     */
    public ArrayList<Double> getAllTargets(int ct) {
        ArrayList<Double> ans = new ArrayList<Double>();
        for (int item = 0; item < numInSet(); item++)
            ans.add(getPrintTargets(item).get(ct));
        return ans;
    }
    /**
     * get the ct'th output of all items in the data set
     * @param ct
     * @return array list of outputs
     */
    public ArrayList<Double> getAllOuts(int ct) {
        ArrayList<Double> ans = new ArrayList<Double>();
        for (int item = 0; item < numInSet(); item++)
            ans.add(getPrintOuts(item).get(ct));
        return ans;
    }
    private String namesToString(ArrayList<String>names) {
        String s = "";
        for (int ct=0; ct<names.size(); ct++) s = s + fixedWidth(names.get(ct));
        return s;
    }
    /**
     * Function to convert data into string; for each item output inputs, targets, and if req outputs
     * @param rawOuts		true if show the raw outputs of the data
     * @param withOuts		true if show the formatted outputs
     * @return String with info on each item separated by newline
     */
    public String toString(boolean rawOuts, boolean withOuts) {
        int ct;
        tWidth = numOutputs()>1 ? 7 : 9;							// set width in which numbers shown
        String s = fixedWidthCentre("Inputs", numInputs());			// first give titles
        s = s + fixedWidthCentre("Targets", numOutputs());
        if (rawOuts) s = s + fixedWidthCentre("Raw Ops", numOutputs());
        if (withOuts) s = s + fixedWidthCentre("Outputs", numOutputs());
        s = s + '\n';

        s = s + namesToString(inNames);								// now print names
        s = s + namesToString(outNames);
        if (rawOuts) s = s + namesToString(outNames);
        if (withOuts) s = s + namesToString(outNames);
        s = s + "\n";

        for (ct=0; ct<numInSet(); ct++) {							// now print each item in the set
            s = s + valstoString(getPrintIns(ct), inFormat);
            s = s + valstoString(getPrintTargets(ct), outFormat);
            if (rawOuts) s = s + valstoString(getOuts(ct), rawFormat);
            if (withOuts) s = s + valstoString(getPrintOuts(ct), outFormat);
            s = s + "\n";
        }
        return s;
    }

    /**
     * calculate the sum of the squares of errors for each output across the data set
     * @return	array list with the SSE for each output
     */
    public ArrayList<Double> getSSE() {
        ArrayList<Double> SSE = new ArrayList<Double>();	// create such a list
        for (int ct=0; ct<numOuts; ct++) SSE.add(0.0);		// for each output set SSE to 0
        for (int item=0; item<numInSet(); item++) {			// for each item in the set
            ArrayList<Double> errs = getErrors(item);		// get the errors for all outputs
            for (int ct=0; ct<numOuts; ct++) SSE.set(ct, SSE.get(ct) + errs.get(ct)*errs.get(ct));
            // add square of each error to total
        }
        for (int ct=0; ct<numOuts; ct++) SSE.set(ct, SSE.get(ct)/numInSet());
        // relate to size in set
        return SSE;											// return the result
    }

    /**
     * calculate sum of SSE for all outputs
     * @return	sum of SSEs
     */
    public double getTotalSSE() {
        ArrayList<Double> AllSSE = getSSE();		// get all SSEs
        double ans = 0.0;							// now add them all
        for (Double d : AllSSE) ans += d;
        return ans;

    }
    /**
     * calculate the number of outputs which are correctly classified (ie target = output)
     * @return	array list with number of items which are correctly classified
     */
    public ArrayList<Integer>getCorrect() {
        ArrayList<Integer> Correct = new ArrayList<Integer>();	// create list
        for (int ct=0; ct<numOuts; ct++) Correct.add(0);		// for each output, set sum to 0
        for (int item=0; item<numInSet(); item++) {				// for each item in the set
            ArrayList<Double> tars = getPrintTargets(item);		// get the targets (denormalised)
            ArrayList<Double> outs = getPrintOuts(item);		// and the outputs
            for (int ct=0; ct<numOuts; ct++) 					// for each output
                if (Math.round(tars.get(ct)) == Math.round(outs.get(ct)) )	// if target = output
                    Correct.set(ct, Correct.get(ct) + 1);	// increment count
        }
        for (int ct=0; ct<numOuts; ct++) 		// turn into %
            Correct.set(ct, Correct.get(ct)*100/numInSet());
        return Correct;
    }
    /**
     * generate analysis of results,
     *  	return sum of square of errors SSE
     *  and if a classification set, % of outputs correctly classified
     * @return string with these results
     */
    public String dataAnalysis() {
        ArrayList<Double> SSE = getSSE();		// get raw data
        String s = "SSE ";
        for (int ct=0; ct<numOuts; ct++) s = s + String.format("%.4f", SSE.get(ct)) + " ";
        // generate string with SSE of each output
        if (outFormat.equals("%.0f")) {			// if classification problem
            ArrayList<Integer> Correct = getCorrect();	// get number classified
            s = s + ": %Correct ";
            for (int ct=0; ct<numOuts; ct++) 		// add number correctly classified as a %
                s = s + Integer.toString(Math.round(Correct.get(ct))) + " ";
        }
        return s;			// return string with result
    }

    /**
     * return name of ct'th output
     * @param ct
     * @return	string with name
     */
    public String getOutName(int ct) {
        return outNames.get(ct);
    }

    /**
     * empty the arraylist which is the log of SSEs during training
     */
    public void clearSSELog() {
        sseLog.clear();
    }
    /**
     * What is the size of the log .. ie how many epochs have been taught
     * @return size
     */
    public int sizeSSELog() {
        return sseLog.size();
    }
    /**
     * Calculate the SSE following the lastest presentation of dataset, and add to the log
     */
    public void addToSSELog() {
        sseLog.add(getSSE());
    }
    /**
     * return an arraylist of the SSEs for the given output during training
     * @param whichOut
     * @return
     */
    public ArrayList<Double> getSSELog(int whichOut) {
        ArrayList<Double> ans = new ArrayList<Double>();			// create space
        for (int ct=0; ct<sizeSSELog(); ct++) ans.add(sseLog.get(ct).get(whichOut));
        // add the whichOut'th SSE to the list
        return ans;													// return answer
    }
    /**
     * @param args
     */
    public static void main(String[] args) {
        // test of dats set
        DataSet AllXor = new DataSet("2 3 %.0f %.0f %.2f;x1 x2 AND OR XOR;0 0 0 0 0;0 1 0 1 1;1 0 0 1 1;1 1 1 1 0");
        System.out.print(AllXor.toString(true, true));
        DataSet Other = new DataSet(DataSet.GetFile("other.txt"));
        System.out.print(Other.toString(true, true));
    }
}