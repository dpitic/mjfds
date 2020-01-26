package ch04.classification;

import ch04.RankedPageData;
import ch04.cv.Dataset;
import ch04.cv.Split;
import ch04.preprocess.StandardisationPreprocessor;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.neural.networks.training.strategy.RegularizationStrategy;

import java.io.IOException;

/**
 * Page prediction using Encog neural network library.  The model tries to
 * predict whether a URL comes from the first page of the search engine results
 * or not.
 */
public class PagePredictionEncog {

    public static void main(String[] args) throws IOException {
        Split split = RankedPageData.readRankedPagesMatrix(
                "data/ranked-pages.json", 0.2);

        Dataset fullTrain = split.getTrain();
        Dataset test = split.getTest();

        // Normalise the data set
        StandardisationPreprocessor preprocessor =
                StandardisationPreprocessor.train(fullTrain);
        fullTrain = preprocessor.transform(fullTrain);
        test = preprocessor.transform(test);

        Split validationSplit = fullTrain.trainTestSplit(0.3);
        Dataset train = validationSplit.getTrain();

        int numInputNeurons = fullTrain.getX()[0].length;

        // Build neural network architecture
        BasicNetwork network = new BasicNetwork();
        network.addLayer(new BasicLayer(new ActivationSigmoid(), true,
                numInputNeurons));
        network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 30));
        network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 1));
        network.getStructure().finalizeStructure();
        network.reset();  // randomly initialise network weights

        // Wrap data set in special wrapper class
        MLDataSet trainSet = Encog.asEncogDataset(train);

        System.out.println("Training model ...");
        MLTrain trainer = new ResilientPropagation(network, trainSet);
        double lambda = 0.01;
        trainer.addStrategy(new RegularizationStrategy(lambda));

        int numEpochs = 101;
        Encog.learningCurves(validationSplit.getTrain(),
                validationSplit.getTest(), network, trainer, numEpochs);

        System.out.println();
        System.out.println("Retraining full model with 20 iterations ...");

        network.reset();

        MLDataSet fullTrainSet = Encog.asEncogDataset(fullTrain);
        trainer = new ResilientPropagation(network, fullTrainSet);
        trainer.addStrategy(new RegularizationStrategy(lambda));

        Encog.learningCurves(fullTrain, test, network, trainer, 21);

        Encog.shutdown();
    }
}
