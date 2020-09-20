import okhttp3.*;

import java.io.IOException;
import java.util.List;

public abstract class AbstractPost implements Post {
    protected String url;
    protected String getBody() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute())
        {
            return response.body().string();
        }
    }
    public AbstractPost(String url){
        this.url=url;
    }

    public String getAuthorName() throws IOException {
        return null;
    }

    public String getAuthorAvatarUrl() throws IOException {
        return null;
    }

    public String getTitle() throws IOException {
        return null;
    }

    public List<PostElement> getPostParts() throws IOException {
        return null;
    }
}
