package ch04.classification;

import ch04.RankedPageData;
import ch04.cv.Dataset;
import ch04.cv.Split;
import ch04.preprocess.StandardisationPreprocessor;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.encog.mathutil.libsvm.svm_model;
import org.encog.mathutil.libsvm.svm_parameter;

import java.io.IOException;
import java.util.List;

/**
 * Page prediction using LIBSVM library.
 */
public class PagePredictionLibSVM {

    public static void main(String[] args) throws IOException {
        Split split = RankedPageData.readRankedPagesMatrix(
                "data/ranked-pages.json", 0.2);

        Dataset train = split.getTrain();
        Dataset test = split.getTest();

        StandardisationPreprocessor preprocessor =
                StandardisationPreprocessor.train(train);
        train = preprocessor.transform(train);
        test = preprocessor.transform(test);

        LibSVM.mute();

        List<Split> folds = train.kFold(3);

        double[] Cs = {0.001, 0.01, 0.1, 0.5, 1.0, 10.0, 20.0};
        for (double C : Cs) {
            DescriptiveStatistics summary = LibSVM.crossValidate(folds,
                    fold -> {
                        svm_parameter param = LibSVM.linearSVC(C);
                        svm_model model = LibSVM.train(fold, param);
                        return model;
                    });

            double mean = summary.getMean();
            double std = summary.getStandardDeviation();
            System.out.printf("linear  C=%8.3f, auc=%.4f ± %.4f%n", C, mean,
                    std);
        }

        Cs = new double[]{0.001, 0.01, 0.1, 0.5, 1.0};
        for (double C : Cs) {
            DescriptiveStatistics summary = LibSVM.crossValidate(folds,
                    fold -> {
                        svm_parameter param = LibSVM.polynomialSVC(2, C);
                        svm_model model = LibSVM.train(fold, param);
                        return model;
                    });

            double mean = summary.getMean();
            double std = summary.getStandardDeviation();
            System.out.printf("poly(2) C=%8.3f, auc=%.4f ± %.4f%n", C, mean,
                    std);
        }

        Cs = new double[]{0.001, 0.01, 0.1};
        for (double C : Cs) {
            DescriptiveStatistics summary = LibSVM.crossValidate(folds,
                    fold -> {
                        svm_parameter param = LibSVM.polynomialSVC(3, C);
                        return LibSVM.train(fold, param);
                    });

            double mean = summary.getMean();
            double std = summary.getStandardDeviation();
            System.out.printf("poly(3) C=%8.3f, auc=%.4f ± %.4f%n", C, mean,
                    std);
        }

        Cs = new double[]{0.001, 0.01, 0.1};
        for (double C : Cs) {
            DescriptiveStatistics summary = LibSVM.crossValidate(folds,
                    fold -> {
                        svm_parameter param = LibSVM.gaussianSVC(C, 1.0);
                        svm_model model = LibSVM.train(fold, param);
                        return model;
                    });

            double mean = summary.getMean();
            double std = summary.getStandardDeviation();
            System.out.printf("rbf     C=%8.3f, auc=%.4f ± %.4f%n", C, mean,
                    std);
        }
    }
}
