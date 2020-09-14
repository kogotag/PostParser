import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        PikabuPost post = new PikabuPost("https://pikabu.ru/story/sovremennaya_shkola_trebuet_sovremennogo_podkhoda_7708510");
        System.out.println(post.title);
    }
}
