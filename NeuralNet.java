import java.util.Optional;
import java.util.Random;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class NeuralNet extends Application {

    LinearLayerNetwork net;								// variable for actual network
    DataSet trainData, unseenData, validData;			// variables for data sets used
    int numHidden = 10;									// number of neurons in hidden layer
    double learnRate = 0.2, momentum = 0.0;				// learning parameters
    String weightsString;								// string used to initialise weights
    String netName;										// string with name of network
    String studentNumber = "12345678";					// for identifiying user

    int numEpochs = 7;									// number of epochs used for training

    int ranSeed = 100;									// seed of random number generator
    Random rgen = new Random();							// random number generator

    Text netText;										// text : used to display results

    final Clipboard clipboard = Clipboard.getSystemClipboard();		// for copying to clipboard
    final ClipboardContent content = new ClipboardContent();

    GraphPlot netPlot;								// for plotting tadpole plot and SSE plot
    int graphX = 450; 									// width of plot
    int graphY = 200;									// height of one (of three) plots

    MenuItem mHidden;			// menuitem for hidden neurons ... here so can hide it

    /**
     * Function to show a message,
     * @param TStr		title of message block
     * @param CStr		content of message
     */
    private void showMessage(String TStr, String CStr) {
        Alert alert = new Alert(AlertType.INFORMATION);	// create dialog
        alert.setTitle(TStr);							// give it a title
        alert.setHeaderText(null);
        alert.setContentText(CStr);						// add content
        alert.showAndWait();							// display and wait for user to press ok
    }

    /**
     * prompt user to get string
     * @param TStr	title of dialog
     * @param PStr	string to prompt user
     * @param DStr	default version
     * @return
     */
    private String getDialog(String TStr, String PStr, String DStr) {
        TextInputDialog dialog = new TextInputDialog(DStr);
        dialog.setTitle(TStr);
        dialog.setHeaderText(PStr);
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) return result.get(); else return DStr;

    }
    /**
     * show welcome message, which asks for student number
     */
    private void showWelcome() {
        studentNumber = getDialog("Welcome to RJMs Neural Network!\n2019-20 version", "Please enter your student number", studentNumber);
    }

    /**
     * show about message
     */
    private void showAbout() {
        showMessage("About", "RJM's Perceptron neural network code\n"+
                "Implemented using Object Orientation\n"+
                "Have classes for layers of neurons\n"+
                "Network is made of one or more such layers\n"+
                "Data can be taught, results printed and plotted");
    }

    /**
     * Dialog allowing user to enter a new value for a variable
     * @param TStr				text string about variable
     * @param currValue			current value of it
     * @return					new value
     */
    private double getValue(String TStr, double currValue) {
        return Double.parseDouble(getDialog(TStr, "Enter value for "+TStr,String.format("%.2f", currValue) ));
    }

    /**
     * Dialog allowing user to enter a new value for a variable   version for integer
     * @param TStr				text string about variable
     * @param currValue		current value of it
     * @return					new value
     */
    private int getValue(String TStr, int currValue) {
        return Integer.parseInt(getDialog(TStr, "Enter value for "+TStr,Integer.toString(currValue) ));
    }

    /**
     * clear the text and the graph areas
     */
    private void clearGraph() {
        netPlot.clearGraph(0, graphY*3);
        netPlot.showUnique(studentNumber);
    }

    /**
     * draw Tadpole Plot
     * @param title			// title of graph
     * @param data			// relevant data
     * @param whichOut		// which output is to be used
     * @param whichY		// which of up to 3 graphs to be plotted
     */
    private void drawTadpole(String title, DataSet data, int whichOut, int whichY) {
        net.presentDataSet(data);
        netPlot.tadpolePlot(title, data.getAllTargets(whichOut), data.getAllOuts(whichOut), whichY*(graphY-10));
    }
    /**
     * draw SSE plot
     * @param title			// title of graph
     * @param data			// relevant data
     * @param whichOut		// which output is to be used
     * @param whichY		// which of up to 3 graphs to be plotted
     */
    private void drawSSE(String title, DataSet data, int whichOut, int whichY) {
        netPlot.yPlot(title, data.getSSELog(whichOut), whichY*(graphY-10));
    }
    /**
     * Display name of network and current value of learning parameters
     */
    private void showNetName() {
        String s = "Student " + studentNumber + '\n' + netName;
        if (net instanceof MLPwithDataSets ) s = s + " " + Integer.toString(numHidden) + " Hidden Neurons";
        netText.setText(s + " Learn Rate " + String.format("%.2f",learnRate) +
                " Momentum " + String.format("%.2f",momentum) +
                " Seed " + Integer.toString(ranSeed));
    }

    /**
     * initialise network at start of run
     */
    private void netInitialise() {
        rgen.setSeed(ranSeed);						// set the seed for random numbers
        net.doInitialise();							// initialise net
        // set weights from weightString or random
        if (weightsString.length()==0 || ranSeed>0) net.setWeights(rgen);
        else net.setWeights(weightsString);
    }

    /**
     * sets up GUI when a network has been set up
     * @param hideHidden	whether to hide option for number of hidden neurons
     */
    private void setupNet(boolean hideHidden) {
        netInitialise();							// initialise network
        netText.setText("");						// clear Text
        clearGraph();								// clear graphs
        mHidden.setDisable(hideHidden);				// is hidden option visible?
        if (studentNumber.equals("12345678"))
            showWelcome();
        else showMessage("Information", netName + " has been configured");	// show user about network
    }

    /**
     * set up a network comprising one layer of linear or sigmoidally activated neurons
     * @param isSig		true if sigmoid
     * @param isAndOrXor 	true if for and or xor, as opposed to other lin sep problem
     */
    private void setupLayerNet (boolean isSig, boolean isAndOrXor) {
        if (isAndOrXor)
            trainData = new DataSet("2 3 %.0f %.0f %.3f;x1 x2 AND OR XOR;0 0 0 0 0;0 1 0 1 1;1 0 0 1 1;1 1 1 1 0");
            // define data set with inputs x1,x2 and outputs AND OR XOR
        else trainData = new DataSet(DataSet.GetFile("other.txt"));

        unseenData = null;	// no unseen set
        validData = null;	// no validation set
        if (isAndOrXor == false) {
            net = new SigmoidLayerNetwork(2, 2, trainData);			// set sigmoid net, 2 ins 3 outs
            numEpochs = 500;								// default number of epochs
            netName = "Sigmoid Layer : Other";				// name it
        }
        else if (isSig) {
            net = new SigmoidLayerNetwork(2, 3, trainData);			// set sigmoid net, 2 ins 3 outs
            numEpochs = 1000;								// default number of epochs
            netName = "Sigmoid Layer Network";				// name it
        }
        else {
            net = new LinearLayerNetwork(2, 3, trainData); 			// add linear layer
            numEpochs = 10;
            netName = "Linear Layer Network";
        }
        if (isAndOrXor)
            weightsString = "0.2 0.5 0.3 0.3 0.5 0.1 0.4 0.1 0.2";	// define initial weights
        else
            weightsString = "0.1 0.2 -0.1 0.2 -0.1 0.3";
        setupNet(true);										// set up the network
    }
    /**
     * set up a MLP whether for XOR problem or other data problem
     * @param isXor		true if xor problem
     */
    private void setupMLPNet (boolean isXor) {
        if (isXor) {
            trainData = new DataSet("2 1 %.0f %.0f %.3f;x1 x2 XOR;0 0 0;0 1 1;1 0 1;1 1 0");
            // define training data for XOR
            weightsString = "0.862518 -0.155797 0.282885 0.834986 -0.505997 -0.864449 0.036498 -0.430437 0.481210";
            // set up Picton's weights
            ranSeed = 0;	// by default, use Picton's weights
            net = new MultiLayerNetwork(2, 2, trainData, new SigmoidLayerNetwork(2, 1, trainData));
            // create MLP, 2 ins, 2 hidden neurons, one output neuron
            netName = "MLP - XOR";
        }
        else {
            trainData = new DataSet("2 2 %.1f %.0f %.3f;x1 x2 c1 c2;"+
                    "0.1 1.2 1 0;0.7 1.8 1 0;0.8 1.6 1 0;0.8 0.6 0 0;1 0.8 0 0;"+
                    "0.3 0.5 1 1;0 0.2 1 1;-0.3 0.8 1 1;-0.5 -1.5 0 1;-1.5 -1.3 0 1");
            weightsString = "0.2 0.5 0.3 0.3 0.5 0.1 -0.2 0.5 0.1 0.4 -0.3 0.1 0.2 0.1 0.2 -0.4 0.5";
            net = new MultiLayerNetwork(2, 3, trainData, new SigmoidLayerNetwork(3, 2, trainData));
            // create MLP two inputs, three hidden neurons : set up data and weights
            netName = "MLP - Non Linear Separation";
        }
        unseenData = null;			// no unseen
        validData = null;			// or validation data
        numEpochs = 1000;			// number of epochs to learn
        setupNet(true);				// set up - no option to change number of hidden neurons
    }
    /**
     * function to configure net using the three data sets and having the appropriate number of hidden neurons
     */
    private void setHidden() {
        net = new MLPwithDataSets(trainData.numInputs(), numHidden, trainData,
                new SigmoidLayerNetwork(numHidden, trainData.numOutputs(), trainData), unseenData, validData);
        netInitialise();
    }
    /**
     * set up an MLP with the IRIS data set problem
     */
    private void setupMLPIris () {
        // first define training and unseen data - from files; no validation
        trainData = new ScaledDataSet(DataSet.GetFile("iristrain.txt"));
        unseenData = new ScaledDataSet(DataSet.GetFile("irisunseen.txt"));
        validData = null;

        weightsString = "";			// random weights only
        setHidden();			// next create MLP with appropriate number of neurons in hidden layer	
        netName = "MLP - IRIS";
        numEpochs = 1000;
        setupNet(false);			// set up - enable option to change number of hidden neurons
    }

    /**
     * set up an MLP to use the train, unseen and valid data sets from files
     */
    private void setupMLPThree (boolean isUser) {
        // set up data sets
        String preStr =  (isUser)? "user" : "";
        trainData = new ScaledDataSet(DataSet.GetFile(preStr+"train.txt"));
        unseenData = new ScaledDataSet(DataSet.GetFile(preStr+"unseen.txt"));
        validData = new ScaledDataSet(DataSet.GetFile(preStr+"valid.txt"));

        weightsString = "";
        setHidden();		// create MLP
        netName = (isUser)? "MLP - Users Train Valid and Unseen" : "MLP - Train Valid Unseen";
        numEpochs = 1000;
        setupNet(false);			// set up - enable option to change number of hidden neurons
    }

    /**
     * set up the menu of commands for the GUI
     * @return the menu bar
     */
    MenuBar setMenu() {
        MenuBar menuBar = new MenuBar();					// create main menu
        menuBar.setStyle("-fx-font-size: 14");				// font size to be used

        Menu mFile = new Menu("File");						// add File main menu
        MenuItem mExit = new MenuItem("Exit");				// whose sub menu has Exit
        mExit.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {				// action on exit
                System.exit(0);								// exit program
            }
        });
        mFile.getItems().addAll(mExit);					// add load, save and exit to File menu

        Menu mSelect = new Menu("Select Network");						// add Config main menu
        MenuItem mLinLayer = new MenuItem("Linear Layer");			// whose sub menu has Linear Layer
        mLinLayer.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {					// action on selecting this network
                setupLayerNet(false, true);
            }
        });
        MenuItem mSigLayer = new MenuItem("Sigmoid Layer");		// whose sub menu has Sigmoid Layer
        mSigLayer.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {					// action on selecting this network
                setupLayerNet(true, true);
            }
        });
        MenuItem mSigLayerOther = new MenuItem("Sig Layer : Other");	//  sub menu has Sigmoid Layer, other data
        mSigLayerOther.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {					// action on selecting this network
                setupLayerNet(true, false);
            }
        });
        MenuItem mMLPXor = new MenuItem("MLP : XOR");			// whose sub menu has MLP XOR
        mMLPXor.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {					// action on selecting this network
                setupMLPNet(true);
            }
        });
        MenuItem mMLPOther = new MenuItem("MLP : Other");		// whose sub menu has MLP Other
        mMLPOther.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {					// action on selecting this network
                setupMLPNet(false);
            }
        });
        MenuItem mMLPIris = new MenuItem("MLP : Iris");		// whose sub menu has MLP Iris
        mMLPIris.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {					// action on selecting this network
                setupMLPIris();
            }
        });
        MenuItem mMLPTrainUnseenValid = new MenuItem("MLP : Three Sets");		// whose sub menu has MLP
        mMLPTrainUnseenValid.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {					// action on selecting this network
                setupMLPThree(false);
            }
        });
        MenuItem mMLPUser = new MenuItem("MLP : Users Three Sets");		// whose sub menu has MLP
        mMLPUser.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {					// action on selecting this network
                setupMLPThree(true);
            }
        });
        mSelect.getItems().addAll(mLinLayer, mSigLayer, mSigLayerOther, mMLPXor, mMLPOther, mMLPIris, mMLPTrainUnseenValid, mMLPUser);
        // add all these items to mSelect main menu item

        Menu mParas = new Menu("Parameters");							// create Parameters menu
        MenuItem mLRate = new MenuItem("Learn Rate");					// add set learn rate sub menu item
        mLRate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                learnRate = getValue ("Learning Rate", learnRate);		// routine to get learning rate
            }
        });
        MenuItem mMomentum = new MenuItem("Momentum");					// add set momentum sub menu item
        mMomentum.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                momentum = getValue ("Momentum", momentum);
            }
        });
        mHidden = new MenuItem("Hidden Neurons");						// add hidden sub menu item
        mHidden.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                numHidden = getValue ("Number Hidden Neurons", numHidden);	// get num hidden neurons
                if (unseenData != null) setHidden();				// set MLP with 3 data sets with these num neurons
            }
        });
        mHidden.setDisable(true);										// by default hide this option
        MenuItem mSeed = new MenuItem("Random Seed");					// add seed sub menu item
        mSeed.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                ranSeed = getValue ("Seed for Random numbers", ranSeed);
                netInitialise();										// set weights accordingly
            }
        });
        MenuItem mEpoch = new MenuItem("Max Epochs");					// add seed sub menu item
        mEpoch.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                numEpochs = getValue ("Number of Epochs to learn", numEpochs);
            }
        });
        mParas.getItems().addAll(mLRate, mMomentum, mHidden, mEpoch, mSeed);


        Menu mHelp = new Menu("Help");									// create Help menu
        MenuItem mWelcome = new MenuItem("Welcome");					// add Welcome sub menu item
        mWelcome.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                showWelcome();											// whose action is to give welcome message
            }
        });
        MenuItem mAbout = new MenuItem("About");						// add About sub men item
        mAbout.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                showAbout();											// and its action to print about
            }
        });
        mHelp.getItems().addAll(mWelcome, mAbout);						// add Welcome and About to Run main item

        menuBar.getMenus().addAll(mFile, mSelect, mParas, mHelp);		// set main menu with File, Config, Run, Help
        return menuBar;													// return the menu
    }

    /**
     * set up the horizontal box for the bottom with relevant buttons
     * @return
     */
    private HBox setButtons() {
        Button btnInitialise = new Button("Initialise");				// button to initialise net
        btnInitialise.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {						// when pressed do this
                netInitialise();
                netText.setText("");
                clearGraph();
                showNetName();
            }
        });
        Button btnPresent = new Button("Present");						// button to present data
        btnPresent.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String s = net.doPresent();								// present
                netText.setText(netText.getText()+"\n"+s);				// display result as string
            }
        });
        Button btnLearn = new Button("Learn");							// button for learn
        btnLearn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String s = net.doLearn (numEpochs, learnRate, momentum);	// learn
                netText.setText(netText.getText()+"\n"+s);				// display result
            }
        });
        Button btnWeights = new Button("Weights");
        btnWeights.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String s = "Weights " + net.getWeights();				// get string with weights
                netText.setText(netText.getText()+"\n"+s);	        	// display weights
            }
        });
        Button btnClear = new Button("Clear text");						// clear text area
        btnClear.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                netText.setText("");
            }
        });
        Button btnClip = new Button("Text to Clipboard");				// to copy text to clipboard
        btnClip.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                content.putString(netText.getText());					// load content with string
                clipboard.setContent(content);							// copy to clipboard
                showMessage("Information", "Text copied to Clipboard");	// say have done so
            }
        });
        Button btnPlot = new Button("Tadpole Plots");					// plot tadpole plots
        btnPlot.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                clearGraph();
                if (unseenData == null)									// if no unseen data
                    for (int ct=0; ct<trainData.numOutputs(); ct++)		// plot each output
                        drawTadpole(trainData.getOutName(ct), trainData, ct, ct);
                else {
                    drawTadpole("Train", trainData,0, 0);				// plot training data
                    drawTadpole("Unseen", unseenData,0, 1);				// unseen data
                }
                if (validData != null)
                    drawTadpole("Valid", validData,0, 2);				// and if there validation
            }
        });
        Button btnSSEPlot = new Button("SSE Plots");					// plot tadpole plots
        btnSSEPlot.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                clearGraph();
                if (unseenData == null)									// if no unseen data
                    for (int ct=0; ct<trainData.numOutputs(); ct++)		// plot each output
                        drawSSE(trainData.getOutName(ct), trainData, ct, ct);
                else {
                    drawSSE("Train", trainData,0, 0);					// plot training data
                    if (validData != null)
                        drawSSE("Valid", validData,0, 1);				// and if there validation
                }
            }
        });
        Button btnGraphClip = new Button("Graphs to Clipboard");		// copy graph to clipboard
        btnGraphClip.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (netPlot.graphShown()) {								// do so only if graph plotted
                    content.clear();									// clear content
                    content.putImage(netPlot.getCanvasImage());			// copy graph to content
                    clipboard.setContent(content);						// and content tio clipboard
                    showMessage("Information", "Graphs copied to Clipboard");	// say done so
                }
                else showMessage("Information", "No Graphs there to be copied");
            }
        });
        HBox hbox = new HBox(new Label ("Operate: "), btnInitialise, btnPresent, btnLearn, btnWeights,
                new Label ("  Other:"), btnClear, btnClip, btnPlot, btnSSEPlot, btnGraphClip);
        // add buttons and text to hbox
        hbox.setStyle("-fx-font-size: 14");			// set style for hbox
        return hbox;								// return box with buttons
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("RJMs Perceptron Neural Network Program");
        BorderPane bp = new BorderPane();						// set up borderpane for GUI
        bp.setPadding(new Insets(10, 20, 10, 20));
        bp.setTop(setMenu());									// put menu at the top
        bp.setBottom(setButtons());								// buttons at bottom
        netText = new Text();									// Text for displaying results
        netText.setText("");
        netText.setFont(new Font("Lucida Console", 14));		// use fixed width font
        Group root = new Group(netText);						// put in a group
        ScrollPane scrollPane = new ScrollPane();				// have pane with scroll bars
        scrollPane.setContent(root);							// put group with text in scrollpane
        bp.setCenter (scrollPane);								// put in centre area

        Group groot = new Group();								// create group for canvas
        Canvas canvas = new Canvas(graphX, graphY*3);			// create canvas for plots
        groot.getChildren().add( canvas );						// add canvas to it
        bp.setRight(groot);										// load canvas to left area

        netPlot = new GraphPlot(canvas.getGraphicsContext2D(), graphX, graphY);
        // create object for doing tadpole plots
        setupLayerNet(false, true);									// set up a default net

        Scene scene = new Scene(bp, 1200, 700);					// overall scene for system
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

}