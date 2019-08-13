package service;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Address;
import util.JsoupUtil;
import util.SslUtils;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Map;

public class SendMessageService {

    //    loginhash: LsqSX
//    inajax: 1
//    formhash: c2e28c87
//    referer: http://www.fmcm.cn/
//    username: hk8188
//    password: hk8188
//    questionid: 0
//    answer:
//
    private static Logger logger = LoggerFactory.getLogger(SendMessageService.class);

    public static Map<String, String> getCookie(String username, String password, String domain) throws Exception {
        SslUtils.ignoreSsl();
        Connection conn = Jsoup.connect(Address.getLoginUrl(username, password, domain));
        conn.method(Connection.Method.GET);
        conn.followRedirects(false);
        Connection.Response response;
        response = conn.execute();
        System.err.println(response.body());
        return response.cookies();
    }


    public static String getFormHash(Map<String, String> cookies, String domain) throws Exception {
        SslUtils.ignoreSsl();
        Connection conn = Jsoup.connect(Address.getFormHash(domain));
        conn.cookies(cookies);
        conn.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        conn.header("Accept-Encoding", "gzip, deflate, sdch");
        conn.header("Accept-Language", "zh-CN,zh;q=0.8");
        conn.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");

        Connection.Response response = conn.execute();

        String HTMLRes = response.body();
        String hashStr = "formhash=";
        int hashIndex = HTMLRes.lastIndexOf(hashStr);
        for (Map.Entry<String, String> c : response.cookies().entrySet()) {
            logger.info("Cookie:" + c.getKey() + "|" + c.getValue());
        }
        System.err.println("OK:->" + response.body());
        return HTMLRes.substring(hashIndex + hashStr.length(), hashIndex + hashStr.length() + 8);
    }


    public static Boolean sendMessage(Map<String, String> cookies, String touId, String msg, TextArea log, String domain, String formHash) throws Exception {
        SslUtils.ignoreSsl();
        Connection conn = Jsoup.connect(String.format("%s/home.php?mod=spacecp&ac=pm&op=send&touid=1&inajax=1", domain));
        for (Map.Entry<String, String> entry : cookies.entrySet()) {
//            if(entry.getKey().equals("8IKi_2132_lastvisit")){
//                conn.cookie("8IKi_2132_lastvisit", FXUtil.getTimestamp());
//                continue;
//            }
            conn.cookie(entry.getKey(), entry.getValue());
        }
        conn.data("pmsubmit", "true");
        conn.data("touid", touId);
        conn.data("formhash", formHash);
        conn.data("handlekey", "showMsgBox");
        conn.data("message", msg);
        conn.data("messageappend", "");

        Document document = conn.post();

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                log.appendText(document.toString() + "\n");
            }
        });
        if (document.text().contains("太快")) {
            return false;
        }
        if (document.text().contains("不存在")) {
            return true;
        }
        return document.text().contains("成功");
    }

    /**
     * @param cookies
     * @param touId    用户ID
     * @param msg      留言
     * @param domain   域名
     * @param formHash Hash值
     * @throws Exception
     */
    public static void hello(Map<String, String> cookies, String touId, String msg, String domain, String formHash) throws Exception {
        Connection conn = Jsoup.connect(Address.hello(touId, domain));
        logger.info(Address.hello(touId, domain));
        conn.headers(JsoupUtil.getDefaultHeader());
        for (Map.Entry<String, String> entry : cookies.entrySet()) {
            conn.cookie(entry.getKey(), entry.getValue());
        }
        conn.data("referer", Address.referer(touId, domain));
        conn.data("addsubmit", "true");
        conn.data("handlekey", String.format("a_friend_li_%S", touId));
        conn.data("formhash", formHash);
        conn.data("note", msg);
        conn.data("gid", "1");
        logger.info("开始发送：" + msg + "\t" + System.currentTimeMillis() + "\n");
        Document document = conn.post();
        Platform.runLater(() -> {
            logger.info(document.text());
            if (document.text().contains("已发送") || document.text().contains("验证")) {
                logger.info(document.html());
            } else {
                logger.info("发送失败！" + "\n");
            }
//                log.appendText(document.text());
        });
    }

    public static boolean follow(String address, Map<String, String> cookies) throws Exception {
        Connection connection = Jsoup.connect(address);
        connection.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        connection.header("Accept-Encoding", "gzip, deflate, sdch");
        connection.header("Accept-Language", "zh-CN,zh;q=0.8");
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
        connection.cookies(cookies);
        try {
            Document document = connection.get();
            logger.info(address);
            logger.info("收听:" + document.html());
            return document.html().contains("成功");
        } catch (ConnectException | SocketTimeoutException conn) {
            conn.printStackTrace();
            return false;
        }

    }


}
