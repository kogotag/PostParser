import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PikabuPost extends AbstractPost {
    private final String authorNameRegex = "<div\\s+class=\"page-story__story\">.*?<a\\s+class=\"user__nick\\s+story__user-link.*?>(?<name>.*?)<\\/a>";
    private final String authorAvatarUrlRegex = "<div\\s+class=\"page-story__story\">.*?<span\\s+class=\"avatar__inner\">.*?<img\\s+data-src=\"(?<img>.*?)\".*?<\\/span>";
    private final String postTitleRegex = "<span\\s+class=\"story__title-link\".*?>(?<title>.*?)<\\/span>";
    private final String postInnerPartRegex = "<div\\s+class=\"page-story__story.*?<div\\s+class=\"story__content-inner\">.*?<div\\s+class=\"story__tags\\s+tags\">";
    private final String postInnerPartSplitRegex = "<div\\s+class=\"story-block story-block_type_(?<type>.*?)\">(?<content>.*?)<\\/div>";
    private final String urlRegex = "^https:\\/\\/pikabu.ru\\/story\\/[^\\/]+\\/?$";
    private final String imagePartRegex = "<a\\s+href=\"(?<url>.*?)\"";

    public PikabuPost(String url) {
        super(url);
    }

    @Override
    public String getAuthorName() throws IOException {
        if (!checkUrl()) return null;
        Pattern authorNamePattern = Pattern.compile(authorNameRegex, Pattern.DOTALL);
        Matcher authorNameMatcher = authorNamePattern.matcher(super.getBody());
        if (authorNameMatcher.find()) {
            return authorNameMatcher.group("name");
        } else return null;
    }

    private String regexFind(String regex, String sequence) {
        StringBuilder builder = new StringBuilder();
        Matcher matcher = Pattern.compile(regex, Pattern.DOTALL).matcher(sequence);
        while (matcher.find()) {
            builder.append(matcher.group());
        }
        return builder.toString();
    }

    @Override
    public String getAuthorAvatarUrl() throws IOException {
        if (!checkUrl()) return null;
        Pattern authorAvatarUrlPattern = Pattern.compile(authorAvatarUrlRegex, Pattern.DOTALL);
        Matcher authorAvatarUrlMatcher = authorAvatarUrlPattern.matcher(super.getBody());
        if (authorAvatarUrlMatcher.find()) {
            return authorAvatarUrlMatcher.group("img");
        } else return null;
    }

    @Override
    public String getTitle() throws IOException {
        if (!checkUrl()) return null;
        Pattern postTitlePattern = Pattern.compile(postTitleRegex, Pattern.DOTALL);
        Matcher postTitleMatcher = postTitlePattern.matcher(super.getBody());
        if (postTitleMatcher.find()) {
            return postTitleMatcher.group("title");
        } else return null;
    }

    private boolean isTextPart(String htmlPart) {
        Pattern pattern = Pattern.compile("story-block_type_text");
        Matcher matcher = pattern.matcher(htmlPart);
        if (matcher.find()) {
            return true;
        } else return false;
    }

    private boolean isImagePart(String htmlPart) {
        Pattern pattern = Pattern.compile("story-block_type_image");
        Matcher matcher = pattern.matcher(htmlPart);
        if (matcher.find()) {
            return true;
        } else return false;
    }

    private String parseTextPart(String htmlPart) {
        return htmlPart
                .replaceAll("<.*?>", "")
                .trim();
    }

    private String parseImagePart(String htmlPart) {
        Matcher matcher = Pattern.compile(imagePartRegex).matcher(htmlPart);
        if (matcher.find()) {
            return matcher.group("url");
        } else return null;
    }

    @Override
    public List<PostElement> getPostParts() throws IOException {
        if (!checkUrl()) return null;
        String postInnerPart = regexFind(postInnerPartRegex, super.getBody());
        if (postInnerPart.isEmpty()) return null;
        List<String> eitherTextOrImageHtml = new ArrayList<>();
        Matcher postInnerPartSplitedMatcher = Pattern.compile(postInnerPartSplitRegex, Pattern.DOTALL).matcher(postInnerPart);
        while (postInnerPartSplitedMatcher.find()) {
            eitherTextOrImageHtml.add(postInnerPartSplitedMatcher.group());
        }
        if (eitherTextOrImageHtml.size() <= 0) return null;
        List<PostElement> postParts = new ArrayList<>();
        List<Integer> added = new ArrayList<>();
        int i = 0;
        int l = eitherTextOrImageHtml.size();
        for (String elem :
                eitherTextOrImageHtml) {
            if (i == l - 1 && !added.contains(i)) {
                if (isTextPart(elem)) postParts.add(new PostElement(parseTextPart(elem), null));
                else if (isImagePart(elem)) postParts.add(new PostElement(null, parseImagePart(elem)));
                break;
            } else if (i == l - 1) {
                break;
            }
            if (added.contains(i)) {
                i++;
                continue;
            }
            if (isImagePart(elem)) {
                postParts.add(new PostElement(null, parseImagePart(elem)));
                added.add(i);
            } else if (isTextPart(elem) && isImagePart(eitherTextOrImageHtml.get(i + 1))) {
                postParts.add(new PostElement(parseTextPart(elem), parseImagePart(eitherTextOrImageHtml.get(i + 1))));
                added.add(i);
                added.add(i + 1);
            }
            i++;
        }
        return postParts;
    }

    @Override
    public boolean checkUrl() {
        Matcher urlMatcher = Pattern.compile(urlRegex).matcher(url);
        if (urlMatcher.find()) {
            return true;
        } else return false;
    }
}
