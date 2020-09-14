import okhttp3.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PikabuPost {
    public String body;
    public String author;
    public String title;
    public String url_to_pic;
    private OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private String doGetRequest(String url) throws IOException {
        RequestBody requestBody = RequestBody.create(url, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    private String htmlParseSingleParam(String regex, String text, String tagName) {
        List<String> result = new ArrayList<>();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            result.add(matcher.group());
        }
        if (result.size() < 1) return null;
        return UnTagHtml(result.get(0), tagName);
    }

    private String UnTagHtml(String tag, String tagName) {
        return tag.split("<" + tagName + ".*?>")[1].split("</" + tagName + ">")[0];
    }

    public PikabuPost(String url) throws IOException {
        body = doGetRequest(url);
        title = htmlParseSingleParam("<span\\s+class=\"story__title-link\".*?>.*?</span>", body, "span");
    }
}
