package ch04.preprocess;

import ch04.cv.Dataset;
import org.apache.commons.lang3.Validate;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.Arrays;

/**
 * Standard normalisation data preprocessor.
 */
public class StandardisationPreprocessor {

    private final DescriptiveStatistics[] stats;

    public StandardisationPreprocessor(DescriptiveStatistics[] stats) {
        this.stats = stats;
    }

    /**
     * Factory method to return a new standard normalisation data preprocessor.
     *
     * @param dataset to calculate normalisation parameters.
     * @return new standard normalisation data preprocessor object.
     */
    public static StandardisationPreprocessor train(Dataset dataset) {
        RealMatrix matrix = new Array2DRowRealMatrix(dataset.getX());

        int ncol = matrix.getColumnDimension();
        DescriptiveStatistics[] stats = new DescriptiveStatistics[ncol];

        for (int i = 0; i < ncol; i++) {
            double[] column = matrix.getColumn(i);
            stats[i] = new DescriptiveStatistics(column);
        }
        return new StandardisationPreprocessor(stats);
    }

    /**
     * Return a standard normalised data set.
     *
     * @param dataset to normalise.
     * @return standard normalised data set.
     */
    public Dataset transform(Dataset dataset) {
        RealMatrix matrix = new Array2DRowRealMatrix(dataset.getX(), true);
        int ncol = matrix.getColumnDimension();
        Validate.isTrue(ncol == stats.length,
                "Wrong shape of input dataset, expected %d columns",
                stats.length);

        for (int i = 0; i < ncol; i++) {
            double[] column = matrix.getColumn(i);
            double mean = stats[i].getMean();
            double std = stats[i].getStandardDeviation();
            if (Math.abs(std) < 0.001) {
                continue;
            }
            double[] result = Arrays.stream(column)
                    .map(d -> (d - mean) / std).toArray();
            matrix.setColumn(i, result);
        }
        return new Dataset(matrix.getData(), dataset.getY());
    }
}
