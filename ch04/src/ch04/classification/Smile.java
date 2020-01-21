package ch04.classification;

import ch04.cv.Dataset;
import ch04.cv.Split;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import smile.classification.SoftClassifier;

import java.util.List;
import java.util.function.Function;

/**
 * Statistical Machine Intelligence and Learning Engine (Smile) classification.
 */
public class Smile {

    public static DescriptiveStatistics crossValidation(
            List<Split> folds,
            Function<Dataset, SoftClassifier<double[]>> trainer) {
        double[] aucs = folds.parallelStream().mapToDouble(fold -> {
            Dataset foldTrain = fold.getTrain();
            Dataset foldValidation = fold.getTest();
            SoftClassifier<double[]> model = trainer.apply(foldTrain);
            return auc(model, foldValidation);
        }).toArray();
        return new DescriptiveStatistics(aucs);
    }

    public static double auc(SoftClassifier<double[]> model,
                             Dataset dataset) {
        double[] probability = predict(model, dataset);
        return Metrics.auc(dataset.getY(), probability);
    }

    private static double[] predict(SoftClassifier<double[]> model,
                                    Dataset dataset) {
        double[][] X = dataset.getX();
        double[] result = new double[X.length];

        double[] probs = new double[2];
        for (int i = 0; i < X.length; i++) {
            model.predict(X[i], probs);
            result[i] = probs[1];
        }
        return result;
    }
}
