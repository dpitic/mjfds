package ch02;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class UrlUtils {

    // Return HTML string response from specified URL
    public  static String request(String url) throws IOException {
        try (InputStream is = new URL(url).openStream()) {
            return IOUtils.toString(is, StandardCharsets.UTF_8);
        }
    }
}
