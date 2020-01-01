package ch02;

import com.fasterxml.jackson.jr.ob.JSON;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class JsonGitExample {

    public static void main(String[] args) throws IOException {
        final String username = "alexeygrigorev";
        final String json = UrlUtils.request("https://api.github.com/users/"
                + username + "/repos");
        System.out.println(json.substring(0, 250));

        // Ugly code requiring lots of type casting
        List<Map<String, ?>> list = (List<Map<String, ?>>) JSON.std.anyFrom(json);
        final String name = (String) list.get(0).get("name");
        System.out.println(name);

        // Better code using query language
        ReadContext ctx = JsonPath.parse(json);
        final String query = "$..[?(@.language=='Java' && @.stargazers_count > 0)]full_name";
        List<String> javaProjects = ctx.read(query);
        System.out.println(javaProjects);
    }
}
