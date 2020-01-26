package ch04.classification;

import ch04.RankedPageData;
import ch04.cv.Dataset;
import ch04.cv.Split;
import ch04.preprocess.StandardisationPreprocessor;
import jsat.classifiers.linear.LogisticRegressionDCD;
import jsat.classifiers.svm.SBP;
import jsat.classifiers.svm.SupportVectorLearner;
import jsat.classifiers.trees.RandomForest;
import jsat.distributions.kernels.KernelTrick;
import jsat.distributions.kernels.PolynomialKernel;
import jsat.regression.LogisticRegression;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.io.IOException;
import java.util.List;

/**
 * Page prediction using JSAT library.  The model tries to predict whether a
 * URL comes from the first page of the search engine results or not.
 */
public class PagePredictionJSAT {

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

        DescriptiveStatistics logreg = JSAT.crossValidate(folds, fold -> {
            LogisticRegression model = new LogisticRegression();
            model.trainC(fold.toJsatClassificationDataset());
            return model;
        });

        System.out.printf("plain logreg     auc=%.4f ± %.4f%n",
                logreg.getMean(), logreg.getStandardDeviation());

        // Regularised logistic regression model; Dual Coordinate Descent (DCD)
        // regularisation parameters
        double[] cs = {0.0001, 0.01, 0.5, 1.0, 5.0, 10.0, 50.0, 70, 100};
        for (double c : cs) {
            int maxIterations = 100;
            DescriptiveStatistics summary = JSAT.crossValidate(folds, fold -> {
                LogisticRegressionDCD model = new LogisticRegressionDCD();
                model.setMaxIterations(maxIterations);
                model.setC(c);
                model.trainC(fold.toJsatClassificationDataset());
                return model;
            });

            double mean = summary.getMean();
            double std = summary.getStandardDeviation();
            System.out.printf("logreg, C=%5.1f, auc = %.4f ± %.4f%n", c,
                    mean, std);
        }

        KernelTrick kernel = new PolynomialKernel(2);
        SupportVectorLearner.CacheMode cacheMode =
                SupportVectorLearner.CacheMode.FULL;

        double[] nus = {0.3, 0.5, 0.7};
        for (double nu : nus) {
            int maxIterations = 30;
            DescriptiveStatistics summary = JSAT.crossValidate(folds, fold -> {
                SBP sbp = new SBP(kernel, cacheMode, maxIterations, nu);
                sbp.trainC(fold.toJsatClassificationDataset());
                return sbp;
            });

            double mean = summary.getMean();
            double std = summary.getStandardDeviation();
            System.out.printf("sbp    nu=%5.1f, auc=%.4f ± %.4f%n", nu,
                    mean, std);
        }

        // Random forest model
        DescriptiveStatistics rf = JSAT.crossValidate(folds, fold -> {
            RandomForest model = new RandomForest();
            model.setFeatureSamples(4);
            model.setMaxForestSize(150);
            model.trainC(fold.toJsatClassificationDataset());
            return model;
        });

        System.out.printf("random forest    auc=%.4f ± %.4f%n", rf.getMean(),
                rf.getStandardDeviation());

        // Logistic regression model
        LogisticRegression finalModel = new LogisticRegression();
        finalModel.trainC(train.toJsatClassificationDataset());

        double auc = JSAT.auc(finalModel, test);
        System.out.printf("final log reg    auc=%.4f%n", auc);
    }
}
