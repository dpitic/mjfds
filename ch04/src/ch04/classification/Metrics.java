package ch04.classification;

import org.apache.commons.lang3.Validate;
import smile.validation.AUC;

import java.util.Arrays;

/**
 * This class implements various metrics for supervised learning.
 */
public class Metrics {

    /**
     * Return the Area Under Curve (AUC) for a binary classifier.
     *
     * @param actual    array of sample labels.
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
     *
     * @param actual    data array.
     * @param predicted data array.
     * @return logarithmic loss.
     */
    public static double logLoss(double[] actual, double[] predicted) {
        return logLoss(actual, predicted, 1e-15);
    }

    /**
     * Return logarithmic loss.
     *
     * @param actual    data array.
     * @param predicted data array.
     * @param eps       tolerance.
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

    /**
     * Calculate the confusion matrix accuracy.
     *
     * @param actual    data array.
     * @param proba     predicted data array.
     * @param threshold for predicted probabilities.
     * @return accuracy.
     */
    public static double accuracy(double[] actual, double[] proba,
                                  double threshold) {
        ConfusionMatrix matrix = confusion(actual, proba, threshold);
        return matrix.accuracy();
    }

    /**
     * Calculate the confusion matrix precision.
     *
     * @param actual    data array.
     * @param proba     predicted data array.
     * @param threshold for predicted probabilities.
     * @return precision.
     */
    public static double precision(double[] actual, double[] proba,
                                   double threshold) {
        ConfusionMatrix matrix = confusion(actual, proba, threshold);
        return matrix.precision();
    }

    /**
     * Calculate the confusion matrix recall.
     *
     * @param actual    data array.
     * @param proba     predicted data array.
     * @param threshold for predicted probabilities.
     * @return recall.
     */
    public static double recall(double[] actual, double[] proba,
                                double threshold) {
        ConfusionMatrix matrix = confusion(actual, proba, threshold);
        return matrix.recall();
    }

    /**
     * Calculate the F1 score for the confusion matrix.
     *
     * @param actual    data array.
     * @param proba     predicted data array.
     * @param threshold for predicted probabilities.
     * @return F1 score.
     */
    public static double f1(double[] actual, double[] proba,
                            double threshold) {
        ConfusionMatrix matrix = confusion(actual, proba, threshold);
        return matrix.f1();
    }

    /**
     * Factory method used to return a new ConfusionMatrix object.
     *
     * @param actual    data.
     * @param proba     predicted results.
     * @param threshold for predicted probabilities.
     * @return new ConfusionMatrix object.
     */
    public static ConfusionMatrix confusion(double[] actual, double[] proba,
                                            double threshold) {
        Validate.isTrue(actual.length == proba.length,
                "The lengths don't match.");

        int tp = 0;
        int tn = 0;
        int fp = 0;
        int fn = 0;

        for (int i = 0; i < actual.length; i++) {
            if (actual[i] == 1.0 && proba[i] > threshold) {
                tp++;
            } else if (actual[i] == 0.0 && proba[i] <= threshold) {
                tn++;
            } else if (actual[i] == 0.0 && proba[i] > threshold) {
                fp++;
            } else if (actual[i] == 1.0 && proba[i] <= threshold) {
                fn++;
            } else {
                throw new IllegalArgumentException(
                        "Unexpected label " + actual[i] + " at index " + i);
            }
        }
        return new ConfusionMatrix(tp, tn, fp, fn);
    }

    /**
     * Confusion matrix class used to calculate supervised learning model
     * metrics.
     */
    public static class ConfusionMatrix {
        private final int tp;
        private final int tn;
        private final int fp;
        private final int fn;

        public ConfusionMatrix(int tp, int tn, int fp, int fn) {
            this.tp = tp;
            this.tn = tn;
            this.fp = fp;
            this.fn = fn;
        }

        /**
         * Return the true positive.
         *
         * @return True positive.
         */
        public int getTp() {
            return tp;
        }

        /**
         * Return the true negative.
         *
         * @return True negative.
         */
        public int getTn() {
            return tn;
        }

        /**
         * Return the false positive.
         *
         * @return False positive.
         */
        public int getFp() {
            return fp;
        }

        /**
         * Return the false negative.
         *
         * @return False negative.
         */
        public int getFn() {
            return fn;
        }

        /**
         * Calculate the accuracy.
         *
         * @return accuracy.
         */
        public double accuracy() {
            int n = tp + tn + fp + fn;
            return 1.0 * (tp + fp) / n;
        }

        /**
         * Calculate the precision.
         *
         * @return precision.
         */
        public double precision() {
            return 1.0 * tp / (tp * fp);
        }

        /**
         * Calculate the recall.
         *
         * @return recall.
         */
        public double recall() {
            return 1.0 * tp / (tp + fn);
        }

        /**
         * Calculate the F1 score.
         *
         * @return F1 score.
         */
        public double f1() {
            double precision = precision();
            double recall = recall();
            return 2 * precision * recall / (precision + recall);
        }
    }
}
