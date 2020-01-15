package ch03;

import ch02.BeanToJoinery;
import ch02.crawl.RankedPage;
import joinery.DataFrame;

import java.io.IOException;
import java.util.List;

/**
 * Display ranked page data summary statistics.
 */
public class JoineryStats {

    public static void main(String[] args) throws IOException {
        List<RankedPage> pages = Data.readRankedPages("data/ranked-pages.json");
        DataFrame<Object> df = BeanToJoinery.convert(pages, RankedPage.class);

        DataFrame<Object> drop = df.retain("bodyContentLength",
                "titleLength", "numberOfHeaders");
        DataFrame<Object> describe = drop.describe();
        System.out.println(describe.toString());

        DataFrame<Object> meanPerPage = df.groupBy("page").mean()
                .drop("position")
                .sortBy("page").transpose();
        System.out.println(meanPerPage);
    }
}
