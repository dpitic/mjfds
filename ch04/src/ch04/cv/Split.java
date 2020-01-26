package ch04.cv;

import java.util.Objects;

/**
 * Split dataset into train and test data sets.
 */
public class Split {

    private final Dataset train; // training data set
    private final Dataset test; // test (validation) data set

    public Split(Dataset train, Dataset test) {
        this.train = train;
        this.test = test;
    }

    /**
     * Return the training data set.
     *
     * @return training data set.
     */
    public Dataset getTrain() {
        return train;
    }

    /**
     * Return the test (validation) data set.
     *
     * @return test data set.
     */
    public Dataset getTest() {
        return test;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Split) {
            Split other = (Split) obj;
            return train.equals(other.train) && test.equals(test);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(train, test);
    }

    /**
     * Split dataset into train and test data sets based on train and test
     * indices.
     *
     * @param dataset    to split.
     * @param trainIndex training data set index integer array.
     * @param testIndex  test data set index integer array.
     * @return a Split object containing the train and test data sets.
     */
    public static Split fromIndexes(Dataset dataset, int[] trainIndex,
                                    int[] testIndex) {
        double[][] X = dataset.getX();
        double[] y = dataset.getY();

        // Create training data set from train indices
        int trainSize = trainIndex.length;

        double[][] trainXres = new double[trainSize][];
        double[] trainYres = new double[trainSize];
        for (int i = 0; i < trainSize; i++) {
            int idx = trainIndex[i];
            trainXres[i] = X[idx];
            trainYres[i] = y[idx];
        }

        // Create test data set from test indices
        int testSize = testIndex.length;

        double[][] testXres = new double[testSize][];
        double[] testYres = new double[testSize];
        for (int i = 0; i < testSize; i++) {
            int idx = testIndex[i];
            testXres[i] = X[idx];
            testYres[i] = y[idx];
        }

        // Build the train and test Dataset objects to return
        Dataset train = new Dataset(trainXres, trainYres);
        Dataset test = new Dataset(testXres, testYres);
        return new Split(train, test);
    }
}
