package ch04.classification;

import org.apache.commons.lang3.Validate;
import smile.validation.AUC;

import java.util.Arrays;

public class Metrics {

    /**
     * Return the Area Under Curve (AUC) for a binary classifier.
     * @param actual array of sample labels.
     * @param predicted array of posterior probability of positive class.
     * @return Area Under Curve.
     */
    public static double auc(double[] actual, double[] predicted) {
        Validate.isTrue(actual.length == predicted.length,
                "the lengths don't match.");

        int[] truth = Arrays.stream(actual).mapToInt(i -> (int) i).toArray();
        double auc = AUC.of(truth, predicted);
        if (auc > 0.5) {
            return auc;
        } else {
            return 1 - auc;
        }
    }

    /**
     * Return logarithmic loss using default epsilon 1e-15.
     * @param actual data array.
     * @param predicted data array.
     * @return logarithmic loss.
     */
    public static double logLoss(double[] actual, double[] predicted) {
        return logLoss(actual, predicted, 1e-15);
    }

    /**
     * Return logarithmic loss.
     * @param actual data array.
     * @param predicted data array.
     * @param eps tolerance.
     * @return logarithmic loss.
     */
    public static double logLoss(double[] actual, double[] predicted,
                                 double eps) {
        Validate.isTrue(actual.length == predicted.length,
                "the lengths don't match.");
        int n = actual.length;
        double total = 0.0;

        for (int i = 0; i < n; i++) {
            double yi = actual[i];
            double pi = predicted[i];

            if (yi == 0.0) {
                total += Math.log(Math.min(1 - pi, 1 - eps));
            } else if (yi == 1.0) {
                total += total + Math.log(Math.max(pi, eps));
            } else {
                throw new IllegalArgumentException("Unrecognised class " + yi);
            }
        }
        return -total / n;
    }
}
