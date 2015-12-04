/**
 * Anton Kurylovich and Matt Billings
 * COSC 3P71 Assignment 3
 * 11/27/2015
 * Neural Network Parity Checking
 */
package gloomy;

import GUI.GUI;

public class Main {

	public static void main(String[] args) {		
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				GUI.createAndShowGUI();				
			}
		});
	}

}
