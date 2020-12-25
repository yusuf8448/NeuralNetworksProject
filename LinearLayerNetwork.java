
import java.util.ArrayList;
import java.util.Random;

/**
 * @author shsmchlr
 * This is a class for a layer of neurons with linear activation
 * All such neurons share the same inputs.
 */
public class LinearLayerNetwork {
    /**
     * data are arraylists of weights and the change in weights
     * and of the outputs and deltas
     * and also how many inputs, neurons and weights
     * also has data set used with network
     */
    protected ArrayList<Double> weights;
    protected ArrayList<Double> changeInWeights;
    protected ArrayList<Double> outputs;
    protected ArrayList<Double> deltas;
    protected int numInputs, numNeurons, numWeights;
    protected DataSet trainData;

    /**
     * Constructor for neuron
     * @param numIns	how many inputs there are (hence how many weights needed)
     * @param numOuts	how many outputs there are (hence how many neurons needed)
     * @param data		the data set used to train the network
     */
    public LinearLayerNetwork(int numIns, int numOuts, DataSet data) {
        numInputs = numIns;							// store number inputs
        numNeurons = numOuts;						// and of outputs in object
        numWeights = (numInputs + 1) * numNeurons;	// for convenience calculate number of weights
        // each neuron has numInputs + 1 weights (+1 because of bias weight)

        weights = new ArrayList<Double>();			// create array list for weights
        changeInWeights = new ArrayList<Double>();	// and for the change in weights
        outputs = new ArrayList<Double>();			// create array list for outputs
        deltas = new ArrayList<Double>();			// and for the change in deltas
        for (int ct=0; ct<numWeights; ct++) {		// for each weight
            weights.add(0.0);						// add next weight as 0
            changeInWeights.add(0.0);				// add next change in weight, value 0
        }
        for (int ct=0; ct<numNeurons; ct++) {		// for each neuron
            outputs.add(0.0);						// add a zero output
            deltas.add(0.0);						// add a zero delta
        }
        trainData = data;							// remember data set used for training
    }
    /**
     * calcOutputs of neuron
     * @param nInputs	arraylist with the neuron inputs
     * Calculates weighted sum being weight(0) + inputs(0..n) * weights(1..n+1)
     */
    protected void calcOutputs(ArrayList<Double> nInputs) {
        int wtIndex = 0;									// used to index weights in order
        double output;
        for (int neuronct = 0; neuronct<numNeurons; neuronct++) {
            output = weights.get(wtIndex++);						// start with bias weight( * 1)
            for (int inputct=0; inputct<numInputs; inputct++) 		// for remaining weights
                output += nInputs.get(inputct) * weights.get(wtIndex++);
            // add weight*appropriate input and move to next weight
            outputs.set(neuronct, output);			// set calculated output as the neuron output
        }
    }
    /**
     * outputsToDataSet to the given data set
     * @param ct	which item in the data set
     * @param d		the data set
     */
    protected void outputsToDataSet (int ct, DataSet d) {
        d.setOutputs(ct, outputs);							// just store outputs in data set
    }

    /**
     * compute outputs of network by passing it each item in data set in turn,
     * these outputs are put back into the data set
     * @param d	data set
     */
    public void presentDataSet(DataSet d) {
        for (int ct=0; ct < d.numInSet(); ct++) {			// for each item in data set
            calcOutputs(d.getIns(ct));						// calculate output
            outputsToDataSet(ct, d);							// and put in data set
        }
    }

    /**
     * find deltas
     *	@param errors
     */
    protected void findDeltas(ArrayList<Double> errors) {
        // use what is in errors to set the delta for each neuron in the layer
        for(int nct = 0; nct < numNeurons; nct++){
            for (int ct=0; ct<deltas.size(); ct++) {
                deltas.set(ct, errors.get(ct)); 		//ToDo changed this to change each of the values of the deltas to equal the values of the errors of the same counth value
            }
        }


    }

    /**
     * return index of weight ...
     * @param wNeuron
     * @param wWeight
     * @return
     */
    private int weightIndex (int wNeuron, int wWeight) { // this needs to be implemented
        int index = wWeight + wNeuron*(numWeights/numNeurons);// this changes the index into a value of within the limits
        return index;
        //ToDO not 100% sure what I am doing here
        // change this to return the index into weights list of the neuron
    }

    /**
     * change a given weight
     * @param wtIndex
     * @param theIn
     * @param delta
     * @param learnRate
     * @param momentum
     */
    private void changeOneWeight(int wtIndex, double theIn, double delta, double learnRate, double momentum) {
        // write code to update the changeInWeight and the Weight of the wtIndex'th weight
        changeInWeights.set(wtIndex, theIn * delta * learnRate + changeInWeights.get(wtIndex) * momentum);
        //ToDo this line sets the weight passing the input*delta*learning rate + prevChangeWeight*moment
        weights.set(wtIndex, weights.get(wtIndex ) + changeInWeights.get(wtIndex));
        //ToDo need to finish this and function below
    }

    /**
     * change all the weights in the layer of neurons
     * @param ins
     * @param learnRate
     * @param momentum
     */
    protected void changeAllWeights(ArrayList<Double> ins, double learnRate, double momentum) {
        // write code to change all weights in the layer
        double theIn;// ToDo does not currently account for the number of neurons, this will only loop through one neuron
        for(int nct = 0; nct < numNeurons; nct++) {// loops through each neuron
            for (int wct = 0; wct < ins.size() + 1; wct++) {// +1 to include the bias weight
                if (wct == 0) theIn = 1.0;
                else theIn = ins.get(wct - 1); // this says if the count is 0 it will use the bias weight
                changeOneWeight(weightIndex(nct, wct), theIn, deltas.get(nct), learnRate, momentum);        // change the wct's weight
                //ToDo, the function has been changed to deltas.size() from weights.size()
            }
        }

    }

    /**
     * adapt the network, by inputting each item from the data set in turn, calculating
     * the output, the error and delta, and adjusting all the weights
     * @param d			data set
     * @param learnRate	learning rate constant
     * @param momentum	momentum constant
     */
    public void learnDataSet(DataSet d, double learnRate, double momentum) {
        for (int ct=0; ct < d.numInSet(); ct++) {				// for each item in set
            calcOutputs(d.getIns(ct));							// calc outputs
            outputsToDataSet(ct, d);								// put in data set
            findDeltas(d.getErrors(ct));						// calc deltas, from the errors
            changeAllWeights(d.getIns(ct), learnRate, momentum);// change the weights
        }
        d.addToSSELog();
    }

    /**
     * return the array list containing the outputs of this layer of neurons
     * @return
     */
    protected ArrayList<Double> getOutputs() {
        return outputs;
    }

    /**
     * finding errors in this layer ...
     * @return
     */
    public ArrayList<Double> weightedDeltas() {
        ArrayList<Double> wtDeltas = new ArrayList<Double>();	// create array for answer
        // write code here to populate the array list

        return wtDeltas;
    }

    /**
     * Load weights with the values in the array of strings wtsSplit
     * @param wtsSplit
     */
    protected void setWeights (String[] wtsSplit) {
        for (int ct=0; ct<weights.size(); ct++) weights.set(ct, Double.parseDouble(wtsSplit[ct]));
    }			// for each item, set weight by converting string to double
    /**
     * Load the weights with the values in the String wts
     * @param wts
     */
    public void setWeights (String wts) {
        setWeights(wts.split(" "));			// split string into array of string and so set weights
    }
    /**
     * Load the weights with random values in range -1 to 1
     * @param rgen	random number generator
     */
    public void setWeights (Random rgen) {
        for (int ct=0; ct<weights.size(); ct++) weights.set(ct,2.0*rgen.nextDouble() - 1);
    }
    /**
     * return how many weights there are in the neuron
     * @return
     */
    public int getNumWeights() {

        return numWeights;			// use number of weights variable
    }
    /**
     * return all the weights in the layer as a string each separated by spaces
     * @return the string
     */
    public String getWeights() {
        String s = "";									// set string to empty
        for (int ct=0; ct<weights.size(); ct++) {
            s = s + String.format("%.5f", weights.get(ct)) + " ";           // ToDo: this should return the weights as entered (check = success)
        }
        return s;
    }
    /**
     * initialise network before running
     */
    public void doInitialise() {
        for (int ct=0; ct<changeInWeights.size(); ct++) changeInWeights.set(ct, 0.0);
        // set the change in weights to be 0
        trainData.clearSSELog();
    }
    /**
     * present the data to the network and return string describing result
     * @return
     */
    public String doPresent() {
        presentDataSet(trainData);
        return trainData.toString(true, true) + "\nOver Set : " + trainData.dataAnalysis()+"\n";
    }


    /**
     * create string which says Epoch then adds the actual epoch in a fixed width field
     * @param epoch
     * @return
     */
    protected String addEpochString (int epoch) {
        return "Epoch " + String.format("%4d", epoch);
    }

    /**
     * get network to learn for numEpochs
     * @param numEpochs		number of epochs to learn
     * @param lRate			learning rate
     * @param momentum		momentum
     * @return				String with data about learning eg SSEs at relevant epochs
     * 						At each epoch if numEpochs low, or do so at 10 of the epochs
     */
    public String doLearn (int numEpochs, double lRate, double momentum) {
        int epochsSoFar = trainData.sizeSSELog();		// SSE log indicates how many epochs so far
        String s = "";
        for (int ct=1; ct<=numEpochs; ct++) {			// for n epochs
            learnDataSet(trainData, lRate, momentum);	// present data and adapt weights
            if (numEpochs<20 || ct % (numEpochs/10) == 0) // print appropriate number of times
                s = s + addEpochString(ct+epochsSoFar) + " : " + trainData.dataAnalysis()+"\n";
        }				// Epoch, and SSE, and if appropriate % correctly classified
        return s;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // Test network on example data set
        DataSet AndOrXor = new DataSet("2 3 %.0f %.0f %.3f;x1 x2 AND OR XOR;0 0 0 0 0;0 1 0 1 1;1 0 0 1 1;1 1 1 1 0");
        LinearLayerNetwork LN = new LinearLayerNetwork(2, 3, AndOrXor);
        LN.setWeights("0.2 0.5 0.3 0.3 0.5 0.1 0.4 0.1 0.2");
        LN.doInitialise();
        System.out.println(LN.doPresent());
        System.out.println("Weights " + LN.getWeights());
        System.out.println(LN.doLearn(10,  0.2,  0.1));
        System.out.println(LN.doPresent());
        System.out.println("Weights " + LN.getWeights());

    }

}