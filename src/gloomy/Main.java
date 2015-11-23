package gloomy;

public class Main {

	public static void main(String[] args) {
//		NeuralNet test = new NeuralNet(.5, 4);
		//test.printNetwork();
		//test.train();
//		test.run();
		//test.printNetwork();
		
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
