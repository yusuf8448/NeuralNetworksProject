import java.util.ArrayList;


/**
 * @author shsmchlr
 * This is a class for a layer of neurons with sigmoidal activation
 * All such neurons share the same inputs.
 */
public class SigmoidLayerNetwork extends LinearLayerNetwork {

    /**
     * Constructor for neuron
     * @param numIns	how many inputs there are (hence how many weights needed)
     * @param numOuts	how many outputs there are (hence how many neurons needed)
     * @param data		the data set used to train the network
     */
    public SigmoidLayerNetwork(int numIns, int numOuts, DataSet data) {
        super(numIns, numOuts, data);
    }

    /**
     * calcOutputs of neuron
     * @param nInputs
     */
    protected void calcOutputs(ArrayList<Double> nInputs) {
        // write code here
        int wtIndex = 0;									// used to index weights in order
        double output;
        for (int neuronct = 0; neuronct<numNeurons; neuronct++) {
            output = weights.get(wtIndex++);						// start with bias weight( * 1)
            for (int inputct=0; inputct<numInputs; inputct++) 		// for remaining weights
                output += nInputs.get(inputct) * weights.get(wtIndex++);
            output = 1.0/(1.0 + Math.exp(-output)); //ToDo this line of code was the only one added from Linear Neurons code
            // add weight*appropriate input and move to next weight
            outputs.set(neuronct, output);			// set calculated output as the neuron output
        }
    }
    /**
     * find deltas
     *	@param errors
     */
    protected void findDeltas(ArrayList<Double> errors) {
        for (int ct=0; ct<deltas.size(); ct++) {

            double error = errors.get(ct);
            double x = 1/(1 + Math.exp(outputs.get(ct))); // ToDo x is the sigmoid function of the weights, where Math.exp(sum_of_outputs)
            error = error*x*(1-x); //ToDo this value has been added to create the change in
            deltas.set(ct, error); //ToDo changed this to change each of the values of the deltas to equal the values of the errors of the same counth value
        }
        // write code here
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // test with and or xor
        DataSet AndOrXor = new DataSet("2 3 %.0f %.0f %.3f;x1 x2 AND OR XOR;0 0 0 0 0;0 1 0 1 1;1 0 0 1 1;1 1 1 1 0");
        SigmoidLayerNetwork SN = new SigmoidLayerNetwork(2, 3, AndOrXor);
        SN.setWeights("0.2 0.5 0.3 0.3 0.5 0.1 0.4 0.1 0.2");
        SN.doInitialise();
        System.out.println(SN.doPresent());
        System.out.println("Weights " + SN.getWeights());
        System.out.println(SN.doLearn(1000,  0.2,  0.1));
        System.out.println(SN.doPresent());
        System.out.println("Weights " + SN.getWeights());

    }

}