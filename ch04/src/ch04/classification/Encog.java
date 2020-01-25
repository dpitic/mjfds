package ch04.classification;

import ch04.cv.Dataset;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;

/**
 * Binary classification using Encog library for neural networks.
 */
public class Encog {

    /**
     * Calculate and display training and validation area under curve
     *
     * @param train    training data set.
     * @param test     test data set.
     * @param network  model.
     * @param trainer  model trainer object.
     * @param noEpochs number of epochs.
     */
    public static void learningCurves(Dataset train, Dataset test,
                                      BasicNetwork network, MLTrain trainer,
                                      int noEpochs) {
        for (int i = 0; i < noEpochs; i++) {
            trainer.iteration();
            if (i % 10 == 0) {
                double aucTrain = auc(network, train);
                double aucVal = auc(network, test);
                System.out.printf("%3d - train:%.4f, val:%.4f%n",
                        i, aucTrain, aucVal);
            }
        }
    }

    /**
     * Return the area under curve.
     *
     * @param network model.
     * @param dataset Dataset object.
     * @return Area Under Curve.
     */
    private static double auc(BasicNetwork network, Dataset dataset) {
        double[] predictTrain = predict(network, dataset);
        return Metrics.auc(dataset.getY(), predictTrain);
    }

    /**
     * Return predictions of the dataset using the model.
     *
     * @param model   used for inference.
     * @param dataset used as input to the model for prediction.
     * @return prediction results.
     */
    private static double[] predict(BasicNetwork model, Dataset dataset) {
        double[][] X = dataset.getX();
        double[] result = new double[X.length];

        for (int i = 0; i < X.length; i++) {
            MLData out = model.compute(new BasicMLData(X[i]));
            result[i] = out.getData(0);
        }
        return result;
    }

    /**
     * Convert training data set to Encog data set.
     *
     * @param train training data set.
     * @return BasicMLDataset object.
     */
    public static BasicMLDataSet asEncogDataset(Dataset train) {
        return new BasicMLDataSet(train.getX(), to2d(train.getY()));
    }

    /**
     * Helper function used to convert labels array to 2D array.
     *
     * @param y 1-D labels array.
     * @return 2-D labels array.
     */
    private static double[][] to2d(double[] y) {
        double[][] res = new double[y.length][];

        for (int i = 0; i < y.length; i++) {
            res[i] = new double[]{y[i]};
        }
        return res;
    }

    /**
     * Shut down the Encog instance.
     */
    public static void shutdown() {
        org.encog.Encog.getInstance().shutdown();
    }
}
