package util;

import java.util.HashMap;
import java.util.Map;

import org.jsoup.Connection;

/**
 * JSOUP Util
 * 
 * @author Administrator
 *
 */
public class JsoupUtil {
	/**
	 * 获取默认请求头
	 */
	public static Map<String, String> getDefaultHeader() {
		Map<String, String> userAgentMap = new HashMap<String, String>();
		userAgentMap.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		userAgentMap.put("Accept-Encoding", "gzip, deflate, sdch");
		userAgentMap.put("Accept-Language", "zh-CN,zh;q=0.8");
		userAgentMap.put("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
		return userAgentMap;
	}
}
