package GUI;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class TrainingListener implements DocumentListener{
	GUI g;
	
	public TrainingListener(GUI g) {
		this.g = g;
	}
	
	@Override
	public void insertUpdate(DocumentEvent e) {
		listenerHelper(e);
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		listenerHelper(e);
	}

	@Override
	public void changedUpdate(DocumentEvent e) { }	//plain text components don't fire this event

	private void listenerHelper(DocumentEvent e) {
		try {
			// check to see if value for widget is the same one the ANN is currently trained for
			if (e.getDocument().getProperty("owner").equals(g.txtLearningRate) && Double.valueOf(g.txtLearningRate.getText()) == g.learningRate) {
				g.btnTrain.setEnabled(false);
			}
			else {
				g.btnTrain.setEnabled(true);
			}
		}
		catch (Exception ex) {	}
	}
}
