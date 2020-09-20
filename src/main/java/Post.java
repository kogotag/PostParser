import java.io.IOException;
import java.util.List;

public interface Post {
    String getAuthorName() throws IOException;
    String getAuthorAvatarUrl() throws IOException;
    String getTitle() throws IOException;
    List<PostElement> getPostParts() throws IOException;
    boolean checkUrl();
}
