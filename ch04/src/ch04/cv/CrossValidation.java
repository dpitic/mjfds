package ch04.cv;

import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * Cross validation class.
 */
public class CrossValidation {

    /**
     * Return a list of train/test Split objects for k fold validation.
     *
     * @param dataset Dataset object to split into train/test data sets.
     * @param k       number of folds.
     * @param shuffle boolean flag.
     * @param seed    for random number generator used to shuffle data.
     * @return List of Split train/test data sets.
     */
    public static List<Split> kFold(Dataset dataset, int k, boolean shuffle,
                                    long seed) {
        int length = dataset.length();
        Validate.isTrue(k < length);

        int[] indexes = IntStream.range(0, length).toArray();
        if (shuffle) {
            shuffle(indexes, seed);
        }

        int[][] folds = prepareFolds(indexes, k);
        List<Split> result = new ArrayList<>();

        for (int i = 0; i < k; i++) {
            int[] testIdx = folds[i];
            int[] trainIdx = combineTrainFolds(folds, indexes.length, i);
            result.add(Split.fromIndexes(dataset, trainIdx, testIdx));
        }
        return result;
    }

    /**
     * Return a Split dataset containing train/test split data sets.
     *
     * @param dataset   Dataset to split.
     * @param testRatio Ratio of dataset to use for test split.
     * @param shuffle   boolean flag to shuffle data set.
     * @param seed      for random number generator.
     * @return Split object consisting of train and test data set.
     */
    public static Split trainTestSplit(Dataset dataset, double testRatio,
                                       boolean shuffle, long seed) {
        Validate.isTrue(testRatio > 0.0 && testRatio < 1.0,
                "test ratio nust be in (0, 1) interval.");

        int[] indexes = IntStream.range(0, dataset.length()).toArray();
        if (shuffle) {
            shuffle(indexes, seed);
        }

        int trainSize = (int) (indexes.length * (1 - testRatio));

        int[] trainIndex = Arrays.copyOfRange(indexes, 0, trainSize);
        int[] testIndex = Arrays.copyOfRange(indexes, trainSize,
                indexes.length);
        return Split.fromIndexes(dataset, trainIndex, testIndex);
    }

    /**
     * Randomly shuffle the indexes.
     *
     * @param indexes array to shuffle.
     * @param seed    for random number generator.
     */
    private static void shuffle(int[] indexes, long seed) {
        Random rnd = new Random(seed);

        for (int i = indexes.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);

            int tmp = indexes[index];
            indexes[index] = indexes[i];
            indexes[i] = tmp;
        }
    }

    /**
     * Calculate k fold indexes for the specified input indexes.
     *
     * @param indexes array for which to calculate k fold indexes.
     * @param k       number of folds.
     * @return Fold indexes array.
     */
    private static int[][] prepareFolds(int[] indexes, int k) {
        int[][] foldIndexes = new int[k][];

        int step = indexes.length / k;
        int beginIndex = 0;

        for (int i = 0; i < k - 1; i++) {
            foldIndexes[i] = Arrays.copyOfRange(indexes, beginIndex,
                    beginIndex + step);
            beginIndex = beginIndex + step;
        }

        foldIndexes[k - 1] = Arrays.copyOfRange(indexes, beginIndex,
                indexes.length);
        return foldIndexes;
    }


    private static int[] combineTrainFolds(int[][] folds, int totalSize,
                                           int excludeIndex) {
        int size = totalSize - folds[excludeIndex].length;
        int result[] = new int[size];

        int start = 0;
        for (int i = 0; i < folds.length; i++) {
            if (i == excludeIndex) {
                continue;
            }
            int[] fold = folds[i];
            System.arraycopy(fold, 0, result, start, fold.length);
            start = start + fold.length;
        }
        return result;
    }
}
