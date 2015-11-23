package gloomy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**Using a ANN to determine the parity bit for a 4-bit pattern using even parity 
 * (odd # of 1s produces a 1 for the parity bit)
 * @author Anton, Matt
 *
 */
public class NeuralNet {

	final static int INPUT_LAYER_NODES = 4;
	final static int  HIDDEN_LAYER_NODES = 4;		
	final static int OUTPUT_LAYER_NODES = 1;
	final static long SEED = 4334562;
	final double learningRate;
	final int maxEpoch;
	
	private List<Neuron> neurons;
	private int[] networkSize; 		// # of indices represent the # of layers and the value at each index the # of neurons in that layer
	private List<int[]> examples;	// holds all 4 bit combinations
	private List<int[]> trainingSet;
	private int correctClassifications;
	private Random rnd;					

	public NeuralNet(double learningRate, int maxEpochs) {
		
		maxEpoch = maxEpochs;
		this.learningRate = learningRate;
		correctClassifications = 0;
		rnd = new Random(SEED);
		neurons = new ArrayList<Neuron>();
		networkSize = new int[] { INPUT_LAYER_NODES, HIDDEN_LAYER_NODES, OUTPUT_LAYER_NODES };
		examples = new ArrayList<int[]>();

		initExamples();
		trainingSet = new ArrayList<int[]>();
		trainingSet.add(examples.get(3));
		trainingSet = examples;
		initNetwork();
	}

	/**DEBUG method
	 * Prints each node out as a line of text
	 */
	public void printNetwork() {
		for (Neuron neuron : neurons) {
			System.out.print(neuron.toString() + "\n");
		}
		System.out.println();
	}
	
	// trains neural net using back propagation
	public void train() { 
		
		int epoch = 0;
		double actualOutput = 0;
		double expectedOutput = 0;
		double error = 0;
		
		while (correctClassifications != trainingSet.size() || epoch <= maxEpoch) {	// all examples not classified correctly && not at maximum epoch
			Collections.shuffle(examples, rnd);
			
			// each example e in the training set
			for (int[] e : trainingSet) { 
				actualOutput = getNeuralNetOutput(e); 	// forward pass , check
				expectedOutput = getEvenParity(e); 
				error =  expectedOutput - actualOutput;	//(ExpectedOutput - ActualOutput) at the output units 
				
				if (Math.abs(error) < .5)
					correctClassifications++;
				
				propagateError(error); 	// Propagate error backwards until all nodes have an error contribution	
				updateWeights();		// Update the weights in the network 	
				//printNetwork();
			}
			System.out.println(correctClassifications + " correct classifications");
			correctClassifications = 0;
			epoch++;
		}
		System.out.println("Training complete");
	}
	
	public void run() {
		
		double actualOutput = 0;
		double expectedOutput = 0;
		
		for (int[] testInput : trainingSet) {
			actualOutput = getNeuralNetOutput(testInput); 	// forward pass , check
			expectedOutput = getEvenParity(testInput); 	
			printOutput(testInput, actualOutput, expectedOutput);
		}
	}


	private void printOutput(int[] testInput, double actualOutput, double expectedOutput) {
		System.out.println(String.format("Input: [ %d, %d, %d, %d]\nActual Output: %f\nExpected Output: %d", 
				testInput[0], testInput[1], testInput[2], testInput[3], actualOutput, (int) expectedOutput)
				);
		System.out.println();
	}
	
	/**Returns 0 if the bit pattern has even parity, and 1 otherwise.
	 * @param e
	 * @return
	 */
	private double getEvenParity(int[] e) {
		
		int numOfOnes = 0;
		
		for (int bit : e) {
			if (bit == 1)
				numOfOnes++;
		}
		return numOfOnes % 2;
	}

	/**
	 * This will initialize the network, randomly generating weights in the range of +/-0.5 and calculating
	 * the value of the neurons.
	 */
	private void initNetwork() {
		// temporary buffers to hold information related to neuron initialization
		double weight = 0;
		double[] myWeights = null; 	//contains weights that connect each node to the nodes in the next layer
		//double value = 0;

		// Generate weights for neuron's various edges
		
		// Generate input layer nodes
		for (int i = 0; i < networkSize[0]; i++) {
			//value = inputData[i];		
			
			myWeights = new double[networkSize[1]];
			
			// generate weights for neuron's various edges that connect it to the neurons in the next layer
			for (int j = 0; j < networkSize[1]; j++) {
				weight = ((double) rnd.nextInt(501) / 1000.0)
						* Math.pow(-1, rnd.nextInt(2));

				myWeights[j] = weight;
			}

			Neuron neuron = new Neuron(0, 0.0, myWeights);
			neurons.add(neuron);
		}
		
		// Initialize the hidden layer of the neuron network.
		// /*
		for (int i = 0; i < networkSize[1]; i++) {
			
			myWeights = new double[networkSize[2]];
			
			for (int j = 0; j < networkSize[2]; j++) {
				weight = ((double) rnd.nextInt(501) / 1000.0)
						* Math.pow(-1, rnd.nextInt(2));

				myWeights[j] = weight;
			}
			// i is the next neuron that we wish to calculate the value for.
			//value = calculateSum(i, 1);	
			Neuron neuron = new Neuron(1, 0.0, myWeights);
			neurons.add(neuron);
		}
		
		// Initialize output layer
		
		for (int i = 0; i < networkSize[2]; i++) {
			//value = calculateSum(i, 2);
			Neuron neuron = new Neuron(2, 0.0, null);
			neurons.add(neuron);
		}
	}
	
	/**
	 * This is the forward pass.
	 * Do we make the value of the node the result of the activation function, or just use the activation function as input for the next value?
	 * @param trainingExample the set of inputs the network will run with
	 */
	private double getNeuralNetOutput(int[] trainingExample) {

		Neuron n = null;
		int networkLayer = 0;
		int neuronInLayer = 0;
		
		// Set value of neurons in the hidden and output layers to the activation function (sigmoid) with it's current value as input
		for (int i = 0; i < (networkSize[0] + networkSize[1] + networkSize[2]); i++) {
			n = neurons.get(i);
			
			// if the neuron is in the input layer
			if (i < networkSize[0]) {
				n.setValue(trainingExample[i]);
			}
			else if (i<networkSize[0]+networkSize[1]) {
				//These two values are needed to find the sum
				networkLayer = 1;
				neuronInLayer = i-(networkSize[0]);
				n.setSummationValue(calculateSum(neuronInLayer,networkLayer));
				n.setValue(sigmoid(n.getSummationValue()));
			}else {
				networkLayer = 2;
				neuronInLayer = i-(networkSize[0] + networkSize[1]);
				n.setSummationValue(calculateSum(neuronInLayer,networkLayer));
				n.setValue(sigmoid(n.getSummationValue()));
			}
			/*
			else {
				n.setValue( sigmoid(n.getValue()) );
			}
			*/
			
		}
		//System.out.println(n.getValue());
		return n.getValue();
	}

	// DEBUG: where I left off
	/**Propagates error back through the layers 
	 * @param error the difference between the expected output and the actual output
	 * @return
	 */
	private void propagateError(double error) {
	
		int totalNeurons = networkSize[0] + networkSize[1] + networkSize[2];
		Neuron n = neurons.get(neurons.size() - 1);		// initially set to output neuron
		n.setError(error);		// error argument remains unchanged for output neuron
		int counter = 1; //counteracts decrement of i, keep hidden layer linked to the output
		for (int i = totalNeurons - 1 - networkSize[2]; i >= networkSize[0]; i--) {
			 n = neurons.get(i);
			 
			 //sum the edgeWeight * error pairs
			 double myError = 0;
			 for (int edge = 0; edge < n.getEdgeWeight().length; edge++) {
				 myError += n.getEdgeWeight()[edge] * neurons.get(i + edge + counter).getError();
			 }
			 counter++;
			 
			 n.setError(myError);
		}
	}

	/**
	 * This is a skeleton copy of the initialize method
	 * Goes through and updates the weights in each neuron
	 */
	private void updateWeights() {	
		// temporary buffers to hold information related to neuron initialization
		double weight = 0;
		double edgeweight = 0;
		double sigResult = 0;
		double value = 0;
		double error = 0;
		double[] myWeights = null; 	//contains weights that connect each node to the nodes in the next layer

		for (int i = 0; i < networkSize[0]; i++) {	
			myWeights = new double[networkSize[1]];
			
			// generate weights for neuron's various edges that connect it to the neurons in the next layer
			for (int j = 0; j < networkSize[1]; j++) {
				edgeweight = neurons.get(i).getEdgeWeight()[j] ;
				sigResult = sigmoidDerivative(neurons.get(j+networkSize[0]).getSummationValue());
				value = neurons.get(i).getValue();
				error = neurons.get(j+networkSize[0]).getError();
				
				
				weight = edgeweight+(learningRate*error*sigResult*value);

				myWeights[j] = weight;
			}
			neurons.get(i).setEdgeWeight(myWeights);
		}

		//updates weights in the hidden layer
		for (int i = networkSize[0]; i < networkSize[0]+networkSize[1]; i++) {
			myWeights = new double[networkSize[2]];
			
			for (int j = 0; j < networkSize[2]; j++) {
				edgeweight = neurons.get(i).getEdgeWeight()[j] ;
				sigResult = sigmoidDerivative(neurons.get(j+networkSize[0]+networkSize[1]).getSummationValue());
				value = neurons.get(i).getValue();
				error = neurons.get(j+networkSize[0]+networkSize[1]).getError();
				
				weight = edgeweight+(learningRate*error*sigResult*value);

				myWeights[j] = weight;
			}
			neurons.get(i).setEdgeWeight(myWeights);
		}
	}
	
	
	/**
	 * a little helper for f(x) = v*w + v*w + ...
	 * 
	 * @param neuronNumber number identifies the neuron that you want to find the summation for
	 * @param layer the layer the neuron is located in
	 * @return sum of v*w
	 */
	private double calculateSum(int neuronNumber, int layer) {
		/*
		 * Take the value of the neuron multiply it by it's edge weight
		 * Repeat for each node in the layer. ex. f(x) = w1*v1 + w2*v2
		 */
		double result = 0;		
		int layerIndex = 0;
		
		// Neurons were added sequentially by layer in a list, so if we can to get the edge weights of a specific neuron we need to find out it's index in the list
		// If the input layer is n neurons, then the first neuron in the next layer will be the n+1 neuron. The layers are accounted for by adding the number
		// of neurons in each previous layer to the count, using layerIndex.
		for (int currentLayer = 0; currentLayer < networkSize.length; currentLayer++) {
			
			if (currentLayer == layer - 1) 
				break;
			
			layerIndex += networkSize[currentLayer];
		}
		
		for (int i = 0; i < networkSize[layer - 1]; i++) {
			// i represents the a neuron in the previous layer
			result += neurons.get(i + layerIndex).getValue() * neurons.get(i + layerIndex).getEdgeWeight()[neuronNumber];
					//function(i, neuronNumber);
		}
		return result;
	}

	/**
	 * @param x the summation
	 * @return
	 */
	private static double sigmoid(double x) {

		return 1.0 / (1.0 + Math.pow( Math.E, -x));
	}

	/**
	 * 
	 * @param x the error
	 * @return 
	 */
	private static double sigmoidDerivative(double x) {
		
		return sigmoid(x)*(1.0-sigmoid(x));
	}
	
	/**
	 * populates example list with all possible 4 bit combinations
	 */
	private void initExamples() {
		examples.add(new int[] { 0, 0, 0, 0 });	//0
		examples.add(new int[] { 0, 0, 0, 1});
		examples.add(new int[] { 0, 0, 1, 0});
		examples.add(new int[] { 0, 0, 1, 1});
		examples.add(new int[] { 0, 1, 0, 0}); //4
		examples.add(new int[] { 0, 1, 0, 1});
		examples.add(new int[] { 0, 1, 1, 0});
		examples.add(new int[] { 0, 1, 1, 1});
		examples.add(new int[] { 1, 0, 0, 0});
		examples.add(new int[] { 1, 0, 0, 1});	//9
		examples.add(new int[] { 1, 0, 1, 0});	
		examples.add(new int[] { 1, 0, 1, 1});
		examples.add(new int[] { 1, 1, 0, 0});
		examples.add(new int[] { 1, 1, 0, 1});
		examples.add(new int[] { 1, 1, 1, 0});	//14
		examples.add(new int[] { 1, 1, 1, 1});
	}
}
