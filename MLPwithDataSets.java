public class MLPwithDataSets extends MultiLayerNetwork {

    protected DataSet unseenData;			// unseen data set
    protected DataSet validationData;		// validation set : is set to null if that set is not being used

    /**
     * Constructor for the MLP
     * @param numIns			number of inputs	of hidden layer
     * @param numOuts			number of outputs	of hidden layer
     * @param data				training data set used
     * @param nextL				next layer
     * @param unseen			unseen data set
     * @param valid				validation data set
     */
    MLPwithDataSets (int numIns, int numOuts, DataSet data, LinearLayerNetwork nextL,
                     DataSet unseen, DataSet valid) {
        super(numIns, numOuts, data, nextL);	// create the MLP
        // and store the data sets
        unseenData = unseen;
        validationData = valid;
    }

    /**
     * initialise network before learning ...
     */
    public void doInitialise() {
        super.doInitialise();
        unseenData.clearSSELog();
        if (validationData != null) validationData.clearSSELog();

    }
    /**
     * present the data to the set and return a String describing results
     * Here it returns the performance when the training, unseen (and if available) validation
     * sets are passed - typically responding with SSE and if appropriate % correct classification
     */
    public String doPresent() {
        String S;
        presentDataSet(trainData);
        S = "Train: " +  trainData.dataAnalysis();
        presentDataSet(unseenData);
        S = S + " Unseen: " + unseenData.dataAnalysis();
        if (validationData != null) {
            presentDataSet(validationData);
            S = S + " Valid: " + validationData.dataAnalysis();
        }
        return S;
    }

    /**
     * learn training data, printing SSE at 10 of the epochs, evenly spaced
     * if a validation set available, learning stops when SSE on validation set rises
     * this check is done by summing SSE over 10 epochs
     * @param numEpochs		number of epochs
     * @param lRate			learning rate
     * @param momentum		momentum
     * @return				String with data about learning eg SSEs at relevant epoch
     */
    public String doLearn (int numEpochs, double lRate, double momentum) {
        String s = "";
        if (validationData==null) s = super.doLearn(numEpochs, lRate, momentum);
            // if no validation set, just use normal doLearn
        else {
            s = super.doLearn(numEpochs, lRate, momentum);
            // delete the above and write and comment code to use validation
        }

        return s;											// return string showing learning
    }

}