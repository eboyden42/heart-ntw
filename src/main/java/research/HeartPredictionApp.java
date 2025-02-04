package research;

import org.w3c.dom.UserDataHandler;
import research.neuralnetwork.NeuralNetwork;
import research.neuralnetwork.Transform;
import research.neuralnetwork.loader.Loader;
import research.neuralnetwork.loader.MetaData;
import research.neuralnetwork.loader.heart.CSVLoader;
import research.neuralnetwork.loader.image.ImageWriter;

import java.io.File;
import java.util.Scanner;

public class HeartPredictionApp {

	public static void main(String[] args) {

		final String directory = "heart.csv";

		if (!new File(directory).exists()) {
			System.out.println("'"+directory+"'"+" does not exist");
			return;
		}

		Loader trainLoader = new CSVLoader(directory, 32);
		Loader testLoader = new CSVLoader(directory, 32);

		MetaData metaData = trainLoader.open();
		int inputSize = metaData.getInputSize();
		int outputSize = metaData.getExpectedSize();
		trainLoader.close();

		Scanner scan = new Scanner(System.in);
		System.out.println("Enter the name of your network, if the file cannot be found a new network will be created:");
		String filename = scan.nextLine();
		if (!filename.contains(".ntw")) {
			filename += ".ntw";
		}

		NeuralNetwork neuralNetwork = NeuralNetwork.load(filename);

		if (neuralNetwork == null) {
			System.out.println("Unable to load network from save, Creating from scratch...");

			//TRAINING SETTINGS - settings are set to default values but can be changed manually
			int epochNumber = 10000;
			double initialLearningRate = 0.001;
			double finalLearningRate = 0.00001;
			int numberOfThreads = 12; //you may want to change this based on your computer (so the network trains faster), nothing will break if you don't
			final double scaleInitialWeights = 0.04; //DO NOT CHANGE THIS UNLESS YOU KNOW WHAT YOU'RE DOING

			neuralNetwork = new NeuralNetwork();
			neuralNetwork.setEpochs(epochNumber);
			neuralNetwork.setLearningRates(initialLearningRate, finalLearningRate);
			neuralNetwork.setThreads(numberOfThreads);
			neuralNetwork.setScaleInitalWeights(scaleInitialWeights);

			//NETWORK ARCHITECTURE SETTINGS - determined dynamically upon program run
			String line = "";
			System.out.println("Do you want to use a default network architecture? (Y/N)");
			line = scan.nextLine();
			while(!line.equals("Y") && !line.equals("N")) {
				System.out.println("Must enter Y or N. Do you want to use a default network architecture?");
				line = scan.nextLine();
			}
			if (line.equals("Y")) {
				neuralNetwork.add(Transform.DENSE, 24, inputSize);
				neuralNetwork.add(Transform.RELU);
				neuralNetwork.add(Transform.DENSE, outputSize);
				neuralNetwork.add(Transform.SOFTMAX);
			}
			else {
				System.out.println("Enter the number of layers you would like in your network (typically 1-3):");
				line = scan.nextLine();
				while(!isInteger(line)) {
					System.out.println("Must enter an integer.");
					line = scan.nextLine();
				}
				int numberOfLayers = Integer.parseInt(line);

				System.out.println("Enter the number of nodes you would like in layer 1. (typically 5-100, descending with each layer):");
				line = scan.nextLine();
				while(!isInteger(line)) {
					System.out.println("Must enter an integer.");
					line = scan.nextLine();
				}
				int nodes = Integer.parseInt(line);
				neuralNetwork.add(Transform.DENSE, nodes, inputSize);
				neuralNetwork.add(Transform.RELU);

				for (int i = 1; i < numberOfLayers; i ++) {
					System.out.printf("Enter the number of nodes you would like in layer %d. (typically 5-100, descending with each layer):\n", i+1);
					line = scan.nextLine();
					while(!isInteger(line)) {
						System.out.println("Must enter an integer.");
						line = scan.nextLine();
					}
					nodes = Integer.parseInt(line);
					neuralNetwork.add(Transform.DENSE, nodes);
					neuralNetwork.add(Transform.RELU);
				}

				//append output layer
				neuralNetwork.add(Transform.DENSE, outputSize);
				neuralNetwork.add(Transform.SOFTMAX);
			}

		}
		else {
			System.out.println("Loaded from "+filename);
			String line = "";
			System.out.println("Do you want to train this loaded network further? (Y/N)");
			line = scan.nextLine();
			while(!line.equals("Y") && !line.equals("N")) {
				System.out.println("Must enter Y or N. Do you want to use a default network architecture?");
				line = scan.nextLine();
			}
			if (line.equals("N")) {
				showSamplePredictions(directory, neuralNetwork);
				return;
			}
		}

		//for newly generated network
		System.out.println(neuralNetwork);
		neuralNetwork.fit(trainLoader, testLoader);
		neuralNetwork.save(filename);

		showSamplePredictions(directory, neuralNetwork);
	}

	public static boolean isInteger(String input) {
		try {
			Integer.parseInt(input);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static void showSamplePredictions(String filename, NeuralNetwork network) {
		System.out.println("Network Info:");
		System.out.println(network);
		System.out.println(String.format("Average Percent Correct: %.4f", network.getPercentCorrect()));
		System.out.println(String.format("Average Loss: %.4f", network.getLoss()));
		System.out.println("Sample Predictions:");

		CSVLoader loader = new CSVLoader("heart.csv", 32);
		loader.open();
		double[][] train = loader.getTrainingData();
		double[] label = loader.getLabelData();

		for (int i = 0; i < 10; i ++) {
			double[] predicted = network.predict(train[i]);
			double first = 0.0;
			double prediction = 0.0;
			double confidence = 0.0;
			for (int n = 0; n < predicted.length; n ++) {
				//System.out.print(String.format("%.2f, ", predicted[n]));
				if (n == 0) {
					first = predicted[n];
				}
				else {
					if (first > predicted[n]) {
						System.out.print("Network Prediction: "+1.0+", ");
						prediction = 1.0;
						confidence = predicted[n-1];
					}
					else {
						System.out.print("Network Prediction: "+0.0+", ");
						prediction = 0.0;
						confidence = predicted[n];
					}
				}
			}
			System.out.println(String.format("Actual: %.2f | Confidence: %.2f <<%b>>", label[i], confidence, prediction == label[i]));
		}
	}

}
