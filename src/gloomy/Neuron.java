package gloomy;

// input is the summation of value*edgeWeight of nodes in previous layer
// an activation function is applied and an output produced
public class Neuron {
	private int layer;
	private double value;
	private double summationValue;
	private double[] edgeWeight;
	private double error = -1;
	
	// Constructors
	
	public Neuron (int layer, double value, double[] edgeWeight) {
		this.layer = layer;
		this.value = value;
		//change edgeWeight to an array of weights
		this.edgeWeight = edgeWeight;
	}
	
	// Properties
	
	public int getLayer() {
		return layer;
	}
	public void setLayer(int layer) {
		this.layer = layer;
	}
	public double getSummationValue(){
		return summationValue;
	}
	public void setSummationValue(double summationValue){
		this.summationValue = summationValue;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public double[] getEdgeWeight() {
		return edgeWeight;
	}
	public void setEdgeWeight(double[] edgeWeight) {
		this.edgeWeight = edgeWeight;
	}
	public double getError() {
		return error;
	}
	public void setError(double error) {
		this.error = error;
	}
	
	// Overrides
	
	public String toString() {
		String s = "";
		s += String.format("Layer: %d, Value: %.4f", layer, value);
		
		// put edge weights into string
		if (edgeWeight != null) {
			
			s += ", Weights: [";
					
			for (double edge : edgeWeight) {
				s += edge + ", ";
			}
			s = s.substring(0, s.length() - 2) + "]";
			
			s += ", Error: " + ((error == -1) ? "-" : error);
		}
		
		return s;
	}
}
