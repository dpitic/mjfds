package ch04.classification;

import ch04.RankedPageData;
import ch04.cv.Dataset;
import ch04.cv.Split;
import ch04.preprocess.StandardisationPreprocessor;
import de.bwaldvogel.liblinear.Model;
import de.bwaldvogel.liblinear.Parameter;
import de.bwaldvogel.liblinear.SolverType;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.io.IOException;
import java.util.List;

/**
 * Binary classification using LIBLINEAR library.  The model tries to predict
 * whether a URL comes from the first page of the search engine results or not.
 */
public class PagePredictionLibLinear {

    public static void main(String[] args) throws IOException {
        Split split = RankedPageData.readRankedPagesMatrix(
                "data/ranked-pages.json", 0.2);

        Dataset train = split.getTrain();
        Dataset test = split.getTest();

        StandardisationPreprocessor preprocessor =
                StandardisationPreprocessor.train(train);
        train = preprocessor.transform(train);
        test = preprocessor.transform(test);

        LibLinear.mute();

        List<Split> folds = train.kFold(3);

        SolverType[] solvers = {SolverType.L1R_LR, SolverType.L2R_LR,
                SolverType.L1R_L2LOSS_SVC, SolverType.L2R_L2LOSS_SVC};
        double[] Cs = {0.01, 0.02, 0.03, 0.05, 0.1, 0.5, 1.0, 10.0, 100.0};

        for (SolverType solver : solvers) {
            for (double C : Cs) {
                DescriptiveStatistics summary = LibLinear.crossValidate(folds,
                        fold -> {
                            Parameter param = new Parameter(solver, C, 0.0001);
                            return LibLinear.train(fold, param);
                        });
                double mean = summary.getMean();
                double std = summary.getStandardDeviation();
                System.out.printf("%15s  C=%7.3f, auc=%.4f ± %.4f%n", solver,
                        C, mean, std);
            }
        }

        Parameter param = new Parameter(SolverType.L1R_LR, 0.05, 0.0001);
        Model finalModel = LibLinear.train(train, param);
        double finalAuc = LibLinear.auc(finalModel, test);
        System.out.printf("final logreg        auc=%.4f%n", finalAuc);
    }
}
