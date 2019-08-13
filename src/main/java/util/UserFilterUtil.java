package util;

import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.regex.Pattern;

public class UserFilterUtil {

    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    public static String getCurrentPage(String i,String url){
        String before = url.substring(0, url.lastIndexOf("-"));
        String after = url.substring(url.lastIndexOf("."));
        return before + "-" + i + after;
    }


    public static String getPageCount(Document document) throws IOException, NumberFormatException {
        String doc = document.select("input[name=custompage]").next().attr("title");
       return doc.replaceAll("共", "").replaceAll("页", "").trim();
    }

}
