package ch04.classification;

import ch04.RankedPageData;
import ch04.cv.Dataset;
import ch04.cv.Split;
import ch04.preprocess.StandardisationPreprocessor;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import smile.classification.LogisticRegression;

import java.io.IOException;
import java.util.List;

/**
 * Page prediction using Smile machine learning library.
 */
public class PagePredictionSmile {

    public static void main(String[] args) throws IOException {
        Split split = RankedPageData.readRankedPagesMatrix(
                "data/ranked-pages.json", 0.2);

        Dataset train = split.getTrain();
        Dataset test = split.getTest();

        StandardisationPreprocessor preprocessor =
                StandardisationPreprocessor.train(train);
        train = preprocessor.transform(train);
        test = preprocessor.transform(test);

        List<Split> folds = train.kFold(3);

        double[] lambdas = { 0, 0.5, 1.0, 5.0, 10.0, 100.0, 1000.0 };
        for (double lambda :
                lambdas) {
            DescriptiveStatistics summary = Smile.crossValidation(folds,
                    fold -> {
                return new LogisticRegression(fold.getX(), fold.getY(), lambda);
                // TODO: fix this error
            });
        }
    }
}
