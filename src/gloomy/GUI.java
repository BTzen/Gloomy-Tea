package gloomy;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javafx.scene.Parent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.text.MaskFormatter;

public class GUI extends JFrame {

	NeuralNet ann;
	double learningRate;
	int maxEpochs;
	
	JTextField txtLearningRate;
	//List<JComponent> componentsToValidate; 	//currently not used
	JTextField txtMaxEpochs;
	JTextArea trainingStatus;
	//training:test ratio
	//input each pattern and the actual output, and raw training accuracy for training and testing set.
	
	public GUI() {
		JPanel userInputs = new JPanel(new GridLayout(2, 2, 0, 3));

		// setup top level container
		setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
		getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 2, 10));
		
		// create J-Panel for border around information displayed to user
		JPanel outputPanel = new JPanel();
		outputPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE));
		
		//componentsToValidate = new ArrayList<>();
		txtLearningRate = new JTextField("0.1");
		txtLearningRate.setToolTipText("Enter a number between 0 and 1");
		txtMaxEpochs = new JTextField("10");
		trainingStatus = new JTextArea("Training has not been performed");
		trainingStatus.setLineWrap(true);
		trainingStatus.setEditable(false);
		trainingStatus.setOpaque(false);
		
		@SuppressWarnings("serial")
		JButton btnTrain = new JButton(new AbstractAction("Train") {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				String errorMsg = validateInput();
				
				// train the neural net if all inputs are valid
				if (errorMsg.isEmpty()) {
					ann = new NeuralNet(learningRate, maxEpochs);
					ann.train();
					trainingStatus.setText(String.format("ANN currently trained with learning rate %f and %d epochs", 
							learningRate, maxEpochs));
				}
			}
		});
		//TODO need to get it to put actual output somewhere
		JButton btnRun = new JButton(new AbstractAction("Run") {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				String errorMsg = validateInput();
				
				// train the neural net if all inputs are valid
				if (errorMsg.isEmpty()) {
					//if we're running it, then the neural net shouldn't be null
					if (ann != null) {
						ann.run();
//						trainingStatus.setText(String.format("Input: [%d, %d, %d, %d]\nExpected Output: %n\nActual Output: %3f", 
//								args));
					}
					ann = new NeuralNet(learningRate, maxEpochs);
				}
			}
		});
		
		// add components to this container
		userInputs.add(new JLabel("Learning Rate:    "));
		userInputs.add(txtLearningRate);
		userInputs.add(new JLabel("Maximum Epochs:   "));
		userInputs.add(txtMaxEpochs);
		
		
		
		JPanel buttonPanel = new JPanel();	// use JPanel to center button within frame
		//btnRun.setPreferredSize(new Dimension(100, btnRun.getPreferredSize().height));	// all this nonsense was to make the button a bit wider
		buttonPanel.add(btnTrain);
		buttonPanel.add(btnRun);
		
		trainingStatus.setPreferredSize(new Dimension(260, 100));	// make the area about as big as its panel
		outputPanel.add(trainingStatus);
		//add(Box.createRigidArea(new Dimension(0, 5)));
		
		// add remaining adjacent level components to top-level container
		add(userInputs);	// userInputs does not have a size up until this point
		add(buttonPanel);
		add(outputPanel);
	}
	
	/**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
	 */
	static void createAndShowGUI() {
		// create and set up the window
		GUI appGUI = new GUI();
		appGUI.setTitle("ANN 4-bit Parity Check");
		appGUI.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		appGUI.setResizable(false);
		
		// display the window
		appGUI.setSize(300,180);
		appGUI.setVisible(true);
	}
	
	/**Evaluates an individual input field for correctness
	 * @return a boolean indicating whether the input tested is valid according to the developer's constraints
	 */
	private boolean validate(String s, InputType t) {
		boolean result = false;

		if (t.equals(InputType.LEARNING_RATE)) {
			Pattern pattern = Pattern.compile("[01]?.\\d*");
			result = pattern.matcher(s).matches();
			
			if (result && Double.valueOf(s) >= 0 && Double.valueOf(s) <= 1)
				result = true;
		}
		else if (t.equals(InputType.MAX_EPOCHS)) {
			try {
				Integer.parseInt(s);
				result = true;
			} 
			catch (Exception e) {
				result = false;
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
		
		if (validate(txtMaxEpochs.getText().toString(), InputType.MAX_EPOCHS)) {
			maxEpochs = Integer.valueOf(txtMaxEpochs.getText().toString());
		}
		else {
			errorMsg += "Invalid number of epochs";
		}
		
		// train the neural net if all inputs are valid
		if (!errorMsg.isEmpty()) {
			JOptionPane.showMessageDialog(null, errorMsg + "\n\nSee tooltips for more information");
		}
		
		return errorMsg;
	}
	
//	private MaskFormatter createFormatter(String s) {
//		MaskFormatter formatter = null;
//		
//		try { formatter = new MaskFormatter(s);	}
//		catch (ParseException e) {
//			System.err.println("Invalid formatter: " + e.getMessage());
//		}
//		return formatter;
//	}

	// used to provide values for checks
	enum InputType {
		LEARNING_RATE,
		MAX_EPOCHS
	}
}
