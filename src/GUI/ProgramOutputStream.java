// taken from http://www.codejava.net/java-se/swing/redirect-standard-output-streams-to-jtextarea
package GUI;

import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

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
		ta.append(String.valueOf((char)b));
		//ta.setCaretPosition(ta.getDocument().getLength());
	}

}
