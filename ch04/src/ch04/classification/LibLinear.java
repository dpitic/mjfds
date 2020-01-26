package ch04.classification;

import ch04.cv.Dataset;
import ch04.cv.Split;
import de.bwaldvogel.liblinear.*;
import org.apache.commons.io.output.NullOutputStream;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.io.PrintStream;
import java.util.List;
import java.util.function.Function;

/**
 * Page prediction using LIBLINEAR library; unofficial Java library.
 */
public class LibLinear {

    public static void mute() {
        PrintStream devNull = new PrintStream(new NullOutputStream());
        de.bwaldvogel.liblinear.Linear.setDebugOutput(devNull);
    }

    /**
     * Return descriptive statistics for the area under curve calculated for
     * each fold
     *
     * @param folds   Number of K-folds for cross validation.
     * @param trainer Callback function that creates a model.
     * @return Descriptive statistics for AUCs of each fold.
     */
    public static DescriptiveStatistics crossValidate(
            List<Split> folds, Function<Dataset, Model> trainer) {
        double[] aucs = folds.parallelStream().mapToDouble(fold -> {
            Dataset foldTrain = fold.getTrain();
            Dataset foldValidation = fold.getTest();
            Model model = trainer.apply(foldTrain);
            return auc(model, foldValidation);
        }).toArray();
        return new DescriptiveStatistics(aucs);
    }

    /**
     * Return a trained LIBLINEAR model.
     *
     * @param dataset on which to train the model.
     * @param param   LIBLINEAR model training parameters.
     * @return Trained model.
     */
    public static Model train(Dataset dataset, Parameter param) {
        Problem problem = wrapDataset(dataset);
        return Linear.train(problem, param);
    }

    private static Problem wrapDataset(Dataset dataset) {
        double[][] X = dataset.getX();
        double[] y = dataset.getY();

        Problem problem = new Problem();
        problem.x = wrapMatrix(X);
        problem.y = y;
        problem.n = X[0].length + 1;
        problem.l = X.length;

        return problem;
    }

    public static double auc(Model model, Dataset dataset) {
        double[] scores;
        if (model.isProbabilityModel()) {
            scores = predictProba(model, dataset);
        } else {
            scores = predicValues(model, dataset);
            scores = sigmoid(scores);
        }
        return Metrics.auc(dataset.getY(), scores);
    }

    public static double[] predictProba(Model model, Dataset dataset) {
        int n = dataset.length();

        double[][] X = dataset.getX();
        double[] results = new double[n];
        double[] probs = new double[2];

        for (int i = 0; i < n; i++) {
            Feature[] row = wrapRow(X[i]);
            Linear.predictProbability(model, row, probs);
            results[i] = probs[1];
        }
        return results;
    }

    public static double[] predicValues(Model model, Dataset dataset) {
        int n = dataset.length();

        double[][] X = dataset.getX();
        double[] results = new double[n];
        double[] values = new double[1];

        for (int i = 0; i < n; i++) {
            Feature[] row = wrapRow(X[i]);
            Linear.predictValues(model, row, values);
            results[i] = values[0];
        }
        return results;
    }

    public static double[] sigmoid(double[] scores) {
        double[] result = new double[scores.length];

        for (int i = 0; i < result.length; i++) {
            result[i] = 1 / (1 + Math.exp(-scores[i]));
        }
        return result;
    }

    private static Feature[] wrapRow(double[] row) {
        int m = row.length;
        Feature[] result = new Feature[m];

        for (int i = 0; i < m; i++) {
            result[i] = new FeatureNode(i + 1, row[i]);
        }
        return result;
    }

    private static Feature[][] wrapMatrix(double[][] X) {
        int n = X.length;
        Feature[][] matrix = new Feature[n][];
        for (int i = 0; i < n; i++) {
            matrix[i] = wrapRow(X[i]);
        }
        return matrix;
    }
}
