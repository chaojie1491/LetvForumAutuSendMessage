//package letv.Letv;
//
//import org.jsoup.Connection;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.junit.Test;
//
//import java.io.IOException;
//
//
//public class PageTest {
//
//    @Test
//    public void pageTest() throws IOException {
//        Connection connection = Jsoup.connect("https://bbs.le.com/thread-2438827-1.html");
//        Document document = connection.get();
//        String doc = document.select("input[name=custompage]").next().attr("title");
//        String pageCount = doc.replaceAll("共","").replaceAll("页","").trim();
//        System.out.println(pageCount);
//    }
//}
