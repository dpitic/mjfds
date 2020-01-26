package ch04.classification;

import ch04.RankedPageData;
import ch04.cv.Dataset;
import ch04.cv.Split;
import ch04.preprocess.StandardisationPreprocessor;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import smile.classification.LogisticRegression;
import smile.classification.SVM;
import smile.math.kernel.MercerKernel;
import smile.math.kernel.PolynomialKernel;

import java.io.IOException;
import java.util.List;

/**
 * Page prediction using Smile machine learning library.  The model tries to
 * predict whether a URL comes from the first page of the search engine results
 * or not.
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

        double[] lambdas = {0, 0.5, 1.0, 5.0, 10.0, 100.0, 1000.0};
        for (double lambda : lambdas) {
            DescriptiveStatistics summary = Smile.crossValidation(folds,
                    fold -> {
                        return LogisticRegression
                                .fit(fold.getX(), fold.getYAsInt(), lambda,
                                        0.001, 100);
                    });

            double mean = summary.getMean();
            double std = summary.getStandardDeviation();
            System.out.printf("logreg, λ=%8.3f, auc=%.4f ± %.4f%n",
                    lambda, mean, std);
        }

//        MercerKernel<double[]> kernel = new PolynomialKernel(2);
//
//        double[] Cs = {0.001, 0.01, 0.1};
//        for (double C : Cs) {
//            DescriptiveStatistics summary = Smile.crossValidation(folds,
//                    fold -> {
//                        double[][] X = fold.getX();
//                        int[] y = fold.getYAsInt();
//                        SVM<double[]> svm = SVM.fit(X, y, kernel, C, 0.001);
//                        // API is different; code broken here
//                        svm.trainPlattScaling(X, y);
//                        return svm;
//                    });
//
//            double mean = summary.getMean();
//            double std = summary.getStandardDeviation();
//            System.out.printf("svm     C=%8.3f, auc=%.4f ± %.4f%n",
//                    C, mean, std);
//        }

        // RandomForest API has changed
    }
}
