/**
 * Anton Kurylovich and Matt Billings
 * COSC 3P71 Assignment 3
 * 11/27/2015
 * Neural Network Parity Checking
 */
package GUI;

import gloomy.NeuralNet;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.PrintStream;
import java.text.NumberFormat;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.Border;

@SuppressWarnings("serial")
public class GUI extends JFrame {

	NeuralNet ann;
	boolean isTrained = false;
	double learningRate;
	double trainingRatio;
	
	JButton btnTrain;
	JTextArea outputArea;
	JTextField txtLearningRate;
	JFormattedTextField txtTrainingRatio;	
	JTextField txtSeed;				
	JTextArea trainingStatus;
	
	public GUI() {

		// pad the content of the top-level container
		getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 2, 10));	// for padding
		
		// input area widgets
		txtLearningRate = new JTextField("0.1");
		txtLearningRate.setToolTipText("Enter a number between 0 and 1");
		txtLearningRate.getDocument().putProperty("owner", txtLearningRate);		//allows identification of widget by DocumentEvent
		txtTrainingRatio = new JFormattedTextField(NumberFormat.getNumberInstance());
		txtTrainingRatio.setToolTipText("The percentage of the data set that the ANN will be trained on."
				+ " Must be at least 50% of the data set.");
		txtTrainingRatio.setText("100");
		txtTrainingRatio.getDocument().putProperty("owner", txtTrainingRatio);
		
		// section for outputting training status to user
		JPanel trainingStatusPanel = new JPanel();
		trainingStatusPanel.setLayout(new BoxLayout(trainingStatusPanel, BoxLayout.Y_AXIS));
		trainingStatusPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE));
		trainingStatus = new JTextArea("Training has not been performed");
		trainingStatus.setLineWrap(true);
		trainingStatus.setEditable(false);
		trainingStatus.setOpaque(false);
		trainingStatus.setWrapStyleWord(true);
		trainingStatus.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		trainingStatusPanel.add(trainingStatus);
		
		// configure output area
		outputArea = new JTextArea();
		outputArea.setLineWrap(true);
		outputArea.setWrapStyleWord(true);
		outputArea.setEditable(false);
		outputArea.append(String.format("%d-%d-%d network\n", 
				NeuralNet.getInputLayerNodes(), NeuralNet.getHiddenLayerNodes(), NeuralNet.getOutputLayerNodes()));
		JScrollPane outputScrollPane = new JScrollPane(outputArea);
		outputScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		outputScrollPane.setPreferredSize(new Dimension(250, 250));
		
		// change console output stream so that info is written to the output text area
		PrintStream ps = new PrintStream(new ProgramOutputStream(outputArea));
		System.setOut(ps);
		
		// LISTENERS
		
		txtLearningRate.getDocument().addDocumentListener(new TrainingListener(this));
		txtTrainingRatio.getDocument().addDocumentListener(new TrainingListener(this));
		
		btnTrain = new JButton(new AbstractAction("Train") {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				String errorMsg = validateInput();
				
				// train the neural net if all inputs are valid
				if (errorMsg.isEmpty()) {
					ann = new NeuralNet(learningRate, trainingRatio);
					ann.train();
					trainingStatus.setText(String.format("Trained with learning rate %.2f and %d epochs", 
							learningRate, ann.getMaxEpoch()));
					btnTrain.setEnabled(false);
					isTrained = true;
				}
			}
		});

		JButton btnRun = new JButton(new AbstractAction("Run") {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (isTrained) {
					ann.run();
				}
			}
		});
		
		JPanel userInputs = new JPanel(new GridLayout(2, 2, 0, 3));

		// add components to userInput container
		userInputs.add(new JLabel("Learning Rate:  "));
		userInputs.add(txtLearningRate);
		userInputs.add(new JLabel("Training Ratio (%): "));
		userInputs.add(txtTrainingRatio);
		
		JPanel buttonPanel = new JPanel(new GridLayout(1,2));	// use JPanel to center button within frame
		buttonPanel.add(btnTrain);
		buttonPanel.add(btnRun);
		
		// add remaining adjacent level components to top-level container
		Border margin = BorderFactory.createEmptyBorder(0, 6, 6, 6);
		JPanel leftPanel = new JPanel(new GridLayout(3,1,0,10));
		leftPanel.add(userInputs, BorderLayout.PAGE_START);
		leftPanel.add(buttonPanel, BorderLayout.CENTER);
		leftPanel.add(trainingStatusPanel, BorderLayout.PAGE_END);
		leftPanel.setBorder(margin);
		JPanel rightPanel = new JPanel(new GridLayout(1,1));
		rightPanel.setBorder(margin);
		rightPanel.add(outputScrollPane);
		
		add(leftPanel, BorderLayout.LINE_START);	// userInputs does not have a size up until this point
		add(rightPanel, BorderLayout.LINE_END);
	}
	
	/**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
	 */
	public static void createAndShowGUI() {
		// create and set up the window
		GUI appGUI = new GUI();
		appGUI.setTitle("ANN 4-bit Parity Check");
		appGUI.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		appGUI.setResizable(false);
		
		// display the window
		appGUI.pack();
		appGUI.setVisible(true);
	}
	
	/**Evaluates an individual input field for correctness
	 * @return a boolean indicating whether the input tested is valid according to the developer's constraints
	 */
	private boolean validate(String s, InputType t) {
		boolean result = false;

		if (t.equals(InputType.LEARNING_RATE)) {
			Pattern pattern = Pattern.compile("[01]?\\.\\d*");
			boolean patternMatches = pattern.matcher(s).matches();
			
			if (patternMatches && Double.valueOf(s) >= 0 && Double.valueOf(s) <= 1)
				result = true;
		}
		else if (t.equals(InputType.MAX_EPOCHS)) {
			try {
				Integer.parseInt(s);
				result = true;
			} 
			catch (Exception e) { }
		}
		else if (t.equals(InputType.TRAINING_RATIO)) {
			if (!s.contains(",")){
				int percentage = Integer.valueOf(s);
				if (percentage <= 100 && percentage >= 50) {
					result = true;
				}
			}
		}
			
		return result;
	}
	
	/**Checks all widgets for valid input that require it
	 * @return a String containing the errors encountered during validation
	 */
	private String validateInput() {
		String errorMsg = "";
		
		// validate components that need it one by one
		if (validate(txtLearningRate.getText().toString(), InputType.LEARNING_RATE)) {
			learningRate = Double.valueOf(txtLearningRate.getText().toString());
		}
		else { 
			errorMsg += "Invalid learning rate.\n"; 
		}
		
		if (validate(txtTrainingRatio.getText().toString(), InputType.TRAINING_RATIO)) {
			trainingRatio = (double) Integer.valueOf(txtTrainingRatio.getText().toString()) / 100;
		}
		else {
			errorMsg += "Invalid training ratio.\n";
		}
		
		// train the neural net if all inputs are valid
		if (!errorMsg.isEmpty()) {
			JOptionPane.showMessageDialog(null, errorMsg + "\nSee tooltips for more information");
		}
		
		return errorMsg;
	}
	
	// used to provide values for checks
	enum InputType {
		LEARNING_RATE,
		MAX_EPOCHS,
		TRAINING_RATIO
	}
}
