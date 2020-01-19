package ch04.classification;

import ch04.cv.Dataset;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;

public class Encog {

    public static void learningCurves(Dataset train, Dataset test,
                                      BasicNetwork network, MLTrain trainer,
                                      int noEpochs) {
        for (int i = 0; i < noEpochs; i++) {
            trainer.iteration();
            if (i % 10 == 0) {
                double aucTrain = auc(network, train);
                double aucVal = auc(network, test);
                System.out.printf("%3d - train:%.4f, val:%.4f%n",
                        i, aucTrain, aucVal);
            }
        }
    }

    private static double auc(BasicNetwork network, Dataset dataset) {
        double[] predictTrain = predict(network, dataset);
        return Metrics.auc(dataset.getY(), predictTrain);
    }

    private static double[] predict(BasicNetwork network, Dataset dataset) {
        return new double[0];
    }
}
