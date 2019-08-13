//package letv.Letv;
//
//import java.io.IOException;
//
//import org.jsoup.Connection;
//import org.jsoup.Jsoup;
//import org.junit.Test;
//
//import service.LetvPageCount;
//import service.PageCountAdapter;
//import service.PostsHandler;
//import util.JsoupUtil;
//
//public class FilterTest {
//
////	@Test
//	public void getPageCountTest() throws IOException {
//		Connection connection = Jsoup.connect("https://bbs.le.com/forum-1336-1.html");
//		connection.headers(JsoupUtil.getDefaultHeader());
//		PageCountAdapter pageCountAdapter = new LetvPageCount();
//		pageCountAdapter.getPageCount(connection.get());
//	}
//
//	@Test
//	public void getLinksTest() throws Exception {
////		PostsHandler handler = new PostsHandler.Builder().AscOrDesc(true).Mudule(1336).Rule("a.threadcolor")
////				.PageCount(new LetvPageCount()).call();
////
////			handler.getLinkList().forEach(System.out::printf);
//	}
//}
