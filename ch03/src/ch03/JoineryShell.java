package ch03;

import ch02.BeanToJoinery;
import ch02.crawl.RankedPage;
import joinery.DataFrame;
import joinery.impl.Shell;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Process the search engine ranked pages in a Joinery Shell.
 */
public class JoineryShell {

    public static void main(String[] args) throws IOException {
        List<RankedPage> pages = Data.readRankedPages("data/ranked-pages.json");
        DataFrame<Object> dataFrame = BeanToJoinery.convert(pages,
                RankedPage.class);
        Shell.repl(Arrays.asList(dataFrame));
    }
}
