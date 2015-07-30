import com.sun.prism.paint.Gradient;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by MichaelBick on 7/29/15.
 */
public class Test {
	private Symbol[] trainStocks;
	private Symbol[] testStocks;
	
    public static void main(String[] args) throws IOException {
    	Symbol[] stocks = {new Symbol("AAPL"), new Symbol("LVS"), new Symbol("GOOG")};
    	
    	Test test = new Test(stocks, stocks);
    	test.test();
    }
    
    public Test (Symbol[] trainStocks, Symbol[] testStocks) {
    	this.trainStocks = trainStocks;
    	this.testStocks = testStocks;
    }

    private void test() throws IOException {
        int FUTURE_DAYS = 10;
        int NUM_POINTS = 30;
        int DAYS_BACK = NUM_POINTS + FUTURE_DAYS + 1;

        // Get training data
        double[][] train = GradientDescent.getData(trainStocks, NUM_POINTS, DAYS_BACK);
        double[] trainActual = GradientDescent.getActual(trainStocks, NUM_POINTS, DAYS_BACK, FUTURE_DAYS);
        
        // Get data mean and standard deviation
        double[] mean = GradientDescent.getMean(train);
        double[] stdDev = GradientDescent.getStdDev(train);
        
        // Normalize training data
        train = GradientDescent.normalize(train, mean, stdDev);

        // Get actual mean and standard deviation
        double actualMean = GradientDescent.getMean(trainActual);
        double actualStdDev = GradientDescent.getStdDev(trainActual);

        // Normalize training actuals
        trainActual = GradientDescent.normalize(trainActual, actualMean, actualStdDev);


        // Get test data
        double[][] test = GradientDescent.getData(testStocks, NUM_POINTS, DAYS_BACK + NUM_POINTS);
        double[] testActual = GradientDescent.getActual(testStocks, NUM_POINTS, DAYS_BACK + NUM_POINTS, FUTURE_DAYS);
        
        // Normalize test data using training mean and standard deviation
        test = GradientDescent.normalize(test, mean, stdDev);


        // Train
        double[] theta = new double[train[0].length];

        theta = GradientDescent.train(train, trainActual, theta, .4, 10000000);

        System.out.println(Arrays.toString(theta));
        System.out.println("Cost (Try to minimize): " + getCost(train, trainActual, theta));

        for (int i = 0; i < testActual.length; i++) {
            System.out.println("Actual: " + testActual[i]);
            System.out.println("Prediction: " + GradientDescent.getPredictions(theta, test, actualMean, actualStdDev)[i]);
        }
    }

    private static double getCost(double[][] data, double[] actual, double[] theta) {
        int size = actual.length;

        double[] predictions = GradientDescent.getPredictions(theta, data, 0, 1);

        double sumErrors = 0;

        for (int i = 0; i < size; i++) {
            sumErrors += Math.pow(predictions[i] - actual[i], 2);
        }

        return (1.0 / (2 * size)) * sumErrors;
    }
}
