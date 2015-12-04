/**
 * Anton Kurylovich and Matt Billings
 * COSC 3P71 Assignment 3
 * 11/27/2015
 * Neural Network Parity Checking
 */
// taken from http://www.codejava.net/java-se/swing/redirect-standard-output-streams-to-jtextarea
package GUI;

import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

/**Creates a stream used to direct output from stdout to the textarea
 *
 */
public class ProgramOutputStream extends OutputStream {
	private JTextArea ta;
	
	public ProgramOutputStream(JTextArea textArea) {
		ta = textArea;
		//prevent caret from autoscrolling to bottom if too many lines are appended to fit in the text area.
		DefaultCaret caret = (DefaultCaret) ta.getCaret();
		caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
	}
	@Override
	public void write(int b) throws IOException {
		ta.setCaretPosition(ta.getDocument().getLength());
		ta.append(String.valueOf((char)b));
	}

}
