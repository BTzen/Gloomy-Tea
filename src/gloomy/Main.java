package gloomy;

public class Main {

	public static void main(String[] args) {
		NeuralNet test = new NeuralNet(4);
		test.printNetwork();
		System.out.println("=================================================\n");
		test.train();
		test.printNetwork();
	}

}
