package ch04.cv;

import jsat.classifiers.CategoricalData;
import jsat.classifiers.ClassificationDataSet;
import jsat.classifiers.DataPoint;
import jsat.classifiers.DataPointPair;
import jsat.linear.DenseVector;
import jsat.regression.RegressionDataSet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class represents a supervised learning data set consisting of features
 * and labels.  The public interface provides methods to split the dataset into
 * training and test data sets and k-fold splits.  This is a helper class for
 * holding the data.
 */
public class Dataset implements Serializable {

    private static long SEED = 1;

    private final double[][] X; // features matrix
    private final double[] y; // labels array

    public Dataset(double[][] X, double[] y) {
        this.X = X;
        this.y = y;
    }

    /**
     * Return the features matrix.
     *
     * @return features matrix.
     */
    public double[][] getX() {
        return X;
    }

    /**
     * Return the labels array.
     *
     * @return labels array.
     */
    public double[] getY() {
        return y;
    }

    /**
     * Return the labels array as an array of integers.
     *
     * @return labels array with integer elements.
     */
    public int[] getYAsInt() {
        return Arrays.stream(y).mapToInt(d -> (int) d).toArray();
    }

    /**
     * Return the number of rows in the features matrix.
     *
     * @return number of rows in the features matrix.
     */
    public int length() {
        return getX().length;
    }

    /**
     * Return a randomly shuffled k-fold data set.
     *
     * @param k number of k-folds.
     * @return Randomly shuffled List of Split train/test data sets.
     */
    public List<Split> shuffleKFold(int k) {
        return CrossValidation.kFold(this, k, true, SEED);
    }

    /**
     * Return an unshuffled k-fold data set.
     *
     * @param k number of k-folds.
     * @return Unshuffled List of Split train/test data sets.
     */
    public List<Split> kFold(int k) {
        return CrossValidation.kFold(this, k, false, SEED);
    }

    /**
     * Return an unshuffled train/test Split object.
     *
     * @param testRatio ratio of data set to use as test data.
     * @return Unshuffled Split object of train/test data.
     */
    public Split trainTestSplit(double testRatio) {
        return CrossValidation.trainTestSplit(this, testRatio, false,
                SEED);
    }

    /**
     * Return a randomly shuffled train/test Split object.
     *
     * @param testRatio ratio of data set to use as test data.
     * @return Shuffled Split object of train/test data.
     */
    public Split shuffleSplit(double testRatio) {
        return CrossValidation.trainTestSplit(this, testRatio, true,
                SEED);
    }

    /**
     * Return a JSAT classification data set for binary classification.
     * <p>
     * This helper method wraps the feature dataset in the JSAT
     * ClassificationDataSet wrapper class.
     *
     * @return ClassificationDataSet object suitable for binary classification.
     */
    public ClassificationDataSet toJsatClassificationDataset() {
        CategoricalData binary = new CategoricalData(2);

        List<DataPointPair<Integer>> data = new ArrayList<>(X.length);
        for (int i = 0; i < X.length; i++) {
            int target = (int) y[i];
            DataPoint row = new DataPoint(new DenseVector(X[i]));
            data.add(new DataPointPair<>(row, target));
        }
        return new ClassificationDataSet(data, binary);
    }

    /**
     * Return a JSAT regression data set.
     * <p>
     * This helper method wraps the labels dataset in the JSAT
     * RegressionDataSet wrapper class.
     *
     * @return RegressionDataset object.
     */
    public RegressionDataSet toJsatRegressionDataset() {
        List<DataPointPair<Double>> data = new ArrayList<>(X.length);

        for (int i = 0; i < X.length; i++) {
            DataPoint row = new DataPoint(new DenseVector(X[i]));
            data.add(new DataPointPair<>(row, y[i]));
        }
        return new RegressionDataSet(data);
    }
}
