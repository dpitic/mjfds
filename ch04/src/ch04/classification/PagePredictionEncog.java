package ch04.classification;

import ch04.RankedPageData;
import ch04.cv.Dataset;
import ch04.cv.Split;

import java.io.IOException;

public class PagePredictionEncog {

    public static void main(String[] args) throws IOException {
        Split split = RankedPageData.readRankedPagesMatrix(
                "data/ranked-pages.json", 0.2);

        Dataset fullTrain = split.getTrain();
        Dataset test = split.getTest();

        // TODO: Continue here
    }
}
