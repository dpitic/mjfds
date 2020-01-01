package ch02;

import com.fasterxml.jackson.jr.ob.JSON;

import java.io.IOException;
import java.util.Map;

public class JsonExample {

    public static void main(String[] args) throws IOException {
        String text = "mastering java for data science";
        final String json = UrlUtils.request("http://md5.jsontest.com/?text="
                + text.replace(' ', '+'));

        Map<String, Object> map = JSON.std.mapFrom(json);
        System.out.println(map.get("original"));
        System.out.println(map.get("md5"));
    }
}
